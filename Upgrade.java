import greenfoot.*;

/**
 * Parent class for animated upgrade
 */
public abstract class Upgrade extends Actor
{
    private RoomData roomData;
    private int r,c,tr,tc;

    //animation related variables
    protected GreenfootImage[] frames;
    private int frameIndex = 0;
    private int animTimer = 0;
    private int animDelay = 6; 
    
    private static int FRAME_COUNT = 6;  
    protected String FRAME_PREFIX = "coin/coin";
    
    //prompt fields
    private boolean declined = false;
    private Player declinedBy = null;
    protected Player player=null;
    
    /**
     * @param RoomDate rd
     * @param roomR room 
     * @param roomC room
     * @param tr tile row
     * @param tc tile col
     */
    public Upgrade(RoomData rd,int r, int c, int tr, int tc) 
    {
        this.roomData=rd;
        this.r=r;
        this.c=c;
        this.tr=tr;
        this.tc=tc;
        //frames = loadFrames();
        //setImage(frames[0]);
    }
    protected abstract void upgrade(Player player);
    
    public void act() 
    {
        animate();
        
        //always only one player
        player=(Player) getOneIntersectingObject(Player.class);

        //if player not touching anymore 
        //allow prompt again later
        if (player==null)
        {
            clearDeclined();
            return;
        }

        //if this same player declined and is still standing here, do NOT reopen
        if (declined && player==declinedBy)
        {
            return;
        }
        
        upgrade(player);

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
    protected void clearDeclined()
    {
        declined = false;
        declinedBy = null;
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
                img.scale(30, 30);
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