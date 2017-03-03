package io;

import java.io.File;
import java.util.ArrayList;

import analysis.Primer;

public class PrimerHandler {
	private static ArrayList<Primer> primerList;
	private static String path = System.getProperty("user.home") + File.separator + "gsat" + File.separator + "genes.txt";
	  private static final String SEPARATOR = ConfigHandler.SEPARATOR_CHAR + "";
	
	  public static void readPrimer() {
			// TODO Auto-generated method stub
			
	}
	  
	public static ArrayList<Primer> getPrimerList() {
		return primerList;
	}
	public static void setPrimerList(ArrayList<Primer> primerList) {
		PrimerHandler.primerList = primerList;
	}
	public static String getPath() {
		return path;
	}
	public static void setPath(String path) {
		PrimerHandler.path = path;
	}

}
