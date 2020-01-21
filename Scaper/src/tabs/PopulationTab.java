package tabs;

import graphics.Population;
import graphics.TextureObject;
import java.util.Optional;
import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;

/**
 * Controls the operations for the population tab
 * 
 * @author George Tiersma
 */
public class PopulationTab
{
    final private int DEFAULT_DISPLACEMENT_STRENGTH = 5;
    
    final private int DEFAULT_VERTEX_WIDTH = 5;
    final private int DEFAULT_VERTEX_HEIGHT = 5;
    
    final Image BLANK_IMAGE = new Image("graphics/blank.png");
    
    // The Population that the user currently has selected in the Population
    // selection combo box
    private Population activePopulation;
    
    // All of the populations
    private Population populations[];
    
    /**
     * CONSTRUCTOR
     */
    public PopulationTab()
    {
        activePopulation = null;
        
        populations = new Population[0];
    }
    
    /**
     * Creates a new population, asking the user for the name
     * 
     * @param terrainWidth The width of the terrain (Measured in vertices)
     * @param terrainDepth The depth of the terrain (Measured in vertices)
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The coordinates of the points used to create the
     *                      terrain's mesh
     * 
     * @return The name of the newly created population. If the user canceled
     *         the population creation, then a blank string is returned.
     */
    public String createPopulation(int terrainWidth, int terrainDepth,
            double xRotate, double yRotate, float[] terrainPoints)
    {
        String name = "";
        
        // Get the name from the user
        Optional<String> result = showNameDialog();
        
        // As long as a name was given...
        if (result.isPresent())
        {
            // ...get it.
            name = result.get();
            
            // Create a new population
            Population newPopulation = new Population(
                    DEFAULT_DISPLACEMENT_STRENGTH, terrainWidth, terrainDepth,
                    DEFAULT_VERTEX_WIDTH, DEFAULT_VERTEX_HEIGHT, name);
            
            // Create a new array with room for another population
            Population[] newPopulations
                    = new Population[populations.length + 1];
        
            // Copy the old array into the new one
            System.arraycopy(populations, 0, newPopulations, 0,
                    populations.length);
            
            // Make the old array the new one
            populations = newPopulations;
            
            // Make the new population be the currently-selected population
            activePopulation = newPopulation;
            
            // Add the new population to the array
            populations[populations.length - 1] = newPopulation;
            
            // Prepare the new population
            activePopulation.load(xRotate, yRotate, terrainPoints);
        }
        
        return name;
    }
    
    /**
     * Deletes the currently-selected population
     */
    public void deleteActivePopulation()
    {
        int index = getActivePopulationIndex();
        
        // Create a new array with room for 1 less population
        Population[] newPopulations = new Population[populations.length - 1];
        
        // For each population...
        for (int i = 0; i < populations.length; i++)
        {
            // ...if the population to be deleted has already been reached...
            if (i > index)
            {
                // ...transfer over the this population to 1 index less than
                // its original index.
                newPopulations[i - 1] = populations[i];
            }
            // ...otherwise, as long as it is not the population to be
            // deleted...
            else if (i != index)
            {
                // ...transfer it over to the same index.
                newPopulations[i] = populations[i];
            }
        }
        
        populations = newPopulations;
    }
    
    /**
     * Gets the name of the bump map used by the currently-selected population
     * 
     * @return The name of the bump map
     */
    public String getActivePopulationBumpName()
    {
        return activePopulation.getBumpMap().getName();
    }
    
    /**
     * Gets the name of 1 of the displacement maps used in the displacement
     * range for the currently-selected population
     * 
     * @param first Whether or not the name being returned should be from the
     *              first displacement map. If false, the name of the second
     *              displacement map will be returned.
     * 
     * @return The name of 1 of the displacement maps
     */
    public String getActivePopulationDisplacementName(boolean first)
    {
        String name;
        
        if (first)
        {
            name = activePopulation.getFirstDisplacement().getName();
        }
        else
        {
            name = activePopulation.getSecondDisplacement().getName();
        }
        
        return name;
    }
    
    /**
     * Gets the displacement strength of the currently-selected population
     * 
     * @return The displacement strength
     */
    public int getActivePopulationDisplacementStrength()
    {
        return activePopulation.getDisplacementStrength();
    }
    
    /**
     * Gets the name of the elevation map used by the currently-selected
     * population
     * 
     * @return The name of the elevation map
     */
    public String getActivePopulationElevationName()
    {
        return activePopulation.getElevation().getName();
    }
    
    /**
     * Gets the name of the map used to determine the height of each Individual
     * 
     * @return The name of the map used to determine the height
     */
    public String getActivePopulationHeightName()
    {
        return activePopulation.getHeight().getName();
    }
    
    /**
     * Gets the index of the currently-selected population in the populations
     * array
     * 
     * @return The index of the currently-selected population
     */
    private int getActivePopulationIndex()
    {
        int index = 0;
        
        // For each population...
        for (int i = 0; i < populations.length; i++)
        {
            // ...if this population is the same as the active population...
            if (activePopulation == populations[i])
            {
                // ...get its index.
                index = i;
                
                // Exit the loop
                i = populations.length;
            }
        }
        
        return index;
    }
    
    /**
     * Gets the name of the currently-selected population
     * 
     * @return The name of the currently-selected population
     */
    public String getActivePopulationName()
    {
        return activePopulation.getName();
    }
    
    /**
     * Gets the name of the placement map used by the currently-selected
     * population
     * 
     * @return The name of the placement map
     */
    public String getActivePopulationPlacementName()
    {
        return activePopulation.getPlacement().getName();
    }
    
    /**
     * Gets the name of the specular map used by the currently-selected
     * population
     * 
     * @return The name of the specular map
     */
    public String getActivePopulationSpecularName()
    {
        return activePopulation.getSpecularMap().getName();
    }
    
    /**
     * Gets the name of the texture used by the currently-selected population
     * 
     * @return The name of the texture
     */
    public String getActivePopulationTextureName()
    {
        return activePopulation.getTexture().getName();
    }
    
    /**
     * Gets the height of the Individuals in the currently-selected population
     * (Measured in vertices)
     * 
     * @return The height of the currently-selected population (Measured in
     *         vertices)
     */
    public int getActivePopulationVertexHeight()
    {
        return activePopulation.getVertexHeight();
    }
    
    /**
     * Gets the width of the Individuals in the currently-selected population
     * (Measured in vertices)
     * 
     * @return The width of the currently-selected population (Measured in
     *         vertices)
     */
    public int getActivePopulationVertexWidth()
    {
        return activePopulation.getVertexWidth();
    }
    
    /**
     * Gets the name of the map used to determine the width of each Individual
     * 
     * @return The name of the map used to determine the width
     */
    public String getActivePopulationWidthName()
    {
        return activePopulation.getWidth().getName();
    }
    
    /**
     * Gets the initial displacement strength that a new population is created
     * with
     * 
     * @return The initial displacement strength
     */
    public int getDefaultDisplacementStrength()
    {
        return DEFAULT_DISPLACEMENT_STRENGTH;
    }
    
    /**
     * Gets the initial height (measured in vertices) that a new population is
     * created with
     * 
     * @return The initial height
     */
    public int getDefaultVertexHeight()
    {
        return DEFAULT_VERTEX_HEIGHT;
    }
    
    /**
     * Gets the initial width (measured in vertices) that a new population is
     * created with
     * 
     * @return The initial width
     */
    public int getDefaultVertexWidth()
    {
        return DEFAULT_VERTEX_WIDTH;
    }
    
    /**
     * Gets the population at the given index
     * 
     * @param index The index of which population to be returned
     * 
     * @return A population
     */
    public Group getPopulation(int index)
    {
        return populations[index].getPopulation();
    }
    
    /**
     * Gets the number of populations
     * 
     * @return The number of populations
     */
    public int getPopulationAmount()
    {
        return populations.length;
    }
    
    /**
     * (Re)constructs the currently-selected population
     * 
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void loadActivePopulation(double xRotate, double yRotate,
            float[] terrainPoints)
    {
        activePopulation.load(xRotate, yRotate, terrainPoints);
    }
    
    /**
     * (Re)constructs all populations
     * 
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void loadPopulations(double xRotate, double yRotate,
            float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.load(xRotate, yRotate, terrainPoints);
        }
    }
    
    /**
     * Gets whether or not at least 1 population currently exists
     * 
     * @return Whether or not a population exists
     */
    public boolean populationExists()
    {
        boolean exists = true;
        
        if (populations.length == 0)
        {
            exists = false;
        }
        
        return exists;
    }
    
    /**
     * Repositions the populations
     * 
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void repositionPopulations(float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.reposition(terrainPoints);
        }
    }
    
    /**
     * Sets the active population to the index given
     * 
     * @param index The index of the population to set as active
     */
    public void setActivePopulation(int index)
    {
        activePopulation = populations[index];
    }
    
    /**
     * Sets the currently-selected population's bump map
     * 
     * @param bump The bump map
     */
    public void setActivePopulationBump(TextureObject bump)
    {
        activePopulation.setBumpMap(bump);
    }
    
    /**
     * Sets the displacement strength of the currently-selected population
     * 
     * @param strength The displacement strength
     */
    public void setActivePopulationDisplacementStrength(int strength)
    {
        activePopulation.setDisplacementStrength(strength);
    }
    
    /**
     * Sets the currently-selected population's elevation map
     * 
     * @param elevation The elevation map
     */
    public void setActivePopulationElevation(TextureObject elevation)
    {
        activePopulation.setElevation(elevation);
    }
    
    /**
     * Sets the first displacement map used in the currently-selected
     * population's displacement range
     * 
     * @param displacement A displacement map
     */
    public void setActivePopulationFirstDisplacement(TextureObject displacement)
    {
        activePopulation.setFirstDisplacement(displacement);
    }
    
    /**
     * Sets the map used to determine the height of the Individuals in the
     * currently-selected population
     * 
     * @param height A map used to determine the height
     */
    public void setActivePopulationHeight(TextureObject height)
    {
        activePopulation.setHeight(height);
    }
    
    /**
     * Sets the currently-selected population's placement map
     * 
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param placement The placement map
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void setActivePopulationPlacement(double xRotate, double yRotate,
            TextureObject placement, float terrainPoints[])
    {
        activePopulation.setPlacement(xRotate, yRotate, terrainPoints,
                placement);
    }
    
    /**
     * Sets the second displacement map used in the currently-selected
     * population's displacement range
     * 
     * @param displacement A displacement map
     */
    public void setActivePopulationSecondDisplacement(
            TextureObject displacement)
    {
        activePopulation.setSecondDisplacement(displacement);
    }
    
    /**
     * Sets the currently-selected population's specular map
     * 
     * @param specular The specular map
     */
    public void setActivePopulationSpecular(TextureObject specular)
    {
        activePopulation.setSpecularMap(specular);
    }
    
    /**
     * Sets the currently-selected population's texture
     * 
     * @param texture The texture
     */
    public void setActivePopulationTexture(TextureObject texture)
    {
        activePopulation.setTexture(texture);
    }
    
    /**
     * Sets the height (measured in vertices) of the Individuals in the
     * currently-selected population
     * 
     * @param height The height of the Individuals (measured in vertices)
     */
    public void setActivePopulationVertexHeight(int height)
    {
        activePopulation.setVertexHeight(height);
    }
    
    /**
     * Sets the height (measured in vertices) of the Individuals in the
     * currently-selected population
     * 
     * @param height The height of the Individuals (measured in vertices)
     */
    public void setActivePopulationVertexHeight(String height)
    {
        int heightster = Integer.parseInt(height);
        
        activePopulation.setVertexHeight(heightster);
    }
    
    /**
     * Sets the width (measured in vertices) of the Individuals in the
     * currently-selected population
     * 
     * @param width The width of the Individuals (measured in vertices)
     */
    public void setActivePopulationVertexWidth(int width)
    {
        activePopulation.setVertexWidth(width);
    }
    
    /**
     * Sets the width (measured in vertices) of the Individuals in the
     * currently-selected population
     * 
     * @param width The width of the Individuals (measured in vertices)
     */
    public void setActivePopulationVertexWidth(String width)
    {
        int widthster = Integer.parseInt(width);
        
        activePopulation.setVertexWidth(widthster);
    }
    
    /**
     * Sets the map used to determine the width of the Individuals in the
     * currently-selected population
     * 
     * @param width A map used to determine the width
     */
    public void setActivePopulationWidth(TextureObject width)
    {
        activePopulation.setWidth(width);
    }
    
    /**
     * Sets the rotation on the X axis of all populations
     * 
     * @param angle The camera's vertical rotation value
     */
    public void setRotationX(double angle)
    {
        for (Population population : populations)
        {
            population.setRotationX(angle);
        }
    }
    
    /**
     * Sets the rotation on the Y axis of all populations
     * 
     * @param angle The camera's horizontal rotation value
     */
    public void setRotationY(double angle)
    {
        for (Population population : populations)
        {
            population.setRotationY(angle);
        }
    }
    
    /**
     * Displays a dialog box for creating a new population
     * 
     * @return The result of the dialog box
     */
    private Optional showNameDialog()
    {
        TextInputDialog nameDialog = new TextInputDialog("Population Name");
        
        nameDialog.setTitle("Create New Population");
        nameDialog.setHeaderText(null);
        nameDialog.setGraphic(null);
        nameDialog.setContentText("Enter a name for the population:");
        
        Optional<String> result = nameDialog.showAndWait();
        
        return result;
    }
}
