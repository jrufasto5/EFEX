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
import atento.ripley.util.FileUtil;

public class SMSToken extends FileUtil {

	private static Logger log = Logger.getLogger(SMSToken.class.getName());

	protected static String token(String ruta, String file, String scope) {
		boolean exito=false;
		String token = "";
		try {

			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = null;
			response = Unirest.post(VariablesGlobales.TOKEN_URL)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.field("client_id", VariablesGlobales.client_id)
					.field("client_secret", VariablesGlobales.client_secret)
					.field("grant_type", VariablesGlobales.grant_type).field("username", VariablesGlobales.username)
					.field("password", VariablesGlobales.password).field("scope", scope).asString();
			String jsonText = response.getBody();
			JSONObject json = new JSONObject(jsonText);
			token = json.getString("access_token");
			generaPlanoIdLlamada(ruta, file, "EXITO," + token, "out");
			exito=true;
		} catch (Exception e1) {
			exito=false;
			generaPlanoIdLlamada(ruta, file, "ERROR," + "GENERAR TOKEN FALLIDO", "out");
			log.error("Error SMSToken.token");
			log.error(e1);
		}finally{
			try {
				if(exito){
					BDConexion.putData("SMS TOKEN", ruta+"-"+file+"-"+VariablesGlobales.client_id+"-"+VariablesGlobales.client_secret+"-"+VariablesGlobales.username, String.valueOf(exito)+"-"+token, Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}else{
					BDConexion.putData("SMS TOKEN", ruta+"-"+file+"-"+VariablesGlobales.client_id+"-"+VariablesGlobales.client_secret+"-"+VariablesGlobales.username, String.valueOf(exito)+"-"+"Revisar el log de la transaccion", Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				log.error(e);

			}
		}

		return null;
	}

	public SMSToken() {
		super();
	}

}