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
 * Parent class containing the similarities between the terrain and the
 * population objects
 * 
 * @author George Tiersma
 */
public class MeshObject
{
    // The number of dimensions
    protected final byte DIMENSIONS = 3;
    // The number of integers in the face array that are needed to define each
    // face
    protected final byte INTS_PER_FACE = 6;
    
    // Multiplied to the user-defined displacement strength variable to increase
    // distance between vertices
    protected final int DISPLACEMENT_MULTIPLIER = 10;
    
    // The size of each side of each face on the mesh when the mesh is not
    // displaced
    protected short faceWidth;
    protected short faceDepth;
    
    // The width of the mesh in vertices
    protected short width;
    // The depth of the mesh in vertices
    protected short depth;
    
    // The multiplier for the displacement map strength that is set by the user
    protected int displacementStrength;
    
    // The number of faces in the mesh
    protected int facesAmount;
    
    // The distance on the x scale in number of pixels in the displacement map
    // that one vertex would retrieve from that of an adjacent vertex.
    protected int widthPixels;
    // The distance on the y scale in number of pixels in the displacement map
    // that one vertex would retrieve from that of an adjacent vertex.
    protected int heightPixels;
    
    // Face data
    protected int[] faces;
    
    // Point data
    protected float[] points;
    // UV data
    protected float[] texturePositions;
    
    // Displacement map
    protected Image displacement;
    
    // Contains the diffuse, bump and displacement map
    protected PhongMaterial texture;
    
    // The mesh
    protected TriangleMesh meshster;
    
    // The view containing the mesh
    protected MeshView viewster;
    
    // The x & y matrix of the color data for each vertex taken from the
    // displacement map
    protected Color[][] vertexRelatives;
    
    /**
     * CONSTRUCTOR
     * 
     * @param fWidth The width of each face on the mesh when the mesh is not
     *               displaced
     * @param fDepth The depth of each face on the mesh when the mesh is not
     *                displaced
     * @param widthster The width of the mesh in vertices
     * @param depthster The depth of the mesh in vertices
     * @param strengthster The multiplier for the displacement map that is set
     *                     by the user
     * @param dister The displacement map
     */
    public MeshObject(short fWidth, short fDepth, short widthster,
            short depthster, int strengthster, Image dister)
    {
        width = widthster;
        depth = depthster;
        displacementStrength = strengthster * DISPLACEMENT_MULTIPLIER;
        
        // Calculate number of faces
        facesAmount = (width - 1) * (depth - 1) * 2;
        
        faceWidth = fWidth;
        faceDepth = fDepth;
        
        // Calculate number of integers needed for the face data
        faces = new int[facesAmount * INTS_PER_FACE];
        
        // Calculate number of floats needed for the float data
        points = new float[width * depth * DIMENSIONS];
        
        // Calculate number of floats needed for the UV data
        texturePositions = new float[width * depth * 2];
        
        displacement = dister;
        
        widthPixels = (int)(displacement.getWidth() / width);
        heightPixels = (int)(displacement.getHeight() / depth);
        
        texture = new PhongMaterial();
        
        meshster = new TriangleMesh();
        
        viewster = new MeshView(meshster);
        
        // Make the array big enough to hold the color data for each vertex
        vertexRelatives = new Color[width][depth];
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
     * variables and objects within this MeshObject
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
        
        // The distance of pixels between each pixel to be gathered.
        double xSpacing = displacement.getWidth() / width;
        double ySpacing = displacement.getHeight() / depth;
        
        // For each column of pixels...
        for (short i = 0; i < width; i++)
        {
            // ...and for each row of pixels...
            for (short j = 0; j < depth; j++)
            {
                // Values in each row need to be put into the array backwards to
                // prevent the displacement map from being flipped horizontally
                // when applied to the mesh, so this "new j" is used instead of
                // the regular "j" variable.
                int newJ = depth - j - 1;
                
                // Calculate the x and y position of the pixel in the map to be
                // gathered for this vertex
                int xPosition = (int)(xSpacing * i);
                int yPosition = (int)(ySpacing * j);
                
                // Get the correct color
                vertexRelatives[i][newJ] = readster.getColor(xPosition,
                        yPosition);
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
        List<Future<Integer>> threadResults = new ArrayList<>();
        
        // Pool of threads for calculating the positions of the vertices
        ExecutorService exster = Executors.newCachedThreadPool();
        
        // For each column of vertices in the mesh...
        for (short z = 0; z < depth; z++)
        {
            // ...and for each row of vertices...
            for (short x = 0; x < width; x++)
            {
                // Thread for finding the x position of the vertex
                Callable<Integer> xThread = new VertexThread(faceWidth, x,
                        displacementStrength, 'x', vertexRelatives[x][z]);
                
                // Thread for finding the y position of the vertex
                Callable<Integer> yThread = new VertexThread(0, 0,
                        displacementStrength, 'y', vertexRelatives[x][z]);
                
                // Thread for finding the z position of the vertex
                Callable<Integer> zThread = new VertexThread(faceDepth, z,
                        displacementStrength, 'z', vertexRelatives[x][z]);
                
                // Get the calculations as they become available
                Future<Integer> xFuture = exster.submit(xThread);
                Future<Integer> yFuture = exster.submit(yThread);
                Future<Integer> zFuture = exster.submit(zThread);
                
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
        for (Future<Integer> vertexResult : threadResults)
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
        
        // An incrementor for the array
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
    public void setDepth(short depthster)
    {
        depth = depthster;
        
        // Re-initialize and re-calculate the variables that rely on the mesh's
        // depth in their calculations
        facesAmount = ((width - 1) * 2) * (depth - 1);
        widthPixels = (int)(displacement.getWidth() / width);
        heightPixels = (int)(displacement.getHeight() / depth);
        faces = new int[facesAmount * INTS_PER_FACE];
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        vertexRelatives = new Color[width][depth];
        
        loadDisplacementPixels();
        loadTexturePositions();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Sets the mesh's diffuse map
     * 
     * @param difster The diffuse map
     */
    public void setDiffuse(Image difster)
    {
        texture.setDiffuseMap(difster);
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
        widthPixels = (int)(displacement.getWidth() / width);
        heightPixels = (int)(displacement.getHeight() / depth);
        vertexRelatives = new Color[width][depth];
        
        loadDisplacementPixels();
        loadPoints();
        loadFaces();
    }
    
    /**
     * Set the strength of the displacement map
     * 
     * @param strengthster The strength of the displacement map
     */
    public void setDisplacementStrength(int strengthster)
    {
        displacementStrength = strengthster * DISPLACEMENT_MULTIPLIER;
        
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
     * Sets the number of columns of vertices that the mesh has
     * 
     * @param widthster The width of the mesh in vertices
     */
    public void setWidth(short widthster)
    {
        width = widthster;
        
        // Re-initialize and re-calculate the variables that rely on the mesh's
        // width in their calculations
        facesAmount = ((width - 1) * 2) * (depth - 1);
        widthPixels = (int)(displacement.getWidth() / width);
        heightPixels = (int)(displacement.getHeight() / depth);
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        faces = new int[facesAmount * INTS_PER_FACE];
        vertexRelatives = new Color[width][depth];
        
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
    @Override
    public String toString()
    {
        String stringster = "MeshObject Properties"
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
