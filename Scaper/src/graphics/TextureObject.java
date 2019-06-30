package graphics;


import java.io.File;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Programmer: Tiersma, George
 * Chemeketa Community College
 * April 28, 2019
 * Class: CIS234J
 * Assignment: Final Project
 * File Name: TextureObject.java
 */

/**
 * A texture to be used as a map for the mesh
 * 
 * @author <a href= "mailto:gtiersma@my.chemeketa.edu" >George Tiersma</a>
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
        
        viewster = new ImageView();
        viewster.setImage(imster);
        viewster.setFitWidth(VIEW_SIZE);
        viewster.setFitHeight(VIEW_SIZE);
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
     * @return The texture's name
     */
    public String getName()
    {
        return name;
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
     * Removes the color from the texture's image view. Does not always work for
     * some reason.
     */
    public void removeColor()
    {
        ColorAdjust grayscale = new ColorAdjust();
        
        grayscale.setSaturation(-1);
        
        viewster.setEffect(grayscale);
    }
}
