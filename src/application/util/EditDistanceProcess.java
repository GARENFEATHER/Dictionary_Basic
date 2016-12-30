package application.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EditDistanceProcess {
	public static HashMap<String,Integer> wordCorpus;
	public static ArrayList<String> editDistanceOne(String word) {
		String letters="abcdefghijklmnopqrstuvwxyz";
		ArrayList<String> wordCollect=new ArrayList<String>();
		int len=word.length();
		for(int i=0;i<len;i++) {
			StringBuilder temp=new StringBuilder(word);
			//É¾³ý
			temp.deleteCharAt(i);
			if(temp.length() == 0) continue;
			if(wordCorpus.get(temp.toString()) != null)
				wordCollect.add(temp.toString());
			for(int j=0;j<26;j++) {
				temp=new StringBuilder(word);
				//²åÈë
				temp.insert(i, letters.charAt(j));
				if(wordCorpus.get(temp.toString()) != null)
					wordCollect.add(temp.toString());
				temp=new StringBuilder(word);
				if(letters.charAt(j) != temp.charAt(i)) {
					//Ìæ»»
					temp.setCharAt(i, letters.charAt(j));
					if(wordCorpus.get(temp.toString()) != null)
						wordCollect.add(temp.toString());
				}
			}
			if(i != len-1) {
				temp=new StringBuilder(word);
				//½»»»Î»ÖÃ
				char c=temp.charAt(i);
				temp.setCharAt(i, temp.charAt(i+1));
				temp.setCharAt(i+1, c);
				if(wordCorpus.get(temp.toString()) != null)
					wordCollect.add(temp.toString());
			}
		}
		for(int j=0;j<26;j++) {
			StringBuilder temp=new StringBuilder(word);
			temp.insert(len, letters.charAt(j));
			if(wordCorpus.get(temp.toString()) != null)
				wordCollect.add(temp.toString());
		}
		return wordCollect;
	}
	public static ArrayList<String> editDistanceTwo(String word) {
		ArrayList<String> wordEditOne=editDistanceOne(word);
		ArrayList<String> wordEditTwo=new ArrayList<String>();
		for(String wordOne:wordEditOne) {
			ArrayList<String> temp=editDistanceOne(wordOne);
			HashSet<String> diff=new HashSet<>(temp);
			if(diff.contains(word)) diff.remove(word);
			wordEditTwo.addAll(diff);
		}
		return wordEditTwo;
	}
	public static ArrayList<String> ComplexDistance(String word,int level) {
		if(level != 2 && level != 1) return null;
		else {
			ArrayList<String> result=new ArrayList<String>();
			String[] diverse=word.split(" ");
			int len=diverse.length;
			for(int i=0;i<len;i++) {
				StringBuilder left=new StringBuilder("");
				StringBuilder right=new StringBuilder("");
				for(int j=0;j<len;j++) {
					if(j < i) left.append(" "+diverse[j]);
					else if(j > i) right.append(" "+diverse[j]);
				}
				if(i != 0) left.deleteCharAt(0);
				ArrayList<String> editSet;
				if(level == 1) editSet=editDistanceOne(diverse[i]);
				else editSet=editDistanceTwo(diverse[i]);
				for(String key:editSet) {
					StringBuilder theWord=new StringBuilder();
					theWord.append(left);
					if(left.length() != 0) theWord.append(" ");
					theWord.append(key);
					theWord.append(right);
					String correctWord=theWord.toString();
					if(wordCorpus.get(correctWord) != null)
						result.add(correctWord);
				}
			}
			return result;
		}
	}
}
