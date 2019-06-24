/*
 * Programmer: Tiersma, George
 * Chemeketa Community College
 * April 26, 2019
 * Class: CIS234J
 * Assignment: Final Project
 * File Name: TextureObject.java
 */

import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 * This is the controller class for Project Scaper. This class programmatically
 * sets up the part of the scene that the FXML document is unable to. It also
 * uses event listeners and event methods for the controls that need them.
 * 
 * @author <a href= "mailto:gtiersma@my.chemeketa.edu" >George Tiersma</a>
 */
public class Controller
{
    // The width of the preview of the model
    final int PREVIEW_WIDTH = 800;
    // The height of the preview of the model
    final int PREVIEW_HEIGHT = 630;
    
    // Instantiate an object for each tab. Each of these objects control the
    // functionality relating to that tab.
    TexturesTab texTab = new TexturesTab();
    TerrainTab terTab = new TerrainTab();
    RenderTab renTab = new RenderTab();
    
    // Whether or not the controls on the form are currently "listening" for
    // actions. Setting this to false will disable most of the action listeners
    // until it is set to true again.
    boolean listen;
    
    // FXML apparently does not like SubScenes very much, so this control is
    // created entirely in this controller class.
    private SubScene preview;
    
    
    
    // Below are the controls taken from the FXML file. They are sorted
    // alphabetically by control type
    
    @FXML private ColorPicker renderColorBC;
    
    @FXML private ComboBox terrainComboDM;
    @FXML private ComboBox terrainComboT;
    @FXML private ComboBox terrainComboBM;
    @FXML private ComboBox terrainComboSM;
    
    @FXML private FlowPane texturesFlowC;
    @FXML private FlowPane texturesFlowG;
    
    @FXML private ImageView terrainImageDM;
    @FXML private ImageView terrainImageT;
    @FXML private ImageView terrainImageBM;
    @FXML private ImageView terrainImageSM;
    
    @FXML private Slider terrainSliderDMS;
    
    @FXML private Spinner<Integer> terrainSpinnerVRW;
    @FXML private Spinner<Integer> terrainSpinnerVRD;
    @FXML private Spinner<Integer> renderSpinnerRW;
    @FXML private Spinner<Integer> renderSpinnerRH;
    
    @FXML private SplitPane splitster;
    
    @FXML private Tab terrainTab;
    @FXML private Tab texturesTab;
    @FXML private Tab renderTab;

    /**
     * CONSTRUCTOR
     */
    public Controller()
    {
        listen = true;
    }
    
    /**
     * INITIALIZOR
     */
    public void initialize()
    {
        // Create the icons for the tabs
        renderTab.setGraphic(buildIcon("icons/render.png"));
        terrainTab.setGraphic(buildIcon("icons/terrain.png"));
        texturesTab.setGraphic(buildIcon("icons/textures.png"));
        
        // Loads the default values for the mesh
        terTab.prepareMesh();
        refreshPreview();
        
        // Listener for the spinner that sets the width of the vertex resolution
        terrainSpinnerVRW.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setWidth(newster);
            refreshPreview();
        });
        
        // Listener for the spinner that sets the depth of the vertex resolution
        terrainSpinnerVRD.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setDepth(newster);
            refreshPreview();
        });
        
        // Listener for the slider that sets the strength of the displacement
        // map
        terrainSliderDMS.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setDisplacementStrength((double) newster);
            refreshPreview();
        });
        
        // Listener for the spinner that sets the width of the rendered image
        renderSpinnerRW.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            renTab.setWidth(newster);
        });
        
        // Listener for the spinner that sets the height of the rendered image
        renderSpinnerRH.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            renTab.setHeight(newster);
        });
        
        // Listener for the color chooser that sets the background color
        renderColorBC.valueProperty().addListener((obster, oldster, newster) ->
        {
            renTab.setBackColor(newster);
            refreshPreview();
        });
        
    }
    
    /**
     * Gets a colored texture from the user through a file chooser
     * 
     * @param eventster The action event
     */
    @FXML
    protected void addColorTexture(ActionEvent eventster)
    {
        // If the user successfully supplied an image...
        if (texTab.addTexture(true))
        {
            // ...stop listening for action events.
            listen = false;
            
            // Add the texture that the user just chose to the pane
            texturesFlowC.getChildren().add(texTab.getLastView(true));
        
            // Create a list of the names of the color textures imported so far
            ObservableList<String> obster = texTab.getTextureNames(true);
            // Add the list to the combo boxes for maps that use color
            terrainComboT.setItems(obster);
            terrainComboDM.setItems(obster);
            terrainComboBM.setItems(obster);
            
            // Begin listening to action events again
            listen = true;
        }
    }
    
    /**
     * Gets a grayscale texture from the user through a file chooser
     * 
     * @param eventster The action event
     */
    @FXML
    protected void addGrayTexture(ActionEvent eventster)
    {
        // If the user successfully supplied an image...
        if (texTab.addTexture(false))
        {
            // ...stop listening for action events.
            listen = false;
            
            // Add the texture that the user just chose to the pane
            texturesFlowG.getChildren().add(texTab.getLastView(false));
        
            // Have the combo box for specular maps load a list of all of the
            // imported grayscale images
            terrainComboSM.setItems(texTab.getTextureNames(false));
            
            // Begin listening to action events again
            listen = true;
        }
    }
    
   /**
    * Gets an icon of the path of the image provided
    * 
    * @param pathster The path of the icon image
    * 
    * @return The icon
    */
    private static ImageView buildIcon(String pathster)
    {
        Image icon = new Image(pathster);
        ImageView viewster = new ImageView();
        
        viewster.setFitHeight(32);
        viewster.setFitWidth(32);
        viewster.setImage(icon);
        
        return viewster;
    }
    
    /**
     * Changes the displacement map to what is currently set in the displacement
     * combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeDisplacement(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box
            String name = terrainComboDM.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);
            
            terrainImageDM.setImage(imster);

            // Set the image as the displacement map
            terTab.setDisplacement(imster);

            refreshPreview();
        }
    }
    
    /**
     * Changes the texture to what is currently set in the texture combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeTexture(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box
            String name = terrainComboT.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);

            terrainImageT.setImage(imster);

            // Set the image as the texture
            terTab.setTexture(imster);

            refreshPreview();
        }
    }
    
    /**
     * Changes the bump map to what is currently set in the bump map combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeBump(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box
            String name = terrainComboBM.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);

            terrainImageBM.setImage(imster);
            
            // Set the image as the bump map
            terTab.setBump(imster);

            refreshPreview();
        }
    }
    
    /**
     * Changes the specular map to what is currently set in the specular map
     * combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeSpecular(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box
            String name = terrainComboSM.getValue().toString();
            
            // For some reason, if an ImageView of a grayscale image is
            // retrieved from the Texture Tab object, the image gains its color
            // again. To prevent that, the grayscale effect is declared here in
            // this class as well, and the adjustment is re-applied to the
            // ImageView.
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(-1);
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(false, name);
            
            terrainImageSM.setImage(imster);
            terrainImageSM.setEffect(grayscale);
            
            // Set the image as the specular map
            terTab.setSpecular(terrainImageSM.getImage());

            refreshPreview();
        }
    }
    
    /**
     * Exits the program if the user chooses to do so
     * 
     * @param eventster The action event
     */
    @FXML
    protected void exit(ActionEvent eventster)
    {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);

        confirmation.setTitle("Exit?");
        confirmation.setHeaderText("");
        confirmation.setContentText("Are you sure you want to exit?");
        
        // Sets the dialog box's icon
        ((Stage)confirmation.getDialogPane().getScene().getWindow()).getIcons()
                .add(new Image("icons/icon.png"));

        Optional<ButtonType> answer = confirmation.showAndWait();

        if (answer.get() == ButtonType.OK)
        {
            Platform.exit();
        }
    }
    
    /**
     * Shows the About box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void openAbout(ActionEvent eventster)
    {
        Alert alster = new Alert(AlertType.INFORMATION);
        
        alster.setTitle("About Project Scaper");
        alster.setHeaderText("Generates 3D meshes from 2D images");
        alster.setContentText("2019 - Created by George Tiersma\n\n"
        + "This program is not copyrighted. It can be used for any purpose"
        + " other than profited redistribution.\n\nversion 0.5 beta");
        
        // Sets the dialog box's icon
        ((Stage)alster.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        alster.showAndWait();
    }
    
    /**
     * Refreshes the preview of the mesh
     */
    @FXML
    protected void refreshPreview()
    {
        // Create a group containing the mesh
        Group groupster = terTab.getMesh();
        
        // Create the camera. This block of code is only temporary. I plan to
        // create a camera tab in Project Scaper soon, allowing the user to
        // define many of the camera's properties. Once I do, this block will be
        // removed.
        PerspectiveCamera perster = new PerspectiveCamera();
        perster.setTranslateX(400);
        perster.setTranslateY(-800);
        perster.setTranslateZ(-30);
        perster.setRotationAxis(Rotate.X_AXIS);
        perster.setRotate(-45);
        
        // Initialize the preview
        preview = new SubScene(groupster, PREVIEW_WIDTH, PREVIEW_HEIGHT, true,
                SceneAntialiasing.BALANCED);
        preview.setCamera(perster);
        preview.setFill(renTab.getBackColor());
        
        // Place the preview in the split pane in the scene
        splitster.getItems().set(0, preview);
    }
    
    /**
     * Resets all of the properties in the scene to their default values if the
     * user chooses to do so
     * 
     * @param eventster The action event
     */
    @FXML
    protected void reset(ActionEvent eventster)
    {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);

        confirmation.setTitle("Continue?");
        confirmation.setHeaderText("");
        confirmation.setContentText(
                "Are you sure you want to reset all of the settings?");

        Optional<ButtonType> answer = confirmation.showAndWait();

        // If the user chooses to continue...
        if (answer.get() == ButtonType.OK)
        {
            // ...stop listening to action events.
            listen = false;
            
            // Reset all of the variables in the tab objects
            texTab = new TexturesTab();
            terTab = new TerrainTab();
            renTab = new RenderTab();
            
            terrainComboDM.setItems(null);
            terrainComboT.setItems(null);
            terrainComboBM.setItems(null);
            terrainComboSM.setItems(null);
            
            terrainComboDM.setValue("");
            terrainComboT.setValue("");
            terrainComboBM.setValue("");
            terrainComboSM.setValue("");
            
            // Remove all imported textures
            for (int i = 1; i < texturesFlowC.getChildren().size(); i++)
            {
                texturesFlowC.getChildren().remove(i);
            }
            
            for (int i = 1; i < texturesFlowG.getChildren().size(); i++)
            {
                texturesFlowG.getChildren().remove(i);
            }
            
            terrainImageDM.setImage(null);
            terrainImageT.setImage(null);
            terrainImageBM.setImage(null);
            terrainImageSM.setImage(null);
            
            terrainSliderDMS.setValue(terTab.getDefaultStrength());
            
            terrainSpinnerVRW.getValueFactory().setValue(
                    terTab.getDefaultSize());
            terrainSpinnerVRD.getValueFactory().setValue(
                    terTab.getDefaultSize());
            renderSpinnerRW.getValueFactory().setValue(
                    renTab.getDefaultWidth());
            renderSpinnerRH.getValueFactory().setValue(
                    renTab.getDefaultHeight());
            
            // Set up the mesh
            terTab.prepareMesh();
            // Display the preview
            refreshPreview();
            
            // Begin listening to action events
            listen = true;
        }
    }
    
    /**
     * Saves a rendered image, replacing the last image that was saved. If there
     * is no image that was saved before this one, it functions the same as the
     * saveAs method.
     */
    @FXML
    protected void save()
    {
        // Set the dimensions of the preview to the resolution entered by the
        // user
        preview.setWidth(renTab.getWidth());
        preview.setHeight(renTab.getHeight());
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.save(writster);
        
        // Reset the dimensions of the preview
        preview.setWidth(PREVIEW_WIDTH);
        preview.setHeight(PREVIEW_HEIGHT);
    }
    
    /**
     * Has the user save a rendered image of the mesh through a file chooser
     */
    @FXML
    protected void saveAs()
    {
        // Set the dimensions of the preview to the resolution entered by the
        // user
        preview.setWidth(renTab.getWidth());
        preview.setHeight(renTab.getHeight());
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.saveAs(writster);
        
        // Reset the dimensions of the preview
        preview.setWidth(PREVIEW_WIDTH);
        preview.setHeight(PREVIEW_HEIGHT);
    }
}
