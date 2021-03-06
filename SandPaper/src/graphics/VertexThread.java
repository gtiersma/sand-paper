package graphics;

import java.util.concurrent.Callable;
import javafx.scene.paint.Color;

/**
 * A thread that calculates what an x, y or z position of a given vertex should
 * be based upon the displacement information assigned to it
 * 
 * @author George Tiersma
 */
public class VertexThread implements Callable<Integer>
{
    // The user-defined strength of the displacement map
    private int displacementStrength;
    
    // A multiplier that is used for the size of each face
    private int faceSize;
   
    // The position of the vertex on the provided axis
    private int position;
    
    // The dimension being calculated
    private char dimension;
    
    // The color of the pixel on the displacement map that belongs to this
    // vertex
    private Color displacementColor;
    
    /**
     * CONSTRUCTOR
     * 
     * @param fSize A multiplier that is used for the size of each face
     * @param poser The numbered position of the vertex
     * @param strengthster The user-defined strength of the displacement map
     * @param dimster The dimension being calculated
     * @param colster The color of the pixel on the displacement map that
     *                belongs to this vertex
     */
    public VertexThread(int fSize, int poser, int strengthster, char dimster,
            Color colster)
    {
        displacementStrength = strengthster;
        
        faceSize = fSize;
        
        position = poser;
        
        dimension = dimster;
        
        displacementColor = colster;
    }
    
    /**
     * Calculates the position of a vertex on either the x, y, or z scale taking
     * into consideration how far the displacement map should shift it
     * 
     * @return The vertex's position on the given axis
     */
    @Override
    public Integer call()
    {
        // The position of the vertex if no displacement map was going to be
        // applied
        int originalPosition;
        
        // How far the vertex should be shifted. It may be negative.
        int shiftAmount;
        
        switch (dimension)
        {
            // If the x position is being retrieved...
            case 'x':
                
                // ...get the red amount in the correct pixel.
                double redAmount = displacementColor.getRed();
                
                originalPosition = position * faceSize;
                
                // Calculate the amount to be shifted
                shiftAmount = (int)((redAmount - 0.5) * -displacementStrength);
                
                position = originalPosition + shiftAmount;
                
                break;
                
            // If the y position is being retrieved...
            case 'y':
                
                // ...get the green amount in the correct pixel.
                double greenAmount = displacementColor.getGreen();
                
                position = (int)((greenAmount - 0.5) * -displacementStrength);
                
                break;
                
            // If the z position is being retrieved...
            case 'z':
                
                // ...get the blue amount in the correct pixel.
                double blueAmount = displacementColor.getBlue();
                
                originalPosition = position * faceSize;
                
                // Calculate the amount to be shifted
                shiftAmount = (int)((blueAmount - 0.5) * -displacementStrength);
                
                position = originalPosition + shiftAmount;
                
                break;
        }
        
        return position;
    }
}
