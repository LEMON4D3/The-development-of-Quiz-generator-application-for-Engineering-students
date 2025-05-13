package util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Init {
	
	List<Map<String, Object>> user = new ArrayList();
	
	public Init() throws SQLException { checkRequiredFiles(); }	
	
	public void checkRequiredFiles() throws SQLException {
		
		for(Map<String, Object> i: user) {
			
			String url = "application/user/";
			
			if((int) i.get("isTeacher") == 1)
				url += "teacher/";
			else
				url += "student/";
			
			url += (String) i.get("username") + "/";
			
			System.out.println(url);
			
			File file = new File(url);
			
			if(!file.exists())
				file.mkdirs();
			
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + url + (String) i.get("username") + ".db");
			
			String sql = "create table if not exists classes ("
					+ "id integer primary key AUTOINCREMENT,"
					+ "`class name`"
					+ ")";
			
			Statement statement = connection.createStatement();
			statement.execute(sql);
			
		}
		
	}
	
	private void toList(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		
		while(rs.next()) {
			
			Map<String, Object> temp = new HashMap<>();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				temp.put(rsmd.getColumnName(i), rs.getObject(i));
				
			}
			
			user.add(temp);
			
		}
		
		for(Map<String, Object> i : user) 
			System.out.println(i);
		
	}
	
}
