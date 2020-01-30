package graphics;

import java.io.File;
import java.util.Random;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * A group of individual MeshObjects spread throughout the terrain
 * 
 * @author George Tiersma
 */
public class Population
{
    // Divides the width and height values to keep Individuals from being too
    // large
    final int SIZE_DIVIDER = 3;
    
    // A value used in calculations to get the correct value for the brightness
    // of a pixel
    final double COLOR_ADJUSTMENT = 100;
    
    // Default textures to use for when no maps have been selected by the user
    final TextureObject GRAY_TEXTURE
            = new TextureObject(new File("src/graphics/gray.png"));
    final TextureObject WHITE_TEXTURE = new TextureObject();
    
    // The number of Individuals that this Population consists of
    int size;
    
    // A multiplier of how much the vertices on an Individual are to be
    // displaced
    int displacementStrength;
    
    // The width and height of each Individual (measured in the number of
    // vertices)
    int vertexWidth;
    int vertexHeight;
    
    String name;
    
    // A matrix for each vertex on the Terrain. The array stores whether or not
    // an Individual is to appear over a vertex.
    boolean locations[][];
    
    // The maps for each Individual
    TextureObject bump;
    TextureObject specular;
    TextureObject texture;
    
    // A grayscale image for the probability of an Individual being created on a
    // specific vertex on the Terrain
    TextureObject placement;
    // An image for determining how much each Individual should be shifted on
    // the x (red), y (green) or z (blue) scale.
    TextureObject shift;
    
    // Grayscale images for determining the width and height of each Individual
    TextureObject width;
    TextureObject height;
    
    // 2 displacement maps for determining the range to which each Individual
    // will be displaced
    TextureObject displacementRange[];
    
    // Each individual this population consists of
    Individual individuals[];
    
    /**
     * CONSTRUCTOR
     * 
     * @param strength The strength of the displacement map
     * @param terrainWidth The width of the terrain (Measured in vertices)
     * @param terrainHeight The height of the terrain (Measured in vertices)
     * @param vertWidth The width of each Individual (Measured in vertices)
     * @param vertHeight The height of each Individual (Measured in vertices)
     * @param namster The name of the population
     */
    public Population(int strength, int terrainWidth, int terrainHeight,
            int vertWidth, int vertHeight, String namster)
    {
        size = 0;
        
        displacementStrength = strength;
        
        vertexWidth = vertWidth;
        vertexHeight = vertHeight;
        
        name = namster;
        
        // The dimensions of this array must match the terrain dimensions to
        // ensure that there is 1 boolean for each vertex on the terrain
        locations = new boolean[terrainWidth][terrainHeight];
        
        bump = WHITE_TEXTURE;
        specular = WHITE_TEXTURE;
        texture = WHITE_TEXTURE;
        
        shift = GRAY_TEXTURE;
        placement = WHITE_TEXTURE;
        
        width = GRAY_TEXTURE;
        height = GRAY_TEXTURE;
        
        displacementRange = new TextureObject[2];
        displacementRange[0] = WHITE_TEXTURE;
        displacementRange[1] = WHITE_TEXTURE;
        
        individuals = new Individual[0];
    }
    
    /**
     * Calculates which vertices on the terrain should have an Individual on it
     */
    private void calculateLocations()
    {
        System.out.println("Population -> Determining where to place Individuals for population " + name + "...");
        size = 0;
        
        // For each row of the terrain's vertices...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of the terrain's vertices...
            for (int j = 0; j < locations[i].length; j++)
            {
                // This is the inverse of the j variable (so it will refer to
                // the pixel or Individual at the opposite end). It must be used
                // with getting the pixel color instead of the regular j
                // variable to prevent the population from having a mirrored
                // position on the terrain.
                int newJ = locations[i].length - j - 1;
                
                // ...get the shade of the corresponding pixel on the placement
                // image.
                Color pixelColor = getPixelColor(true, i, newJ, placement);
                
                // Calculate whether or not an Individual should be created
                // there
                locations[i][j] = isRandomlyBrightEnough(pixelColor);
                
                // If one is to be created there...
                if (locations[i][j])
                {
                    // ...increment the number of Individuals to create.
                    size++;
                }
            }
        }
    }
    
    /**
     * Creates an Individual for this population
     * 
     * @param faceWidth The width of each face on the Individual
     * @param faceHeight The height of each face on the Individual
     * @param shiftX How much the Individual is to be shifted on the x scale
     * @param shiftY How much the Individual is to be shifted on the y scale
     * @param shiftZ How much the Individual is to be shifted on the z scale
     * @param x The x position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param y The y position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param z The z position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param xRotate The currently set vertical rotation value
     * @param yRotate The currently set horizontal rotation value
     */
    private void createIndividual(int faceWidth, int faceHeight, int shiftX,
            int shiftY, int shiftZ, double x, double y, double z,
            double xRotate, double yRotate)
    {
        System.out.println("Population -> Creating an Individual for population " + name + "...");
        Image displacement = generateDisplacement();
        
        Individual newIndividual = new Individual(vertexWidth, vertexHeight,
                faceWidth, faceHeight, displacementStrength, shiftX, shiftY,
                shiftZ, x, y, z, xRotate, yRotate, displacement);
        
        newIndividual.load();
        
        // Create a new array with room for the additional element
        Individual newIndividuals[] = new Individual[individuals.length + 1];
        
        // Copy the Individuals to the new array
        System.arraycopy(individuals, 0, newIndividuals, 0, individuals.length);
        
        // Add the new Individual to the new array
        newIndividuals[individuals.length] = newIndividual;
        
        // Make the old array into the new array
        individuals = newIndividuals;
    }
    
    /**
     * (Re)creates all of the Individuals for this population
     * 
     * @param xRotate The currently set vertical rotation value
     * @param yRotate The currently set horizontal rotation value
     * @param terrainPoints The positions of each vertex in the terrain
     */
    private void createIndividuals(double xRotate,
            double yRotate, float[] terrainPoints)
    {
        System.out.println("Population -> Creating all of the Individuals for population " + name + "...");
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // The shift adjustments for the Individual
                    int shiftX;
                    int shiftY;
                    int shiftZ;
                    
                    // Calculates the index of the point on the terrain to which
                    // this Individual is to be created upon
                    int pointIndex = ((j * locations.length) + i) * 3;
                    
                    // The width and height multipliers to determine the size of
                    // the Individual's faces
                    int widthster;
                    int heightster;
                    
                    // The position of the point on the terrain to which this
                    // new Individual will belong
                    double x = terrainPoints[pointIndex];
                    double y = terrainPoints[pointIndex + 1];
                    double z = terrainPoints[pointIndex + 2];
                    
                    // Get the correct pixel colors for this Individual
                    Color shiftColor = getPixelColor(true, i, j, shift);
                    Color widthColor = getPixelColor(true, i, j, width);
                    Color heightColor = getPixelColor(true, i, j, height);
                    
                    shiftX = getColorValue(true, 'r', shiftColor);
                    shiftY = getColorValue(true, 'g', shiftColor);
                    shiftZ = getColorValue(true, 'b', shiftColor);
                    
                    // The values returned from these functions are too large
                    // for the width and height, so it is divided to a smaller
                    // value
                    widthster = getColorValue(false, ' ', widthColor)
                            / SIZE_DIVIDER;
                    heightster = getColorValue(false, ' ', heightColor)
                            / SIZE_DIVIDER;
                    
                    createIndividual(widthster, heightster, shiftX, shiftY,
                            shiftZ, x, y, z, xRotate, yRotate);
                }
            }
        }
    }
    
    /**
     * Generates a displacement map with pixels within the range of the 2
     * displacement maps
     * 
     * @return A displacement map with pixels within the range of the 2
     * displacement maps
     */
    private Image generateDisplacement()
    {
        System.out.println("Population -> Constructing a new displacement map for population " + name + "...");
        // The generated map
        WritableImage newDisplacement = new WritableImage(vertexWidth,
                vertexHeight);
        PixelWriter writster = newDisplacement.getPixelWriter();
        
        // For each row of vertices on the Individual...
        for (int i = 0; i < vertexWidth; i++)
        {
            // ...and for each column of vertices...
            for (int j = 0; j < vertexHeight; j++)
            {
                // Color for how the vertex is to be displaced
                Color randomColor;
                
                // The 2 colors representing the smallest and largest possible
                // values of red, green and blue for the randomly generated
                // pixel color
                Color pixelColors[] = new Color[2];
                
                pixelColors[0] = getPixelColor(false, i, j,
                        displacementRange[0]);
                pixelColors[1] = getPixelColor(false, i, j,
                        displacementRange[1]);
                
                randomColor = getRandomColor(pixelColors);
                
                writster.setColor(i, j, randomColor);
            }
        }
        
        return newDisplacement;
    }
    
    /**
     * Gets the bump map for this Population
     * 
     * @return A TextureObject of this Population's bump map
     */
    public TextureObject getBumpMap()
    {
        System.out.println("Population -> Getting population " + name + " bump map " + bump.getName() + "...");
        return bump;
    }
    
    /**
     * Gets a random number depending upon the brightness of the given color
     * 
     * @param negativeRange Whether or not the number being returned should have
     *                      the possibility of being negative. If this is true,
     *                      the range of the number that will be returned will
     *                      be from -50 - +50. If this is false, the range of
     *                      the number will be from 1 - 100.
     * @param channel The color channel to perform the calculations from. 'r' is
     *                for red. 'g' is for green. 'b' is for blue. Any other
     *                character will return the brightness.
     * @param colster The given color
     * 
     * @return A random number
     */
    private int getColorValue(boolean negativeRange, char channel,
            Color colster)
    {
        System.out.println("Population -> Calculating the value for a color for population " + name + "...");
        int colorValue;
        
        switch (channel)
        {
            case 'r':
                colorValue = (int)(colster.getRed() * COLOR_ADJUSTMENT);
                break;
            case 'g':
                colorValue = (int)(colster.getGreen() * COLOR_ADJUSTMENT);
                break;
            case 'b':
                colorValue = (int)(colster.getBlue() * COLOR_ADJUSTMENT);
                break;
            default:
                colorValue = (int)(colster.getBrightness() * COLOR_ADJUSTMENT);
        }
        
        // If the value should not be negative...
        if (negativeRange)
        {
            // ...put the value in the range of -50 - +50
            colorValue = colorValue - (int)(COLOR_ADJUSTMENT / 2);
        }
        
        return colorValue;
    }
    
    /**
     * Gets the currently set displacement strength for this Population
     * 
     * @return The currently set displacement strength for this Population
     */
    public int getDisplacementStrength()
    {
        System.out.println("Population -> Getting population " + name + " displacement strength " + displacementStrength + "...");
        return displacementStrength;
    }
    
    /**
     * Gets the number of pixels that should separate 2 vertices in a UV map
     * 
     * @param length The number of pixels long that the image is
     * @param vertexAmount The number of vertices that exist between one end of
     *                     the mesh and the other
     * 
     * @return The distance that should exist between 2 non-displaced vertices
     */
    private int getDistanceBetweenVertices(double length, double vertexAmount)
    {
        System.out.println("Population -> Getting distance between 2 non-displaced vertices on the terrain " + (int)(length / vertexAmount) + "...");
        return (int)(length / vertexAmount);
    }
    
    /**
     * Gets the first displacement map used in the displacement range
     * 
     * @return A TextureObject of the first displacement map used in the
     *         displacement range
     */
    public TextureObject getFirstDisplacement()
    {
        System.out.println("Population -> Getting population " + name + " first displacement map in the displacement range " + displacementRange[0].getName() + "...");
        return displacementRange[0];
    }
    
    /**
     * Gets the map used to specify the height of the Individuals in this
     * population
     * 
     * @return A TextureObject of the map used to specify the height of the
     *         Individuals in this population
     */
    public TextureObject getHeight()
    {
        System.out.println("Population -> Getting population " + name + " height map " + height.getName() + "...");
        return height;
    }
    
    /**
     * Gets the name of this population
     * 
     * @return The name of this population
     */
    public String getName()
    {
        System.out.println("Population -> Getting population " + name + " name...");
        return name;
    }
    
    /**
     * Gets a pixel's color in an image based upon the provided vertex position
     * 
     * @param forTerrainVertices Whether or not the UVs are based upon the
     *                           terrain's mesh or the Individuals' meshes
     * @param positionX The X position of the pixel that the color is to be
     *                  retrieved from
     * @param positionY The Y position of the pixel that the color is to be
     *                  retrieved from
     * @param texster The image that the color is to be retrieved from
     * 
     * @return The color of the pixel to be retrieved
     */
    private Color getPixelColor(boolean forTerrainVertices, int positionX,
            int positionY, TextureObject texster)
    {
        System.out.println("Population -> Getting a pixel's color for population " + name + "...");
        // The number of UV points wide that the map will contain
        int widthUV;
        // The number of UV points high that the map will contain
        int heightUV;
        
        // If the image is mapped based on the terrain mesh...
        if (forTerrainVertices)
        {
            // ...get how many vertices wide and high the terrain is.
            widthUV = locations.length;
            heightUV = locations[0].length;
        }
        // ...otherwise...
        else
        {
            // ...get how many vertices wide and high the Individuals of this
            // population are.
            widthUV = vertexWidth;
            heightUV = vertexHeight;
        }
        
        int distanceX = getDistanceBetweenVertices(texster.getWidth(), widthUV);
        int distanceY = getDistanceBetweenVertices(texster.getHeight(),
                heightUV);
        
        int pixelX = getPixelPosition(distanceX, positionX);
        int pixelY = getPixelPosition(distanceY, positionY);
        
        Color colster = texster.getColor(pixelX, pixelY);
        
        return colster;
    }
    
    /**
     * Gets the position of a pixel in an image for the given vertex position
     * 
     * @param distance The distance between each vertex
     * @param vertexPosition The numbered position of the vertex
     * 
     * @return The position of a pixel in an image
     */
    private int getPixelPosition(int distance, int vertexPosition)
    {
        System.out.println("Population -> Getting population " + name + " UV coordinate " + (distance * vertexPosition) + "...");
        return distance * vertexPosition;
    }
    
    /**
     * Gets the placement map
     * 
     * @return A TextureObject of the placement map
     */
    public TextureObject getPlacement()
    {
        System.out.println("Population -> Getting population " + name + " placement map " + placement.getName() + "...");
        return placement;
    }
    
    /**
     * Gets the population of individual meshes
     * 
     * @return the population
     */
    public Group getPopulation()
    {
        System.out.println("Population -> Getting population " + name + "...");
        Group groupster = new Group();
        
        for (Individual individual : individuals)
        {
            groupster.getChildren().add(individual.getMeshView());
        }
        
        return groupster;
    }
    
    /**
     * Gets a random color that is between each of the red, green and blue
     * values of the 2 given pixels
     * 
     * @param colors An array containing the 2 colors that determine the range
     * 
     * @return A random color that is between the 2 given pixels
     */
    private Color getRandomColor(Color[] colors)
    {
        System.out.println("Population -> Getting a random color for population " + name + "...");
        // The color values for the random color
        double newRed;
        double newGreen;
        double newBlue;
        
        // The min and max color values for the color range
        double reds[] = new double[2];
        double greens[] = new double[2];
        double blues[] = new double[2];
        
        // The random color
        Color newColor;
        
        reds[0] = colors[0].getRed();
        reds[1] = colors[1].getRed();
        greens[0] = colors[0].getGreen();
        greens[1] = colors[1].getGreen();
        blues[0] = colors[0].getBlue();
        blues[1] = colors[1].getBlue();
        
        newRed = getRandomNumber(reds[0], reds[1]);
        newGreen = getRandomNumber(greens[0], greens[1]);
        newBlue = getRandomNumber(blues[0], blues[1]);
        
        newColor = new Color(newRed, newGreen, newBlue, 1.0);
        
        return newColor;
    }
    
    /**
     * Gets a random number between the 2 given values
     * 
     * @param min The least possible value
     * @param max The most possible value
     * 
     * @return A random number between the 2 given values
     */
    private double getRandomNumber(double min, double max)
    {
        System.out.println("Population -> Getting a random number between " + min + " and " + max + " for population " + name + "...");
        double difference = max - min;
        
        double newNumber;
        
        Random ranster = new Random();
        
        newNumber = ranster.nextDouble() * difference + min;
        
        return newNumber;
    }
    
    /**
     * Gets the second displacement map used to specify the displacement range
     * 
     * @return A TextureObject of the second displacement map used to specify
     *         the displacement range
     */
    public TextureObject getSecondDisplacement()
    {
        System.out.println("Population -> Getting population " + name + " second displacement map in the displacement range " + displacementRange[1].getName() + "...");
        return displacementRange[1];
    }
    
    /**
     * Gets the shift map assigned to this population
     * 
     * @return A TextureObject of the shift map assigned to this population
     */
    public TextureObject getShift()
    {
        System.out.println("Population -> Getting population " + name + " shift map " + shift.getName() + "...");
        return shift;
    }
    
    /**
     * Gets the specular map
     * 
     * @return A TextureObject of the specular map
     */
    public TextureObject getSpecularMap()
    {
        System.out.println("Population -> Getting population " + name + " specular map " + specular.getName() + "...");
        return specular;
    }
    
    /**
     * Gets the texture
     * 
     * @return A TextureObject of the texture
     */
    public TextureObject getTexture()
    {
        System.out.println("Population -> Getting population " + name + " texture " + texture.getName() + "...");
        return texture;
    }
    
    /**
     * Gets the height of each Individual measured in vertices
     * 
     * @return The height of each Individual (Measured in vertices)
     */
    public int getVertexHeight()
    {
        System.out.println("Population -> Getting population " + name + " height " + vertexHeight + "...");
        return vertexHeight;
    }
    
    /**
     * Gets the width of each Individual measured in vertices
     * 
     * @return The width of each Individual (Measured in vertices)
     */
    public int getVertexWidth()
    {
        System.out.println("Population -> Getting population " + name + " width " + vertexWidth + "...");
        return vertexWidth;
    }
    
    /**
     * Gets the map used to specify the width of the Individuals in this
     * population
     * 
     * @return A TextureObject of the map used to specify the width of the
     *         Individuals in this population
     */
    public TextureObject getWidth()
    {
        System.out.println("Population -> Getting population " + name + " width map " + width.getName() + "...");
        return width;
    }
    
    /**
     * Gets a random boolean. How bright the given color is determines the odds
     * of the boolean being true. Brighter colors have a larger chance of
     * resulting with true.
     * 
     * @param shade The color that the function's probability is based upon
     * 
     * @return A random boolean
     */
    private boolean isRandomlyBrightEnough(Color shade)
    {
        System.out.println("Population -> Determining if a color is bright enough for population " + name + "...");
        boolean brightEnough = false;
        
        double brightness = shade.getBrightness();
        
        double randomValue;
        
        Random ranster = new Random();
        
        randomValue = ranster.nextDouble();
        
        // If the random value is greater than the value from the color's
        // brightness...
        if (brightness < randomValue)
        {
            // ...true is to be returned.
            brightEnough = true;
        }
        
        return brightEnough;
    }
    
    /**
     * Prepares the population to be used
     * 
     * @param xRotate The camera's set vertical rotation
     * @param yRotate The camera's set horizontal rotation
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void load(double xRotate, double yRotate, float[] terrainPoints)
    {
        System.out.println("Population -> Loading population " + name + "...");
        // Remove all Individuals
        individuals = new Individual[0];
        
        calculateLocations();
                
        createIndividuals(xRotate, yRotate, terrainPoints);
    }
    
    /**
     * Positions the Individuals based upon the terrain's vertex positions
     * provided
     * 
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void reposition(float[] terrainPoints)
    {
        System.out.println("Population -> Repositioning population " + name + "...");
        // The index of the Individual currently being repositioned
        int index = 0;
        
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // ...get the index of the coordinates for the vertex for
                    // the current Individual.
                    int xIndex = index * 3;
                    int yIndex = xIndex + 1;
                    int zIndex = xIndex + 2;
                    
                    // Reposition it
                    individuals[index].reposition(terrainPoints[xIndex],
                            terrainPoints[yIndex], terrainPoints[zIndex]);
                            
                    index++;
                }
            }
        }
    }
    
    /**
     * Sets the bump map
     * 
     * @param bumpster A TextureObject of the bump map
     */
    public void setBumpMap(TextureObject bumpster)
    {
        System.out.println("Population -> Setting bump map to " + bumpster.getName() + " for population " + name + "...");
        bump = bumpster;
        
        for (Individual individual : individuals)
        {
            individual.setBump(bump.getImage());
        }
    }
    
    /**
     * Sets the displacement strength
     * 
     * @param strength The displacement strength
     */
    public void setDisplacementStrength(int strength)
    {
        System.out.println("Population -> Setting displacement strength to " + strength + " for population " + name + "...");
        displacementStrength = strength;
        
        for (Individual inster : individuals)
        {
            inster.setDisplacementStrength(displacementStrength);
        }
    }
    
    /**
     * Sets the first displacement map to be used in the displacement range
     * 
     * @param dister A displacement map
     */
    public void setFirstDisplacement(TextureObject dister)
    {
        System.out.println("Population -> Setting the first displacement map in the displacement range to " + dister.getName() + " for population " + name + "...");
        displacementRange[0] = dister;
        
        for (Individual inster : individuals)
        {
            inster.setDisplacement(generateDisplacement());
        }
    }
    
    /**
     * Sets the map used to determine the height of the Individuals in this
     * population
     * 
     * @param heightster A map used to determine the height of the Individuals
     *                   in this population
     */
    public void setHeight(TextureObject heightster)
    {
        System.out.println("Population -> Setting the height map to " + heightster.getName() + " for population " + name + "...");
        height = heightster;
        
        int count = 0;
        
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // Get the correct pixel color for this Individual
                    Color heightColor = getPixelColor(true, i, j, height);
                    
                    int heightBrightness = getColorValue(false, ' ',
                            heightColor);
                            
                    heightBrightness = heightBrightness / SIZE_DIVIDER;
                    
                    individuals[count].setFaceHeight(heightBrightness);
                    
                    count++;
                }
            }
        }
    }
    
    /**
     * Sets the placement map
     * 
     * @param xRotate The camera's vertical rotation value
     * @param yRotate The camera's horizontal rotation value
     * @param terrainPoints The array of coordinates used to position the points
     *                      in the terrain's MeshView
     * @param placster The placement map
     */
    public void setPlacement(double xRotate, double yRotate,
            float terrainPoints[], TextureObject placster)
    {
        System.out.println("Population -> Setting the placement map to " + placster.getName() + " for population " + name + "...");
        placement = placster;
        
        load(xRotate, yRotate, terrainPoints);
        
        setTexture(texture);
        setBumpMap(bump);
        setSpecularMap(specular);
    }
    
    /**
     * Sets the shift map
     * 
     * @param shiftster The shift map
     */
    public void setShift(TextureObject shiftster)
    {
        System.out.println("Population -> Setting the shift map to " + shiftster.getName() + " for population " + name + "...");
        shift = shiftster;
        
        int count = 0;
        
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // ...get the correct pixel color for this Individual
                    Color shiftColor = getPixelColor(true, i, j, shift);
                    
                    // Get the correct shift amounts for this color
                    int shiftX = getColorValue(true, 'r', shiftColor);
                    int shiftY = getColorValue(true, 'g', shiftColor);
                    int shiftZ = getColorValue(true, 'b', shiftColor);
                    
                    individuals[count].setShift(shiftX, shiftY, shiftZ);
                    
                    count++;
                }
            }
        }
    }
    
    /**
     * Rotates the Individuals in correspondence with the camera's vertical
     * rotation value
     * 
     * @param angle The camera's vertical rotation value
     */
    public void setRotationX(double angle)
    {
        System.out.println("Population -> Setting the population " + name + " rotation on the x axis to " + angle + "...");
        for (Individual individual : individuals)
        {
            individual.setRotationX(angle);
        }
    }
    
    /**
     * Rotates the Individuals in correspondence with the camera's horizontal
     * rotation value
     * 
     * @param angle The camera's horizontal rotation value
     */
    public void setRotationY(double angle)
    {
        System.out.println("Population -> Setting the population " + name + " rotation on the y axis to " + angle + "...");
        for (Individual individual : individuals)
        {
            individual.setRotationY(angle);
        }
    }
    
    /**
     * Sets the second displacement map to be used in the displacement range
     * 
     * @param dister A displacement map
     */
    public void setSecondDisplacement(TextureObject dister)
    {
        System.out.println("Population -> Setting the second displacement map in the displacement range to " + dister.getName() + " for the population " + name + "...");
        displacementRange[1] = dister;
        
        for (Individual inster : individuals)
        {
            inster.setDisplacement(generateDisplacement());
        }
    }
    
    /**
     * Sets the specular map
     * 
     * @param specster The specular map
     */
    public void setSpecularMap(TextureObject specster)
    {
        System.out.println("Population -> Setting the specular map to " + specster.getName() + " for the population " + name + "...");
        specular = specster;
        
        for (Individual individual : individuals)
        {
            individual.setSpecular(specular.getImage());
        }
    }
    
    /**
     * Sets the texture
     * 
     * @param texster A texture
     */
    public void setTexture(TextureObject texster)
    {
        System.out.println("Population -> Setting the texture to " + texster.getName() + " for the population " + name + "...");
        texture = texster;
        
        for (Individual individual : individuals)
        {
            individual.setTexture(texture.getImage());
        }
    }
    
    /**
     * Sets the height of the Individuals
     * 
     * @param heightster the height of the Individuals (Measured in vertices)
     */
    public void setVertexHeight(int heightster)
    {
        System.out.println("Population -> Setting the population " + name + " height to " + heightster + "...");
        vertexHeight = heightster;
        
        for (Individual inster : individuals)
        {
            inster.setDepth(vertexHeight);
        }
    }
    
    /**
     * Sets the width of the Individuals
     * 
     * @param widthster the width of the Individuals (Measured in vertices)
     */
    public void setVertexWidth(int widthster)
    {
        System.out.println("Population -> Setting the population " + name + " width to " + widthster + "...");
        vertexWidth = widthster;
        
        for (Individual inster : individuals)
        {
            inster.setWidth(vertexWidth);
        }
    }
    
    /**
     * Sets the map used to determine the width of the Individuals in this
     * population
     * 
     * @param widthster A map used to determine the width of the Individuals in
     *                  this population
     */
    public void setWidth(TextureObject widthster)
    {
        System.out.println("Population -> Setting the width map to " + width.getName() + " the population " + name + "...");
        width = widthster;
        
        int count = 0;
        
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // Get the correct pixel color for this Individual
                    Color widthColor = getPixelColor(true, i, j, width);
                    
                    int widthBrightness = getColorValue(false, ' ', widthColor)
                            / SIZE_DIVIDER;
                    
                    individuals[count].setFaceWidth(widthBrightness);
                    
                    count++;
                }
            }
        }
    }
    
    /**
     * Gets a string representation of all of the variables in this MeshObject
     * 
     * @return A string representation of all of the variables in this
     *         Population
     */
    @Override
    public String toString()
    {
        String stringster = "Population " + name + " properties:\n";
        stringster = stringster + "-----------------------------------------\n";
        
        stringster = stringster + "Population size: " + size + "\n";
        
        stringster = stringster + "Displacement Strength: "
                + displacementStrength + "\n\n";
        
        stringster = stringster + "Width of each Individual (vertices): "
                + vertexWidth + "\n";
        stringster = stringster + "Height of each Individual (vertices): "
                + vertexHeight + "\n\n";
        
        stringster = stringster + "Locations image: " + placement.getName()
                + "\n";
        stringster = stringster + "Population locations:\n";
        for (int i = 0; i < locations.length; i++)
        {
            for (int j = 0; j < locations[i].length; j++)
            {
                char isItHere;
                
                if (locations[i][j])
                {
                    isItHere = 'O';
                }
                else
                {
                    isItHere = 'X';
                }
                
                stringster = stringster + "[" + isItHere + "]";
            }
            stringster = stringster + "\n";
        }
        stringster = stringster + "\n";
        
        stringster = stringster + "Bump map: " + bump.getName() + "\n";
        stringster = stringster + "Specular map: " + specular.getName() + "\n";
        stringster = stringster + "Texture map: " + texture.getName() + "\n\n";
        
        stringster = stringster + "Shift image: " + shift.getName() + "\n";
        stringster = stringster + "Width image: " + width.getName() + "\n";
        stringster = stringster + "Height image: " + height.getName() + "\n\n";
        
        stringster = stringster + "Displacement range: from "
                + displacementRange[0].getName() + " to "
                + displacementRange[1].getName();
        
        return stringster;
    }
}
