package tabs;

import graphics.LightObject;
import java.util.Optional;
import javafx.scene.PointLight;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

/**
 * Controls the operations for the light tab
 * 
 * @author George Tiersma
 */
public class LightTab
{
    private final double DEFAULT_X_PERCENTAGE = 120.0;
    private final double DEFAULT_Y_PERCENTAGE = 10.0;
    private final double DEFAULT_Z_PERCENTAGE = -20.0;
    
    private final Color DEFAULT_COLOR = Color.WHITE;
    
    // The approximate position of the center of the mesh
    private double centerX;
    private double centerY;
    private double centerZ;
    
    // The approximate greatest distance that a point may be from the center of
    // the mesh
    private double furthest;
    
    // The light object currently selected by the user in the choice box
    private LightObject activeLight;
    
    // All of the lights
    private LightObject lights[];
    
    /**
     * CONSTRUCTOR
     */
    public LightTab()
    {
        centerX = 0;
        centerY = 0;
        centerZ = 0;
        
        furthest = 0;
        
        lights = new LightObject[0];
    }
    
    /**
     * Creates a new light
     * 
     * @return The name of the light
     */
    public String createLight()
    {
        String name = "";
        
        // Get the name from the user
        Optional<String> result = showNameDialog();
        
        // As long as a name was given...
        if (result.isPresent())
        {
            // ...get it.
            name = result.get();
            
            // Create a new light.
            LightObject newLight = new LightObject(DEFAULT_COLOR, name, 
                    DEFAULT_X_PERCENTAGE, DEFAULT_Y_PERCENTAGE,
                    DEFAULT_Z_PERCENTAGE);
            
            // Create a new array with room for another light
            LightObject[] newLights = new LightObject[lights.length + 1];
        
            // Copy the old array into the new one
            System.arraycopy(lights, 0, newLights, 0, lights.length);
            
            // Make the old array the new one
            lights = newLights;
            
            // Make the new light be the currently selected light
            activeLight = newLight;
            
            // Add the new light to the array
            lights[lights.length - 1] = newLight;
        }
        
        return name;
    }
    
    /**
     * Deletes the light currently selected by the user
     * 
     * @param index The location in the array of the currently selected light
     */
    public void deleteActiveLight(int index)
    {
        // Create a new array with room for 1 less light
        LightObject[] newLights = new LightObject[lights.length - 1];
        
        // For each light...
        for (int i = 0; i < lights.length; i++)
        {
            // ...if the light to be deleted has already been reached...
            if (i > index)
            {
                // ...transfer over the light to 1 position less than its
                // original position.
                newLights[i - 1] = lights[i];
            }
            // ...otherwise, as long as it is not the light to be deleted...
            else if (i != index)
            {
                // ...transfer it over to the same position.
                newLights[i] = lights[i];
            }
        }
        
        lights = newLights;
    }
    
    /**
     * Gets the light currently chosen by the user in the choice box
     * 
     * @return The light currently chosen
     */
    public PointLight getActiveLight()
    {
        return activeLight.getLight();
    }
    
    /**
     * Gets the name of the currently chosen light
     * 
     * @return The chosen light's name
     */
    public String getActiveLightName()
    {
        return activeLight.getName();
    }
    
    /**
     * Gets the X position percentage of the light currently chosen
     * 
     * @return The chosen light's X position
     */
    public double getActiveLightX()
    {
        return activeLight.getPercentageX();
    }
    
    /**
     * Gets the Y position percentage of the light currently chosen
     * 
     * @return The chosen light's Y position
     */
    public double getActiveLightY()
    {
        return activeLight.getPercentageY();
    }
    
    /**
     * Gets the Z position percentage of the light currently chosen
     * 
     * @return The chosen light's Z position
     */
    public double getActiveLightZ()
    {
        return activeLight.getPercentageZ();
    }
    
    /**
     * Gets the color that each light is created as
     * 
     * @return The lights' initial color
     */
    public Color getDefaultColor()
    {
        return DEFAULT_COLOR;
    }
    
    /**
     * Gets the X position percentage that each light has upon creation
     * 
     * @return The lights' initial X position
     */
    public double getDefaultX()
    {
        return DEFAULT_X_PERCENTAGE;
    }
    
    /**
     * Gets the Y position percentage that each light has upon creation
     * 
     * @return The lights' initial Y position
     */
    public double getDefaultY()
    {
        return DEFAULT_Y_PERCENTAGE;
    }
    
    /**
     * Gets the Z position percentage that each light has upon creation
     * 
     * @return The lights' initial Z position
     */
    public double getDefaultZ()
    {
        return DEFAULT_Z_PERCENTAGE;
    }
    
    /**
     * Gets the light of the given index
     * 
     * @param index The index of which light to return
     * 
     * @return The light of the given index
     */
    public PointLight getLight(int index)
    {
        return lights[index].getLight();
    }
    
    /**
     * Gets the number of lights created by the user
     * 
     * @return The number of lights created by the user
     */
    public int getLightAmount()
    {
        return lights.length;
    }
    
    /**
     * Gets the name of the light at the given index
     * 
     * @param index The number of lights
     * 
     * @return The name of the light at the given index
     */
    public String getLightName(int index)
    {
        return lights[index].getName();
    }
    
    /**
     * Gets an array of all the point lights
     * 
     * @return An array of all the point lights
     */
    public PointLight[] getLights()
    {
        PointLight[] pointster = new PointLight[lights.length];
        
        for (int i = 0; i < lights.length; i++)
        {
            pointster[i] = lights[i].getLight();
        }
        
        return pointster;
    }
    
    /**
     * Gets whether or not a user-created light currently exists
     * 
     * @return Whether or not a user-created light currently exists
     */
    public boolean lightExists()
    {
        boolean lightExists = false;
        
        if (lights.length > 0)
        {
            lightExists = true;
        }
        
        return lightExists;
    }
    
    /**
     * Repositions the lights in relation to the mesh. For use for when the
     * shape of the mesh has changed.
     */
    public void repositionLights()
    {
        activeLight.setXPosition(furthest, centerX);
        activeLight.setYPosition(furthest, centerY);
        activeLight.setZPosition(furthest, centerZ);
    }
    
    /**
     * Set the currently active light
     * 
     * @param index The index of which light should become active
     */
    public void setActiveLight(int index)
    {
        activeLight = lights[index];
    }
    
    /**
     * Sets the color of the currently active light
     * 
     * @param colster The color of the light to be set
     */
    public void setActiveLightColor(Color colster)
    {
        activeLight.setColor(colster);
    }
    
    /**
     * Sets the currently selected light's X position.
     * 
     * @param x The percentage of where the light should be placed on the X
     * scale
     */
    public void setActiveLightX(double x)
    {
        activeLight.setXPosition(furthest, centerX, x);
    }
    
    /**
     * Sets the currently selected light's X position. For use when a string is
     * given.
     * 
     * @param x The percentage of where the light should be placed on the X
     * scale
     */
    public void setActiveLightX(String x)
    {
        double newX = Double.valueOf(x);
        
        activeLight.setXPosition(furthest, centerX, newX);
    }
    
    /**
     * Sets the currently selected light's Y position.
     * 
     * @param y The percentage of where the light should be placed on the Y
     * scale
     */
    public void setActiveLightY(double y)
    {
        activeLight.setYPosition(furthest, centerY, y);
    }
    
    /**
     * Sets the currently selected light's Y position. For use when a string is
     * given.
     * 
     * @param y The percentage of where the light should be placed on the Y
     * scale
     */
    public void setActiveLightY(String y)
    {
        double newY = Double.valueOf(y);
        
        activeLight.setYPosition(furthest, centerY, newY);
    }
    
    /**
     * Sets the currently selected light's Z position.
     * 
     * @param z The percentage of where the light should be placed on the Z
     * scale
     */
    public void setActiveLightZ(double z)
    {
        activeLight.setZPosition(furthest, centerZ, z);
    }
    
    /**
     * Sets the currently selected light's Z position. For use when a string is
     * given.
     * 
     * @param z The percentage of where the light should be placed on the Z
     * scale
     */
    public void setActiveLightZ(String z)
    {
        double newZ = Double.valueOf(z);
        
        activeLight.setZPosition(furthest, centerZ, newZ);
    }
    
    /**
     * Sets the furthest distance that a point may be on the mesh from the
     * mesh's center
     * 
     * @param distance The furthest distance that a point may be on the mesh
     * from the mesh's center
     */
    public void setFurthestPoint(double distance)
    {
        furthest = distance;
    }
    
    /**
     * Sets the approximate position of the center of the mesh
     * 
     * @param x The approximate center of the mesh on the X scale
     * @param y The approximate center of the mesh on the Y scale
     * @param z The approximate center of the mesh on the Z scale
     */
    public void setOrigin(double x, double y, double z)
    {
        centerX = x;
        centerY = y;
        centerZ = z;
    }
    
    /**
     * Gets a name of a light to create from the user
     */
    private Optional showNameDialog()
    {
        TextInputDialog nameDialog = new TextInputDialog("Light Name");
        
        nameDialog.setTitle("Create New Light");
        nameDialog.setHeaderText(null);
        nameDialog.setGraphic(null);
        nameDialog.setContentText("Enter a name for the light:");
        
        Optional<String> result = nameDialog.showAndWait();
        
        return result;
    }
}
