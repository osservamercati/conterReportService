package com.LogMail.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LogManager.getLogger(DemoApplication.class);
	
	public static void main(String[] args) throws InterruptedException, ParseException {

		log.info("Start application for count report ");
		String body = "";
		try {
			String token = sendPost("https://connect.creditsafe.com/v1/authenticate");

			URL url = new URL("https://connect.creditsafe.com/v1/access/countries");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			conn.setRequestProperty("Authorization", token);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				body += "\n" + output;
			}
			conn.disconnect();

			if(sendMail("francesco.capparucci@gmail.com", "Uso rapporti Informativi CS", body))
			{
				log.info("Success");
			}else {
				log.error("Errore invio mess");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.toString());
		}
	}

	public static String sendPost(String urlp) throws InterruptedException, ParseException {
		try {
			String returnValue = "";
			log.info("Start send message");

			HashMap<String, String> valori = new HashMap<String, String>() {
				{
					put("username", "gabriele.cionchi@mafcoit.it");
					put("password", "UJwtFC5zy*JIjPY3v;YoS5|P");
				}
			};
			String jsonInputString = "{\"username\": \"gabriele.cionchi@mafcoit.it\", \"password\": \"UJwtFC5zy*JIjPY3v;YoS5|P\"}";

			URL url = new URL("https://connect.creditsafe.com/v1/authenticate");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			log.debug("username : " + valori.get("username") + " password : " + valori.get("password"));

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(valori);
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://connect.creditsafe.com/v1/authenticate"))
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.setHeader("Content-Type", "application/json").build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			Object obj = JSONValue.parse(response.body());
			JSONObject jsonObject = (JSONObject) obj;
			returnValue = (String) jsonObject.get("token");

			return returnValue;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.toString());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.toString());
			return null;
		}

	}

	public static boolean sendMail(String mailTo, String subject, String body) throws InterruptedException {
		try {

			HashMap<String, String> valori = new HashMap<String, String>() {
				{
					put("mailto", mailTo);
					put("subject", subject);
					put("body", body);
				}
			};
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(valori);
			HttpClient client = HttpClient.newHttpClient();
			System.out.println("mail pronta mail to = " + mailTo + " soggetto = " + subject + " body = " + body);
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8009/sendMail"))
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.setHeader("Content-Type", "application/json").build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(response.body());
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
