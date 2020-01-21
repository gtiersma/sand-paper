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
    // Used to repositon the Individual so that if the pixel's color assigned to
    // it from the elevation map is exactly between white and black, the
    // Individual would rest upon the terrain
    final private int BASE_ELEVATION = -25;
    
    // Added to the X rotation to ensure the Individual is facing the camera
    final private int BASE_X_ROTATION = 90;
    
    // How high the mesh should be lifted above the terrain
    private int elevation;
    
    // The Z position to assign the X rotational pivot point to
    private double pivotCenterZ;
    
    // The position of the vertex point on the terrain of where this mesh will
    // be placed
    private double x;
    private double y;
    private double z;
    
    // Rotations used to keep the mesh facing forward with all camera positions
    private Rotate xRotate;
    private Rotate yRotate;
    
    /**
     * CONSTRUCTOR
     * 
     * @param widthster The width of the mesh in vertices
     * @param heightster The depth of the mesh in vertices
     * @param fWidth The width of each face in the mesh when not displaced
     * @param fHeight The height of each face in the mesh when not displaced
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param evster How high the mesh should be lifted above the terrain
     * @param eckster The x position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param whyster The y position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param xRot How much the camera is rotated on the x scale
     * @param yRot How much the camera is rotated on the y scale
     * @param zeester The z position of the vertex point on the terrain of where
     *                this mesh will be placed
     * @param dister The displacement map
     */
    public Individual(int widthster, int heightster, int fWidth, int fHeight,
            int strengthster, int evster, double eckster, double whyster,
            double zeester, double xRot, double yRot, Image dister)
    {
        super(widthster, heightster, fWidth, fHeight, strengthster, dister);
        
        elevation = evster;
        
        x = eckster;
        y = whyster;
        z = zeester;
        
        pivotCenterZ = 0;
        
        // Rotational values are made negative to rotate correctly
        xRotate = new Rotate(BASE_X_ROTATION - xRot, Rotate.X_AXIS);
        yRotate = new Rotate(-yRot, Rotate.Y_AXIS);
    }
    
    /**
     * Loads the data needed to construct the mesh into most of the variables
     * and objects within this Individual object
     */
    @Override
    public void load()
    {
        super.load();
        
        reposition();
        prepareRotations();
    }
    
    /**
     * Calculates the Z point to which the Individual is to be rotated on the x
     * axis
     */
    private void preparePivotPoints()
    {
        // This should be the edge of the Individual. Having this be the pivot
        // point will keep the bottom of the Individual on the terrain (if the
        // Individual is to be placed there).
        pivotCenterZ = (width * faceWidth) / 2;
        
        xRotate.setPivotX(pivotCenterZ);
    }
    
    /**
     * Loads the data needed to construct the mesh into most of the variables
     * and objects within this Individual object
     */
    private void prepareRotations()
    {
        preparePivotPoints();
        
        // yRotate must be added first. Otherwise, this won't work for some
        // reason
        viewster.getTransforms().add(yRotate);
        viewster.getTransforms().add(xRotate);
    }
    
    /**
     * Places the mesh at the correct position based upon its current variable
     * values
     */
    private void reposition()
    {
        viewster.setTranslateX(x - faceWidth);
        // This calculation ensures that the mesh appears directly on top of the
        // terrain when the elevation is set to 0
        viewster.setTranslateY(y + elevation);
        viewster.setTranslateZ(z + faceDepth);
    }
    
    /**
     * Repositions this Individual based upon the coordinates provided
     * 
     * @param exster The x position of the vertex to which this Individual is to
     *               be positioned to
     * @param whyster The y position of the vertex to which this Individual is
     *                to be positioned to
     * @param zeester The z position of the vertex to which this Individual is
     *                to be positioned to
     */
    public void reposition(float exster, float whyster, float zeester)
    {
        x = exster;
        y = whyster;
        z = zeester;
        
        preparePivotPoints();
        
        reposition();
    }
    
    /**
     * Repositions this Individual based upon the elevation provided
     * 
     * @param elster How much the Individual is to be elevated
     */
    public void setElevation(int elster)
    {
        elevation = elster + BASE_ELEVATION;
        
        viewster.setTranslateY(y + elevation);
    }
    
    /**
     * Sets how high each face on the Individual should be
     * 
     * @param heightster How high each face on the Individual should be
     */
    public void setFaceHeight(int heightster)
    {
        faceDepth = heightster;
        
        loadPoints();
    }
    
    /**
     * Sets how wide each face on the Individual should be
     * 
     * @param widthster How wide each face on the Individual should be
     */
    public void setFaceWidth(int widthster)
    {
        faceWidth = widthster;
        
        loadPoints();
    }
    
    /**
     * Sets how much the Individual should be rotated on the x scale
     * 
     * @param angle How much the camera is set to be rotated on the x scale
     */
    public void setRotationX(double angle)
    {
        xRotate.setAngle(BASE_X_ROTATION - angle);
    }
    
    /**
     * Sets how much the Individual should be rotated on the y scale
     * 
     * @param angle How much the camera is set to be rotated on the y scale
     */
    public void setRotationY(double angle)
    {
        yRotate.setAngle(-angle);
    }
    
    /**
     * Gets a string representation of all of the variables in this Individual
     * 
     * @return A string representation of all of the variables in this
     * Individual
     */
    @Override
    public String toString()
    {
        String stringster = super.toString() + "\n\n";
        
        stringster = stringster + "Individual properties:";
        stringster = stringster + "---------------------------------------\n\n";
        
        stringster = stringster + "Position: " + x + "," + y + "," + z + "\n";
        stringster = stringster + "Elevation: " + elevation + "\n\n";
        
        stringster = stringster + "X axis rotation: " + xRotate.getAngle();
        stringster = stringster + "Y axis rotation: " + yRotate.getAngle();
        
        stringster = stringster + "Rotational pivot Z point of the mesh: "
                + pivotCenterZ + "\n\n";
        
        return stringster;
    }
}
