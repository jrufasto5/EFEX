package atento.ripley;

import org.apache.log4j.Logger;

import PIN4.PIN4ValidarPin;
import atento.ripley.util.BDConexion;
import atento.ripley.util.CifradoUtil;
import atento.ripley.util.StringUtils;

public class NetClientPostGet extends PIN4ValidarPin {
	private static Logger log = Logger.getLogger(NetClientPostGet.class.getName());

	public static void main(String[] args) {

		try {
			String tipo = null;
			String ruta = null;
			String archivo = null;
			String param1 = null;
			String param2 = null;
			String param3 = null;
			String param4 = null;
			String param5 = null;

			try {
				args = args[0].split(",");
				tipo = args[0];
				ruta = args[1];
				archivo = args[2];
				param1 = args[3];
				param2 = args[4];
				param3 = args[5];
				try {
					param4 = args[6];
					param5 = args[7];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info(tipo+" - "+ruta+" - "+archivo);
				log.debug(args[0]);
			} catch (Exception e) {
				log.info("Errro cargando parametros");
				log.error(e);
				e.printStackTrace();
			}

			NetClientPostGet nc = new NetClientPostGet();

			(new StringUtils()).cargarPropertiesCnn(tipo);

		    BDConexion.ConnectionGD();
			
			if (tipo.equalsIgnoreCase("1")) {
				String str = token(ruta, archivo, param1);
			}
			if (tipo.equalsIgnoreCase("2")) {
				String str = datos(ruta, archivo, param1, param2, param3);
			}
			if (tipo.equalsIgnoreCase("3")) {
				String str = enviarPin(ruta, archivo, param1, param2, param3);
			}
			if (tipo.equalsIgnoreCase("4")) {
				String str = validarPin(ruta, archivo, param1, param2, param3);
			}
			if (tipo.equalsIgnoreCase("5")) {
				String str = validaClave(ruta, archivo, param1, param2, param3, param4, param5);
			}
			if (tipo.equalsIgnoreCase("6")) {
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.TOKEN_URL));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.client_id));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.client_secret));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.grant_type));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.username));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.password));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.DATOS_URL));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.PIN_URL));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.VALIDAR_URL));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.PIN4_URL));
				System.out.println(CifradoUtil.encrypt(VariablesGlobales.CERT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
