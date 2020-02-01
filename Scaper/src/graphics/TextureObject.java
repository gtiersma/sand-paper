package graphics;


import java.io.File;
import java.util.Objects;
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
        name = path.substring(path.lastIndexOf("\\") + 1);
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
        filster = new File("src/graphics/blank.png");
        path = filster.toString();
        name = path.substring(path.lastIndexOf("\\") + 1);
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
        String namster = "";
        
        if (!Objects.equals(name, "blank.png"))
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
     * Removes the color from the texture's ImageView. It only changes the
     * ImageView.
     */
    public void removeColor()
    {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation(-1);
        viewster.setEffect(grayscale);
    }
}
