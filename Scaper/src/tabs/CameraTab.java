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
    final private double DEFAULT_HORIZONTAL_ANGLE = 0;
    final private double DEFAULT_VERTICAL_ANGLE = 45;
    final private double DEFAULT_ZOOM = 0;
    final private double DEFAULT_FIELD_OF_VIEW = 30;
    
    final private double NEAR_CLIP = 0.1;
    final private double FAR_CLIP = Double.MAX_VALUE;
    
    // The position central to the mesh
    private double originX;
    private double originY;
    private double originZ;
    
    // How far the camera's position should be adjusted to center the mesh
    private double xOffset;
    private double yOffset;
    
    // User-defined amounts to adjust the camera's position
    private double xAdjustment;
    private double yAdjustment;
    
    // The angle to which the mesh should be rotating (simulating the effect of
    // the camera orbiting and being rotated around the mesh)
    private double horizontalAngle;
    private double verticalAngle;
    
    // The farthest distance a point is from the mesh center
    private double furthest;
    
    // How far the camera should be zoomed in or out
    private double zoom;
    
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
        originX = 0;
        originY = 0;
        originZ = 0;
        
        horizontalAngle = DEFAULT_HORIZONTAL_ANGLE;
        verticalAngle = DEFAULT_VERTICAL_ANGLE;
        
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
    public double getDefaultField()
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
        final double FURTHEST_POINT_ADJUSTMENT = -1.03;
        
        double z = originZ + zoom + (furthest * FURTHEST_POINT_ADJUSTMENT);
        
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
    public void setFieldOfView(double degrees)
    {
        camster.setFieldOfView(degrees);
    }
    
    /**
     * Sets how far the furthest point is from the center of the mesh
     * 
     * @param distance The greatest distance a point on the mesh is from the
     *                 center of the mesh
     */
    public void setFurthestPoint(double distance)
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
    public void setHorizontalAngle(double angle)
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
    public void setOrigin(double x, double y, double z)
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
    public void setVerticalAngle(double angle)
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
    public void setXAdjustment(double adjustment)
    {
        xAdjustment = adjustment;
        
        refreshPosition();
    }
    
    /**
     * Sets how far the camera should be adjusted on the x axis relative to its
     * regular position (This should be how far the user chooses to shift the
     * camera)
     * 
     * @param adjustment How far to shift the camera on the x axis
     */
    public void setXAdjustment(String adjustment)
    {
        xAdjustment = Double.parseDouble(adjustment);
        
        refreshPosition();
    }
    
    /**
     * Sets how far the camera should be adjusted on the y axis relative to its
     * regular position (This should be how far the user chooses to shift the
     * camera)
     * 
     * @param adjustment How far to shift the camera on the y axis
     */
    public void setYAdjustment(double adjustment)
    {
        yAdjustment = adjustment;
        
        refreshPosition();
    }
    
    /**
     * Sets how far the camera should be adjusted on the y axis relative to its
     * regular position (This should be how far the user chooses to shift the
     * camera)
     * 
     * @param adjustment How far to shift the camera on the y axis
     */
    public void setYAdjustment(String adjustment)
    {
        yAdjustment = Double.parseDouble(adjustment);
        
        refreshPosition();
    }
    
    /**
     * Zooms the camera either in or out
     * 
     * @param zoomster How far to zoom the camera in. A negative value will zoom
     * the camera out
     */
    public void setZoom(double zoomster)
    {
        zoom = zoomster;
        
        refreshZoom();
    }
    
    /**
     * Zooms the camera either in or out
     * 
     * @param zoomster How far to zoom the camera in. A negative value will zoom
     * the camera out
     */
    public void setZoom(String zoomster)
    {
        this.zoom = Double.parseDouble(zoomster);
        
        refreshZoom();
    }
}
