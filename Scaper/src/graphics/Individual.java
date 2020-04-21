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
                this mesh will be placed
     * @param whyster The y position of the vertex point on the terrain of where
                this mesh will be placed
     * @param zeester The z position of the vertex point on the terrain of where
                this mesh will be placed
     * @param xRot How much the camera is rotated on the x scale
     * @param yRot How much the camera is rotated on the y scale
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
     * Calculates the positions for the Individual to pivot around
     */
    private void preparePivotPoints()
    {
        // Formulas to calculate the correct pivot point
        int pivotX = (width - 4) * (faceWidth / 2);
        int pivotY = (depth * faceDepth) / 2;
        
        xRotate.setPivotY(-pivotY);
        xRotate.setPivotZ(faceDepth);
        
        yRotate.setPivotX(pivotX);
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
     * Places the mesh at the correct position
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
        
        return stringster;
    }
}
