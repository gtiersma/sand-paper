package graphics;

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
    // A value used in calculations to get the correct value for the brightness
    // of a pixel
    final double BRIGHTNESS_ADJUSTMENT = 100;
    
    final TextureObject DEFAULT_TEXTURE = new TextureObject();
    
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
    // A grayscale image for determining how high or low each Individual should
    // be raised or lowered
    TextureObject elevation;
    
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
        
        bump = DEFAULT_TEXTURE;
        specular = DEFAULT_TEXTURE;
        texture = DEFAULT_TEXTURE;
        
        elevation = DEFAULT_TEXTURE;
        placement = DEFAULT_TEXTURE;
        
        width = DEFAULT_TEXTURE;
        height = DEFAULT_TEXTURE;
        
        displacementRange = new TextureObject[2];
        displacementRange[0] = DEFAULT_TEXTURE;
        displacementRange[1] = DEFAULT_TEXTURE;
        
        individuals = new Individual[0];
    }
    
    /**
     * Calculates which vertices on the terrain should have an Individual on it
     */
    private void calculateLocations()
    {
        size = 0;
        
        // For each row of the terrain's vertices...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of the terrain's vertices...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...get the shade of the corresponding pixel on the placement
                // image.
                Color pixelColor = getPixelColor(true, i, j, placement);
                
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
     * @param evster How much the Individual should be raised or lowered over
     *               the terrain
     * @param x The x position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param y The y position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param z The z position of the vertex on the terrain to which the
     *          Individual will be placed
     * @param xRotate The currently set vertical rotation value
     * @param yRotate The currently set horizontal rotation value
     */
    private void createIndividual(int faceWidth, int faceHeight, int evster,
            double x, double y, double z, double xRotate, double yRotate)
    {
        Image displacement = generateDisplacement();
        
        Individual newIndividual = new Individual(vertexWidth, vertexHeight,
                faceWidth, faceHeight, displacementStrength, evster, x, y, z,
                xRotate, yRotate, displacement);
        
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
        // For each row of vertices on the terrain...
        for (int i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (int j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // The elevation adjustment for the Individual
                    int evster;
                    
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
                    Color elevationColor = getPixelColor(true, i, j, elevation);
                    Color widthColor = getPixelColor(true, i, j, width);
                    Color heightColor = getPixelColor(true, i, j, height);
                    
                    evster = getBrightness(true, elevationColor);
                    // The values returned from these functions is too large
                    // for the width and height, so it is divided in half
                    widthster = getBrightness(false, widthColor) / 2;
                    heightster = getBrightness(false, heightColor) / 2;
                    
                    createIndividual(widthster, heightster, evster, x, y, z,
                            xRotate, yRotate);
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
     * Gets a random number depending upon the brightness of the given color
     * 
     * @param negativeRange Whether or not the number being returned should have
     * the possibility of being negative. If this is true, the range of the
     * number that will be returned will be from -50 - +50. If this is false,
     * the range of the number will be from 1 - 50.
     * @param shade The given color
     * 
     * @return A random number
     */
    private int getBrightness(boolean negativeRange, Color shade)
    {
        // Gets a number of the range 0-100
        int brightness = (int)(shade.getBrightness() * BRIGHTNESS_ADJUSTMENT);
        
        // Puts the value in the range of -50 - +50
        brightness = brightness - (int)(BRIGHTNESS_ADJUSTMENT / 2);
        
        // If the value should not be negative...
        if (!negativeRange)
        {
            // ...put the value in the range of 0 - +50.
            brightness = Math.abs(brightness);
        }
        
        return brightness;
    }
    
    /**
     * Gets the bump map for this Population
     * 
     * @return A TextureObject of this Population's bump map
     */
    public TextureObject getBumpMap()
    {
        return bump;
    }
    
    /**
     * Gets the currently set displacement strength for this Population
     * 
     * @return The currently set displacement strength for this Population
     */
    public int getDisplacementStrength()
    {
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
        return (int)(length / vertexAmount);
    }
    
    /**
     * Gets the elevation map assigned to this population
     * 
     * @return A TextureObject of the elevation map assigned to this population
     */
    public TextureObject getElevation()
    {
        return elevation;
    }
    
    /**
     * Gets the first displacement map used in the displacement range
     * 
     * @return A TextureObject of the first displacement map used in the
     *         displacement range
     */
    public TextureObject getFirstDisplacement()
    {
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
        return height;
    }
    
    /**
     * Gets the name of this population
     * 
     * @return The name of this population
     */
    public String getName()
    {
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
        return distance * vertexPosition;
    }
    
    /**
     * Gets the placement map
     * 
     * @return A TextureObject of the placement map
     */
    public TextureObject getPlacement()
    {
        return placement;
    }
    
    /**
     * Gets the population of individual meshes
     * 
     * @return the population
     */
    public Group getPopulation()
    {
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
        return displacementRange[1];
    }
    
    /**
     * Gets the specular map
     * 
     * @return A TextureObject of the specular map
     */
    public TextureObject getSpecularMap()
    {
        return specular;
    }
    
    /**
     * Gets the texture
     * 
     * @return A TextureObject of the texture
     */
    public TextureObject getTexture()
    {
        return texture;
    }
    
    /**
     * Gets the height of each Individual measured in vertices
     * 
     * @return The height of each Individual (Measured in vertices)
     */
    public int getVertexHeight()
    {
        return vertexHeight;
    }
    
    /**
     * Gets the width of each Individual measured in vertices
     * 
     * @return The width of each Individual (Measured in vertices)
     */
    public int getVertexWidth()
    {
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
        displacementStrength = strength;
        
        for (Individual inster : individuals)
        {
            inster.setDisplacementStrength(displacementStrength);
        }
    }
    
    /**
     * Sets the elevation map
     * 
     * @param elster The elevation map
     */
    public void setElevation(TextureObject elster)
    {
        elevation = elster;
        
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
                    Color elevationColor = getPixelColor(true, i, j, elevation);
                    
                    int evster = getBrightness(true, elevationColor);
                    
                    individuals[count].setElevation(evster);
                    
                    count++;
                }
            }
        }
    }
    
    /**
     * Sets the first displacement map to be used in the displacement range
     * 
     * @param dister A displacement map
     */
    public void setFirstDisplacement(TextureObject dister)
    {
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
                    
                    int heightBrightness = getBrightness(false, heightColor);
                    
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
        placement = placster;
        
        load(xRotate, yRotate, terrainPoints);
        
        setTexture(texture);
        setBumpMap(bump);
        setSpecularMap(specular);
    }
    
    /**
     * Rotates the Individuals in correspondence with the camera's vertical
     * rotation value
     * 
     * @param angle The camera's vertical rotation value
     */
    public void setRotationX(double angle)
    {
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
                    
                    int widthBrightness = getBrightness(false, widthColor);
                    
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
        
        stringster = stringster + "Elevation image: " + elevation.getName()
                + "\n";
        stringster = stringster + "Width image: " + width.getName() + "\n";
        stringster = stringster + "Height image: " + height.getName() + "\n\n";
        
        stringster = stringster + "Displacement range: from "
                + displacementRange[0].getName() + " to "
                + displacementRange[1].getName();
        
        return stringster;
    }
}
