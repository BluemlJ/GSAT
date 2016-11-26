import java.io.File;
import java.io.IOException;

import org.biojava.bio.program.abi.ABIFChromatogram;
import org.biojava.bio.program.abi.ABIFParser;
import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

public class test {

	public static void main(String[] args) {
		// TODO Change ABI File Path
		File myFile = new File("D:/Dokumente/Dropbox/BP_GSAT/Materialien/Dateien/Bsp/AB/93GH02_A01.ab1");
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
			SymbolList mySequence = myTrace.getSequence();
			//ABITools.getAlignment(mySequence);
			//PhredSequence myPhred = new PhredSequence(mySequence, mySequence.;
			//System.out.println(mySequence.seqString());
			int[] cTrace = myTrace.getTrace(DNATools.c());
			for(int i = 0; i < cTrace.length; i++){
				//System.out.println(cTrace[i]);
			}
			int[] basecalls = myTrace.getBasecalls();
			for(int i = 0; i < basecalls.length; i++){
				System.out.println(basecalls[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalSymbolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
