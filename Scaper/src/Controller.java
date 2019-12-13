

import tabs.TexturesTab;
import tabs.TerrainTab;
import tabs.RenderTab;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import tabs.CameraTab;
import tabs.LightTab;

/**
 * This is the controller class for Project Scaper. This class programmatically
 * sets up the part of the scene that the FXML document is unable to. It also
 * uses event listeners and event methods for the controls that need them.
 * 
 * @author George Tiersma
 */
public class Controller
{
    // The width of the previewItems of the model
    final int PREVIEW_WIDTH = 800;
    // The height of the previewItems of the model
    final int PREVIEW_HEIGHT = 400;
    
    // Instantiate an object for each tab. Each of these objects control the
    // functionality relating to that tab.
    TexturesTab texTab;
    TerrainTab terTab;
    RenderTab renTab;
    CameraTab camTab;
    LightTab ligTab;
    
    // Whether or not the controls on the form are currently "listening" for
    // actions. Setting this to false will disable most of the action listeners
    // until it is set to true again.
    boolean listen;
    
    // FXML apparently does not like SubScenes very much, so this control is
    // created entirely in this controller class.
    private SubScene preview;
    
    
    
    // Below are the controls taken from the FXML file. They are sorted
    // alphabetically by control type
    
    @FXML private Button lightButtonLD;
    @FXML private Button lightButtonLN;
    
    @FXML private ChoiceBox lightChoiceL;
    
    @FXML private ColorPicker renderColorBC;
    @FXML private ColorPicker lightColorC;
    
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
    
    @FXML private RadioButton cameraRadioFOVH;
    @FXML private RadioButton cameraRadioFOVV;
    
    @FXML private Slider terrainSliderDMS;
    @FXML private Slider cameraSliderAH;
    @FXML private Slider cameraSliderAV;
    @FXML private Slider cameraSliderFOVD;
    
    @FXML private Spinner<Integer> terrainSpinnerVRW;
    @FXML private Spinner<Integer> terrainSpinnerVRD;
    @FXML private Spinner<Integer> renderSpinnerRW;
    @FXML private Spinner<Integer> renderSpinnerRH;
    @FXML private Spinner<Integer> cameraSpinnerPAH;
    @FXML private Spinner<Integer> cameraSpinnerPAV;
    @FXML private Spinner<Integer> cameraSpinnerPAZ;
    @FXML private Spinner<Double> lightSpinnerPX;
    @FXML private Spinner<Double> lightSpinnerPY;
    @FXML private Spinner<Double> lightSpinnerPZ;
    
    @FXML private SplitPane splitster;
    
    @FXML private Tab terrainTab;
    @FXML private Tab texturesTab;
    @FXML private Tab renderTab;

    /**
     * CONSTRUCTOR
     */
    public Controller()
    {
        ligTab = new LightTab();
        camTab = new CameraTab();
        renTab = new RenderTab();
        terTab = new TerrainTab();
        texTab = new TexturesTab();
        
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
        
        preview = new SubScene(new Group(), PREVIEW_WIDTH, PREVIEW_HEIGHT, true,
                SceneAntialiasing.BALANCED);
        // Place the previewItems in the split pane in the scene
        splitster.getItems().set(0, preview);
        
        preparePreview();
        
        // Create group for field of view radio buttons
        ToggleGroup cameraRadiosFOV = new ToggleGroup();
        cameraRadioFOVH.setToggleGroup(cameraRadiosFOV);
        cameraRadioFOVV.setToggleGroup(cameraRadiosFOV);
        
        
        
        //----------------------------------------------------------------------
        // Terrain Tab Listeners
        //----------------------------------------------------------------------
        terrainSpinnerVRW.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setWidth(newster);
            
            refreshPreview();
        });
        // The second listeners for the spinners are executed when the value is
        // changed and then the focus is left from the control. This particular
        // action is not triggered by the first listener.
        terrainSpinnerVRW.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                terTab.setWidth(terrainSpinnerVRW.getEditor().getText());
            
                refreshPreview();
            }
        });
        
        terrainSpinnerVRD.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setDepth(newster);
            
            refreshPreview();
        });
        terrainSpinnerVRD.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                terTab.setDepth(terrainSpinnerVRD.getEditor().getText());
            
                refreshPreview();
            }
        });
        
        terrainSliderDMS.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.setDisplacementStrength(newster.floatValue());
            
            refreshPreview();
        });
        
        //----------------------------------------------------------------------
        // Render Tab Listeners
        //----------------------------------------------------------------------
        renderColorBC.valueProperty().addListener((obster, oldster, newster) ->
        {
            renTab.setBackColor(newster);
            preview.setFill(renTab.getBackColor());
        });
        
        //----------------------------------------------------------------------
        // Camera Tab Listeners
        //----------------------------------------------------------------------
        cameraSliderAH.valueProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                camTab.setHorizontalAngle(newster.doubleValue());
            
                preview.setRoot(getPreview());
            }
        });
        
        cameraSliderAV.valueProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                camTab.setVerticalAngle(newster.doubleValue());
            
                preview.setRoot(getPreview());
            }
        });
        
        cameraSpinnerPAH.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            camTab.setXAdjustment(newster);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAH.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                camTab.setXAdjustment(cameraSpinnerPAH.getEditor().getText());
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSpinnerPAV.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            camTab.setYAdjustment(newster);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAV.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                camTab.setYAdjustment(cameraSpinnerPAV.getEditor().getText());
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSpinnerPAZ.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            camTab.setZoom(newster);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAZ.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                camTab.setZoom(cameraSpinnerPAZ.getEditor().getText());
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSliderFOVD.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            camTab.setFieldOfView(newster.doubleValue());
            
            preview.setCamera(camTab.getCamera());
        });
        
        cameraRadioFOVH.setOnAction((evster) ->
        {
            camTab.setOrientation(cameraRadioFOVH.isSelected());
            
            preview.setCamera(camTab.getCamera());
        });
        
        cameraRadioFOVV.setOnAction((evster) ->
        {
            camTab.setOrientation(cameraRadioFOVH.isSelected());
            
            preview.setCamera(camTab.getCamera());
        });
        
        //----------------------------------------------------------------------
        // Light Tab Listeners
        //----------------------------------------------------------------------
        lightChoiceL.setOnAction((evster) ->
        {
            if (listen == true)
            {
                // Index of currently selected light
                int selectedIndex =
                        lightChoiceL.getSelectionModel().getSelectedIndex();
            
                ligTab.setActiveLight(selectedIndex);
            
                loadLight();
            }
        });
        
        lightButtonLN.setOnAction((evster) ->
        {
            createLight();
        });
        
        lightButtonLD.setOnAction((evster) ->
        {
            deleteLight();
        });
        
        lightSpinnerPX.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            ligTab.setActiveLightX(newster);
            
            refreshPreview();
        });
        lightSpinnerPX.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (!newster)
            {
                ligTab.setActiveLightX(lightSpinnerPX.getEditor().getText());
                
                refreshPreview();
                
                // Fixes an issue in JavaFX of the Spinner's value not being
                // returned after it was changed.
                lightSpinnerPX.increment(0);
            }
        });
        
        lightSpinnerPY.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            ligTab.setActiveLightY(newster);
            
            refreshPreview();
        });
        lightSpinnerPY.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                ligTab.setActiveLightY(lightSpinnerPY.getEditor().getText());
                
                refreshPreview();
                
                lightSpinnerPY.increment(0);
            }
        });
        
        lightSpinnerPZ.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            ligTab.setActiveLightZ(newster);
            
            refreshPreview();
        });
        lightSpinnerPZ.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                ligTab.setActiveLightZ(lightSpinnerPZ.getEditor().getText());
                
                refreshPreview();
                
                lightSpinnerPZ.increment(0);
            }
        });
        
        lightColorC.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            ligTab.setActiveLightColor(newster);
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

            preview.setRoot(getPreview());
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

            preview.setRoot(getPreview());
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

            preview.setRoot(getPreview());
        }
    }
    
    /**
     * Creates a new light. For use when the user clicks the "New" button on the
     * light tab.
     */
    protected void createLight()
    {
        String name = ligTab.createLight();
            
        // As long as a name was given by the user...
        if (!name.equals(""))
        {
            // ...pause on listening to events.
            listen = false;
                
            // Add the name to the list of lights in the choice box
            lightChoiceL.getItems().add(name);
            // Set the new name in the choice box
            lightChoiceL.setValue(name);
            
            enableLightControls(true);
                
            // Continue listening to events
            listen = true;
                
            resetLightControls();
                
            refreshPreview();
        }
    }
    
    /**
     * Deletes the currently selected light
     */
    protected void deleteLight()
    {
        // The index of the currently selected index
        int selectedIndex
                = lightChoiceL.getSelectionModel().getSelectedIndex();

        int lightAmount = ligTab.getLightAmount();

        // Delete the light
        ligTab.deleteActiveLight(selectedIndex);

        // Pause on listening to events
        listen = false;

        // If the first light was the deleted light...
        if (selectedIndex == 0)
        {
            // ...and if there was only 1 light...
            if (lightAmount == 1)
            {
                // ...there are now no lights, so make the choice box blank.
                lightChoiceL.setValue("");
                
                enableLightControls(false);
            }
            // ...otherwise, if there is more than 1 light...
            else
            {
                // ...set the active light to the last light in the list.
                ligTab.setActiveLight(lightAmount - 2);
                lightChoiceL.setValue(ligTab.getActiveLightName());
            }
        }
        // ...otherwise, if the first light was not chosen...
        else
        {
            // ...set the active light to the light on the list before the
            // deleted one.
            ligTab.setActiveLight(selectedIndex - 1);
            lightChoiceL.setValue(ligTab.getActiveLightName());
        }

        // Remove the light from the choice box's list
        lightChoiceL.getItems().remove(selectedIndex);

        // Continue listening to events
        listen = true;

        loadLight();

        refreshPreview();
    }
    
    /**
     * Either enables or disables the controls for manipulating lights. This
     * does not include the "new" button, as that button should always be
     * available.
     * 
     * @param toEnable Whether or not to enable or disable the controls
     */
    protected void enableLightControls(boolean toEnable)
    {
        lightChoiceL.setDisable(!toEnable);
        lightButtonLD.setDisable(!toEnable);
        lightSpinnerPX.setDisable(!toEnable);
        lightSpinnerPY.setDisable(!toEnable);
        lightSpinnerPZ.setDisable(!toEnable);
        lightColorC.setDisable(!toEnable);
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
     * Creates a group of objects to be used as the preview in the viewport
     * 
     * @return A group of everything to be used in the preview
     */
    protected Group getPreview()
    {
        int lightAmount = ligTab.getLightAmount();
        
        Group previewItems = new Group();
        
        // Rotate to the correct position
        previewItems.getTransforms().add(camTab.getXRotate());
        previewItems.getTransforms().add(camTab.getYRotate());
        
        // Add the mesh
        previewItems.getChildren().add(terTab.getMesh());
        
        // Add each light
        for (int i = 0; i < lightAmount; i++)
        {
            previewItems.getChildren().add(ligTab.getLight(i));
        }
        
        return previewItems;
    }
    
    /**
     * Loads the properties of the light chosen in the choice box on the light
     * tab into the light tab's controls
     */
    protected void loadLight()
    {
        lightSpinnerPX.getValueFactory().setValue(ligTab.getActiveLightX());
        lightSpinnerPY.getValueFactory().setValue(ligTab.getActiveLightY());
        lightSpinnerPZ.getValueFactory().setValue(ligTab.getActiveLightZ());
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
        + " other than profited redistribution.\n\nversion 0.51 beta");
        
        // Sets the dialog box's icon
        ((Stage)alster.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        alster.showAndWait();
    }
    
    /**
     * Prepares the previewItems for saving a rendered image. This should be executed
     * immediately before an image is to be saved.
     */
    protected void prepareForRender()
    {
        renTab.setWidth(renderSpinnerRW.getEditor().getText());
        renTab.setHeight(renderSpinnerRH.getEditor().getText());
        
        int width = renTab.getWidth();
        int height = renTab.getHeight();
        
        camTab.setCameraOffset(width / 2, height / 2);
        
        preview.setWidth(width);
        preview.setHeight(height);
    }
    
    /**
     * Gets the preview ready to be shown
     */
    protected void preparePreview()
    {
        terTab.prepareMesh();
        
        // To be centered, the mesh must be adjusted by half of the previewItems's
        // size
        camTab.setCameraOffset(PREVIEW_WIDTH / 2, PREVIEW_HEIGHT / 2);
        
        refreshPreview();
        
        preview.setFill(renTab.getBackColor());
    }
    
    /**
     * Refreshes the model preview. This should be used each time after a change
     * is made to the shape of the 3d object.
     */
    protected void refreshPreview()
    {
        // Re-center the camera
        camTab.setOrigin(terTab.getCenterX(), terTab.getCenterY(),
                terTab.getCenterZ());
        camTab.setFurthestPoint(terTab.getFurthestPoint());
        
        // Re-position lights
        ligTab.setOrigin(terTab.getCenterX(), terTab.getCenterY(),
                terTab.getCenterZ());
        ligTab.setFurthestPoint(terTab.getFurthestPoint());
        
        if (ligTab.lightExists())
        {
            ligTab.repositionLights();
        }
        
        preview.setRoot(getPreview());
        preview.setCamera(camTab.getCamera());
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
            camTab = new CameraTab();
            
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
            cameraSliderAH.setValue(camTab.getDefaultHorizontalAngle());
            cameraSliderAV.setValue(camTab.getDefaultVerticalAngle());
            cameraSliderFOVD.setValue(camTab.getDefaultField());
            
            terrainSpinnerVRW.getValueFactory().setValue(
                    terTab.getDefaultSize());
            terrainSpinnerVRD.getValueFactory().setValue(
                    terTab.getDefaultSize());
            renderSpinnerRW.getValueFactory().setValue(
                    renTab.getDefaultWidth());
            renderSpinnerRH.getValueFactory().setValue(
                    renTab.getDefaultHeight());
            cameraSpinnerPAH.getValueFactory().setValue(0);
            cameraSpinnerPAV.getValueFactory().setValue(0);
            cameraSpinnerPAZ.getValueFactory().setValue(0);
            
            cameraRadioFOVH.setSelected(true);
            
            preparePreview();
            
            // Begin listening to action events
            listen = true;
        }
    }
    
    /**
     * Resets the controls on the light tab to their default values (excluding
     * the choice box)
     */
    protected void resetLightControls()
    {
        lightSpinnerPX.getValueFactory().setValue(ligTab.getDefaultX());
        lightSpinnerPY.getValueFactory().setValue(ligTab.getDefaultY());
        lightSpinnerPZ.getValueFactory().setValue(ligTab.getDefaultZ());
        lightColorC.setValue(ligTab.getDefaultColor());
    }
    
    /**
     * Resets the previewItems's size, re-centering the camera on the mesh
     */
    protected void resetPreviewSize()
    {
        // To be centered, the mesh must be adjusted by half of the previewItems's
        // size
        camTab.setCameraOffset(PREVIEW_WIDTH / 2, PREVIEW_HEIGHT / 2);
        
        preview.setWidth(PREVIEW_WIDTH);
        preview.setHeight(PREVIEW_HEIGHT);
    }
    
    /**
     * Saves a rendered image, replacing the last image that was saved. If there
     * is no image that was saved before this one, it functions the same as the
     * saveAs method.
     */
    @FXML
    protected void save()
    {
        prepareForRender();
        
        // Create a screenshot of the previewItems
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.save(writster);
        
        resetPreviewSize();
    }
    
    /**
     * Has the user save a rendered image of the mesh through a file chooser
     */
    @FXML
    protected void saveAs()
    {
        prepareForRender();
        
        // Create a screenshot of the previewItems
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.saveAs(writster);
        
        resetPreviewSize();
    }
}
