package atento.ripley.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtil {
	private static Logger log = Logger.getLogger(FileUtil.class.getName());

	public static void generaPlanoIdLlamada(String rutaArchivo, String nombreArchivo, String resultadoWs, String extensionArchivo) {
		FileWriter fichero = null;
		log.info("Comienza generaPlanoIdLlamada");
		try {
			File d = new File(rutaArchivo, "");
			if (!d.exists()) {
				d.mkdirs();
			}
	
			File f = null;
			f = new File(d.getPath(), String.valueOf(nombreArchivo) +"."+ extensionArchivo);
			if (f.exists()) {
				f.delete();
			}
			fichero = new FileWriter(f.getPath(), true);
			fichero.write(resultadoWs);
			fichero.close();
			log.info("Finaliza generaPlanoIdLlamada");
		} catch (Exception ex) {
			log.info("Error generaPlanoIdLlamada");
			log.error(ex);
			try {
				fichero.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	public FileUtil() {
		super();
	}

}