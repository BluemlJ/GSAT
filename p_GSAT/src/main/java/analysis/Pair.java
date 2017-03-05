package analysis;

/**
 * This is a helper class to store pairs. In some cases, it's useful to connect two values by
 * constructing such a Pair.
 * 
 * @author Kevin Otto
 *
 * @param <A> The type of the first element
 * @param <B> The type of the second element
 */
public class Pair<A, B> {


  /**
   * The first element.
   */
  public A first;



  /**
   * The second element.
   */
  public B second;



  /**
   * The constructor sets the two fields first and second.
   * 
   * @param first The first element of this pair
   * @param second The second element of this pair
   */
  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }
}
