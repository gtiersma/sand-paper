
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
    public void start(Stage stagester) throws Exception
    {
        final int WINDOW_WIDTH = 1200;
        final int WINDOW_HEIGHT = 600;
        
        Parent parster = FXMLLoader.load(getClass().getResource("GUI.fxml"));
    
        Scene scenster = new Scene(parster, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scenster.getStylesheets().add("design.css");
        
        // Prepare the stage
        stagester.setTitle("Sand Paper v0.6");
        stagester.getIcons().add(new Image(Scaper.class.getResourceAsStream(
                "icons/icon.png")));
        stagester.setScene(scenster);
        stagester.setResizable(false);
        stagester.show();
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
