package com.bjsxt.sorm.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bjsxt.sorm.bean.ColumnInfo;
import com.bjsxt.sorm.bean.TableInfo;
import com.bjsxt.sorm.utils.JavaFileUtils;
import com.bjsxt.sorm.utils.StringUtils;

public class TableContext {
	public static  Map<String,TableInfo>  tables = new HashMap<String,TableInfo>();
	
	public static  Map<Class,TableInfo>  poClassTableMap = new HashMap<Class,TableInfo>();
	
	private TableContext(){}
	
	static {
		try {
			Connection con = DBManager.getConn();
			DatabaseMetaData dbmd = con.getMetaData(); 
			
			ResultSet tableRet = dbmd.getTables(null, "%","%",new String[]{"TABLE"}); 
			
			while(tableRet.next()){
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				
				TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>()
						,new HashMap<String, ColumnInfo>());
				tables.put(tableName, ti);
				
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");  
				while(set.next()){
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), 
							set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);  //鏌ヨt_user琛ㄤ腑鐨勪富閿�
				while(set2.next()){
					ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);  �
					ti.getPriKeys().add(ci2);
				}
				
				if(ti.getPriKeys().size()>0){ 
					ti.setOnlyPriKey(ti.getPriKeys().get(0));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		updateJavaPOFile();
		
		loadPOTables();
	}
	
	public static void updateJavaPOFile(){
		Map<String,TableInfo> map = TableContext.tables;
		for(TableInfo t:map.values()){
			JavaFileUtils.createJavaPOFile(t,new MySqlTypeConvertor());
		}	
	}
	
	/**
	 * 鍔犺浇po鍖呬笅闈㈢殑绫�
	 */
	public static void loadPOTables(){
		
		for(TableInfo tableInfo:tables.values()){
			try {
				Class c = Class.forName(DBManager.getConf().getPoPackage()
						+"."+StringUtils.firstChar2UpperCase(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		 Map<String,TableInfo>  tables = TableContext.tables;
		 System.out.println(tables);
	}

}
