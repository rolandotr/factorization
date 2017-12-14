package factoring;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import javax.jws.Oneway;

public class AlgebraicDivision {

	
	private static final int a = 1;
	private static final int b = 2;
	private static final int c = 3;
	private static final int d = 4;
	private static final int e = 5;
	private static final int f = 6;
	private static final int g = 7;
	private static final int h = 8;
	private static final int i = 9;
	
	public static void main(String[] args) {
		Expression.mapping = new AlphabeticalMapping();
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
		makeDivision(xE, yE);
		getKernels(xE);
		getKernels(yE);
		factorize(xE);
		factorize(yE);
	}

	private static void factorize(Expression e) {
		System.out.print("Factorizing : "+e.toString());
		FactoredExpression result = e.factorize();
		System.out.println(" = "+result.toString());
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
		makeDivision(xE, yE);
		getKernels(xE);
		getKernels(yE);
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
		makeDivision(xE, yE);
		getKernels(xE);
		getKernels(yE);
		factorize(xE);
		factorize(yE);
	}
	
	private static void getKernels(Expression e) {
		System.out.println("Computing kernels of "+e.toString());
		List<Expression> kernels = e.findKernels();
		for (Expression expression : kernels) {
			System.out.println(expression.toString());
		}
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
		makeDivision(xE, yE);
		getKernels(xE);
		getKernels(yE);
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
		makeDivision(xE, yE);
		getKernels(xE);
		getKernels(yE);
		factorize(xE);
		factorize(yE);
	}

	private static void makeDivision(Expression x, Expression y){
		System.out.println("Computing division between "+x.toString()+" and "+y.toString());
		Expression quotient = x.divideBy(y);
		//Expression quotient = division(new Expression(w), new Expression(z));
		System.out.println(quotient.toString());
	}
}
