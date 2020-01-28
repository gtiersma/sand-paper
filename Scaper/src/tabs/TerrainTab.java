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
     * Gets the approximate position of the center of the terrain on the x scale
     * 
     * @return The center position of the terrain on the x scale
     */
    public double getCenterX()
    {
        System.out.println("terTab -> Getting terrain center on x scale " + terster.getCenter('x') + "...");
        return terster.getCenter('x');
    }
    
    /**
     * Gets the approximate position of the center of the terrain on the y scale
     * 
     * @return The center position of the terrain on the y scale
     */
    public double getCenterY()
    {
        System.out.println("terTab -> Getting terrain center on y scale " + terster.getCenter('y') + "...");
        return terster.getCenter('y');
    }
    
    /**
     * Gets the approximate position of the center of the terrain on the z scale
     * 
     * @return The center position of the terrain on the z scale
     */
    public double getCenterZ()
    {
        System.out.println("terTab -> Getting terrain center on z scale " + terster.getCenter('z') + "...");
        return terster.getCenter('z');
    }
    
    /**
     * Gets the default size of the terrain in vertices
     * 
     * @return The default size
     */
    public int getDefaultSize()
    {
        System.out.println("terTab -> Getting the default terrain size...");
        return DEFAULT_SIZE;
    }
    
    /**
     * Gets the default strength of the displacement map
     * 
     * @return The default strength
     */
    public double getDefaultStrength()
    {
        System.out.println("terTab -> Getting the default terrain displacement strength...");
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
        System.out.println("terTab -> Getting the terrain's greatest distance " + terster.getFurthestPoint() + "...");
        return terster.getFurthestPoint();
    }
    
    /**
     * Gets the coordinates of the points used in the creation of the terrain's
     * mesh
     * 
     * @return The point coordinates
     */
    public float[] getPoints()
    {
        System.out.println("terTab -> Getting the terrain's points...");
        return terster.getPoints();
    }
    
    /**
     * Gets the terrain in the form of a group
     * 
     * @return The terrain
     */
    public MeshView getTerrain()
    {
        System.out.println("terTab -> Getting the terrain's MeshView...");
        return viewster;
    }
    
    /**
     * Gets the depth of the terrain (measured in vertices)
     * 
     * @return The height of the terrain
     */
    public int getTerrainDepth()
    {
        System.out.println("terTab -> Getting the terrain's depth " + terster.getDepth() + "...");
        return terster.getDepth();
    }
    
    /**
     * Gets the width of the terrain (measured in vertices)
     * 
     * @return The width of the terrain
     */
    public int getTerrainWidth()
    {
        System.out.println("terTab -> Getting the terrain's width " + terster.getWidth() + "...");
        return terster.getWidth();
    }
    
    /**
     * Prepares the initial terrain to be displayed
     */
    public void prepareTerrain()
    {
        System.out.println("terTab -> Preparing the terrain...");
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
        System.out.println("terTab -> Setting the terrain's bump map...");
        terster.setBump(bumpster);
    }
    
    /**
     * Sets the depth of the terrain in vertices
     * 
     * @param depth The depth of the terrain
     */
    public void setDepth(int depth)
    {
        System.out.println("terTab -> Setting the terrain's depth " + depth + "...");
        terster.setDepth(depth);
    }
    
    /**
     * Sets the depth of the terrain in vertices
     * 
     * @param depth The depth of the terrain
     */
    public void setDepth(String depth)
    {
        System.out.println("terTab -> Setting the terrain's depth as a string " + depth + "...");
        terster.setDepth(Integer.parseInt(depth));
    }
    
    /**
     * Sets the displacement map
     * 
     * @param displacement The displacement map
     */
    public void setDisplacement(Image displacement)
    {
        System.out.println("terTab -> Setting the terrain's displacement strength...");
        terster.setDisplacement(displacement);
    }
    
    /**
     * Sets the strength of the displacement map
     * 
     * @param displacementStrength The displacement map's strength
     */
    public void setDisplacementStrength(float displacementStrength)
    {
        System.out.println("terTab -> Setting the terrain's displacement strength " + displacementStrength + "...");
        terster.setDisplacementStrength(displacementStrength);
    }
    
    /**
     * Sets the specular map
     * 
     * @param specster The specular map
     */
    public void setSpecular(Image specster)
    {
        System.out.println("terTab -> Setting the terrain's specular map...");
        terster.setSpecular(specster);
    }
    
    /**
     * Sets the texture map
     * 
     * @param texture The texture map
     */
    public void setTexture(Image texture)
    {
        System.out.println("terTab -> Setting the terrain's texture...");
        terster.setTexture(texture);
    }
    
    /**
     * Sets the width of the terrain in vertices
     * 
     * @param width The width of the terrain
     */
    public void setWidth(int width)
    {
        System.out.println("terTab -> Setting the terrain's width " + width + "...");
        terster.setWidth(width);
    }
    
    /**
     * Sets the width of the terrain in vertices
     * 
     * @param width The width of the terrain
     */
    public void setWidth(String width)
    {
        System.out.println("terTab -> Setting the terrain's width as a string " + width + "...");
        terster.setWidth(Integer.parseInt(width));
    }
}
