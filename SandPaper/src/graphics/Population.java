package graphics;

import generics.DeepCloner;
import generics.ProgressBarDialog;
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
    // A value used in calculations to get the correct value for the brightness
    // of a pixel
    private final byte COLOR_ADJUSTMENT = 100;
    private final byte THREE_DIMENSIONS = 3;
    
    // Divides the width and height values to keep Individuals from being too
    // large
    private final short SIZE_DIVIDER = 3;
    
    // Default textures to use for when no maps have been selected by the user
    private final TextureObject GRAY_TEXTURE
            = new TextureObject(new File("src/graphics/unassignedGray.png"));
    private final TextureObject WHITE_TEXTURE = new TextureObject();
    
    // Whether or not the service is ready to be used
    private boolean servicePrepared;
    
    // The rotation values that Individuals will use to calculate their Rotate-
    // related values
    private short baseRotateX;
    private short baseRotateY;
    
    // The width and height of each Individual (measured in the number of
    // vertices)
    private short vertexWidth;
    private short vertexHeight;
    
    // The number of Individuals that this Population consists of
    private int size;
    
    // A multiplier of how much the vertices on an Individual are to be
    // displaced
    private int displacementStrength;
    
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
    private TextureObject diffuse;
    private TextureObject specular;
    
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
     * @param rotateX The rotation value that Individuals will use to calculate
     *                their Rotate-related values on the x axis
     * @param rotateY The rotation value that Individuals will use to calculate
     *                their Rotate-related values on the y axis
     * @param terrainWidth The width of the terrain (Measured in vertices)
     * @param terrainDepth The depth of the terrain (Measured in vertices)
     * @param vertWidth The width of each Individual (Measured in vertices)
     * @param vertHeight The height of each Individual (Measured in vertices)
     * @param strength The strength of the displacement map
     * @param namster The name of the population
     */
    public Population(short rotateX, short rotateY, short terrainWidth,
            short terrainDepth, short vertWidth, short vertHeight, int strength, 
            String namster)
    {
        servicePrepared = false;
        
        size = 0;
        
        displacementStrength = strength;
        
        vertexWidth = vertWidth;
        vertexHeight = vertHeight;
        
        baseRotateX = rotateX;
        baseRotateY = rotateY;
        
        name = namster;
        
        // The dimensions of this array must match the terrain dimensions to
        // ensure that there is 1 boolean for each vertex on the terrain
        locations = new boolean[terrainWidth][terrainDepth];
        
        bump = WHITE_TEXTURE;
        specular = WHITE_TEXTURE;
        diffuse = WHITE_TEXTURE;
        
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
        double horizontalSpacing = getUVSpacing(placement.getWidth(),
                locations.length);
        double verticalSpacing = getUVSpacing(placement.getHeight(),
                locations[0].length);
        
        size = 0;
        
        // For each row of the terrain's vertices...
        for (short i = 0; i < locations.length; i++)
        {
            // ...and for each column of the terrain's vertices...
            for (short j = 0; j < locations[i].length; j++)
            {
                // This is the inverse of the j variable (so it will refer to
                // the pixel or Individual at the opposite end). It must be used
                // with getting the pixel color instead of the regular j
                // variable to prevent the population from having a mirrored
                // position on the terrain.
                int newJ = locations[i].length - j - 1;
                
                // Get the shade of the corresponding pixel on the placement
                // image.
                Color pixelColor = getPixelColor(i, newJ, horizontalSpacing,
                        verticalSpacing, placement);
                
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
        // The service is no longer ready to be used
        servicePrepared = false;
        // Reset the service
        individualService.reset();
        
        // Close the progress dialog
        individualProgress.close();
    }
    
    /**
     * Creates an Individual for this population
     * 
     * @param locationX The x position (measured in vertices on the terrain) of
     *                  the terrain's vertex to which this Individual will be
     *                  placed adjacent to
     * @param locationY The y position (measured in vertices on the terrain) of
     *                  the terrain's vertex to which this Individual will be
     *                  placed adjacent to
     * @param terrainWidth The width of the terrain (measured in vertices)
     * @param vWidth The width of each Individual (measured in vertices)
     * @param vHeight The height of each Individual (measured in vertices)
     * @param xRotate The currently set base vertical rotation value
     * @param yRotate The currently set base horizontal rotation value
     * @param dStrength The displacement strength
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
     * @param terrainPoints The array of point positions used in the creation of
     *                      the terrain
     * @param bumpster The bump map for this population
     * @param difster The diffuse map for this population
     * @param shiftster The shift map for this population
     * @param specster The specular map for this population
     * @param widther The width map for this population
     * @param heightster The height map for this population
     * @param dRange The 2 displacement maps used to define the range to which
     *               a displacement map will be generated for this Individual
     */
    private Individual createIndividual(short locationX, short locationY,
            short terrainWidth, short vWidth, short vHeight, short xRotate,
            short yRotate, int dStrength, double xShiftSpace,
            double yShiftSpace, double xWidthSpace, double yWidthSpace,
            double xHeightSpace, double yHeightSpace, float[] terrainPoints,
            TextureObject bumpster,  TextureObject difster,
            TextureObject shiftster, TextureObject specster,
            TextureObject widthster, TextureObject heightster,
            TextureObject[] dRange)
    {
        // The shift adjustments for the Individual
        byte shiftX;
        byte shiftY;
        byte shiftZ;
        
        // The width and height multipliers to determine the size of
        // the Individual's faces
        short faceWidth;
        short faceHeight;
             
        int pointIndex = getBasePointIndex(locationX, locationY, terrainWidth);
                    
        // The position of the point on the terrain to which this
        // new Individual will belong
        int x = (int)terrainPoints[pointIndex];
        int y = (int)terrainPoints[pointIndex + 1];
        int z = (int)terrainPoints[pointIndex + 2];
                    
        // Get the correct pixel colors for this Individual
        Color shiftColor = getPixelColor(locationX, locationY, xShiftSpace,
                yShiftSpace, shiftster);
        Color widthColor = getPixelColor(locationX, locationY, xWidthSpace,
                yWidthSpace, widthster);
        Color heightColor = getPixelColor(locationX, locationY, xHeightSpace,
                yHeightSpace, heightster);
        
        Individual newIndividual;
        
        // The values used to determine how far the Individual is to be shifted
        // from its default position
        shiftX = getColorValue(true, 'r', shiftColor);
        shiftY = getColorValue(true, 'g', shiftColor);
        shiftZ = getColorValue(true, 'b', shiftColor);
                    
        // The values returned from these functions are too large
        // for the width and height, so it is divided to a smaller
        // value
        faceWidth = (short)(getColorValue(false, ' ', widthColor)
                / SIZE_DIVIDER);
        faceHeight = (short)(getColorValue(false, ' ', heightColor)
                / SIZE_DIVIDER);
                            
        Image displacement = generateDisplacement(vWidth, vHeight, dRange);
        
        newIndividual = new Individual(shiftX, shiftY, shiftZ, faceWidth,
                faceHeight, vWidth, vHeight, xRotate, yRotate, dStrength, x, y,
                z, displacement);
        
        newIndividual.load();
        
        newIndividual.setDiffuse(difster.getImage());
        newIndividual.setBump(bumpster.getImage());
        newIndividual.setSpecular(specster.getImage());
        
        return newIndividual;
    }
    
    /**
     * (Re)creates all of the Individuals for this population
     * 
     * @param terrainPoints The positions of each vertex in the terrain
     */
    private void createIndividuals(String actionDescription,
            float[] terrainPoints)
    {
        DeepCloner cloner = new DeepCloner();
        
        // Constants of global variables. These are used in the service instead
        // of the original variables to avoid the possibility their values from
        // being changed by the outside thread while still in use by the
        // service.
        final short BASE_ROTATE_X = baseRotateX;
        final short BASE_ROTATE_Y = baseRotateY;
        
        final short TERRAIN_WIDTH = (short)locations.length;
        final short TERRAIN_DEPTH = (short)locations[0].length;
        
        final short VERTEX_WIDTH = vertexWidth;
        final short VERTEX_HEIGHT = vertexHeight;
        
        final int DISPLACEMENT_STRENGTH = displacementStrength;
        
        final int SIZE = size;
        
        // The distance between each pixel on a map being retrieved for an
        // Individual (measured in pixels)
        final double X_SHIFT_SPACE = getUVSpacing(shift.getWidth(),
                TERRAIN_WIDTH);
        final double Y_SHIFT_SPACE = getUVSpacing(shift.getHeight(),
                TERRAIN_DEPTH);
        final double X_WIDTH_SPACE = getUVSpacing(width.getWidth(),
                TERRAIN_WIDTH);
        final double Y_WIDTH_SPACE = getUVSpacing(width.getHeight(),
                TERRAIN_DEPTH);
        final double X_HEIGHT_SPACE = getUVSpacing(height.getWidth(),
                TERRAIN_WIDTH);
        final double Y_HEIGHT_SPACE = getUVSpacing(height.getHeight(),
                TERRAIN_DEPTH);
        
        final TextureObject BUMP = new TextureObject(bump.getFile());
        final TextureObject SHIFT = new TextureObject(shift.getFile());
        final TextureObject SPECULAR = new TextureObject(specular.getFile());
        final TextureObject DIFFUSE = new TextureObject(diffuse.getFile());
        final TextureObject WIDTH = new TextureObject(width.getFile());
        final TextureObject HEIGHT = new TextureObject(height.getFile());
        
        final float[] TERRAIN_POINTS = cloner.clone(terrainPoints);
        
        final TextureObject[] DISPLACEMENT_RANGE
                = cloner.clone(displacementRange);
        
        final boolean[][] LOCATIONS = cloner.clone(locations);
        
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
                        int done = TERRAIN_WIDTH * TERRAIN_DEPTH;
                        int progress = 0;
                
                        Individual[] newIndividuals = new Individual[SIZE];
                
                        // For each row of vertices on the terrain...
                        for (short i = 0; i < TERRAIN_WIDTH; i++)
                        {
                            // ...and for each column of vertices on the
                            // terrain...
                            for (short j = 0; j < TERRAIN_DEPTH; j++)
                            {
                                // ...if an Individual is to be created there...
                                if (LOCATIONS[i][j])
                                {
                                    // ...create a new Individual.
                                    Individual newIndividual =
                                            createIndividual(i, j,
                                                    TERRAIN_WIDTH, VERTEX_WIDTH,
                                                    VERTEX_HEIGHT,
                                                    BASE_ROTATE_X,
                                                    BASE_ROTATE_Y,
                                                    DISPLACEMENT_STRENGTH, 
                                                    X_SHIFT_SPACE,
                                                    Y_SHIFT_SPACE,
                                                    X_WIDTH_SPACE,
                                                    Y_WIDTH_SPACE,
                                                    X_HEIGHT_SPACE,
                                                    Y_HEIGHT_SPACE,
                                                    TERRAIN_POINTS, BUMP,
                                                    DIFFUSE, SHIFT, SPECULAR,
                                                    WIDTH, HEIGHT,
                                                    DISPLACEMENT_RANGE);
        
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
        
        startService(actionDescription);
    }
    
    /**
     * Generates a displacement map with pixels within the range of the 2
     * displacement maps using the provided parameters
     * 
     * @param vWidth The width of each Individual (measured in vertices)
     * @param vHeight The height of each Individual (measured in vertices)
     * @param range The 2 displacement maps that act as the range of values a
     *              new displacement map is generated from
     * 
     * @return A displacement map with pixels within the range of the 2
     *         displacement maps
     */
    private Image generateDisplacement(short vWidth, short vHeight,
            TextureObject[] range)
    {
        // Get the spacing that should be between each UV point for the
        // placement map
        double[] widthDisplacementSpacings = new double[2];
        double[] heightDisplacementSpacings = new double[2];
        
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
        for (byte i = 0; i < vWidth; i++)
        {
            // ...and for each column of vertices...
            for (byte j = 0; j < vHeight; j++)
            {
                // Color for how the vertex is to be displaced
                Color randomColor;
                
                // The 2 colors representing the smallest and largest possible
                // values of red, green and blue for the randomly generated
                // pixel color
                Color pixelColors[] = new Color[2];
                
                pixelColors[0] = getPixelColor(i, j,
                        widthDisplacementSpacings[0],
                        heightDisplacementSpacings[0], range[0]);
                pixelColors[1] = getPixelColor(i, j,
                        widthDisplacementSpacings[1],
                        heightDisplacementSpacings[1], range[1]);
                
                randomColor = getRandomColor(pixelColors);
                
                writster.setColor(i, j, randomColor);
            }
        }
        
        return newDisplacement;
    }
    
    /**
     * Gets the index of the point in the terrain's point array to which an
     * Individual's position will be based upon
     * 
     * @param locationX The column of vertices on the terrain that contains the
     *                  base point
     * @param locationY The row of vertices on the terrain that contains the
     *                  base point
     * @param terrainWidth The width of the terrain (measured in vertices)
     * 
     * @return The index in the terrain's point array of an Individual's base
     *         point
     */
    private int getBasePointIndex(short locationX, short locationY,
            short terrainWidth)
    {
        // The row of vertices in the terrain that the base point belongs to
        int row = locationY * terrainWidth;
        
        // The numbered vertex that is a base point if the terrain's vertices
        // are numbered left-to-right, up-to-down
        int position = row + locationX;
        
        // Factor in the fact that there are 3 vertices for each point
        int index = position * THREE_DIMENSIONS;
        
        return index;
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
    private byte getColorValue(boolean negativeRange, char channel,
            Color colster)
    {
        byte colorValue;
        
        switch (channel)
        {
            case 'r':
                colorValue = (byte)(colster.getRed() * COLOR_ADJUSTMENT);
                break;
            case 'g':
                colorValue = (byte)(colster.getGreen() * COLOR_ADJUSTMENT);
                break;
            case 'b':
                colorValue = (byte)(colster.getBlue() * COLOR_ADJUSTMENT);
                break;
            default:
                colorValue = (byte)(colster.getBrightness() * COLOR_ADJUSTMENT);
        }
        
        // If the value should not be negative...
        if (negativeRange)
        {
            // ...put the value in the range of -50 - +50
            colorValue = (byte)(colorValue - COLOR_ADJUSTMENT / 2);
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
     * @param distanceX The distance between each UV point on the image on the x
     *                  scale (Measured in pixels)
     * @param distanceY The distance between each UV point on the image on the y
     *                  scale (Measured in pixels)
     * @param positionX The X position of the pixel that the color is to be
     *                  retrieved from
     * @param positionY The Y position of the pixel that the color is to be
     *                  retrieved from
     * @param texster The image that the color is to be retrieved from
     * 
     * @return The color of the pixel to be retrieved
     */
    private Color getPixelColor(int positionX, int positionY, double distanceX,
            double distanceY, TextureObject texster)
    {
        int pixelX = (int)(distanceX * positionX);
        int pixelY = (int)(distanceY * positionY);
        
        // When casting the pixel x and y positions into an integer type,
        // there's a possibility that it may be rounded up to match the length
        // of the map, putting the index out-of-bounds. These if statements
        // ensure that does not happpen.
        if (pixelX == texster.getWidth())
        {
            pixelX--;
        }
        if (pixelY == texster.getHeight())
        {
            pixelY--;
        }
        
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
     * @return a Group of the population's meshes
     */
    public Group getMeshes()
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
     * Gets the diffuse map
     * 
     * @return A TextureObject of the diffuse map
     */
    public TextureObject getDiffuse()
    {
        return diffuse;
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
    private double getUVSpacing(double length, double vertexAmount)
    {
        return length / vertexAmount;
    }
    
    /**
     * Gets the height of each Individual measured in vertices
     * 
     * @return The height of each Individual (Measured in vertices)
     */
    public short getVertexHeight()
    {
        return vertexHeight;
    }
    
    /**
     * Gets the width of each Individual measured in vertices
     * 
     * @return The width of each Individual (Measured in vertices)
     */
    public short getVertexWidth()
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
        boolean brightEnough;
        
        double brightness = shade.getBrightness();
        
        double randomValue = getRandomNumber(0, 1);
        
        // Whether or not the color happens to be bright enough for the random
        // value
        brightEnough = brightness < randomValue;
        
        return brightEnough;
    }
    
    /**
     * Gets whether or not the service is ready for use (or in use)
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
     * @param actionDescription A description of the change being made to the
     *                          population. It is used as the progress dialog's
     *                          title.
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void load(String actionDescription, float[] terrainPoints)
    {
        // Remove all Individuals
        individuals = new Individual[0];
        
        calculateLocations();
                
        createIndividuals(actionDescription, terrainPoints);
    }
    
    /**
     * Re-loads this population. This method is used in place of the regular
     * load method to prevent an exception from occurring of a population of 0 
     * being modified.
     * 
     * @param actionDescription A description of the change being made to the
     *                          population. It is used as the progress dialog's
     *                          title.
     * @param terrainPoints The array of point positions used in the creation of
     *                      the terrain
     */
    private void reload(String actionDescription, float[] terrainPoints)
    {
        // As long as an Individual exists...
        if (individuals.length > 0)
        {
            // ...load the Population.
            load(actionDescription, terrainPoints);
        }
    }
    
    /**
     * Positions the Individuals based upon the terrain's vertex positions
     * provided
     * 
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void reposition(float[] terrainPoints)
    {
        // The length of the array that maps out the Individual's position is
        // equal to the terrain's width
        short terrainWidth = (short)locations.length;
        
        int index = 0;
        
        // For each row of vertices on the terrain...
        for (short i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (short j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // ...get the index of the coordinates for the vertex for
                    // the current Individual.
                    int xIndex = getBasePointIndex(i, j, terrainWidth);
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
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     */
    public void setDisplacementStrength(int strength, float[] terrainPoints)
    {
        String actionDescription = "Setting Population Displacement";
        
        displacementStrength = strength;
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Sets the first displacement map to be used in the displacement range
     * 
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     * @param dister A displacement map
     */
    public void setFirstDisplacement(float[] terrainPoints,
            TextureObject dister)
    {
        String actionDescription = "Setting Population Displacement";
        
        displacementRange[0] = dister;
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Sets the map used to determine the height of the Individuals in this
     * population
     * 
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     * @param heightster A map used to determine the height of the Individuals
     *                   in this population
     */
    public void setHeight(float[] terrainPoints, TextureObject heightster)
    {
        String actionDescription = "Setting Population Height";
        
        height = heightster;
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Sets the placement map
     * 
     * @param terrainPoints The array of coordinates used to position the points
     *                      in the terrain's MeshView
     * @param placster The placement map
     */
    public void setPlacement(float terrainPoints[], TextureObject placster)
    {
        String actionDescription = "Positioning Population";
        
        placement = placster;
        
        load(actionDescription, terrainPoints);
        
        setBumpMap(bump);
        setDiffuse(diffuse);
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
        double horizontalUVSpacing = getUVSpacing(shift.getWidth(),
                locations.length);
        double verticalUVSpacing = getUVSpacing(shift.getHeight(),
                locations[0].length);
        
        int count = 0;
        
        // For each row of vertices on the terrain...
        for (short i = 0; i < locations.length; i++)
        {
            // ...and for each column of vertices on the terrain...
            for (short j = 0; j < locations[i].length; j++)
            {
                // ...if an Individual is to be created there...
                if (locations[i][j])
                {
                    // ...get the correct pixel color for this Individual
                    Color shiftColor = getPixelColor(i, j, horizontalUVSpacing,
                            verticalUVSpacing, shift);
                    
                    // Get the correct shift amounts for this color
                    byte shiftX = getColorValue(true, 'r', shiftColor);
                    byte shiftY = getColorValue(true, 'g', shiftColor);
                    byte shiftZ = getColorValue(true, 'b', shiftColor);
                    
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
    public void setRotationX(short angle)
    {
        baseRotateX = angle;
        
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
    public void setRotationY(short angle)
    {
        baseRotateY = angle;
        
        for (Individual individual : individuals)
        {
            individual.setRotationY(angle);
        }
    }
    
    /**
     * Sets the second displacement map to be used in the displacement range
     * 
     * @param dister A displacement map
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     */
    public void setSecondDisplacement(TextureObject dister,
            float[] terrainPoints)
    {
        String actionDescription = "Setting Population Displacement";
        
        displacementRange[1] = dister;
        
        reload(actionDescription, terrainPoints);
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
     * Sets the diffuse map
     * 
     * @param difster A diffuse map
     */
    public void setDiffuse(TextureObject difster)
    {
        diffuse = difster;
        
        for (Individual individual : individuals)
        {
            individual.setDiffuse(diffuse.getImage());
        }
    }
    
    /**
     * Sets the height of the Individuals
     * 
     * @param heightster the height of the Individuals (Measured in vertices)
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     */
    public void setVertexHeight(short heightster, float[] terrainPoints)
    {
        String actionDescription = "Changing Population Vertex Height";
        
        vertexHeight = heightster;
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Sets the width of the Individuals
     * 
     * @param widthster The width of the Individuals (Measured in vertices)
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     */
    public void setVertexWidth(short widthster, float[] terrainPoints)
    {
        String actionDescription = "Changing Population Vertex Width";
        
        vertexWidth = widthster;
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Sets the map used to determine the width of the Individuals in this
     * population
     * 
     * @param widthster A map used to determine the width of the Individuals in
     *                  this population
     * @param terrainPoints The point positions used in the creation of the
     *                      terrain's MeshView
     */
    public void setWidth(TextureObject widthster, float[] terrainPoints)
    {
        String actionDescription = "Setting Population Width";
        
        width = widthster;
        
        reload(actionDescription, terrainPoints);
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
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void updateForTerrainDepthChange(short terrainDepth,
            float[] terrainPoints)
    {
        String actionDescription = "Changing Terrain Depth";
        
        locations = new boolean[locations.length][terrainDepth];
        
        reload(actionDescription, terrainPoints);
    }
    
    /**
     * Re-adjusts the population for when the terrain's width is changed
     * 
     * @param terrainWidth The new width of the terrain (measured in vertices)
     * @param terrainPoints The point data used to create the terrain's MeshView
     */
    public void updateForTerrainWidthChange(short terrainWidth,
            float[] terrainPoints)
    {
        String actionDescription = "Changing Terrain Width";
        
        locations = new boolean[terrainWidth][locations[0].length];
        
        reload(actionDescription, terrainPoints);
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
        stringster = stringster + "Diffuse map: " + diffuse.getName() + "\n\n";
        
        stringster = stringster + "Shift image: " + shift.getName() + "\n";
        stringster = stringster + "Width image: " + width.getName() + "\n";
        stringster = stringster + "Height image: " + height.getName() + "\n\n";
        
        stringster = stringster + "Displacement range: from "
                + displacementRange[0].getName() + " to "
                + displacementRange[1].getName();
        
        return stringster;
    }
}
