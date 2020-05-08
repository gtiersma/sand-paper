package tabs;


import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Controls the operations for the render tab
 * 
 * @author George Tiersma
 */
public class RenderTab
{
    final private int DEFAULT_WIDTH = 1920;
    final private int DEFAULT_HEIGHT = 1080;
    
    private int width;
    private int height;
    
    // The last directory that the user saved to
    private String previousDirectory;
    
    // The background color behind the mesh
    private Color backColor;
    
    // The last image that was saved in the current session
    private File openedImage;
    
    /**
     * CONSTRUCTOR
     */
    public RenderTab()
    {
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        
        previousDirectory = System.getProperty("user.home");
        
        backColor = Color.WHITE;
    }
    
    /**
     * Gets the background color
     * 
     * @return The background color
     */
    public Color getBackColor()
    {
        return backColor;
    }
    
    /**
     * Gets the default height of the render
     * 
     * @return The default height
     */
    public int getDefaultHeight()
    {
        return DEFAULT_HEIGHT;
    }
    
    /**
     * Gets the default width of the render
     * 
     * @return The default width
     */
    public int getDefaultWidth()
    {
        return DEFAULT_WIDTH;
    }
    
    /**
     * Gets the height of the render
     * 
     * @return The height
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * Gets the width of the render
     * 
     * @return The width
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Saves a rendered image. If the user has yet to choose a file save
     * location, executes the saveAs method instead.
     * 
     * @param writster A snapshot of the preview
     */
    public void save(WritableImage writster)
    {
        try
        {
            // Save, over-writing the last image
            ImageIO.write(SwingFXUtils.fromFXImage(writster, null), "png",
                    openedImage);
        }
        // If something goes wrong relating to an illegal argument...
        catch (IllegalArgumentException ex)
        {
            // ...execute saveAs instead.
            saveAs(writster);
        }
        // If the last image cannot be over-written...
        catch (IOException ex)
        {
            // ...alert the user to the problem.
            Alert error = new Alert(Alert.AlertType.ERROR);
        
            // Style the dialog
            DialogPane dister = error.getDialogPane();
            dister.getStylesheets().add("design.css");
        
            // Sets the icon of the dialog box
            ((Stage)dister.getScene().getWindow()).getIcons().add(
                    new Image("icons/icon.png"));

            error.setTitle("Unable to Save File");
            error.setHeaderText("");
            error.setContentText("The file cannot be saved. This could be due "
                    + "to a permissions conflict.");

            error.showAndWait();
        }
    }
    
    /**
     * Saves a rendered image to the location chosen by the user in a file
     * chooser.
     * 
     * @param writster A snapshot of the preview
     */
    public void saveAs(WritableImage writster)
    {
        // Set up the file chooser
        FileChooser chooster = new FileChooser();
        FileChooser.ExtensionFilter pngExtension
                = new FileChooser.ExtensionFilter("PNG File (*.png)", "*.png");
        FileChooser.ExtensionFilter allExtensions
                = new FileChooser.ExtensionFilter("All Files", "*");
        
        chooster.setTitle("Save Image Render");
        chooster.setInitialDirectory(new File(previousDirectory));
        chooster.getExtensionFilters().add(pngExtension);
        chooster.getExtensionFilters().add(allExtensions);
        
        // Get the file settings from the chooser
        openedImage = chooster.showSaveDialog(null);
        
        // As long as the user actually chose to save somewhere...
        if (openedImage != null)
        {
            // ...save the image.
            save(writster);
            
            // Get the directory that the user saved to
            previousDirectory = openedImage.getAbsolutePath();
        }
    }
    
    /**
     * Set the background color
     * 
     * @param colster The background color
     */
    public void setBackColor(Color colster)
    {
        backColor = colster;
    }
    
    /**
     * Set the preferred rendered image height
     * 
     * @param heightster The rendered image height
     */
    public void setHeight(int heightster)
    {
        height = heightster;
    }
    
    /**
     * Set the preferred rendered image width
     * 
     * @param widthster The rendered image width
     */
    public void setWidth(int widthster)
    {
        width = widthster;
    }
}
