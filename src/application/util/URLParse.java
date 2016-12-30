package application.util;

import application.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParse {
	public static Word parse(String spell) throws IOException, RedirectNetException{
		String page=null;
		Word word;
		String url="http://dict.youdao.com/w/"+spell+"/";
		page=sendGet(url);
		word=getWordMessage(page);
		if(word != null) word.setSpell(spell);
		return word;
	}
	private static Word getWordMessage(String pageHtml) {
		Word word=new Word();
		Pattern sentenceGet=Pattern.compile("<div id=\"bilingual\".+?>(.+?)</div>");
		Pattern netTranslateGet=Pattern.compile("<div class=\"wt-container\".+?>(.+?)</div>");
		Pattern translateGet=Pattern.compile("<h2 class=\"wordbook-js\">.+</h2><div class=\"trans-container\">.+?</div>");
		Pattern liParse=Pattern.compile("<li>(.+?)</li>");
		Pattern pparse=Pattern.compile("<p>(.+?)</p>");
		Pattern spanParse=Pattern.compile("<span.*>(.+?)</span>");
		
		Matcher mNet=netTranslateGet.matcher(pageHtml);
		Matcher mNormal=translateGet.matcher(pageHtml);
		Matcher mSentence=sentenceGet.matcher(pageHtml);
		
		if(!mNet.find()) return null;
		if(mNormal.find()) {
			String s=mNormal.group();
			mNormal=liParse.matcher(s);
			while(mNormal.find())
				word.addMessage(mNormal.group(1));
		} else {
			String s=mNet.group(1);
			mNet=spanParse.matcher(s);
			while(mNet.find()) 
				word.addMessage(mNet.group(1));
		}
		if(mSentence.find()) {
			String s=mSentence.group(1);
			mSentence=liParse.matcher(s);
			while(mSentence.find()) {
				String sp=mSentence.group(1);
				Matcher temp=pparse.matcher(sp);
				String eng=null,ch=null;
				if(temp.find()) {
					eng=temp.group(1);
					eng=eng.replaceAll("<span[^/]*>|</span>|<b>|</b>|<a.*>|</a>", "");
				}
				if(temp.find()) {
					ch=temp.group(1);
					ch=ch.replaceAll("<span[^/]*>|</span>|<b>|</b>|<a.*>|</a>", "");
				}
				if(eng != null && ch != null) word.sentence.put(eng, ch);
			}
		}
		return word;
	}
	private static String sendGet(String url) throws IOException, RedirectNetException{
		try {
			PrintWriter pw=new PrintWriter(new File("temp.html"));
			URL readData=new URL(url);
			HttpURLConnection con=(HttpURLConnection) readData.openConnection();
			con.setRequestProperty("Host", "dict.youdao.com");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			con.setRequestProperty("Upgrade-Insecure-Requests","1");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0");
			con.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
			con.setRequestProperty("Accept-Charset", "utf-8");
			con.setRequestProperty("contentType", "utf-8");
			con.setRequestProperty("Cookie", "_ntes_nnid=509ba038c9a213b48828a5c649ea086b,1476496922365; OUTFOX_SEARCH_USER_ID_NCOO=1033810078.5873746; P_INFO=microcomputer_nju@126.com|1477298454|0|other|00&99|jis&1477297821&flashmail#jis&320100#10#0#0|&0|mail&mailsettings&mail126&flashmail|microcomputer_nju@126.com; YOUDAO_EAD_UUID=ac0f4cd2-3fc2-4927-97da-4dc4703b2b99; _ga=GA1.2.1914635919.1477664538; JSESSIONID=abcBK7JoxBjPMhwKpJoGv; webDict_HdAD=%7B%22req%22%3A%22http%3A//dict.youdao.com%22%2C%22width%22%3A960%2C%22height%22%3A240%2C%22showtime%22%3A5000%2C%22fadetime%22%3A500%2C%22notShowInterval%22%3A3%2C%22notShowInDays%22%3Afalse%2C%22lastShowDate%22%3A%22Mon%20Nov%2008%202010%22%7D; search-popup-show=-1; OUTFOX_SEARCH_USER_ID=-805578273@221.6.40.175; tabRecord.webTrans=%23tPETrans; DICT_UGC=be3af0da19b5c5e6aa4e17bd8d90b28a|; ___rl__test__cookies=1477713325778; PICUGC_FLASH=; PICUGC_SESSION=90fbe28fda40b5ff04f343a261160ce83d3439fc-%00_TS%3Asession%00");
			con.connect();
			redirectException(con.getHeaderField("Set-Cookie"));
			BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			StringBuilder page=new StringBuilder();
			String line;
			while((line=br.readLine()) != null) {
				if(line.length() == 0) continue;
				line=line.replaceAll("^[ \t]+", "");
				line=line.replaceAll("[ \t]+$", "");
				page.append(line);
				pw.write(line);
			}
			pw.close();
			return page.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static void redirectException(String cookie) throws RedirectNetException{
		if(cookie == null || cookie.indexOf("youdao") == -1)
			throw new RedirectNetException(cookie);
		else return;
	}
}
