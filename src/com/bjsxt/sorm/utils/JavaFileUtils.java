package com.bjsxt.sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bjsxt.sorm.bean.ColumnInfo;
import com.bjsxt.sorm.bean.JavaFieldGetSet;
import com.bjsxt.sorm.bean.TableInfo;
import com.bjsxt.sorm.core.DBManager;
import com.bjsxt.sorm.core.MySqlTypeConvertor;
import com.bjsxt.sorm.core.TableContext;
import com.bjsxt.sorm.core.TypeConvertor;

public class JavaFileUtils {
	
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column,TypeConvertor convertor){
		JavaFieldGetSet jfgs  = new JavaFieldGetSet();
		
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		//public String getUsername(){return username;}
		//鐢熸垚get鏂规硶鐨勬簮浠ｇ爜
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+";\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		//public void setUsername(String username){this.username=username;}
		//鐢熸垚set鏂规硶鐨勬簮浠ｇ爜
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"(");
		setSrc.append(javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		return jfgs;
	}
	
	public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		
		Map<String,ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<JavaFieldGetSet>();

		for(ColumnInfo c:columns.values()){
			javaFields.add(createFieldGetSetSRC(c,convertor));
		}
		
		StringBuilder src = new StringBuilder();
		
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		src.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
		
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getFieldInfo());
		}
		src.append("\n\n");
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getGetInfo());
		}
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getSetInfo());
		}
		
		src.append("}\n");
		return src.toString();
	}
	
	
	public static void createJavaPOFile(TableInfo tableInfo,TypeConvertor convertor){
		String src = createJavaSrc(tableInfo,convertor);
		
		String srcPath = DBManager.getConf().getSrcPath()+"\\";
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "/");
		
		File f = new File(srcPath+packagePath);
		
		if(!f.exists()){ 
			f.mkdirs();
		}
		
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java"));
			bw.write(src);
			System.out.println(""+tableInfo.getTname()+
					""+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw!=null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("id", "int", 0);
//		JavaFieldGetSet f = createFieldGetSetSRC(ci,new MySqlTypeConvertor());
//		System.out.println(f);
		
		Map<String,TableInfo> map = TableContext.tables;
		for(TableInfo t:map.values()){
			createJavaPOFile(t,new MySqlTypeConvertor());
		}
	}
	
}
