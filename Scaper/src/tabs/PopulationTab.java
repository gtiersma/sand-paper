package tabs;

import graphics.Population;
import graphics.TextureObject;
import java.util.Optional;
import javafx.concurrent.Service;
import javafx.scene.Group;
import javafx.scene.control.Alert;
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
     * Performs the tasks on the currently-selected population that are to be
     * performed immediately after the population's service is completed
     */
    public void concludeActivePopulationService()
    {
        activePopulation.concludeService();
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
            
            // As long as the name does not already exist...
            if (!isDuplicateName(name))
            {
                // ...create a new population.
                Population newPopulation = new Population(
                        DEFAULT_DISPLACEMENT_STRENGTH, terrainWidth,
                        terrainDepth, DEFAULT_VERTEX_WIDTH,
                        DEFAULT_VERTEX_HEIGHT, name);
            
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
            // ...otherwise, if the name given already exists...
            else
            {
                // ...display an error dialog.
                displayDuplicateNameError();
                
                name = "";
            }
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
     * Displays an error dialog box for when the user tries to create a new
     * population with the same name as a population that already exists
     */
    private void displayDuplicateNameError()
    {
        Alert alster = new Alert(Alert.AlertType.ERROR);
        
        alster.setTitle("Population Already Exists");
        alster.setHeaderText("");
        alster.setContentText("A population with that name already exists.");
        
        alster.show();
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
     * Gets the name of the shift map used by the currently-selected population
     * 
     * @return The name of the shift map
     */
    public String getActivePopulationShiftName()
    {
        return activePopulation.getShift().getName();
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
     * Gets the service that belongs to the currently-selected population
     * 
     * @return The active population's service
     */
    public Service getActivePopulationService()
    {
        return activePopulation.getService();
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
     * Gets the New Population name suggestion that should be presented to the
     * user when the "New Population" dialog is displayed
     * 
     * @return The New Population name suggestion
     */
    private String getNameSuggestion()
    {
        // The very first name suggestion to present the user with
        String firstSuggestion = "Population Name";
        
        // The name suggestion that should be used
        String suggestion = firstSuggestion;
        
        // If a population named with the first suggestion already exists...
        if (isDuplicateName(firstSuggestion))
        {
            // Each suggestion to be given after the first one
            String sequentialSuggestion = firstSuggestion;
            
            // Until a name is found that is not already in use by a
            // population...
            for (int i = 2; isDuplicateName(sequentialSuggestion); i++)
            {
                // ...construct a name suggestion with the next number.
                sequentialSuggestion = firstSuggestion + " " + i;
                
                // Make this suggestion the one to be used
                suggestion = sequentialSuggestion;
            }
        }
        
        return suggestion;
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
     * Gets whether or not the currently-selected population's service is
     * prepared for usage
     * 
     * @return Whether or not the service is prepared
     */
    public boolean isActivePopulationServicePrepared()
    {
        return activePopulation.isServicePrepared();
    }
    
    /**
     * Gets whether or not the given name is already a name of a population
     * 
     * @param name The name to be checked to see if it is a duplicate
     * 
     * @return Whether or not the given name is a duplicate
     */
    private boolean isDuplicateName(String name)
    {
        boolean duplicate = false;
        
        // For each population...
        for (Population population : populations)
        {
            // ...if its name matches this function's parameter...
            if (population.getName().equals(name))
            {
                // ...it is a duplicate.
                duplicate = true;
            }
        }
        
        return duplicate;
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
     * Recreates all populations with different randomly-generated numbers
     * 
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The array of the positions of each point on the
     *                      terrain
     */
    public void regeneratePopulations(double xRotate, double yRotate,
            float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.load(xRotate, yRotate, terrainPoints);
        }
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
     * Sets the currently-selected population's shift map
     * 
     * @param shift The shift map
     */
    public void setActivePopulationShift(TextureObject shift)
    {
        activePopulation.setShift(shift);
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
        TextInputDialog nameDialog = new TextInputDialog(getNameSuggestion());
        
        nameDialog.setTitle("Create New Population");
        nameDialog.setHeaderText(null);
        nameDialog.setGraphic(null);
        nameDialog.setContentText("Enter a name for the population:");
        
        Optional<String> result = nameDialog.showAndWait();
        
        return result;
    }
    
    /**
     * Recreates all of the populations based upon the new depth of the terrain
     * 
     * @param terrainDepth The depth of the terrain (measured in vertices)
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void updateForTerrainDepthChange(int terrainDepth, double xRotate,
            double yRotate, float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.updateForTerrainDepthChange(terrainDepth, xRotate,
                    yRotate, terrainPoints);
        }
    }
    
    /**
     * Recreates all of the populations based upon the new width of the terrain
     * 
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void updateForTerrainWidthChange(int terrainWidth, double xRotate,
            double yRotate, float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.updateForTerrainWidthChange(terrainWidth, xRotate,
                    yRotate, terrainPoints);
        }
    }
}
