package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Test;

import analysis.AnalysedSequence;
import exceptions.DuplicateGeneException;
import io.FileRetriever;
import io.GeneHandler;

public class CSVReadingTests {

	public static String path = "resources" + File.separator + "readingtests" + File.separator;

	@After
	public void setup() throws IOException {
		GeneHandler.deleteGene("FSA1");
	}

	@Test
	public void testReadingCSVTwoFiles() throws IOException, DuplicateGeneException {
		GeneHandler.addGene("FSA1", "AATAAT", "ecoli", "comment");
		LinkedList<AnalysedSequence> result = FileRetriever.convertFilesToSequences(path + "two");

		result.sort((s1, s2) -> {
			return s1.getFileName().compareTo(s2.getFileName());
		});

		// there are only three sequences
		assertTrue(result.size() == 3);

		// first sequence

		AnalysedSequence test = result.pop();

		assertEquals("x.ab1", test.getFileName());
		assertEquals("FSA1", test.getReferencedGene().getName());

		assertEquals("A4R (CGC)", test.getMutations().getFirst());
		assertTrue(test.getMutations().size() == 1);

		assertEquals("comment", test.getComments());
		assertEquals("testresearcher", test.getResearcher());
		assertEquals("04/03/17", test.getAddingDate());
		assertEquals("3", test.getAvgQuality() + "");
		assertEquals(22, (int) test.getTrimPercentage());
		assertEquals("ATC", test.getSequence());
		assertEquals("none", test.getPrimer());
		assertEquals(-1, test.getHisTagPosition());
		assertEquals(true, test.isManuallyChecked());

		// second sequence

		test = result.pop();

		assertEquals("y.ab1", test.getFileName());
		assertEquals("FSA1", test.getReferencedGene().getName());

		assertEquals("R6F (AAA)", test.getMutations().getFirst());
		assertTrue(test.getMutations().size() == 1);

		assertEquals("comment", test.getComments());
		assertEquals("-", test.getResearcher());
		assertEquals("02/03/17", test.getAddingDate());
		assertEquals("99", test.getAvgQuality() + "");
		assertEquals(55, (int) test.getTrimPercentage());
		assertEquals("ATCTACTACTATACG", test.getSequence());
		assertEquals("none", test.getPrimer());
		assertEquals(3, test.getHisTagPosition());
		assertEquals(false, test.isManuallyChecked());

		// third sequence

		test = result.pop();

		assertEquals("z.ab1", test.getFileName());
		assertEquals("FSA1", test.getReferencedGene().getName());

		assertEquals("F6R (ACT)", test.getMutations().getFirst());
		assertTrue(test.getMutations().size() == 1);

		assertEquals("c", test.getComments());
		assertEquals("-", test.getResearcher());
		assertEquals("02/03/17", test.getAddingDate());
		assertEquals("99", test.getAvgQuality() + "");
		assertEquals(49, (int) test.getTrimPercentage());
		assertEquals("AATATC", test.getSequence());
		assertEquals("none", test.getPrimer());
		assertEquals(-1, test.getHisTagPosition());
		assertEquals(true, test.isManuallyChecked());

	}

	@Test
	public void testReadingCSVOneFile() throws IOException, DuplicateGeneException {
		GeneHandler.addGene("FSA1", "AATAAT", "ecoli", "comment");
		LinkedList<AnalysedSequence> result = FileRetriever.convertFilesToSequences(path + "one");

		result.sort((s1, s2) -> {
			return s1.getFileName().compareTo(s2.getFileName());
		});

		// there are only two sequences
		assertTrue(result.size() == 2);

		// first sequence

		AnalysedSequence test = result.pop();

		assertEquals("test1.ab1", test.getFileName());
		System.out.println(test.getReversedSequence());
		assertEquals("FSA1", test.getReferencedGene().getName());

		assertTrue(test.getMutations().size() == 3);
		assertEquals("K9L (CAA)", test.getMutations().pop());
		assertEquals("D9L (CAA)", test.getMutations().pop());
		assertEquals("W9L (CAA)", test.getMutations().pop());

		assertEquals("l", test.getComments());
		assertEquals("test", test.getResearcher());
		assertEquals("06/03/17", test.getAddingDate());
		assertEquals("44", test.getAvgQuality() + "");
		assertEquals(3, (int) test.getTrimPercentage());
		assertEquals("AAAAAAAAAAAAAAAAA", test.getSequence());
		assertEquals("none", test.getPrimer());
		assertEquals(4, test.getHisTagPosition());
		assertEquals(true, test.isManuallyChecked());

		// second sequence

		test = result.pop();

		assertEquals("test2.ab1", test.getFileName());
		assertEquals("FSA1", test.getReferencedGene().getName());

		assertTrue(test.getMutations().size() == 2);
		assertEquals("K9J (CCA)", test.getMutations().pop());
		assertEquals("G5H (AGG)", test.getMutations().pop());

		assertEquals("d", test.getComments());
		assertEquals("test2", test.getResearcher());
		assertEquals("06/03/17", test.getAddingDate());
		assertEquals("33", test.getAvgQuality() + "");
		assertEquals(4, (int) test.getTrimPercentage());
		assertEquals("TTCCTTCTCCTCC", test.getSequence());
		assertEquals("none", test.getPrimer());
		assertEquals(-1, test.getHisTagPosition());
		assertEquals(true, test.isManuallyChecked());

	}

	@Test
	public void testEmptyFolder() throws IOException {

		String emptyPath = path + "empty/test.txt";

		File file = new File(emptyPath);
		file.delete();
		LinkedList<AnalysedSequence> result = FileRetriever.convertFilesToSequences(emptyPath);

		assertTrue(result.size() == 0);

		file.createNewFile();

	}

}
