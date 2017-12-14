package factoring;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*Trujillo- Jun 13, 2015
 * An expression is a sum of products. It could be seemed as a logical formula in disjunctive normal form. Therefore, 
 * it does not contain repeated cubes*/
public class Expression implements Comparable<Expression>{

	private int[][] expression;
	TreeMap<Integer, Integer> literals;
	public static Mapping mapping;
	
	public static final Expression ZERO_EXPRESSION = new Expression();
	public static final Expression ONE_EXPRESSION = new Expression(new int[][]{new int[]{Literal.ONE}});

	/*Trujillo- Jun 13, 2015
	 * For efficiency, we keep literals ordered within cubes*/
	public Expression(int[][] expression){
		this.expression = sortExpression(expression);
		validateExpression(this.expression);
		createLiterals();
	}

	/*Trujillo- Jun 17, 2015
	 * Here we simply verify that cubes are not repeated. Note that, 
	 * this method expect all cubes to be ordered*/
	private void validateExpression(int[][] e) {
		SortedSet<String> in = new TreeSet<>();
		for (int i = 0; i < e.length; i++) {
			int[] cube = e[i];
			String key = "";
			for (int j = 0; j < cube.length; j++) {
				key += cube[j]+"*";
			}
			if (in.contains(key)) throw new RuntimeException("The cube "+key+" is already in the expression e = "+printExpression(e));
			in.add(key);
		}
	}

	private String printExpression(int[][] e) {
		if (mapping == null) mapping = new DefaultMapping();
		String result = "";
		for (int i = 0; i < e.length; i++) {
			for (int j = 0; j < e[i].length; j++) {
				result += mapping.getValue(e[i][j]);
				if (j < e[i].length-1) result += "";
			}
			if (i < e.length-1) result += "+";
		}
		return result;
	}

	public Expression(List<int[]> cubes) {
		this(transform(cubes));
	}

	private Expression() {
		this(new int[][]{});
	}

	public Expression(Integer literal) {
		this(new int[][]{new int[]{literal}});
	}

	public Expression(int[] cubeInG) {
		this(new int[][]{cubeInG});
	}

	public static int[][] transform(List<int[]> cubes) {
		int[][] result = new int[cubes.size()][];
		int index = 0;
		for (int[] cube : cubes){
			result[index++] = cube;
		}
		return result;
	}

	private static int[][] sortExpression(int[][] expression) {
		int[][] result = new int[expression.length][];
		SortedMap<Integer, Integer> cube;
		for (int i = 0; i < expression.length; i++) {
			cube = new TreeMap<>();
			for (int j = 0; j < expression[i].length; j++) {
				if (cube.containsKey(expression[i][j])) 
					cube.put(expression[i][j], cube.get(expression[i][j])+1);
				else cube.put(expression[i][j], 1);
			}
			result[i] = new int[expression[i].length];
			int pos = 0;
			for (int literal : cube.keySet()){
				int repetitions = cube.get(literal);
				for (int j = 0; j < repetitions; j++) {
					result[i][pos++] = literal;
				}
			}
		}
		return result;
	}

	public void createLiterals() {
		literals = new TreeMap<>();
		for (int i = 0; i < expression.length; i++) {
			for (int j = 0; j < expression[i].length; j++) {
				literals.put(expression[i][j], expression[i][j]);
			}
		}
	}

	public int size() {
		return expression.length;
	}
	
	public boolean isACube() {
		return size() == 1;
	}


	public int[] getCube(int i) {
		return expression[i];
	}

	public int numberOfLiterals() {
		return literals.size();
	}

	public SortedMap<Integer, Integer> getLiterals() {
		return literals;
	}

	public int[][] getCubes() {
		return expression;
	}

	public static Expression intersection(List<Expression> expressions) {
		Expression[] result = new Expression[expressions.size()];
		int index = 0;
		for (Expression e : expressions) result[index++] = e;
		return intersection(result);
	}
	public static Expression intersection(Expression[] expressions) {
		if (expressions.length == 1) return expressions[0]; 
		Expression e1 = expressions[0];
		Expression e2;
		for (int i = 1; i < expressions.length; i++) {
			e2 = expressions[i];
			e1 = intersection(e1, e2);
		}
		return e1;
	}

	private static Expression intersection(Expression e1, Expression e2) {
		List<int[]> cubes = new LinkedList<>();
		for (int i = 0; i < e1.size(); i++) {
			int[] cubeE1 = e1.getCube(i); 
			for (int j = 0; j < e2.size(); j++) {
				int[] cubeE2 = e2.getCube(j);
				if (cubesAreEqual(cubeE1, cubeE2)){
					cubes.add(cubeE1);
					break;
				}
			}
		}
		return new Expression(cubes);
	}


	public Expression complementIn(Expression upperSet) {
		List<int[]> cubes = new LinkedList<>();
		boolean[] found = new boolean[this.size()];
		for (int i = 0; i < upperSet.size(); i++) {
			int[] cubeInUpperSet = upperSet.getCube(i);
			boolean toBeincluded = true;
			for (int j = 0; j < this.size(); j++) {
				int[] cubeInThisSet = this.getCube(j);
				if (cubesAreEqual(cubeInUpperSet, cubeInThisSet)){
					toBeincluded = false;
					found[j] = true;
					break;
				}
			}
			if (toBeincluded) cubes.add(cubeInUpperSet);
		}
		for (int i = 0; i < found.length; i++) {
			if (!found[i]) throw new RuntimeException();
		}
		if (cubes.isEmpty()) return Expression.ZERO_EXPRESSION;
		return new Expression(cubes);
	}

	public static boolean cubesAreEqual(int[] cube1, int[] cube2) {
		if (cube1.length != cube2.length) return false;
		for (int i = 0; i < cube2.length; i++) {
			if (cube1[i] != cube2[i]) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		if (isZeroExpression()) return "0";
		if (isOneExpression()) return "1";
		if (mapping == null) mapping = new DefaultMapping();
		String result = "";
		for (int i = 0; i < expression.length; i++) {
			for (int j = 0; j < expression[i].length; j++) {
				result += mapping.getValue(expression[i][j]);
				if (j < expression[i].length-1) result += "";
			}
			if (i < expression.length-1) result += "+";
		}
		return result;
	}

	public boolean isDivisorOf(Expression dividend) {
		if (this.equals(ONE_EXPRESSION)) return true;
		if (this.equals(ZERO_EXPRESSION)) return false;
		Expression quotient = dividend.divideBy(this);
		if (quotient.equals(ZERO_EXPRESSION)) return false;
		if (quotient.equals(ONE_EXPRESSION)) return true;
		Expression integerPart = this.multiplyBy(quotient);
		Expression result = integerPart.complementIn(dividend);
		return result.isEmpty();
	}


	public Expression multiplyBy(Expression factor) {
		if (factor.isZeroExpression() || this.isZeroExpression()) return ZERO_EXPRESSION;
		if (factor.isOneExpression()) return this;
		if (this.isOneExpression()) return factor;
		int[][] result = new int[this.size()*factor.size()][];
		for (int i = 0; i < this.size(); i++) {
			for (int j = 0; j < factor.size(); j++) {
				result[i*factor.size()+j] = multiplyCubes(this.getCube(i), factor.getCube(j));
			}			
		}
		result = Expression.convertIntoASet(result);
		return new Expression(result);
	}

	private static int[][] convertIntoASet(int[][] e) {
		e = sortExpression(e);
		SortedMap<String, int[]> in = new TreeMap<>();
		for (int i = 0; i < e.length; i++) {
			String key = "";
			for (int j = 0; j < e[i].length; j++){
				key += e[i][j]+"*";
			}
			if (in.containsKey(key)) continue;
			in.put(key, e[i]);
		}
		int[][] result = new int[in.size()][];
		int index = 0;
		for (int[] value : in.values()) result[index++] = value;
		return result;
	}

	public Expression add(Expression e) {		
		if (this.isZeroExpression()) return e;
		if (e.isZeroExpression()) return this;
		List<int[]> result = new LinkedList<>();
		for (int i = 0; i < this.size(); i++) {
			result.add(this.getCube(i));
		}
		for (int i = 0; i < e.size(); i++) {
			result.add(e.getCube(i));
		}
		int[][] r = transform(result);
		r = convertIntoASet(r);
		return new Expression(r);
	}

	public boolean isOneExpression() {
		if (this.equals(ONE_EXPRESSION)) return true;
		if (expression.length != 1) return false;
		if (expression[0].length != 1) return false;
		if (expression[0][0] != Literal.ONE) return false;
		return true;
	}

	public boolean isZeroExpression() {
		return this.equals(ZERO_EXPRESSION);
	}

	private int[] multiplyCubes(int[] cube1, int[] cube2) {
		if (isOneCube(cube1)) {
			int[] result = new int[cube2.length];
			System.arraycopy(cube2, 0, result, 0, cube2.length);
			return result;
		}
		if (isOneCube(cube2)) {
			int[] result = new int[cube1.length];
			System.arraycopy(cube1, 0, result, 0, cube1.length);
			return result;
		}
		int[] result = new int[cube1.length+cube2.length];
		System.arraycopy(cube1, 0, result, 0, cube1.length);
		System.arraycopy(cube2, 0, result, cube1.length, cube2.length);
		return result;
	}

	private boolean isOneCube(int[] cube) {
		return cube.length == 1 && cube[0] == Literal.ONE;
	}

	private boolean isEmpty() {
		return expression.length == 0;
	}

	/*Trujillo- Jun 14, 2015
	 * Return the quotient of this division*/
	public Expression divideBy(Expression g){
		if (g.equals(ZERO_EXPRESSION)) throw new RuntimeException("Division by ZERO");
		if (g.equals(ONE_EXPRESSION)) return this;
		if (this.size() == 1){
			if (g.size() > 1) return Expression.ZERO_EXPRESSION;
			int[] quotient = cubeDivision(this.getCube(0), g.getCube(0));
			if (quotient == null) return Expression.ZERO_EXPRESSION;
			else return new Expression(new int[][]{quotient});
		}
		//We restrict the cubes in f to those that are in g
		//Expression u = restrictionToLiteralsIn(this, g, true); 
		//We restrict the cubes in f to those that are NOT in g
		//Expression v = restrictionToLiteralsIn(this, g, false);
		/*For every literal in g, we will create a sub expression of v
		 * of those cubes generated by the considered literal*/
		List<Expression> subExpressions = getSubExpressions(this, g);
		Expression h = Expression.intersection(subExpressions);
		return h;
	}

	private static int[] cubeDivision(int[] dividend, int[] divisor) {
		List<Integer> result = new LinkedList<>();
		boolean[] alreadyDivided = new boolean[divisor.length];
		for (int i = 0; i < dividend.length; i++) {
			boolean found = false;
			for (int j = 0; j < divisor.length; j++) {
				if (dividend[i] == divisor[j] && !alreadyDivided[j]){
					alreadyDivided[j] = true;
					found = true;
					break;
				}
			}
			if (!found) result.add(dividend[i]);
		}
		for (int i = 0; i < alreadyDivided.length; i++) {
			if (!alreadyDivided[i]) return null; 
		}
		if (result.isEmpty()) return Expression.ONE_EXPRESSION.getCube(0);
		int[] quotient = new int[result.size()];
		int index = 0;
		for (int x : result){
			quotient[index++] = x;
		}
		return quotient;
	}

	public static Expression[] getSubExpressions(Expression u, Expression v, Expression g){
		Expression[] subExpressions = new Expression[g.size()];
		int pos = 0;
		for (int[] cubeInG : g.getCubes()) {
			List<int[]> subExpressionTmp = new LinkedList<>();
			for (int i = 0; i < u.size(); i++){
				int[] cubeInU = u.getCube(i);
				boolean equal = true;
				if (cubeInU.length == cubeInG.length) {
					for (int j = 0; j < cubeInU.length; j++) {
						if (cubeInG[j] != cubeInU[j]){
							equal = false;
							break;
						}
					}
					if (equal) {//then we add the corresonding v's cube
						subExpressionTmp.add(v.getCube(i));
					}
				}
			}
			int[][] subExpression = new int[subExpressionTmp.size()][];
			int index = 0;
			for (int[] cube : subExpressionTmp){
				subExpression[index++] = cube;
			}
			subExpressions[pos++] = new Expression(subExpression);
		}
		return subExpressions;
	}

	/*Trujillo- Jun 18, 2015
	 * For each cube g_i in g, we going to look for all cubes in 
	 * f that can be devided by g_i. Then, we form a
	 * sub expression with the quotient of these even divisions*/
	public static List<Expression> getSubExpressions(Expression f, Expression g){
		List<Expression> subExpressions = new LinkedList<>();
		int pos = 0;
		for (int[] cubeInG : g.getCubes()) {
			Expression tmp = new Expression();
			Expression divisor = new Expression(cubeInG);
			for (int[] cubeInf : f.getCubes()){
				Expression dividen = new Expression(cubeInf);
				if (divisor.isDivisorOf(dividen)){
					Expression quotient = dividen.divideBy(divisor);
					tmp = tmp.add(quotient);
				}
			}
			subExpressions.add(tmp);
		}
		return subExpressions;
	}
	public static Expression restrictionToLiteralsIn(Expression dividend, Expression divisor, boolean in){
		List<Expression> tmp = new LinkedList<>();
		for (int i = 0; i < dividend.size(); i++) {
			Expression cubeDividend = new Expression(new int[][]{dividend.getCube(i)});
			for (int j = 0; j < divisor.size(); j++) {
				Expression cubeDivisor = new Expression(new int[][]{divisor.getCube(j)});
				if (cubeDivisor.isDivisorOf(cubeDividend)){
					if (in) tmp.add(cubeDivisor);
					else tmp.add(cubeDividend.divideBy(cubeDivisor));
				}
			}
		}
		if (tmp.isEmpty()) return new Expression(new int[][]{});
		int[][] tmpToArray = new int[tmp.size()][];
		int pos = 0;
		for (Expression e : tmp) {
			tmpToArray[pos++] = e.getCube(0);
		}
		return new Expression(tmpToArray);
	}
	

	public List<Expression> findKernels(){
		if (isOneExpression() || isZeroExpression()) return new LinkedList<>();
		if (size() == 1) return new LinkedList<>();
		if (containConstants()) return new LinkedList<>();
		Expression largestCubeFactor = getLargestCommonCube();
		Expression quotient = this.divideBy(largestCubeFactor);
		SortedMap<Integer, Integer> in = new TreeMap<>();
		SortedMap<Integer, Integer> out = getLiterals();
		//return findKernels(in, out, quotient);
		return findKernelsOptimal(in, out, quotient);
	}

	public boolean containConstants() {
		for (int i = 0; i < size(); i++){
			int[] cube = getCube(i);
			for (int j = 0; j < cube.length; j++){
				if (cube[j] == Literal.ONE) return true;
			}
		}
		return false;
	}

	private List<Expression> findKernels(SortedMap<Integer, Integer> in,
			SortedMap<Integer, Integer> out, Expression g) {
		if (g.containConstants()) return new LinkedList<>();
		SortedSet<Expression> result = new TreeSet<>();
		for (Integer literal : out.keySet()){
			Expression quotien = g.divideBy(new Expression(literal));
			if (quotien.size() > 1){//if the literal appears in more than one cube in g
				Expression largestCube = quotien.getLargestCommonCube();
				boolean freeOfPreviousLiterals = true;
				for (Integer literalIn : in.keySet()){
					if (largestCube.containsLiteral(literalIn)){
						freeOfPreviousLiterals = false;
						break;
					}
				}
				if (freeOfPreviousLiterals){
					SortedMap<Integer, Integer> inNew = cloneMap(in); 
					SortedMap<Integer, Integer> outNew = cloneMap(out);
					inNew.put(literal, literal);
					outNew.remove(literal);
					Expression divisor = largestCube.multiplyBy(new Expression(literal));
					Expression newExp = g.divideBy(divisor);
					List<Expression> tmp = findKernels(inNew, outNew, newExp);
					for (Expression e : tmp) result.add(e);
				}
			}
		}
		result.add(g);
		List<Expression> r = new LinkedList<>();
		for (Expression e : result){
			r.add(e);
		}
		return r;
	}

	/*Trujillo- Jun 20, 2015
	 * This one only looks for Kernels of level zero*/
	private List<Expression> findKernelsOptimal(SortedMap<Integer, Integer> in,
			SortedMap<Integer, Integer> out, Expression g) {
		if (g.containConstants()) return new LinkedList<>();
		SortedSet<Expression> result = new TreeSet<>();
		for (Integer literal : out.keySet()){
			Expression quotien = g.divideBy(new Expression(literal));
			if (quotien.size() > 1){//if the literal appears in more than one cube in g
				Expression largestCube = quotien.getLargestCommonCube();
				boolean freeOfPreviousLiterals = true;
				for (Integer literalIn : in.keySet()){
					if (largestCube.containsLiteral(literalIn)){
						freeOfPreviousLiterals = false;
						break;
					}
				}
				if (freeOfPreviousLiterals){
					SortedMap<Integer, Integer> inNew = cloneMap(in); 
					SortedMap<Integer, Integer> outNew = cloneMap(out);
					inNew.put(literal, literal);
					outNew.remove(literal);
					Expression divisor = largestCube.multiplyBy(new Expression(literal));
					Expression newExp = g.divideBy(divisor);
					List<Expression> tmp = findKernelsOptimal(inNew, outNew, newExp);
					for (Expression e : tmp) result.add(e);
				}
			}
		}
		List<Expression> r = new LinkedList<>();
		if (result.isEmpty()) {
			r.add(g);
		}
		else{
			for (Expression e : result){
				r.add(e);
			}
		}
		return r;
	}

	private SortedMap<Integer, Integer> cloneMap(SortedMap<Integer, Integer> in) {
		SortedMap<Integer, Integer> result = new TreeMap<>();
		for (Integer key : in.keySet()) {
			result.put(key, in.get(key));
		}
		return result;
	}

	private boolean containsLiteral(Integer literalIn) {
		return literals.containsKey(literalIn);
	}

	public Expression getLargestCommonCube() {
		Map<Integer, Integer> common = new TreeMap<>();
		int[] cube = getCube(0);
		LinkedList<Integer> tobeRemoved;
		for (int j = 0; j < cube.length; j++) {
			common.put(cube[j], cube[j]);
		}
		for (int i = 1; i < size(); i++) {
			cube = getCube(i);
			tobeRemoved = new LinkedList<>();
			for (Integer literal : common.keySet()){
				boolean founded = false;
				for (int j = 0; j < cube.length; j++) {
					if (literal == cube[j]){
						founded = true;
						break;
					}
				}
				if (!founded) tobeRemoved.add(literal); 
			}
			for (Integer literal : tobeRemoved) common.remove(literal);
		}
		int[] resultedCube = new int[common.size()];
		int index = 0;
		for (Integer  literal : common.keySet()){
			resultedCube[index++] = literal;
		}
		if (resultedCube.length == 0) return Expression.ONE_EXPRESSION;
		else return new Expression(new int[][]{resultedCube});
	}

	/*Trujillo- Jun 24, 2015
	 * An expression is factorized recursively. The variable depth indicates
	 * how deep in the recursive process. As long as the threshold is not reached, 
	 * we will print intermidiate results.*/
	public FactoredExpression factorize(){
		int optimal = Integer.MAX_VALUE;
		FactoredExpression best = null;
		FactoredExpression tmp = null;
		if (isOneExpression() || isZeroExpression() || size() == 1) return new FactoredExpression(this);
		//for (Expression kernel : findKernels()){
		for (Expression kernel : findKernels()){
			if (this.equals(kernel))//porque es la propia expression
				continue;
			//System.out.println("Analyzing Kernel "+kernel.toString());
			Expression quotient = this.divideBy(kernel);
			Expression remaining = (kernel.multiplyBy(quotient)).complementIn(this);
			FactoredExpression quotientF = quotient.factorize();
			FactoredExpression kernelF = kernel.factorize();
			FactoredExpression remainingF = remaining.factorize();
			tmp = (quotientF.multiplyBy(kernelF));
			if (!remaining.isZeroExpression()) tmp = tmp.add(remainingF);
			if (tmp.totalNumberOfLiterals() < optimal){
				best = tmp;
				optimal = tmp.totalNumberOfLiterals();
			}
		}
		if(best == null) return new FactoredExpression(this);
		return best;
	}

	public int totalNumberOfLiterals() {
		int total = 0;
		for (int i = 0; i < size(); i++) {
			total += getCube(i).length;
		}
		return total;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Expression){
			Expression e = (Expression)o;
			int[] c1, c2;
			if (this.size() != e.size()) return false;
			for (int i = 0; i < this.size(); i++) {
				c1 = this.getCube(i);
				c2 = e.getCube(i);
				if (c1.length != c2.length) return false;
				for (int j = 0; j < c2.length; j++) {
					if (c1[j] != c2[j]) return false;
				}
			}
			return true;
		}else return false;
	}

	@Override
	public int compareTo(Expression o) {
		if (this.equals(o)) return 0;
		if (this.totalNumberOfLiterals() < o.totalNumberOfLiterals()) return -1;
		else return 1;
	}
	
	

	public static void main(String[] args) {
		Expression.mapping = new AlphabeticalMapping();
		int[][] x = new int[][]{
				new int[]{1, 3}, 
				new int[]{1, 2}, 
		};
		int[][] y = new int[][]{
				new int[]{2, 4, 6}, 
				new int[]{2, 3, 4, 5}, 
		};
		Expression xE = new Expression(x);
		FactoredExpression f = xE.factorize();
		System.out.println(f.toString());
		Expression yE = new Expression(y);
		FactoredExpression fy = yE.factorize();
		System.out.println(fy.toString());
	}
	
}
