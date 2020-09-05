package helpBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    // The "index" of the tab initially opened when Sand Paper is first launched
    final private byte INITIAL_OPEN_TAB = 0;
    
    // The help box message displayed for when no information can be found for a
    // certain control
    final private String DEFAULT_MESSAGE = "Welcome to Sand Paper.\n\nThis " +
            "is the help box. It displays useful information about what the " +
            "cursor is currently hovering over.\n\nNot sure where to start? " +
            "Why not take a look at the tutorials?! They can be found in the " +
            "help menu.";
    final private String XML_PATH = "src/helpBox/help.xml";
    
    // 0-based values used to keep track of which tabs are currently open
    // For example, if bottomOpenTab's value is 0, the render tab must be open.
    // If it is 1, the camera tab must be open.
    private byte bottomOpenTab;
    private byte rightOpenTab;
    
    // The description for each control (organized in groups for faster access)
    private Map<String, String> textures;
    private Map<String, String> terrain;
    private Map<String, String> populations;
    private Map<String, String> render;
    private Map<String, String> camera;
    private Map<String, String> lights;
    
    // The tooltip string for each control (organized in groups for faster
    // access)
    private Map<String, String> texturesTips;
    private Map<String, String> terrainTips;
    private Map<String, String> populationsTips;
    private Map<String, String> renderTips;
    private Map<String, String> cameraTips;
    private Map<String, String> lightsTips;
    
    private XMLInputFactory facster;
    private XMLEventReader readster;
    
    /**
     * CONTROLLER
     */
    public Adviser() 
    {
        bottomOpenTab = INITIAL_OPEN_TAB;
        rightOpenTab = INITIAL_OPEN_TAB;
        
        textures = new HashMap();
        terrain = new HashMap();
        populations = new HashMap();
        render = new HashMap();
        camera = new HashMap();
        lights = new HashMap();
        
        texturesTips = new HashMap();
        terrainTips = new HashMap();
        populationsTips = new HashMap();
        renderTips = new HashMap();
        cameraTips = new HashMap();
        lightsTips = new HashMap();
        
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
     * Gets a reference to the hash map of the given name. If a match of the
     * given name is not found, the map returned is the textures controls map.
     * 
     * @param name The name of the hash map to be retrieved
     * @param tips Whether or not the hash map being returned should contain
     *             tool tip strings
     * 
     * @return The hash map
     */
    private Map getHashMap(String name, boolean tips)
    {
        Map mapster = textures;
        
        if (tips)
        {
            switch(name)
            {
                case "textures":
                    mapster = texturesTips;
                    break;
                
                case "terrain":
                    mapster = terrainTips;
                    break;
                
                case "populations":
                    mapster = populationsTips;
                    break;
                
                case "render":
                    mapster = renderTips;
                    break;
                
                case "camera":
                    mapster = cameraTips;
                    break;
                
                case "lights":
                    mapster = lightsTips;
            }
        }
        else
        {
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
        }
        
        return mapster;
    }
    
    /**
     * Gets information for a control that belongs to the given key
     * 
     * @param key The string used to look up the control's information
     * 
     * @return The information for the control. An empty string is returned if a
     *         match cannot be found.
     */
    public String getText(String key)
    {
        String textster = "";
        
        // See if a match can be found in the hashmap for the currently-open tab
        // on the right
        switch(rightOpenTab)
        {
            case 0:
                textster = textures.get(key);
                break;
                
            case 1:
                textster = terrain.get(key);
                break;
                
            case 2:
                textster = populations.get(key);
        }
        
        // If a match was not found...
        if (textster.equals(""))
        {
            // ...see if a match can be found in the hashmap for the
            // currently-open tab on the bottom.
            switch(bottomOpenTab)
            {
                case 0:
                    textster = render.get(key);
                    break;
                
                case 1:
                    textster = camera.get(key);
                    break;
                
                case 2:
                    textster = lights.get(key);
            }
        }
        // ...otherwise...
        else
        {
            // ...have the help box display the default message.
            textster = DEFAULT_MESSAGE;
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
        
        // See if a match can be found in the hashmap for the currently-open tab
        // on the right
        switch(rightOpenTab)
        {
            case 0:
                title = texturesTips.get(key);
                break;
                
            case 1:
                title = terrainTips.get(key);
                break;
                
            case 2:
                title = populationsTips.get(key);
        }
        
        // If a match was not found...
        if (title == null)
        {
            // ...see if a match can be found in the hashmap for the
            // currently-open tab on the bottom.
            switch(bottomOpenTab)
            {
                case 0:
                    title = renderTips.get(key);
                    break;
                
                case 1:
                    title = cameraTips.get(key);
                    break;
                
                case 2:
                    title = lightsTips.get(key);
            }
        }
        
        return title;
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
            Map<String, String> currentTips = texturesTips;
                
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
                            currentMap = getHashMap(attribute, false);
                            currentTips = getHashMap(attribute, true);
                            break;
                    
                        // If it belongs to a control element...
                        case "control":
                            // ...get the control's name and use it for the key.
                            controlID = getAttribute(startster, "name");
                            break;
                            
                        // If it belongs to a textster element...
                        case "title":
                            // ...the next event will contain the textster. Get it.
                            evster = readster.nextEvent();
                            // ...get the textster from it to use as the help box
                            // text.
                            String title = evster.asCharacters().getData();
                            currentTips.put(controlID, title);
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
}
