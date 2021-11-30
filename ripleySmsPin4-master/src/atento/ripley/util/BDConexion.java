package atento.ripley.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;

import atento.ripley.VariablesGlobales;

public class BDConexion {
	
	private static Connection cnnGD;
	
	public static void ConnectionGD(){
		if(isActive()){
			try {
				Class.forName(VariablesGlobales.DRIVER_SQLSERVER_GD);
				try {
					cnnGD= DriverManager.getConnection(getConnectionUrlGD(), VariablesGlobales.USERNAME_CNN_GD,VariablesGlobales.PASSWORD_CNN_GD);
					setCnnGD(cnnGD);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

	}
	
	private static String getConnectionUrlGD(){
		String cadenaConexion="";
		cadenaConexion="jdbc:sqlserver://"+VariablesGlobales.SERVER_PRINCIPAL_GD+";portNumber=1433;instanceName=./"+VariablesGlobales.SERVER_INSTANCE_GD+";databaseName="+VariablesGlobales.BD_PRINCIPAL_GD;
		System.out.println("Cadena Conexion: "+cadenaConexion);
		return cadenaConexion;
	}

	public static Connection getCnnGD() {
		return cnnGD;
	}

	public static void setCnnGD(Connection cnn) {
		BDConexion.cnnGD = cnn;
	}
	
	public static void putData(String tipo,String datos_in,String datos_out,String ip,String usuario){
		if(isActive()){
	    	try{
	    		PreparedStatement pstmt =  BDConexion.getCnnGD().prepareCall("{call [INSERT_BITACORA_RIPLEY] (?,?,?,?,?)}");
	    		pstmt.setString(1, tipo);
	    		pstmt.setString(2, datos_in);
	    		pstmt.setString(3, datos_out);
	    		pstmt.setString(4, ip);
	    		pstmt.setString(5, usuario);
		    	pstmt.execute();
	    	}catch(Exception e){
				e.printStackTrace();
			}
		}
    	
    }
	
	public static boolean isActive() {
		boolean property=false;
		try {
			if (VariablesGlobales.SERVER_LOG.equalsIgnoreCase("SI")) {
				property = true;
			} else {
				property = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return property;
	}
	
}
