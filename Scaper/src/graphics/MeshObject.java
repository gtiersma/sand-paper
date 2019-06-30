package graphics;


import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/*
 * Programmer: Tiersma, George
 * Chemeketa Community College
 * May 10, 2019
 * Class: CIS234J
 * Assignment: Final Project
 * File Name: MeshObject.java
 */

/**
 * The 3D object designed from the materials imported from the user
 * 
 * @author <a href= "mailto:gtiersma@my.chemeketa.edu" >George Tiersma</a>
 */
public class MeshObject
{
    // The number of integers in the face array that are needed to define each
    // face
    private final int INTS_PER_FACE = 6;
    // A multiplier that is used for the size of each face
    private final int FACE_SIZE = 10;
    private final int DIMENSIONS = 3;
    
    // A multiplier for how strong the displacement map will shift vertices
    private final double STRENGTH_MULTIPLIER = 10;
    
    // The width of the mesh in vertices
    private int width;
    // The depth of the mesh in vertices
    private int depth;
    
    // The number of faces in the mesh
    private int facesNum;
    
    // The multiplier for the displacement map strength that is set by the user
    private double displacementStrength;
    
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
        facesNum = ((width - 1) * (depth - 1) * 2);
        
        // Calculate number of integers needed for the face data
        faces = new int[facesNum * INTS_PER_FACE];
        
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
     * Prepares and then gets the mesh view
     * 
     * @return The mesh view
     */
    public MeshView getMeshView()
    {
        // Re-initialize the mesh view
        viewster = new MeshView(meshster);
        viewster.setDrawMode(DrawMode.FILL);
        viewster.setMaterial(texture);
        
        return viewster;
    }
    
    /**
     * Gets the position of a vertex on either the x, y, or z scale taking into
     * consideration how far the displacement map should shift it
     * 
     * @param x The vertex's row on the x axis
     * @param z The vertex's row on the z axis
     * @param dimension The dimension to which the value being returned is to be
     *                  used to shift the vertex.
     *                  For example, a value of 'x' would mean that the value
     *                  being returned should be applied to the vertex's x
     *                  position.
     * 
     * @return The vertex's position
     */
    private double getRelativePositioning(int x, int z, char dimension)
    {
        // Calculate the row and column of the pixel that should be retrieved
        // for this particular vertex
        int pixelRow = (int)(widthPixels * x);
        int pixelColumn = (int)(heightPixels * z);
        
        // The position of the vertex if no displacement map was going to be
        // applied
        int originalPosition;
        
        // How far the vertex should be shifted. It may be negative.
        double shiftAmount;
        
        // The new position of the vertex
        double vertexPosition = 0;
        
        switch (dimension)
        {
            // If the x position is being retrieved...
            case 'x':
                
                // ...get the red amount in the correct pixel.
                double redAmount
                        = vertexRelatives[pixelRow][pixelColumn].getRed();
                
                originalPosition = x * FACE_SIZE;
                
                // Calculate the amount to be shifted
                shiftAmount = (redAmount - 0.5) * displacementStrength
                        * STRENGTH_MULTIPLIER;
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
                
            // If the y position is being retrieved...
            case 'y':
                
                // ...get the green amount in the correct pixel.
                double greenAmount
                        = vertexRelatives[pixelRow][pixelColumn].getGreen();
                
                vertexPosition = (greenAmount - 0.5) * displacementStrength
                        * STRENGTH_MULTIPLIER;
                
                break;
                
            // If the z position is being retrieved...
            case 'z':
                
                // ...get the blue amount in the correct pixel.
                double blueAmount
                        = vertexRelatives[pixelRow][pixelColumn].getBlue();
                
                originalPosition = z * FACE_SIZE;
                
                // Calculate the amount to be shifted
                shiftAmount = (blueAmount - 0.5) * displacementStrength
                        * STRENGTH_MULTIPLIER;
                
                vertexPosition = originalPosition + shiftAmount;
                
                break;
        }
        
        return vertexPosition;
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
        for (int i = 0; i + 1 < facesNum * INTS_PER_FACE;
                i = i + INTS_PER_FACE * 2)
        {
            // ...if the point the face is being built off of is not at the
            // verticle edge on the right side of the mesh...
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
     * Calculate the vertices and load them into the mesh
     */
    public void loadPoints()
    {
        // The index to which a value is currently being assigned
        int index = 0;
        
        // For each column of vertices in the mesh...
        for (int z = 0; z < depth; z++)
        {
            // ...and for each row of vertices...
            for (int x = 0; x < width; x++)
            {
                // ...get the x position.
                points[index] = (x * FACE_SIZE)
                        + (float)getRelativePositioning(x, z, 'x');
                
                index++;
                
                // Get the y position
                points[index] = (float)getRelativePositioning(x, z, 'y');
                
                index++;
                
                // Get the z position
                points[index] = (z * FACE_SIZE)
                        + (float)getRelativePositioning(x, z, 'z');
                
                index++;
            }
        }
        
        // Clear any points that may already be in the mesh
        meshster.getPoints().clear();
        // Add the new points
        meshster.getPoints().addAll(points);
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
        facesNum = ((width - 1) * 2) * (depth - 1);
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        faces = new int[facesNum * INTS_PER_FACE];
        
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
    public void setDisplacementStrength(double strengthster)
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
        facesNum = ((width - 1) * 2) * (depth - 1);
        widthPixels = displacement.getWidth() / width;
        heightPixels = displacement.getHeight() / depth;
        points = new float[width * depth * DIMENSIONS];
        texturePositions = new float[width * depth * 2];
        faces = new int[facesNum * INTS_PER_FACE];
        
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
                + "\n\nNumber of Faces: " + facesNum
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
