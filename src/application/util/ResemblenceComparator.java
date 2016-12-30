package application.util;

import java.util.Comparator;
import java.util.HashMap;


public class ResemblenceComparator implements Comparator<String>{
	HashMap<String,Integer> compareBase;
	public ResemblenceComparator(HashMap<String,Integer> compareBase) {
		this.compareBase=compareBase;
	}
	
	@Override
	public int compare(String o1, String o2) {
		int w1=compareBase.get(o1);
		int w2=compareBase.get(o2);
		return w1-w2;
	}
}
