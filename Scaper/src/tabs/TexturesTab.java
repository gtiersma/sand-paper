package tabs;


import graphics.TextureObject;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controls the operations for the textures tab
 * 
 * @author George Tiersma
 */
public class TexturesTab
{
    final Image BLANK_IMAGE = new Image("graphics/blank.png");
    
    // The last directory that the user imported an image from
    private String previousDirectory;
    
    // The colored textures
    private TextureObject[] colorTextures;
    // The grayscale textures
    private TextureObject[] grayTextures;
    
    /**
     * CONSTRUCTOR
     */
    public TexturesTab()
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

                // If it is to be a colored texture...
                if (color)
                {
                    // ...add it to the array of colored textures.
                    colorTextures = addToArray(colorTextures, texster);
                }
                // ...otherwise...
                else
                {
                    // ...add it to the array of grayscale textures.
                    grayTextures = addToArray(grayTextures, texster);
                    // Remove its color
                    grayTextures[grayTextures.length - 1].removeColor();
                }

                // The texture has been successfully added
                textureAdded = true;

                // Get the directory that the texture came from
                previousDirectory = filster.getAbsolutePath().substring(0,
                        filster.getAbsolutePath().lastIndexOf("\\") + 1);
            }
        }
        // If something goes wrong...
        catch (Exception e)
        {
            // ...alert the user.
            Alert alster = new Alert(Alert.AlertType.ERROR);
        
            alster.setTitle("Error");
            alster.setHeaderText("");
            alster.setContentText(
                "The file chosen appears to not be an image.");
        
            alster.showAndWait();
        }
        
        return textureAdded;
    }
    
    /**
     * Adds a texture object to an array
     * 
     * @param texsters The array of texture objects
     * @param texster The texture object to be added to the array
     * 
     * @return The array of texture objects with the new texture object
     */
    private TextureObject[] addToArray(TextureObject[] texsters, TextureObject texster)
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
     * Gets the image of the texture name provided
     * 
     * @param color Whether or not it is a colored texture
     * @param name The name of the texture to be received
     * 
     * @return The image of the texture
     */
    public Image getImageByName(boolean color, String name)
    {
        Image imster = BLANK_IMAGE;
        
        // If the texture is colored...
        if (color)
        {
            // ...for each colored texture object...
            for (int i = 0; i < colorTextures.length; i++)
            {
                // ...if that texture objects name matches...
                if (colorTextures[i].getName().equals(name))
                {
                    // ...get that image.
                    imster = colorTextures[i].getImage();
                    
                    // Exit the loop
                    i = colorTextures.length;
                }
            }
        }
        // ...otherwise, if it's grayscale...
        else
        {
            // ...for each grayscaled texture object...
            for (int i = 0; i < grayTextures.length; i++)
            {
                // ...if that texture objects name matches...
                if (grayTextures[i].getName().equals(name))
                {
                    // ...get that image.
                    imster = grayTextures[i].getImage();
                    
                    // Exit the loop
                    i = grayTextures.length;
                }
            }
        }
        
        return imster;
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
        
        if (color)
        {
            viewster = colorTextures[colorTextures.length - 1].getView();
        }
        else
        {
            viewster = grayTextures[grayTextures.length - 1].getView();
        }
        
        return viewster;
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
        String[] names;
        
        // If the texture names should be of the colored textures...
        if (color)
        {
            // ...make enough room in the array for each name.
            names = new String[colorTextures.length];
            
            // Get each name
            for (int i = 0; i < colorTextures.length; i++)
            {
                names[i] = colorTextures[i].getName();
            }
        }
        // ...otherwise, if the names should be of the grayscale textures...
        else
        {
            // ...make enough room in the array for each name.
            names = new String[grayTextures.length];
            
            // Get each name
            for (int i = 0; i < grayTextures.length; i++)
            {
                names[i] = grayTextures[i].getName();
            }
        }
        
        // Turn the array into an observable list
        ObservableList<String> obster
                = FXCollections.observableArrayList(names);
        
        return obster;
    }
}
