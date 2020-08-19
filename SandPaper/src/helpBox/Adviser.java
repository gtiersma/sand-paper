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
    final String XML_PATH = "src/helpBox/help.xml";
    
    // The description for each control (organized in groups)
    private Map<String, String> menu;
    private Map<String, String> textures;
    private Map<String, String> terrain;
    private Map<String, String> populations;
    private Map<String, String> render;
    private Map<String, String> camera;
    private Map<String, String> lights;
    
    private XMLInputFactory facster;
    private XMLEventReader readster;
    
    /**
     * CONTROLLER
     */
    public Adviser() 
    {
        menu = new HashMap();
        textures = new HashMap();
        terrain = new HashMap();
        populations = new HashMap();
        render = new HashMap();
        camera = new HashMap();
        lights = new HashMap();
        
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
     * Gets a reference to the hash map of the given name
     * 
     * @param name The name of the hash map to be retrieved
     * 
     * @return The hash map
     */
    private Map getHashMap(String name)
    {
        Map mapster = menu;
        
        switch(name)
        {
            case "textures":
                mapster = textures;
                break;
                
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
     * Loads the data from the XML file
     */
    public void load()
    {
        try
        {
            // The key that will be stored for the control currently being
            // parsed from the XML
            String controlID = "";
            // The text that will be displayed in the help box for the control
            // currently being parsed from the XML
            String controlText = "";
                    
            // The HashMap that is currently having data input into it
            Map<String, String> currentMap = menu;
                
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
                            // Get the title from it to use as the help box text
                            controlText = evster.asCharacters().getData();
                            break;
                            
                        // If it belongs to a text element...
                        case "text":
                            // ...the next event will contain the text. Get it.
                            evster = readster.nextEvent();
                            // Get the text and concat it to the title for the 
                            // help box text
                            controlText = controlText + "\n\n" + evster.asCharacters().getData();
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
