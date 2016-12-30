package application.model;

import java.util.HashMap;
import java.util.Iterator;


public class Word {
	private String spell,message;
	private boolean isDefault=true;
	public HashMap<String,String> sentence=new HashMap<String,String>();
	public Word(String spell,String message) {
		this.spell=spell;
		this.message=message;
		isDefault=true;
	}
	public Word(String spell) {
		this(spell,null);
	}
	public Word() {
		this(null,null);
	}
	public void notDefault() {
		isDefault=false;
	}
	public boolean defaultCheck() {
		return isDefault;
	}
	public String getSpell() {
		return spell;
	}
	public void setSpell(String spell) {
		this.spell=spell;
	}
	public String getMessage() {
		return message;
	}
	public void addMessage(String message) {
		if(this.message == null || this.message == "") this.message=message;
		else this.message=this.message+" | "+message;
	}
	public String showSentence() {
		StringBuilder re=new StringBuilder();
		Iterator<String> iter=sentence.keySet().iterator();
		while(iter.hasNext()) {
			String eng=(String) iter.next();
			String ch=sentence.get(eng);
			re.append(eng+" -> "+ch+"\n");
		}
		return re.toString();
	}
}
