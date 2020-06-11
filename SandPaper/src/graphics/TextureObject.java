package graphics;


import java.io.File;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * A texture to be used as a map for the mesh
 * 
 * @author George Tiersma
 */
public class TextureObject
{
    // The size of the image view
    final private int VIEW_SIZE = 80;
   
    // Names of images used for unassigned textures
    final private String[] EMPTY_NAMES = {"unassignedGray.png",
        "unassignedWhite.png"};
    
    // The path to the texture
    private String path;
    // The name of the texture
    private String name;
    
    // The file of the texture
    private File filster;
    // The image of the texture
    private Image imster;
    private PixelReader pixster;
    // The image view of the texture
    private ImageView viewster;
    
    /**
     * CONSTRUCTOR
     * 
     * @param fileFile The file to the texture
     */
    public TextureObject(File fileFile)
    {
        filster = fileFile;
        path = fileFile.toString();
        name = path.substring(path.lastIndexOf("\\") + 1, path.indexOf("."));
        imster = new Image("file:" + path);
        pixster = imster.getPixelReader();
        
        viewster = new ImageView();
        viewster.setImage(imster);
        viewster.setFitWidth(VIEW_SIZE);
        viewster.setFitHeight(VIEW_SIZE);
    }
    
    /**
     * CONSTRUCTOR
     */
    public TextureObject()
    {
        // When no parameter is given, the blank texture is assigned
        filster = new File("src/graphics/unassignedWhite.png");
        path = filster.toString();
        name = path.substring(path.lastIndexOf("\\") + 1, path.indexOf("."));
        imster = new Image("file:" + path);
        pixster = imster.getPixelReader();
        
        viewster = new ImageView();
        viewster.setImage(imster);
        viewster.setFitWidth(VIEW_SIZE);
        viewster.setFitHeight(VIEW_SIZE);
    }
    
    /**
     * Gets the color of the pixel at the given coordinates
     * 
     * @param x The x coordinate of the pixel
     * @param y The y coordinate of the pixel
     * 
     * @return The color of the pixel
     */
    public Color getColor(int x, int y)
    {
        return pixster.getColor(x, y);
    }
    
    /**
     * Gets the file of the texture
     * 
     * @return The texture's file
     */
    public File getFile()
    {
        return filster;
    }
    
    /**
     * Gets the height of the image
     * 
     * @return The Image's height
     */
    public double getHeight()
    {
        return imster.getHeight();
    }
    
    /**
     * Gets the image object of the texture
     * 
     * @return The texture's image object
     */
    public Image getImage()
    {
        return imster;
    }
    
    /**
     * Gets the name of the texture
     * 
     * @return The texture's name. If the texture has no name, a blank string is
     *         returned.
     */
    public String getName()
    {
        // Whether or not this texture's name should be hidden
        boolean nameShouldBeHidden = false;
        
        String namster = "";
        
        // For each possible name of an image used for unassigned textures...
        for (byte i = 0; i < EMPTY_NAMES.length; i++)
        {
            // ...if this texture has one of those names...
            if (name.equals(EMPTY_NAMES[i]))
            {
                // ...its name should be hidden from the user.
                nameShouldBeHidden = true;
            }
        }
        
        if (!nameShouldBeHidden)
        {
            namster = name;
        }
        
        return namster;
    }
    
    /**
     * Gets the path to the texture
     * 
     * @return The texture's path
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * Gets the image view of the texture
     * 
     * @return The texture's image view
     */
    public ImageView getView()
    {
        return viewster;
    }
    
    /**
     * Gets the width of the image
     * 
     * @return The Image's width
     */
    public double getWidth()
    {
        return imster.getWidth();
    }
    
    /**
     * Increments a given String. The string must be entirely a number.
     * 
     * @return The incremented String
     */
    private String incrementString(String stringster)
    {
        int number = Integer.parseInt(stringster);
                
        number++;
        
        String incrementedString = Integer.toString(number);
        
        return incrementedString;
    }
    
    /**
     * Gets whether or not there is an error with loading the image
     * 
     * @return Whether or not there is an error with loading the image
     */
    public boolean isValid()
    {
        return !imster.errorProperty().get();
    }
    
    /**
     * Removes the color from the texture's ImageView. It only changes the
     * ImageView.
     */
    public void removeColor()
    {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation(-1);
        viewster.setEffect(grayscale);
    }
    
    /**
     * Changes the name to prevent duplicate names with another TextureObject.
     * 
     * It changes the name by incrementing a number at the end of the name. If
     * there is not a number at the end of the name, it appends "_2" to the
     * name.
     */
    public void rename()
    {
        // The digits at the end of the name
        String endDigits = "";
        
        // For each character in the name (going from the end to the
        // beginning)...
        for (int i = name.length() - 1; i > -1; i--)
        {
            // ...if the character is a digit...
            if (Character.isDigit(name.charAt(i)))
            {
                // ...concat it to the beginning of the ending digits.
                endDigits = name.charAt(i) + endDigits;
                
                // If the beginning of the string has been reached...
                if (i == 0)
                {
                    // ...increment the digits gathered and make it the new
                    // name.
                    name = incrementString(endDigits);
                }
            }
            // ...otherwise, if the character is not a digit and is the last
            // character in the name...
            else if (endDigits.equals(""))
            {
                // ...append the number 2 to the end.
                name = name + "_2";
                
                // Exit the loop
                i = -1;
            }
            // ...otherwise...
            else
            {
                // ...get the number of characters in the name excluding the
                // number at the end.
                int lengthWithoutNumber = name.length() - endDigits.length();
                
                // Increment the number from the end of the string
                String newNumber = incrementString(endDigits);
                
                // Replace the number at the end of the string with the new
                // incremented number
                name = name.substring(0, lengthWithoutNumber) + newNumber;
                
                // Exit the loop
                i = -1;
            }
        }
    }
}
