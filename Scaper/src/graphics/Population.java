package graphics;

import controls.ProgressBarDialog;
import java.io.File;
import java.util.Random;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    private final int SIZE_DIVIDER = 3;
    
    // A value used in calculations to get the correct value for the brightness
    // of a pixel
    private final double COLOR_ADJUSTMENT = 100;
    
    // Default textures to use for when no maps have been selected by the user
    private final TextureObject GRAY_TEXTURE
            = new TextureObject(new File("src/graphics/gray.png"));
    private final TextureObject WHITE_TEXTURE = new TextureObject();
    
    // Whether or not the service is ready to be used
    private boolean servicePrepared;
    
    // The number of Individuals that this Population consists of
    private int size;
    
    // A multiplier of how much the vertices on an Individual are to be
    // displaced
    private int displacementStrength;
    
    // The width and height of each Individual (measured in the number of
    // vertices)
    private int vertexWidth;
    private int vertexHeight;
    
    private String name;
    
    // A matrix for each vertex on the Terrain. The array stores whether or not
    // an Individual is to appear over a vertex.
    private boolean locations[][];
    
    // The dialog to show the progress of the population's generation
    private ProgressBarDialog individualProgress;
    
    // The service used when creating Individuals
    private Service<Individual[]> individualService;
    
    // The maps for each Individual
    private TextureObject bump;
    private TextureObject specular;
    private TextureObject texture;
    
    // A grayscale image for the probability of an Individual being created on a
    // specific vertex on the Terrain
    private TextureObject placement;
    // An image for determining how much each Individual should be shifted on
    // the x (red), y (green) or z (blue) scale.
    private TextureObject shift;
    
    // Grayscale images for determining the width and height of each Individual
    private TextureObject width;
    private TextureObject height;
    
    // 2 displacement maps for determining the range to which each Individual
    // will be displaced
    private TextureObject displacementRange[];
    
    // Each individual this population consists of
    private Individual individuals[];
    
    /**
     * CONSTRUCTOR
     * 
     * @param strength The strength of the displacement map
     * @param terrainWidth The width of the terrain (Measured in vertices)
     * @param terrainDepth The depth of the terrain (Measured in vertices)
     * @param vertWidth The width of each Individual (Measured in vertices)
     * @param vertHeight The height of each Individual (Measured in vertices)
     * @param namster The name of the population
     */
    public Population(int strength, int terrainWidth, int terrainDepth,
            int vertWidth, int vertHeight, String namster)
    {
        servicePrepared = false;
        
        size = 0;
        
        displacementStrength = strength;
        
        vertexWidth = vertWidth;
        vertexHeight = vertHeight;
        
        name = namster;
        
        // The dimensions of this array must match the terrain dimensions to
        // ensure that there is 1 boolean for each vertex on the terrain
        locations = new boolean[terrainWidth][terrainDepth];
        
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
        // Get the spacing that should be between each UV point for the
        // placement map
        int horizontalSpacing = getUVSpacing(placement.getWidth(),
                locations.length);
        int verticalSpacing = getUVSpacing(placement.getHeight(),
                locations[0].length);
        
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
                
                // Get the shade of the corresponding pixel on the placement
                // image.
                Color pixelColor = getPixelColor(horizontalSpacing,
                        verticalSpacing, i, newJ, placement);
                
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
     * Performs the procedures to be used immediately after the service finishes
     */
    public void concludeService()
    {
        // Get the Individuals created from the service
        individuals = individualService.getValue();
        // Reset the service
        individualService.reset();
        // Close the progress dialog
        individualProgress.close();
        // The service is no longer ready to be used
        servicePrepared = false;
    }
    
    /**
     * Creates an Individual for this population
     * 
     * @param dStrength The displacement strength
     * @param locationX The x position (measured in vertices on the terrain) of
     *                  the terrain's vertex to which this Individual will be
     *                  placed adjacent to
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param locationY The y position (measured in vertices on the terrain) of
     *                  the terrain's vertex to which this Individual will be
     *                  placed adjacent to
     * @param xShiftSpace The distance between each pixel on the shift map to be
     *                    returned for an Individual on the image's x axis
     * @param yShiftSpace The distance between each pixel on the shift map to be
     *                    returned for an Individual on the image's y axis
     * @param xWidthSpace The distance between each pixel on the width map to be
     *                    returned for an Individual on the image's x axis
     * @param yWidthSpace The distance between each pixel on the width map to be
     *                    returned for an Individual on the image's y axis
     * @param xHeightSpace The distance between each pixel on the height map to
     *                     be returned for an Individual on the image's x axis
     * @param yHeightSpace The distance between each pixel on the height map to
     *                     be returned for an Individual on the image's y axis
     * @param vWidth The width of each Individual (measured in vertices)
     * @param vHeight The height of each Individual (measured in vertices)
     * @param faceWidth The width of each face on the Individual
     * @param faceHeight The height of each face on the Individual
     * @param xRotate The currently set vertical rotation value
     * @param yRotate The currently set horizontal rotation value
     * @param terrainPoints The array of point positions used in the creation of
     *                      the terrain
     * @param bumpster The bump map for this population
     * @param shiftster The shift map for this population
     * @param specster The specular map for this population
     * @param texster The texture for this population
     * @param widther The width map for this population
     * @param heightster The height map for this population
     * @param dRange The 2 displacement maps used to define the range to which
     *               a displacement map will be generated for this Individual
     */
    private Individual createIndividual(int dStrength, int locationX,
            int locationY, int terrainWidth, int xShiftSpace, int yShiftSpace,
            int xWidthSpace, int yWidthSpace, int xHeightSpace,
            int yHeightSpace, int vWidth, int vHeight, double xRotate,
            double yRotate, float[] terrainPoints, TextureObject bumpster,
            TextureObject shiftster, TextureObject specster,
            TextureObject texster, TextureObject widthster,
            TextureObject heightster, TextureObject[] dRange)
    {
        final int THREE_DIMENSIONS = 3;
        
        // The shift adjustments for the Individual
        int shiftX;
        int shiftY;
        int shiftZ;
                    
        // Calculates the index of the point on the terrain to which
        // this Individual is to be created upon
        int pointIndex = ((locationX * terrainWidth) + locationY)
                * THREE_DIMENSIONS;
                    
        // The width and height multipliers to determine the size of
        // the Individual's faces
        int faceWidth;
        int faceHeight;
                    
        // The position of the point on the terrain to which this
        // new Individual will belong
        double x = terrainPoints[pointIndex];
        double y = terrainPoints[pointIndex + 1];
        double z = terrainPoints[pointIndex + 2];
                    
        // Get the correct pixel colors for this Individual
        Color shiftColor = getPixelColor(xShiftSpace, yShiftSpace, locationX,
                locationY, shiftster);
        Color widthColor = getPixelColor(xWidthSpace, yWidthSpace, locationX,
                locationY, widthster);
        Color heightColor = getPixelColor(xHeightSpace, yHeightSpace, locationX,
                locationY, heightster);
                    
        Individual newIndividual;
        
        // The values used to determine how far the Individual is to be shifted
        // from its default position
        shiftX = getColorValue(true, 'r', shiftColor);
        shiftY = getColorValue(true, 'g', shiftColor);
        shiftZ = getColorValue(true, 'b', shiftColor);
                    
        // The values returned from these functions are too large
        // for the width and height, so it is divided to a smaller
        // value
        faceWidth = getColorValue(false, ' ', widthColor) / SIZE_DIVIDER;
        faceHeight = getColorValue(false, ' ', heightColor) / SIZE_DIVIDER;
                            
        Image displacement = generateDisplacement(vWidth, vHeight, dRange);
        
        newIndividual = new Individual(vWidth, vHeight, faceWidth, faceHeight,
                dStrength, shiftX, shiftY, shiftZ, x, y, z, xRotate, yRotate,
                displacement);
        
        newIndividual.load();
        
        newIndividual.setTexture(texster.getImage());
        newIndividual.setBump(bumpster.getImage());
        newIndividual.setSpecular(specster.getImage());
        
        return newIndividual;
    }
    
    /**
     * (Re)creates all of the Individuals for this population
     * 
     * @param xRotate The currently set vertical rotation value
     * @param yRotate The currently set horizontal rotation value
     * @param terrainPoints The positions of each vertex in the terrain
     */
    private void createIndividuals(double xRotate, double yRotate,
            float[] terrainPoints)
    {
        // Constants of global variables. These are used in the service instead
        // of the original variables to avoid the possibility their values from
        // being changed by the outside thread while still in use by the
        // service.
        final int DISPLACEMENT_STRENGTH = displacementStrength;
        
        final int SIZE = size;
        
        final int TERRAIN_WIDTH = locations.length;
        final int TERRAIN_HEIGHT = locations[0].length;
        
        final int VERTEX_WIDTH = vertexWidth;
        final int VERTEX_HEIGHT = vertexHeight;
        
        // The distance between each pixel on a map being retrieved for an
        // Individual (measured in pixels)
        final int X_SHIFT_SPACE = getUVSpacing(shift.getWidth(), TERRAIN_WIDTH);
        final int Y_SHIFT_SPACE = getUVSpacing(shift.getHeight(),
                TERRAIN_HEIGHT);
        final int X_WIDTH_SPACE = getUVSpacing(width.getWidth(), TERRAIN_WIDTH);
        final int Y_WIDTH_SPACE = getUVSpacing(width.getHeight(),
                TERRAIN_HEIGHT);
        final int X_HEIGHT_SPACE = getUVSpacing(height.getWidth(),
                TERRAIN_WIDTH);
        final int Y_HEIGHT_SPACE = getUVSpacing(height.getHeight(),
                TERRAIN_HEIGHT);
        
        final TextureObject BUMP = bump;
        final TextureObject SHIFT = shift;
        final TextureObject SPECULAR = specular;
        final TextureObject TEXTURE = texture;
        final TextureObject WIDTH = width;
        final TextureObject HEIGHT = height;
        
        final TextureObject[] DISPLACEMENT_RANGE = displacementRange;
        
        final boolean[][] LOCATIONS = locations;
        
        individualService = new Service<Individual[]>()
        {
            @Override
            protected Task<Individual[]> createTask()
            {
                return new Task<Individual[]>()
                {
                    @Override
                    protected Individual[] call()
                    {
                        // Counter variable assigning newly created Individuals
                        // to the array
                        int currentIndex = 0;
                
                        // Used for keeping track of progress for the progress
                        // bar
                        int done = TERRAIN_WIDTH * TERRAIN_HEIGHT;
                        int progress = 0;
                
                        Individual[] newIndividuals = new Individual[SIZE];
                
                        // For each row of vertices on the terrain...
                        for (int i = 0; i < TERRAIN_WIDTH; i++)
                        {
                            // ...and for each column of vertices on the terrain...
                            for (int j = 0; j < TERRAIN_HEIGHT; j++)
                            {
                                // ...if an Individual is to be created there...
                                if (LOCATIONS[i][j])
                                {
                                    // ...create a new Individual.
                                    Individual newIndividual =
                                            createIndividual(
                                                    DISPLACEMENT_STRENGTH, j, i,
                                                    TERRAIN_WIDTH,
                                                    X_SHIFT_SPACE,
                                                    Y_SHIFT_SPACE,
                                                    X_WIDTH_SPACE,
                                                    Y_WIDTH_SPACE,
                                                    X_HEIGHT_SPACE,
                                                    Y_HEIGHT_SPACE,
                                                    VERTEX_WIDTH, VERTEX_HEIGHT,
                                                    xRotate, yRotate,
                                                    terrainPoints, BUMP, SHIFT,
                                                    SPECULAR, TEXTURE, WIDTH,
                                                    HEIGHT, DISPLACEMENT_RANGE);
        
                                    // Add the new Individual to the new array
                                    newIndividuals[currentIndex] =
                                            newIndividual;
                            
                                    currentIndex++;
                                }
                        
                                updateProgress(progress, done);
                                progress++;
                            }
                        }
                
                        return newIndividuals;
                    }
                };
            }
        };
        
        startService("Positioning Population");
    }
    
    /**
     * Generates a displacement map with pixels within the range of the 2
     * displacement maps using this Population's global variables
     * 
     * @return A displacement map with pixels within the range of the 2
     * displacement maps
     */
    private Image generateDisplacement()
    {
        return generateDisplacement(vertexWidth, vertexHeight,
                displacementRange);
    }
    
    /**
     * Generates a displacement map with pixels within the range of the 2
     * displacement maps using the provided parameters
     * 
     * @return A displacement map with pixels within the range of the 2
     * displacement maps
     */
    private Image generateDisplacement(int vWidth, int vHeight,
            TextureObject[] range)
    {
        // Get the spacing that should be between each UV point for the
        // placement map
        int[] widthDisplacementSpacings = new int[2];
        int[] heightDisplacementSpacings = new int[2];
        
        widthDisplacementSpacings[0]
                = getUVSpacing(range[0].getWidth(), vWidth);
        heightDisplacementSpacings[0]
                = getUVSpacing(range[0].getHeight(), vHeight);
        widthDisplacementSpacings[1]
                = getUVSpacing(range[1].getWidth(), vWidth);
        heightDisplacementSpacings[1]
                = getUVSpacing(range[1].getHeight(), vHeight);
        
        // The generated map
        WritableImage newDisplacement = new WritableImage(vWidth,
                vHeight);
        PixelWriter writster = newDisplacement.getPixelWriter();
        
        // For each row of vertices on the Individual...
        for (int i = 0; i < vWidth; i++)
        {
            // ...and for each column of vertices...
            for (int j = 0; j < vHeight; j++)
            {
                // Color for how the vertex is to be displaced
                Color randomColor;
                
                // The 2 colors representing the smallest and largest possible
                // values of red, green and blue for the randomly generated
                // pixel color
                Color pixelColors[] = new Color[2];
                
                pixelColors[0] = getPixelColor(widthDisplacementSpacings[0],
                        heightDisplacementSpacings[0], i, j,
                        range[0]);
                pixelColors[1] = getPixelColor(widthDisplacementSpacings[1],
                        heightDisplacementSpacings[1], i, j,
                        range[1]);
                
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
        return displacementStrength;
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
    private Color getPixelColor(int distanceX, int distanceY, int positionX,
            int positionY, TextureObject texster)
    {
        int pixelX = distanceX * positionX;
        int pixelY = distanceY * positionY;
        
        Color colster = texster.getColor(pixelX, pixelY);
        
        return colster;
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
     * Gets this population's service
     * 
     * @return The service
     */
    public Service getService()
    {
        return individualService;
    }
    
    /**
     * Gets the shift map assigned to this population
     * 
     * @return A TextureObject of the shift map assigned to this population
     */
    public TextureObject getShift()
    {
        return shift;
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
     * Gets the number of pixels that should separate 2 vertices in a UV map
     * 
     * @param length The number of pixels long that the image is
     * @param vertexAmount The number of vertices that exist between one end of
     *                     the mesh and the other
     * 
     * @return The distance that should exist between 2 non-displaced vertices
     */
    private int getUVSpacing(double length, double vertexAmount)
    {
        return (int)(length / vertexAmount);
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
     * Gets whether or not the service is ready for use
     * 
     * @return Whether or not the service is ready for use
     */
    public boolean isServicePrepared()
    {
        return servicePrepared;
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
        
        // Get the spacing that should be between each UV point for the
        // height map
        int horizontalUVSpacing = getUVSpacing(height.getWidth(),
                locations.length);
        int verticalUVSpacing = getUVSpacing(height.getHeight(),
                locations[0].length);
        
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
                    Color heightColor = getPixelColor(horizontalUVSpacing,
                            verticalUVSpacing, i, j, height);
                    
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
        shift = shiftster;
        
        // Get the spacing that should be between each UV point for the shift
        // map
        int horizontalUVSpacing = getUVSpacing(shift.getWidth(),
                locations.length);
        int verticalUVSpacing = getUVSpacing(shift.getHeight(),
                locations.length);
        
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
                    Color shiftColor = getPixelColor(horizontalUVSpacing,
                            verticalUVSpacing, i, j, shift);
                    
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
     * Sets the height of the Individuals. Accepts a string parameter.
     * 
     * @param heightster The height of the Individuals (Measured in vertices)
     */
    public void setVertexHeight(String heightster)
    {
        int heightHeight = Integer.parseInt(heightster);
        
        setVertexWidth(heightHeight);
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
     * @param widthster The width of the Individuals (Measured in vertices)
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
     * Sets the width of the Individuals. Accepts a string parameter.
     * 
     * @param widthster The width of the Individuals (Measured in vertices)
     */
    public void setVertexWidth(String widthster)
    {
        int widthWidth = Integer.parseInt(widthster);
        
        setVertexWidth(widthWidth);
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
        
        // Constants for use in the service
        final TextureObject WIDTH = width;
        
        // Get the spacing that should be between each UV point for the width
        // map
        final int X_SPACING = getUVSpacing(width.getWidth(), locations.length);
        final int Y_SPACING = getUVSpacing(width.getHeight(), locations.length);
        
        final boolean[][] LOCATIONS = locations;
        
        Individual[] newIndividuals = individuals;
        
        individualService = new Service<Individual[]>()
        {
            @Override
            protected Task<Individual[]> createTask()
            {
                return new Task<Individual[]>()
                {
                    @Override
                    protected Individual[] call()
                    {
                        // Used for keeping track of progress for the progress
                        // bar
                        int done = size;
                        int progress = 0;
        
                        // For each row of vertices on the terrain...
                        for (int i = 0; i < LOCATIONS.length; i++)
                        {
                            // ...and for each column of vertices on the
                            // terrain...
                            for (int j = 0; j < LOCATIONS[i].length; j++)
                            {
                                // ...if an Individual is to be created there...
                                if (LOCATIONS[i][j])
                                {
                                    // Get the correct pixel color for this
                                    // Individual
                                    Color widthColor = getPixelColor(X_SPACING,
                                            Y_SPACING, i, j, WIDTH);
                    
                                    int widthBrightness = getColorValue(false,
                                            ' ', widthColor);
                                    
                                    widthBrightness = widthBrightness /
                                            SIZE_DIVIDER;
                    
                                    newIndividuals[progress].setFaceWidth(
                                            widthBrightness);
                                    
                                    updateProgress(progress, done);
                                    progress++;
                                }
                            }
                        }
                        
                        return newIndividuals;
                    }
                };
            }
        };
        
        startService("Setting Population Width");
    }
    
    /**
     * Runs this population's service (if not already running)
     * 
     * @param action A short description of the work that the service is
     *               currently doing. It is used as the progress dialog's
     *               title.
     */
    private void startService(String action)
    {
        // The service is now ready for use
        servicePrepared = true;
        
        // Create a progress dialog and show it
        individualProgress = new ProgressBarDialog(action, individualService);
        individualProgress.show();
        
        // As long as the service is not already running...
        if (!individualService.isRunning())
        {
            // ...start it.
            individualService.start();
        }
    }
    
    /**
     * Re-adjusts the population for when the terrain's depth is changed
     * 
     * @param terrainDepth The new depth of the terrain (measured in vertices)
     * @param xRotate The camera's set vertical rotation
     * @param yRotate The camera's set horizontal rotation
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void updateForTerrainDepthChange(int terrainDepth, double xRotate,
            double yRotate, float[] terrainPoints)
    {
        locations = new boolean[locations.length][terrainDepth];
        
        load(xRotate, yRotate, terrainPoints);
    }
    
    /**
     * Re-adjusts the population for when the terrain's width is changed
     * 
     * @param terrainWidth The new width of the terrain (measured in vertices)
     * @param xRotate The camera's set vertical rotation
     * @param yRotate The camera's set horizontal rotation
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void updateForTerrainWidthChange(int terrainWidth, double xRotate,
            double yRotate, float[] terrainPoints)
    {
        locations = new boolean[terrainWidth][locations[0].length];
        
        load(xRotate, yRotate, terrainPoints);
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
