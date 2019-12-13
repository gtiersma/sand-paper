package graphics;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * The 3D object designed from the materials imported from the user
 * 
 * @author George Tiersma
 */
public class MeshObject
{
    // The number of integers in the face array that are needed to define each
    // face
    private final int INTS_PER_FACE = 6;
    private final int DIMENSIONS = 3;
    
    // Multiplied to the user-defined displacement strength variable to increase
    // distance between vertices
    private final float DISPLACEMENT_MULTIPLIER = 10;
    
    // The width of the mesh in vertices
    private int width;
    // The depth of the mesh in vertices
    private int depth;
    
    // The number of faces in the mesh
    private int facesAmount;
    
    // The multiplier for the displacement map strength that is set by the user
    private float displacementStrength;
    
    // The distance on the x scale in number of pixels in the displacement map
    // that one vertex would retrieve from that of an adjacent vertex.
    private double widthPixels;
    // The distance on the y scale in number of pixels in the displacement map
    // that one vertex would retrieve from that of an adjacent vertex.
    private double heightPixels;
    
    // Face data
    private int[] faces;
    
    // Point data
    private float[] points;
    // UV data
    private float[] texturePositions;
    
    // Displacement map
    private Image displacement;
    
    // Contains the texture, bump map and displacement map
    private PhongMaterial texture;
    
    // The mesh
    private TriangleMesh meshster;
    
    // The view containing the mesh
    private MeshView viewster;
    
    // The x & y matrix of the color data for each pixel in the displacement
    // map. It is used to shift the relative position of each vertex.
    private Color[][] vertexRelatives;
    
    /**
     * CONSTRUCTOR
     * 
     * @param widthster The width of the mesh in vertices
     * @param depthster The depth of the mesh in vertices
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param dister The displacement map
     * @param bumpster The bump map
     * @param specster The specular map
     */
    public MeshObject(int widthster, int depthster, int strengthster,
            Image dister, Image bumpster, Image specster)
    {
        width = widthster;
        depth = depthster;
        displacementStrength = strengthster;
        
        // Calculate number of faces
        facesAmount = ((width - 1) * (depth - 1) * 2);
        
        // Calculate number of integers needed for the face data
        faces = new int[facesAmount * INTS_PER_FACE];
        
        // Calculate number of floats needed for the float data
        points = new float[width * depth * DIMENSIONS];
        
        // Calculate number of floats needed for the UV data
        texturePositions = new float[width * depth * 2];
        
        displacement = dister;
        
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        
        texture = new PhongMaterial();
        
        meshster = new TriangleMesh();
        
        viewster = new MeshView(meshster);
        
        // Make the array big enough to hold the color data for each pixel
        vertexRelatives = new Color[(int)displacement.getWidth()]
                [(int)displacement.getHeight()];
    }
    
    /**
     * Gets the approximate center position of the mesh.
     * 
     * Instead of calculating the exact center of the mesh by averaging the
     * position of every point on the mesh, this function only samples several
     * points at particular positions in the mesh, increasing performance.
     * 
     * @param dimension The dimension of the position to be returned (x, y or z)
     * 
     * @return The approximate center position of the mesh
     */
    public double getCenter(char dimension)
    {
        // The number of points of the mesh of which their position will be
        // gathered.
        final int CENTER_POINTS_AMOUNT = 5;
        
        // The average of the positions of the points
        double average;
        // The sum of all of the positions of the points
        double total = 0;
        
        // The indexes of the points
        int[] indexes = new int[CENTER_POINTS_AMOUNT];
        
        // The index of the point at the center of the mesh
        indexes[0] = (width * depth) / 2;
        
        // If the width is even...
        if (width % 2 == 0)
        {
            // ...half of the width must be subtracted to get the center-most
            // point.
            indexes[0] = indexes[0] - (width / 2);
        }
        
        // The point between the center and the back edge of the mesh
        indexes[1] = indexes[0] - width * (depth / 4);
        // The point between the center and the left edge of the mesh
        indexes[2] = indexes[0] - width / 4;
        // The point between the center and the right edge of the mesh
        indexes[3] = indexes[0] + width / 4;
        // The point between the center and the front edge of the mesh
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
     * the center of the mesh.
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
     * from the center of the mesh.
     * 
     * @return The approximate furthest distance
     */
    private double getFurthestPoint(char dimension)
    {
        double center = getCenter(dimension);
        
        // The furthest point
        double far = 0;
        
        // The 4 corner points of the mesh
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
            
            // If it is the furthest from the mesh center so far...
            if (far < possibleFar)
            {
                // ...it's currently the furthest point.
                far = possibleFar;
            }
        }
        
        return far;
    }
    
    /**
     * Prepares and then gets the mesh view
     * 
     * @return The mesh view
     */
    public MeshView getMeshView()
    {
        return viewster;
    }
    
    /**
     * Loads the data needed to construct the mesh into most all of the
     * variables and objects in this MeshObject
     */
    public void load()
    {
        loadDisplacementPixels();
        loadPoints();
        loadTexturePositions();
        loadFaces();
        
        viewster.setDrawMode(DrawMode.FILL);
        viewster.setMaterial(texture);
    }
    
    /**
     * Loads the pixel colors from the displacement map
     */
    public void loadDisplacementPixels()
    {
        PixelReader readster = displacement.getPixelReader();
        
        // For each column of pixels...
        for (int i = 0; i < displacement.getWidth(); i++)
        {
            // ...and for each row of pixels...
            for (int j = 0; j < displacement.getHeight(); j++)
            {
                // Values in each row need to be put into the array backwards to
                // prevent the displacement map from being flipped horizontally
                // when applied to the mesh, so this "new j" is used instead of
                // the regular "j" variable.
                int newJ = (int)displacement.getHeight() - j - 1;
                
                // ...get the correct color.
                vertexRelatives[i][newJ] = readster.getColor(i, j);
            }
        }
    }
    
    /**
     * Loads the face data into the mesh object
     */
    public void loadFaces()
    {
        // The number of which vertex the face being created is based upon. The
        // vertices are number from left to right, top to bottom.
        int point = 0;
        
        // For every set of values for each 2 faces in the face array...
        for (int i = 0; i + 1 < facesAmount * INTS_PER_FACE;
                i = i + INTS_PER_FACE * 2)
        {
            // ...if the point the face is being built off of is not at the
            // vertical edge on the right side of the mesh...
            if (!((point + 1) % width == 0))
            {
                // ...get the points for the first of the 2 faces.
                faces[i] = point + width + 1;
                faces[i + 1] = point + width + 1;
                faces[i + 2] = point + width;
                faces[i + 3] = point + width;
                faces[i + 4] = point;
                faces[i + 5] = point;
                
                // Get the points for the second of the 2 faces
                faces[i + INTS_PER_FACE] = point + 1;
                faces[i + INTS_PER_FACE + 1] = point + 1;
                faces[i + INTS_PER_FACE + 2] = point + width + 1;
                faces[i + INTS_PER_FACE + 3] = point + width + 1;
                faces[i + INTS_PER_FACE + 4] = point;
                faces[i + INTS_PER_FACE + 5] = point;
            }
            // ...otherwise...
            else
            {
                // ...no faces should be built off of the points on the right
                // edge of the mesh, so no values will be assigned for this
                // iteration.
                // This line of code prevents the incrementor from incrementing
                // this iteration. If this was not here, there would be null
                // values in the face array for the 12 elements being skipped
                // over.
                i = i - INTS_PER_FACE * 2;
            }
            
            // Move to the next point for the next iteration
            point++;
        }
        
        // Remove the faces already present
        meshster.getFaces().clear();
        // Assign the new faces
        meshster.getFaces().addAll(faces);
    }
    
    /**
     * Calculates the vertex positions and loads them into the mesh
     */
    public void loadPoints()
    {
        // Arraylist of objects that will retrieve the values of the threads
        List<Future<Float>> threadResults = new ArrayList<>();
        
        // Pool of threads for calculating the positions of the vertices
        ExecutorService exster = Executors.newCachedThreadPool();
        
        // For each column of vertices in the mesh...
        for (int z = 0; z < depth; z++)
        {
            // ...and for each row of vertices...
            for (int x = 0; x < width; x++)
            {
                // Calculate the row and column of the pixel that should be
                // retrieved for this particular vertex
                int pixelRow = (int)(widthPixels * x);
                int pixelColumn = (int)(heightPixels * z);
                
                // Multiply the user-defined displacement strength with the
                // system-defined strength
                float strengthster = displacementStrength
                        * DISPLACEMENT_MULTIPLIER;
                
                // Thread for finding the x position of the vertex
                Callable<Float> xThread = new VertexThread(x, strengthster, 'x',
                        vertexRelatives[pixelRow][pixelColumn]);
                
                // Thread for finding the y position of the vertex
                Callable<Float> yThread = new VertexThread(0, strengthster, 'y',
                        vertexRelatives[pixelRow][pixelColumn]);
                
                // Thread for finding the z position of the vertex
                Callable<Float> zThread = new VertexThread(z, strengthster, 'z',
                        vertexRelatives[pixelRow][pixelColumn]);
                
                // Get the calculations as they become available
                Future<Float> xFuture = exster.submit(xThread);
                Future<Float> yFuture = exster.submit(yThread);
                Future<Float> zFuture = exster.submit(zThread);
                
                // Add them to the arraylists
                threadResults.add(xFuture);
                threadResults.add(yFuture);
                threadResults.add(zFuture);
            }
        }
        
        // Clear any points that may already be in the mesh
        meshster.getPoints().clear();
        
        // Incremation variable
        int i = 0;
        // For each calculation...
        for (Future<Float> vertexResult : threadResults)
        {
            try
            {
                // ...get it.
                points[i] = vertexResult.get();
                
                // Vertex positions must be added to the mesh view individually
                // for some reason. I tried adding the entire array to the mesh
                // view after this loop, and all of the vertices were left
                // positioned at 0,0,0.
                meshster.getPoints().addAll(points[i]);
            }
            catch (InterruptedException | ExecutionException ex)
            {
                points[i] = 0;
            }
            
            i++;
        }
        
        exster.shutdown();
    }
    
    /**
     * Loads the UV mapping positions into the mesh
     */
    private void loadTexturePositions()
    {
        // The percentage of the width and height of the displacement image that
        // each face would occupy
        float faceSizeU = (float)(1.0 / (width - 1));
        float faceSizeV = (float)(1.0 / (depth - 1));
        
        // An incrementor for the array element
        int i = 0;
        
        // For each row in reverse order... (reverse order prevents the maps
        // from being flipped horizontally)
        for (int v = depth - 1; v > -1; v--)
        {
            // ...and for each column...
            for (int u = 0; u < width; u++)
            {
                // ...get the percentage that the point is from the vertical
                // edges.
                texturePositions[i] = u * faceSizeU;
                // Get the percentage that the pooint is from the horizontal
                // edges
                texturePositions[i + 1] = v * faceSizeV;
                
                i = i + 2;
            }
        }
        
        // Remove any old UV coordinates
        meshster.getTexCoords().clear();
        // Add the new ones
        meshster.getTexCoords().setAll(texturePositions);
    }
    
    /**
     * Sets the bump map
     * 
     * @param bumpster The bump map
     */
    public void setBump(Image bumpster)
    {
        texture.setBumpMap(bumpster);
    }
    
    /**
     * Sets the number of rows of vertices that the mesh has
     * 
     * @param depthster The depth of the mesh in vertices
     */
    public void setDepth(int depthster)
    {
        depth = depthster;
        
        // Re-initialize and re-calculate the variables that rely on the mesh's
        // depth in their calculations
        facesAmount = ((width - 1) * 2) * (depth - 1);
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        faces = new int[facesAmount * INTS_PER_FACE];
        
        loadDisplacementPixels();
        loadTexturePositions();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Sets the mesh's displacement map
     * 
     * @param dister The displacement map
     */
    public void setDisplacement(Image dister)
    {
        displacement = dister;
        
        // Re-initialize and re-calculate the variables that rely on the mesh's
        // displacement map in their calculations
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        vertexRelatives = new Color[(int)displacement.getWidth()]
                [(int)displacement.getHeight()];
        
        loadDisplacementPixels();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Set the strength of the displacement map
     * 
     * @param strengthster The strength of the displacement map
     */
    public void setDisplacementStrength(float strengthster)
    {
        displacementStrength = strengthster;
        
        // Re-initialize and re-calculate the variables that rely on the
        // displacement map's strength in their calculations
        loadDisplacementPixels();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Set the mesh's specular map
     * 
     * @param specster The specular map
     */
    public void setSpecular(Image specster)
    {
        texture.setSpecularMap(specster);
    }
    
    /**
     * Sets the mesh's texture map
     * 
     * @param texster The texture map
     */
    public void setTexture(Image texster)
    {
        texture.setDiffuseMap(texster);
    }
    
    /**
     * Sets the number of columns of vertices that the mesh has
     * 
     * @param widthster The width of the mesh in vertices
     */
    public void setWidth(int widthster)
    {
        width = widthster;
        
        // Re-initialize and re-calculate the variables that rely on the mesh's
        // width in their calculations
        facesAmount = ((width - 1) * 2) * (depth - 1);
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        faces = new int[facesAmount * INTS_PER_FACE];
        
        loadDisplacementPixels();
        loadTexturePositions();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Gets a string representation of all of the variables in this MeshObject
     * 
     * @return A string representation of all of the variables in this
     * MeshObject
     */
    public String toString()
    {
        String stringster = "Project Scaper - MeshObject Properties"
                + "\n--------------------------------------------------"
                + "\nModel Width in Vertices: " + width
                + "\nModel Depth in Vertices: " + depth
                + "\n\nNumber of Faces: " + facesAmount
                + "\n\nDisplacement Strength: " + displacementStrength + "\n";
        
        // These variables are taken from the mesh object instead of the
        // external variables to increase accuracy in debugging
        int[] facesFromMesh = new int[faces.length];
        float[] pointsFromMesh = new float[points.length];
        float[] UVsFromMesh = new float[texturePositions.length];
        
        meshster.getFaces().toArray(facesFromMesh);
        meshster.getPoints().toArray(pointsFromMesh);
        meshster.getTexCoords().toArray(UVsFromMesh);
        
        // A second incrementor for the loops below
        int j = 1;

        // Adds the face variables to the string
        for (int i = 0; i < facesFromMesh.length; i = i + INTS_PER_FACE)
        {
            stringster = stringster + "\nFace #" + j + ": " + facesFromMesh[i]
                    + ", " + facesFromMesh[i + 1] + ", " + facesFromMesh[i + 2]
                    + ", " + facesFromMesh[i + 3] + ", " + facesFromMesh[i + 4]
                    + ", " + facesFromMesh[i + 5];
                    
            j++;
        }
        
        stringster = stringster + "\n";
        
        j = 1;
        
        // Adds the point variables to the string
        for (int i = 0; i < pointsFromMesh.length; i = i + DIMENSIONS)
        {
            stringster = stringster + "\nPoint #" + j + ": " + pointsFromMesh[i]
                    + ", " + pointsFromMesh[i + 1] + ", "
                    + pointsFromMesh[i + 2];
                    
            j++;
        }
        
        stringster = stringster + "\n";
        
        j = 1;
        
        // Adds the UV variables to the string
        for (int i = 0; i < UVsFromMesh.length; i = i + 2)
        {
            stringster = stringster + "\nTexture UV #" + j + ": "
                    + UVsFromMesh[i] + ", " + UVsFromMesh[i + 1];
                    
            j++;
        }
        
        return stringster;
    }
}
