package generics;


import javafx.concurrent.Service;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * A dialog box with a progress bar to show the progress of an action that the
 * program is performing
 * 
 * @author George Tiersma
 */
public class ProgressBarDialog
{
    // The text in the alert's title bar
    String title;
    
    Alert alster;
    
    ProgressBar progster;
    
    // The service that the progress bar is bound to
    Service servster;
    
    /**
     * CONSTRUCTOR
     * 
     * @param titster The text in the alert's title bar
     * @param servServ The service that the progress bar is bound to
     */
    public ProgressBarDialog(String titster, Service servServ)
    {
        title = titster;
        
        alster = new Alert(Alert.AlertType.NONE);
        
        progster = new ProgressBar();
        
        servster = servServ;
    }
    
    /**
     * Closes the alert
     */
    public void close()
    {
        alster.setResult(ButtonType.CANCEL);
        alster.close();
    }
    
    /**
     * Prepares the progress bar for use
     */
    private void load()
    {
        final int PROGRESS_BAR_WIDTH = 200;
        final int ALERT_WIDTH = 220;
        
        alster.setTitle(title);
        alster.setGraphic(progster);
        
        style();
        
        // Bind the progress bar to the service
        progster.progressProperty().bind(servster.progressProperty());
        
        // Set the cursor to the waiting cursor when the alert is hovered over
        alster.getDialogPane().setCursor(Cursor.WAIT);
        
        progster.setPrefWidth(PROGRESS_BAR_WIDTH);
        alster.getDialogPane().setPrefWidth(ALERT_WIDTH);
    }
    
    /**
     * Displays the alert
     */
    public void show()
    {
        load();
        alster.show();
    }
    
    /**
     * Sets the dialog's design to the stylesheet
     */
    private void style()
    {
        DialogPane dister = alster.getDialogPane();
        
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        dister.getStylesheets().add("design.css");
    }
}
