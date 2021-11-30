package SMS;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import atento.ripley.VariablesGlobales;
import atento.ripley.util.BDConexion;

public class SMSValidarPin extends SMSEnviarPin {
	private static Logger log = Logger.getLogger(SMSValidarPin.class.getName());

	protected static String validarPin(String ruta, String file, String token, String pin, String dni) {
		boolean exito=false;
		boolean resultado=false;
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = null;
			response = Unirest.post(VariablesGlobales.VALIDAR_URL).header("Authorization", "Bearer " + token)
					.header("Content-Type", "application/json")
					.body("{  \r\n   \"PersonIdentification\":{  \r\n      \"TypeOfIdentification\":\"C\",\r\n      \"IdentityCardNumber\":\""+dni+"\"\r\n   },\r\n   \"Authentication\":{  \r\n      \"AuthenticationMethod\":\"TOTP\",\r\n      \"PIN\":\""
							+ pin + "\"\r\n   }\r\n}\r\n")
					.asString();
			if (response.getStatus() == 200) {
				String jsonText = response.getBody();
				JSONObject json = new JSONObject(jsonText);
				resultado = json.getJSONObject("Authentication").getBoolean("AuthenticationResult");
				generaPlanoIdLlamada(ruta, file, "EXITO," + new Boolean(resultado).toString(), "out");
				exito=true;
			} else {
				generaPlanoIdLlamada(ruta, file, "ERROR," + response.getStatus() + "-" + response.getBody(), "out");
				exito=false;
			}
		} catch (Exception e1) {
			exito=false;
			generaPlanoIdLlamada(ruta, file, "ERROR," + "VALIDAR PIN SMS FALLIDO", "out");
			log.info("Error PIN4ValidarPin.validarPin");
			log.error(e1);
		}finally{
			try {
				if(exito){
					BDConexion.putData("SMS DATOS", ruta+"-"+file+"-"+token+"-"+pin, String.valueOf(exito)+"-"+String.valueOf(resultado), Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}else{
					BDConexion.putData("SMS DATOS", ruta+"-"+file+"-"+token+"-"+pin, String.valueOf(exito)+"-"+"Revisar el log de la transaccion", Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				log.error(e);

			}
		}

		return null;
	}

	public SMSValidarPin() {
		super();
	}

}