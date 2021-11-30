package atento.ripley.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import atento.ripley.VariablesGlobales;

public class StringUtils {

	Properties properties = new Properties();

	public void cargarPropertiesCnn(String tipo) {
		InputStream appProperties = getClass().getResourceAsStream("/ripleyws.properties");

		try {
			properties.load(appProperties);

			VariablesGlobales.CIFRADO = properties.getProperty("cnn.ripley.CIFRADO");
			VariablesGlobales.CIFRADO_FRASE = properties.getProperty("cnn.ripley.CIFRADO_FRASE");
			VariablesGlobales.SERVER_PRINCIPAL_GD = properties.getProperty("cnn.sqlserver.serverGD");
			VariablesGlobales.SERVER_INSTANCE_GD = properties.getProperty("cnn.sqlserver.instanceGD");
			
			VariablesGlobales.SERVER_LOG = properties.getProperty("cnn.sqlserver.serverLOG");
			VariablesGlobales.BD_PRINCIPAL_GD = properties.getProperty("cnn.sqlserver.baseGD");
			VariablesGlobales.DRIVER_SQLSERVER_GD = properties.getProperty("cnn.sqlserver.driverGD");

			VariablesGlobales.USERNAME_CNN_GD = properties.getProperty("cnn.sqlserver.usernameGD");
			VariablesGlobales.PASSWORD_CNN_GD = properties.getProperty("cnn.sqlserver.passwordGD");
			if (tipo.equalsIgnoreCase("1")) {
				setVariablesGlobalesToken();
			}
			if (tipo.equalsIgnoreCase("2")) {
				setVariablesGlobalesDatos();
			}
			if (tipo.equalsIgnoreCase("3")) {
				setVariablesGlobalesPin();
			}
			if (tipo.equalsIgnoreCase("4")) {
				setVariablesGlobalesValidar();
			}
			if (tipo.equalsIgnoreCase("5")) {
				setVariablesGlobalesPin4();
			}
			if (tipo.equalsIgnoreCase("6")) {
				setVariablesGlobalesToken();
				setVariablesGlobalesDatos();
				setVariablesGlobalesPin();
				setVariablesGlobalesValidar();
				setVariablesGlobalesPin4();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			appProperties.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setVariablesGlobalesToken() {
		VariablesGlobales.TOKEN_URL = getProperty("cnn.ripley.TOKEN_URL");
		VariablesGlobales.client_id = getProperty("cnn.ripley.client_id");
		VariablesGlobales.client_secret = getProperty("cnn.ripley.client_secret");
		VariablesGlobales.grant_type = getProperty("cnn.ripley.grant_type");
		VariablesGlobales.username = getProperty("cnn.ripley.username");
		VariablesGlobales.password = getProperty("cnn.ripley.password");
	}

	private void setVariablesGlobalesDatos() {
		VariablesGlobales.DATOS_URL = getProperty("cnn.ripley.DATOS_URL");
	}

	private void setVariablesGlobalesPin() {
		VariablesGlobales.PIN_URL = getProperty("cnn.ripley.PIN_URL");
		VariablesGlobales.PROGRAM_ID = getProperty("cnn.ripley.program_id");
		
	}

	private void setVariablesGlobalesValidar() {
		VariablesGlobales.VALIDAR_URL = getProperty("cnn.ripley.VALIDAR_URL");
	}

	private void setVariablesGlobalesPin4() {
		VariablesGlobales.PIN4_URL = getProperty("cnn.ripley.PIN4_URL");
		VariablesGlobales.CERT = getProperty("cnn.ripley.CERT");
	}

	public String getProperty(String property) {
		try {
			if (VariablesGlobales.CIFRADO.equalsIgnoreCase("SI")) {
				property = CifradoUtil.decrypt(properties.getProperty(property));
			} else {
				property = properties.getProperty(property);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return property;
	}
}
