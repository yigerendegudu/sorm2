package com.bjsxt.sorm.core;

public interface TypeConvertor {
	
	public String databaseType2JavaType(String columnType);
	
	public String javaType2DatabaseType(String javaDataType);
	
}
