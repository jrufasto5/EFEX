package PIN4;

import java.io.StringReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import Ripley.IVRPinBlock;
import SMS.SMSValidarPin;
import atento.ripley.VariablesGlobales;
import atento.ripley.util.BDConexion;

import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Set;

public class PIN4ValidarPin extends SMSValidarPin {
	private static Logger log = Logger.getLogger(PIN4ValidarPin.class.getName());

	protected static String validaClave(String ruta, String file, String sesion, String tipodoc, String nrodoc, String sNroTarjeta,
			String sClave) {
			boolean exito=false;
			String responseCode = null;
			String validarPin = null;
		try {

			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = null;
			try {
				String llavePublica = null;
				String keySessionId = null;
				try {
					response = Unirest.post(VariablesGlobales.PIN4_URL+"IVR.asmx")
							.header("Content-Type", "text/xml")
							//.header( "SOAPAction", strReferenceToSoapActionValue );
							.body("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"><soapenv:Header/><soapenv:Body><tem:GetKeysSixSecurity><tem:SESSIONID>"+
									sesion
									+"</tem:SESSIONID></tem:GetKeysSixSecurity></soapenv:Body></soapenv:Envelope>")
							.asString();

					String xml = response.getBody();
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse(new InputSource(new StringReader(xml)));
					NodeList errNodes = doc.getElementsByTagName("GetKeysSixSecurityResult");
					if (errNodes.getLength() > 0) {
						Element err = (Element) errNodes.item(0);
						llavePublica = err.getElementsByTagName("KEYPUBCHANNEL").item(0).getTextContent();
						keySessionId = err.getElementsByTagName("KEYSESSIONID").item(0).getTextContent();

					} else {
						// success
					}
				} catch (Exception e) {
					exito=false;
					generaPlanoIdLlamada(ruta, file, "ERROR," + "SEGURIDAD PIN4 FALLIDO", "out");
					log.error("Error PIN4ValidarPin.validaClave");
					log.error(e);
				}

				IVRPinBlock oPinBlock = new IVRPinBlock();
				String pinBlock = oPinBlock.generatorPB(sClave, sNroTarjeta, llavePublica);
				System.out.println("pinBlock: " + pinBlock);

				HttpResponse<String> responsePin = Unirest.post(VariablesGlobales.PIN4_URL+"IVR.asmx")
						.header("Content-Type", "text/xml")
						.body("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"><soapenv:Header/><soapenv:Body><tem:Validar_Clave4>"
								+ "<tem:SESSIONID>"+sesion+"</tem:SESSIONID>"
								+ "<tem:KEYSESSIONID>"+keySessionId+"</tem:KEYSESSIONID>"
								+ "<tem:NRO_TARJETA>"+sNroTarjeta+"</tem:NRO_TARJETA>"
								+ "<tem:PINBLOCK>"+pinBlock+"</tem:PINBLOCK>"
								+ "<tem:TIPO_DOCUMENTO>"+tipodoc+"</tem:TIPO_DOCUMENTO>"
								+ "<tem:NRO_DOCUMENTO>"+nrodoc+"</tem:NRO_DOCUMENTO>"
								+ "</tem:Validar_Clave4></soapenv:Body></soapenv:Envelope>")
						.asString();

				String xml = responsePin.getBody();
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(xml)));
				NodeList errNodes = doc.getElementsByTagName("Validar_Clave4Result");
				if (errNodes.getLength() > 0) {
					Element err = (Element) errNodes.item(0);
					responseCode = err.getElementsByTagName("COD_RPTA").item(0).getTextContent();
					validarPin = err.getElementsByTagName("VALIDAR_PIN").item(0).getTextContent();
				} else {
					// success
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			generaPlanoIdLlamada(ruta, file, "EXITO," + responseCode+","+validarPin, "out");
			 exito=true;
		} catch (Exception e1) {
			 exito=false;
			generaPlanoIdLlamada(ruta, file, "ERROR," + "VALIDAR CLAVE PIN4 FALLIDO", "out");
			log.error("Error PIN4ValidarPin.validaClave");
			log.error(e1);
		}finally{
			try {
				if(exito){
					BDConexion.putData("PIN4", ruta+"-"+file+"-"+nrodoc+"-"+sesion+"-"+sNroTarjeta, String.valueOf(exito)+"-"+String.valueOf(responseCode), Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}else{
					BDConexion.putData("PIN4", ruta+"-"+file+"-"+nrodoc+"-"+sesion+"-"+sNroTarjeta, String.valueOf(exito)+"-"+"Revisar el log de la transaccion", Inet4Address.getLocalHost().getHostAddress(), "IVR");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				log.error(e);

			}
		}

		return null;

	}

	static String getWSRipleyValidar_Documento(String url, String RequestFinal) {
		DefaultHttpClient defaultHttpClient = null;
		HttpClient httpClient = null;
		String result = null;
		try {
			String strRequest = RequestFinal;
			System.out.println("Request: " + strRequest);
			defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
			HttpPost postRequest = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
			postRequest.setConfig(requestConfig);
			StringEntity input = new StringEntity(strRequest);
			input.setContentType("application/soap+xml");
			postRequest.setEntity((HttpEntity) input);
			org.apache.http.HttpResponse response = defaultHttpClient.execute((HttpUriRequest) postRequest);
			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException("Error : Cde error HTTP : " + response.getStatusLine().getStatusCode());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document XMLDoc = factory.newDocumentBuilder().parse(response.getEntity().getContent());
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr1 = xpath.compile("//NRO_TARJETA");
			String strNro_Tarjeta = ((String) String.class.cast(expr1.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strNro_Tarjeta.length() == 0)
				strNro_Tarjeta = " ";
			System.out.println("NRO_TARJETA: " + strNro_Tarjeta);
			XPathExpression expr2 = xpath.compile("//NRO_CONTRATO");
			String strNro_Contrato = ((String) String.class.cast(expr2.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strNro_Contrato.length() == 0)
				strNro_Contrato = " ";
			System.out.println("NRO_CONTRATO: " + strNro_Contrato);
			XPathExpression expr3 = xpath.compile("//TIPO_DOC");
			String strTip_Doc = ((String) String.class.cast(expr3.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strTip_Doc.length() == 0)
				strTip_Doc = " ";
			System.out.println("TIPO_DOC: " + strTip_Doc);
			XPathExpression expr4 = xpath.compile("//NRO_DOC");
			String strNroDoc = ((String) String.class.cast(expr4.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strNroDoc.length() == 0)
				strNroDoc = " ";
			System.out.println("NRO_DOC: " + strNroDoc);
			XPathExpression expr5 = xpath.compile("//NOM_CLIENTE");
			String strNomCliente = ((String) String.class.cast(expr5.evaluate(XMLDoc, XPathConstants.STRING))).trim()
					.replace(",", "").replaceAll(" +", " ");
			if (strNomCliente.length() == 0)
				strNomCliente = " ";
			System.out.println("NOM_CLIENTE: " + strNomCliente);
			XPathExpression expr6 = xpath.compile("//TITULAR");
			String strTitular = ((String) String.class.cast(expr6.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strTitular.length() == 0)
				strTitular = " ";
			System.out.println("TITULAR: " + strTitular);
			XPathExpression expr7 = xpath.compile("//FECHA_NAC");
			String strFechaNac = ((String) String.class.cast(expr7.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strFechaNac.length() == 0)
				strFechaNac = " ";
			System.out.println("FECHA_NAC: " + strFechaNac);
			XPathExpression expr8 = xpath.compile("//DIRECCION");
			String strDireccion = ((String) String.class.cast(expr8.evaluate(XMLDoc, XPathConstants.STRING))).trim()
					.replace(",", "").replaceAll(" +", " ");
			if (strDireccion.length() == 0)
				strDireccion = " ";
			System.out.println("DIRECCION: " + strDireccion);
			XPathExpression expr9 = xpath.compile("//TELEFONO_CEL");
			String strTeleCel = ((String) String.class.cast(expr9.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strTeleCel.length() == 0)
				strTeleCel = " ";
			System.out.println("TELEFONO_CEL: " + strTeleCel);
			XPathExpression expr10 = xpath.compile("//TELEFONO_FIJO");
			String strTelFijo = ((String) String.class.cast(expr10.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strTelFijo.length() == 0)
				strTelFijo = " ";
			System.out.println("TELEFONO_FIJO: " + strTelFijo);
			XPathExpression expr11 = xpath.compile("//CORREO");
			String strCorreo = ((String) String.class.cast(expr11.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strCorreo.length() == 0)
				strCorreo = " ";
			System.out.println("CORREO: " + strCorreo);
			XPathExpression expr12 = xpath.compile("//COD_RPTA");
			String strCodRpta = ((String) String.class.cast(expr12.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strCodRpta.length() == 0)
				strCodRpta = " ";
			System.out.println("COD_RPTA: " + strCodRpta);
			XPathExpression expr13 = xpath.compile("//MSJ_RPTA");
			String strMsjRpta = ((String) String.class.cast(expr13.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strMsjRpta.length() == 0)
				strMsjRpta = " ";
			System.out.println("MSJ_RPTA: " + strMsjRpta);
			XPathExpression expr14 = xpath.compile("//SESION");
			String strSesion = ((String) String.class.cast(expr14.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strSesion.length() == 0)
				strSesion = " ";
			System.out.println("SESION: " + strSesion);
			XPathExpression expr15 = xpath.compile("//TRACE");
			String strTrace = ((String) String.class.cast(expr15.evaluate(XMLDoc, XPathConstants.STRING))).trim();
			if (strTrace.length() == 0)
				strTrace = " ";
			System.out.println("TRACE: " + strTrace);
			if (strCodRpta.equals("00")) {
				result = "00," + strNro_Tarjeta + "," + strNro_Contrato + "," + strTip_Doc + "," + strNroDoc + ","
						+ strNomCliente + "," + strTitular + "," + strFechaNac + "," + strDireccion + "," + strTeleCel
						+ "," + strTelFijo + "," + strCorreo + "," + strCodRpta + "," + strMsjRpta + "," + strSesion
						+ "," + strTrace;
			} else if (strCodRpta.equals("01")) {
				result = "01,ERROR DE SISTEMA";
			} else if (strCodRpta.equals("02")) {
				result = "02,VALIDACION DE PARAMETROS";
			} else if (strCodRpta.equals("03")) {
				result = "03,NUMERO DE DOCUMENTO NO ENCONTRADO";
			}
			result = String.valueOf(strCodRpta) + ",NO EXISTE CODIGO DE RPTA EN LAS ESPECIFICACIONES";
		} catch (Exception e) {
			e.printStackTrace();
			result = "Resultado= " + e.getMessage();
		} finally {
		}
		if (defaultHttpClient != null)
			defaultHttpClient.getConnectionManager().shutdown();
		return result;
	}

	public PIN4ValidarPin() {
		super();
	}

}