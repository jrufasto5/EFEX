package SMS;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import atento.ripley.VariablesGlobales;
import atento.ripley.util.BDConexion;

public class SMSEnviarPin extends SMSDatos {

	private static Logger log = Logger.getLogger(SMSEnviarPin.class.getName());

	protected static String enviarPin(String ruta, String file, String token, String phoneNumber, String dni) {
		boolean exito=false;
		try {

			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = null;
			try {
				response = Unirest.post(VariablesGlobales.PIN_URL).header("Authorization", "Bearer " + token)
						.header("Content-Type", "application/json")
						.body("{  \r\n  \"AdditionalInformationList\": {\r\n    \"AdditionalInformation\": [\r\n      {\r\n        \"Key\": \"program_id\",\r\n        \"Value\": \""+VariablesGlobales.PROGRAM_ID+"\"\r\n      }\r\n    ]\r\n  },\r\n   \"PersonIdentification\":{  \r\n      \"TypeOfIdentification\":\"C\",\r\n      \"IdentityCardNumber\":\""+dni+"\"\r\n   },\r\n   \"Authentication\":{  \r\n      \"AuthenticationMethod\":\"TOTP\"\r\n   },\r\n   \"PhoneAddress\":{  \r\n      \"PhoneNumber\":\""
								+ phoneNumber + "\"\r\n   },\r\n   \"ElectronicAddress\":{  \r\n      \"EmailAddress\":\"\"\r\n   },\r\n   \"ActionType\":{  \r\n      \"TypeName\":\"sms\"\r\n   }\r\n}\r\n")
						. asString();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
			if (response.getStatus() == 200) {
				generaPlanoIdLlamada(ruta, file, "EXITO," + "Message sent successfully", "out");
				exito=true;
			}else{
				generaPlanoIdLlamada(ruta, file, "ERROR," + "Incorrect response", "out");
				exito=false;
			}
		} catch (Exception e1) {
			exito=false;
			generaPlanoIdLlamada(ruta, file, "ERROR," + "ENVIAR SMS FALLIDO", "out");
			log.error("Error SMSEnviarPin.enviarPin");
			log.error(e1);
		}finally{
			try {
				if(exito){
					BDConexion.putData("SMS ENVIA PIN", ruta+"-"+file+"-"+token+"-"+phoneNumber, String.valueOf(exito)+"-"+phoneNumber, Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}else{
					BDConexion.putData("SMS ENVIA PIN", ruta+"-"+file+"-"+token+"-"+phoneNumber, String.valueOf(exito)+"-Revisar el log de la transaccion", Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}
			} catch (UnknownHostException e) {
				log.error(e);
				e.printStackTrace();
			}
		}

		return null;
	}

	public SMSEnviarPin() {
		super();
	}

}