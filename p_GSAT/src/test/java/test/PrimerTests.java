package test;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import analysis.Primer;
import io.PrimerHandler;

public class PrimerTests {

  @Ignore
  @Test
  public void testPrimerRead() throws IOException {
    PrimerHandler.initPrimer();
    PrimerHandler.readPrimer();
  }

  @Ignore
  @Test
  public void testPrimerWrite() throws IOException {
    PrimerHandler.initPrimer();
    Primer p1 = new Primer("AATAATAAT", "Lovis Heindrich", 50, "A01", "primer1");
    Primer p2 = new Primer("TTATTATTA", "Kevin Otto", 100, "B01", "primer2");
    ArrayList<Primer> primerList = new ArrayList<Primer>();
    primerList.add(p1);
    primerList.add(p2);
    PrimerHandler.setPrimerList(primerList);
    PrimerHandler.writePrimer();
  }
}
