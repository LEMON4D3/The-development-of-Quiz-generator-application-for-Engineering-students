package login;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.controller;
import util.user;

public class loginController implements Initializable{

	@FXML
	TextField username;

	@FXML
	Label errorT;
	
	@FXML
	PasswordField password;
	
	Statement statement;
	ResultSetMetaData rsmd;
	
	List<Map<String, Object>> data = new ArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	
	public void signUpBtnFn(ActionEvent e) throws IOException {
		
		new controller().changeScene(e, "/signUp/signUp.fxml");
		
	}
	
	// login Btn
	public void loginFn(ActionEvent e) throws SQLException, IOException {
		
		String folderPath = "application/user/";
        File folder = new File(folderPath);
        
        if (!folder.exists()) { boolean created = folder.mkdirs();  }
		
        
        getUser();
        
        if(statement == null) {
    		System.err.println("Statement is null");
    		return;
    	}
        
        String usernameT = username.getText();
    	String passwordT = password.getText();
        
    	boolean isData = false;
    	
        for(int i = 0; i < data.size(); i++) {
        	
        	Map<String, Object> map = data.get(i);
        	if(usernameT.equals(map.get("username")) && passwordT.equals(map.get("password"))) {
        		
        		user.currentUser = (String) map.get("username");
        		user.isTeacher = ((Integer) map.get("isTeacher") == 1) ? true : false;
        		
        		if(user.isTeacher)
        			new controller().changeScene(e, "/homepage/teacher/Homepage.fxml");
        		else
        			new controller().changeScene(e, "/homepage/student/Homepage.fxml");
        		
        		isData = true;
        		
        	}
        }

		if(usernameT.isEmpty() || passwordT.isEmpty())
			errorT.setText("Please fill in all fields");
		else if (!isData)
        	errorT.setText("Invalid Username or Password");
        
	}
	
	private void getUser() {
		
        	
    	try {
    		
    		Connection connection = DriverManager.getConnection("jdbc:sqlite:application/user/user.db");
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery("select * FROM user");
    		rsmd = rs.getMetaData();
    		
    		while(rs.next()) {
    			
    			Map<String, Object> map = new HashMap<>();
    			
    			for(int i = 1; i <= rsmd.getColumnCount(); i++) { map.put(rsmd.getColumnName(i), rs.getObject(i)); }
    			
    			data.add(map);
    			
    		}
    		
    	} catch (SQLException e) { e.printStackTrace(); }
    	
    	
    	
	}
	
	//End of login Btn
}
