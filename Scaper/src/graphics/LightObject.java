package graphics;

import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * Controls a light in the scene
 * 
 * @author George Tiersma
 */
public class LightObject
{
    private double intensity;
    
    private double x;
    private double y;
    private double z;
    
    // Percentage of where on the scale within the mesh's range that the light
    // is. 0% is one end of the mesh. 100% is the other end. 50% is the center.
    // Anything below 0% or above 100% is outside the mesh.
    private double xPercentage;
    private double yPercentage;
    private double zPercentage;
    
    private String name;
    
    private Color lightColor;
    
    private PointLight lightster;
    
    /**
     * CONSTRUCTOR
     * 
     * @param colster The color the light should begin as
     * @param namster The name of the light
     * @param exster The starting position of the light on the X scale
     * @param intster The starting intensity of the light
     * @param zeester The starting position of the light on the Z scale
     * @param waister The starting position of the light on the Y scale
     */
    public LightObject(Color colster, String namster, double intster,
            double exster, double waister, double zeester)
    {
        intensity = intster;
        
        x = 0;
        y = 0;
        z = 0;
        
        xPercentage = exster;
        yPercentage = waister;
        zPercentage = zeester;
        
        name = namster;
        
        lightColor = colster;
        
        lightster = new PointLight(lightColor);
    }
    
    /**
     * Calculates the specific position the light should be placed at on a
     * dimensional scale given the data provided
     * 
     * @param far The approximate greatest distance between the center of the
     *            mesh and the furthest point from the mesh for the dimension
     *            being calculated (x, y or z)
     * @param origin The approximate position of the center of the mesh on the
     *               scale being calculated (x, y or z)
     * @param percentage Where the light should be positioned in the form of a
     *                   percentage relative to the mesh. For example, 0 would
     *                   the edge of the mesh. 100 would be the opposite edge.
     *                   50 would be the center. anything below 0 or above 100
     *                   will place the light outside of the mesh's boundaries.
     * 
     * @return The position the light should be placed at for a specific
     *         dimensional scale
     */
    private double calculatePosition(double far, double origin,
            double percentage)
    {
        // How much percentage that the far variable's value makes up
        final double FAR_PERCENTAGE = 50;
        
        // How much distance 1% should be
        double onePercent = far / FAR_PERCENTAGE;
        
        // The position that a value of 0% should be at
        double zeroPercentPosition = origin - far;
        
        // The position the light is to be placed at. Percentage is inverted to
        // correct the light's placement.
        double position = (percentage * onePercent) + zeroPercentPosition;
        
        return position;
    }
    
    /**
     * Gets the current intensity of the light
     * 
     * @return The current intensity of the light
     */
    public double getIntensity()
    {
        return intensity;
    }
    
    /**
     * Gets the light
     * 
     * @return The light
     */
    public PointLight getLight()
    {
        return lightster;
    }
    
    /**
     * Gets the name of the light
     * 
     * @return The name of the light
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Gets the position percentage of the light on the X scale
     * 
     * @return The position percentage on the X scale
     */
    public double getPercentageX()
    {
        return xPercentage;
    }
    
    /**
     * Gets the position percentage of the light on the Y scale
     * 
     * @return The position percentage on the Y scale
     */
    public double getPercentageY()
    {
        return yPercentage;
    }
    
    /**
     * Gets the position percentage of the light on the Z scale
     * 
     * @return The position percentage on the Z scale
     */
    public double getPercentageZ()
    {
        return zPercentage;
    }
    
    /**
     * Create the Rotation objects for the light
     */
    public void initializeRotations()
    {
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        
        lightster.getTransforms().add(xRotate);
        lightster.getTransforms().add(yRotate);
    }
    
    /**
     * Sets the light's color
     * 
     * @param colster The color to be set
     */
    public void setColor(Color colster)
    {
        lightster.setColor(colster);
    }
    
    /**
     * Sets the lights intensity
     * 
     * @param intster The light's intensity (a value from 0-1)
     */
    public void setIntensity(double intster)
    {
        final double MAX_COLOR_VALUE = 255;
        
        int red = (int)(lightColor.getRed() * MAX_COLOR_VALUE);
        int green = (int)(lightColor.getGreen() * MAX_COLOR_VALUE);
        int blue = (int)(lightColor.getBlue() * MAX_COLOR_VALUE);
        
        intensity = intster;
        
        // The intensity is assigned as the opacity value of the color
        lightColor = Color.rgb(red, green, blue, intensity);
        
        lightster.setColor(lightColor);
    }
    
    /**
     * Set a Rotation object for this light
     * 
     * @param dimension The dimension of which the light rotates on ('x', 'y' or
     *                  'z')
     * @param rotster The Rotation object to be set
     */
    public void setRotation(char dimension, Rotate rotster)
    {
        // The index of the light's transforms to which the rotation should be
        // assigned
        int index = 0;
        
        if (dimension == 'y')
        {
            index = 1;
        }
                    
        lightster.getTransforms().set(index, rotster);
    }
    
    /**
     * Sets the light's position on the X scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the X scale
     * @param origin The approximate position on the X scale of the center of
     *               the mesh
     */
    public void setXPosition(double far, double origin)
    {
        x = calculatePosition(far, origin, xPercentage);
        
        lightster.setTranslateX(x);
    }
    
    /**
     * Sets the light's position on the X scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the X scale
     * @param origin The approximate position on the X scale of the center of
     *               the mesh
     * @param percentage The position that the light is to be placed at on the X
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     */
    public void setXPosition(double far, double origin, double percentage)
    {
        xPercentage = percentage;
        
        x = calculatePosition(far, origin, percentage);
        
        lightster.setTranslateX(x);
    }
    
    /**
     * Sets the light's position on the Y scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Y scale
     * @param origin The approximate position on the Y scale of the center of
     *               the mesh
     */
    public void setYPosition(double far, double origin)
    {
        y = calculatePosition(far, origin, yPercentage);
        
        lightster.setTranslateY(y);
    }
    
    /**
     * Sets the light's position on the Y scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Y scale
     * @param origin The approximate position on the Y scale of the center of
     *               the mesh
     * @param percentage The position that the light is to be placed at on the Y
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     */
    public void setYPosition(double far, double origin, double percentage)
    {
        yPercentage = percentage;
        
        y = calculatePosition(far, origin, percentage);
        
        lightster.setTranslateY(y);
    }
    
    /**
     * Sets the light's position on the Z scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Z scale
     * @param origin The approximate position on the Z scale of the center of
     *               the mesh
     */
    public void setZPosition(double far, double origin)
    {
        z = calculatePosition(far, origin, zPercentage);
        
        lightster.setTranslateZ(z);
    }
    
    /**
     * Sets the light's position on the Z scale
     * 
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Z scale
     * @param origin The approximate position on the Z scale of the center of
     *               the mesh
     * @param percentage The position that the light is to be placed at on the Z
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     */
    public void setZPosition(double far, double origin, double percentage)
    {
        zPercentage = percentage;
        
        z = calculatePosition(far, origin, percentage);
        
        lightster.setTranslateZ(z);
    }
}
