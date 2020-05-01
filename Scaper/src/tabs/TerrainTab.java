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
    final short FACE_SIZE = 10;
    // Initial size of terrain (measured in vertices)
    final short DEFAULT_VERTEX_SIZE = 50;
    
    // Initial strength of the displacement map
    final int DEFAULT_STRENGTH = 50;
    
    final Image BLANK_IMAGE = new Image("graphics/unassignedWhite.png");
    
    Terrain terster;
    
    MeshView viewster;
    
    /**
     * CONSTRUCTOR
     */
    public TerrainTab()
    {
        terster = new Terrain(FACE_SIZE, DEFAULT_VERTEX_SIZE,
                DEFAULT_VERTEX_SIZE, DEFAULT_STRENGTH, BLANK_IMAGE);
    }
    
    /**
     * Gets the default size of the terrain (measured in vertices)
     * 
     * @return The default size
     */
    public short getDefaultSize()
    {
        return DEFAULT_VERTEX_SIZE;
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
