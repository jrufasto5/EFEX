package SMS;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import atento.ripley.VariablesGlobales;
import atento.ripley.util.BDConexion;

public class SMSDatos extends SMSToken {

	private static Logger log = Logger.getLogger(SMSDatos.class.getName());

	protected static String datos(String ruta, String file, String token, String tipo, String nroDoc) {
		boolean exito=false;
		String phoneNumber = "";
		try {

			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = null;
			response = Unirest.post(VariablesGlobales.DATOS_URL).header("Authorization", "Bearer " + token)
					.header("Content-Type", "application/json")
					.body("{\"Security\":{\"UsernameToken\":{\"Username\":\"KSN-U100Q\",\"Password\":\"Bc0R8nS2019\",\"Nonce\":\"QmMwUjhuUzIwMTk=\",\"Created\":\"2019-01-02T17:31:22.961Z\"}},\"HeaderRequest\":{\"request\":{\"serviceId\":\"1\",\"consumerId\":\"1\",\"moduleId\":\"1\",\"channelCode\":\"1\",\"messageId\":\"1\",\"timestamp\":\"1\",\"countryCode\":\"1\",\"groupMember\":\"1\",\"referenceNumber\":\"1\"},\"identity\":{\"netId\":\"1\",\"userId\":\"1\",\"supervisorId\":\"1\",\"deviceId\":\"1\",\"serverId\":\"1\",\"branchCode\":\"1\"}},\"Body\":{\"RetrieveCustomerReferenceDataDirectoryEntryByDocumentId\":{\"BankingTransaction\":{\"TransactionIdentifier\":\"123456\"},\"System\":{\"OperatingSession\":{\"CommunicationChannel\":{\"Description\":\"Canal\"}}},\"Customer\":{\"RepresentativeOfCustomer\":{\"Person\":{\"PersonIdentification\":{\"TypeOfIdentification\":\""
							+ tipo + "\",\"IdentityCardNumber\":\"" + nroDoc + "\"}}}}}}}")
					.asString();
			String jsonText = response.getBody();
			JSONObject json = new JSONObject(jsonText);
			json = json.getJSONObject("Body");
			json = json.getJSONObject("RetrieveCustomerReferenceDataDirectoryEntryByDocumentIdResponse");
			json = json.getJSONObject("CustomerDetail");
			JSONArray jsonNodeType = json.getJSONArray("Detail");
			JSONObject jsonType = jsonNodeType.getJSONObject(0);
			String phoneType = jsonType.getJSONObject("Customer").getJSONObject("RepresentativeOfCustomer")
					.getJSONObject("Person").getString("ContactType");
			
			if (phoneType.equalsIgnoreCase("1")) {
				phoneNumber = jsonType.getJSONArray("PhoneAddress").getJSONObject(0).getString("MobileNumber");
			}

			generaPlanoIdLlamada(ruta, file, "EXITO," + phoneNumber, "out");
			exito=true;
		} catch (Exception e1) {
			exito=false;
			generaPlanoIdLlamada(ruta, file, "ERROR,OBTENER DATOS FALLIDO", "out");
			log.error("Error SMSDatos.datos");
			log.error(e1);
		}finally{
			try {
				if(exito){
					BDConexion.putData("SMS DATOS", ruta+"-"+file+"-"+token+"-"+tipo+"-"+nroDoc, String.valueOf(exito)+"-"+phoneNumber, Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}else{
					BDConexion.putData("SMS DATOS", ruta+"-"+file+"-"+token+"-"+tipo+"-"+nroDoc, String.valueOf(exito)+"Revisar el log de la transaccion", Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}
			} catch (UnknownHostException e) {
				log.error(e);
				e.printStackTrace();
			}
		}

		return null;
	}

	public SMSDatos() {
		super();
	}

}