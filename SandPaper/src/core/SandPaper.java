package core;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Project SandPaper - Allows graphic artists not familiar with 3D modeling to
 easily create their own rendered images of a 3D model they create using only
 2D images.
 * 
 * @author George Tiersma
 */
public class SandPaper extends Application
{
    /**
     * MAIN METHOD
     * 
     * @param args Does nothing
     */
    public static void main(System[] args)
    {
        launch();
    }
    
    /**
     * Starts the program
     * 
     * @param stagester The main stage
     */
    @Override
    public void start(Stage stagester)
    {
        final int MIN_WINDOW_SIZE = 400;
        final int WINDOW_WIDTH = 1200;
        final int WINDOW_HEIGHT = 600;
        
        try
        {
            // The icon for Sand Paper's title bar
            Image icon = new Image(
                    SandPaper.class.getResourceAsStream("/icons/icon.png"));
            
            Parent parster
                    = FXMLLoader.load(getClass().getResource("GUI.fxml"));
    
            Scene scenster = new Scene(parster, WINDOW_WIDTH, WINDOW_HEIGHT);
        
            scenster.getStylesheets().add("design.css");
        
            // Prepare the stage
            stagester.setMinHeight(MIN_WINDOW_SIZE);
            stagester.setMinWidth(MIN_WINDOW_SIZE);
            stagester.setTitle("Sand Paper v0.8");
            stagester.getIcons().add(icon);
            stagester.setScene(scenster);
            stagester.show();
        }
        catch (IOException ex)
        {
            showLaunchError();
            Logger.getLogger(SandPaper.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }
    
    /**
     * Displays an error for if Sand Paper fails to load the fxml file
     */
    private void showLaunchError()
    {
        Alert alster = new Alert(Alert.AlertType.ERROR);
        
        alster.setTitle("Fatal Error");
        alster.setHeaderText("Unable to Load FXML");
        alster.setContentText("Sand Paper is unable to load the user "
                + "interface, so it will terminate.");
        
        alster.setGraphic(new ImageView("/icons/icon.png"));
        
        alster.showAndWait();
    }
}
