package graphics;

import javafx.scene.PointLight;
import javafx.scene.paint.Color;

/**
 * Controls a light in the scene
 * 
 * @author George Tiersma
 */
public class LightObject
{
    private final double DEFAULT_INTENSITY = 1;
    
    private final double DEFAULT_X_PERCENTAGE = 100;
    private final double DEFAULT_Y_PERCENTAGE = 100;
    private final double DEFAULT_Z_PERCENTAGE = 100;
    
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
    
    private Color lightColor;
    
    private PointLight lightster;
    
    /**
     * CONSTRUCTOR
     */
    public LightObject()
    {
        intensity = DEFAULT_INTENSITY;
        
        x = 0;
        y = 0;
        z = 0;
        
        xPercentage = DEFAULT_X_PERCENTAGE;
        yPercentage = DEFAULT_Y_PERCENTAGE;
        zPercentage = DEFAULT_Z_PERCENTAGE;
        
        lightColor = Color.rgb(255, 255, 255, intensity);
        
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
        
        // The position the light is to be placed at
        double position = (percentage * onePercent) + zeroPercentPosition;
        
        return position;
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
     * Sets the light's color
     * 
     * @param colster The color to be set
     */
    public void setColor(Color colster)
    {
        // The color must be assigned this way in order to retain the light's
        // intensity
        lightColor = Color.rgb((int)colster.getRed(), (int)colster.getGreen(),
                (int)colster.getBlue(), intensity);
                
        lightster.setColor(lightColor);
    }
    
    /**
     * Sets the lights intensity
     * 
     * @param intster The light's intensity (a value from 0-1)
     */
    public void setIntensity(double intster)
    {
        intensity = intster;
        
        // The intensity is assigned as the opacity value of the color
        lightColor = Color.rgb((int)lightColor.getRed(),
                (int)lightColor.getGreen(), (int)lightColor.getBlue(),
                intensity);
        
        lightster.setColor(lightColor);
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
