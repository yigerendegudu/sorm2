package com.bjsxt.sorm.core;

import java.util.List;

@SuppressWarnings("all")
public interface Query {
	public int executeDML(String sql,Object[] params);
	
	public void insert(Object obj);
	
	public void delete(Class clazz,Object id);   // delete from User where id=2;
	public void delete(Object obj);
	
	public int update(Object obj,String[] fieldNames);  //update user set uname=?,pwd=?
	
	public List queryRows(String sql,Class clazz,Object[] params);
	public Object queryUniqueRow(String sql,Class clazz,Object[] params);
	
	public Object queryValue(String sql,Object[] params);
	
	public Number queryNumber(String sql,Object[] params);
	
	
}
