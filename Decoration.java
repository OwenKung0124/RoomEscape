import greenfoot.*;

/**
 * A visual-only world object for large decorations 
 * (e.g., statues, props).
 *
 * This actor is meant to be cosmetic only. 
 * It can be larger than a tile and overlap multiple tiles.
 *
 * Important: In Greenfoot, collisions are based on an Actor's image size.
 * If you give this decoration a large image, it will also have a large collision
 * area. So if you need blocking/collision, use a separate collider actor
 * (Wall, InvisibleWall, or a tile-sized hitbox) and keep Decoration for visuals.
 */
public class Decoration extends SuperSmoothMover
{
    /**
     * Creates a decoration from an image file and scales it to the given size.
     *
     * @param filename image path relative to the images/ folder
     *                 (example: "deco/statue.png")
     * @param w target width in pixels after scaling
     * @param h target height in pixels after scaling
     */
    public Decoration(String filename, int w, int h) 
    {
        GreenfootImage img = new GreenfootImage(filename);
        img.scale(w, h);
        setImage(img);
    }
}