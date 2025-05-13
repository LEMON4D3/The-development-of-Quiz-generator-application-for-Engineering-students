package homepage.lib;

import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class emptyContainer extends VBox {
	
	List<String> title;
	
	public Button joinBtn = new Button("+ Join Button");
	
	public emptyContainer(List<String> Title) {
		
		title = Title;
		
		this.getStyleClass().add("newVBox");
		this.setPadding(new Insets(40, 0, 0, 0));
		
		
		this.getChildren().add(new topPane());
		
		this.getChildren().add(new scrollPane());
		
	}
	
	
	private class topPane extends GridPane {
		
		topPane() {
			
			int colSpace[] = {10, 193, 363, 161, 72, 15};
			
			for(int i: colSpace) {
				
				ColumnConstraints temp = new ColumnConstraints();
				temp.setPrefWidth(i);
				
				this.getColumnConstraints().add(temp);
				
			}
			
			Label label = new Label("My Classes");
			label.setMinHeight(75);
			label.setPrefWidth(225);
			label.getStyleClass().add("newVBox-label");
			label.setAlignment(Pos.CENTER);
			
			this.add(label, 1, 0);
			
			joinBtn.getStyleClass().add("newVBox-btn");
			
			this.add(joinBtn, 3, 0);
			
			Image image = new Image(getClass().getResource("/homepage/rsc/user.png").toExternalForm());
			ImageView userImg = new ImageView(image);
			userImg.setFitWidth(67);
			userImg.setFitHeight(67);
			
			this.add(userImg, 4, 0);
			
		}
		
	}
	
	private class scrollPane extends ScrollPane {
		
		scrollPane() {
			
			
			
			this.setPadding(new Insets(0, 0, 0, 10));
			this.setPrefHeight(600);
			
			this.setStyle("-fx-background: rgba(0, 0, 0, 0); -fx-background-color: rgba(0, 0, 0, 0);");
			this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			this.setContent(new bodyPane());
			
		}
		
	}
	
	private class bodyPane extends GridPane {
		
		bodyPane() {
			
			this.setPadding(new Insets(10, 0, 10, 0));
			
//			for(int x = 0, y = 0, i = 0; i < title.size(); x++, i++) {
//				
//				itemCard label = new itemCard(i);
//				GridPane.setHalignment(label, HPos.CENTER); // Horizontal center
//				GridPane.setValignment(label, VPos.CENTER);
//				
//				if(x == 3) {
//					
//					x = 0;
//					y++;
//					
//					this.add(label, x, y);
//					
//				} else this.add(label, x, y);
//				
//			}
			
			this.setHgap(25);
			this.setVgap(25);


			
			for (int i = 0; i < 3; i++) {
			    ColumnConstraints cc = new ColumnConstraints();
			    cc.setHgrow(Priority.ALWAYS);
			    cc.setFillWidth(true);
			    cc.setPercentWidth(100.0 / 3);
			    this.getColumnConstraints().add(cc);
			}
			
			
		}
		
		
			
		}
		
	}
	



