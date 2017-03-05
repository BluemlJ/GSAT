package analysis;

/**
 * This class models a primer
 * 
 * @author lovisheindrich
 *
 */
public class Primer extends Sequence {

  private int meltingPoint;
  private String id;
  private String name;
  private String comment;

  public Primer(String sequence, String researcher, int meltingPoint, String id, String name, String comment) {
    super(sequence, researcher);

    this.meltingPoint = meltingPoint;
    this.id = id;
    this.name = name;
    this.comment = comment;

  }

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
