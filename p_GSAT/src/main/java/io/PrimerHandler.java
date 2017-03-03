package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import analysis.Primer;

public class PrimerHandler {
	private static ArrayList<Primer> primerList;
	private static String path = System.getProperty("user.home") + File.separator + "gsat" + File.separator
			+ "primer.txt";
	private static final String SEPARATOR = ConfigHandler.SEPARATOR_CHAR + "";

	public static void readPrimer(String primerPath) throws NumberFormatException, IOException {

		// open primer.txt or initialize it
		path = primerPath;
		initPrimer();

		// read all primer from the file
		primerList = new ArrayList<Primer>();
		BufferedReader primerReader = new BufferedReader(new FileReader(primerPath));
		String line;
		// for each line
		while ((line = primerReader.readLine()) != null) {
			// Format: name;sequence;researcher;meltingpoint;id
			String[] sepLine = line.split(SEPARATOR);
			String name = sepLine[0];
			String sequence = sepLine[1];
			String researcher = sepLine[2];
			int meltingPoint = Integer.parseInt(sepLine[3]);
			String id = sepLine[4];

			primerList.add(new Primer(sequence, researcher, meltingPoint, id, name));
		}

		primerReader.close();

	}

	private static void initPrimer() throws IOException {
		if (!exists()) {
			File primerFile = new File(path);
			primerFile.getParentFile().mkdirs();
			primerFile.createNewFile();

			BufferedWriter geneWriter = new BufferedWriter(new FileWriter(path));
			geneWriter.write(
					"DummyPrimer" + SEPARATOR + "ATG" + SEPARATOR + "none" + SEPARATOR + "0" + SEPARATOR + "none");
			geneWriter.close();

		}
	}

	//TODO
	public static void writeGenes(String genePath) throws IOException {
	    // clears all primers from file
	    BufferedWriter primerWriter = new BufferedWriter(new FileWriter(genePath));
	    // write all previously known primers
	    for (Primer primer : primerList) {
	    	// Format: name;sequence;researcher;meltingpoint;id
	    	StringBuilder primerString = new StringBuilder();
	      primerString.append(primer.getName());
	      primerString.append(SEPARATOR);    
	      if(primer.getSequence() == null || primer.getSequence().equals("")){
	    	  primerString.append("none");
	      }else{
	    	  primerString.append(primer.getSequence());
	      }
	      primerString.append(SEPARATOR);
	      if(primer.getResearcher() == null || primer.getResearcher().equals("")){
	    	  primerString.append("none");
	      }else{
	    	  primerString.append(primer.getResearcher());
	      }
	      primerString.append(SEPARATOR);
	      primerString.append(primer.getMeltingPoint());
	      primerString.append(SEPARATOR);
	      if (primer.getId() == null || primer.getId().equals("")) {
	        primerString.append("none");
	      } else {
	        primerString.append(primer.getId());
	      }
	      primerString.append(System.getProperty("line.separator"));
	      primerWriter.write(primerString.toString());
	    }
	    }

	private static boolean exists() {
		File config = new File(path);
		return config.exists();
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
