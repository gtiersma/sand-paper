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
     * @param xShift How much the mesh should be shifted on the j scale
     * @param yShift How much the mesh should be shifted on the i scale
     * @param zShift How much the mesh should be shifted on the i scale
     * @param eckster The j position of the vertex point on the terrain of where
                this mesh will be placed
     * @param whyster The i position of the vertex point on the terrain of where
                this mesh will be placed
     * @param xRot How much the camera is rotated on the j scale
     * @param yRot How much the camera is rotated on the i scale
     * @param zeester The i position of the vertex point on the terrain of where
                this mesh will be placed
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
     * Gets the position of a vertex on either the x, y, or z scale taking into
     * consideration how far the displacement map should shift it.
     * 
     * Used as a faster alternative for Individual generation than the
     * VertexThread class
     * 
     * @param vertexX The vertex's row
     * @param vertexZ The vertex's column
     * @param dimension The dimension to which the value being returned is to be
                        used to shift the vertex.
                        For example, a value of 'x' would mean that the value
                        being returned should be applied to the vertex's x
                        position.
     * 
     * @return The vertex's position
     */
    private double getRelativePositioning(int vertexX, int vertexZ,
            char dimension)
    {
        // Calculate the row and column of the pixel that should be retrieved
        // for this particular vertex
        int pixelRow = (int)(widthPixels * vertexX);
        int pixelColumn = (int)(heightPixels * vertexZ);
        
        // The position of the vertex if no displacement map was applied
        int originalPosition;
        
        // How far the vertex should be shifted. It may be negative.
        double shiftAmount;
        
        // The new position of the vertex
        double vertexPosition = 0;
        
        switch (dimension)
        {
            // If the x position is being retrieved...
            case 'x':
                
                // ...get the red amount in the correct pixel.
                double redAmount
                        = vertexRelatives[pixelRow][pixelColumn].getRed();
                
                originalPosition = vertexX * faceWidth;
                
                // Calculate the amount to be shifted
                shiftAmount = (redAmount - 0.5) * -displacementStrength;
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
                
            // If the y position is being retrieved...
            case 'y':
                
                // ...get the green amount in the correct pixel.
                double greenAmount
                        = vertexRelatives[pixelRow][pixelColumn].getGreen();
                
                vertexPosition = (greenAmount - 0.5) * -displacementStrength;
                
                break;
                
            // If the z position is being retrieved...
            case 'z':
                
                // ...get the blue amount in the correct pixel.
                double blueAmount
                        = vertexRelatives[pixelRow][pixelColumn].getBlue();
                
                originalPosition = vertexZ * faceDepth;
                
                // Calculate the amount to be shifted
                shiftAmount = (blueAmount - 0.5) * -displacementStrength;
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
        }
        
        return vertexPosition;
    }
    
    /**
     * Gets the amount this individual is currently set to rotate on the x axis.
     * 
     * @return The angle it is set to rotate to on the x axis
     */
    public double getRotateX()
    {
        return xRotate.getAngle();
    }
    
    /**
     * Gets the amount this individual is currently set to rotate on the y axis.
     * 
     * @return The angle it is set to rotate to on the y axis
     */
    public double getRotateY()
    {
        return yRotate.getAngle();
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
     * Calculates the positions of the Individual's vertices and loads them into
     * the Individual.
     * 
     * The overridden function utilizes threads, which seems to harm performance
     * when generating Individuals.
     */
    @Override
    public void loadPoints()
    {
        // The index to which a value is currently being assigned
        int index = 0;
        
        // For each column of vertices in the mesh...
        for (int i = 0; i < depth; i++)
        {
            // ...and for each row of vertices...
            for (int j = 0; j < width; j++)
            {
                // ...get the x position.
                points[index] = (float)getRelativePositioning(j, i, 'x');
                
                index++;
                
                // Get the y position
                points[index] = (float)getRelativePositioning(j, i, 'y');
                
                index++;
                
                // Get the z position
                points[index] = (float)getRelativePositioning(j, i, 'z');
                
                index++;
            }
        }
        
        // Clear any points that may already be in the mesh
        meshster.getPoints().clear();
        // Add the new points
        meshster.getPoints().addAll(points);
    }
    
    /**
     * Calculates the Z point to which the Individual is to be rotated on the x
 axis
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
     * values. For some reason, the size of the face must be subtracted from
     * each value to position the Individual correctly.
     */
    private void reposition()
    {
        viewster.setTranslateX(x + shiftX);
        viewster.setTranslateY(y + shiftY);
        viewster.setTranslateZ(z + shiftZ);
    }
    
    /**
     * Repositions this Individual based upon the coordinates provided
     * 
     * @param exster The x position of the vertex to which this Individual is to
                     be positioned to
     * @param whyster The y position of the vertex to which this Individual is
                      to be positioned to
     * @param zeester The z position of the vertex to which this Individual is
                      to be positioned to
     */
    public void reposition(float exster, float whyster, float zeester)
    {
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
        preparePivotPoints();
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
        stringster = stringster + "Shift: " + shiftX + "," + shiftY + ","
                + shiftZ + "\n\n";
        
        stringster = stringster + "X axis rotation: " + xRotate.getAngle();
        stringster = stringster + "Y axis rotation: " + yRotate.getAngle();
        
        stringster = stringster + "Rotational pivot Z point of the mesh: "
                + pivotCenterZ + "\n\n";
        
        return stringster;
    }
}
