package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class init {

	public init() {
		
		controller.ct = new controller();
		
		Map<Integer, String> tableName = new HashMap<>();
		
		try {
		
			Connection connection = DriverManager.getConnection("jdbc:sqlite:user/table.db");
			
			SQLFn.statement = connection.createStatement();
			
			String createTable = "create table if not exists `table name` (" +
					"id int auto_increment primary key," +
					"name varchar(100) not null )";
			
			SQLFn.statement.execute(createTable);
			
			tableName = getTable();
			
			ResultSet pref = SQLFn.get(tableName.get(1));
			
			Preferences.init(pref.getString(2), pref.getString(3), pref.getString(4));
			
		} catch (Exception e) { e.printStackTrace(); }
		
		
		
	}
	
	
	Map<Integer, String> getTable() throws SQLException {
		
		ResultSet rs = SQLFn.statement.executeQuery("select * from `table name`");
		Map<Integer, String> tableName = new HashMap<>();
		
		while(rs.next()) {
			
			tableName.put(rs.getInt(1), rs.getString(2));
			
		}
		
		return tableName;
		
	}
}
