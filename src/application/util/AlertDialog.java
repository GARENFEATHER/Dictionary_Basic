package application.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertDialog {
	public static void createInfo(String header,String content) {
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Illegal");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	public static void createError(String header,String content) {
		Alert alert=new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	public static void emptyInput() {
		Alert alert=new Alert(AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText("An Illegal Input");
		alert.setContentText("Empty input!");
		alert.showAndWait();
	}
	public static void historySaved() {
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Succeeded");
		alert.setHeaderText("HistoryData");
		alert.setContentText("Search history has been updated!");
		alert.showAndWait();
	}
	public static void historyReset() {
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Succeeded");
		alert.setHeaderText("HistoryData");
		alert.setContentText("Search history has been reset!");
		alert.showAndWait();
	}
	public static void noHistory() {
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle("Failed");
		alert.setHeaderText("HistoryData");
		alert.setContentText("No search history has been saved now!");
		alert.showAndWait();
	}
}
