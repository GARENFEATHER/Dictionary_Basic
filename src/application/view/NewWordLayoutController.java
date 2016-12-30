package application.view;

import application.*;
import application.model.*;
import application.util.AlertDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewWordLayoutController {
	private Main mainApp;
	private Word word;
	
	@FXML
	private TextField newSpell;
	@FXML
	private TextField newMeaning;
	@FXML
	private TextField newSenEng1;
	@FXML
	private TextField newSenCh1;
	@FXML
	private TextField newSenEng2;
	@FXML
	private TextField newSenCh2;

	@FXML
	private Button ok;
	@FXML
	private Button cancel;
	
	private Stage dialogStage;

	@FXML
	private void initialize() {
	}
	public void setMain(Main mainApp) {
		this.mainApp=mainApp;
	}
	public void setWord(Word word) {
		this.word=word;
		this.newSpell.setText(word.getSpell());
		this.newSpell.setEditable(false);
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage=dialogStage;
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	@FXML
	private void handleOk() {
		String[] errorMessage=new String[2];
		if(checkInput(errorMessage)) {
			word.addMessage(newMeaning.getText());
			if(newSenEng1.getText().length() != 0 && newSenCh1.getText().length() != 0)
				word.sentence.put(newSenEng1.getText(), newSenCh1.getText());
			if(newSenEng2.getText().length() != 0 && newSenCh2.getText().length() != 0)
				word.sentence.put(newSenEng2.getText(), newSenCh2.getText());
			word.notDefault();
			mainApp.addNewWord(word);
			dialogStage.close();
		} else {
			AlertDialog.createInfo(errorMessage[0], errorMessage[1]);
		}
	}
	private boolean checkInput(String[] errorMessage) {
		if(newMeaning.getText().length() == 0) {
			errorMessage[0]="Invalid Meaning";
			errorMessage[1]="You have to give your meaning to the word";
			return false;
		} else if((newSenEng1.getText().length() != 0 && newSenCh1.getText().length() == 0) || (newSenEng1.getText().length() == 0 && newSenCh1.getText().length() != 0) || (newSenEng2.getText().length() != 0 && newSenCh2.getText().length() == 0) || (newSenEng2.getText().length() == 0 && newSenCh2.getText().length() != 0)) {
			errorMessage[0]="Invalid sentence";
			errorMessage[1]="Sentence has to be with the translation";
			return false;
		}
		return true;
	}
}
