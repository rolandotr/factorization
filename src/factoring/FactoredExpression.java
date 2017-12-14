package factoring;

/*Trujillo- Jun 15, 2015
 * A factored expression is represented in the form of a tree with AND and OR gates. It is therefore defined recursively.*/
public class FactoredExpression {

	FactoredExpression left;
	FactoredExpression right;
	Expression simpleFactoredExpression;
	boolean multiplication;

	public FactoredExpression(FactoredExpression e1,
			FactoredExpression e2, boolean multiplication) {
		this.left = e1;
		this.right = e2;
		this.multiplication = multiplication;
	}

	public FactoredExpression(Expression e) {
		this.simpleFactoredExpression = e;
	}

	public FactoredExpression multiplyBy(FactoredExpression e) {
		if (isOneExpression()) return e;
		if (isZeroExpression()) return new FactoredExpression(Expression.ZERO_EXPRESSION);
		if (e.isOneExpression()) return this;
		if (e.isZeroExpression()) return new FactoredExpression(Expression.ZERO_EXPRESSION);
		return new FactoredExpression(this, e, true);
	}

	private boolean isOneExpression() {
		if (!isSimple()) return false;
		return simpleFactoredExpression.isOneExpression();
	}

	private boolean isZeroExpression() {
		if (!isSimple()) return false;
		return simpleFactoredExpression.isZeroExpression();
	}
	
	public FactoredExpression add(FactoredExpression e) {
		if (isZeroExpression()) return e;
		if (e.isZeroExpression()) return this;
		return new FactoredExpression(this, e, false);
	}

	public int totalNumberOfLiterals() {
		if (isSimple()){
			return simpleFactoredExpression.totalNumberOfLiterals();
		}
		else return left.totalNumberOfLiterals()+right.totalNumberOfLiterals();
	}

	private boolean isSimple() {
		return simpleFactoredExpression != null;
	}

	@Override
	public String toString() {
		if (isSimple()) return simpleFactoredExpression.toString();
		String result = "";
		if (multiplication){
			if (left.size() == 1) result += left.toString();
			else{
				result += "(";
				result += left.toString();
				result += ")";
			}
			//result += "*";
			if (right.size() == 1) result += right.toString();
			else{
				result += "(";
				result += right.toString();
				result += ")";
			}
		}
		else{
			result += left.toString();
			result += "+";
			result += right.toString();
		}
		return result;
	}

	public int size() {
		if (isSimple()) return simpleFactoredExpression.size();
		else return left.size()+right.size();
	}

	/*Trujillo- Jun 17, 2015
	 * Transform a factorized expression into an expression*/
	public Expression normalize() {
		if (isSimple()) return simpleFactoredExpression;
		Expression l = left.normalize(); 
		Expression r = right.normalize();
		Expression result;
		if (multiplication) return l.multiplyBy(r);
		else return l.add(r);
	}

	public boolean isASum() {
		return !multiplication;
	}

}
