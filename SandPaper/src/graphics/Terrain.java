package graphics;


/**
 * The 3D object designed from the materials imported from the user
 * 
 * @author George Tiersma
 */
public class Terrain extends MeshObject
{
    private TextureObject displacementTexture;
    private TextureObject diffuseTexture;
    private TextureObject bumpTexture;
    private TextureObject specularTexture;
    
    /**
     * CONSTRUCTOR
     * 
     * @param fSize The length of each side of each face on the mesh when the
     * mesh is not displaced
     * @param widthster The width of the terrain in vertices
     * @param depthster The depth of the terrain in vertices
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param dister The displacement map
     */
    public Terrain(short fSize, short widthster, short depthster,
            int strengthster, TextureObject dister)
    {
        super(fSize, fSize, widthster, depthster, strengthster,
                dister.getImage());
        
        displacementTexture = dister;
        diffuseTexture = new TextureObject();
        bumpTexture = new TextureObject();
        specularTexture = new TextureObject();
    }
    
    /**
     * Gets the bump map
     * 
     * @return The bump map
     */
    public TextureObject getBump()
    {
        return bumpTexture;
    }
    
    /**
     * Gets the approximate center position of the terrain.
     * 
     * Instead of calculating the exact center of the terrain by averaging the
     * position of every point on the terrain, this function only samples
     * several points at particular positions in the terrain, increasing
     * performance.
     * 
     * @param dimension The dimension of the position to be returned (x, y or z)
     * 
     * @return The approximate center position of the terrain
     */
    public float getCenter(char dimension)
    {
        // The number of points of the terrain of which their position will be
        // gathered.
        final byte CENTER_POINTS_AMOUNT = 5;
        // The number of corners that the terrain has
        final byte CORNERS_AMOUNT = 4;
        
        byte dimensionValue = getDimensionValue(dimension);
        
        // The average of the positions of the points
        float average;
        // The sum of all of the positions of the points
        float total = 0;
        
        // The indexes of the points
        int[] indexes = new int[CENTER_POINTS_AMOUNT];
        
        // An estimation of the center-most point's index
        indexes[0] = width * (depth / 2) + (width / 2);
        // The point between the center and the back edge of the terrain
        indexes[1] = indexes[0] - width * (depth / CORNERS_AMOUNT);
        // The point between the center and the left edge of the terrain
        indexes[2] = indexes[0] - width / CORNERS_AMOUNT;
        // The point between the center and the right edge of the terrain
        indexes[3] = indexes[0] + width / CORNERS_AMOUNT;
        // The point between the center and the front edge of the terrain
        indexes[4] = indexes[0] + width * (depth / CORNERS_AMOUNT);
        
        // For each point...
        for (byte i = 0; i < CENTER_POINTS_AMOUNT; i++)
        {
            // ...calculate the index for the correct dimension.
            indexes[i] = indexes[i] * DIMENSIONS + dimensionValue;
            
            // Add that point's position to the total
            total = total + points[indexes[i]];
        }
        
        // Get the average position of the points
        average = total / CENTER_POINTS_AMOUNT;
        
        return average;
    }
    
    /**
     * Gets the depth of the terrain (Measured in vertices)
     * 
     * @return The depth of the terrain (Measured in vertices)
     */
    public short getDepth()
    {
        return depth;
    }
    
    /**
     * Gets the diffuse map
     * 
     * @return The diffuse map
     */
    public TextureObject getDiffuse()
    {
        return diffuseTexture;
    }
    
    /**
     * Gets 0 if an 'x' is given, 1 for a 'y' and 2 for a 'z'
     * 
     * @param dimension The dimension of which to return a value (x, y or z)
     * 
     * @return 0 if an 'x' is given, 1 for a 'y' and 2 for a 'z'
     */
    private byte getDimensionValue(char dimension)
    {
        byte value = 0;
        
        if (dimension == 'y')
        {
            value = 1;
        }
        else if (dimension == 'z')
        {
            value = 2;
        }
        
        return value;
    }
    
    /**
     * Gets the displacement map
     * 
     * @return The displacement map
     */
    public TextureObject getDisplacement()
    {
        return displacementTexture;
    }
    
    /**
     * Gets the displacement strength
     * 
     * @return The set strength of displacement map
     */
    public int getDisplacementStrength()
    {
        return displacementStrength;
    }
    
    /**
     * Gets the approximate furthest distance of a point of any dimension from
     * the center of the terrain.
     * 
     * @return The approximate furthest distance
     */
    public float getFurthestPoint()
    {
        // The furthest distance for each dimension
        float furthestX = getFurthestPoint('x');
        float furthestY = getFurthestPoint('y');
        float furthestZ = getFurthestPoint('z');
        
        // The furthest distance of any of the dimension
        float furthest;
        
        if (furthestX < furthestY)
        {
            if (furthestY < furthestZ)
            {
                furthest = furthestZ;
            }
            else
            {
                furthest = furthestY;
            }
        }
        else
        {
            furthest = furthestX;
        }
        
        return furthest;
    }
    
    /**
     * Gets the approximate furthest distance of a point of the given dimension
     * from the center of the terrain.
     * 
     * @return The approximate furthest distance
     */
    private float getFurthestPoint(char dimension)
    {
        byte dimensionValue = getDimensionValue(dimension);
        
        float center = getCenter(dimension);
        
        // The furthest point
        float far = 0;
        
        // The 4 corner points of the terrain
        int[] cornerPoints = {
                0,
                (width - 1),
                (width * (depth - 1)),
                ((width * depth) - 1)
                };
        
        // For each point in a corner...
        for (byte i = 0; i < cornerPoints.length; i++)
        {
            // The index of the position of this corner point of the given
            // dimension
            int farIndex = cornerPoints[i] * DIMENSIONS + dimensionValue;
            
            // The distance this point is from the center
            float possibleFar = Math.abs(points[farIndex] - center);
            
            // If it is the furthest from the terrain center so far...
            if (far < possibleFar)
            {
                // ...it's currently the furthest point.
                far = possibleFar;
            }
        }
        
        return far;
    }
    
    /**
     * Gets the array of point coordinates used in the creation of the terrain
     * mesh
     * 
     * @return The array of point coordinates
     */
    public float[] getPoints()
    {
        return points;
    }
    
    /**
     * Gets the specular map
     * 
     * @return The specular map
     */
    public TextureObject getSpecular()
    {
        return specularTexture;
    }
    
    /**
     * Gets the width of the terrain (Measured in vertices)
     * 
     * @return The width of the terrain (Measured in vertices)
     */
    public short getWidth()
    {
        return width;
    }
    
    /**
     * Sets the bump map
     * 
     * @param bumpster The new bump map
     */
    public void setBump(TextureObject bumpster)
    {
        bumpTexture = bumpster;
        
        setBump(bumpster.getImage());
    }
    
    /**
     * Sets the diffuse map
     * 
     * @param difster The new diffuse map
     */
    public void setDiffuse(TextureObject diffster)
    {
        diffuseTexture = diffster;
        
        setDiffuse(diffster.getImage());
    }
    
    /**
     * Sets the displacement map
     * 
     * @param difster The new displacement map
     */
    public void setDisplacement(TextureObject dister)
    {
        displacementTexture = dister;
        
        setDisplacement(dister.getImage());
    }
    
    /**
     * Sets the specular map
     * 
     * @param difster The new specular map
     */
    public void setSpecular(TextureObject specster)
    {
        specularTexture = specster;
        
        setDisplacement(specster.getImage());
    }
}
