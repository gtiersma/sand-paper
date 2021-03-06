package tabs;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;

/**
 * Controls the operations for the camera tab
 * 
 * @author George Tiersma
 */
public class CameraTab
{
    // The number of pixels that are grouped for each zoom-per-pixel bracket.
    // For example, if this is set to 250, an average render image dimension
    // between 0-250 would be the bracket for index 0 of the zoom-per-pixel
    // array, an average dimension between 250-500 would be for index 1, etc.
    final private short ZOOM_PIXEL_RANGE = 250;
    
    final private int DEFAULT_FIELD_OF_VIEW = 30;
    final private int DEFAULT_HORIZONTAL_ANGLE = 0;
    final private int DEFAULT_VERTICAL_ANGLE = 45;
    final private int DEFAULT_ZOOM = 0;
    
    final private double NEAR_CLIP = 0.1;
    final private double FAR_CLIP = Double.MAX_VALUE;
    
    // How much the camera is to zoom per pixel of the render image's dimensions
    // set by the user (used when creating a render image). Which index is used
    // is dependant on the average dimension size currently set. ZOOM_PIXEL
    // _RANGE is the number of pixels that each index is grouped by.
    
    // There should be a formula to calculate these values, but it has not been
    // developed yet, so until then, these constants are used.
    final private double[] RESIZE_ZOOM_PER_PIXEL = {-6, -1, 0, 0.5, 1, 1, 1,
            1.2, 1.3, 1.4, 1.4, 1.5};
    
    // The camera's field of view measured in degrees
    private short fieldOfView;
    // The angle to which the mesh should be rotating (simulating the effect of
    // the camera orbiting and being rotated around the mesh)
    private short horizontalAngle;
    private short verticalAngle;
    
    // The position central to the mesh
    private int originX;
    private int originY;
    private int originZ;
    
    // User-defined amounts to adjust the camera's position
    private int xAdjustment;
    private int yAdjustment;
    
    // The farthest distance a point is from the mesh center
    private int furthest;
    
    // How far the camera should be zoomed in or out
    private int zoom;
    
    // How far the camera's position should be adjusted to center the mesh
    private double xOffset;
    private double yOffset;
    
    // The camera
    private PerspectiveCamera camster;
    
    // The Rotate objects for the mesh.
    // The original plan was to rotate and orbit the camera around the mesh. 
    // This proved to be much more complicated than expected, so the mesh is
    // what is rotated, simulating the effect of the camera being rotated and
    // orbiting.
    private Rotate xRotate;
    private Rotate yRotate;
    
    /**
     * CONSTRUCTOR
     */
    public CameraTab()
    {
        fieldOfView = DEFAULT_FIELD_OF_VIEW;
        
        horizontalAngle = DEFAULT_HORIZONTAL_ANGLE;
        verticalAngle = DEFAULT_VERTICAL_ANGLE;
        
        originX = 0;
        originY = 0;
        originZ = 0;
        
        zoom = DEFAULT_ZOOM;
        
        furthest = 0;
        
        xOffset = 0;
        yOffset = 0;
        
        xAdjustment = 0;
        yAdjustment = 0;
        
        camster = new PerspectiveCamera(false);
        camster.setNearClip(NEAR_CLIP);
        camster.setFarClip(FAR_CLIP);
        
        xRotate = new Rotate(verticalAngle, Rotate.X_AXIS);
        yRotate = new Rotate(horizontalAngle, Rotate.Y_AXIS);
    }
    
    /**
     * Gets the PerspectiveCamera object
     * 
     * @return The PerspectiveCamera object
     */
    public PerspectiveCamera getCamera()
    {
        return camster;
    }
    
    /**
     * Gets the field-of-view value that is set upon program launch
     * 
     * @return The default field-of-view value
     */
    public int getDefaultField()
    {
        return DEFAULT_FIELD_OF_VIEW;
    }
    
    /**
     * Gets the horizontal angle value that is set upon program launch
     * 
     * @return The default horizontal angle value
     */
    public double getDefaultHorizontalAngle()
    {
        return DEFAULT_HORIZONTAL_ANGLE;
    }
    
    /**
     * Gets the vertical angle value that is set upon program launch
     * 
     * @return The default vertical angle value
     */
    public double getDefaultVerticalAngle()
    {
        return DEFAULT_VERTICAL_ANGLE;
    }
    
    /**
     * Gets the field of view of the camera
     * 
     * @return The field of view (measured in degrees)
     */
    public short getFieldOfView()
    {
        return fieldOfView;
    }
    
    /**
     * Gets the amount to zoom in or out for when the preview is resized
     * 
     * @param dimensionAverage The average of the width and height of the new
     *                         size
     * 
     * @return The additional amount to zoom for preview's new size
     */
    private int getResizeZoom(double dimensionAverage)
    {
        // The indices of the RESIZE_ZOOM_PER_PIXEL array that will point to the
        // values that make up the range to determine how far to zoom per pixel.
        // These 2 indices will always be adjacent in the array, and the value
        // to zoom per pixel will almost always be somewhere between the 2
        // values.
        byte zoomBeginIndex = (byte)(dimensionAverage / ZOOM_PIXEL_RANGE);
        byte zoomEndIndex = (byte)(zoomBeginIndex + 1);
        
        // The total additional amount for the camera to zoom
        int extraZoom;
        
        // The amount the camera is to zoom per pizel of the dimension average
        double zoomPerPixel;
        
        // If the calculated index is out of bounds...
        if (zoomBeginIndex >= RESIZE_ZOOM_PER_PIXEL.length)
        {
            // ...make it the last index.
            zoomBeginIndex = (byte)(RESIZE_ZOOM_PER_PIXEL.length - 1);
        }
        
        // Initialize the amount to zoom per pixel to the lowest value in the
        // range of possible values
        zoomPerPixel = RESIZE_ZOOM_PER_PIXEL[zoomBeginIndex];
        
        // As long as the ending index points to an index actually within the
        // array's bounds...
        if (zoomEndIndex < RESIZE_ZOOM_PER_PIXEL.length)
        {
            // Get the remainder of the calculation that was used to find the
            // indices
            double sizeRemainder = dimensionAverage % ZOOM_PIXEL_RANGE;
            
            // Find the percentage of how far the remainder was to causing an
            // increment in the indices
            double remainderZoomPercentage = sizeRemainder / ZOOM_PIXEL_RANGE;
            // Get the range
            double zoomDifference = RESIZE_ZOOM_PER_PIXEL[zoomEndIndex]
                    - zoomPerPixel;
            // Find the additional amount to be zoomed per pixel from the
            // remainder
            double zoomFromRemainder = zoomDifference * remainderZoomPercentage;
            
            // Add the remainder zoom to the amount to zoom per pixel
            zoomPerPixel = zoomPerPixel + zoomFromRemainder;
        }
        
        // Calculate the total zoom amount
        extraZoom = (int)(dimensionAverage * zoomPerPixel);
        
        return extraZoom;
    }
    
    /**
     * Gets how far the camera is to be adjusted on the x axis from its central
     * position
     * 
     * @return How far the camera is adjusted on the x axis
     */
    public double getXAdjustment()
    {
        return xAdjustment;
    }
    
    /**
     * Gets the Rotate object for rotating the mesh on the X axis, simulating
     * the camera's rotation and orbit
     * 
     * @return The Rotate object for the X axis
     */
    public Rotate getXRotate()
    {
        return xRotate;
    }
    
    /**
     * Gets how far the camera is to be adjusted on the y axis from its central
     * position
     * 
     * @return How far the camera is adjusted on the y axis
     */
    public double getYAdjustment()
    {
        return yAdjustment;
    }
    
    /**
     * Gets the Rotate object for rotating the mesh on the Y axis, simulating
     * the camera's rotation and orbit
     * 
     * @return The Rotate object for the Y axis
     */
    public Rotate getYRotate()
    {
        return yRotate;
    }
    
    /**
     * Gets how far the camera is zoomed in from its central position
     * 
     * @return The camera's zoom adjustment
     */
    public double getZoom()
    {
        return zoom;
    }
    
    /**
     * Prepares the Rotation objects by setting their pivot point to the center
     * of the mesh
     */
    private void prepareRotations()
    {
        xRotate.setPivotX(originX);
        xRotate.setPivotY(originY);
        xRotate.setPivotZ(originZ);
        
        yRotate.setPivotX(originX);
        yRotate.setPivotY(originY);
        yRotate.setPivotZ(originZ);
    }

    /**
     * Positions the camera
     */
    private void refreshPosition()
    {
        camster.setTranslateX(xOffset + originX + xAdjustment);
        camster.setTranslateY(yOffset + originY + yAdjustment);
    }

    /**
     * Sets the camera's zoom
     */
    private void refreshZoom()
    {
        // Percentage variable of how far the default camera position should be
        // zoomed in relative to the farthest point from the mesh center
        final double FURTHEST_POINT_ADJUSTMENT = -1.2;
        
        double adjustment = furthest * FURTHEST_POINT_ADJUSTMENT;
        
        double z = originZ + zoom + adjustment;
        
        camster.setTranslateZ(z);
    }

    /**
     * Sets how far the camera should be adjusted on the x and y scale to center
     * the mesh
     * 
     * @param x How far the camera should be adjusted on the x scale to center
     *          the mesh
     * @param y How far the camera should be adjusted on the y scale to center
     *          the mesh
     */
    public void setCameraOffset(double x, double y)
    {
        // Making these values negative allows for a positive parameter given in
        // this function to move the camera in the correct direction
        xOffset = -x;
        yOffset = -y;
        
        refreshPosition();
    }
    
    /**
     * Sets the camera's field of view
     * 
     * @param degrees The angle of the field of view
     */
    public void setFieldOfView(short degrees)
    {
        camster.setFieldOfView(degrees);
    }
    
    /**
     * Sets how far the furthest point is from the center of the mesh
     * 
     * @param distance The greatest distance a point on the mesh is from the
     *                 center of the mesh
     */
    public void setFurthestPoint(int distance)
    {
        furthest = distance;
        
        refreshZoom();
    }
    
    /**
     * Sets the horizontal angle to which the mesh should be rotated (simulating
     * the rotation and orbit of the camera)
     * 
     * @param angle How far the mesh should be rotated
     */
    public void setHorizontalAngle(short angle)
    {
        horizontalAngle = angle;
        
        yRotate.setAngle(horizontalAngle);
    }
    
    /**
     * Sets whether the orientation of the field of view should be horizontal or
     * vertical
     * 
     * @param vertical If the orientation of the field of view should be
     * vertical
     */
    public void setOrientation(boolean vertical)
    {
        camster.setVerticalFieldOfView(vertical);
    }
    
    /**
     * Sets the position to which the camera should focus on (This should be the
     * center of the mesh)
     * 
     * @param x Where the camera should focus on the x axis
     * @param y Where the camera should focus on the y axis
     * @param z Where the camera should focus on the z axis
     */
    public void setOrigin(int x, int y, int z)
    {        
        originX = x;
        originY = y;
        originZ = z;
        
        refreshPosition();
        prepareRotations();
        refreshZoom();
    }
    
    /**
     * Sets the vertical angle to which the mesh should be rotated (simulating
     * the rotation and orbit of the camera)
     * 
     * @param angle How far the mesh should be rotated
     */
    public void setVerticalAngle(short angle)
    {
        verticalAngle = angle;
        
        xRotate.setAngle(angle);
    }
    
    /**
     * Sets how far the camera should be adjusted on the x axis relative to its
     * regular position (This should be how far the user chooses to shift the
     * camera)
     * 
     * @param adjustment How far to shift the camera on the x axis
     */
    public void setXAdjustment(int adjustment)
    {
        xAdjustment = adjustment;
        
        refreshPosition();
    }
    
    /**
     * Sets how far the camera should be adjusted on the y axis relative to its
     * regular position (This should be how far the user chooses to shift the
     * camera)
     * 
     * @param adjustment How far to shift the camera on the y axis
     */
    public void setYAdjustment(int adjustment)
    {
        yAdjustment = adjustment;
        
        refreshPosition();
    }
    
    /**
     * Zooms the camera either in or out
     * 
     * @param zoomster How far to zoom the camera in. A negative value will zoom
     *                 the camera out.
     */
    public void setZoom(int zoomster)
    {
        zoom = zoomster;
        
        refreshZoom();
    }
    
    /**
     * Zooms the camera either in or out for when the preview is resized
     * 
     * @param zoomster How far to zoom the camera in. A negative value will zoom
     *                 the camera out.
     * @param dimensionAverage The average of the width and height that is set
     *                          to be the image's dimensions
     */
    public void zoomForResize(int zoomster, double dimensionAverage)
    {
        int extraZoom = getResizeZoom(dimensionAverage);
        
        // Zoom the regular amount in addition to the zoom adjustment for the
        // render
        setZoom(zoomster + extraZoom);
    }
}
