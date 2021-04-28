package generics;

import graphics.TextureObject;

/**
 * Creates deep copies of arrays
 * 
 * @author George Tiersma
 */
public class DeepCloner
{

    /**
     * CONSTRUCTOR
     */
    public DeepCloner() {}
    
    /**
     * Clones a 2d array of booleans
     * 
     * @param source The array being cloned
     * 
     * @return The cloned array
     */
    public boolean[][] clone(boolean[][] source)
    {
        boolean[][] copy = new boolean[source.length][source[0].length];
        
        for (int i = 0; i < source.length; i++)
        {
            System.arraycopy(source[i], 0, copy[i], 0, source[i].length);
        }
        
        return copy;
    }
    
    /**
     * Clones an array of floats
     * 
     * @param source The array being cloned
     * 
     * @return The cloned array
     */
    public float[] clone(float[] source)
    {
        float[] copy = new float[source.length];
        
        System.arraycopy(source, 0, copy, 0, source.length);
        
        return copy;
    }
    
    /**
     * Clones an array of TextureObjects
     * 
     * @param source The array being cloned
     * 
     * @return The cloned array
     */
    public TextureObject[] clone(TextureObject[] source)
    {
        TextureObject[] copy = new TextureObject[source.length];
        
        for (int i = 0; i < source.length; i++)
        {
            copy[i] = source[i].getCopy();
        }
        
        return copy;
    }
}
