

import graphics.LightObject;
import graphics.Population;
import graphics.Terrain;
import graphics.TextureObject;
import tabs.TextureTab;
import tabs.TerrainTab;
import tabs.RenderTab;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tabs.CameraTab;
import tabs.LightTab;
import tabs.PopulationTab;

/**
 * This is the controller class for Project Scaper. This class programmatically
 * sets up the part of the scene that the FXML document is unable to. It also
 * uses event listeners and event methods for the controls that need them.
 * 
 * @author George Tiersma
 */
public class Controller
{
    // The width of the preview of the model
    final int PREVIEW_WIDTH = 800;
    // The height of the preview of the model
    final int PREVIEW_HEIGHT = 400;
    
    // Instantiate an object for each tab. Each of these objects control the
    // functionality relating to that tab.
    TextureTab texTab;
    TerrainTab terTab;
    RenderTab renTab;
    CameraTab camTab;
    LightTab ligTab;
    PopulationTab popTab;
    
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
    @FXML private Button populationButtonPRG;
    @FXML private Button populationButtonPD;
    @FXML private Button populationButtonVRWD;
    @FXML private Button populationButtonVRWI;
    @FXML private Button populationButtonVRHD;
    @FXML private Button populationButtonVRHI;
    
    @FXML private ChoiceBox lightChoiceL;
    @FXML private ChoiceBox populationChoiceP;
    
    @FXML private ColorPicker renderColorBC;
    @FXML private ColorPicker lightColorC;
    
    @FXML private ComboBox terrainComboDM;
    @FXML private ComboBox terrainComboT;
    @FXML private ComboBox terrainComboBM;
    @FXML private ComboBox terrainComboSM;
    @FXML private ComboBox populationComboP;
    @FXML private ComboBox populationComboS;
    @FXML private ComboBox populationComboSW;
    @FXML private ComboBox populationComboSH;
    @FXML private ComboBox populationComboDR1;
    @FXML private ComboBox populationComboDR2;
    @FXML private ComboBox populationComboT;
    @FXML private ComboBox populationComboBM;
    @FXML private ComboBox populationComboSM;
    
    @FXML private FlowPane texturesFlowC;
    @FXML private FlowPane texturesFlowG;
    
    @FXML private ImageView terrainImageDM;
    @FXML private ImageView terrainImageT;
    @FXML private ImageView terrainImageBM;
    @FXML private ImageView terrainImageSM;
    @FXML private ImageView populationImageP;
    @FXML private ImageView populationImageS;
    @FXML private ImageView populationImageSW;
    @FXML private ImageView populationImageSH;
    @FXML private ImageView populationImageDR1;
    @FXML private ImageView populationImageDR2;
    @FXML private ImageView populationImageT;
    @FXML private ImageView populationImageBM;
    @FXML private ImageView populationImageSM;
    
    @FXML private RadioButton cameraRadioFOVH;
    @FXML private RadioButton cameraRadioFOVV;
    
    @FXML private Slider terrainSliderDMS;
    @FXML private Slider cameraSliderAH;
    @FXML private Slider cameraSliderAV;
    @FXML private Slider cameraSliderFOVD;
    @FXML private Slider populationSliderDRS;
    
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
    @FXML private Tab renderTab;
    
    @FXML private TextField terrainTextVRW;
    @FXML private TextField terrainTextVRD;
    @FXML private TextField populationTextVRW;
    @FXML private TextField populationTextVRH;
    
    @FXML private VBox everything;

    /**
     * CONSTRUCTOR
     */
    public Controller()
    {
        ligTab = new LightTab();
        camTab = new CameraTab();
        renTab = new RenderTab();
        terTab = new TerrainTab();
        texTab = new TextureTab();
        popTab = new PopulationTab();
        
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
        
        preview = new SubScene(new Group(), PREVIEW_WIDTH, PREVIEW_HEIGHT, true,
                SceneAntialiasing.BALANCED);
        // Place the preview in the split pane in the scene
        splitster.getItems().set(0, preview);
        
        removeViewColor();
        
        formatTextFields();
        
        preparePreview();
        
        // Create group for field of view radio buttons
        ToggleGroup cameraRadiosFOV = new ToggleGroup();
        cameraRadioFOVH.setToggleGroup(cameraRadiosFOV);
        cameraRadioFOVV.setToggleGroup(cameraRadiosFOV);
        
        
        
        //----------------------------------------------------------------------
        // Terrain Tab Listeners
        //----------------------------------------------------------------------
        terrainTextVRW.textProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                setTerrainVertexWidth(Integer.parseInt(newster));
            }
        });
        
        terrainTextVRD.textProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                setTerrainVertexDepth(Integer.parseInt(newster));
            }
        });
        
        terrainSliderDMS.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            terTab.getTerrain().setDisplacementStrength(newster.floatValue());
            
            popTab.repositionPopulations(terTab.getTerrain().getPoints());
        
            recenterOnTerrain();
            
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
                popTab.setRotationY(newster.doubleValue());
        
                refreshPreview();
            }
        });
        
        cameraSliderAV.valueProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                camTab.setVerticalAngle(newster.doubleValue());
                popTab.setRotationX(newster.doubleValue());
        
                refreshPreview();
            }
        });
        
        cameraSpinnerPAH.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            camTab.setXAdjustment(newster);
            preview.setCamera(camTab.getCamera());
        });
        // The second listener is for when the value is changed and the focus
        // leaves the controller. The above listener will not be triggered in
        // this instance.
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
        
        lightSpinnerPX.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                ligTab.setActiveLightX(newster);
            }
        });
        lightSpinnerPX.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (!newster)
            {
                ligTab.setActiveLightX(lightSpinnerPX.getEditor().getText());
                
                // Fixes an issue in JavaFX of the Spinner's value not being
                // returned after it was changed.
                lightSpinnerPX.increment(0);
            }
        });
        
        lightSpinnerPY.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                ligTab.setActiveLightY(newster);
            }
        });
        lightSpinnerPY.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                ligTab.setActiveLightY(lightSpinnerPY.getEditor().getText());
                
                lightSpinnerPY.increment(0);
            }
        });
        
        lightSpinnerPZ.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                ligTab.setActiveLightZ(newster);
            }
        });
        lightSpinnerPZ.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                ligTab.setActiveLightZ(lightSpinnerPZ.getEditor().getText());
                
                lightSpinnerPZ.increment(0);
            }
        });
        
        lightColorC.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                ligTab.getActiveLight().setColor(newster);
            }
        });
        
        //----------------------------------------------------------------------
        // Population Tab Listeners
        //----------------------------------------------------------------------
        populationChoiceP.setOnAction((evster) ->
        {
            if (listen == true)
            {
                // Index of currently selected population
                int selectedIndex =
                        populationChoiceP.getSelectionModel()
                                .getSelectedIndex();
            
                popTab.setActivePopulation(selectedIndex);
            
                loadPopulation();
            }
        });
        
        populationTextVRW.textProperty().addListener(
                (obster, oldster, newster) ->
        {
            popTab.getActivePopulation().setVertexWidth(newster, terTab.getTerrain().getPoints());
        
            refreshPreview();
        });
        
        populationTextVRH.textProperty().addListener(
                (obster, oldster, newster) ->
        {
            popTab.getActivePopulation().setVertexHeight(newster, terTab.getTerrain().getPoints());
        
            refreshPreview();
        });
        
        populationSliderDRS.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                popTab.getActivePopulation().setDisplacementStrength(
                        newster.intValue(), terTab.getTerrain().getPoints());
                
                refreshPreview();
            }
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
            
            // Set the list to the combo boxes for maps
            terrainComboT.setItems(obster);
            terrainComboDM.setItems(obster);
            terrainComboBM.setItems(obster);
            terrainComboSM.setItems(obster);
            populationComboS.setItems(obster);
            populationComboDR1.setItems(obster);
            populationComboDR2.setItems(obster);
            populationComboT.setItems(obster);
            populationComboBM.setItems(obster);
            populationComboSM.setItems(obster);
            
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
            
            // Create a list of the names of the grayscale textures imported so
            // far
            ObservableList<String> obster = texTab.getTextureNames(false);
            
            // Set the list to the combo boxes for maps
            populationComboP.setItems(obster);
            populationComboSW.setItems(obster);
            populationComboSH.setItems(obster);
            
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
     * Changes the terrain's displacement map to what is currently set in the
     * terrain tab's displacement combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeDisplacement(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboDM.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);
            
            terrainImageDM.setImage(imster);

            // Set the image as the displacement map
            terTab.getTerrain().setDisplacement(imster);
            
            // Populations must be re-positioned since the shape of the terrain
            // has changed
            popTab.repositionPopulations(terTab.getTerrain().getPoints());
            
            recenterOnTerrain();
            
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's first displacement map in its
     * displacement range to what is currently set in the population tab's first
     * displacement map combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeFirstDisplacement(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboDR1.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageDR1.setImage(texster.getImage());
            
            // Set the image as the first of the 2 displacement range maps
            popTab.getActivePopulation().setFirstDisplacement(
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's height image to what is
     * currently set in the population tab's height combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeHeight(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box
            String name = populationComboSH.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(false, name);

            populationImageSH.setImage(texster.getImage());
            
            // Set the image as the population's height determinant
            popTab.getActivePopulation().setHeight(
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's placement image to what is
     * currently set in the population tab's placement combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changePlacement(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {   
            // ...get the camera's rotation values.
            double xRotate = camTab.getXRotate().getAngle();
            double yRotate = camTab.getYRotate().getAngle();
            
            // Get the selected image name from the combo box
            String name = populationComboP.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(false, name);

            populationImageP.setImage(texster.getImage());
            
            // Set the image as the population's placement determinant
            popTab.getActivePopulation().setPlacement(xRotate, yRotate,
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's bump map to what is currently
     * set in the population tab's bump map combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changePopulationBump(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboBM.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageBM.setImage(texster.getImage());
            
            // Set the image as the population's bump map
            popTab.getActivePopulation().setBumpMap(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's specular map to what is
     * currently set in the population tab's specular map combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changePopulationSpecular(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboSM.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageSM.setImage(texster.getImage());
            
            // Set the image as the specular map
            popTab.getActivePopulation().setSpecularMap(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's texture to what is currently
     * set in the population tab's texture combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changePopulationTexture(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboT.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageT.setImage(texster.getImage());
            
            // Set the image as the population's texture
            popTab.getActivePopulation().setTexture(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's second displacement map in
     * its displacement range to what is currently set in the population tab's
     * second displacement map combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeSecondDisplacement(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboDR2.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageDR2.setImage(texster.getImage());
            
            // Set the image as the second of the 2 displacement range maps
            popTab.getActivePopulation().setSecondDisplacement(texster,
                    terTab.getTerrain().getPoints());
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's shift image to what is
     * currently set in the population tab's shift combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeShift(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboS.getValue().toString();
            
            TextureObject texster = texTab.getTexture(true, name);

            populationImageS.setImage(texster.getImage());
            
            // Set the image as the population's shift determinant
            popTab.getActivePopulation().setShift(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the terrain's bump map to what is currently set in the terrain
     * tab's bump map combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeTerrainBump(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboBM.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);

            terrainImageBM.setImage(imster);
            
            // Set the image as the bump map
            terTab.getTerrain().setBump(imster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the terrain's specular map to what is currently set in the
     * specular map combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeTerrainSpecular(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboSM.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);
            
            terrainImageSM.setImage(imster);
            
            // Set the image as the specular map
            terTab.getTerrain().setSpecular(terrainImageSM.getImage());
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the terrain's texture to what is currently set in the texture
     * combo box
     * 
     * @param eventster The action event
     */
    @FXML
    protected void changeTerrainTexture(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboT.getValue().toString();
            
            // Get the image belonging to that name
            Image imster = texTab.getImageByName(true, name);

            terrainImageT.setImage(imster);

            // Set the image as the texture
            terTab.getTerrain().setTexture(imster);
        
            refreshPreview();
        }
    }
    
    @FXML
    protected void changeTerrainVertexDepth()
    {
        int depth = Integer.parseInt(terrainTextVRD.getText());
                
        terTab.getTerrain().setDepth(depth);
            
        updatePopulationsForTerrainSizeChange(false, depth);
    }
    
    @FXML
    protected void changeTerrainVertexWidth()
    {
        int width = Integer.parseInt(terrainTextVRW.getText());
                
        terTab.getTerrain().setWidth(width);
            
        updatePopulationsForTerrainSizeChange(true, width);
    }
    
    /**
     * Changes the currently-selected population's width image to what is
     * currently set in the population tab's width combo box
     * 
     * @param eventster The event listener
     */
    @FXML
    protected void changeWidth(ActionEvent eventster)
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboSW.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(false, name);

            populationImageSW.setImage(texster.getImage());
            
            // Set the image as the population's width determinant
            popTab.getActivePopulation().setWidth(texster,
                    terTab.getTerrain().getPoints());
        
            refreshPreview();
        }
    }
    
    /**
     * Creates a new light. For use when the user clicks the "New" button on the
     * light tab.
     */
    @FXML
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
     * Creates a new population. For use when the user clicks the "New" button
     * on the population tab.
     */
    @FXML
    protected void createPopulation()
    {
        // Get the camera's rotation values
        double xRotate = camTab.getXRotate().getAngle();
        double yRotate = camTab.getYRotate().getAngle();
        
        String name;
        
        Terrain terster = terTab.getTerrain();
        
        name = popTab.createPopulation(terster.getWidth(), terster.getDepth(),
                xRotate, yRotate, terster.getPoints());
            
        // As long as a name was given by the user...
        if (!name.equals(""))
        {
            // ...pause on listening to events.
            listen = false;
                
            // Add the name to the list of populations in the choice box
            populationChoiceP.getItems().add(name);
            // Set the new name in the choice box
            populationChoiceP.setValue(name);
            
            enablePopulationControls(true);
                
            resetPopulationControls();
            
            refreshPreview();
            
            // Continue listening to events
            listen = true;
        }
    }
    
    /**
     * Decreases the height (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    protected void decrementPopulationVertexHeight()
    {
        listen = false;
        
        int height = Integer.parseInt(populationTextVRH.getText());
        
        height--;
        
        popTab.getActivePopulation().setVertexHeight(height,
                terTab.getTerrain().getPoints());
        
        populationTextVRH.setText(Integer.toString(height));
        
        refreshPreview();
        
        listen = true;
    }
    
    /**
     * Decreases the width (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    protected void decrementPopulationVertexWidth()
    {
        listen = false;
        
        int width = Integer.parseInt(populationTextVRW.getText());
        
        width--;
        
        popTab.getActivePopulation().setVertexWidth(width,
                terTab.getTerrain().getPoints());
        
        populationTextVRW.setText(Integer.toString(width));
        
        refreshPreview();
        
        listen = true;
    }
    
    /**
     * Decreases the depth (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    protected void decrementTerrainVertexDepth()
    {
        listen = false;
        
        int depth = Integer.parseInt(terrainTextVRD.getText());
        
        depth--;
                
        setTerrainVertexDepth(depth);
        
        listen = true;
    }
    
    /**
     * Decreases the width (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    protected void decrementTerrainVertexWidth()
    {
        listen = false;
        
        int width = Integer.parseInt(terrainTextVRW.getText());
        
        width--;
                
        setTerrainVertexWidth(width);
        
        listen = true;
    }
    
    /**
     * Deletes the currently-selected light
     */
    @FXML
    protected void deleteLight()
    {
        // The index of the currently selected light
        int selectedIndex
                = lightChoiceL.getSelectionModel().getSelectedIndex();

        int lightAmount = ligTab.getLightAmount();

        // Delete the light
        ligTab.deleteActiveLight();

        // Pause on listening to events
        listen = false;

        // If the first light was the deleted light...
        if (selectedIndex == 0)
        {
            // ...and if there was only 1 light...
            if (lightAmount == 1)
            {
                // ...there are now no lights, so make the choice box empty.
                lightChoiceL.setValue("");
                
                enableLightControls(false);
            }
            // ...otherwise, if there is more than 1 light...
            else
            {
                // ...set the active light to the last light in the list.
                ligTab.setActiveLight(lightAmount - 2);
                lightChoiceL.setValue(ligTab.getActiveLight().getName());
            }
        }
        // ...otherwise, if the first light was not chosen...
        else
        {
            // ...set the active light to the light on the list before the
            // deleted one.
            ligTab.setActiveLight(selectedIndex - 1);
            lightChoiceL.setValue(ligTab.getActiveLight().getName());
        }

        // Remove the light from the choice box's list
        lightChoiceL.getItems().remove(selectedIndex);

        // Continue listening to events
        listen = true;

        loadLight();
        
        refreshPreview();
    }
    
    /**
     * Deletes the currently-selected population
     */
    @FXML
    protected void deletePopulation()
    {
        // The index of the currently selected population
        int selectedIndex
                = populationChoiceP.getSelectionModel().getSelectedIndex();

        int populationAmount = popTab.getPopulationAmount();

        // Delete the population
        popTab.deleteActivePopulation();
        
        // Pause on listening to events
        listen = false;

        // If the first population was the deleted population...
        if (selectedIndex == 0)
        {
            // ...and if there was only 1 population...
            if (populationAmount == 1)
            {
                // ...there are now no populations, so make the choice box
                // empty.
                populationChoiceP.setValue("");
                
                resetPopulationControls();
                enablePopulationControls(false);
            }
            // ...otherwise, if there is more than 1 population...
            else
            {
                // ...set the active population to the last population in the
                // list.
                popTab.setActivePopulation(populationAmount - 2);
                populationChoiceP.setValue(
                        popTab.getActivePopulation().getName());

                loadPopulation();
            }
        }
        // ...otherwise, if the first population was not chosen...
        else
        {
            // ...set the active population to the population on the list before
            // the deleted one.
            popTab.setActivePopulation(selectedIndex - 1);
            populationChoiceP.setValue(popTab.getActivePopulation().getName());

            loadPopulation();
        }

        // Remove the population from the choice box's list
        populationChoiceP.getItems().remove(selectedIndex);

        // Continue listening to events
        listen = true;
        
        refreshPreview();
    }
    
    /**
     * Either enables or disables the controls for manipulating lights. This
     * does not include the "new" button, as that button should always be
     * enabled.
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
     * Either enables or disables the controls for manipulating populations.
     * This does not include the "new" button, as that button should always be
     * enabled.
     * 
     * @param toEnable Whether or not to enable or disable the controls
     */
    protected void enablePopulationControls(boolean toEnable)
    {
        populationChoiceP.setDisable(!toEnable);
        populationButtonPRG.setDisable(!toEnable);
        populationButtonPD.setDisable(!toEnable);
        populationComboP.setDisable(!toEnable);
        populationComboS.setDisable(!toEnable);
        populationComboSW.setDisable(!toEnable);
        populationComboSH.setDisable(!toEnable);
        populationButtonVRWD.setDisable(!toEnable);
        populationTextVRW.setDisable(!toEnable);
        populationButtonVRWI.setDisable(!toEnable);
        populationButtonVRHD.setDisable(!toEnable);
        populationTextVRH.setDisable(!toEnable);
        populationButtonVRHI.setDisable(!toEnable);
        populationComboDR1.setDisable(!toEnable);
        populationComboDR2.setDisable(!toEnable);
        populationSliderDRS.setDisable(!toEnable);
        populationComboT.setDisable(!toEnable);
        populationComboBM.setDisable(!toEnable);
        populationComboSM.setDisable(!toEnable);
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
     * Forces all of the TextFields that are used for storing numeric data to
     * only contain numbers
     */
    protected void formatTextFields()
    {
        // Format for numeric values
        UnaryOperator<Change> numericStyle = content ->
        {
            Change chanster = null;

            if (content.getText().matches("[0-9]*"))
            {
                chanster = content;
            }

            return chanster;
        };
        
        // Each control must have its own TextFormatter
        TextFormatter<String> terrainWidthFormat
                = new TextFormatter<>(numericStyle);
        TextFormatter<String> terrainDepthFormat
                = new TextFormatter<>(numericStyle);
        TextFormatter<String> populationWidthFormat
                = new TextFormatter<>(numericStyle);
        TextFormatter<String> populationHeightFormat
                = new TextFormatter<>(numericStyle);
        
        terrainTextVRW.setTextFormatter(terrainWidthFormat);
        terrainTextVRD.setTextFormatter(terrainDepthFormat);
        populationTextVRW.setTextFormatter(populationWidthFormat);
        populationTextVRH.setTextFormatter(populationHeightFormat);
    }
    
    
    /**
     * Increases the height (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    protected void incrementPopulationVertexHeight()
    {
        listen = false;
        
        int height = Integer.parseInt(populationTextVRH.getText());
        
        height++;
        
        popTab.getActivePopulation().setVertexHeight(height,
                terTab.getTerrain().getPoints());
        
        populationTextVRH.setText(Integer.toString(height));
        
        refreshPreview();
        
        listen = true;
    }
    
    /**
     * Increases the width (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    protected void incrementPopulationVertexWidth()
    {
        listen = false;
        
        int width = Integer.parseInt(populationTextVRW.getText());
        
        width++;
        
        popTab.getActivePopulation().setVertexWidth(width,
                terTab.getTerrain().getPoints());
        
        populationTextVRW.setText(Integer.toString(width));
        
        refreshPreview();
        
        listen = true;
    }
    
    /**
     * Increases the depth (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    protected void incrementTerrainVertexDepth()
    {
        listen = false;
        
        int depth = Integer.parseInt(terrainTextVRD.getText());
        
        depth++;
                
        setTerrainVertexDepth(depth);
        
        listen = true;
    }
    
    /**
     * Increases the width (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    protected void incrementTerrainVertexWidth()
    {
        listen = false;
        
        int width = Integer.parseInt(terrainTextVRW.getText());
        
        width++;
                
        setTerrainVertexWidth(width);
        
        listen = true;
    }
    
    /**
     * Loads the properties of the active light into the light tab's controls
     */
    protected void loadLight()
    {
        LightObject activeLight = ligTab.getActiveLight();
        
        lightSpinnerPX.getValueFactory().setValue(activeLight.getPercentageX());
        lightSpinnerPY.getValueFactory().setValue(activeLight.getPercentageY());
        lightSpinnerPZ.getValueFactory().setValue(activeLight.getPercentageZ());
    }
    
    /**
     * Loads the properties of the active population into the population tab's
     * controls
     */
    protected void loadPopulation()
    {
        listen = false;
        
        // The names of the maps to load
        String placementName;
        String shiftName;
        String widthName;
        String heightName;
        String displacementName1;
        String displacementName2;
        String textureName;
        String bumpName;
        String specularName;
        
        Population activePopulation = popTab.getActivePopulation();
        
        // Gets the names of the maps to load
        placementName = activePopulation.getPlacement().getName();
        shiftName = activePopulation.getShift().getName();
        widthName = activePopulation.getWidth().getName();
        heightName = activePopulation.getHeight().getName();
        displacementName1 = activePopulation.getFirstDisplacement().getName();
        displacementName2 = activePopulation.getSecondDisplacement().getName();
        textureName = activePopulation.getTexture().getName();
        bumpName = activePopulation.getBumpMap().getName();
        specularName = activePopulation.getSpecularMap().getName();
        
        // Get the texure objects for those maps
        TextureObject placementTexture
                = texTab.getTexture(false, placementName);
        TextureObject shiftTexture
                = texTab.getTexture(false, shiftName);
        TextureObject widthTexture = texTab.getTexture(false, widthName);
        TextureObject heightTexture = texTab.getTexture(false, heightName);
        TextureObject displacementTexture1
                = texTab.getTexture(true, displacementName1);
        TextureObject displacementTexture2
                = texTab.getTexture(true, displacementName2);
        TextureObject diffuseTexture = texTab.getTexture(true, textureName);
        TextureObject bumpTexture = texTab.getTexture(true, bumpName);
        TextureObject specularTexture = texTab.getTexture(true, specularName);

        // Set the image previews to the correct image for the currently-
        // selected population
        populationImageP.setImage(placementTexture.getImage());
        populationImageS.setImage(shiftTexture.getImage());
        populationImageSW.setImage(widthTexture.getImage());
        populationImageSH.setImage(heightTexture.getImage());
        populationImageDR1.setImage(displacementTexture1.getImage());
        populationImageDR2.setImage(displacementTexture2.getImage());
        populationImageT.setImage(diffuseTexture.getImage());
        populationImageBM.setImage(bumpTexture.getImage());
        populationImageSM.setImage(specularTexture.getImage());
        
        // Set the combo boxes to the correct map name
        populationComboP.setValue(placementName);
        populationComboS.setValue(shiftName);
        populationComboSW.setValue(widthName);
        populationComboSH.setValue(heightName);
        populationComboDR1.setValue(displacementName1);
        populationComboDR2.setValue(displacementName2);
        populationComboT.setValue(textureName);
        populationComboBM.setValue(bumpName);
        populationComboSM.setValue(specularName);
        
        // Set displacement strength Slider
        populationSliderDRS.setValue(
                activePopulation.getDisplacementStrength());
        
        // Set vertex resolution TextFields
        populationTextVRH.setText(
                Integer.toString(activePopulation.getVertexHeight()));
        populationTextVRW.setText(
                Integer.toString(activePopulation.getVertexWidth()));
        
        listen = true;
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
        alster.setHeaderText("Generates 3D objects from 2D images");
        alster.setContentText("2019 - Created by George Tiersma\n\n"
        + "This program is not copyrighted. It can be used for any purpose"
        + " other than profited redistribution.\n\nversion 0.6 beta");
        
        // Sets the dialog box's icon
        ((Stage)alster.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
        
        alster.showAndWait();
    }
    
    /**
     * Prepares the preview for saving a rendered image. This should be executed
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
        terTab.prepareTerrain();
        
        // To be centered, the terrain must be adjusted by half of the preview's
        // size
        camTab.setCameraOffset(PREVIEW_WIDTH / 2, PREVIEW_HEIGHT / 2);
        
        recenterOnTerrain();
        
        refreshPreview();
        preview.setCamera(camTab.getCamera());
        preview.setFill(renTab.getBackColor());
    }
    
    /**
     * Re-centers objects that are positioned relative to the terrain (such as
     * the camera and lights). For use whenever the terrain's size or shape has
     * changed.
     */
    protected void recenterOnTerrain()
    {
        // Estimation of the center point of the terrain
        double terrainCenterX;
        double terrainCenterY;
        double terrainCenterZ;
        
        // The greatest distance of a point from the terrain's center
        double terrainFarPoint;
            
        Terrain terster = terTab.getTerrain();
        
        terrainCenterX = terster.getCenter('x');
        terrainCenterY = terster.getCenter('y');
        terrainCenterZ = terster.getCenter('z');
        terrainFarPoint = terster.getFurthestPoint();
        
        // Re-center the camera
        camTab.setOrigin(terrainCenterX, terrainCenterY, terrainCenterZ);
        camTab.setFurthestPoint(terrainFarPoint);
        
        // Re-position lights
        ligTab.setOrigin(terrainCenterX, terrainCenterY, terrainCenterZ);
        ligTab.setFurthestPoint(terrainFarPoint);
    }
    
    /**
     * Refreshes the content in the preview pane
     */
    protected void refreshPreview()
    {
        int lightAmount = ligTab.getLightAmount();
        int populationAmount = popTab.getPopulationAmount();
        
        Group previewItems = new Group();
        
        // Rotate to the correct position
        previewItems.getTransforms().add(camTab.getXRotate());
        previewItems.getTransforms().add(camTab.getYRotate());
        
        // Add the terrain
        previewItems.getChildren().add(terTab.getTerrain().getMeshView());
        
        // Add each light
        for (int i = 0; i < lightAmount; i++)
        {
            previewItems.getChildren().add(ligTab.getLight(i).getPointLight());
        }
        
        // If at least 1 population exists, and the currently-selected
        // population's service is about to be started...
        if (popTab.populationExists()
                && popTab.getActivePopulation().isServicePrepared())
        {
            // ...get the service.
            Service populationService
                    = popTab.getActivePopulation().getService();
            
            // Disable all of the controls
            everything.setDisable(true);
        
            // Once the service is finished...
            populationService.setOnSucceeded(e ->
            {
                // ...perform the post-service activities on the active
                // population.
                popTab.getActivePopulation().concludeService();
                
                // Add each population
                for (int i = 0; i < populationAmount; i++)
                {
                    previewItems.getChildren().add(popTab.getPopulation(i));
                }
            
                // Apply the content to the preview pane
                preview.setRoot(previewItems);
                
                // Re-enable all of the controls
                everything.setDisable(false);
            });
        }
        // ...otherwise...
        else
        {
            // ...add each population.
            for (int i = 0; i < populationAmount; i++)
            {
                previewItems.getChildren().add(popTab.getPopulation(i));
            }
            
            // Apply the content to the preview pane
            preview.setRoot(previewItems);
        }
    }
    
    /**
     * Refreshes the currently-selected population, re-randomizing some of the
     * population's properties
     */
    @FXML
    protected void regeneratePopulation()
    {
        // Get the camera's rotation value
        double xRotate = camTab.getXRotate().getAngle();
        double yRotate = camTab.getYRotate().getAngle();
        
        // The title for the progress dialog
        String actionDescription = "Re-generating Population";
        
        popTab.getActivePopulation().load(xRotate, yRotate, actionDescription,
                terTab.getTerrain().getPoints());
        
        refreshPreview();
    }
    
    /**
     * Removes the color from the ImageView previews for maps that are to be
     * grayscale
     */
    protected void removeViewColor()
    {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation(-1);
        
        populationImageP.setEffect(grayscale);
        populationImageSW.setEffect(grayscale);
        populationImageSH.setEffect(grayscale);
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
            texTab = new TextureTab();
            terTab = new TerrainTab();
            renTab = new RenderTab();
            camTab = new CameraTab();
            ligTab = new LightTab();
            popTab = new PopulationTab();
            
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
            
            renderSpinnerRW.getValueFactory().setValue(
                    renTab.getDefaultWidth());
            renderSpinnerRH.getValueFactory().setValue(
                    renTab.getDefaultHeight());
            cameraSpinnerPAH.getValueFactory().setValue(0);
            cameraSpinnerPAV.getValueFactory().setValue(0);
            cameraSpinnerPAZ.getValueFactory().setValue(0);
            
            cameraRadioFOVH.setSelected(true);
            
            terrainTextVRD.setText(Integer.toString(terTab.getDefaultSize()));
            terrainTextVRW.setText(Integer.toString(terTab.getDefaultSize()));
            
            // Clear light tab
            lightChoiceL.getItems().clear();
            resetLightControls();
            enableLightControls(false);
            
            // Clear population tab
            populationChoiceP.getItems().clear();
            resetPopulationControls();
            enablePopulationControls(false);
            
            // Now that everything has been reset, prepare the preview again
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
     * Resets the controls on the population tab to their default values
     * (excluding the choice box listing all of the populations)
     */
    protected void resetPopulationControls()
    {
        // Clear map previews
        populationImageP.setImage(null);
        populationImageS.setImage(null);
        populationImageSW.setImage(null);
        populationImageSH.setImage(null);
        populationImageDR1.setImage(null);
        populationImageDR2.setImage(null);
        populationImageT.setImage(null);
        populationImageBM.setImage(null);
        populationImageSM.setImage(null);
        
        populationComboP.setValue("");
        populationComboS.setValue("");
        populationComboSW.setValue("");
        populationComboSH.setValue("");
        populationTextVRW.setText(
                Integer.toString(popTab.getDefaultVertexWidth()));
        populationTextVRH.setText(
                Integer.toString(popTab.getDefaultVertexHeight()));
        populationComboDR1.setValue("");
        populationComboDR2.setValue("");
        populationSliderDRS.setValue(popTab.getDefaultDisplacementStrength());
        populationComboT.setValue("");
        populationComboBM.setValue("");
        populationComboSM.setValue("");
    }
    
    /**
     * Resets the preview's size, re-centering the camera on the terrain
     */
    protected void resetPreviewSize()
    {
        // To be centered, the terrain must be adjusted by half of the preview's
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
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.save(writster);
        
        resetPreviewSize();
    }
    
    /**
     * Has the user save a rendered image through a file chooser
     */
    @FXML
    protected void saveAs()
    {
        prepareForRender();
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.saveAs(writster);
        
        resetPreviewSize();
    }
    
    /**
     * Sets the depth (in vertices) of the terrain. The TextField is
     * also updated to reflect the changes.
     * 
     * @param depth The depth to be set
     */
    protected void setTerrainVertexDepth(int depth)
    {
        terTab.getTerrain().setDepth(depth);
            
        updatePopulationsForTerrainSizeChange(false, depth);
                
        terrainTextVRD.setText(Integer.toString(depth));
    }
    
    /**
     * Sets the width (in vertices) of the terrain. The TextField is
     * also updated to reflect the changes.
     * 
     * @param width The width to be set
     */
    protected void setTerrainVertexWidth(int width)
    {
        terTab.getTerrain().setWidth(width);
            
        updatePopulationsForTerrainSizeChange(true, width);
                
        terrainTextVRW.setText(Integer.toString(width));
    }
    
    /**
     * Recreates the populations to accommodate the terrain's new size
     * 
     * @param didWidthChange Whether or not the terrain's width was adjusted
     * @param terrainSize The new size of the terrain
     */
    protected void updatePopulationsForTerrainSizeChange(boolean didWidthChange,
            int terrainSize)
    {
        // Get the camera's rotation value
        double xRotate = camTab.getXRotate().getAngle();
        double yRotate = camTab.getYRotate().getAngle();
        
        // If the terrain's width changed...
        if (didWidthChange)
        {
            // ...update the populations for a width change.
            popTab.updateForTerrainWidthChange(terrainSize, xRotate, yRotate,
                terTab.getTerrain().getPoints());
        }
        // ...otherwise, the terrain's depth must have changed...
        else
        {
            // ...so update them for a depth change.
            popTab.updateForTerrainDepthChange(terrainSize, xRotate, yRotate,
                terTab.getTerrain().getPoints());
        }
        
        recenterOnTerrain();
        
        refreshPreview();
    }
}
