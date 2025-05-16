package homepage.teacher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.GroupLayout.Alignment;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.Util;
import util.controller;
import util.user;
import homepage.lib.emptyContainer;
import homepage.student.*;

public class homepageController implements Initializable{
	
	@FXML
	ImageView imageBook;
	
	@FXML
	VBox bodyPane;
	
	@FXML
	Button createClass, createClassBottomBtn;
	
	@FXML
	Label errorText, descT;
	
	@FXML
	TextField classNameTF;
	
	List<Map<String, Object>> list = new ArrayList<>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		getClasses();
		
	}

	public void reportBtnFn(ActionEvent event) {

		new controller().changeScene(event, "/homepage/teacher/report/Report.fxml");

	}

	public void userBtnFn(MouseEvent event) { new Util().userPopUp(event); }

	private void getClasses()  {
		
		try {
		
			String userDB = "application/user/teacher/" + user.currentUser + "/" + user.currentUser + ".db";
			
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + userDB);
			
			String getClassesString = "select `class name` from classes";
			Statement getClassesStatement = connection.createStatement();
			
			list = Util.getList(getClassesStatement, getClassesString);
			
			if(!list.isEmpty())
				removeBodyComponents();
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	normalContainer bodyContainer;
	
	private void removeBodyComponents() {
		
		bodyPane.getChildren().removeAll(imageBook, descT, createClassBottomBtn, bodyContainer);
		
		List<String> className = new ArrayList<>();
		
		try {
			
			Connection userConnection = DriverManager.getConnection("jdbc:sqlite:application/user/teacher/" + user.currentUser + "/" + user.currentUser + ".db");
			
			String getClassNameString = "select `class name` from classes";
			Statement getClassNameStatement = userConnection.createStatement();
			
			List<Map<String, Object>> list = Util.getList(getClassNameStatement, getClassNameString);
			
			for(Map<String, Object> i : list)
				className.add((String) i.get("class name")); 
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		if(className == null)
			System.out.println("Class Name is empty");
		
		bodyContainer = new normalContainer(className);
		bodyPane.getChildren().add(bodyContainer);
		
	}
	
	private class normalContainer extends ScrollPane {
		
		List<String> className = new ArrayList<>();
		
		normalContainer(List<String> className) {
			
			this.className = className;
			
			String scollPaneStyle = "-fx-background-color: transparent; -fx-background: transparent;";
			this.setStyle(scollPaneStyle);
			this.setVbarPolicy(ScrollBarPolicy.NEVER);
			
			this.setContent(new normalGridPane());
			this.setPadding(new Insets(15, 25, 15, 25));
			this.setMinHeight(585);
			
		}
		
		private class normalGridPane extends GridPane {
			
			normalGridPane() {

				this.setPadding(new Insets(0, 0, 0, 25));

				List<itemCard> itemCardList = new ArrayList<>();

				for(int x = 0, y = 0, i = 0; i < className.size(); x++, i++) {

					final int yLocation = y;

					itemCard label = new itemCard(className.get(i));
					GridPane.setHalignment(label, HPos.CENTER); // Horizontal center
					GridPane.setValignment(label, VPos.CENTER);

					System.out.println(label.getWidth());

					if (x >= 3) {

						x = 0;
						y++;

						this.add(label, x, y);

					} else this.add(label, x, y);

				}

				Platform.runLater(() -> {

					double widthFinal = 0;
					double containerWidth = this.localToScene(this.getBoundsInLocal()).getWidth();

					List<List<Node>> nodeList = new ArrayList<>();
					List<Node> nodeListTemp = new ArrayList<>();

					for(int i = 0; i < this.getChildren().size(); i++) {

						Node node = this.getChildren().get(i);
						Bounds bounds = node.localToScene(node.getBoundsInLocal());
						widthFinal += bounds.getWidth();

						if(900 < widthFinal) {

							nodeList.add(nodeListTemp);

							nodeListTemp = new ArrayList<>();
							nodeListTemp.add(node);
						} else nodeListTemp.add(node);

					}

					this.getChildren().clear();
					for(int y = 0; y < nodeList.size(); y++) {

						List<Node> nodeListTemp2 = nodeList.get(y);
						for(int x = 0; x < nodeListTemp2.size(); x++) {

							Node node = nodeListTemp2.get(x);
							GridPane.setHalignment(node, HPos.CENTER);
							GridPane.setValignment(node, VPos.CENTER);
							this.add(node, x, y);

						}

					}

				});

				this.setHgap(25);
				this.setVgap(25);
				
			}
			
		}
		
		private class itemCard extends VBox {
			
			itemCard(String title) {
				
				String backgroundStyle = "-fx-background-color: white;"
						+ "-fx-background-radius: 20;";
				
				this.setStyle(backgroundStyle);
				this.setOnMouseClicked(event -> {
				
					try {
						
						
						user.currentClass = title;
						System.out.println(user.currentClass);
						
						new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");
						
					} catch(Exception exception) {
						exception.printStackTrace();
					}
					
					
				});

				this.setPrefWidth(VBox.USE_COMPUTED_SIZE);
				this.setMaxHeight(185);
				this.setStyle(backgroundStyle);

				String upperStyle = "-fx-background-color: #00799A;"
						+ "-fx-background-radius: 20;";

				AnchorPane upperContainer = new AnchorPane();
				upperContainer.setStyle(upperStyle);
				upperContainer.setPrefHeight(133);

				HBox titleContainer = new HBox();
				titleContainer.setAlignment(Pos.CENTER);
				titleContainer.setPadding(new Insets(15, 25, 15, 25));

				Label titleT = new Label(title);
				titleT.setStyle("-fx-font-size: 20; -fx-text-fill: black;");

				titleContainer.getChildren().add(titleT);

				this.getChildren().add(upperContainer);
				this.getChildren().add(titleContainer);

				
			}
		}
	}
	
	public void createClassBtnFn(ActionEvent e) throws IOException {
		
		Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		
		Stage miniStage = new Stage();
		miniStage.initModality(Modality.APPLICATION_MODAL);
		miniStage.initOwner(mainStage); 
		
		VBox pane = new VBox();
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color: white;");
		
		
		
		pane.setPrefWidth(794);
		pane.setMinHeight(361);
		
		Label createClassT = new Label("Create Class");
		
		String createClassStyle = "-fx-font-size: 36; -fx-font-fill: #1C80BE";
		createClassT.setStyle(createClassStyle);
		pane.getChildren().add(createClassT);
		
		HBox classNameContainer = new HBox();
		
		classNameContainer.setMaxWidth(697);
		classNameContainer.setPrefHeight(50);
		classNameContainer.setAlignment(Pos.CENTER);
		
		Label classNameT = new Label("Class Name");
		TextField classNameTF = new TextField();
		
		classNameContainer.getChildren().addAll(classNameT, classNameTF);
		pane.getChildren().add(classNameContainer);
		
		HBox sectionContainer = new HBox();
		
		sectionContainer.setMaxWidth(697);
		sectionContainer.setPrefHeight(50);
		sectionContainer.setAlignment(Pos.CENTER);
		
		Label sectionT = new Label("Section");
		TextField sectionTF = new TextField();
		
		sectionContainer.getChildren().addAll(sectionT, sectionTF);
		pane.getChildren().add(sectionContainer);
		
		HBox subjectContainer = new HBox();
		
		subjectContainer.setMaxWidth(697);
		subjectContainer.setPrefHeight(50);
		subjectContainer.setAlignment(Pos.CENTER);
		
		Label subjectT = new Label("Subject");
		TextField subjectTF = new TextField();
		
		subjectContainer.getChildren().addAll(subjectT, subjectTF);
		pane.getChildren().add(subjectContainer);
		
		HBox roomContainer = new HBox();
		
		roomContainer.setMaxWidth(697);
		roomContainer.setPrefHeight(50);
		roomContainer.setAlignment(Pos.CENTER);
		
		Label roomT = new Label("Room");
		TextField roomTF = new TextField();
		
		roomContainer.getChildren().addAll(roomT, roomTF);
		pane.getChildren().add(roomContainer);
		
		Label errorT = new Label();
		pane.getChildren().add(errorT);
		
		Button done = new Button("done");
		done.setOnAction(f -> {
			
			checkRequiredFiles();
			
			String classCode = generateCode(), className = classNameTF.getText(), 
					section = sectionTF.getText(), subject = subjectTF.getText(),
					room = roomTF.getText();
			
			try {
				
				switch(checkList(new popUpVariable(classCode, className, section, subject, room))) {
				case classNameAlreadyAdded:
					errorT.setText("Class Name Already Added!");
					break;
				case classNameDuplicate:
					errorT.setText("Bro Someone got it already");
					break;
				case subjectAlreadyAdded:
					errorT.setText("You already have it bro the Subject");
					break;
				case subjectDuplicate:
					errorT.setText("Someone have it already better luck next time");
					break;
				case error:
					errorT.setText("How you even flopped it real bad bro");
					break;
				case pass:
					miniStage.close();
					getClasses();
					break;
				default:
					break;
				
					
				}
				
			} catch (Exception e1) { e1.printStackTrace(); }
			
			
			
		});
		
		pane.getChildren().add(done);
		
		Scene scene = new Scene(pane);
		miniStage.setScene(scene);
		
		miniStage.showAndWait();
		
		
		
	}
	
	private void checkRequiredFiles() {
		
		try {
		
			String applicationDirString = "application/classes/";
			File createUserDir = new File(applicationDirString);
			if(!createUserDir.exists() && !createUserDir.isDirectory())
				createUserDir.mkdirs();
			
			String applicationDBString = applicationDirString + "classes.db";
			File createUserDB = new File(applicationDBString);
			createUserDB.createNewFile();
			
			Connection applicationConnection = DriverManager.getConnection("jdbc:sqlite:" + applicationDBString);
			Statement applicationStatement = applicationConnection.createStatement();
			
			String createUserTableString = "create table if not exists classes("
					+ "id integer primary key autoincrement not null,"
					+ "`class code` text not null,"
					+ "`class name` text not null,"
					+ "section text not null,"
					+ "subject text not null,"
					+ "room text not null"
					+ ")";
			
			applicationStatement.execute(createUserTableString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String generateCode() {
		
		String classCode = "";
		
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		
		List<Map<String, Object>> classesList = new ArrayList<>();
		
		try {
			
			Connection classesConnection = DriverManager.getConnection("jdbc:sqlite:application/classes/classes.db");
			
			String getClassCodeString = "select `class code` from classes";
			Statement getClassCodeStatement = classesConnection.createStatement();
			
			classesList = Util.getList(getClassCodeStatement, getClassCodeString);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
		
		while(true) {
			
			Random random = new Random();
	        StringBuilder sb = new StringBuilder(5);
	        
	        boolean notDuplicate = false;
	        
	        for (int i = 0; i < 5; i++) {
	            int index = random.nextInt(CHARACTERS.length());
	            sb.append(CHARACTERS.charAt(index));
	        }
			
	        for(Map<String, Object> i : classesList) {
	        	
	        	if(i.get("class code").equals(sb.toString())) {
	        		System.out.println(i.get("class code") + ": " + sb.toString());
	        		notDuplicate = true;
	        		break;
	        	}
	        	else {
	        		
	        		classCode = sb.toString();
	        		notDuplicate = false;
	        		
	        	}
	        	
	        }
	        
	        if(list.isEmpty() || !notDuplicate) {
	        	
	        	classCode = sb.toString();
	        	break;
	        	
	        }
		
		}
		
		return classCode;
		
	}
	
	private class popUpVariable {
		
		public String classCode, className, section, subject, room;
		
		popUpVariable(String classCode, String className, String section, String subject, String room) {
			
			this.classCode = classCode;
			this.className = className;
			this.section = section;
			this.subject = subject;
			this.room = room;
			
		}
		
	}
	
	private Connection createRequiredTable() {
		
		try {
		
			String userDir = "application/user/teacher/" + user.currentUser + "/";
			String userDB = userDir + user.currentUser + ".db";
			
			File checkUserDir = new File(userDir);
			if(!checkUserDir.exists() && !checkUserDir.isDirectory())
				checkUserDir.mkdirs();
			
			File checkUserDB = new File(userDB);
			checkUserDB.createNewFile();
				
			Connection userConnection = DriverManager.getConnection("jdbc:sqlite:" + userDB);
			Statement userStatement = userConnection.createStatement();
			
			String createRequiredTable = "create table if not exists classes("
					+ "id integer primary key not null,"
					+ "`class name` text not null,"
					+ "section text not null,"
					+ "subject text not null,"
					+ "room text not null"
					+ ")";
			
			userStatement.execute(createRequiredTable);
			
			String createStudentList = "create table if not exists student("
					+ "id integer primary key not null,"
					+ "`student name` text not null"
					+ ")";
			userStatement.execute(createStudentList);
			
			return userConnection;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private Connection isClassDBExist() {
		
		try {
			
			String classesDirString = "application/classes/";
			File classesDirFile = new File(classesDirString);
			if(!classesDirFile.exists() && !classesDirFile.isDirectory())
				classesDirFile.mkdirs();
			
			String classesDBString = classesDirString + "classes.db";
			File classesDBFile = new File(classesDBString);
			classesDBFile.createNewFile();
			
			Connection classesConnection = DriverManager.getConnection("jdbc:sqlite:" + classesDBString);
			Statement classesStatement = classesConnection.createStatement();
			
			String createRequiredTableString = "create table if not exists classes("
					+ "id integer primary key autoincrement,"
					+ "`class code` text not null,"
					+ "`class name` text not null,"
					+ "section text not null,"
					+ "subject text not null,"
					+ "room text not null"
					+ ")";
			
			classesStatement.execute(createRequiredTableString);
			
			return classesConnection;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private infoStat checkList(popUpVariable info) throws Exception {
		
		Connection classesConnection = null, userConnection = null;
		
		classesConnection = isClassDBExist();
		userConnection = createRequiredTable();
		
		String getAllClassDataString = "select * from classes";
		Statement getAllClassDataStatement = classesConnection.createStatement();
		
		String getAllUserClassDataString = "select * from classes";
		Statement getAllUserClassDataStatement = userConnection.createStatement();
		
		List<Map<String, Object>> classList = Util.getList(getAllClassDataStatement, getAllClassDataString);
		List<Map<String, Object>> userList = Util.getList(getAllUserClassDataStatement, getAllUserClassDataString);
		
		switch(checkInfo(classList, userList, "class name", info.className)) {
		case pass:
			System.out.println("Class name passed!");
			break;
		case duplicate:
			return infoStat.classNameDuplicate;
		case alreadyAdded:
			return infoStat.classNameAlreadyAdded;
		default:
			return infoStat.error;
		}
		
		switch(checkInfo(classList, userList, "subject", info.subject)) {
		case pass:
			System.out.println("Subject passed!");
			break;
		case duplicate:
			return infoStat.subjectDuplicate;
		case alreadyAdded:
			return infoStat.subjectAlreadyAdded;
		default:
			return infoStat.error;
		}
		
		String insertClasses = "insert into classes (`class code`, `class name`, section, subject, room) values (?, ?, ?, ?, ?)";
		PreparedStatement prepareClasses = classesConnection.prepareStatement(insertClasses);
		
		prepareClasses.setString(1, info.classCode);
		prepareClasses.setString(2, info.className);
		prepareClasses.setString(3, info.section);
		prepareClasses.setString(4, info.subject);
		prepareClasses.setString(5, info.room);
		
		prepareClasses.executeUpdate();
		
		String insertUser = "insert into classes (`class name`) values (?)";
		PreparedStatement prepareUser = userConnection.prepareStatement(insertUser);
		
		prepareUser.setString(1, info.className);
		
		prepareUser.executeUpdate();
		
		String classNameDirString = "application/classes/" + info.className + "/";
		File classNameDirFile = new File(classNameDirString);
		if(!classNameDirFile.exists() && !classNameDirFile.isDirectory())
			classNameDirFile.mkdirs();
		
		String classNameDBString = classNameDirString + info.className + ".db";
		File classNameDBFile = new File(classNameDBString);
		classNameDBFile.createNewFile();
		
		Connection classNameConnection = DriverManager.getConnection("jdbc:sqlite:" + classNameDBString);
		Statement classNameStatement = classNameConnection.createStatement();
		
		String createStudentTable = "create table if not exists student("
				+ "id integer primary key autoincrement,"
				+ "`student name` text not null"
				+ ")";
		classNameStatement.execute(createStudentTable);
		
		String createRequiredClassTable = "create table if not exists classes("
				+ "id integer primary key autoincrement,"
				+ "announcement text not null,"
				+ "`announcement description` text,"
				+ "isQuiz integer not null"
				+ ")";
		classNameStatement.execute(createRequiredClassTable);
		
		System.out.println("Class Created: " + info.className);
		
		return infoStat.pass;
		
	}
	
	enum infoStat {
		pass,
		error, 
		
		alreadyAdded,
		duplicate,
		
		classNameDuplicate,
		classNameAlreadyAdded,
		
		subjectDuplicate,
		subjectAlreadyAdded
	}
	
	
	//checkInfo(classList, userList, "subject", info.subject)
	private infoStat checkInfo(List<Map<String, Object>> dataClassName, List<Map<String,Object>> currentUserClass, String type, String user) {
		
		for(Map<String, Object> i: currentUserClass) {
			
			if(user.equals(i.get(type)))
				return infoStat.alreadyAdded;
			
		}
		
		for(Map<String, Object> i: dataClassName) {
			
			if(user.equals(i.get(type)))
				return infoStat.duplicate;
			
		}
		
		return infoStat.pass;
		
	}
	
}
