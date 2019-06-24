
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.MeshView;

/*
 * Programmer: Tiersma, George
 * Chemeketa Community College
 * May 4, 2019
 * Class: CIS234J
 * Assignment: Final Project
 * File Name: TerrainTab.java
 */

/**
 * Controls the operations involving the controls present on the terrain tab
 * 
 * @author <a href= "mailto:gtiersma@my.chemeketa.edu" >George Tiersma</a>
 */
public class TerrainTab
{
    final int DEFAULT_SIZE = 50;
    final int DEFAULT_STRENGTH = 50;
    
    final Image BLANK_IMAGE = new Image("blank.png");
    
    // The mesh
    MeshObject meshster;
    
    /**
     * CONSTRUCTOR
     */
    public TerrainTab()
    {
        meshster = new MeshObject(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_STRENGTH,
                BLANK_IMAGE, BLANK_IMAGE, BLANK_IMAGE);
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
     * Gets the mesh in the form of a group
     * 
     * @return The mesh
     */
    public Group getMesh()
    {
        MeshView viewster = meshster.getMeshView();
        
        Group groupster = new Group(viewster);
        
        return groupster;
    }
    
    /**
     * Prepares the mesh to be displayed initially 
     */
    public void prepareMesh()
    {
        meshster.setTexture(BLANK_IMAGE);
        meshster.load();
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
    public void setDisplacementStrength(double displacementStrength)
    {
        meshster.setDisplacementStrength(displacementStrength);
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
}
