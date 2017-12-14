package factoring;


public abstract class Mapping {

	
	public final String getValue(int key){
		if (key == Literal.ONE) return "1";
		else return getSpecificValue(key);
	}

	protected abstract String getSpecificValue(int key);
	
}
