package factoring;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Factorizations implements Iterator<FactoredExpression>{

	private Expression expression;
	private Iterator<Expression> kernelIterator;
	private Factorizations kernelFactorizedIterator;
	private Factorizations quotientFactorizedIterator;
	private Factorizations remainingFactorizedIterator;
	private Expression currentKernelExpression;
	private Expression currentQuotientExpression;
	private Expression currentRemainingExpression;

	private int minKernelSize;
	private int minQuotientSize;
	private int minRemainigSize;

	private FactoredExpression currentKernelF;
	private FactoredExpression currentQuotientF;
	private FactoredExpression currentRemainingF;
	private boolean onlyOneFactorization = false;
	
	
	public Factorizations(Expression e){
		expression = e;
		initializeIterator();
	}
	
	/*Trujillo- Jun 26, 2015
	 * Here we initialize all internal interators. We needs first the set
	 * of kernels and a kernel in it. This kernel will generate a quotient and 
	 * a remaining. We then generate the iterator for both, and take the first element
	 * of these iterators, which provide the first solution.*/
	private void initializeIterator() {
		if (expression.isOneExpression() || expression.isZeroExpression() || expression.isACube()){
			onlyOneFactorization = true;
			currentKernelExpression = expression;
			return;
		}
		List<Expression> kernels = expression.findKernels(); 
		//System.out.println("Number of kernels = "+kernels.size());
		kernelIterator = kernels.iterator();
		if (kernels.isEmpty()) throw new RuntimeException("Empty kernel set for: "+expression.toString());
		currentKernelExpression = kernelIterator.next();
		if (expression.equals(currentKernelExpression)){//porque es la propia expression
			if (kernelIterator.hasNext()) currentKernelExpression = kernelIterator.next();
			else {
				onlyOneFactorization = true;
				return;
			}
		}
		kernelFactorizedIterator = new Factorizations(currentKernelExpression);
		currentKernelF = kernelFactorizedIterator.next();
		minKernelSize = currentKernelF.totalNumberOfLiterals();
		currentQuotientExpression = expression.divideBy(currentKernelExpression);
		currentRemainingExpression = (currentKernelExpression.multiplyBy(currentQuotientExpression)).complementIn(expression);
		quotientFactorizedIterator = new Factorizations(currentQuotientExpression);
		remainingFactorizedIterator = new Factorizations(currentRemainingExpression);
		minRemainigSize = currentRemainingExpression.totalNumberOfLiterals();
		currentQuotientF = quotientFactorizedIterator.next();
		minQuotientSize = currentQuotientF.totalNumberOfLiterals();
		//currentRemainingF = remainingFactorizedIterator.next();
		//note that we are not verifying whether there are elements in this iterators
		//because at least there must be one.
	}

	@Override
	public boolean hasNext() {
		if (onlyOneFactorization){
			if (currentKernelExpression != null) return true;
			else return false;
		}
		if (remainingFactorizedIterator.hasNext()) return true;
		if (quotientFactorizedIterator.hasNext()) return true;
		if (kernelFactorizedIterator.hasNext()) return true;
		if (kernelIterator.hasNext()) return true;
		return false;
	}

	/*Trujillo- Jun 26, 2015
	 * First, asking for next implies that there was previously one.
	 * We proceed as follows:
	 * 		- If the iterator remaining has more elements, then we ask for it a finish
	 * 		- If not, we ask whether the quotient iterator has more elements
	 * 			- If yes, we take it, and we create a new iterator for the current remaining.
	 * 				Note that, the remaining is the same, we just have a different factorization
	 * 				of the quotient. 
	 * 			- If no, we ask to the kernel factorization whether it has more elementes
	 * 				- If yes, then we take it, and create new iterators for the current quotient
	 * 					and the current remaining.
	 * 				- if no, we ask to the iterator of kernels whether it has more kernels.
	 * 					- if yes, we take the next kernel and create an iterator of factorizations for it.
	 * 						We re-compute the new quotient and the remaining, and we create the iterators for then.
	 * 					- if no, then we are done, there are no more elements.
	 * 
	 * An important remark is that, for a given triplet of kernel, quotient, and remaining, 
	 * we will only choose the best factorization. This optimal factorization is formed by the
	 * best kernel factorization, the best quotient factorization, and the best remaining factorization.
	 * THIS STILL NEEDS TO BE DONE!!!!*/
	@Override
	public FactoredExpression next() {
		if (!hasNext()) throw new NoSuchElementException();
		if (onlyOneFactorization){
			currentKernelExpression = null;
			return new FactoredExpression(expression);
		}
		if (remainingFactorizedIterator.hasNext()){
			currentRemainingF = remainingFactorizedIterator.next();
		}
		else{
			if (quotientFactorizedIterator.hasNext()){
				currentQuotientF = quotientFactorizedIterator.next();
				remainingFactorizedIterator = new Factorizations(currentRemainingExpression);
				currentRemainingF = remainingFactorizedIterator.next();
			}
			else{
				if (kernelFactorizedIterator.hasNext()){
					currentKernelF = kernelFactorizedIterator.next();
					quotientFactorizedIterator = new Factorizations(currentQuotientExpression);
					remainingFactorizedIterator = new Factorizations(currentRemainingExpression);
					currentQuotientF = quotientFactorizedIterator.next();
					currentRemainingF = remainingFactorizedIterator.next();
				}
				else{
					if (kernelIterator.hasNext()){
						currentKernelExpression = kernelIterator.next();
						kernelFactorizedIterator = new Factorizations(currentKernelExpression);
						currentKernelF = kernelFactorizedIterator.next();
						currentQuotientExpression = expression.divideBy(currentKernelExpression);
						currentRemainingExpression = (currentKernelExpression.multiplyBy(currentQuotientExpression)).complementIn(expression);
						quotientFactorizedIterator = new Factorizations(currentQuotientExpression);
						remainingFactorizedIterator = new Factorizations(currentRemainingExpression);
						currentQuotientF = quotientFactorizedIterator.next();
						currentRemainingF = remainingFactorizedIterator.next();
					}
					else{
						throw new NoSuchElementException();
					}
				}
			}
		}
		return computeResult();
	}

	private FactoredExpression computeResult() {
		/*System.out.println("Result for "+expression.toString());
		System.out.println("quotient - "+currentQuotientF.toString());
		System.out.println("remaining - "+currentRemainingF.toString());
		System.out.println("kernel - "+currentKernelF.toString());*/
		return (currentKernelF.multiplyBy(currentQuotientF)).add(currentRemainingF);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		Expression.mapping = new AlphabeticalMapping();
		System.out.println("Example 1");
		example0();
		System.out.println("Example 1");
		example1();
		System.out.println("Example 2");
		example2();
		System.out.println("Example 3");
		example3();
		System.out.println("Example 4");
		example4();
		System.out.println("Example 5");
		example5();
	}
	
	private static final int a = 1;
	private static final int b = 2;
	private static final int c = 3;
	private static final int d = 4;
	private static final int e = 5;
	private static final int f = 6;
	private static final int g = 7;
	private static final int h = 8;
	private static final int i = 9;

	private static void example0(){
		int[][] x = new int[][]{
				new int[]{b, d, f}, 
				new int[]{b, c, d, e}, 
		};
		Expression xE = new Expression(x);
		factorize(xE);
	}

	private static void example1(){
		int[][] x = new int[][]{
				new int[]{a, b, c}, 
				new int[]{a, c, g}, 
				new int[]{b, d, f}, 
				new int[]{b, c, d, e}, 
		};
		int[][] y = new int[][]{
				new int[]{a}, 
		};
		Expression xE = new Expression(x);
		Expression yE = new Expression(y);
		factorize(xE);
		factorize(yE);
	}

	private static void factorize(Expression e) {
		System.out.println("Factorizing : "+e.toString());
		Factorizations factors = new Factorizations(e);
		int index = 0;
		while (factors.hasNext()){
			System.out.println(index +" - "+factors.next().toString());
			index++;
		}
	}

	private static void example2(){
		int[][] x = new int[][]{
				new int[]{a, b, d}, 
				new int[]{b, c, d}, 
				new int[]{h, c}, 
				new int[]{i, g}, 
		};
		int[][] y = new int[][]{
				new int[]{a}, 
				new int[]{c}, 
		};
		Expression xE = new Expression(x);
		Expression yE = new Expression(y);
		factorize(xE);
		factorize(yE);
	}
	private static void example3(){
		int[][] x = new int[][]{
				new int[]{a, b, c}, 
				new int[]{a, c, e}, 
				new int[]{b, d}, 
				new int[]{d, e}, 
		};
		int[][] y = new int[][]{
				new int[]{a, c}, 
				new int[]{d}, 
		};
		Expression xE = new Expression(x);
		Expression yE = new Expression(y);
		factorize(xE);
		factorize(yE);
	}
	
	private static void example4(){
		int[][] x = new int[][]{
				new int[]{a, b, f, g}, 
				new int[]{a, c, f, g}, 
				new int[]{d, f, g}, 
				new int[]{b, h}, 
				new int[]{b, i}, 
				new int[]{c, h}, 
				new int[]{c, i}, 
		};
		int[][] y = new int[][]{
				new int[]{b}, 
				new int[]{c}, 
		};
		Expression xE = new Expression(x);
		Expression yE = new Expression(y);
		factorize(xE);
		factorize(yE);
	}
	
	private static void example5(){
		int[][] x = new int[][]{
				new int[]{a, b}, 
				new int[]{a, c}, 
				new int[]{e, b, d}, 
		};
		int[][] y = new int[][]{
				new int[]{c}, 
				new int[]{b}, 
		};
		Expression xE = new Expression(x);
		Expression yE = new Expression(y);
		factorize(xE);
		factorize(yE);
	}

}
