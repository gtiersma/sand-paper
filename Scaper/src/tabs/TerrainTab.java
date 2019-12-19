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
        terster = new Terrain(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_STRENGTH,
                BLANK_IMAGE, BLANK_IMAGE, BLANK_IMAGE);
    }
    
    /**
     * Gets the approximate position of the center of the terrain on the x scale
     * 
     * @return The center position of the terrain on the x scale
     */
    public double getCenterX()
    {
        return terster.getCenter('x');
    }
    
    /**
     * Gets the approximate position of the center of the terrain on the y scale
     * 
     * @return The center position of the terrain on the y scale
     */
    public double getCenterY()
    {
        return terster.getCenter('y');
    }
    
    /**
     * Gets the approximate position of the center of the terrain on the z scale
     * 
     * @return The center position of the terrain on the z scale
     */
    public double getCenterZ()
    {
        return terster.getCenter('z');
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
     * Gets the greatest distance that any point is from the center of the
     * terrain on any of the 3 scales (x, y or z)
     * 
     * @return The greatest distance from the terrain's center
     */
    public double getFurthestPoint()
    {
        return terster.getFurthestPoint();
    }
    
    /**
     * Gets the terrain in the form of a group
     * 
     * @return The terrain
     */
    public MeshView getTerrain()
    {
        return viewster;
    }
    
    /**
     * Prepares the initial terrain to be displayed
     */
    public void prepareTerrain()
    {
        terster.setTexture(BLANK_IMAGE);
        terster.load();
        
        viewster = terster.getMeshView();
    }
    
    /**
     * Sets the bump map
     * 
     * @param bumpster The bump map
     */
    public void setBump(Image bumpster)
    {
        terster.setBump(bumpster);
    }
    
    /**
     * Sets the depth of the terrain in vertices
     * 
     * @param depth The depth of the terrain
     */
    public void setDepth(int depth)
    {
        terster.setDepth(depth);
    }
    
    /**
     *
     * @param depth
     */
    public void setDepth(String depth)
    {
        terster.setDepth(Integer.parseInt(depth));
    }
    
    /**
     * Sets the displacement map
     * 
     * @param displacement The displacement map
     */
    public void setDisplacement(Image displacement)
    {
        terster.setDisplacement(displacement);
    }
    
    /**
     * Sets the strength of the displacement map
     * 
     * @param displacementStrength The displacement map's strength
     */
    public void setDisplacementStrength(float displacementStrength)
    {
        terster.setDisplacementStrength(displacementStrength);
    }
    
    /**
     * Sets the specular map
     * 
     * @param specster The specular map
     */
    public void setSpecular(Image specster)
    {
        terster.setSpecular(specster);
    }
    
    /**
     * Sets the texture map
     * 
     * @param texture The texture map
     */
    public void setTexture(Image texture)
    {
        terster.setTexture(texture);
    }
    
    /**
     * Sets the width of the terrain in vertices
     * 
     * @param width The width of the terrain
     */
    public void setWidth(int width)
    {
        terster.setWidth(width);
    }
    
    /**
     * Sets the width of the terrain in vertices
     * 
     * @param width The width of the terrain
     */
    public void setWidth(String width)
    {
        terster.setWidth(Integer.parseInt(width));
    }
}
