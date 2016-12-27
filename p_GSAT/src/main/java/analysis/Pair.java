package analysis;

/**
 * Helper class to store Pairs
 * 
 * @author Kevin
 *
 * @param <First>
 * @param <Second>
 */
public class Pair<First, Second> {

  public First first;
  public Second second;

  public Pair(First first, Second second) {
    this.first = first;
    this.second = second;
  }
}
