package biojavatest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.biojava.bio.program.abi.ABIFChromatogram;
import org.biojava.bio.program.abi.ABIFParser;
import org.biojava.bio.program.abi.ABITrace;

public class test {

	public static void main(String[] args) {
		// TODO Change ABI File Path
		File myFile = new File("/Users/lovisheindrich/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
		System.out.println("File Access successful: " +  myFile.canRead());
		try {
			ABITrace myTrace = new ABITrace(myFile);
			ABIFParser myParser = new ABIFParser(myFile);
			ABIFChromatogram myChroma = new ABIFChromatogram();
			
			//The map has no particular order and so cannot be relied on to iterate over records in the same order they were read from the file.
			/*Map<String,ABIFParser.TaggedDataRecord> dataMap = myParser.getAllDataRecords();
			for (Map.Entry<String,ABIFParser.TaggedDataRecord> entry : dataMap.entrySet()) {
			    System.out.println(entry.getKey() + ", " + entry.getValue());
			}*/
			
			
			//System.out.println(myTrace.getSequenceLength());
			//SymbolList mySequence = myTrace.getSequence();
			//System.out.println(mySequence.seqString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
