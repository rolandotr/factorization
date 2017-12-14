package factoring;

import java.util.Map;
import java.util.TreeMap;

public class AlphabeticalMapping extends Mapping{
	
	private static final Map<Integer, String> alphabet = new TreeMap<>();
    static {
    	alphabet.put(1, "a");
    	alphabet.put(2, "b");
    	alphabet.put(3, "c");
    	alphabet.put(4, "d");
    	alphabet.put(5, "e");
    	alphabet.put(6, "f");
    	alphabet.put(7, "g");
        alphabet.put(8, "h");
        alphabet.put(9, "i");
        alphabet.put(10, "j");
        alphabet.put(11, "k");
        alphabet.put(12, "l");
        alphabet.put(13, "m");
        alphabet.put(14, "n");
    }
    
	public String getSpecificValue(int key) {
		return alphabet.get(key);
	}


}
