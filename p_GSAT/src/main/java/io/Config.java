package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;

/**
 * This class is responsible for reading the configuration file and storing its
 * values
 * 
 * @author lovisheindrich
 *
 */
public class Config {
	public static String researcher;
	public static String path;

	/**
	 * sets the path of the configuration file
	 * 
	 * @param path
	 */
	public static void setPath(String path) {
		Config.path = path;
	}

	/**
	 * read the content of the configuration file and store its values locally
	 * 
	 * @throws IOException
	 * @throws ConfigReadException
	 * @throws ConfigNotFoundException 
	 */
	public static void readConfig() throws ConfigReadException, ConfigNotFoundException, IOException {
		BufferedReader configReader;
		try {
			configReader = new BufferedReader(new FileReader(Config.path));
		} catch (FileNotFoundException e) {
			throw new ConfigNotFoundException(path);
		}
		String researcherLine = configReader.readLine();
		if (researcherLine.substring(0, 11).equals("researcher:") == false) {
			throw new ConfigReadException("researcher");
		}
		Config.researcher = researcherLine.substring(11).trim();
		configReader.close();
	}

}
