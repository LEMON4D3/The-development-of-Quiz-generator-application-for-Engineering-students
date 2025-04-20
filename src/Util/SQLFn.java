package Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLFn {

	static Statement statement;
	
	public static void update(String table, String columnName[], String newData[] , String id) throws SQLException {
		
		// update `table` set `rowName` = `newData` where id = `id`
		// update `User Preference` set `background` = `newData` where id = `id`
		
		String updateFn = "update `" + table + "` set ";
		
		ResultSet rs = get(table);
		
		while(rs.next())
			System.out.println(rs.getInt(1) + rs.getString(2) + rs.getString(3) + rs.getString(4));
		
		for(int i = 0; i < newData.length - 1; i++) {
			
			updateFn = updateFn + "'" + columnName[i] + "'='" + newData[i] + "', ";
			
		}
		
		updateFn = updateFn + "'" + columnName[columnName.length - 1] + "'='" + newData[newData.length - 1] + "' ";
		
		updateFn = updateFn + "where id = '" + id + "'";
		
		System.out.println(updateFn);
		statement.executeUpdate(updateFn);
		
		new init();
		
	}
	
	public static void insert(String table, String newData[]) throws SQLException {
		
		// insert into `table` values (`newData[0]`, `newData[1]`, `newData[2]`, `newData[3]`, ...)
		
		String insertSQL = "insert into `" + table + "` values (";
		
		for(int i = 0; i < newData.length; i++) {
			
			String temp = "`newData[" + i + "]`, ";
			
			insertSQL = insertSQL + temp;
		}
		
		insertSQL = insertSQL + ")";
		
		statement.executeUpdate(insertSQL);
		
	}
	
	public static ResultSet get(String table) throws SQLException {
		
		
		ResultSet result = statement.executeQuery("select * from `" + table + "`");
		
		return result;
		
	}
}
