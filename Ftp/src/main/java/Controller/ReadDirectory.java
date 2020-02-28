package Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadDirectory {
	
	private static Logger logger = LogManager.getLogger();
	
	public List<File> readDir(String path) {
		
		try {
			return Files.walk(Paths.get(path)).filter(Files::isRegularFile).map(Path::toFile)
			.collect(Collectors.toList());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return null;
	}

}
