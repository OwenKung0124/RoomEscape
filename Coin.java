import greenfoot.*;

/**
 * A collectible coin
 * updates RoomData when collected.
 */
public class Coin extends Actor 
{

    private RoomData roomData;
    private int r,c,tr,tc;

    //animation related variables
    private GreenfootImage[] frames;
    private int frameIndex = 0;
    private int animTimer = 0;
    private int animDelay = 6; 
    
    private static final int FRAME_COUNT = 6;  
    private static final String FRAME_PREFIX = "coin/coin";
    /**
     * @param RoomDate rd
     * @param roomR room 
     * @param roomC room
     * @param tr tile row
     * @param tc tile col
     */
    public Coin(RoomData rd,int r, int c, int tr, int tc) 
    {
        this.roomData=rd;
        this.r=r;
        this.c=c;
        this.tr=tr;
        this.tc=tc;
        frames = loadCoinFrames();
        setImage(frames[0]);
    }

    public void act() 
    {
        animate();
        if (isTouching(Player.class)) 
        {
            collect();
        }
    }
    private void animate()
    {
        if (frames == null || frames.length == 0)
        {
            return;   
        }
    
        animTimer++;
        if (animTimer >= animDelay)
        {
            animTimer = 0;
            frameIndex = (frameIndex + 1) % frames.length;
            setImage(frames[frameIndex]);
        }
    }
    /**
     * Collect the coin:
     * remove the coin
     * update the RoomData
     */
    private void collect() 
    {
        //playSoundEffect
        SoundManager.playCoinSound();
        
        //update RoomData directly so it won't respawn next time
        if (roomData != null) 
        {
            if (roomData.tiles[tr][tc] == GameConfig.COIN) 
            {
                   roomData.setTile(tr, tc, GameConfig.FLOOR);  //permanently remove coin from that room’s layout
            }
        }

        World world = getWorld();
        if (world != null) 
        {
            world.removeObject(this);   
        }
    }
    private GreenfootImage[] loadCoinFrames()
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