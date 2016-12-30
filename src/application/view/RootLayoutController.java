package application.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import application.*;
import application.model.*;
import application.util.*;
public class RootLayoutController {
	private Main mainApp;
	private ArrayList<String> history=new ArrayList<String>();
	private int historyNum;
	
	@FXML
	private Button tabDict;
	@FXML
	private Button tabAbout;
	@FXML
	private Button goBack;
	@FXML
	private Button goForward;
	@FXML
	private Button toSearch;
	@FXML
	private Button clearAll;
	@FXML
	private Button addNew;
	@FXML
	private Button deleteCustom;
	@FXML
	private Button youdao1;
	@FXML
	private Button youdao2;
	
	@FXML
	private Label showWord;
	@FXML
	private Label showMeaning;
	@FXML
	private Label senEng1;
	@FXML
	private Label senCh1;
	@FXML
	private Label senEng2;
	@FXML
	private Label senCh2;
	@FXML
	private Label noResult1;
	@FXML
	private Label noResult2;
	@FXML
	private Label errorWord;
	
	@FXML
	private ListView<String> associationArea;
	@FXML
	private TextField context;
	
	@FXML
	private AnchorPane dictSuccess;
	@FXML
	private AnchorPane dictFailure;
	@FXML
	private AnchorPane dictNotFound;
	@FXML
	private AnchorPane about;
	
	@FXML
	private ImageView sasuke;
	@FXML
	private ImageView logo;
	@FXML
	private ImageView original;

	@FXML
	private void initialize() {
		emptyResult();
		context.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				newValue=InputProcess.processInvalid(newValue);
				if(newValue != null && newValue.length() != 0) {
					mainApp.searchForResult(newValue);
				}
			}
		});
	}
	public RootLayoutController() {
		this.historyNum=history.size();
	}
	public ImageView getNotFound() {
		return this.sasuke;
	}
	public ImageView getLogo() {
		return this.logo;
	}
	public ImageView getOriginal() {
		return original;
	}
	public void setMain(Main mainApp) {
		this.mainApp=mainApp;
		associationArea.setItems(mainApp.getResultList());
	}
	public ArrayList<String> getHistory() {
		return history;
	}
	public void setHistoryNum(int historyNum) {
		this.historyNum=historyNum;
	}
	public void theLatestView() {
		if(historyNum == 0) return;
		String spell=history.get(historyNum-1);
		searchBase(spell, true);
		context.setText(spell);
	}
	@FXML
	private void handleAddNewWord() {
		String word=InputProcess.processInvalid(context.getText());
		this.mainApp.showAddWordDialog(word);
	}
	@FXML
	private void handleSearch() {
		String content=context.getText();
		searchBase(content,false);
	}
	private void searchBase(String content,boolean recall) {
		emptyResult();
		content=InputProcess.processInvalid(content);
		if(content == null || content.length() == 0) {
			emptyResult();
			AlertDialog.emptyInput();
			return;
		}
		if(!recall) {
			if(historyNum != history.size()) clearHistory(historyNum);
			if(historyNum != 0 && content != history.get(historyNum-1) || historyNum == 0) {
				history.add(content);
				historyNum=history.size();
			}
		}
		mainApp.searchForResult(content);
		if(mainApp.inTheList(content)) {
			mode1();
			exisistWord(mainApp.getWord(content));
		} else {
			mode2();
			noResult1.setText("当前输入查找的内容是："+content);
			youdao1.setText("有道查询："+content);
			if(mainApp.inTheCorpus(content)) {
				noResult2.setText("本地词库中没有更多关于此内容的信息，建议选择联网查找");
				youdao2.setVisible(false);
			}
			else {
				if(mainApp.getResultList().size() != 0) {
					String mostPossible=mainApp.getResultList().get(0);
					noResult2.setText("查找内容可能存在拼写错误，您是不是要找："+mostPossible);
					youdao2.setVisible(true);
					youdao2.setText("本地查询："+mostPossible);
				} else {
					noResult2.setText("查找内容不存在当前语料库中，可选择联网查询");
					youdao2.setVisible(false);
				}
			}
		}
	}
	@FXML
	private void handleBack() {
		if(historyNum == 0) return;
		historyNum--;
		if(historyNum == 0) {
			historyNum=1;
			return;
		}
		String text=history.get(historyNum-1);
		context.setText(text);
		searchBase(text,true);
		//System.out.println(this.history.size()+" "+this.historyNum);
	}
	@FXML
	private void handleForward() {
		if(historyNum == 0) return;
		if(historyNum != history.size()) {
			String text=history.get(historyNum);
			context.setText(text);
			searchBase(text,true);
			historyNum++;
		}
		//System.out.println(this.history.size()+" "+this.historyNum);
	}
	@FXML
	private void handleClearAll() {
		context.setText("");
		emptyResult();
		this.mainApp.getResultList().clear();
	}
	@FXML
	private void handleHyperLink1() {
		String content=youdao1.getText();
		content=content.substring(content.lastIndexOf('：')+1, content.length());
		internetSearch(content);
	}
	@FXML
	private void handleHyperLink2() {
		String content=youdao2.getText();
		String text=content.substring(content.lastIndexOf('：')+1, content.length());
		if(content.indexOf("本地") == -1) {
			internetSearch(text);
		} else {
			searchBase(text,false);
			context.setText(text);
		}
	}
	@FXML
	private void handleDelete() {
		String spell=context.getText();
		mainApp.deleteCustomWord(spell);
		emptyResult();
	}
	@FXML
	private void handleXML(MouseEvent arg) {
		if(arg.getButton().equals(MouseButton.PRIMARY)) {
			mainApp.saveWordData(history);
			AlertDialog.historySaved();
		} else if(arg.getButton().equals(MouseButton.SECONDARY)) {
			boolean status=mainApp.delXMLData();
			historyNum=0;
			history.clear();
			handleClearAll();
			if(status) AlertDialog.historyReset();
			else AlertDialog.noHistory();
		}
	}
	@FXML
	private void handleSelected(MouseEvent arg) {
		if(arg.getButton().equals(MouseButton.PRIMARY)) {
			searchBase(associationArea.getSelectionModel().getSelectedItem(),false);
			context.setText(associationArea.getSelectionModel().getSelectedItem());
		} else if(arg.getButton().equals(MouseButton.SECONDARY)) {
			mainApp.getResultList().remove(associationArea.getSelectionModel().getSelectedIndex());
		}
	}
	@FXML
	private void handleGoOriginal() {
		String url="http://dict.youdao.com/w/eng/"+showWord.getText();
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void internetSearch(String text) {
		try {
			Word word=URLParse.parse(text);
			if(word != null) {
				mainApp.addNewWord(word);
				searchBase(word.getSpell(),false);
				context.setText(text);
			} else {
				mode4();
				this.errorWord.setText(text+"?");
			}
		} catch (IOException e) {
			AlertDialog.createError("UnknownHostException!", "DNS解析错误或无法连接网络，请检查本地网络情况");
			e.printStackTrace();
		} catch (RedirectNetException e) {
			AlertDialog.createError("RedirectNetException!", "网络发生未知的重定向，如果连接了校园网或其他网络，请检查登陆状况");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	@FXML
	private void chooseDict() {
		mode1();
		this.goBack.setDisable(false);
		this.goForward.setDisable(false);
		this.toSearch.setDisable(false);
	}
	@FXML
	private void chooseAbout() {
		mode3();
		this.goBack.setDisable(true);
		this.goForward.setDisable(true);
		this.toSearch.setDisable(true);
	}
	private void exisistWord(Word theWord) {
		if(!theWord.defaultCheck()) deleteCustom.setVisible(true);
		else deleteCustom.setVisible(false);
		showWord.setText(theWord.getSpell());
		showMeaning.setText(theWord.getMessage());
		if(theWord.sentence.size() != 0) {
			HashMap<String,String> sentences=theWord.sentence;
			Iterator<String> iter=sentences.keySet().iterator();
			String s=iter.next();
			senEng1.setText(s);
			senCh1.setText(sentences.get(s));
			if(iter.hasNext()) {
				s=iter.next();
				senEng2.setText(s);
				senCh2.setText(sentences.get(s));
			} else {
				senEng2.setText(null);
				senCh2.setText(null);
			}
		}
	}
	private void mode1() {
		dictSuccess.setVisible(true);
		getOriginal().setVisible(true);
		dictFailure.setVisible(false);
		dictNotFound.setVisible(false);
		about.setVisible(false);
	}
	private void mode2() {
		dictSuccess.setVisible(false);
		dictFailure.setVisible(true);
		dictNotFound.setVisible(false);
		about.setVisible(false);
	}
	private void mode3() {
		dictSuccess.setVisible(false);
		dictFailure.setVisible(false);
		dictNotFound.setVisible(false);
		about.setVisible(true);
	}
	private void mode4() {
		dictSuccess.setVisible(false);
		dictFailure.setVisible(false);
		dictNotFound.setVisible(true);
		about.setVisible(false);
	}
	private void clearHistory(int start) {
		for(int i=start;i<history.size();i++)
			history.remove(i);
		historyNum=history.size();
		System.out.println(history.size()+" "+historyNum);
	}
	private void emptyResult() {
		mode1();
		getOriginal().setVisible(false);
		showWord.setText(null);
		showMeaning.setText(null);
		senEng1.setText(null);
		senCh1.setText(null);
		senEng2.setText(null);
		senCh2.setText(null);
		deleteCustom.setVisible(false);
	}
}
