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
    // Percentage of where on the scale within the mesh's range that the light
    // is. 0% is one end of the mesh. 100% is the other end. 50% is the center.
    // Anything below 0% or above 100% is outside the mesh.
    private int xPercentage;
    private int yPercentage;
    private int zPercentage;
    
    private double x;
    private double y;
    private double z;
    
    private String name;
    
    private Color lightColor;
    
    private PointLight lightster;
    
    /**
     * CONSTRUCTOR
     * 
     * @param exster The starting position of the light on the X scale
     * @param zeester The starting position of the light on the Z scale
     * @param waister The starting position of the light on the Y scale
     * @param namster The name of the light
     * @param colster The color the light should begin as
     */
    public LightObject(int exster, int waister, int zeester, String namster,
            Color colster)
    {
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
     * Gets the color of the light
     * 
     * @return The color of the light
     */
    public Color getColor()
    {
        return lightColor;
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
    public int getPercentageX()
    {
        return xPercentage;
    }
    
    /**
     * Gets the position percentage of the light on the Y scale
     * 
     * @return The position percentage on the Y scale
     */
    public int getPercentageY()
    {
        return yPercentage;
    }
    
    /**
     * Gets the position percentage of the light on the Z scale
     * 
     * @return The position percentage on the Z scale
     */
    public int getPercentageZ()
    {
        return zPercentage;
    }
    
    /**
     * Gets the point light
     * 
     * @return The light
     */
    public PointLight getPointLight()
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
        lightster.setColor(colster);
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
     * @param percentage The position that the light is to be placed at on the X
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the X scale
     * @param origin The approximate position on the X scale of the center of
     *               the mesh
     */
    public void setXPosition(int percentage, double far, double origin)
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
     * @param percentage The position that the light is to be placed at on the Y
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Y scale
     * @param origin The approximate position on the Y scale of the center of
     *               the mesh
     */
    public void setYPosition(int percentage, double far, double origin)
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
     * @param percentage The position that the light is to be placed at on the Z
     *                   scale in the form of a percentage of the light's
     *                   position relative to the position of the mesh. For
     *                   example, a value of 0 would position the light at the
     *                   edge of the mesh. 100 would be the opposite edge. 50
     *                   would be the mesh's center. Anything below 0 or above
     *                   100 would be outside the boundaries of the mesh.
     * @param far The approximate distance from the center of the mesh to the
     *            outer most point on the mesh on the Z scale
     * @param origin The approximate position on the Z scale of the center of
     *               the mesh
     */
    public void setZPosition(int percentage, double far, double origin)
    {
        zPercentage = percentage;
        
        z = calculatePosition(far, origin, percentage);
        
        lightster.setTranslateZ(z);
    }
}
