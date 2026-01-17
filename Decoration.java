import greenfoot.*;

/**
 *  Decoration: A visual display for large decorations 
 *  (e.g., statues, lion statue).
 *
 * @author Clifton Lin
 * @version Jan 2026
 */
public class Decoration extends SuperSmoothMover
{
    /**
     * Creates a decoration from an image file and scales it to the given size.
     *
     * @param filename: image path 
     *                
     * @param w: target width in pixels after scaling
     * @param h: target height in pixels after scaling
     */
    public Decoration(String filename, int w, int h) 
    {
        GreenfootImage img = new GreenfootImage(filename);
        img.scale(w, h);
        setImage(img);
    }
}