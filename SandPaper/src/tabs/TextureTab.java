package tabs;


import graphics.TextureObject;
import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controls the operations for the textures tab
 * 
 * @author George Tiersma
 */
public class TextureTab
{
    // The last directory that the user imported an image from
    private String previousDirectory;
    
    // The colored textures
    private ArrayList<TextureObject> colorTextures;
    // The grayscale textures
    private ArrayList<TextureObject> grayTextures;
    
    /**
     * CONSTRUCTOR
     */
    public TextureTab()
    {
        previousDirectory = System.getProperty("user.home");
        
        colorTextures = new ArrayList<>();
        grayTextures = new ArrayList<>();
    }
    
    /**
     * Gets a texture from the user through a file chooser and adds it to an
     * ArrayList of textures
     * 
     * @param mainStage Sand Paper's primary stage
     * @param color Whether or not the texture is colored or grayscale
     * 
     * @return Whether or not a texture was successfully added to a texture
     *         ArrayList
     */
    public boolean addTexture(Window mainStage, boolean color)
    {
        // Whether or not a texture has been successfully added to a texture
        // ArrayList
        boolean textureAdded = false;
        
        // The file retrieved from the file chooser
        File filster;
        
        // Prepare the file chooser
        FileChooser chooster = new FileChooser();
        FileChooser.ExtensionFilter imageExtensions
                = new FileChooser.ExtensionFilter(
                "Image Files (*.png, *.gif, *.jpg, *.mpo)", "*.png", "*.gif",
                "*.jpg", "*.jps", "*.mpo");
        FileChooser.ExtensionFilter allExtensions
                = new FileChooser.ExtensionFilter("All Files", "*");
        
        chooster.setTitle("Open Texture");
        chooster.setInitialDirectory(new File(previousDirectory));
        chooster.getExtensionFilters().add(imageExtensions);
        chooster.getExtensionFilters().add(allExtensions);
        
        try
        {
            // Get the file from the file chooser
            filster = chooster.showOpenDialog(mainStage);
        
            // If a file was retrieved...
            if (filster != null)
            {
                // ...create a new texture object out of it.
                TextureObject texster = new TextureObject(filster);

                // As long as the file being read is a valid image file...
                if (texster.isValid())
                {
                    // ...get the ArrayList of TextureObjects to which it will
                    // belong.
                    ArrayList<TextureObject> texsters = getTextures(color);
        
                    // Change its name if the name already exists
                    checkForDuplicateName(texster, texsters);
                    
                    // If the new texture is to be colored...
                    if (color)
                    {
                        // ...add it to that ArrayList.
                        colorTextures.add(texster);
                    }
                    // ...otherwise, it is to be gray...
                    else
                    {
                        // ...remove its color.
                        texster.removeColor();
                        grayTextures.add(texster);
                    }

                    // The texture has been successfully added
                    textureAdded = true;

                    // Get the directory that the texture came from
                    previousDirectory = filster.getAbsolutePath().substring(0,
                            filster.getAbsolutePath().lastIndexOf("\\") + 1);
                }
                else
                {
                    displayError("The image file is unreadable.");
                }
            }
        }
        // If something goes wrong...
        catch (Exception e)
        {
            displayError("The file chosen appears to not be an image.");
        }
        
        return textureAdded;
    }
    
    /**
     * Checks if the given TextureObject has a name that matches a texture that
     * is already present in the given ArrayList. It changes the given
     * TextureObject's name if a match is found.
     * 
     * @param texster The TextureObject which may need to have its name changed
     * @param texsters The ArrayList of TextureObjects to compare names from
     */
    private void checkForDuplicateName(TextureObject texster,
            ArrayList<TextureObject> texsters)
    {
        String name = texster.getName();
        
        // For each TextureObject in the ArrayList...
        for (int i = 0; i < texsters.size(); i++)
        {
            // ...get its name.
            String existingName = texsters.get(i).getName();
                
            // If the name matches...
            if (existingName.equals(name))
            {
                // ...rename the TextureObject.
                texster.rename();
                
                // Make sure none of the TextureObjects match the new name
                checkForDuplicateName(texster, texsters);
                    
                // Exit the loop
                i = texsters.size();
            }
        }
    }
    
    /**
     * Deletes an indicated texture.
     * 
     * It does not remove the texture's ImageView control. Make sure that is
     * removed first before the texture is deleted.
     * 
     * @param color Whether to delete a colored or a colorless texture
     * @param index The index of which texture to delete
     */
    public void deleteTexture(boolean color, short index)
    {
        getTextures(color).remove(index);
    }
    
    /**
     * Displays a dialog box describing an error
     * 
     * @param description A user-friendly description of the error
     */
    private void displayError(String description)
    {
        Alert alster = new Alert(Alert.AlertType.ERROR);
        
        // Style the dialog
        DialogPane dister = alster.getDialogPane();
        dister.getStylesheets().add("design.css");
        
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        alster.setTitle("Error");
        alster.setHeaderText("");
        alster.setContentText(description);
        
        alster.showAndWait();
    }
    
    /**
     * Gets the image view of the last texture imported
     * 
     * @param color Whether or not it is a colored image
     * 
     * @return The image view of the last texture imported
     */
    public ImageView getLastView(boolean color)
    {
        int textureAmount;
        
        ImageView viewster;
        
        ArrayList<TextureObject> texsters = getTextures(color);
        
        textureAmount = texsters.size();
        
        viewster = texsters.get(textureAmount - 1).getView();
        
        return viewster;
    }
    
    /**
     * Gets the indexes of the textures selected by the user 
     * 
     * @param color Whether to retrieve the indexes of the colored or the
     *              colorless textures
     * 
     * @return The indices of the selected textures
     */
    public ArrayList<Short> getSelectedIndices(boolean color)
    {
        // Number of textures
        int texturesAmount;
        
        ArrayList<Short> selectedIndices = new ArrayList<>();
        ArrayList<TextureObject> texsters = getTextures(color);
        
        texturesAmount = texsters.size();
        
        // For each texture...
        for (short i = 0; i < texturesAmount; i++)
        {
            // ...if it is selected...
            if (texsters.get(i).isSelected())
            {
                // ...get its index.
                selectedIndices.add(i);
            }
        }
        
        return selectedIndices;
    }
    
    /**
     * Gets an TextureObject by index
     * 
     * @param color Whether or not it is a colored image
     * @param index The index of the texture to be retrieved
     * 
     * @return The TextureObject
     */
    public TextureObject getTexture(boolean color, short index)
    {
        return getTextures(color).get(index);
    }
    
    /**
     * Gets a TextureObject by name
     * 
     * @param color Whether or not it is a colored image
     * @param name The name of the TextureObject to be returned
     * 
     * @return The TextureObject
     */
    public TextureObject getTexture(boolean color, String name)
    {
        int textureAmount;
        
        TextureObject texster = new TextureObject(1);
        
        ArrayList<TextureObject> texsters = getTextures(color);
        
        textureAmount = texsters.size();
        
        // For each TextureObject...
        for (int i = 0; i < textureAmount; i++)
        {
            // ...get it.
            TextureObject possibleMatch = texsters.get(i);
            
            // If that TextureObject's name matches...
            if (possibleMatch.getName().equals(name))
            {
                // ...get it.
                texster = possibleMatch;
                    
                // Exit the loop
                i = textureAmount;
            }
            // ...otherwise, if none of the TextureObjects had a matching
            // name...
            else if (i == textureAmount - 1)
            {
                // ...get a blank TextureObject.
                texster = new TextureObject(1);
            }
        }
        
        return texster;
    }
    
    /**
     * Gets a list of texture names
     * 
     * @param color Whether or not it is the colored textures being retrieved
     * 
     * @return The list of texture names
     */
    public ObservableList getTextureNames(boolean color)
    {
        int textureAmount;
        
        String[] names;
        
        ArrayList<TextureObject> texsters = getTextures(color);
        
        textureAmount = texsters.size();
        
        // Make enough room in the array for each name
        names = new String[textureAmount];
            
        // Get each name
        for (int i = 0; i < textureAmount; i++)
        {
            names[i] = texsters.get(i).getName();
        }
        
        // Turn the array into an observable list
        ObservableList<String> obster
                = FXCollections.observableArrayList(names);
        
        return obster;
    }
    
    /**
     * Gets the indicated array of TextureObjects
     * 
     * @param color If true, the colored textures will be retrieved. If false,
     *              the grayscale textures will be retrieved.
     * 
     * @return The indicated array of TextureObjects
     */
    private ArrayList<TextureObject> getTextures(boolean color)
    {
        // The texture object array to be checked
        ArrayList<TextureObject> texsters;
        
        // If the colored textures are to be checked...
        if (color)
        {
            // ...get them.
            texsters = colorTextures;
        }
        // ...otherwise...
        else
        {
            // ...get the gray ones.
            texsters = grayTextures;
        }
        
        return texsters;
    }
}
