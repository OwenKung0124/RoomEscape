import greenfoot.*;

/**
 * Door:
 * - When the door is unlocked and the player touches
 *   it tells GameWorld to move to the next room.
 *   
 * Door creates a Decoration for the look
 * - Left/Right doors use vertical images (mid)
 * - Top/Bottom doors use horizontal images (h)
 * 
 * @author:     OWwen Kung
 * @version     Jan 2026
 */
public class Door extends SuperSmoothMover
{
    //direction this door leads to (room movement):
    //dr=change in row
    //dc=change in column
    //e.g.
    //top door has dr = -1, dc = 0
    //bottom door has dr =  1, dc = 0
    //left door has dr = 0, dc = -1
    //right door has dr = 0, dc =  1
    private int dr;
    private int dc;

    private String name;
    
    private boolean unlocked = false;
    
    //left/right door picture size
    private static final int VISUAL_V_W = 48;
    private static final int VISUAL_V_H = 200;

    //top door picture size
    //top door usually needs to be taller 
    //so it can cover the bg images behind
    private static final int TOP_VISUAL_W = 165;
    private static final int TOP_VISUAL_H = 155;

    //bottom door picture size
    private static final int BOTTOM_VISUAL_W = 170;
    private static final int BOTTOM_VISUAL_H = 70;

    // top/bottom doors move up/down
    private static final int TOP_EXTRA_Y = -25;
    private static final int BOTTOM_EXTRA_Y = 20;

    //left door
    private static final int LEFT_EXTRA_X  = -40;

   //right door
    private static final int RIGHT_EXTRA_X = -32;

    //side door extra y to match bg
    private static final int SIDE_EXTRA_Y = 6;

    //left/right images
    private static final String VISUAL_V_CLOSED = "door/door_closed_mid.png";
    private static final String VISUAL_V_OPEN   = "door/door_open_mid.png";

    //top/down images
    private static final String VISUAL_H_CLOSED = "door/door_closed_h.png";
    private static final String VISUAL_H_OPEN   = "door/door_open_h.png";

    //the visual
    private DoorVisual visual;

    //store chosen visual size
    private int visualW;
    private int visualH;

    /**
     * Create a door.
     *
     * @param name label text\
     * @param dr which room row direction this door leads to
     * @param dc which room column direction this door leads to
     */
    public Door(String name,int dr, int dc)
    {
        this.name = name;
        this.dr = dr;
        this.dc = dc;
    }

    /**
     * Runs once when added to world.
     *  builds invisible hitbox image
     *  builds the big visual Decoration
     *  positions the visual correctly
     */
    protected void addedToWorld(World w)
    {
        updateHitboxImage();
        buildVisual();
        syncVisualLocation();
    }
    /**
     * called by Door System to and use dr+dc 
     * to figure out which neighboring room the door leads to.
     */
    public int getDr() 
    { 
        return dr; 
    }
    /**
     * called by Door System to and use dr+dc 
     * to figure out which neighboring room the door leads to.
     */
    public int getDc() 
    { 
        return dc; 
    }

    /**
     * door is locked if unlocked == false
     */
    public boolean isLocked()
    {
        return !unlocked;
    }
    /**
     * Lock/unlock door.
     *  updates the visual sprite (open/closed)
     *  plays sound when turning from locked -> unlocked
     */
    public void setUnlocked(boolean on)
    {
        if (unlocked != on)
        {
            //only play sound when it becomes unlocked
            if (!unlocked && on)
            {
                SoundManager.playDoorOpenSound();
            }

            unlocked = on;

            //update the big visual sprite
            if (visual != null && visual.getWorld() != null)
            {
                visual.setSprite(getVisualPath(), shouldMirror());
                syncVisualLocation();
            }
        }
    }
    /**
     * Every frame:
     *  keep visual aligned to door hitbox
     *  if unlocked and player touches hitbox -> change room
     */
    public void act()
    {
        //keep the visual stuck to the wall
        syncVisualLocation();

        //locked door does nothing
        if (!unlocked)
        {
            return;
        }

        //only when unlocked, touching player triggers room change
        if (isTouching(Player.class))
        {
            World w = getWorld();
            if (w instanceof GameWorld)
            {
                ((GameWorld) w).tryMove(dr, dc);
            }
        }
    }
    /**
     * Builds an invisible image for the hitbox.
     * Size is based on doorway geometry, not the big visual sprite
     */
    private void updateHitboxImage()
    {
        int w = (dc != 0) ? GameConfig.BORDER_THICK : GameConfig.DOOR_GAP_W;
        int h = (dc != 0) ? GameConfig.DOOR_GAP_H    : GameConfig.BORDER_THICK;

        GreenfootImage hitbox = new GreenfootImage(w, h);
        hitbox.setTransparency(0); // invisible
        setImage(hitbox);
    }
    /**
     * true = left/right door
     * false = top/bottom door
     */
    private boolean isVerticalDoor()
    {
        return dc != 0;
    }
    /**
     * chooses which image to use for visual:
     *  open or closed
     *  vertical or horizontal
     */
    private String getVisualPath()
    {
        if (isVerticalDoor())
        {
            if(unlocked)
            {
                return VISUAL_V_OPEN;
            }
            else
            {
                return VISUAL_V_CLOSED;
            }
        }
        else
        {
            if(unlocked)
            {
                return VISUAL_H_OPEN;
            }
            else
            {
                return VISUAL_H_CLOSED;
            }
        }
    }

    /**
     * mirror the RIGHT door so it faces inward
     */
    private boolean shouldMirror()
    {
        return (dc == 1);
    }

    /**
     * Decide how big the visual should be based on door direction.
     */
    private void chooseVisualSize()
    {
        if (isVerticalDoor())
        {
            visualW = VISUAL_V_W;
            visualH = VISUAL_V_H;
            return;
        }

        //horizontal
        if (dr == -1) 
        {
            //top
            visualW = TOP_VISUAL_W;
            visualH = TOP_VISUAL_H;
        }
        else         
        {
            //bottom
            visualW = BOTTOM_VISUAL_W;
            visualH = BOTTOM_VISUAL_H;
        }
    }

    /**
     * Creates the visual Decoration (big door sprite).
     * Removes the old one first if it already exists.
     */
    private void buildVisual()
    {
        World w = getWorld();
        if (w == null)
        {
            return;
        }

        chooseVisualSize();

        //remove old visual to prevent duplicates
        if (visual != null && visual.getWorld() != null)
        {
            w.removeObject(visual);
        }

        //create new big visual
        visual = new DoorVisual(getVisualPath(), visualW, visualH);
        visual.setSprite(getVisualPath(), shouldMirror());
        w.addObject(visual, getX(), getY());
    }

    /**
     * Reposition the visual so it aligned to the bg
     * The hitbox stays at the true doorway opening.
     */
    private void syncVisualLocation()
    {
        if (visual == null || visual.getWorld() == null)
        {
            return;   
        }

        int x=getX();
        int y=getY();

        if (isVerticalDoor())
        {
            int pushX=(visualW/2)-(GameConfig.BORDER_THICK/2);

            //left door
            if (dc == -1)
            {
               x+=(pushX+LEFT_EXTRA_X); 
            }

            //right door
            if (dc ==  1)
            {
                x-=(pushX+RIGHT_EXTRA_X);
            }

            //move both side doors up/down together
            y+=SIDE_EXTRA_Y;
        }
        else
        {
            int pushY=(visualH/2)-(GameConfig.BORDER_THICK/2);

            //top door: push upward to cover background doorway behind
            if (dr == -1)
            {
                y -= (pushY + TOP_EXTRA_Y);
            }

            //bottom door: push downward
            if (dr ==  1)
            {
                y += (pushY + BOTTOM_EXTRA_Y);
            }
        }

        visual.setLocation(x, y);
    }
}