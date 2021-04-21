

import static com.sun.javafx.PlatformUtil.isWindows;
import generics.InputVerifier;
import graphics.LightObject;
import graphics.Population;
import graphics.Terrain;
import graphics.TextureObject;
import helpBox.Adviser;
import java.io.IOException;
import java.util.ArrayList;
import tabs.TextureTab;
import tabs.TerrainTab;
import tabs.RenderTab;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
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
import javafx.scene.control.Control;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tabs.CameraTab;
import tabs.LightTab;
import tabs.PopulationTab;

/**
 * This is the controller class for Sand Paper. This class programmatically
 * sets up the part of the scene that the FXML document is unable to. It also
 * uses event listeners and event methods for the controls that need them.
 * 
 * @author George Tiersma
 */
public class Controller
{
    // The initial width of the preview of the model
    final private int DEFAULT_PREVIEW_WIDTH = 800;
    // The initial height of the preview of the model
    final private int DEFAULT_PREVIEW_HEIGHT = 400;
    
    // The key combos to scroll the help box up or down
    final private KeyCombination HELP_SCROLL_DOWN
            = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
    final private KeyCombination HELP_SCROLL_UP
            = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
    
    // Instantiate an object for each tab. Each of these objects control the
    // functionality relating to that tab.
    private TextureTab texTab;
    private TerrainTab terTab;
    private RenderTab renTab;
    private CameraTab camTab;
    private LightTab ligTab;
    private PopulationTab popTab;
    
    // Whether or not the controls on the form are currently "listening" for
    // actions. Setting this to false will disable most of the action listeners
    // until it is set to true again.
    private boolean listen;
    
    Adviser helper;
    private InputVerifier validator;
    
    // FXML apparently does not like SubScenes very much, so this control is
    // created entirely in this controller class.
    private SubScene preview;
    
    
    
    // Below are the controls taken from the FXML file. They are sorted
    // alphabetically by control type
    
    @FXML private Button texturesButtonCA;
    @FXML private Button texturesButtonGA;
    @FXML private Button lightButtonLN;
    @FXML private Button lightButtonLD;
    @FXML private Button populationButtonPN;
    @FXML private Button populationButtonPRG;
    @FXML private Button populationButtonPD;
    @FXML private Button populationButtonVRWD;
    @FXML private Button populationButtonVRWI;
    @FXML private Button populationButtonVRHD;
    @FXML private Button populationButtonVRHI;
    @FXML private Button populationButtonDRSD;
    @FXML private Button populationButtonDRSI;
    
    @FXML private ChoiceBox lightChoiceL;
    @FXML private ChoiceBox populationChoiceP;
    
    @FXML private ColorPicker renderColorBC;
    @FXML private ColorPicker lightColorC;
    
    @FXML private ComboBox terrainComboDM;
    @FXML private ComboBox terrainComboDM2;
    @FXML private ComboBox terrainComboBM;
    @FXML private ComboBox terrainComboSM;
    @FXML private ComboBox populationComboP;
    @FXML private ComboBox populationComboS;
    @FXML private ComboBox populationComboSW;
    @FXML private ComboBox populationComboSH;
    @FXML private ComboBox populationComboDR1;
    @FXML private ComboBox populationComboDR2;
    @FXML private ComboBox populationComboDM;
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
    
    @FXML private Pane previewContainer;
    
    @FXML private RadioButton cameraRadioFOVH;
    @FXML private RadioButton cameraRadioFOVV;
    
    @FXML private ScrollPane texturesScrollC;
    @FXML private ScrollPane texturesScrollG;
    
    @FXML private Slider cameraSliderAH;
    @FXML private Slider cameraSliderAV;
    
    @FXML private Spinner<Integer> renderSpinnerRW;
    @FXML private Spinner<Integer> renderSpinnerRH;
    @FXML private Spinner<Integer> cameraSpinnerPAH;
    @FXML private Spinner<Integer> cameraSpinnerPAV;
    @FXML private Spinner<Integer> cameraSpinnerPAZ;
    @FXML private Spinner<Integer> cameraSpinnerFOVD;
    @FXML private Spinner<Integer> lightSpinnerPX;
    @FXML private Spinner<Integer> lightSpinnerPY;
    @FXML private Spinner<Integer> lightSpinnerPZ;
    
    @FXML private Tab textureTab;
    @FXML private Tab terrainTab;
    @FXML private Tab populationTab;
    @FXML private Tab renderTab;
    @FXML private Tab cameraTab;
    @FXML private Tab lightTab;
    
    @FXML private TabPane rightTabs;
    @FXML private TabPane bottomTabs;
    
    @FXML private TextArea helpBox;
    
    @FXML private TextField terrainTextVRW;
    @FXML private TextField terrainTextVRD;
    @FXML private TextField terrainTextDMS;
    @FXML private TextField populationTextVRW;
    @FXML private TextField populationTextVRH;
    @FXML private TextField populationTextDRS;
    
    @FXML private VBox everything;

    /**
     * CONSTRUCTOR
     */
    public Controller()
    {
        helper = new Adviser();
        validator = new InputVerifier();
        
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
        // The size that the SubScene is initialized to does not matter. It will
        // automatically be resized to fit its pane.
        preview = new SubScene(new Group(), 1, 1, true,
                SceneAntialiasing.BALANCED);
        // Place the preview in the split pane in the scene
        previewContainer.getChildren().set(0, preview);
        
        // Prepare the help box
        helper.load();
        loadTooltips();
        
        removeViewColor();
        
        formatControls();
        
        preparePreview();
        
        // Create group for field of view radio buttons
        ToggleGroup cameraRadiosFOV = new ToggleGroup();
        cameraRadioFOVH.setToggleGroup(cameraRadiosFOV);
        cameraRadioFOVV.setToggleGroup(cameraRadiosFOV);
        
        
        
        //----------------------------------------------------------------------
        // Texture Tab Listeners
        //----------------------------------------------------------------------
        texturesFlowC.addEventHandler(MouseEvent.MOUSE_CLICKED, evster ->
        {
            refreshTextureButton(true);
        });
        texturesFlowG.addEventHandler(MouseEvent.MOUSE_CLICKED, evster ->
        {
            refreshTextureButton(false);
        });
        
        //----------------------------------------------------------------------
        // Terrain Tab Listeners
        //----------------------------------------------------------------------
        terrainTextVRW.textProperty().addListener((obster, oldster, newster) ->
        {
            // As long as the program is currently listening to events and the
            // text input is not blank...
            if (listen && !newster.equals(""))
            {
                // ...parse and validate the input.
                short width = validator.parsidateTerrainSize(newster);
                
                // If it was parsed successfully...
                if (width != validator.getParseFailValue())
                {
                    // ...set it as the width.
                    setTerrainVertexWidth(false, width);
                }
            }
        });
        // This focus listener prevents invalid text from remaining in the
        // textfield
        terrainTextVRW.focusedProperty().addListener((obster, oldster, newster)
                ->
        {
            if (!newster)
            {
                int width = terTab.getTerrain().getWidth();
                
                terrainTextVRW.setText(String.valueOf(width));
            }
        });
        
        terrainTextVRD.textProperty().addListener((obster, oldster, newster) ->
        {
            if (listen && !newster.equals(""))
            {
                short depth = validator.parsidateTerrainSize(newster);
                
                if (depth != validator.getParseFailValue())
                {
                    setTerrainVertexDepth(false, depth);
                }
            }
        });
        terrainTextVRD.focusedProperty().addListener((obster, oldster, newster)
                ->
        {
            if (!newster)
            {
                int depth = terTab.getTerrain().getDepth();
                
                terrainTextVRD.setText(String.valueOf(depth));
            }
        });
        
        terrainTextDMS.textProperty().addListener((obster, oldster, newster) ->
        {
            if (listen && !newster.equals(""))
            {
                short strength =
                        validator.parsidateDisplacementStrength(newster);
                
                if (strength != validator.getParseFailValue())
                {
                    setTerrainDisplacementStrength(false, strength);
                }
            }
        });
        terrainTextDMS.focusedProperty().addListener((obster, oldster, newster)
                ->
        {
            if (!newster)
            {
                int strength = terTab.getTerrain().getDisplacementStrength();
                
                terrainTextDMS.setText(String.valueOf(strength));
            }
        });
        
        //----------------------------------------------------------------------
        // Render Tab Listeners
        //----------------------------------------------------------------------
        renderSpinnerRW.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            if (listen)
            {
                int validValue = validateSpinner(true, renTab.getWidth(),
                        renderSpinnerRW);
                
                renTab.setWidth(validValue);
            }
        });
        // The second listener is for when the value is changed and the focus
        // leaves the controller. The above listener will not be triggered in
        // this instance.
        renderSpinnerRW.focusedProperty().addListener((obster, oldster, newster)
                ->
        {
            if (listen)
            {
                int validValue = validateSpinner(true, renTab.getWidth(),
                        renderSpinnerRW);
                
                renTab.setWidth(validValue);
            }
        });
        
        renderSpinnerRH.valueProperty().addListener((obster, oldster, newster)
                ->
        {
            int validValue = validateSpinner(true, renTab.getHeight(),
                    renderSpinnerRH);
                
            renTab.setHeight(validValue);
        });
        renderSpinnerRH.focusedProperty().addListener((obster, oldster, newster)
                ->
        {
            if (listen)
            {
                int validValue = validateSpinner(true, renTab.getHeight(),
                        renderSpinnerRH);
                
                renTab.setHeight(validValue);
            }
        });
        
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
                camTab.setHorizontalAngle(newster.shortValue());
                popTab.setRotationY(newster.shortValue());
        
                refreshPreview();
            }
        });
        
        cameraSliderAV.valueProperty().addListener((obster, oldster, newster) ->
        {
            if (listen)
            {
                camTab.setVerticalAngle(newster.shortValue());
                popTab.setRotationX(newster.shortValue());
        
                refreshPreview();
            }
        });
        
        cameraSpinnerPAH.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            int validValue = validateSpinner(false,
                    (int)camTab.getXAdjustment(), cameraSpinnerPAH);
            
            camTab.setXAdjustment(validValue);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAH.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                int validValue = validateSpinner(false,
                        (int)camTab.getXAdjustment(), cameraSpinnerPAH);
            
                camTab.setXAdjustment(validValue);
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSpinnerPAV.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            int validValue = validateSpinner(false,
                    (int)camTab.getYAdjustment(), cameraSpinnerPAV);
            
            camTab.setYAdjustment(validValue);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAV.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                int validValue = validateSpinner(false,
                        (int)camTab.getYAdjustment(), cameraSpinnerPAV);
            
                camTab.setYAdjustment(validValue);
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSpinnerPAZ.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            int validValue = validateSpinner(false, (int)camTab.getZoom(),
                    cameraSpinnerPAZ);
            
            camTab.setZoom(validValue);
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerPAZ.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (newster == false)
            {
                int validValue = validateSpinner(false, (int)camTab.getZoom(),
                        cameraSpinnerPAZ);
            
                camTab.setZoom(validValue);
                preview.setCamera(camTab.getCamera());
            }
        });
        
        cameraSpinnerFOVD.valueProperty().addListener((obster, oldster,
                newster) ->
        {
            short degrees = (short)validateSpinner(true,
                    camTab.getFieldOfView(), cameraSpinnerFOVD);
            camTab.setFieldOfView(degrees);
            
            preview.setCamera(camTab.getCamera());
        });
        cameraSpinnerFOVD.focusedProperty().addListener((obster, oldster,
                newster) ->
        {
            if (listen)
            {
                short degrees = (short)validateSpinner(true,
                        camTab.getFieldOfView(), cameraSpinnerFOVD);
                camTab.setFieldOfView(degrees);
            
                preview.setCamera(camTab.getCamera());
            }
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
                int validValue = validateSpinner(false,
                        ligTab.getActiveLight().getPercentageX(),
                        lightSpinnerPX);
            
                ligTab.setActiveLightX(validValue);
            }
        });
        lightSpinnerPX.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                int validValue = validateSpinner(false,
                    ligTab.getActiveLight().getPercentageX(), lightSpinnerPX);
            
                ligTab.setActiveLightX(validValue);
            }
        });
        
        lightSpinnerPY.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                int validValue = validateSpinner(false,
                        ligTab.getActiveLight().getPercentageY(),
                        lightSpinnerPY);
            
                ligTab.setActiveLightY(validValue);
            }
        });
        lightSpinnerPY.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                int validValue = validateSpinner(false,
                    ligTab.getActiveLight().getPercentageY(), lightSpinnerPY);
            
                ligTab.setActiveLightY(validValue);
            }
        });
        
        lightSpinnerPZ.valueProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                int validValue = validateSpinner(false,
                        ligTab.getActiveLight().getPercentageZ(),
                        lightSpinnerPZ);
            
                ligTab.setActiveLightZ(validValue);
            }
        });
        lightSpinnerPZ.focusedProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen)
            {
                int validValue = validateSpinner(false,
                    ligTab.getActiveLight().getPercentageZ(), lightSpinnerPZ);
            
                ligTab.setActiveLightZ(validValue);
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
                listen = false;
                
                // Index of currently selected population
                int selectedIndex = populationChoiceP.getSelectionModel()
                        .getSelectedIndex();
            
                popTab.setActivePopulation(selectedIndex);
            
                loadPopulation();
                
                listen = true;
            }
        });
        
        populationTextVRW.textProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen && !newster.equals(""))
            {
                short width = validator.parsidatePopulationSize(newster);
                
                if (width != validator.getParseFailValue())
                {
                    setPopulationVertexWidth(false, width);
                }
            }
        });
        populationTextVRW.focusedProperty().addListener((obster, oldster,
                newster) ->
        {
            if (!newster)
            {
                int width = popTab.getActivePopulation().getVertexWidth();
                
                populationTextVRW.setText(String.valueOf(width));
            }
        });
        
        populationTextVRH.textProperty().addListener(
                (obster, oldster, newster) ->
        {
            if (listen && !newster.equals(""))
            {
                short height = validator.parsidatePopulationSize(newster);
                
                if (height != validator.getParseFailValue())
                {
                    setPopulationVertexHeight(false, height);
                }
            }
        });
        populationTextVRH.focusedProperty().addListener((obster, oldster,
                newster) ->
        {
            if (!newster)
            {
                int height = popTab.getActivePopulation().getVertexHeight();
                
                populationTextVRH.setText(String.valueOf(height));
            }
        });
        
        populationTextDRS.textProperty().addListener((obster, oldster, newster)
                ->
        {
            if (listen && !newster.equals(""))
            {
                short strength =
                        validator.parsidateDisplacementStrength(newster);
                
                if (strength != validator.getParseFailValue())
                {
                    setPopulationDisplacementStrength(false, strength);
                }
            }
        });
        populationTextDRS.focusedProperty().addListener((obster, oldster,
                newster) ->
        {
            if (!newster)
            {
                int strength =
                        popTab.getActivePopulation().getDisplacementStrength();
                
                populationTextDRS.setText(String.valueOf(strength));
            }
        });
        
        //----------------------------------------------------------------------
        // Pane resize listeners for the preview
        //----------------------------------------------------------------------
        previewContainer.widthProperty().addListener((obster, oldster, newster)
                ->
        {
            recenterCamera();
        });
        previewContainer.heightProperty().addListener((obster, oldster, newster)
                ->
        {
            recenterCamera();
        });
        
        //----------------------------------------------------------------------
        // Listeners for the Tabs. These are used for indicating which tabs are
        // currently open to the help box.
        //----------------------------------------------------------------------
        textureTab.setOnSelectionChanged(event ->
        {
            if (textureTab.isSelected())
            {
                int tabIndex = rightTabs.getTabs().indexOf(textureTab);
                
                helper.setRightTab(tabIndex);
            }
        });
        terrainTab.setOnSelectionChanged(event ->
        {
            if (terrainTab.isSelected())
            {
                int tabIndex = rightTabs.getTabs().indexOf(terrainTab);
                
                helper.setRightTab(tabIndex);
            }
        });
        populationTab.setOnSelectionChanged(event ->
        {
            if (populationTab.isSelected())
            {
                int tabIndex = rightTabs.getTabs().indexOf(populationTab);
                
                helper.setRightTab(tabIndex);
            }
        });
        
        renderTab.setOnSelectionChanged(event ->
        {
            if (renderTab.isSelected())
            {
                int tabIndex = bottomTabs.getTabs().indexOf(renderTab);
                
                helper.setBottomTab(tabIndex);
            }
        });
        cameraTab.setOnSelectionChanged(event ->
        {
            if (cameraTab.isSelected())
            {
                int tabIndex = bottomTabs.getTabs().indexOf(cameraTab);
                
                helper.setBottomTab(tabIndex);
            }
        });
        lightTab.setOnSelectionChanged(event ->
        {
            if (lightTab.isSelected())
            {
                int tabIndex = bottomTabs.getTabs().indexOf(lightTab);
                
                helper.setBottomTab(tabIndex);
            }
        });
        
        //----------------------------------------------------------------------
        // Listeners for the Help Box
        //----------------------------------------------------------------------
        
        // Key combo listener for scrolling the help box
        everything.setOnKeyPressed((KeyEvent event) ->
        {
            if (HELP_SCROLL_DOWN.match(event))
            {
                scrollHelpBox(true);
            }
            else if (HELP_SCROLL_UP.match(event))
            {
                scrollHelpBox(false);
            }
        });
        
        // A tab cannot directly have a hover listener, so its tab pane must
        // have one instead.
        rightTabs.hoverProperty().addListener((event)->
                displayHelp(rightTabs.getSelectionModel().getSelectedItem()));
        bottomTabs.hoverProperty().addListener((event)->
                displayHelp(bottomTabs.getSelectionModel().getSelectedItem()));
        
        // Return's the help box's message to its original, introductory message
        // when the cursor is moved from a tab pane to over the preview
        preview.hoverProperty().addListener((event)->
                helpBox.setText(helper.getDefaultText())
                );
        
        texturesScrollC.hoverProperty().addListener((event)->
                displayHelp(texturesScrollC));
        texturesButtonCA.hoverProperty().addListener((event)->
                displayHelp(texturesButtonCA));
        texturesScrollG.hoverProperty().addListener((event)->
                displayHelp(texturesScrollG));
        texturesButtonGA.hoverProperty().addListener((event)->
                displayHelp(texturesButtonGA));
        
        terrainTextVRW.hoverProperty().addListener((event)->
                displayHelp(terrainTextVRW));
        terrainTextVRD.hoverProperty().addListener((event)->
                displayHelp(terrainTextVRD));
        terrainComboDM.hoverProperty().addListener((event)->
                displayHelp(terrainComboDM));
        terrainTextDMS.hoverProperty().addListener((event)->
                displayHelp(terrainTextDMS));
        terrainComboDM2.hoverProperty().addListener((event)->
                displayHelp(terrainComboDM2));
        terrainComboBM.hoverProperty().addListener((event)->
                displayHelp(terrainComboBM));
        terrainComboSM.hoverProperty().addListener((event)->
                displayHelp(terrainComboSM));
        
        populationChoiceP.hoverProperty().addListener((event)->
                displayHelp(populationChoiceP));
        populationButtonPN.hoverProperty().addListener((event)->
                displayHelp(populationButtonPN));
        populationButtonPRG.hoverProperty().addListener((event)->
                displayHelp(populationButtonPRG));
        populationButtonPD.hoverProperty().addListener((event)->
                displayHelp(populationButtonPD));
        populationComboP.hoverProperty().addListener((event)->
                displayHelp(populationComboP));
        populationComboS.hoverProperty().addListener((event)->
                displayHelp(populationComboS));
        populationComboSW.hoverProperty().addListener((event)->
                displayHelp(populationComboSW));
        populationComboSH.hoverProperty().addListener((event)->
                displayHelp(populationComboSH));
        populationTextVRW.hoverProperty().addListener((event)->
                displayHelp(populationTextVRW));
        populationTextVRH.hoverProperty().addListener((event)->
                displayHelp(populationTextVRH));
        populationComboDR1.hoverProperty().addListener((event)->
                displayHelp(populationComboDR1));
        populationComboDR2.hoverProperty().addListener((event)->
                displayHelp(populationComboDR2));
        populationTextDRS.hoverProperty().addListener((event)->
                displayHelp(populationTextDRS));
        populationComboDM.hoverProperty().addListener((event)->
                displayHelp(populationComboDM));
        populationComboBM.hoverProperty().addListener((event)->
                displayHelp(populationComboBM));
        populationComboSM.hoverProperty().addListener((event)->
                displayHelp(populationComboSM));
        
        renderSpinnerRW.hoverProperty().addListener((event)->
                displayHelp(renderSpinnerRW));
        renderSpinnerRH.hoverProperty().addListener((event)->
                displayHelp(renderSpinnerRH));
        renderColorBC.hoverProperty().addListener((event)->
                displayHelp(renderColorBC));
        
        cameraSliderAH.hoverProperty().addListener((event)->
                displayHelp(cameraSliderAH));
        cameraSliderAV.hoverProperty().addListener((event)->
                displayHelp(cameraSliderAV));
        cameraSpinnerPAH.hoverProperty().addListener((event)->
                displayHelp(cameraSpinnerPAH));
        cameraSpinnerPAV.hoverProperty().addListener((event)->
                displayHelp(cameraSpinnerPAV));
        cameraSpinnerPAZ.hoverProperty().addListener((event)->
                displayHelp(cameraSpinnerPAZ));
        cameraSpinnerFOVD.hoverProperty().addListener((event)->
                displayHelp(cameraSpinnerFOVD));
        cameraRadioFOVH.hoverProperty().addListener((event)->
                displayHelp(cameraRadioFOVH));
        cameraRadioFOVV.hoverProperty().addListener((event)->
                displayHelp(cameraRadioFOVV));
        
        lightChoiceL.hoverProperty().addListener((event)->
                displayHelp(lightChoiceL));
        lightButtonLN.hoverProperty().addListener((event)->
                displayHelp(lightButtonLN));
        lightButtonLD.hoverProperty().addListener((event)->
                displayHelp(lightButtonLD));
        lightSpinnerPX.hoverProperty().addListener((event)->
                displayHelp(lightSpinnerPX));
        lightSpinnerPY.hoverProperty().addListener((event)->
                displayHelp(lightSpinnerPY));
        lightSpinnerPZ.hoverProperty().addListener((event)->
                displayHelp(lightSpinnerPZ));
        lightColorC.hoverProperty().addListener((event)->
                displayHelp(lightColorC));
    }
    
    /**
     * Adds all of the Population objects to the preview
     * 
     * @param previewItems A group containing all objects to be used in the
     *                     SubScene preview except the Populations
     */
    private void addPopulationsToPreview(Group previewItems)
    {
        Service[] services = popTab.getServices();
        
        // As long as there is at least 1 Population object...
        if (services.length > 0)
        {
            // ...disable all of the controls
            everything.setDisable(true);
            
            // For each of the Populations...
            for (short i = 0; i < services.length; i++)
            {
                // ...if the Population's Service is in use...
                if (popTab.getPopulation(i).isServicePrepared())
                {
                    // The incrementation variable must be final to be used in
                    // the lando
                    final short I = i;
            
                    // Once the service is finished...
                    services[i].setOnSucceeded(e ->
                    {
                        // ...perform the post-service activities on the active
                        // population.
                        popTab.getPopulation(I).concludeService();
                
                        addPopulationToPreview(I, previewItems);
                    });
                }
                // ...otherwise, if the Population is not using a Service...
                else
                {
                    // ...just add it.
                    addPopulationToPreview(i, previewItems);
                }
            }
        }
    }
    
    /**
     * Adds a population object to the preview. Re-enables all of the controls 
     * if there are no longer any population objects running a Service.
     * 
     * @param index The index of the Population object to add to the preview
     * @param previewItems All of the other items in the preview (terrain,
     *                     lights, etc)
     */
    private void addPopulationToPreview(short index, Group previewItems)
    {
        // Add the population's meshes to the Group
        previewItems.getChildren().add(popTab.getPopulation(index).getMeshes());
                
        // Apply the content to the preview pane
        preview.setRoot(previewItems);

        // If all of the populations Services are finished...
        if (!popTab.isServicePrepared())
        {
            // ...re-enable all of the controls
            everything.setDisable(false);
        }
    }
    
    /**
     * Displays the "import texture" dialog box and imports the texture of the
     * user's choosing into Sand Paper's system
     * 
     * @param color Whether or not a colored texture should be imported
     */
    private void addTexture(boolean color)
    {
        // If the user chooses a texture in the dialog...
        if (texTab.addTexture(everything.getScene().getWindow(), color))
        {
            // ...add the texture to the correct FlowPane
            if (color)
            {
                texturesFlowC.getChildren().add(texTab.getLastView(color));
            }
            else
            {
                texturesFlowG.getChildren().add(texTab.getLastView(color));
            }
        }
    }
    
    /**
     * Changes the terrain's displacement map to what is currently set in the
     * terrain tab's displacement combo box
     */
    @FXML
    private void changeDisplacement()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboDM.getValue().toString();
            
            // Get the texture belonging to that name
            TextureObject texster = texTab.getTexture(true, name);
            
            terrainImageDM.setImage(texster.getImage());

            // Set the image as the displacement map
            terTab.getTerrain().setDisplacement(texster);
            
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
     */
    @FXML
    private void changeFirstDisplacement()
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
     */
    @FXML
    private void changeHeight()
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
     */
    @FXML
    private void changePlacement()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {   
            // ...get the selected image name from the combo box
            String name = populationComboP.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(false, name);

            populationImageP.setImage(texster.getImage());
            
            // Set the image as the population's placement determinant
            popTab.getActivePopulation().setPlacement(
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's bump map to what is currently
     * set in the population tab's bump map combo box
     */
    @FXML
    private void changePopulationBump()
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
            popTab.getActivePopulation().setBump(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's diffuse map to what is
     * currently set in the population tab's diffuse map combo box
     */
    @FXML
    private void changePopulationDiffuse()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = populationComboDM.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            populationImageT.setImage(texster.getImage());
            
            // Set the image as the population's diffuse map
            popTab.getActivePopulation().setDiffuse(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's specular map to what is
     * currently set in the population tab's specular map combo box
     */
    @FXML
    private void changePopulationSpecular()
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
            popTab.getActivePopulation().setSpecular(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's second displacement map in
     * its displacement range to what is currently set in the population tab's
     * second displacement map combo box
     */
    @FXML
    private void changeSecondDisplacement()
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
            popTab.getActivePopulation().setSecondDisplacement(
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's shift image to what is
     * currently set in the population tab's shift combo box
     */
    @FXML
    private void changeShift()
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
     */
    @FXML
    private void changeTerrainBump()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboBM.getValue().toString();
            
            // Get the texture belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            terrainImageBM.setImage(texster.getImage());
            
            // Set the image as the bump map
            terTab.getTerrain().setBump(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the terrain's diffuse map to what is currently set in the diffuse
     * map combo box
     */
    @FXML
    private void changeTerrainDiffuse()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboDM2.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);

            terrainImageT.setImage(texster.getImage());

            // Set the image as the diffuse map
            terTab.getTerrain().setDiffuse(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the terrain's specular map to what is currently set in the
     * specular map combo box
     */
    @FXML
    private void changeTerrainSpecular()
    {
        // If action listeners are not to be ignored at the moment...
        if (listen)
        {
            // ...get the selected image name from the combo box.
            String name = terrainComboSM.getValue().toString();
            
            // Get the image belonging to that name
            TextureObject texster = texTab.getTexture(true, name);
            
            terrainImageSM.setImage(texster.getImage());
            
            // Set the image as the specular map
            terTab.getTerrain().setSpecular(texster);
        
            refreshPreview();
        }
    }
    
    /**
     * Changes the currently-selected population's width image to what is
     * currently set in the population tab's width combo box
     */
    @FXML
    private void changeWidth()
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
            popTab.getActivePopulation().setWidth(
                    terTab.getTerrain().getPoints(), texster);
        
            refreshPreview();
        }
    }
    
    /**
     * The actions performed for when the user clicks the button in the colored
     * textures FlowPane
     */
    @FXML
    private void colorTextureButtonClick()
    {
        // Whether or not the textures being dealt with have color
        final boolean COLOR = true;
        
        listen = false;
        
        textureButtonClick(COLOR);
       
        // Create a list of the names of the color textures imported so far
        ObservableList<String> obster = texTab.getTextureNames(COLOR);
        
        // Set the list to the combo boxes for maps
        terrainComboDM2.setItems(obster);
        terrainComboDM.setItems(obster);
        terrainComboBM.setItems(obster);
        terrainComboSM.setItems(obster);
        populationComboS.setItems(obster);
        populationComboDR1.setItems(obster);
        populationComboDR2.setItems(obster);
        populationComboDM.setItems(obster);
        populationComboBM.setItems(obster);
        populationComboSM.setItems(obster);
        
        listen = true;
    }
    
    /**
     * Creates a new light. For use when the user clicks the "New" button on the
     * light tab.
     */
    @FXML
    private void createLight()
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
    private void createPopulation()
    {
        // Get the camera's rotation values
        short xRotate = (short)camTab.getXRotate().getAngle();
        short yRotate = (short)camTab.getYRotate().getAngle();
        
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
     * Decreases the strength of the displacement of the active population by 1.
     * The TextField is also updated to reflect the changes.
     */
    @FXML
    private void decrementPopulationDisplacementStrength()
    {
        // Get and parse the displacement strength
        short strength = validator.parseDisplacementStrength(
                populationTextDRS.getText());
        
        // If the displacement strength was parsed successfully and the
        // decremented strength validates...
        if (strength != validator.getParseFailValue() && 
                validator.validateDisplacementStrength(strength--))
        {
            // ...set the decremented value as the new displacement strength.
            setPopulationDisplacementStrength(true, strength--);
        }
    }
    
    /**
     * Decreases the height (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    private void decrementPopulationVertexHeight()
    {
        short height = validator.parsePopulationSize(
                populationTextVRH.getText());
        
        if (height != validator.getParseFailValue() &&
                validator.validateSize(height--))
        {
            setPopulationVertexHeight(true, height--);
        }
    }
    
    /**
     * Decreases the width (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    private void decrementPopulationVertexWidth()
    {
        short width = validator.parsePopulationSize(
                populationTextVRW.getText());
        
        if (width != validator.getParseFailValue() &&
                validator.validateSize(width--))
        {
            setPopulationVertexWidth(true, width--);
        }
    }
    
    /**
     * Decreases the displacement strength of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void decrementTerrainDisplacementStrength()
    {
        short strength = validator.parseDisplacementStrength(
                terrainTextDMS.getText());
        
        if (strength != validator.getParseFailValue() &&
                validator.validateDisplacementStrength(strength--))
        {
            setTerrainDisplacementStrength(true, strength--);
        }
    }
    
    /**
     * Decreases the depth (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void decrementTerrainVertexDepth()
    {
        short depth = validator.parseTerrainSize(terrainTextVRD.getText());
        
        if (depth != validator.getParseFailValue() &&
                validator.validateSize(depth--))
        {
            setTerrainVertexDepth(true, depth--);
        }
    }
    
    /**
     * Decreases the width (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void decrementTerrainVertexWidth()
    {
        short width = validator.parseTerrainSize(terrainTextVRW.getText());
        
        if (width != validator.getParseFailValue() &&
                validator.validateSize(width--))
        {
            setTerrainVertexWidth(true, width--);
        }
    }
    
    /**
     * Deletes the currently-selected light
     */
    @FXML
    private void deleteLight()
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
    private void deletePopulation()
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
     * Displays information of the given control's key in the help box
     * 
     * @param controlKey A key used to look up the information for the help box
     */
    private void displayHelp(String controlKey)
    {
        double helpBoxWidth = helpBox.getWidth();
        double helpBoxHeight = helpBox.getHeight();
        
        String info;
        
        Font boxFont = helpBox.getFont();
        
        info = helper.getText(helpBoxWidth, helpBoxHeight, controlKey, boxFont);
        
        helpBox.setText(info);
    }
    
    /**
     * Displays information of the given control in the help box
     * 
     * @param conster The control to display information of
     */
    private void displayHelp(Control conster)
    {
        // The control's CSS ID is used as the key for getting the control's
        // info
        displayHelp(conster.getId());
    }
    
    /**
     * Displays information of the given tab in the help box
     * 
     * @param tabster The tab to display information of
     */
    private void displayHelp(Tab tabster)
    {
        displayHelp(tabster.getId());
    }
    
    /**
     * Either enables or disables the controls for manipulating lights. This
     * does not include the "new" button, as that button should always be
     * enabled.
     * 
     * @param toEnable Whether or not to enable or disable the controls
     */
    private void enableLightControls(boolean toEnable)
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
    private void enablePopulationControls(boolean toEnable)
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
        populationButtonDRSD.setDisable(!toEnable);
        populationTextDRS.setDisable(!toEnable);
        populationButtonDRSI.setDisable(!toEnable);
        populationComboDM.setDisable(!toEnable);
        populationComboBM.setDisable(!toEnable);
        populationComboSM.setDisable(!toEnable);
    }
    
    /**
     * Exits the program if the user chooses to do so
     */
    @FXML
    private void exit()
    {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        
        styleDialog(confirmation);

        confirmation.setTitle("Exit?");
        confirmation.setHeaderText("");
        confirmation.setContentText("Are you sure you want to exit?");

        Optional<ButtonType> answer = confirmation.showAndWait();

        if (answer.get() == ButtonType.OK)
        {
            Platform.exit();
        }
    }
    
    /**
     * Formats the controls that need to be formatted
     */
    private void formatControls()
    {
        validator.formatNumericTextField(terrainTextDMS);
        validator.formatNumericTextField(terrainTextVRW);
        validator.formatNumericTextField(terrainTextVRD);
        validator.formatNumericTextField(populationTextDRS);
        validator.formatNumericTextField(populationTextVRW);
        validator.formatNumericTextField(populationTextVRH);
        
        validator.formatNumericSpinner(renderSpinnerRW);
        validator.formatNumericSpinner(renderSpinnerRH);
        validator.formatNumericSpinner(cameraSpinnerPAH);
        validator.formatNumericSpinner(cameraSpinnerPAV);
        validator.formatNumericSpinner(cameraSpinnerPAZ);
        validator.formatNumericSpinner(lightSpinnerPX);
        validator.formatNumericSpinner(lightSpinnerPY);
        validator.formatNumericSpinner(lightSpinnerPZ);
    }
    
    /**
     * The actions performed for when the user clicks the button in the 
     * grayscale textures FlowPane
     */
    @FXML
    private void grayTextureButtonClick()
    {
        // Whether or not the textures being dealt with have color
        boolean COLOR = false;
        
        listen = false;
        
        textureButtonClick(COLOR);
        
        // Create a list of the names of the color textures imported so far
        ObservableList<String> obster = texTab.getTextureNames(COLOR);
        
        // Set the list to the combo boxes for maps
        populationComboP.setItems(obster);
        populationComboSW.setItems(obster);
        populationComboSH.setItems(obster);
        
        listen = true;
    }
    
    /**
     * Increases the strength of the displacement of the active population by 1.
     * The TextField is also updated to reflect the changes.
     */
    @FXML
    private void incrementPopulationDisplacementStrength()
    {
        short strength = validator.parseDisplacementStrength(
                populationTextDRS.getText());
        
        if (strength != validator.getParseFailValue() &&
                validator.validateDisplacementStrength(strength++))
        {
            setPopulationDisplacementStrength(true, strength++);
        }
    }
    
    /**
     * Increases the height (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    private void incrementPopulationVertexHeight()
    {
        short height = validator.parsePopulationSize(
                populationTextVRH.getText());
        
        if (height != validator.getParseFailValue() &&
                validator.validateSize(height++))
        {
            setPopulationVertexHeight(true, height++);
        }
    }
    
    /**
     * Increases the width (in vertices) of the active population by 1. The
     * TextField is also updated to reflect the changes.
     */
    @FXML
    private void incrementPopulationVertexWidth()
    {
        short width = validator.parsePopulationSize(
                populationTextVRW.getText());
        
        if (width != validator.getParseFailValue() &&
                validator.validateSize(width++))
        {
            setPopulationVertexWidth(true, width++);
        }
    }
    
    /**
     * Increases the displacement strength of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void incrementTerrainDisplacementStrength()
    {
        short strength = validator.parseDisplacementStrength(
                terrainTextDMS.getText());
        
        if (strength != validator.getParseFailValue() &&
                validator.validateDisplacementStrength(strength++))
        {
            setTerrainDisplacementStrength(true, strength++);
        }
    }
    
    /**
     * Increases the depth (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void incrementTerrainVertexDepth()
    {
        short depth = validator.parseTerrainSize(terrainTextVRD.getText());
        
        if (depth != validator.getParseFailValue() &&
                validator.validateSize(depth++))
        {
            setTerrainVertexDepth(true, depth++);
        }
    }
    
    /**
     * Increases the width (in vertices) of the terrain by 1. The TextField is
     * also updated to reflect the changes.
     */
    @FXML
    private void incrementTerrainVertexWidth()
    {
        short width = validator.parseTerrainSize(terrainTextVRW.getText());
        
        if (width != validator.getParseFailValue() &&
                validator.validateSize(width++))
        {
            setTerrainVertexWidth(true, width++);
        }
    }
    
    /**
     * Loads the properties of the active light into the light tab's controls
     */
    private void loadLight()
    {
        LightObject activeLight = ligTab.getActiveLight();
        
        lightSpinnerPX.getValueFactory().setValue(activeLight.getPercentageX());
        lightSpinnerPY.getValueFactory().setValue(activeLight.getPercentageY());
        lightSpinnerPZ.getValueFactory().setValue(activeLight.getPercentageZ());
        
        lightColorC.setValue(activeLight.getColor());
    }
    
    /**
     * Loads the properties of the active population into the population tab's
     * controls
     */
    private void loadPopulation()
    {
        // The names of the maps to load
        String placementName;
        String shiftName;
        String widthName;
        String heightName;
        String displacementName1;
        String displacementName2;
        String diffuseName;
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
        diffuseName = activePopulation.getDiffuse().getName();
        bumpName = activePopulation.getBump().getName();
        specularName = activePopulation.getSpecular().getName();
        
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
        TextureObject diffuseTexture = texTab.getTexture(true, diffuseName);
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
        populationComboDM.setValue(diffuseName);
        populationComboBM.setValue(bumpName);
        populationComboSM.setValue(specularName);
        
        // Set the TextFields
        populationTextDRS.setText(Integer.toString(
                activePopulation.getDisplacementStrength()));
        populationTextVRH.setText(
                Integer.toString(activePopulation.getVertexHeight()));
        populationTextVRW.setText(
                Integer.toString(activePopulation.getVertexWidth()));
    }
    
    /**
     * Creates a tooltip for the given control
     * 
     * @param conster The control to assign a tooltip to
     */
    private void loadTooltip(Control conster)
    {
        // The key to be looked up for each control is stored as the control's
        // CSS ID.
        String key = conster.getId();
        String title = helper.getTitle(key);
        
        conster.setTooltip(new Tooltip(title));
    }
    
    /**
     * Creates a tooltip for the given tab
     * 
     * @param tabster The tab to assign a tooltip to
     */
    private void loadTooltip(Tab tabster)
    {
        String key = tabster.getId();
        String title = helper.getTitle(key);
        
        tabster.setTooltip(new Tooltip(title));
    }
    
    /**
     * Creates tooltips for all of the controls
     */
    private void loadTooltips()
    {
        loadTooltip(textureTab);
        loadTooltip(texturesScrollC);
        // These 2 commented buttons throw null exceptions. Not sure why
        //loadTooltip(texturesButtonCA);
        loadTooltip(texturesScrollG);
        //loadTooltip(texturesButtonGA);
        
        loadTooltip(terrainTab);
        loadTooltip(terrainTextVRW);
        loadTooltip(terrainTextVRD);
        loadTooltip(terrainComboDM);
        loadTooltip(terrainTextDMS);
        loadTooltip(terrainComboDM2);
        loadTooltip(terrainComboBM);
        loadTooltip(terrainComboSM);
        
        loadTooltip(populationTab);
        loadTooltip(populationChoiceP);
        loadTooltip(populationButtonPN);
        loadTooltip(populationButtonPRG);
        loadTooltip(populationButtonPD);
        loadTooltip(populationComboP);
        loadTooltip(populationComboS);
        loadTooltip(populationComboSW);
        loadTooltip(populationComboSH);
        loadTooltip(populationTextVRW);
        loadTooltip(populationTextVRH);
        loadTooltip(populationComboDR1);
        loadTooltip(populationComboDR2);
        loadTooltip(populationTextDRS);
        loadTooltip(populationComboDM);
        loadTooltip(populationComboBM);
        loadTooltip(populationComboSM);
        
        loadTooltip(renderTab);
        loadTooltip(renderSpinnerRW);
        loadTooltip(renderSpinnerRH);
        loadTooltip(renderColorBC);
        
        loadTooltip(cameraTab);
        loadTooltip(cameraSliderAH);
        loadTooltip(cameraSliderAV);
        loadTooltip(cameraSpinnerPAH);
        loadTooltip(cameraSpinnerPAV);
        loadTooltip(cameraSpinnerPAZ);
        loadTooltip(cameraSpinnerFOVD);
        loadTooltip(cameraRadioFOVH);
        loadTooltip(cameraRadioFOVV);
        
        loadTooltip(lightTab);
        loadTooltip(lightChoiceL);
        loadTooltip(lightButtonLN);
        loadTooltip(lightButtonLD);
        loadTooltip(lightSpinnerPX);
        loadTooltip(lightSpinnerPY);
        loadTooltip(lightSpinnerPZ);
        loadTooltip(lightColorC);
    }
    
    /**
     * Shows the About box
     */
    @FXML
    private void openAbout()
    {
        Alert alster = new Alert(AlertType.INFORMATION);
        
        styleDialog(alster);
        
        alster.setTitle("About Sand Paper");
        alster.setHeaderText("Use images to create 3D scenery");
        alster.setContentText("Sand Paper is a 3D modeling software solution "
                + "for graphic designers that have limited knowledge with 3D "
                + "modeling.\n\n"
                + "version 0.8\n\n"
                + "Copyright (C) 2019-2021 George Tiersma\n\n"
                + "This program is free software: you can redistribute it "
                + "and/or modify it under the terms of the GNU General Public "
                + "License as published by the Free Software Foundation, "
                + "either version 3 of the License, or (at your option) any "
                + "later version.");
        
        alster.setGraphic(new ImageView("/icons/icon.png"));
        
        alster.showAndWait();
    }
    
    /**
     * Opens the beginner-level PDF tutorial file with the default program set
     * by the OS
     */
    @FXML
    private void openBeginnerTutorial()
    {
        final String FILE = "tutorials\\beginner.pdf";
        
        String command;
        
        // If the OS is Windows...
        if (isWindows())
        {
            // ...use the windows command to open a file.
            command = "cmd /C start " + FILE;
        }
        // ...otherwise...
        else
        {
            // ...it must be a linux OS. Use the linux command.
            command = "xdg-open " + FILE;
        }
        
        try
        {
            // Open the tutorial
            Runtime.getRuntime().exec(command);
        }
        // If it cannot be opened...
        catch (IOException ex)
        {
            // ...tell the user.
            showTutorialErrorDialog();
        }
    }
    
    /**
     * Prepares the preview for saving a rendered image. This should be executed
     * immediately before an image is to be saved.
     */
    private void prepareForRender()
    {
        int width = renTab.getWidth();
        int height = renTab.getHeight();
        
        // If one of the render's spinners still has focus, any changes made in
        // that spinner may have not been updated. This will block fixes that.
        if (renderSpinnerRW.isFocused())
        {
            width = validateSpinner(true, width, renderSpinnerRW);
            renTab.setWidth(width);
        }
        else if (renderSpinnerRH.isFocused())
        {
            height = validateSpinner(true, height, renderSpinnerRH);
            renTab.setHeight(height);
        }
        
        // Adjust the camera's position so that the render will be centered on
        // the same point as the preview
        camTab.setCameraOffset(width / 2, height / 2);
        
        // Zoom the camera so that approximately the same content visible in the
        // preview is visible in the render
        camTab.zoomForResize(cameraSpinnerPAZ.getValue(),
                renTab.getDimensionAverage());
        
        // Unbind the preview from the pane's size
        preview.heightProperty().unbind();
        preview.widthProperty().unbind();
        // Resize the preview to match the size of the render
        preview.setWidth(width);
        preview.setHeight(height);
    }
    
    /**
     * Gets the preview ready to be shown
     */
    private void preparePreview()
    {
        terTab.prepareTerrain();
        
        resetPreviewSize();
        
        refreshPreview();
        preview.setCamera(camTab.getCamera());
        preview.setFill(renTab.getBackColor());
    }
    
    /**
     * Re-centers the camera on the terrain. For use whenever preview size is
     * changed.
     */
    private void recenterCamera()
    {
        // The average of the preview pane's width and height
        double previewAverage;
        double previewWidth = previewContainer.getWidth();
        double previewHeight = previewContainer.getHeight();
        
        previewAverage = (previewWidth + previewHeight) / 2;
        
        // Center the terrain in the preview
        camTab.setCameraOffset(previewWidth / 2, previewHeight / 2);
        
        // Zoom the camera in or out if needed
        camTab.zoomForResize(cameraSpinnerPAZ.getValue(), previewAverage);
    }
    
    /**
     * Re-centers objects that are positioned relative to the terrain (such as
     * the camera and lights). For use whenever the terrain's size or shape has
     * changed.
     */
    private void recenterOnTerrain()
    {
        // Estimation of the center point of the terrain
        int terrainCenterX;
        int terrainCenterY;
        int terrainCenterZ;
        
        // The greatest distance of a point from the terrain's center
        int terrainFarPoint;
            
        Terrain terster = terTab.getTerrain();
        
        terrainCenterX = (int)terster.getCenter('x');
        terrainCenterY = (int)terster.getCenter('y');
        terrainCenterZ = (int)terster.getCenter('z');
        terrainFarPoint = (int)terster.getFurthestPoint();
        
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
    private void refreshPreview()
    {
        int lightAmount = ligTab.getLightAmount();
        
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
        
        // Apply the content to the preview pane
        preview.setRoot(previewItems);
        
        addPopulationsToPreview(previewItems);
    }
    
    /**
     * Checks whether or not a button on the texture tab should be used to add
     * or remove textures and adjusts its text and ID as seen as appropriate.
     * 
     * @param color Whether or not the button is used for colored textures or 
     *              not
     */
    private void refreshTextureButton(boolean color)
    {
        // Parts that are combined to create the correct ID for when the button
        // is looked-up by the adviser
        final String ADD = "Add";
        final String REMOVE = "Remove";
        final String COLOR = "Colored";
        final String GRAYSCALE = "Grayscale";
        
        final String TEXT_ADD = "Import\nImage";
        final String TEXT_REMOVE = "Remove\nImage(s)";
        
        // The ID always begins with the word "textures"
        String id = "textures";
        
        // The texture button being dealt with. Initialized to the button in the
        // grayscale textures box.
        Button textureButton = texturesButtonGA;
        
        // If the texture box the button belongs to is for colored textures..,
        if (color)
        {
            // ...set it to the button for colored textures.
            textureButton = texturesButtonCA;
            // Add the word "Color" to the ID
            id = id + COLOR;
        }
        // ...otherwise...
        else
        {
            // ...add the word "Grayscale" to the ID
            id = id + GRAYSCALE;
        }
        
        // If there is a texture selected of the indicated color orientation...
        if (!texTab.getSelectedIndices(color).isEmpty())
        {
            // ...the button's text will indicate that it will remove the 
            // selected textures.
            textureButton.setText(TEXT_REMOVE);
            // Add the word "remove" to the ID
            id = id + REMOVE;
        }
        // ...otherwise, if no textures are selected...
        else
        {
            // ...the button's text will indicate that it will add a texture.
            textureButton.setText(TEXT_ADD);
            id = id + ADD;
        }
        
        textureButton.setId(id);
    }
    
    /**
     * Refreshes the currently-selected population, re-randomizing some of the
     * population's properties
     */
    @FXML
    private void regeneratePopulation()
    {
        // The title for the progress dialog
        String actionDescription = "Re-generating Population";
        
        popTab.getActivePopulation().load(actionDescription,
                terTab.getTerrain().getPoints());
        
        refreshPreview();
    }
    
    /**
     * Removes a specified texture from the terrain and all populations
     * 
     * @param color Whether or not the texture being removed is colored or
     *              colorless
     * @param index The index in the FlowPane of the texture to remove
     * @param texster The texture to be removed
     */
    private void removeTexture(boolean color, short index,
            TextureObject texster)
    {
        // A blank white image to replace the texture with
        final TextureObject BLANK_TEXTURE = new TextureObject();
        
        String name = texster.getName();
        
        float[] terrainPoints;
        
        ImageView viewster = texster.getView();
        
        Population activePopulation = popTab.getActivePopulation();
            
        Terrain terster = terTab.getTerrain();
        
        terrainPoints = terster.getPoints();
            
        // If it is a colored texture...
        if (color)
        {
            // ...remove it from the FlowPane.
            texturesFlowC.getChildren().remove(viewster);
            
            // If it is currently set as the terrain's displacement map...
            if (terster.getDisplacement().is(name))
            {
                // ...remove the ImageView.
                terrainImageDM.setImage(null);
                
                // Give the terrain a blank displacement map.
                terster.setDisplacement(BLANK_TEXTURE);
            }
            if (terster.getDiffuse().is(name))
            {
                terrainImageT.setImage(null);
                
                terster.setDiffuse(BLANK_TEXTURE);
            }
            if (terster.getBump().is(name))
            {
                terrainImageBM.setImage(null);
                
                terster.setBump(BLANK_TEXTURE);
            }
            if (terster.getSpecular().is(name))
            {
                terrainImageT.setImage(null);
                
                terster.setSpecular(BLANK_TEXTURE);
            }
            
            // Removing the shift maps that match this texture of any
            // of the populations, if at least 1 map was removed...
            if (popTab.removeShift(name))
            {
                // ...and if the active population had its shift map removed...
                if (activePopulation.getShift().is(name))
                {
                    // ...clear the population's shift map ImageView.
                    populationImageS.setImage(null);
                }
            }
            if (popTab.removeFirstDisplacement(name, terrainPoints))
            {
                if (activePopulation.getFirstDisplacement().is(name))
                {
                    populationImageDR1.setImage(null);
                }
            }
            if (popTab.removeSecondDisplacement(name, terrainPoints))
            {
                if (activePopulation.getSecondDisplacement().is(name))
                {
                    populationImageDR2.setImage(null);
                }
            }
            if (popTab.removeDiffuse(name))
            {
                if (activePopulation.getDiffuse().is(name))
                {
                    populationImageT.setImage(null);
                }
            }
            if (popTab.removeBump(name))
            {
                if (activePopulation.getBump().is(name))
                {
                    populationImageBM.setImage(null);
                }
            }
            if (popTab.removeSpecular(name))
            {
                if (activePopulation.getSpecular().is(name))
                {
                    populationImageSM.setImage(null);
                }
            }
        }
        // ...otherwise, the texture to be removed must be grayscale.
        else
        {
            texturesFlowG.getChildren().remove(viewster);
            
            if (popTab.removePlacement(name, terrainPoints))
            {
                if (activePopulation.getPlacement().is(name))
                {
                    populationImageP.setImage(null);
                }
            }
            if (popTab.removeWidth(name, terrainPoints))
            {
                if (activePopulation.getWidth().is(name))
                {
                    populationImageSW.setImage(null);
                }
            }
            if (popTab.removeHeight(name, terrainPoints))
            {
                if (activePopulation.getHeight().is(name))
                {
                    populationImageSH.setImage(null);
                }
            }
        }
        
        // Remove the texture from the TextureTab's ArrayList
        texTab.deleteTexture(color, index);
    }
    
    /**
     * Removes the texture ImageViews of the given indices from the correct
     * FlowPane
     * 
     * @param color Whether or not to remove colored or colorless textures
     * @param indicesToRemove The indices indicating which ImageViews to remove
     *                        from the FlowPane
     */
    private void removeTextures(boolean color, ArrayList<Short> indicesToRemove)
    {
        // Get the user's confirmation
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        styleDialog(confirmation);
        confirmation.setTitle("Continue?");
        confirmation.setHeaderText("Are you sure you want to remove the "
                + "selected textures?");
        confirmation.setContentText("They won't being deleted from your "
                + "computer. They will only be removed from Sand Paper.");
        Optional<ButtonType> answer = confirmation.showAndWait();
        
        // If the user gives the ok...
        if (answer.get() == ButtonType.OK)
        {
            // ...for each index (in reverse order to prevent the ArrayList's
            // resizing from throwing off the loop)...
            int lastIndex = indicesToRemove.size() - 1;
            for (int i = lastIndex; i >= 0; i--)
            {
                // ...get it.
                short currentIndex = indicesToRemove.get(i);
            
                TextureObject currentTexture
                        = texTab.getTexture(color, currentIndex);
            
                removeTexture(color, currentIndex, currentTexture);
            }
            
            // If there is at least 1 population...
            if (popTab.populationExists())
            {
                // ...reload it.
                loadPopulation();
            }
            
            // No texture should be selected now, so the button will need to
            // change its text back
            refreshTextureButton(color);
        }
    }
    
    /**
     * Removes the color from the ImageView previews for maps that are to be
     * grayscale
     */
    private void removeViewColor()
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
     */
    @FXML
    private void reset()
    {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        
        styleDialog(confirmation);

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
            
            // The index of the first image in each texture flow pane
            final int FIRST_IMAGE_INDEX = 1;
            
            // Reset all of the variables in the tab objects
            texTab = new TextureTab();
            terTab = new TerrainTab();
            renTab = new RenderTab();
            camTab = new CameraTab();
            ligTab = new LightTab();
            popTab = new PopulationTab();
            
            // Remove combo box choices
            terrainComboDM.setItems(null);
            terrainComboDM2.setItems(null);
            terrainComboBM.setItems(null);
            terrainComboSM.setItems(null);
            populationComboP.setItems(null);
            populationComboS.setItems(null);
            populationComboSW.setItems(null);
            populationComboSH.setItems(null);
            populationComboDR1.setItems(null);
            populationComboDR2.setItems(null);
            populationComboDM.setItems(null);
            populationComboBM.setItems(null);
            populationComboSM.setItems(null);
            
            // Empty combo boxes
            terrainComboDM.setValue("");
            terrainComboDM2.setValue("");
            terrainComboBM.setValue("");
            terrainComboSM.setValue("");
            
            // Remove all imported textures
            while (texturesFlowC.getChildren().size() > FIRST_IMAGE_INDEX)
            {
                texturesFlowC.getChildren().remove(FIRST_IMAGE_INDEX);
            }
            while (texturesFlowG.getChildren().size() > FIRST_IMAGE_INDEX)
            {
                texturesFlowG.getChildren().remove(FIRST_IMAGE_INDEX);
            }
            
            // Remove image previews
            terrainImageDM.setImage(null);
            terrainImageT.setImage(null);
            terrainImageBM.setImage(null);
            terrainImageSM.setImage(null);
            
            // Reset sliders
            cameraSliderAH.setValue(camTab.getDefaultHorizontalAngle());
            cameraSliderAV.setValue(camTab.getDefaultVerticalAngle());
            
            // Reset spinner settings
            renderSpinnerRW.getValueFactory().setValue(
                    renTab.getDefaultWidth());
            renderSpinnerRH.getValueFactory().setValue(
                    renTab.getDefaultHeight());
            cameraSpinnerFOVD.getValueFactory().setValue(
                    camTab.getDefaultField());
            cameraSpinnerPAH.getValueFactory().setValue(0);
            cameraSpinnerPAV.getValueFactory().setValue(0);
            cameraSpinnerPAZ.getValueFactory().setValue(0);
            
            // Reset spinner text
            renderSpinnerRW.getEditor().textProperty().setValue(
                    String.valueOf(renTab.getDefaultWidth()));
            renderSpinnerRH.getEditor().textProperty().setValue(
                    String.valueOf(renTab.getDefaultHeight()));
            cameraSpinnerFOVD.getEditor().textProperty().setValue(
                    String.valueOf(camTab.getDefaultField()));
            cameraSpinnerPAH.getEditor().textProperty().setValue("0");
            cameraSpinnerPAV.getEditor().textProperty().setValue("0");
            cameraSpinnerPAZ.getEditor().textProperty().setValue("0");
            
            cameraRadioFOVH.setSelected(true);
            
            // Reset text field text
            terrainTextDMS.setText(Integer.toString(
                    terTab.getDefaultStrength()));
            terrainTextVRD.setText(Integer.toString(terTab.getDefaultSize()));
            terrainTextVRW.setText(Integer.toString(terTab.getDefaultSize()));
            
            renderColorBC.setValue(renTab.getDefaultBackColor());
            
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
    private void resetLightControls()
    {
        int x = ligTab.getDefaultX();
        int y = ligTab.getDefaultY();
        int z = ligTab.getDefaultZ();
        
        // Set the spinner's setting to its original value
        lightSpinnerPX.getValueFactory().setValue(x);
        lightSpinnerPY.getValueFactory().setValue(y);
        lightSpinnerPZ.getValueFactory().setValue(z);
        
        // Set the spinner's text to its original value
        lightSpinnerPX.getEditor().textProperty().setValue(String.valueOf(x));
        lightSpinnerPY.getEditor().textProperty().setValue(String.valueOf(y));
        lightSpinnerPZ.getEditor().textProperty().setValue(String.valueOf(z));
        
        lightColorC.setValue(ligTab.getDefaultColor());
    }
    
    /**
     * Resets the controls on the population tab to their default values
     * (excluding the choice box listing all of the populations)
     */
    private void resetPopulationControls()
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
        populationTextDRS.setText(
                Integer.toString(popTab.getDefaultDisplacementStrength()));
        populationComboDM.setValue("");
        populationComboBM.setValue("");
        populationComboSM.setValue("");
    }
    
    /**
     * Resets the preview's size, re-centering the camera on the terrain
     */
    private void resetPreviewSize()
    {
        // To be centered, the terrain must be adjusted by half of the preview's
        // size
        camTab.setCameraOffset(DEFAULT_PREVIEW_WIDTH / 2, DEFAULT_PREVIEW_HEIGHT
                / 2);
        
        camTab.setZoom(cameraSpinnerPAZ.getValue());
        
        // Force the preview to automatically resize when the user resizes the
        // adjacent panes
        preview.heightProperty().bind(previewContainer.heightProperty());
        preview.widthProperty().bind(previewContainer.widthProperty());
        
        recenterOnTerrain();
        
        recenterCamera();
    }
    
    /**
     * Saves a rendered image, replacing the last image that was saved. If there
     * is no image that was saved before this one, it functions the same as the
     * saveAs method.
     */
    @FXML
    private void save()
    {
        prepareForRender();
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.save(everything.getScene().getWindow(), writster);
        
        resetPreviewSize();
    }
    
    /**
     * Has the user save a rendered image through a file chooser
     */
    @FXML
    private void saveAs()
    {
        prepareForRender();
        
        // Create a screenshot of the preview
        WritableImage writster
                = preview.snapshot(new SnapshotParameters(), null);
        
        // Save the screenshot
        renTab.saveAs(everything.getScene().getWindow(), writster);
        
        resetPreviewSize();
    }
    
    /**
     * Scrolls the help box if it currently has a scroll bar
     * 
     * @param down Whether or not it should scroll down
     */
    private void scrollHelpBox(boolean down)
    {
        final byte SCROLL_AMOUNT = 5;
        
        if (down)
        {
            helpBox.setScrollTop(helpBox.getScrollTop() + SCROLL_AMOUNT);
        }
        else
        {
            helpBox.setScrollTop(helpBox.getScrollTop() - SCROLL_AMOUNT);
        }
    }
    
    /**
     * Sets the displacement strength of the currently-selected population. The
     * TextField is also updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param strength The height to be set
     */
    private void setPopulationDisplacementStrength(boolean updateTextBox,
            short strength)
    {
        listen = false;
        
        popTab.getActivePopulation().setDisplacementStrength(strength,
                terTab.getTerrain().getPoints());
        
        refreshPreview();
        
        if (updateTextBox)
        {
            populationTextDRS.setText(Short.toString(strength));
        }
        
        listen = true;
    }
    
    /**
     * Sets the height (in vertices) of the currently-selected population. The
     * TextField is also updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param height The height to be set
     */
    private void setPopulationVertexHeight(boolean updateTextBox, short height)
    {
        listen = false;
        
        popTab.getActivePopulation().setVertexHeight(height,
                terTab.getTerrain().getPoints());
        
        refreshPreview();
        
        if (updateTextBox)
        {
            populationTextVRH.setText(Short.toString(height));
        }
        
        listen = true;
    }
    
    /**
     * Sets the width (in vertices) of the currently-selected population. The
     * TextField is also updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param width The width to be set
     */
    private void setPopulationVertexWidth(boolean updateTextBox, short width)
    {
        listen = false;
        
        popTab.getActivePopulation().setVertexWidth(width,
                terTab.getTerrain().getPoints());
        
        refreshPreview();
        
        if (updateTextBox)
        {
            populationTextVRW.setText(Short.toString(width));
        }
        
        listen = true;
    }
    
    /**
     * Sets the displacement strength of the terrain. The TextField is also
     * updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param strength The height to be set
     */
    private void setTerrainDisplacementStrength(boolean updateTextBox,
            short strength)
    {
        listen = false;
        
        terTab.getTerrain().setDisplacementStrength(strength);
            
        popTab.repositionPopulations(terTab.getTerrain().getPoints());
        
        if (updateTextBox)
        {
            terrainTextDMS.setText(Short.toString(strength));
        }
        
        recenterOnTerrain();
            
        refreshPreview();
        
        listen = true;
    }
    
    /**
     * Sets the depth (in vertices) of the terrain. The TextField is
     * also updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param depth The depth to be set
     */
    private void setTerrainVertexDepth(boolean updateTextBox, short depth)
    {
        listen = false;
        
        terTab.getTerrain().setDepth(depth);
            
        updatePopulationsForTerrainSizeChange(false, depth);
            
        ligTab.repositionLights();
                
        if (updateTextBox)
        {
            terrainTextVRD.setText(Short.toString(depth));
        }
        
        listen = true;
    }
    
    /**
     * Sets the width (in vertices) of the terrain. The TextField is
     * also updated to reflect the changes.
     * 
     * @param updateTextBox Whether or not the TextBox should be updated to
     *                      display the given strength.
     *                      Updating the TextBox when this method is called from
     *                      a listener can cause an exception within JavaFX, so
     *                      it would be a good idea to set this to false if it
     *                      is not needed.
     * @param width The width to be set
     */
    private void setTerrainVertexWidth(boolean updateTextBox, short width)
    {
        listen = false;
        
        terTab.getTerrain().setWidth(width);
            
        updatePopulationsForTerrainSizeChange(true, width);
            
        ligTab.repositionLights();
           
        if (updateTextBox)
        {
            terrainTextVRW.setText(Short.toString(width));
        }
        
        listen = true;
    }
    
    /**
     * Display the error dialog telling the user that Sand Paper failed to open
     * a tutorial file
     */
    private void showTutorialErrorDialog()
    {
        Alert alster = new Alert(AlertType.ERROR);
        
        styleDialog(alster);
        
        alster.setTitle("Error");
        alster.setHeaderText("Unable to Open Tutorial");
        alster.setContentText("Sand Paper is unable to open the tutorial file."
        + "\n\nThe tutorial file can be found in the tutorials directory"
        + " included with Sand Paper. Please try opening the file from there.");
        
        alster.setGraphic(new ImageView("/icons/icon.png"));
        
        alster.showAndWait();
    }
    
    /**
     * Applies the stylesheet and icon to a dialog
     * 
     * @param alster The alert to be stylized
     */
    private void styleDialog(Alert alster)
    {
        DialogPane dister = alster.getDialogPane();
        
        dister.getStylesheets().add("design.css");
        
        // Sets the icon of the dialog box
        ((Stage)dister.getScene().getWindow()).getIcons().add(
                new Image("icons/icon.png"));
    }
    
    /**
     * Performs the necessary actions for when a button in 1 of the FlowPanes on
     * the texture tab is clicked
     * 
     * @param color Whether or not the button clicked is for colored or
     *              colorless textures.
     */
    private void textureButtonClick(boolean color)
    {
        ArrayList<Short> selectedTextures = texTab.getSelectedIndices(color);
        
        // If there are no selected textures in the correct FlowPane...
        if (selectedTextures.isEmpty())
        {
            // ...then the button must be used for importing a texture.
            addTexture(color);
        }
        // ...otherwise...
        else
        {
            // ...the button must be used for removing textures.
            removeTextures(color, selectedTextures);
        }
        
        // Keeps SandPaper from hanging if there is a population present
        refreshPreview();
    }
    
    /**
     * Recreates the populations to accommodate the terrain's new size
     * 
     * @param didWidthChange Whether or not the terrain's width was adjusted
     * @param terrainSize The new size of the terrain
     */
    private void updatePopulationsForTerrainSizeChange(boolean didWidthChange,
            short terrainSize)
    {
        // If the terrain's width changed...
        if (didWidthChange)
        {
            // ...update the populations for a width change.
            popTab.updateForTerrainWidthChange(terrainSize,
                    terTab.getTerrain().getPoints());
        }
        // ...otherwise, the terrain's depth must have changed...
        else
        {
            // ...so update them for a depth change.
            popTab.updateForTerrainDepthChange(terrainSize,
                    terTab.getTerrain().getPoints());
        }
        
        recenterOnTerrain();
        
        refreshPreview();
    }
    
    /**
     * Gets the value of the provided integer spinner if it validates.
     * Otherwise, it returns the provided old value.
     * 
     * @param mustBePositive Whether or not the spinner's value must be a
     *                       positive value
     * @param oldValue The spinner's last value that validated successfully
     * @param spinster The spinner
     * 
     * @return The value set by the user in the spinner if validated. If not
     *         validated, the spinner's previous value that is provided as a
     *         parameter.
     */
    private int validateSpinner(boolean mustBePositive, int oldValue,
            Spinner spinster)
    {
        int validValue;
        
        // Get the spinner's text property
        StringProperty content = spinster.getEditor().textProperty();
                
        // If the user's input validates...
        if (validator.isSpinnerValueValid(mustBePositive, content.get()))
        {
            // ...set the user's value as the valid value.
            validValue = Integer.parseInt(content.get());
        }
        // ...otherwise...
        else
        {
            // ...set valid value to be the spinner's previous validated value.
            validValue = oldValue;
            // Return the spinner's value factory to the previous value
            spinster.getValueFactory().setValue(oldValue);
            // Return the spinner's text to the previous value
            content.setValue(String.valueOf(oldValue));
        }
        
        return validValue;
    }
}
