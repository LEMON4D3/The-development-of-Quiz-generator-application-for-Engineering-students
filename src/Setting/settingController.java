package Setting;

import javafx.scene.paint.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Util.Preferences;
import Util.SQLFn;
import Util.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class settingController implements Initializable{
	
	@FXML
	Rectangle mainPreview;
	
	@FXML
	Rectangle headerPreview;
	
	String blackPackage[] = {
		"1A1A1A",
		"282828",
		"363636"
	};
	
	String normalPackage[] = {
		"00008B",
		"FFD700",
		"002A45"
	};
	
	String lightPackage[] = {
		"F0F0F0",
		"F9F9F9",
		"CECECE"
	};
	
	String purplePackage[] = {
		"492B51",
		"962EFD",
		"9B909E"
	};
	
	String[] colorSet = {Preferences.mainBackground, Preferences.headerBackground, Preferences.borderColor};
	
	@FXML
	Button saveBtn, exitBtn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		saveBtn.setFocusTraversable(false);
		exitBtn.setFocusTraversable(false);
		
		
		
	}
	
	void setColor(String color, int index) {
		
		colorSet[index] = color;
		
		switch(index) {
		case 0:
			mainPreview.setFill(Color.valueOf(colorSet[index]));
			break;
		case 1:
			headerPreview.setFill(Color.valueOf(colorSet[index]));
			break;
		case 2:
			headerPreview.setStroke(Color.valueOf(colorSet[index]));
		}
		
		
		
	}
	
	public void mainBlack(MouseEvent e) { setColor(blackPackage[0], 0); }
	
	public void headerBlack(MouseEvent e) { setColor(blackPackage[1], 1); }
	
	public void borderBlack(MouseEvent e) { setColor(blackPackage[2], 2); }
	
	public void mainNormal(MouseEvent e) { setColor(normalPackage[0], 0); }
	
	public void headerNormal(MouseEvent e) { setColor(normalPackage[1], 1); }
	
	public void borderNormal(MouseEvent e) { setColor(normalPackage[2], 2); }
	
	public void mainLight(MouseEvent e) { setColor(lightPackage[0], 0); }
	
	public void headerLight(MouseEvent e) { setColor(lightPackage[1], 1); }
	
	public void borderLight(MouseEvent e) { setColor(lightPackage[2], 2); }
	
	public void mainPurple(MouseEvent e) { setColor(purplePackage[0], 0); }
	
	public void headerPurple(MouseEvent e) { setColor(purplePackage[1], 1); }
	
	public void borderPurple(MouseEvent e) { setColor(purplePackage[2], 2); }
	
	
	
	public void save(ActionEvent e) throws Exception {
		
		String columnName[] = {"background", "header", "border"};
		
		SQLFn.update("User Preference", columnName, colorSet, "1");
		
		controller.ct.switchScene(e, "../application/Homepage.fxml");
		
	}
	
	public void exit(ActionEvent e) throws IOException {
		
		controller.ct.switchScene(e, "../application/Homepage.fxml");
		
	}
}
