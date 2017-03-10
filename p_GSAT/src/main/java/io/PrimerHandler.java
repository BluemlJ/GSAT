package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import analysis.Gene;
import analysis.Primer;
import exceptions.DuplicateGeneException;

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
			String comment = sepLine[5];

			primerList.add(new Primer(sequence, researcher, meltingPoint, id, name, comment));
		}

		primerReader.close();

	}

	public static void readPrimer() throws NumberFormatException, IOException {
		initPrimer();
		readPrimer(path);
	}

	public static void initPrimer() throws IOException {
		if (!exists()) {
			File primerFile = new File(path);
			primerFile.getParentFile().mkdirs();
			primerFile.createNewFile();

			BufferedWriter geneWriter = new BufferedWriter(new FileWriter(path));
			geneWriter.write("DummyPrimer" + SEPARATOR + "ATG" + SEPARATOR + "none" + SEPARATOR + "0" + SEPARATOR
					+ "none" + SEPARATOR + "none");
			geneWriter.close();

		}
	}

	/**
	 * 
	 * @param genePath
	 * @throws IOException
	 */
	public static void writePrimer(String primerPath) throws IOException {
		// clears all primers from file
		BufferedWriter primerWriter = new BufferedWriter(new FileWriter(primerPath));
		// write all previously known primers
		for (Primer primer : primerList) {
			// Format: name;sequence;researcher;meltingpoint;id
			StringBuilder primerString = new StringBuilder();
			primerString.append(primer.getName());
			primerString.append(SEPARATOR);
			if (primer.getSequence() == null || primer.getSequence().equals("")) {
				primerString.append("none");
			} else {
				primerString.append(primer.getSequence());
			}
			primerString.append(SEPARATOR);
			if (primer.getResearcher() == null || primer.getResearcher().equals("")) {
				primerString.append("none");
			} else {
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
			primerString.append(SEPARATOR);
			if (primer.getComment() == null || primer.getComment().equals("")) {
				primerString.append("none");
			} else {
				primerString.append(primer.getComment());
			}
			primerString.append(System.getProperty("line.separator"));
			primerWriter.write(primerString.toString());
		}

		primerWriter.close();
	}

	public static void writePrimer() throws IOException {
		writePrimer(path);
	}

	private static boolean exists() {
		File config = new File(path);
		return config.exists();
	}

	/**
	 * adds a primer to the list if it does not exist yet
	 * 
	 * @param primer
	 *            the primer to be added
	 */
	public static boolean addPrimer(Primer primer) {
		// only add if primer doesn´t exist yet
		try {
			readPrimer();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (getPrimer(primer.getName(), primer.getId()) == null) {
			primerList.add(primer);

			try {
				writePrimer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	/**
	 * deletes a primer from the primerList
	 * 
	 * @param name
	 * @param id
	 */
	public static void deletePrimer(String name, String id) {
		int pos = -1;
		Primer primer;
		for (int i = 0; i < primerList.size(); i++) {
			primer = primerList.get(i);
			if (primer.getName().equals(name) && primer.getId().equals(id)) {
				pos = i;
			}
		}
		if (pos != -1) {
			primerList.remove(pos);
		}
	}

	/**
	 * deletes a primer from the primerList
	 * 
	 * @param name
	 * @param id
	 */
	public static void deletePrimer(String name) {
		int pos = -1;
		Primer primer;
		for (int i = 0; i < primerList.size(); i++) {
			primer = primerList.get(i);
			if (primer.getName().equals(name)) {
				pos = i;
			}
		}
		if (pos != -1) {
			primerList.remove(pos);
		}
	}

	/**
	 * Returns a primer identified by name and id from the primerList
	 * 
	 * @param name
	 * @param primerId
	 * @return the primer identified by name and primerId or null
	 * @author Lovis Heindrich
	 */
	public static Primer getPrimer(String name, String primerId) {
		for (Primer primer : primerList) {
			if (primer.getName().equals(name) && primer.getId().equals(primerId)) {
				return primer;
			}
		}
		return null;
	}

	/**
	 * Returns a primer identified by name and id from the primerList
	 * 
	 * @param name
	 * @return the primer identified by name and primerId or null
	 * @author Lovis Heindrich
	 */
	public static Primer getPrimer(String name) {
		for (Primer primer : primerList) {
			if (primer.getName().equals(name)) {
				return primer;
			}
		}
		return null;
	}

	
	/**
     * clears the txt file at a given path
     * 
     * @param path
     * @throws IOException
     */
    public static void clearTxtFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write("");
        writer.close();
    }
	
	
	public static ArrayList<Primer> getPrimerList() {
		return primerList;
	}

	public static String[] getPrimerListAsString() {
		String[] ret = new String[primerList.size()];
		for (int i = 0; i < primerList.size(); i++) {
			ret[i] = primerList.get(i).getName();
		}

		return ret;
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
