package tabs;


import graphics.Terrain;
import javafx.scene.image.Image;
import javafx.scene.shape.MeshView;

/**
 * Controls the operations for the terrain tab
 * 
 * @author George Tiersma
 */
public class TerrainTab
{
    // The length of each side of each face on the mesh when the mesh is not
    // displaced
    final int FACE_SIZE = 10;
    
    final int DEFAULT_SIZE = 50;
    final int DEFAULT_STRENGTH = 50;
    
    final Image BLANK_IMAGE = new Image("graphics/blank.png");
    
    Terrain terster;
    
    MeshView viewster;
    
    /**
     * CONSTRUCTOR
     */
    public TerrainTab()
    {
        terster = new Terrain(DEFAULT_SIZE, DEFAULT_SIZE, FACE_SIZE,
                DEFAULT_STRENGTH, BLANK_IMAGE);
    }
    
    /**
     * Gets the default size of the terrain in vertices
     * 
     * @return The default size
     */
    public int getDefaultSize()
    {
        return DEFAULT_SIZE;
    }
    
    /**
     * Gets the default strength of the displacement map
     * 
     * @return The default strength
     */
    public double getDefaultStrength()
    {
        return DEFAULT_STRENGTH;
    }
    
    /**
     * Gets the terrain object
     * 
     * @return The terrain
     */
    public Terrain getTerrain()
    {
        return terster;
    }
    
    /**
     * Prepares the initial terrain to be displayed
     */
    public void prepareTerrain()
    {
        terster.setTexture(BLANK_IMAGE);
        terster.load();
    }
}
