package graphics;

import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
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
    final double BRIGHTNESS_MULTIPLIER = 100;
    
    // The number of Individuals that this Population consists of
    int size;
    
    // A multiplier of how much the vertices on an Individual are to be
    // displaced
    int displacementStrength;
    
    // The width and height of each Individual (measured in the number of
    // vertices)
    int vertexWidth;
    int vertexHeight;
    
    // A matrix for each vertex on the Terrain. The array stores whether or not
    // an Individual is to appear over a vertex.
    boolean locations[][];
    
    // The maps for each Individual
    Image bump;
    Image specular;
    Image texture;
    
    // A grayscale image for the probability of an Individual being created on a
    // specific vertex on the Terrain
    Image placement;
    // A grayscale image for determining how high or low each Individual should
    // be raised or lowered
    Image elevation;
    
    // Grayscale images for determining the width and height of each Individual
    Image width;
    Image height;
    
    // Pixel readers for the images
    PixelReader placementPixels;
    PixelReader elevationPixels;
    
    PixelReader widthPixels;
    PixelReader heightPixels;
    
    // 2 displacement maps for determining the range to which each Individual
    // will be displaced
    Image displacementRange[];
    
    // Pixel readers for the 2 displacement maps
    PixelReader displacementPixels[];
    
    // Each individual this population consists of
    Individual individuals[];
    
    /**
     * CONSTRUCTOR
     * 
     * @param strength The strength of the displacement map
     * @param vertWidth The width of each Individual (Measured in vertices)
     * @param vertHeight The height of each Individual (Measured in vertices)
     * @param bumpster The bump map for each Individual
     * @param evster A grayscale image for determining how high or low each
     *               Individual should be raised or lowered
     * @param placementster A grayscale image for the probability of an
     *                      Individual being created on a specific vertex on the
     *                      Terrain
     * @param specster The specular map for each Individual
     * @param texster The texture for each Individual
     * @param widthster A grayscale image for determining the width of each
     *                  Individual
     * @param heightster A grayscale image for determining the height of each
     *                   Individual
     * @param displacement1 The 1st displacement map for determining the range
     *                      to which each Individual will be displaced
     * @param displacement2 The 2nd displacement map for determining the range
     *                      to which each Individual will be displaced
     */
    public Population(int strength, int vertWidth, int vertHeight,
            Image bumpster, Image evster, Image placementster, Image specster,
            Image texster, Image widthster, Image heightster,
            Image displacement1, Image displacement2)
    {
        size = 0;
        
        displacementStrength = strength;
        
        vertexWidth = vertWidth;
        vertexHeight = vertHeight;
        
        locations = new boolean[0][0];
        
        bump = bumpster;
        specular = specster;
        texture = texster;
        
        elevation = evster;
        placement = placementster;
        
        width = widthster;
        height = heightster;
        
        elevationPixels = evster.getPixelReader();
        placementPixels = evster.getPixelReader();
        
        widthPixels = evster.getPixelReader();
        heightPixels = evster.getPixelReader();
        
        displacementRange = new Image[2];
        displacementRange[0] = displacement1;
        displacementRange[1] = displacement2;
        
        displacementPixels = new PixelReader[2];
        displacementPixels[0] = displacement1.getPixelReader();
        displacementPixels[1] = displacement2.getPixelReader();
    }
    
    /**
     * Calculates which vertices on the terrain should have an Individual on it
     * 
     * @param terrainFaceSize The width of the terrain (Measured in vertices)
     * @param terrainHeight The height of the terrain (Measured in vertices)
     */
    private void calculateLocations(int terrainWidth, int terrainHeight)
    {
        // The dimensions of this array must match the terrain dimensions to
        // ensure that there is 1 boolean for each vertex on the terrain
        locations = new boolean[terrainWidth][terrainHeight];
        
        size = 0;
        
        // For each row of the terrain's vertices...
        for (int i = 0; i < terrainWidth; i++)
        {
            // ...and for each column of the terrain's vertices...
            for (int j = 0; j < terrainHeight; j++)
            {
                // ...get the shade of the corresponding pixel on the placement
                // image.
                Color pixelColor = getPixelColor(terrainWidth, terrainHeight, i,
                        j, placement, placementPixels);
                
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
     * @param z The z position of the vertex on the terrain to which the
     *          Individual will be placed
     */
    private void createIndividual(int faceWidth, int faceHeight, int evster,
            double x, double y, double z)
    {
        Image displacement = generateDisplacement();
        
        Individual newIndividual = new Individual(vertexWidth, vertexHeight,
                faceWidth, faceHeight, displacementStrength, evster, x, y, z,
                displacement);
        
        // Create a new array with room for the additional element
        Individual newIndividuals[] = new Individual[individuals.length + 1];
        
        // Copy the Individuals to the new array
        System.arraycopy(individuals, 0, newIndividuals, 0, individuals.length);
        
        // Add the new Individual to the new array
        newIndividuals[newIndividuals.length] = newIndividual;
        
        // Make the old array into the new array
        individuals = newIndividuals;
    }
    
    /**
     * (Re)creates all of the Individuals for this population
     * 
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param terrainHeight The height of the terrain (measured in vertices)
     * @param terrainPoints The positions of each vertex in the terrain
     */
    private void createIndividuals(int terrainWidth, int terrainHeight,
            double[] terrainPoints)
    {
        // For each row of vertices on the terrain...
        for (int i = 0; i < terrainWidth; i++)
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
                    Color elevationColor = getPixelColor(terrainWidth,
                            terrainHeight, i, j, elevation, elevationPixels);
                    
                    Color widthColor = getPixelColor(terrainWidth,
                            terrainHeight, i, j, width, widthPixels);
                    Color heightColor = getPixelColor(terrainWidth,
                            terrainHeight, i, j, height, heightPixels);
                    
                    evster = getBrightness(elevationColor);
                    
                    widthster = getBrightness(widthColor);
                    heightster = getBrightness(heightColor);
                    
                    createIndividual(widthster, heightster, evster, x, y, z);
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
                
                // The 2 colors representing the smallest and largest amount of
                // red, green and blue for how the vertex is to be displaced
                Color pixelColors[] = new Color[2];
                
                pixelColors[0] = getPixelColor(vertexWidth, vertexHeight, i, j,
                        displacementRange[0], displacementPixels[0]);
                pixelColors[1] = getPixelColor(vertexWidth, vertexHeight, i, j,
                        displacementRange[1], displacementPixels[1]);
                
                randomColor = getRandomColor(pixelColors);
                
                writster.setColor(i, j, randomColor);
            }
        }
        
        return newDisplacement;
    }
    
    /**
     * Gets a number from -50 to 50, depending upon the brightness of the given
     * color
     * 
     * @return A number from -50 to 50
     */
    private int getBrightness(Color shade)
    {
        // Gets a number of the range 0-100
        int brightness = (int)(shade.getBrightness() * BRIGHTNESS_MULTIPLIER);
        
        brightness = brightness - (int)(BRIGHTNESS_MULTIPLIER / 2);
        
        return brightness;
    }
    
    /**
     * Gets the distance that should exist between 2 non-displaced vertices
     * 
     * @param length The distance between one end of the mesh and the other
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
     * Gets the pixel color in an image that belongs to a vertex on the terrain
     * 
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param terrainHeight The height of the terrain (measured in vertices)
     * @param positionX The X position of the pixel that the color is to be
     *                  retrieved from
     * @param positionY The Y position of the pixel that the color is to be
     *                  retrieved from
     * @param imster The image to retrieve a pixel's color from
     * @param pixels The PixelReader of the image containing the color to be
     *               obtained
     * 
     * @return The color of the pixel to be retrieved
     */
    private Color getPixelColor(int terrainWidth, int terrainHeight,
            int positionX, int positionY, Image imster, PixelReader pixels)
    {
        int distanceX = getDistanceBetweenVertices(terrainWidth, imster.getWidth());
        int distanceY = getDistanceBetweenVertices(terrainHeight, imster.getHeight());
        
        int pixelX = getPixelPosition(distanceX, positionX);
        int pixelY = getPixelPosition(distanceY, positionY);
        
        Color colster = pixels.getColor(pixelX, pixelY);
        
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
        
        // A random value between -50 and 50
        randomValue = (randomValue * BRIGHTNESS_MULTIPLIER)
                - (BRIGHTNESS_MULTIPLIER / 2);
        
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
     * Prepares the population
     * 
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param terrainHeight The height of the terrain (measured in vertices)
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void load(int terrainWidth, int terrainHeight,
            double[] terrainPoints)
    {
        calculateLocations(terrainWidth, terrainHeight);
                
        createIndividuals(terrainWidth, terrainHeight, 
                terrainPoints);
    }
}
