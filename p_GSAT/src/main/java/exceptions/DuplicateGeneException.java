package exceptions;

/**
 * This exception is thrown when the user tries to add a gene which already exists in our database
 * @author Lovis
 *
 */
public class DuplicateGeneException extends Exception{
   private String name;
   
   public DuplicateGeneException(String name){
     super("Gene " + name + " already exists");
     this.name = name;
   }
    
}
