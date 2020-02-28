package Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Inizialize {
	
	public Properties properties() throws IOException {
	
		InputStream inputStream;
		Properties prop = new Properties();	
		String propFileName = "application.properties";
		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		return prop;

	}
	
	
}
