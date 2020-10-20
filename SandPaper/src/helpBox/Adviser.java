package helpBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Provides information on a requested control
 * 
 * @author George Tiersma
 */

public class Adviser
{
    // The index positions for each tab
    final private int TEXTURES_TAB_INDEX = 0;
    final private int TERRAIN_TAB_INDEX = 1;
    final private int POPULATIONS_TAB_INDEX = 2;
    final private int RENDER_TAB_INDEX = 0;
    final private int CAMERA_TAB_INDEX = 1;
    final private int LIGHTS_TAB_INDEX = 2;
    
    // The help box message displayed for when no information can be found for a
    // certain control
    final private String DEFAULT_MESSAGE = "Welcome to Sand Paper.\n\nThis " +
            "is the help box. It displays useful information about what the " +
            "cursor is currently hovering over.\n\nNot sure where to start? " +
            "Why not take a look at the tutorials? They can be found in the " +
            "help menu.";
    final private String SCROLL_INSTRUCTIONS = "Use ctrl+up and ctrl+down to " +
            "scroll through this help box.";
    final private String XML_PATH = "src/helpBox/help.xml";
    
    // The description for each control (organized in groups for faster access)
    private Map<String, String> textures;
    private Map<String, String> terrain;
    private Map<String, String> populations;
    private Map<String, String> render;
    private Map<String, String> camera;
    private Map<String, String> lights;
    
    // The tooltip string for each control
    private Map<String, String> tips;
    
    // Pointers to the currently open tabs
    private Map<String, String> bottomOpen;
    private Map<String, String> rightOpen;
    
    private XMLInputFactory facster;
    private XMLEventReader readster;
    
    /**
     * CONTROLLER
     */
    public Adviser() 
    {
        textures = new HashMap();
        terrain = new HashMap();
        populations = new HashMap();
        render = new HashMap();
        camera = new HashMap();
        lights = new HashMap();
        
        tips = new HashMap();
        
        // Initialize to the initially open tabs
        bottomOpen = render;
        rightOpen = textures;
        
        facster = XMLInputFactory.newInstance();
    }
    
    /**
     * Get an attribute's value with the given name that belongs to the given
     * element
     * 
     * @param element The element that has the attribute to be retrieved
     * @param name The name of the attribute that a value is to be retrieved of
     * 
     * @return The value of the given attribute. A blank string is returned if 
     * the attribute is not found in the element.
     */
    private String getAttribute(StartElement element, String name)
    {
        // Whether or not the attribute value has been retrieved
        boolean valueFound = false;
        
        String value = "";
        
        // Get the attributes of the element
        Iterator<Attribute> attributeIterator = element.getAttributes();
        
        // While there are still attributes to analyze and the correct attribute
        // value has yet to be found...
        while (attributeIterator.hasNext() && !valueFound)
        {
            // ...get the next attribute.
            Attribute currentAttribute = attributeIterator.next();
            
            String currentName = currentAttribute.getName().getLocalPart();
            
            // If this is the attribute that needs to be retrieved...
            if (currentName.equals(name))
            {
                // ...get its value.
                value = currentAttribute.getValue();
                
                // Exit the loop
                valueFound = true;
            }
        }
        
        return value;
    }
    
    /**
     * Gets the initial message for the help box. This is the message used when 
     * the cursor is not hovering over a control.
     * 
     * @return The original help box message
     */
    public String getDefaultText()
    {
        return DEFAULT_MESSAGE;
    }
    
    /**
     * Gets a reference to the hash map of the given name. If a match of the
     * given name is not found, the map returned is the textures controls map.
     * 
     * @param name The name of the hash map to be retrieved
     * 
     * @return The hash map
     */
    private Map getHashMap(String name)
    {
        Map mapster = textures;
        
        switch(name)
        {
            case "terrain":
                mapster = terrain;
                break;
                
            case "populations":
                mapster = populations;
                break;
                
            case "render":
                mapster = render;
                break;
                
            case "camera":
                mapster = camera;
                break;
                
            case "lights":
                mapster = lights; 
        }
        
        return mapster;
    }
    
    /**
     * Gets information for a control that belongs to the given key
     * 
     * @param boxWidth The width of the help box
     * @param boxHeight The height of the help box
     * @param key The string used to look up the control's information
     * @param fontster The font used in the help box
     * 
     * @return The information for the control. The default message is returned
     *         if a match cannot be found.
     */
    public String getText(double boxWidth, double boxHeight, String key,
            Font fontster)
    {
        // Initialize to the main help message (in case no match is found)
        String textster = DEFAULT_MESSAGE;
        
        // If the hashmap for the tab currently open on the right has a match...
        if (rightOpen.containsKey(key))
        {
            // ...get it.
            textster = rightOpen.get(key);
        }
        // ...otherwise, if the hashmap for the other tab has a match...
        else if (bottomOpen.containsKey(key))
        {
            // ...get it.
            textster = bottomOpen.get(key);
        }
        
        // If the text does not fit in the help box...
        if (isBoxTooSmall(boxWidth, boxHeight, textster, fontster))
        {
            // ...concat the instructions to scroll the help box.
            textster = SCROLL_INSTRUCTIONS + "\n\n" + textster;
        }
        
        return textster;
    }
    
    /**
     * Gets the tooltip title string for a control that belongs to the given key
     * 
     * @param key The string used to look up the control's tooltip string
     * 
     * @return The tooltip string for the control. An empty string is returned
     *         if a match cannot be found.
     */
    public String getTitle(String key)
    {
        String title = "";
        
        if (tips.containsKey(key))
        {
            title = tips.get(key);
        }
        
        return title;
    }
    
    /**
     * Get whether or not the information fits in the help box without the need
     * of a scroll bar.
     * 
     * @param boxWidth The width of the help box
     * @param boxHeight The height of the help box
     * @param helpInfo The information to be put in the help box
     * @param fontster The font used in the help box
     * 
     * @return Whether or not the help info fits
     */
    private boolean isBoxTooSmall(double boxWidth, double boxHeight,
            String helpInfo, Font fontster)
    {
        // Whether or not it fits
        boolean boxTooSmall = true;
        
        // How many pixels high the font is
        double characterHeight;
        // The number of rows of text
        double rowAmount;
        // The height of all the rows of text
        double textPixelHeight;
        // The length of all the text (without word wrap)
        double textPixelLength;
        
        Text textster = new Text(helpInfo);
        textster.setFont(fontster);
        
        characterHeight = textster.getLayoutBounds().getHeight();
        textPixelLength = textster.getLayoutBounds().getWidth();
        
        rowAmount = textPixelLength / boxWidth;
        
        textPixelHeight = rowAmount * characterHeight;
        
        // If the height of the text is less than the height of the help box...
        if (textPixelHeight < boxHeight)
        {
            // ...the text fits without needing scroll bar.
            boxTooSmall = false;
        }
        
        return boxTooSmall;
    }
    
    /**
     * Loads the data from the XML file
     */
    public void load()
    {
        try
        {
            // The key that will be stored for the control currently being
            // parsed from the XML
            String controlID = "";
                    
            // The HashMap that is currently having data input into it.
            // It is initialized to the textures control map to keep it from
            // being null (for safety).
            Map<String, String> currentMap = textures;
                
            // Open the XML
            readster = facster.createXMLEventReader(
                    new FileInputStream(XML_PATH));
            
            // For each event in the XML...
            while (readster.hasNext())
            {
                // ...get it.
                XMLEvent evster = readster.nextEvent();
                
                // As long as it is a start of an element...
                if (evster.isStartElement())
                {
                    // ...get the StartElement object from it.
                    StartElement startster = evster.asStartElement();
                    
                    // Get a string representation of the tag
                    String tagName = startster.getName().getLocalPart();
                    
                    switch(tagName)
                    {
                        // If it belongs to a group element...
                        case "group":
                            // ...get its name.
                            String attribute = getAttribute(startster, "name");
                            // Set the current HashMap to the one that belongs
                            // to this group.
                            currentMap = getHashMap(attribute);
                            break;
                    
                        // If it belongs to a control element...
                        case "control":
                            // ...get the control's name and use it for the key.
                            controlID = getAttribute(startster, "name");
                            break;
                            
                        // If it belongs to a title element...
                        case "title":
                            // ...the next event will contain the title. Get it.
                            evster = readster.nextEvent();
                            // ...get the title from it to use as a tooltip.
                            String title = evster.asCharacters().getData();
                            tips.put(controlID, title);
                            break;
                            
                        // If it belongs to a text element...
                        case "text":
                            // ...the next event will contain the text. Get it.
                            evster = readster.nextEvent();
                            // Get the text and concat it to the textster for the 
                            // help box text
                            String controlText
                                    = evster.asCharacters().getData();
                            currentMap.put(controlID, controlText);
                    }
                }
            }
            
            readster.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("ERROR: Help XML file not found");
        }
        catch (XMLStreamException ex)
        {
            System.out.println("ERROR: Help XML file is corrupt");
        }
    }
    
    /**
     * Updates the Adviser's variable that keeps track of which tab is currently
     * open on the bottom tab pane
     * 
     * @param index The index of the tab that's currently open in the bottom tab
     * pane
     */
    public void setBottomTab(int index)
    {
        switch(index)
        {
            case RENDER_TAB_INDEX:
                bottomOpen = render;
                break;
                
            case CAMERA_TAB_INDEX:
                bottomOpen = camera;
                break;
                
            case LIGHTS_TAB_INDEX:
                bottomOpen = lights;
        }
    }
    
    /**
     * Updates the Adviser's variable that keeps track of which tab is currently
     * open on the right tab pane
     * 
     * @param index The index of the tab that's currently open in the right tab
     * pane
     */
    public void setRightTab(int index)
    {
        switch(index)
        {
            case TEXTURES_TAB_INDEX:
                rightOpen = textures;
                break;
                
            case TERRAIN_TAB_INDEX:
                rightOpen = terrain;
                break;
                
            case POPULATIONS_TAB_INDEX:
                rightOpen = populations;
        }
    }
}
