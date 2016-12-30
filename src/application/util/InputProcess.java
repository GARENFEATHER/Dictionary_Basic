package application.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputProcess {
	public static String processInvalid(String words) {
		if(words == null) return null;
		ArrayList<String> reGroup=new ArrayList<String>();
		words=words.trim().toLowerCase();
		StringBuilder result=new StringBuilder(words);
		deleteInvalid(result);
		String[] group=result.toString().split(" ");
		for(String item:group) {
			if(item.length() != 0)
				reGroup.add(item);
		}
		result=new StringBuilder();
		int all=reGroup.size();
		for(int i=0;i<all;i++) {
			String item=reGroup.get(i);
			if(item.length() != 0)
				result.append(item);
			if(i != all-1) result.append(" ");
		}
		if(result.length() == 0) return null;
		else return result.toString();
	}
	private static void deleteInvalid(StringBuilder word) {
		Pattern check=Pattern.compile("[^a-zA-Z\\-' ]+");
		Matcher m=check.matcher(word);
		while(m.find()) {
			int start=m.start();
			int end=m.end();
			word.delete(start, end);
			m.reset();
		}
	}
}
