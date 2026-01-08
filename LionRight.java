import greenfoot.*;

/**
 * LionRight is a statue in the room
 * It is a solid blocker
 *
 * Use Decoration class to display the actual graphics
 */
public class LionRight extends Blocker
{
    //The visible statue sprite actor 
    //so that the sprite can display
    private Decoration visual;

    /**
     * Creates a statue with a collision hitbox of size (w, h).
     * The hitbox is tranparent, just to block the movement
     *
     * @param w:    hitbox width
     * @param h:    hitbox height
     */
    public LionRight(int w, int h)
    {
        super(w, h);

        //create a transparent image for the hitbox
        //the actor still collides because the Blocker logic uses this size,
        //but the player won't see a sprite drawn for the hitbox.
        setImage(new GreenfootImage(w, h));

    }

    /**
     * after the hitbox ix created
     * spawns the visible statue decoration 
     * and positions it so its bottom lines up with this tile.
     *
     * @param world:    the world this actor was added to
     */
    protected void addedToWorld(World world)
    {
        //create the visual decoration
        visual=new Decoration(GameConfig.LION_RIGHT_IMG, GameConfig.LION_RIGHT_W, GameConfig.LION_RIGHT_H);

        //add the decoration at the same tile position as the hitbox.
        world.addObject(visual, getX(), getY());

        //align the larger decoration sprite to the bottom
        int dy=(visual.getImage().getHeight() / 2) - (getImage().getHeight() / 2);
        visual.setLocation(getX(), getY() - dy);
    }

    /**
     * when the hitbox is removed
     * removes the visible decoration too
     * so it doesn't get left behind.
     *
     * @param world:    the world this actor was removed from
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