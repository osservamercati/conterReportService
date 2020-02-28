package Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

@Controller
public class FtpConnection {

	String result = "";
	InputStream inputStream;
	
	private static Logger logger = LogManager.getLogger();
	
	public FTPClient Connection () throws IOException {
		
		Properties prop = new Inizialize().properties();
		FTPClient ftpClient = new FTPClient();
		try {
			logger.info("Start connection FTP ");
			logger.info("USERNAME : " + prop.getProperty("ftp.user") + " password : " + prop.getProperty("ftp.pass") + " server : " + prop.getProperty("ftp.server"));

			ftpClient.connect(prop.getProperty("ftp.server"), Integer.parseInt(prop.getProperty("ftp.port")));
			ftpClient.login(prop.getProperty("ftp.user"), prop.getProperty("ftp.pass"));
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		}catch (Exception e) {
			logger.error(e.toString());
		}
		return ftpClient;

	}
}
