package tabs;

import graphics.Population;
import java.util.Optional;
import javafx.concurrent.Service;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controls the operations for the population tab
 * 
 * @author George Tiersma
 */
public class PopulationTab
{
    final private short DEFAULT_VERTEX_WIDTH = 5;
    final private short DEFAULT_VERTEX_HEIGHT = 5;
    
    final private int DEFAULT_DISPLACEMENT_STRENGTH = 5;
    
    final Image BLANK_IMAGE = new Image("graphics/unassignedWhite.png");
    
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
     * Gets whether or not at least 1 population has its service either ready to
     * be used or already in use.
     * 
     * @return Whether or not a population's service is ready for use
     */
    public boolean isServicePrepared()
    {
        boolean prepared = false;
        
        for (int i = 0; i < populations.length; i++)
        {
            if (populations[i].isServicePrepared())
            {
                prepared = true;
                
                i = populations.length;
            }
        }
        
        return prepared;
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
    public String createPopulation(short terrainWidth, short terrainDepth,
            short xRotate, short yRotate, float[] terrainPoints)
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
                // The title of the progress dialog
                String actionDescription = "Creating Population";
                
                // Create a new population.
                Population newPopulation = new Population(xRotate, yRotate,
                        terrainWidth, terrainDepth, DEFAULT_VERTEX_WIDTH,
                        DEFAULT_VERTEX_HEIGHT, DEFAULT_DISPLACEMENT_STRENGTH, 
                        name);
            
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
                activePopulation.load(actionDescription, terrainPoints);
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
        
        // Style the dialog
        DialogPane dister = alster.getDialogPane();
        dister.getStylesheets().add("design.css");
        
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        alster.setTitle("Population Already Exists");
        alster.setHeaderText("");
        alster.setContentText("A population with that name already exists.");
        
        alster.show();
    }
    
    /**
     * Gets the population currently-selected by the user
     * 
     * @return The active population
     */
    public Population getActivePopulation()
    {
        return activePopulation;
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
     * Gets the Population object at the given index
     * 
     * @param index The index of which Population to be returned
     * 
     * @return A Population object
     */
    public Population getPopulation(int index)
    {
        return populations[index];
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
     * Gets all of the Service objects belonging to all of the Populations
     * 
     * @return The populations' Service objects
     */
    public Service[] getServices()
    {
        Service[] services = new Service[populations.length];
        
        for (short i = 0; i < services.length; i++)
        {
            services[i] = populations[i].getService();
        }
        
        return services;
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
     * Sets the rotation on the X axis of all populations
     * 
     * @param angle The camera's vertical rotation value
     */
    public void setRotationX(short angle)
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
    public void setRotationY(short angle)
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
        
        // Style the dialog
        DialogPane dister = nameDialog.getDialogPane();
        dister.getStylesheets().add("design.css");
        
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
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
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void updateForTerrainDepthChange(short terrainDepth,
            float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.updateForTerrainDepthChange(terrainDepth, terrainPoints);
        }
    }
    
    /**
     * Recreates all of the populations based upon the new width of the terrain
     * 
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param terrainPoints The coordinates of the points used in the creation
     *                      of the terrain's mesh
     */
    public void updateForTerrainWidthChange(short terrainWidth,
            float[] terrainPoints)
    {
        for (Population population : populations)
        {
            population.updateForTerrainWidthChange(terrainWidth, terrainPoints);
        }
    }
}
