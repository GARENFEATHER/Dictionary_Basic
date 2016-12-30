package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import application.model.*;
import application.util.*;
import application.view.*;
public class Main extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private HashMap<String,Word> wordList=new HashMap<String,Word>();
	private HashMap<String,Integer> wordCorpus=new HashMap<String,Integer>();
	
	private HashMap<String,Word> additionalWordList=new HashMap<String,Word>();
	private ObservableList<String> resultList=FXCollections.observableArrayList();
	private File XMLfile=new File("resources/data.xml");
	
	public Main() {
		Preferences pref=Preferences.userNodeForPackage(Main.class);
		pref.put("filePath", XMLfile.getAbsolutePath());
		initWordData();
		initPartialExtension();
		initWordCorpus();
		System.out.println(wordList.size()+" "+wordCorpus.size());
	}
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage=primaryStage;
		this.primaryStage.setTitle("Dictionary");
		initLayout();
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public ObservableList<String> getResultList() {
		return resultList;
	}
	public boolean inTheList(String word) {
		if(wordList.get(word) != null || additionalWordList.get(word) != null) return true;
		else return false;
	}
	public boolean inTheCorpus(String word) {
		if(wordCorpus.get(word) != null) return true;
		else return false;
	}
	public Word getWord(String word) {
		if(wordList.get(word) != null)
			return wordList.get(word);
		else return additionalWordList.get(word);
	}
	public void deleteCustomWord(String spell) {
		additionalWordList.remove(spell);
		wordCorpus.remove(spell);
	}
	public void addNewWord(Word word) {
		additionalWordList.put(word.getSpell(), word);
		if(wordCorpus.get(word.getSpell()) == null)
			wordCorpus.put(word.getSpell(), 1);
	}
	public void saveWordData(ArrayList<String> history) {
		try {
			JAXBContext con=JAXBContext.newInstance(LoadWordData.class);
			Marshaller m=con.createMarshaller();
			LoadWordData lwd=new LoadWordData();
			lwd.setAdditionalWordList(additionalWordList);
			lwd.setHistory(history);
			lwd.setHistoryNum(history.size());
			if(!XMLfile.exists()) XMLfile.createNewFile();
			m.marshal(lwd, XMLfile);
		} catch(Exception e) {
			AlertDialog.createError("Save Error", "Could not save word data!");
		}
	}
	private void loadWordData(RootLayoutController controller) {
		try {
			JAXBContext con=JAXBContext.newInstance(LoadWordData.class);
			Unmarshaller um=con.createUnmarshaller();
			
			LoadWordData lwd=(LoadWordData) um.unmarshal(XMLfile);
			additionalWordList.clear();
			additionalWordList.putAll(lwd.getAdditionalWordList());
			addtionalToCorpus();
			
			controller.getHistory().clear();
			controller.getHistory().addAll(lwd.getHistory());
			
			controller.setHistoryNum(lwd.getHistoryNum());
			
			controller.theLatestView();
		} catch(Exception e) {
			AlertDialog.createError("Load Error", "Could not load word data!");
		}
	}
	private void addtionalToCorpus() {
		Iterator<String> iter=additionalWordList.keySet().iterator();
		while(iter.hasNext()) {
			String spell=iter.next();
			if(wordCorpus.get(spell) == null)
				wordCorpus.put(spell, 1);
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
	public void initLayout() {
		try {
			FXMLLoader loader=new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout2.fxml"));
			rootLayout=(BorderPane) loader.load();
			
			File file=new File("resources/back.jpg");
			String urlLocal=file.toURI().toURL().toString();
			
			rootLayout.setBackground(new Background(new BackgroundImage(new Image(urlLocal),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));
			Scene scene=new Scene(rootLayout);
			primaryStage.setScene(scene);
			//primaryStage.initStyle(StageStyle.UNDECORATED);
			
			RootLayoutController controller=loader.getController();
			controller.setMain(this);
			
			file=new File("resources/notFound.png");
			urlLocal=file.toURI().toURL().toString();
			controller.getNotFound().setImage(new Image(urlLocal));
			
			file=new File("resources/logo.png");
			urlLocal=file.toURI().toURL().toString();
			controller.getLogo().setImage(new Image(urlLocal));
			
			file=new File("resources/netView.png");
			urlLocal=file.toURI().toURL().toString();
			controller.getOriginal().setImage(new Image(urlLocal));
			
			if(XMLfile.exists()) loadWordData(controller);
			
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void initWordData() {
		Pattern wordSplit1=Pattern.compile("([0-9]+)\t(.+)\t\t(.+)");
		Pattern wordSplit2=Pattern.compile("([0-9]+)\t(.+)\t(.+)\t(.+)");
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("resources/dictionary.txt")));
			String line=br.readLine();
			//int i=1;
			while((line=br.readLine()) != null) {
				Matcher match=wordSplit1.matcher(line);
				if(match.find()) {
					String spell=match.group(2).toLowerCase();
					String message=match.group(3);
					wordList.put(spell, new Word(spell,message));
				} else {
					match=wordSplit2.matcher(line);
					if(match.find()) {
						String spell=match.group(2);
						String message=match.group(4);
						wordList.put(spell, new Word(spell,message));
					}
				}
				//i++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initPartialExtension() {
		Pattern head=Pattern.compile("^[0-9]+\\|");
		Pattern example=Pattern.compile("(^[^0-9]+)/r/n     (.+)/r/n");
		//int i=1;
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("resources/cetsix_new.txt")));
			String line="";
			Word newWord=new Word("",null);
			while((line=br.readLine()) != null) {
				Matcher match=head.matcher(line);
				if(match.find()) {
					Pattern wordOne=Pattern.compile("^[0-9]+\\|(.+?)\\|(.+?)(\\||<br>)");
					match=wordOne.matcher(line);
					if(match.find()) {
						String spell=match.group(1).toLowerCase();
						newWord=wordList.get(spell);
						if(newWord == null) {
							String message=match.group(2);
							wordList.put(spell, new Word(spell,message));
							newWord=wordList.get(spell);
						}
						Pattern sentenceGet=Pattern.compile("^[0-9]+\\|.+?\\|.+?(\\||<br>)(.+/r/n)");
						match=sentenceGet.matcher(line);
						if(match.find()) {
							String[] engAndCh=match.group(2).split("/r/n     ");
							if(engAndCh[0].charAt(0) == ' ')
								engAndCh[0]=engAndCh[0].substring(1, engAndCh[0].length());
							engAndCh[1]=engAndCh[1].substring(0, engAndCh[1].length()-4);
							newWord.sentence.put(engAndCh[0], engAndCh[1]);
						}
						//i++;
					}
				} else {
					match=example.matcher(line);
					if(match.find()) {
						String eng=match.group(1);
						if(eng.lastIndexOf('|') != -1)
							eng=eng.substring(eng.lastIndexOf('|')+1,eng.length());
						if(eng.charAt(0) == ' ')
							eng=eng.substring(1,eng.length());
						String ch=match.group(2);
						newWord.sentence.put(eng, ch);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initWordCorpus() {
		try {
			InputStream is=new FileInputStream("resources/Corpus.xls");
			jxl.Workbook readxls=Workbook.getWorkbook(is);
			Sheet corpus=readxls.getSheet(0);
			int rows=corpus.getRows();
			for(int i=1;i<rows;i++) {
				Cell word=corpus.getCell(1,i);
				Cell freq=corpus.getCell(2,i);
				//System.out.println(word.getContents()+" ** "+Integer.parseInt(freq.getContents()));
				wordCorpus.put(word.getContents().toLowerCase(),Integer.parseInt(freq.getContents()));
			}
			readxls.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//a、at、of、be、in、to、with、and、the
		HashSet<String> baseWord=new HashSet<String>();
		baseWord.add("a");
		baseWord.add("at");
		baseWord.add("to");
		baseWord.add("of");
		baseWord.add("the");
		baseWord.add("to");
		baseWord.add("and");
		baseWord.add("with");
		baseWord.add("be");
		baseWord.add("in");
		baseWord.add("for");
		Iterator<String> exitWords=wordList.keySet().iterator();
		while(exitWords.hasNext()) {
			String key=exitWords.next();
			if(wordCorpus.get(key) == null) {
				int sum=0,count=0;
				String[] keyDiverse=key.split(" ");
				for(String simple:keyDiverse) {
					if(!baseWord.contains(simple)) {
						if(wordCorpus.get(simple) != null) {
							sum+=wordCorpus.get(simple);
						} else {
							wordCorpus.put(simple, 1);
							sum+=1;
						}
					} else {
						sum+=1;
					}
					count++;
				}
				wordCorpus.put(key, sum/count);
			}
		}
		EditDistanceProcess.wordCorpus=wordCorpus;
	}
	public void showAddWordDialog(String spell) {
		try {
			FXMLLoader loader=new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/NewWordLayout3.fxml"));
			AnchorPane page=(AnchorPane) loader.load();
			
			File file=new File("resources/b2.jpg");
			String urlLocal=file.toURI().toURL().toString();
			
			page.setBackground(new Background(new BackgroundImage(new Image(urlLocal),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));
			
			Stage dialog=new Stage();
			dialog.setTitle("Add New Word");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(primaryStage);
			
			Scene scene=new Scene(page);
			dialog.setScene(scene);
			
			NewWordLayoutController controller=loader.getController();
			controller.setMain(this);
			controller.setDialogStage(dialog);
			controller.setWord(new Word(spell));
			
			dialog.showAndWait();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public boolean delXMLData() {
		if(XMLfile.exists()) {
			XMLfile.delete();
			return true;
		} else return false;
	}
	public void searchForResult(String word) {
		HashSet<String> different=new HashSet<String>();
		ArrayList<String> result1=null,result2=null;
		StringBuilder w=new StringBuilder(word);
			word=w.toString();
			if(w.indexOf(" ") != -1) {
				result1=EditDistanceProcess.ComplexDistance(word,1);
				if(result1.size() < 30) result2=EditDistanceProcess.ComplexDistance(word,2);
			} else {
				result1=EditDistanceProcess.editDistanceOne(word);
				if(result1.size() < 30) result2=EditDistanceProcess.editDistanceTwo(word);
			}
			resultList.clear();
			Collections.sort(result1,new ResemblenceComparator(wordCorpus));
			if(result2 != null && result2.size() != 0) Collections.sort(result2,new ResemblenceComparator(wordCorpus));
			if(result1.size() != 0) different.addAll(result1);
			if(result2 != null && result2.size() != 0) different.addAll(result2);
			resultList.addAll(different);
	}
}
