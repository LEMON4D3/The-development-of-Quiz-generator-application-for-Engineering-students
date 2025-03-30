package Util;

import javafx.scene.paint.Paint;
import javafx.scene.paint.*;

public class Preferences {
	
	static public String mainBackground, headerBackground, borderColor;
	
	static void init(String main, String header, String border) {
		
		mainBackground = main;
		headerBackground = header;
		borderColor = border;
		
	}
	
}
