package application.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="words")
public class LoadWordData {
	private HashMap<String,Word> additionalWordList;
	private ArrayList<String> history;
	private int historyNum;
	
	@XmlElement(name="history")
	public ArrayList<String> getHistory() {
		return history;
	}
	
	@XmlElement(name="historyNum")
	public int getHistoryNum() {
		return historyNum;
	}

	public void setHistoryNum(int historyNum) {
		this.historyNum = historyNum;
	}

	public void setHistory(ArrayList<String> history) {
		this.history = history;
	}

	@XmlElement(name="additionalList")
	public HashMap<String, Word> getAdditionalWordList() {
		return additionalWordList;
	}

	public void setAdditionalWordList(HashMap<String, Word> additionalWordList) {
		this.additionalWordList = additionalWordList;
	}
	
}
