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
    private int w;
    private int h;
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
        this.w = w;
        this.h = h;
        setSprite(filename);
    }

    /**
     * Change the sprite image and keep the same scaled size.
     */
    public void setSprite(String filename)
    {
        GreenfootImage img = new GreenfootImage(filename);
        img.scale(w, h);
        setImage(img);
    }

    /**
     * Change the sprite and  mirror it horizontally
     */
    public void setSprite(String filename, boolean mirrorHorizontally)
    {
        GreenfootImage img = new GreenfootImage(filename);
        if (mirrorHorizontally) img.mirrorHorizontally();
        img.scale(w, h);
        setImage(img);
    }
}