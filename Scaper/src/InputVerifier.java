
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Validates user input
 * 
 * @author George Tiersma
 */
public class InputVerifier
{
    // The smallest vertex size that the terrain and populations can be
    final int MIN_MESH_SIZE = 2;
    
    final int MAX_TERRAIN_SIZE = 1000;
    final int MAX_POPULATION_SIZE = 100;
    
    final int MAX_RESOLUTION_DIGIT_AMOUNT = 5;
    
    /**
     * CONSTRUCTOR
     */
    public InputVerifier() {}
    
    
    /**
     * Displays an error for when the user enters a value that is so large that
     * it runs a risk of overloading the program
     * 
     * @param maxSize The largest allowed value
     */
    private void displayTooBigError(int maxSize)
    {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        
        DialogPane dister = errorDialog.getDialogPane();
        
        dister.getStylesheets().add("design.css");
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));

        errorDialog.setTitle("Number Too Large");
        errorDialog.setHeaderText("");
        errorDialog.setContentText("The number entered is too large. To help"
                + " prevent the possibility of the program overloading, the"
                + " largest number that is allowed is " + maxSize + ".");
        
        errorDialog.show();
    }
    
    /**
     * Checks if a given vertex size is valid for the terrain or a population
     * 
     * @param maxSize The largest allowed size
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    private boolean isMeshSizeValid(int maxSize, int size)
    {
        boolean valid = false;
        
        // If the size is not too big...
        if (size <= maxSize)
        {
            // ...and it is not too small...
            if (size >= MIN_MESH_SIZE)
            {
                // ...it is valid.
                valid = true;
            }
        }
        // ...but if the size is too large...
        else
        {
            // ...tell the user what the problem is.
            displayTooBigError(maxSize);
        }
        
        return valid;
    }
    
    /**
     * Checks if a given resolution size is valid
     * 
     * @param resolution The given resolution. This parameter is a String
     *                   instead of an Integer because the input is meant to be
     *                   the text of a spinner.
     * 
     * @return Whether or not the resolution size is valid
     */
    public boolean isResolutionValid(String resolution)
    {
        boolean valid = true;
        
        // If the resolution is blank or has more digits than it should...
        if (resolution.equals("")
                || resolution.length() > MAX_RESOLUTION_DIGIT_AMOUNT)
        {
            // ...it is not valid.
            valid = false;
        }
        // ...otherwise...
        else
        {
            // ...for each character in the resolution...
            for (int i = 0; i < resolution.length(); i++)
            {
                // ...if it is not a digit...
                if (!Character.isDigit(resolution.charAt(i)))
                {
                    // ...the resolution is not valid.
                    valid = false;
                    
                    // There is no use in checking the other characters, so exit
                    // the loop
                    i = resolution.length();
                }
            }
        }
        
        return valid;
    }
    
    /**
     * Checks if a given vertex size is valid for a population
     * 
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    public boolean isPopulationSizeValid(int size)
    {
        return isMeshSizeValid(MAX_POPULATION_SIZE, size);
    }
    
    /**
     * Checks if a given vertex size is valid for the terrain
     * 
     * @param size The vertex size
     * 
     * @return Whether or not the size is valid
     */
    public boolean isTerrainSizeValid(int size)
    {
        return isMeshSizeValid(MAX_TERRAIN_SIZE, size);
    }
}
