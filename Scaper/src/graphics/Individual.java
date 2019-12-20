package graphics;

import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * A single mesh within a population
 * 
 * @author George Tiersma
 */
public class Individual extends MeshObject
{
    // The initial rotation values
    final private int DEFAULT_X_ROTATE = 0;
    final private int DEFAULT_Y_ROTATE = 90;
    final private int DEFAULT_Z_ROTATE = 0;
    
    // The x and y position in the center of the mesh
    private int centerX;
    private int centerY;
    
    // How high the mesh should be lifted above the terrain
    private int evalation;
    
    // The position of the vertex point on the terrain of where this mesh will
    // be placed
    private int x;
    private int y;
    private int z;
    
    // Rotations used to keep the mesh facing forward with all camera positions
    private Rotate xRotate;
    private Rotate yRotate;
    private Rotate zRotate;
    
    /**
     * CONSTRUCTOR
     * 
     * @param widthster The width of the terrain in vertices
     * @param depthster The depth of the terrain in vertices
     * @param fSize The length of each side of each face on the mesh when the
     *              mesh is not displaced
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param evster How high the mesh should be lifted above the terrain
     * @param eckster The x position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param whyster The y position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param zeester The z position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param dister The displacement map
     * @param bumpster The bump map
     * @param specster The specular map
     */
    public Individual(int widthster, int depthster, int fSize, int strengthster,
            int evster, int eckster, int whyster, int zeester, Image dister,
            Image bumpster, Image specster)
    {
        super(widthster, depthster, fSize, strengthster, dister, bumpster, specster);
        
        evalation = evster;
        
        x = eckster;
        y = whyster;
        z = zeester;
        
        // Adding half of the mesh's size to its position gets the center
        centerX = x + ((width * faceSize) / 2);
        centerY = y + ((depth * faceSize) / 2);
        
        xRotate = new Rotate(DEFAULT_X_ROTATE, Rotate.X_AXIS);
        yRotate = new Rotate(DEFAULT_Y_ROTATE, Rotate.Y_AXIS);
        zRotate = new Rotate(DEFAULT_Z_ROTATE, Rotate.Z_AXIS);
    }
    
    /**
     * Loads the data needed to construct the mesh into most of the variables
     * and objects within this Individual object
     */
    public void load()
    {
        super.load();
        
        reposition();
        prepareRotations();
    }
    
    /**
     * Loads the data needed to construct the mesh into most of the variables
     * and objects within this Individual object
     */
    private void prepareRotations()
    {
        xRotate.setPivotX(centerX);
        xRotate.setPivotY(centerY);
        xRotate.setPivotZ(z);
        
        yRotate.setPivotX(centerX);
        yRotate.setPivotY(centerY);
        yRotate.setPivotZ(z);
        
        zRotate.setPivotX(centerX);
        zRotate.setPivotY(centerY);
        zRotate.setPivotZ(z);
        
        viewster.getTransforms().add(xRotate);
        viewster.getTransforms().add(yRotate);
        viewster.getTransforms().add(zRotate);
    }
    
    /**
     * Places the mesh at the correct position based upon its current variable
     * values
     */
    private void reposition()
    {
        viewster.setTranslateX(x);
        // This calculation ensures that the mesh appears directly on top of the
        // terrain when the evalation is set to 0
        viewster.setTranslateY(y + evalation + (depth * faceSize));
        viewster.setTranslateX(z);
    }
}
