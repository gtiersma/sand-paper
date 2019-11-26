package tabs;


import graphics.MeshObject;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

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
    
    Group meshGroup;
    
    // The mesh
    MeshObject meshster;
    
    /**
     * CONSTRUCTOR
     */
    public TerrainTab()
    {
        meshster = new MeshObject(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_STRENGTH,
                BLANK_IMAGE, BLANK_IMAGE, BLANK_IMAGE);
        meshGroup = new Group();
    }
    
    /**
     * Gets the approximate position of the center of the mesh on the x scale
     * 
     * @return The center position of the mesh on the x scale
     */
    public double getCenterX()
    {
        return meshster.getCenter('x');
    }
    
    /**
     * Gets the approximate position of the center of the mesh on the y scale
     * 
     * @return The center position of the mesh on the y scale
     */
    public double getCenterY()
    {
        return meshster.getCenter('y');
    }
    
    /**
     * Gets the approximate position of the center of the mesh on the z scale
     * 
     * @return The center position of the mesh on the z scale
     */
    public double getCenterZ()
    {
        return meshster.getCenter('z');
    }
    
    /**
     * Gets the default size of the mesh in vertices
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
     * Gets the greatest distance that any point is from the center of the mesh
     * on any of the 3 scales (x, y or z)
     * 
     * @return The greatest distance from the mesh's center
     */
    public double getFurthestPoint()
    {
        return meshster.getFurthestPoint();
    }
    
    /**
     * Gets the mesh in the form of a group
     * 
     * @return The mesh
     */
    public Group getMesh()
    {
        return meshGroup;
    }
    
    /**
     * Prepares the initial mesh to be displayed
     */
    public void prepareMesh()
    {
        MeshView viewster = meshster.getMeshView();
        
        meshster.setTexture(BLANK_IMAGE);
        meshster.load();
        
        meshGroup.getChildren().add(viewster);
    }
    
    /**
     * Sets the bump map
     * 
     * @param bumpster The bump map
     */
    public void setBump(Image bumpster)
    {
        meshster.setBump(bumpster);
    }
    
    /**
     * Sets the depth of the mesh in vertices
     * 
     * @param depth The depth of the mesh
     */
    public void setDepth(int depth)
    {
        meshster.setDepth(depth);
    }
    
    /**
     *
     * @param depth
     */
    public void setDepth(String depth)
    {
        meshster.setDepth(Integer.parseInt(depth));
    }
    
    /**
     * Sets the displacement map
     * 
     * @param displacement The displacement map
     */
    public void setDisplacement(Image displacement)
    {
        meshster.setDisplacement(displacement);
    }
    
    /**
     * Sets the strength of the displacement map
     * 
     * @param displacementStrength The displacement map's strength
     */
    public void setDisplacementStrength(float displacementStrength)
    {
        meshster.setDisplacementStrength(displacementStrength);
    }
    
    /**
     * Applies a Rotation object to the mesh
     * 
     * @param dimension The dimension to which this rotation is being applied (x
     * or y)
     * @param rotster The Rotation object
     */
    public void setRotation(char dimension, Rotate rotster)
    {
        meshster.setRotation(dimension, rotster);
    }
    
    /**
     * Sets the specular map
     * 
     * @param specster The specular map
     */
    public void setSpecular(Image specster)
    {
        meshster.setSpecular(specster);
    }
    
    /**
     * Sets the texture map
     * 
     * @param texture The texture map
     */
    public void setTexture(Image texture)
    {
        meshster.setTexture(texture);
    }
    
    /**
     * Sets the width of the mesh in vertices
     * 
     * @param width The width of the mesh
     */
    public void setWidth(int width)
    {
        meshster.setWidth(width);
    }
    
    /**
     * Sets the width of the mesh in vertices
     * 
     * @param width The width of the mesh
     */
    public void setWidth(String width)
    {
        meshster.setWidth(Integer.parseInt(width));
    }
}
