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
    final private short BASE_X_ROTATION = 90;
    
    // How much the Individual should be shifted
    private byte shiftX;
    private byte shiftY;
    private byte shiftZ;
    
    // Half of the displacement strength. The Individual's default position is
    // in the middle of the displacement strength spectrum, so this value is
    // commonly needed in calculations.
    private int halfStrength;
    
    // The position of the vertex point on the terrain of where this mesh will
    // be placed
    private float x;
    private float y;
    private float z;
    
    // Rotations used to keep the mesh facing forward with all camera positions
    private Rotate xRotate;
    private Rotate yRotate;
    
    /**
     * CONSTRUCTOR
     * 
     * @param xShift How much the mesh should be shifted on the x scale
     * @param yShift How much the mesh should be shifted on the y scale
     * @param zShift How much the mesh should be shifted on the z scale
     * @param fWidth The width of each face in the mesh when not displaced
     * @param fHeight The height of each face in the mesh when not displaced
     * @param widthster The width of the mesh in vertices
     * @param heightster The depth of the mesh in vertices
     * @param xRot How much the camera is rotated on the x scale
     * @param yRot How much the camera is rotated on the y scale
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param eckster The x position of the vertex point on the terrain of where
                this mesh will be placed
     * @param whyster The y position of the vertex point on the terrain of where
                this mesh will be placed
     * @param zeester The z position of the vertex point on the terrain of where
                this mesh will be placed
     * @param dister The displacement map
     */
    public Individual(byte xShift, byte yShift, byte zShift, short fWidth,
            short fHeight, short widthster, short heightster, short xRot,
            short yRot, int strengthster, float eckster, float whyster,
            float zeester, Image dister)
    {
        super(fWidth, fHeight, widthster, heightster, strengthster, dister);
        
        shiftX = xShift;
        shiftY = yShift;
        shiftZ = zShift;
        
        halfStrength = displacementStrength / 2;
        
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
    private int getRelativePositioning(byte vertexX, byte vertexZ,
            char dimension)
    {
        // The center value for a color value's range (which is 0.0 - 1.0)
        final double MIDDLE_COLOR = 0.5;
        
        // How far the vertex should be shifted. It may be negative.
        short shiftAmount;
        
        // The position of the vertex if no displacement map was applied
        int originalPosition;
        
        // Calculate the row and column of the pixel that should be retrieved
        // for this particular vertex
        int pixelRow = widthPixels * vertexX;
        int pixelColumn = heightPixels * vertexZ;
        
        // The new position of the vertex
        int vertexPosition = 0;
        
        switch (dimension)
        {
            // If the x position is being retrieved...
            case 'x':
                
                // ...get the red amount in the correct pixel.
                double redAmount
                        = vertexRelatives[pixelRow][pixelColumn].getRed();
                
                originalPosition = vertexX * faceWidth;
                
                // Calculate the amount to be shifted
                shiftAmount = (short)((redAmount - MIDDLE_COLOR)
                        * -displacementStrength);
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
                
            // If the y position is being retrieved...
            case 'y':
                
                // ...get the green amount in the correct pixel.
                double greenAmount
                        = vertexRelatives[pixelRow][pixelColumn].getGreen();
                
                vertexPosition = (int)((greenAmount - MIDDLE_COLOR)
                        * -displacementStrength);
                
                break;
                
            // If the z position is being retrieved...
            case 'z':
                
                // ...get the blue amount in the correct pixel.
                double blueAmount
                        = vertexRelatives[pixelRow][pixelColumn].getBlue();
                
                originalPosition = vertexZ * faceDepth;
                
                // Calculate the amount to be shifted
                shiftAmount = (short)((blueAmount - MIDDLE_COLOR) 
                        * -displacementStrength);
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
        }
        
        return vertexPosition;
    }
    
    /**
     * Gets half of the Individual's width (measured in faces)
     * 
     * @return Half of the Individual's width (measured in faces)
     */
    private double getHalfFaceWidth()
    {
        double halfFaceWidth = width / 2;
        
        // If the vertex width is even...
        if (width % 2 == 0)
        {
            // ...0.5 will need to be subtracted from it for it to be accurate.
            halfFaceWidth = halfFaceWidth - 0.5;
        }
        
        return halfFaceWidth;
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
        for (byte i = 0; i < depth; i++)
        {
            // ...and for each row of vertices...
            for (byte j = 0; j < width; j++)
            {
                // ...get the x position.
                points[index] = getRelativePositioning(j, i, 'x');
                
                index++;
                
                // Get the y position
                points[index] = getRelativePositioning(j, i, 'y');
                
                index++;
                
                // Get the z position
                points[index] = getRelativePositioning(j, i, 'z');
                
                index++;
            }
        }
        
        // Clear any points that may already be in the mesh
        meshster.getPoints().clear();
        // Add the new points
        meshster.getPoints().addAll(points);
    }
    
    /**
     * Calculates the positions for the Individual to pivot on
     */
    private void preparePivotPoints()
    {
        double halfFaceWidth = getHalfFaceWidth();
        
        // Formula to calculate the correct pivot point on the x axis
        double pivotX = faceWidth * halfFaceWidth - halfStrength;
        
        xRotate.setPivotX(pivotX);
        xRotate.setPivotY(-halfStrength);
        xRotate.setPivotZ(-halfStrength);
        
        yRotate.setPivotX(pivotX);
        yRotate.setPivotY(-halfStrength);
        yRotate.setPivotZ(-halfStrength);
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
        // Half the width of the Individual. This must be subtracted from the x
        // position to ensure that the center of the Individual stays at the
        // same position regardless of how wide it is.
        double halfWidth = getHalfFaceWidth() * faceWidth;
        
        viewster.setTranslateX(x + shiftX + halfStrength - halfWidth);
        viewster.setTranslateY(y + shiftY + halfStrength);
        viewster.setTranslateZ(z + shiftZ + halfStrength);
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
    public void setShift(byte xShift, byte yShift, byte zShift)
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
    public void setRotationX(short angle)
    {
        xRotate.setAngle(BASE_X_ROTATION - angle);
    }
    
    /**
     * Sets how much the Individual should be rotated on the y scale
     * 
     * @param angle How much the camera is set to be rotated on the y scale
     */
    public void setRotationY(short angle)
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
