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
    // Added to the X rotation to ensure the Individual is facing the camera
    final private int BASE_X_ROTATION = 90;
    
    // How much the Individual should be shifted
    private int shiftX;
    private int shiftY;
    private int shiftZ;
    
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
     * @param xShift How much the mesh should be shifted on the x scale
     * @param yShift How much the mesh should be shifted on the y scale
     * @param zShift How much the mesh should be shifted on the z scale
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
            int strengthster, int xShift, int yShift, int zShift,
            double eckster, double whyster, double zeester, double xRot,
            double yRot, Image dister)
    {
        super(widthster, heightster, fWidth, fHeight, strengthster, dister);
        
        shiftX = xShift;
        shiftY = yShift;
        shiftZ = zShift;
        
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
        System.out.println("Individual -> Loading Individual...");
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
        System.out.println("Individual -> Preparing rotational pivot point...");
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
        System.out.println("Individual -> Preparing rotations...");
        preparePivotPoints();
        
        // yRotate must be added first. Otherwise, this won't work for some
        // reason
        viewster.getTransforms().add(yRotate);
        viewster.getTransforms().add(xRotate);
    }
    
    /**
     * Places the mesh at the correct position based upon its current variable
     * values. For some reason, the size of the face must be subtracted from
     * each value to position the Individual correctly.
     */
    private void reposition()
    {
        System.out.println("Individual -> Repositioning Individual...");
        viewster.setTranslateX(x - faceWidth + shiftX);
        viewster.setTranslateY(y - faceDepth + shiftY);
        viewster.setTranslateZ(z - faceWidth + shiftZ);
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
        System.out.println("Individual -> Repositioning Individual to the terrain vertex at " + exster + "," + whyster + "," + zeester + "...");
        x = exster;
        y = whyster;
        z = zeester;
        
        reposition();
    }
    
    /**
     * Sets how much this Individual should be shifted from the terrain's vertex
     * that it was positioned at
     * 
     * @param xShift How much the Individual is to be shifted on the x scale
     * @param yShift How much the Individual is to be shifted on the y scale
     * @param zShift How much the Individual is to be shifted on the z scale
     */
    public void setShift(int xShift, int yShift, int zShift)
    {
        System.out.println("Individual -> Setting Individual shift amount to " + xShift + "," + yShift + "," + zShift + "...");
        shiftX = xShift;
        shiftY = yShift;
        shiftZ = zShift;
        
        reposition();
    }
    
    /**
     * Sets how high each face on the Individual should be
     * 
     * @param heightster How high each face on the Individual should be
     */
    public void setFaceHeight(int heightster)
    {
        System.out.println("Individual -> Setting the face height to " + heightster + "...");
        faceDepth = heightster;
        
        loadPoints();
        reposition();
        preparePivotPoints();
    }
    
    /**
     * Sets how wide each face on the Individual should be
     * 
     * @param widthster How wide each face on the Individual should be
     */
    public void setFaceWidth(int widthster)
    {
        System.out.println("Individual -> Setting the face width to " + widthster + "...");
        faceWidth = widthster;
        
        loadPoints();
        reposition();
        preparePivotPoints();
    }
    
    /**
     * Sets how much the Individual should be rotated on the x scale
     * 
     * @param angle How much the camera is set to be rotated on the x scale
     */
    public void setRotationX(double angle)
    {
        System.out.println("Individual -> Rotating on x axis to " + angle + "...");
        xRotate.setAngle(BASE_X_ROTATION - angle);
    }
    
    /**
     * Sets how much the Individual should be rotated on the y scale
     * 
     * @param angle How much the camera is set to be rotated on the y scale
     */
    public void setRotationY(double angle)
    {
        System.out.println("Individual -> Rotating on y axis to " + angle + "...");
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
        stringster = stringster + "Shift: " + shiftX + "," + shiftY + ","
                + shiftZ + "\n\n";
        
        stringster = stringster + "X axis rotation: " + xRotate.getAngle();
        stringster = stringster + "Y axis rotation: " + yRotate.getAngle();
        
        stringster = stringster + "Rotational pivot Z point of the mesh: "
                + pivotCenterZ + "\n\n";
        
        return stringster;
    }
}
