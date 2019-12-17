
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Project Scaper - Allows graphic artists not familiar with 3D modeling to
 * easily create their own rendered images of a 3D model they create using only
 * 2D images.
 * 
 * @author George Tiersma
 */
public class Scaper extends Application
{
    /**
     * Starts the program
     * 
     * @param stagester The main stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stagester) throws Exception {
        
        Parent parster = FXMLLoader.load(getClass().getResource("GUI.fxml"));
    
        Scene scenster = new Scene(parster, 1200, 600);
        
        // Prepare the stage
        stagester.setTitle("Project Scaper v0.6");
        stagester.getIcons().add(new Image(Scaper.class.getResourceAsStream(
                "icons/icon.png")));
        stagester.setScene(scenster);
        stagester.show();
    
        showWelcome();
    }
    
    /**
     * Displays the welcome box. This box is only temporary and will be either
     * replaced with a different welcome box or removed entirely by the release
     * of v1.0.
     */
    public void showWelcome()
    {
        Alert alster = new Alert(Alert.AlertType.INFORMATION);
        
        alster.setTitle("Welcome to Project Scaper");
        alster.setHeaderText("Generate 3D meshes from 2D images");
        alster.setContentText("Project Scaper allows digital artists that are not familiar with 3D modeling to create their own 3D object and take a rendered screenshot of it for use in image editing programs such as Gimp and Adobe Photoshop.\n\nIt is still unfinished and is missing many useful features, however it is still operational.\n\nTo create your own 3D object, import your image maps on the Textures tab. There is a separate place for colored and grayscale images. Then load the maps on the Terrain tab. Finally, go to File - Save As to save an image of the object.\n\nNote: Unlike displacement maps used in most programs, Project Scaper uses color images for displacement maps. Red correlates to the Y axis, green correlates to the X acis and blue correlates to the Z axis.\n\nTry out the sample images in the root directory. Load \"castle\" as the displacement map and load \"castle_tex\" as the texture. These textures were quickly created within 5 minutes, yet they're capable of creating an entire 3D castle.");
        
        // Sets the icon of the dialog box
        ((Stage)alster.getDialogPane().getScene().getWindow())
                .getIcons().add(new Image("icons/icon.png"));
        
        alster.show();
    }
    
    /**
     * MAIN METHOD
     * 
     * @param args Does nothing
     */
    public static void main(System[] args)
    {
        launch();
    }
}
