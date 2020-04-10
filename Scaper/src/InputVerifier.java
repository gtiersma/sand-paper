/**
 * Validates user input
 * 
 * @author George Tiersma
 */
public class InputVerifier
{
    // The smallest vertex size that the terrain and populations can be
    final int MIN_MESH_SIZE = 2;
    
    /**
     * CONSTRUCTOR
     */
    public InputVerifier() {}
    
    /**
     * Checks if a given vertex size is valid for the terrain or a population
     * 
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    public boolean isMeshSizeValid(int size)
    {
        boolean valid = false;
        
        if (size >= MIN_MESH_SIZE)
        {
            valid = true;
        }
        
        return valid;
    }
}
