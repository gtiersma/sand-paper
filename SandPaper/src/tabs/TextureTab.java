package tabs;


import graphics.TextureObject;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private TextureObject[] colorTextures;
    // The grayscale textures
    private TextureObject[] grayTextures;
    
    /**
     * CONSTRUCTOR
     */
    public TextureTab()
    {
        previousDirectory = System.getProperty("user.home");
        
        colorTextures = new TextureObject[0];
        grayTextures = new TextureObject[0];
    }
    
    /**
     * Gets a texture from the user through a file chooser and adds it to an
     * array of textures
     * 
     * @param color Whether or not the texture is colored or grayscale
     * 
     * @return Whether or not a texture was successfully added to a texture
     * array
     */
    public boolean addTexture(boolean color)
    {
        // Whether or not a texture has been successfully added to a texture
        // array
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
            filster = chooster.showOpenDialog(null);
        
            // If a file was retrieved...
            if (filster != null)
            {
                // ...create a new texture object out of it.
                TextureObject texster = new TextureObject(filster);

                // As long as the file being read is a valid image file...
                if (texster.isValid())
                {
                    // ...get the array of TextureObjects to which it will
                    // belong.
                    TextureObject[] texsters = getTextures(color);
        
                    // Change its name if the name already exists
                    checkForDuplicateName(texster, texsters);
                    
                    // If the new texture is to be colored...
                    if (color)
                    {
                        // ...add it to that array.
                        colorTextures = addToArray(texster, texsters);
                    }
                    // ...otherwise, it is to be gray...
                    else
                    {
                        // ...remove its color.
                        texster.removeColor();
                        grayTextures = addToArray(texster, texsters);
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
     * Adds a texture object to an array
     * 
     * @param texster The texture object to be added to the array
     * @param texsters The array of texture objects
     * 
     * @return The array of texture objects with the new texture object
     */
    private TextureObject[] addToArray(TextureObject texster,
            TextureObject[] texsters)
    {
        // The current size
        int size = texsters.length;
        
        // A new array with room for another texture object
        TextureObject[] newTexsters = new TextureObject[size + 1];
        
        // Transfer all of the texture objects to the new array
        System.arraycopy(texsters, 0, newTexsters, 0, size);
        
        // Add the new texture object
        newTexsters[size] = texster;
        
        return newTexsters;
    }
    
    /**
     * Checks if the given TextureObject has a name that matches a texture that
     * is already present in the given array. It changes the given
     * TextureObject's name if a match is found.
     * 
     * @param texster The TextureObject which may need to have its name changed
     * @param texsters The array of TextureObjects to compare names from
     */
    private void checkForDuplicateName(TextureObject texster,
            TextureObject[] texsters)
    {
        String name = texster.getName();
        
        // For each TextureObject in the array...
        for (int i = 0; i < texsters.length; i++)
        {
            // ...get its name.
            String existingName = texsters[i].getName();
                
            // If the name matches...
            if (existingName.equals(name))
            {
                // ...rename the TextureObject.
                texster.rename();
                
                // Make sure none of the TextureObjects match the new name
                checkForDuplicateName(texster, texsters);
                    
                // Exit the loop
                i = texsters.length;
            }
        }
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
     * Gets the image of the texture name provided
     * 
     * @param color Whether or not it is a colored texture
     * @param name The name of the texture to be received
     * 
     * @return The image of the texture
     */
    public Image getImageByName(boolean color, String name)
    {
        TextureObject texster = getTexture(color, name);
        
        return texster.getImage();
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
        ImageView viewster;
        
        TextureObject[] texsters = getTextures(color);
        
        viewster = texsters[texsters.length - 1].getView();
        
        return viewster;
    }
    
    /**
     * Gets a TextureObject
     * 
     * @param color Whether or not it is a colored image
     * @param name The name of the TextureObject to be returned
     * 
     * @return The TextureObject
     */
    public TextureObject getTexture(boolean color, String name)
    {
        int textureAmount;
        
        TextureObject texster = new TextureObject();
        
        TextureObject[] texsters = getTextures(color);
        
        textureAmount = texsters.length;
        
        // For each TextureObject...
        for (int i = 0; i < textureAmount; i++)
        {
            // ...if that TextureObject's name matches...
            if (texsters[i].getName().equals(name))
            {
                // ...get it.
                texster = texsters[i];
                    
                // Exit the loop
                i = textureAmount;
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
        
        TextureObject[] texsters = getTextures(color);
        
        textureAmount = texsters.length;
        
        // Make enough room in the array for each name
        names = new String[textureAmount];
            
        // Get each name
        for (int i = 0; i < textureAmount; i++)
        {
            names[i] = texsters[i].getName();
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
    private TextureObject[] getTextures(boolean color)
    {
        // The texture object array to be checked
        TextureObject[] texsters;
        
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
    
    /**
     * Gets whether or not there is a texture currently selected of the given
     * type
     * 
     * @param color Whether or not the colored texture type is to be checked
     * 
     * @return Whether or not a texture is selected
     */
    public boolean isTextureSelected(boolean color)
    {
        boolean textureSelected = false;
        
        // Number of textures
        int texturesAmount;
        
        TextureObject[] texsters = getTextures(color);
        
        texturesAmount = texsters.length;
            
        // For each texture...
        for (int i = 0; i < texturesAmount; i++)
        {
            // ...if it is selected...
            if (texsters[i].isSelected())
            {
                // ...a selected texture has been found.
                textureSelected = true;
                // Exit the loop
                i = texturesAmount;
            }
        }
        
        return textureSelected;
    }
}
