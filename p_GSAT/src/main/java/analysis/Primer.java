package analysis;

import java.util.Date;

/**
 * This class models a primer.
 * 
 * @author lovisheindrich
 *
 */
public class Primer extends Sequence {

  /**
   * Melting point of the primer in degree.
   */
  private int meltingPoint;

  /**
   * Unique id of the primer.
   */
  private String id;

  /**
   * Name of the primer.
   */
  private String name;

  /**
   * User comment for the primer.
   */
  private String comment;

  /**
   * Constructor for a primer which sets all relevant parameters. Uses the current time as the
   * adding date.
   * 
   * @param sequence The sequence string of the primer.
   * @param researcher The researcher who added the primer.
   * @param meltingPoint The melting point of the primer.
   * @param id The unique id of the primer.
   * @param name The name of the primer.
   * @param comment The user comment for the primer.
   * @author Lovis Heindrich
   */
  public Primer(String sequence, String researcher, int meltingPoint, String id, String name,
      String comment) {

    super(sequence, researcher);

    this.meltingPoint = meltingPoint;
    this.id = id;
    this.name = name;
    this.comment = comment;

  }

  /**
   * Constructor for a primer which sets all relevant parameters including the date.
   * 
   * @param sequence The sequence string of the primer.
   * @param researcher The researcher who added the primer.
   * @param meltingPoint The melting point of the primer.
   * @param id The unique id of the primer.
   * @param name The name of the primer.
   * @param comment The user comment for the primer.
   * @param date The adding date of the primer.
   * @author Lovis Heindrich
   */
  public Primer(String sequence, String researcher, int meltingPoint, String id, String name,
      String comment, Date date) {
    super(sequence, researcher, date);

    this.meltingPoint = meltingPoint;
    this.id = id;
    this.name = name;
    this.comment = comment;

  }

  // GETTER UND SETTER

  public int getMeltingPoint() {
    return meltingPoint;
  }

  public void setMeltingPoint(int meltingPoint) {
    this.meltingPoint = meltingPoint;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

}
