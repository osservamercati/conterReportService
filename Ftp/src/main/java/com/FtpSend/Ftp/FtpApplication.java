package com.FtpSend.Ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import Controller.FtpConnection;
import Controller.Inizialize;
import Controller.ReadDirectory;

@SpringBootApplication

public class FtpApplication {

	static private String path = "C:\\Users\\Administrator\\Desktop\\MYSQL\\bkp";
	private static Logger logger = LogManager.getLogger();

	public static void main(String[] args) throws InterruptedException, IOException {
		SpringApplication.run(FtpApplication.class, args);
		String body = "Ivio file : "+" \n";
		FTPClient ftpClient = new FtpConnection().Connection();
		try {
			logger.info("Start End Ftp Mysql ");
			List<File> filesInFolder = new ReadDirectory().readDir(path);
			logger.info("directory file : " + path + " numero file : " + filesInFolder.size());

			for (File file : filesInFolder) {
				logger.info("Start uploading file " + file.getName());
				FileInputStream in = new FileInputStream(file);
				ftpClient.storeFile(file.getName(), in);
				body = body+file.getName()+" \n";
			}
		} catch (IOException ex) {
			logger.error("Error: " + ex.getMessage());
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				logger.error(ex.toString());
			}
			if (sendMail("", "upload Su Ftp Mof", body)) {
				logger.info("Success");
			} else {
				logger.error("Errore invio mess");
			}
			logger.info("End Ftp Mysql");
		}
	}

	public static boolean sendMail(String mailTo, String subject, String body) throws InterruptedException {

		try {
			Properties prop = new Inizialize().properties();

			HashMap<String, String> valori = new HashMap<String, String>() {
				{
					put("mailto", prop.getProperty("mail.to"));
					put("subject", subject);
					put("body", body);
				}
			};
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(valori);
			HttpClient client = HttpClient.newHttpClient();
			logger.info("mail pronta mail to = " + mailTo + " soggetto = " + subject + " body = " + body);
			logger.debug("request body : " + requestBody);
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://63.33.245.62:8009/sendMail"))
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.setHeader("Content-Type", "application/json").build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			logger.info(response.body());
			return true;
		} catch (MalformedURLException e) {
			logger.error(e.toString());
			return false;
		} catch (IOException e) {
			logger.error(e.toString());
			return false;
		}
	}
}
