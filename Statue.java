import greenfoot.*;

/**
 * A Statue is a solid blocker that also shows a larger decorative image on top.
 *
 */
public class Statue extends Blocker
{
    //The visible statue sprite actor 
    //so that the sprite can display
    private AnimatedDecoration visual;

    /**
     * Creates a statue with a collision hitbox of size (w, h).
     *
     * @param w:    hitbox width
     * @param h:    hitbox height
     */
    public Statue(int w, int h)
    {
        super(w, h);

        //create a transparent image for the hitbox.
        //the actor still collides because the Blocker logic uses this size,
        //but the player won't see a sprite drawn for the hitbox.
        setImage(new GreenfootImage(w, h));

    }

    /**
     * after the hitbox ix created
     * creates the visible statue decoration and 
     * positions it so its bottom lines up with this tile.
     *
     * @param world:    the world this actor was added to
     */
    protected void addedToWorld(World world)
    {
        //create the visual decoration
        //visual = new Decoration(GameConfig.STATUE_IMG, GameConfig.STATUE_W, GameConfig.STATUE_H); 
        //public AnimatedDecoration(String baseFileName, int frameCount, int w, int h)
        visual=new AnimatedDecoration(GameConfig.STATUE_IMG, 2,GameConfig.STATUE_W, GameConfig.STATUE_H);  

        //add the decoration at the same tile position as the hitbox.
        world.addObject(visual, getX(), getY());

        // Bottom-align the larger decoration sprite to the tile-sized hitbox:
        int dy = (visual.getImage().getHeight() / 2) - (getImage().getHeight() / 2);
        visual.setLocation(getX(), getY() - dy);
    }

    /**
     * when the hitbox is removed
     * removes the visible decoration too
     * so it doesn't get left behind.
     *
     * @param world the world this actor was removed from
     */
    protected void removedFromWorld(World world)
    {
        ///only remove if the decoration exists and is still in a world.
        if (visual != null && visual.getWorld() != null) 
        {
            world.removeObject(visual);
        }
    }
}