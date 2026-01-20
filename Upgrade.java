import greenfoot.*;
import java.util.ArrayList;

/**
 * Parent class for animated upgrade
 * 
 * @author:Owen Kung, Cartis Lee
 * @version Jan 2026
 */
public abstract class Upgrade extends Blocker
{
    //private RoomData roomData;
    //private int r,c,tr,tc;

    //animation related variables
    protected GreenfootImage[] frames;
    private int frameIndex = 0;
    private int animTimer = 0;
    protected int animDelay = 6; 
    
    protected  int FRAME_COUNT = 6;  
    protected String FRAME_PREFIX = "coin/coin";
    protected int VISUAL_W = 30;
    protected int VISUAL_H = 30;

    //prompt fields
    private boolean declined=false;
    private boolean accepted=false;
    private Player declinedBy=null;
    private Player acceptedBy=null;
    protected Player player=null;

    //how close the player must be to trigger the prompt
    //protected int TRIGGER_RADIUS = 60;
    
    /**
     * Creates a statue with a collision hitbox of size (w, h).
     *
     * @param w:    hitbox width
     * @param h:    hitbox height
     */
     public Upgrade(int w, int h)
    {
        super(w, h);
        VISUAL_W=w;
        VISUAL_H=h;
        
        //create a transparent image for the hitbox.
        //the actor still collides because the Blocker logic uses this size,
        //but the player won't see a sprite drawn for the hitbox.
        setImage(new GreenfootImage(VISUAL_W, VISUAL_H));

    }
    protected abstract void upgrade(Player player);
    
    public void act() 
    {
        animate();
        
        //always only one player
        player = getNearestPlayerInRange(triggerRadius());
        
        //if player not touching anymore 
        //allow prompt again later
        if (player==null)
        {
            clearDeclined();
            clearAccepted();
            return;
        }

        //if this same player declined and is still standing near, do not reopen
        if (declined && player==declinedBy)
        {
            return;
        }
        
        //if this same player accepted and is still standing near, do not reopen
        if (accepted && player==acceptedBy)
        {
            return;
        }
        
        upgrade(player);
    }
    private int triggerRadius()
    {
        int w = getImage().getWidth();
        int h = getImage().getHeight();
        return Math.max(w, h) / 2 + 40; // extra padding
    }
    private Player getNearestPlayerInRange(int radius)
    {
        ArrayList<Player> ps = (ArrayList<Player>) getObjectsInRange(radius, Player.class);
        if (ps == null || ps.isEmpty())
        {
             return null;    
        }
        //usually only one player, so just return the first
        return ps.get(0);
    }
    /**
     * called by PromptManager to mark who decline the prompt
     * 
     * @param p: Player who declines the prompt
     */
    public void markDeclined(Player p)
    {
        declined = true;
        declinedBy = p;
    }
    private void clearDeclined()
    {
        declined = false;
        declinedBy = null;
    }
    /**
     * called by PromptManager to mark who accepts the prompt
     * 
     * @param p: Player who accepts the prompt
     */
    public void markAccepted(Player p)
    {
        accepted=true;
        acceptedBy=p;
    }
    private void clearAccepted()
    {
        accepted=false;
        acceptedBy=null;
    }
    protected void animate()
    {
        if (frames== null||frames.length == 0)
        {
            return;   
        }
    
        animTimer++;
        if (animTimer>=animDelay)
        {
            animTimer = 0;
            frameIndex = (frameIndex + 1) % frames.length;
            setImage(frames[frameIndex]);
        }
    }
    protected GreenfootImage[] loadFrames()
    {
        GreenfootImage[] imgs = new GreenfootImage[FRAME_COUNT];
    
        try
        {
            for (int i = 0; i < FRAME_COUNT; i++)
            {
                GreenfootImage img = new GreenfootImage(FRAME_PREFIX + (i+1) + ".png");
                img.scale(VISUAL_W,VISUAL_H);
                imgs[i] = img;
            }
            return imgs;
        }
        catch (IllegalArgumentException e)
        {
            //file not found
            //create placeholderimage
            GreenfootImage placeHolderImage = new GreenfootImage(30, 30);
            placeHolderImage.setColor(Color.YELLOW);
            placeHolderImage.fillOval(2, 2, 26, 26);
            placeHolderImage.setColor(Color.ORANGE);
            placeHolderImage.drawOval(2, 2, 26, 26);
    
            return new GreenfootImage[]
            {
                placeHolderImage 
            };
        }
    }
}