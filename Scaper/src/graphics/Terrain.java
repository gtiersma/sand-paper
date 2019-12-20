package graphics;

import javafx.scene.image.Image;


/**
 * The 3D object designed from the materials imported from the user
 * 
 * @author George Tiersma
 */
public class Terrain extends MeshObject
{
    /**
     * CONSTRUCTOR
     * 
     * @param widthster The width of the terrain in vertices
     * @param depthster The depth of the terrain in vertices
     * @param fSize The length of each side of each face on the mesh when the
     * mesh is not displaced
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param dister The displacement map
     * @param bumpster The bump map
     * @param specster The specular map
     */
    public Terrain(int widthster, int depthster, int fSize, int strengthster,
            Image dister, Image bumpster, Image specster)
    {
        super(widthster, depthster, fSize, strengthster, dister, bumpster,
                specster);
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
    public double getCenter(char dimension)
    {
        // The number of points of the terrain of which their position will be
        // gathered.
        final int CENTER_POINTS_AMOUNT = 5;
        
        // The average of the positions of the points
        double average;
        // The sum of all of the positions of the points
        double total = 0;
        
        // The indexes of the points
        int[] indexes = new int[CENTER_POINTS_AMOUNT];
        
        // The index of the point at the center of the terrain
        indexes[0] = (width * depth) / 2;
        
        // If the width is even...
        if (width % 2 == 0)
        {
            // ...half of the width must be subtracted to get the center-most
            // point.
            indexes[0] = indexes[0] - (width / 2);
        }
        
        // The point between the center and the back edge of the terrain
        indexes[1] = indexes[0] - width * (depth / 4);
        // The point between the center and the left edge of the terrain
        indexes[2] = indexes[0] - width / 4;
        // The point between the center and the right edge of the terrain
        indexes[3] = indexes[0] + width / 4;
        // The point between the center and the front edge of the terrain
        indexes[4] = indexes[0] + width * (depth / 4);
        
        // For each point...
        for (int i = 0; i < CENTER_POINTS_AMOUNT; i++)
        {
            // ...calculate the index for the correct dimension.
            indexes[i] = indexes[i] * DIMENSIONS + getDimensionValue(dimension);
            
            // Add that point's position to the total
            total = total + points[indexes[i]];
        }
        
        // Get the average position of the points
        average = total / CENTER_POINTS_AMOUNT;
        
        return average;
    }
    
    /**
     * Gets 0 if an 'x' is given, 1 for a 'y' and 2 for a 'z'
     * 
     * @param dimension The dimension of which to return a value (x, y or z)
     * 
     * @return 0 if an 'x' is given, 1 for a 'y' and 2 for a 'z'
     */
    private int getDimensionValue(char dimension)
    {
        int value = 0;
        
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
     * Gets the approximate furthest distance of a point of any dimension from
     * the center of the terrain.
     * 
     * @return The approximate furthest distance
     */
    public double getFurthestPoint()
    {
        // The furthest distance for each dimension
        double furthestX = getFurthestPoint('x');
        double furthestY = getFurthestPoint('y');
        double furthestZ = getFurthestPoint('z');
        
        // The furthest distance of any of the dimension
        double furthest;
        
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
    private double getFurthestPoint(char dimension)
    {
        double center = getCenter(dimension);
        
        // The furthest point
        double far = 0;
        
        // The 4 corner points of the terrain
        int[] cornerPoints = {
                0,
                (width - 1),
                (width * (depth - 1)),
                ((width * depth) - 1)
                };
        
        // For each point in a corner...
        for (int i = 0; i < cornerPoints.length; i++)
        {
            // The index of the position of this corner point of the given
            // dimension
            int farIndex = cornerPoints[i] * DIMENSIONS
                    + getDimensionValue(dimension);
            
            // The distance this point is from the center
            double possibleFar = Math.abs(points[farIndex] - center);
            
            // If it is the furthest from the terrain center so far...
            if (far < possibleFar)
            {
                // ...it's currently the furthest point.
                far = possibleFar;
            }
        }
        
        return far;
    }
}
