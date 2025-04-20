package ProblemSet;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Util.SQLFn;
import Util.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProblemSetController implements Initializable{
	
	@FXML
	ComboBox<String> status, title, difficulty;
	
	@FXML
	HBox categoryList;
	
	@FXML
	GridPane mainPane;
	
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
			rs = SQLFn.get("Problem Set");
			
			int x = 0, y = 0;
			
			while(rs.next() && y < 3) {
				
				if(x == 3) {
					x = 0;
					y++;
					
					if(y == 3)
						break;
				}
				
				mainPane.add(new CardList(rs), x, y);
				x++;
			}
			
		} catch (SQLException e) { e.printStackTrace(); }
		
		
		
	}
	
	public void resetFn(ActionEvent e) {
		
		status.valueProperty().set(null);
		title.valueProperty().set(null);
		difficulty.valueProperty().set(null);
		
	}
	
	
	
	public void backFn(ActionEvent e) throws IOException {
		
		controller.ct.switchScene(e, "../application/Homepage.fxml");
		
	}
	
	ListCell<String> promptTextBack(ComboBox<String> cb) {
		
		ListCell<String> lc = new ListCell<String>() {
		
			protected void updateItem(String item, boolean empty) {
				
				super.updateItem(item, empty);
				if(item == null || empty)
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
	
	CardList(ResultSet set) throws SQLException {
		
		this.setId("mainPane");
		this.getStylesheets().add(getClass().getResource("mainPane.css").toExternalForm());
		
		Label title = new Label(set.getString(2));
		Label status = new Label("Status: " + set.getString(3));
		Label diff = new Label("Difficulty: " + set.getString(4));
		
		Button code = new Button("Code");
		code.setOnAction(e -> {
			
			
			
		});
		
		this.setSpacing(10);
		this.setPrefHeight(104);
		this.setPrefWidth(221);
		
		
		
		this.setPadding(new Insets(10, 0, 0, 15));
		this.getChildren().addAll(title, status, diff, code);
		
	}
	
}