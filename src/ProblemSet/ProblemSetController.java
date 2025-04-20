package ProblemSet;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import PracticeExercise.PracticeExerciseController;
import Util.SQLFn;
import Util.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProblemSetController implements Initializable{
	
	@FXML
	TextField search;
	
	@FXML
	ComboBox<String> status, title, difficulty;
	
	@FXML
	HBox categoryList;
	
	@FXML
	GridPane mainPane;
	
	String conditions[] = {"", "", ""};
	
	private int lastStop = 0;
	
	List<Map<String, Object>> list = new ArrayList<>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		ObservableList<String> statusOpt = FXCollections.observableArrayList( "Complete", "Incomplete", "In Progress" );
		ObservableList<String> titleOpt = FXCollections.observableArrayList( "Ascending", "Descending" );
		ObservableList<String> difficultyOpt = FXCollections.observableArrayList( "Easy", "Medium", "Hard" );
		
		status.setItems(statusOpt);
		title.setItems(titleOpt);
		difficulty.setItems(difficultyOpt);
		
		status.setButtonCell(promptTextBack(status));
		title.setButtonCell(promptTextBack(title));
		difficulty.setButtonCell(promptTextBack(difficulty));
		
		@SuppressWarnings("unused")
		ResultSet rs = null;
		
		try {
			
			refreshData();
			addChildren();
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		
		
	}
	
	public void resetFn(ActionEvent e) throws SQLException {
		
		refreshData();
		
		String newCondition[] = {"", "", ""};
		conditions = newCondition;
		
		status.valueProperty().set(status.getPromptText());
		title.valueProperty().set(title.getPromptText());
		difficulty.valueProperty().set(difficulty.getPromptText());
		
	}
	
	public void nextFn(ActionEvent e) throws SQLException {
		
		if(lastStop == list.size())
			return;
		
		mainPane.getChildren().clear();
		
		addChildren();
		
		
	}
 
	public void statusBtnFn(ActionEvent e) throws SQLException {
		
		conditions[2] = status.getValue(); 
		categoryConditions();
		
	}
	
	public void difficultyBtnFn(ActionEvent e) throws SQLException { 
		
		conditions[1] = difficulty.getValue(); 
		categoryConditions();
		
	}
	
	public void titleBtnFn(ActionEvent e) throws SQLException { 
		
		conditions[0] = title.getValue(); 
		categoryConditions();
		
	}
	
	public void previousFn(ActionEvent e) throws Exception {
		
		lastStop = lastStop - (lastStop % 9);
		
		if(lastStop == 0)
			return;
		
		lastStop -= 9;
		mainPane.getChildren().clear();
		
		for(int x = 0, y = 0; lastStop < list.size(); x++, lastStop++) {
			
			if(x == 3) {
				
				x = 0;
				y++;
				
				if(y == 3)
					break;
				
			}
			
			mainPane.add(new CardList(list.get(lastStop), mainPane), x, y);
			
		}
		
	}
	
	public void backFn(ActionEvent e) throws IOException {
		
		controller.ct.switchScene(e, "../application/Homepage.fxml");
		
	}
	
	public void searchFn(ActionEvent e) throws SQLException {
		
		if(search.getText().isEmpty())
			return;
		
		refreshData();
		
		for(int i = 0; i < list.size();) {
			
			String title = (String) list.get(i).get("Title");
			
			if(!title.toLowerCase().contains(search.getText().toLowerCase()))
				list.remove(i);
			else
				i++;
			
		}
		
		lastStop = 0;
		addChildren();
	}
	
	/*
	 * 
	 * Starts of Class functions
	 * 
	 */
	
	private void refreshData() throws SQLException {
		
		list.clear();
		
		ResultSet rs = SQLFn.get("Problem Set");
		ResultSetMetaData rsmd = rs.getMetaData();
		
		
		while(rs.next()) {
			
			Map<String, Object> row = new HashMap<>();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				String columnName = rsmd.getColumnName(i);
				Object value = rs.getObject(i);
				
				row.put(columnName, value);
			}
			
			list.add(row);
		}
		
	}
	
	private void categoryConditions() throws SQLException {
		
		if(search.getText().isEmpty())
			refreshData();
		else
			searchFn(new ActionEvent());
		
		if(!conditions[0].isEmpty() ) {
			
			if(conditions[0].equals("Ascending"))
				list.sort(Comparator.comparing(e -> (String) e.get("Title")));
			else if(conditions[0].equals("Descending"))
				list.sort(Comparator.comparing(e -> (String) e.get("Title"), Comparator.reverseOrder()));
			
		}
		
		if(!conditions[1].isEmpty()) {
			
			if(conditions[1].equals("Easy"))
				textCondition("Difficulty", "Easy");
			else if(conditions[1].equals("Medium"))
				textCondition("Difficulty", "Medium");
			else if(conditions[1].equals("Hard"))
				textCondition("Difficulty", "Hard");
			
		}
		
		if(!conditions[2].isEmpty()) {
			
			// "Complete", "Incomplete", "In Progress"
			
			if(conditions[2].equals("In Progress"))
				textCondition("Status", "In Progress");
			else if(conditions[2].equals("Incomplete"))
				textCondition("Status", "Incomplete");
			else if(conditions[2].equals("Complete"))
				textCondition("Status", "Complete");
			
			
		}
		
		lastStop = 0;
		addChildren();
		
	}
	
	private void textCondition(String table, String condition) {
		
		for(int i = 0; i < list.size(); ) {
			
			if(!list.get(i).get(table).equals(condition))
				list.remove(i);
			else
				i++;
		}
		
	}
	
	private void addChildren() throws SQLException {
		
		mainPane.getChildren().clear();
		
		for(int x = 0, y = 0; lastStop < list.size(); x++, lastStop++) {
			
			if(x == 3) {
				x = 0;
				y++;
				
				if(y == 3)
					break;
			}
			
			mainPane.add(new CardList(list.get(lastStop), mainPane), x, y);
			
		}
		
	}
	
	private ListCell<String> promptTextBack(ComboBox<String> cb) {
		
		ListCell<String> lc = new ListCell<String>() {
		
			protected void updateItem(String item, boolean empty) {
				
				super.updateItem(item, empty);
				if(item == null || empty || item == "")
					this.setText(cb.getPromptText());
				else
					this.setText(item);
			}
		};
		
		return lc;
	}
	
}

/*
 * 
 * The card inside of the gridpane
 * 
 */

class CardList extends VBox{
	
	CardList(Map<String, Object> data, GridPane node) throws SQLException {
		
		this.setId("mainPane");
		this.getStylesheets().add(getClass().getResource("mainPane.css").toExternalForm());
		
		Label title = new Label((String) data.get("Title"));
		Label status = new Label("Status: " + data.get("Status"));
		Label diff = new Label("Difficulty: " + data.get("Difficulty"));
		
		Button code = new Button("Code");
		code.setOnAction(e -> {
			
			Stage stage = (Stage) node.getScene().getWindow();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../PracticeExercise/PracticeExercise.fxml"));
			Parent root = null;
			try {
				root = loader.load();
			} catch (IOException e1) { e1.printStackTrace(); }
			
			PracticeExerciseController controller = loader.getController();
			controller.init(title.getText());
			
			stage.setScene(new Scene(root));
			stage.show();
			
		});
		
		this.setSpacing(10);
		this.setPrefHeight(104);
		this.setPrefWidth(221);
		
		
		
		this.setPadding(new Insets(10, 0, 0, 15));
		this.getChildren().addAll(title, status, diff, code);
		
	}
	
}