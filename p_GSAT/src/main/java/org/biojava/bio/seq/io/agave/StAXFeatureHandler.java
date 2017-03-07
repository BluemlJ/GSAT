/**
 * 
 * BioJava development code
 *
 * 
 * 
 * This code may be freely distributed and modified under the
 * 
 * terms of the GNU Lesser General Public Licence. This should
 * 
 * be distributed with the code. If you do not have a copy,
 * 
 * see:
 *
 * 
 * 
 * http://www.gnu.org/copyleft/lesser.html
 *
 * 
 * 
 * Copyright for this code is held jointly by the individual
 * 
 * authors. These should be listed in @author doc comments.
 *
 * 
 * 
 * For more information on the BioJava project and its aims,
 * 
 * or to join the biojava-l mailing list, visit the home page
 * 
 * at:
 *
 * 
 * 
 * http://www.biojava.org/
 *
 * 
 * 
 */

package org.biojava.bio.seq.io.agave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.SimpleAnnotation;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.StrandedFeature;
import org.biojava.bio.seq.io.ParseException;
import org.biojava.bio.seq.io.SeqIOListener;
import org.biojava.bio.symbol.Location;
import org.biojava.utils.ChangeVetoException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;



/**
 * 
 * StAX handler shamelessly ripped off from Thomas Down's
 * 
 * XFFFeatureSetHandler. It was modified for greater
 * 
 * generality.
 *
 * 
 * 
 * <strong>NOTE</strong> This class is not thread-safe -- it
 * 
 * must only be used for one parse at any time.
 * 
 * <p>
 * 
 * Because AGAVE has nested feature and each feature must attached to
 * 
 * some parent feature-holder in biojava, which means we need to generate parents first,
 * 
 * so we have to keep the tree structure of features in memory, which is really bad.
 * 
 * anyway, we still saved a lot of memory compared with DOM tree . Hanning Ni)
 * 
 * 
 * 
 * @author copied from Thomas Down
 * 
 * @author copied from David Huen
 * 
 * @author Hanning Ni Doubletwist Inc
 *
 * 
 * 
 */

public class StAXFeatureHandler extends StAXContentHandlerBase

{

  // class variables

  private boolean setOnceFired = false;

  protected String myLocalName;

  private boolean hasCallback = false;

  private List handlers;

  private boolean startFired = false;

  private boolean endFired = false;

  private boolean inFeature = false; // have we been here before?



  // class variables for use by nested elements

  protected Feature.Template featureTemplate;

  protected StAXFeatureHandler staxenv;

  protected SeqIOListener featureListener;

  protected int startLoc;

  protected int endLoc; // needed because some XML don't give start and stops as attributes.

  /**
   * 
   * this is the stack of handler objects for the current feature.
   * 
   * The base value is the FeatureHandler itself.
   * 
   * your feature and property handlers place and remove themselves
   * 
   * from this stack.
   *
   * 
   * 
   * the purpose of all this is to implement context sensitivty for
   * 
   * property handlers translucently. Property handlers can pop the
   * 
   * stack for other handlers that implement interfaces that process
   * 
   * that element. This way the context code is within the object that
   * 
   * defines that context rather than in a child property handler.
   * 
   */

  protected List callbackStack;

  protected int stackLevel;



  // store all the sub features, should be a tree structure with the root is

  // top level seqeuence

  protected List subFeatures;



  protected SimpleAnnotation annot = new SimpleAnnotation();



  StAXFeatureHandler(StAXFeatureHandler staxenv) {

    // cache environmnet

    this.staxenv = staxenv;

    handlers = new ArrayList();

    callbackStack = new ArrayList();

    subFeatures = new ArrayList(1);

  }



  StAXFeatureHandler() {

    handlers = new ArrayList();

    callbackStack = new ArrayList();

    subFeatures = new ArrayList(1);

  }



  // there should be a factory method here to make this class



  /**
   * 
   * Sets the element name that the class responds to.
   * 
   */

  public void setHandlerCharacteristics(String localName, boolean hasCallback) {

    if (!setOnceFired) {

      myLocalName = localName;

      this.hasCallback = hasCallback;

      setOnceFired = true;

    }

  }



  public void setFeatureListener(SeqIOListener siol) {

    featureListener = siol;

  }



  // Class to implement bindings

  class Binding {

    final ElementRecognizer recognizer;

    final StAXHandlerFactory handlerFactory;

    Binding(ElementRecognizer er, StAXHandlerFactory hf)

    {

      recognizer = er;

      handlerFactory = hf;

    }

  }



  // method to add a handler

  // we do not distinguish whither it is a feature or property

  // handler. The factory method creates the right type subclassed

  // from the correct type of handler

  protected void addHandler(

      ElementRecognizer rec,

      StAXHandlerFactory handler)

  {

    handlers.add(new Binding(rec, handler));

  }



  /**
   * 
   * generates a very basic Template for the feature with
   * 
   * SmallAnnotation in the annotation field.
   * 
   * <p>
   * 
   * Override if you wish a more specialised Template.
   * 
   */

  protected Feature.Template createTemplate() {

    // create Gene Template for this

    StrandedFeature.Template st = new StrandedFeature.Template();



    // assume feature set to describe a transcript

    st.source = myLocalName;

    st.type = myLocalName;

    st.strand = StrandedFeature.UNKNOWN;

    // set up annotation bundle

    st.annotation = annot;

    st.location = Location.empty;

    if (staxenv != null)

      staxenv.subFeatures.add(this);



    return st;

  }



  /**
   * 
   * recursively attach children features to parent
   * 
   */

  protected void realizeSubFeatures(Feature feature)

  {

    try {

      Iterator it = subFeatures.iterator();

      while (it.hasNext())

      {

        StAXFeatureHandler handler = (StAXFeatureHandler) it.next();

        if (handler instanceof SequenceHandler) // already handled

          continue;

        Feature f = feature.createFeature(handler.featureTemplate);

        handler.realizeSubFeatures(f);

      }

    } catch (BioException e) {
      e.printStackTrace();
    }

    catch (ChangeVetoException e) {
      e.printStackTrace();
    }

  }



  protected void addFeatureToSequence(Sequence seq) throws Exception

  {

    Iterator it = subFeatures.iterator();

    while (it.hasNext())

    {

      StAXFeatureHandler handler = (StAXFeatureHandler) it.next();

      Feature f = seq.createFeature(handler.featureTemplate);

      handler.realizeSubFeatures(f);

    }

  }

  /**
   * 
   * return current stack level. Remember that the
   * 
   * stack level is incremented/decremented AFTER
   * 
   * the push()/pop() calls and superclass
   * 
   * startElement()/StopElement calls.
   * 
   */

  protected int getLevel() {

    return stackLevel;

  }



  /**
   * 
   * return iterator to callbackStack
   * 
   */

  protected ListIterator getHandlerStackIterator(int level) {

    return callbackStack.listIterator(level);

  }



  /**
   * 
   * Push StAXContentHandler object onto stack
   * 
   */

  protected void push(StAXContentHandler handler) {

    // push handler

    callbackStack.add(handler);



    // increment pointer

    stackLevel++;

  }



  /**
   * 
   * pop a StAXContentHandler off the stack.
   * 
   */

  protected void pop() {

    // decrement pointer

    stackLevel--;



    // pop handler

    callbackStack.remove(stackLevel);

  }



  /**
   * 
   * Return current feature listener
   * 
   */

  public SeqIOListener getFeatureListener() {

    return featureListener;

  }



  /**
   * 
   * Fire the startFeature event.
   * 
   */

  private void fireStartFeature()

      throws ParseException

  {

    if (startFired)

      throw new ParseException("startFeature event has already been fired");



    if (featureTemplate == null)

      featureTemplate = createTemplate();



    if (featureTemplate.annotation == null)

      featureTemplate.annotation = Annotation.EMPTY_ANNOTATION;



    featureListener.startFeature(featureTemplate);

    startFired = true;

  }



  /**
   * 
   * Fire the endFeature event.
   * 
   */

  private void fireEndFeature()

      throws ParseException

  {

    if (!startFired)

      throw new ParseException("startFeature has not yet been fired!");



    if (endFired)

      throw new ParseException("endFeature event has already been fired!");

    featureListener.endFeature();

    endFired = true;

  }



  protected void setProperty(String name, String value, boolean forFeature)

  {

    if (value != null) {

      try {

        annot.setProperty(name, value);

        if (forFeature)

          featureListener.addFeatureProperty(name, value);

        else

          featureListener.addSequenceProperty(name, value);

      }

      catch (ChangeVetoException cae) {

        System.err.println(" veto exception caught.");

      }

      catch (ParseException cae) {

        System.err.println("parse exception in addProperty() .");

      }

    }

  }

  /**
   * 
   * Element-specific handler.
   * 
   * Subclass this to do something useful!
   * 
   */

  public void startElementHandler(

      String nsURI,

      String localName,

      String qName,

      Attributes attrs)

      throws SAXException

  {

  }



  /**
   * 
   * Handles basic entry processing for all feature handlers.
   * 
   */

  public void startElement(

      String nsURI,

      String localName,

      String qName,

      Attributes attrs,

      DelegationManager dm)

      throws SAXException

  {

    // sanity check

    if (!setOnceFired)

      throw new SAXException("StAXFeaturehandler not initialised before use!");



    // find out if this element is really for me.

    // perform delegation

    // if (!(myLocalName.equals(localName)) ) {

    if (dm.getRecursive())

      for (int i = handlers.size() - 1; i >= 0; --i) {

      Binding b = (Binding) handlers.get(i);

      if (b.recognizer.filterStartElement(nsURI, localName, qName, attrs)) {

      dm.delegate(b.handlerFactory.getHandler(this));

      return;

      }

      }

    // }

    // I don't have a delegate for it but it might be a stray...

    if (!(myLocalName.equals(localName))) {

      // this one's not for me!

      return;

    }



    // this one's mine.

    // initialise if this is the first time thru'

    if (!inFeature) {

      inFeature = true;



      if (hasCallback) {

        if (stackLevel == 0) push(this);

      }



      // indicate start of feature to listener

      try {

        if (!startFired) fireStartFeature();

      }

      catch (ParseException pe) {

        throw new SAXException("ParseException thrown in user code");

      }

    }



    // call the element specific handler now.

    startElementHandler(nsURI, localName, qName, attrs);



  }



  /**
   * 
   * Element specific exit handler
   * 
   * Subclass to do anything useful.
   * 
   */

  public void endElementHandler(

      String nsURI,

      String localName,

      String qName,

      StAXContentHandler handler)

      throws SAXException

  {

  }



  /**
   * 
   * Handles basic exit processing.
   * 
   */

  public void endElement(

      String nsURI,

      String localName,

      String qName,

      StAXContentHandler handler)

      throws SAXException

  {

    // is this a return after delegation or really mine?

    if (!(myLocalName.equals(localName)))

      return;



    // last chance to clear up before exiting

    // endElementHandler(nsURI, localName, qName, handler);



    // it's mine, get prepared to leave.

    if (hasCallback) pop();



    // is it time to go beddy-byes?

    if (stackLevel == 0) {

      // we need to cope with zero-sized elements

      try {

        if (!startFired) fireStartFeature();

        if (!endFired) fireEndFeature();

      }

      catch (ParseException pe) {

        throw new SAXException("ParseException thrown in user code");

      }

    }

    endElementHandler(nsURI, localName, qName, handler);

  }

}

