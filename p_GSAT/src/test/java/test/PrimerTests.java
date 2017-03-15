package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import analysis.Primer;
import io.PrimerHandler;

public class PrimerTests {


  @Test
  public void testAddDeletePrimer() throws IOException {
    PrimerHandler.clearTxtFile();
    Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
    Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment1");
    // test duplicate check
    PrimerHandler.setPrimerList(new ArrayList<Primer>());
    PrimerHandler.addPrimer(p1);
    assertEquals(PrimerHandler.getPrimerList().size(), 1);
    PrimerHandler.addPrimer(p1);
    PrimerHandler.addPrimer(p2);
    assertEquals(PrimerHandler.getPrimerList().size(), 2);
    // test get
    Primer p3 = PrimerHandler.getPrimer("primer1", "A01");
    Primer p4 = PrimerHandler.getPrimer("primer2", "B01");
    assertEquals(p1.getSequence(), p3.getSequence());
    assertEquals(p2.getSequence(), p4.getSequence());
    // test delete
    PrimerHandler.deletePrimer("primer1 (A01)");
    p3 = PrimerHandler.getPrimer("primer1", "A01");
    assertEquals(p3, null);

  }


	@Test
	public void testPrimerWrite() throws IOException {
		PrimerHandler.initPrimer();
		Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1", "comment1");
		Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2", "comment1");
		ArrayList<Primer> primerList = new ArrayList<Primer>();
		primerList.add(p1);
		primerList.add(p2);
		PrimerHandler.setPrimerList(primerList);
		PrimerHandler.writePrimer();

		PrimerHandler.setPrimerList(null);
		PrimerHandler.readPrimer();
		assertEquals(PrimerHandler.getPrimerList().get(0).getName(), "primer1");
		assertEquals(PrimerHandler.getPrimerList().get(1).getName(), "primer2");
	}
}
