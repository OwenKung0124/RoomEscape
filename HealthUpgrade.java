import greenfoot.*;

/**
 * Animated HealthUpgrade to upgrade player's attack power
 * 
 * @author:Cartis Lee
 * @version Jan 2026
 */
public class HealthUpgrade extends Upgrade
{
    
   /**
     * @param RoomDate rd
     * @param roomR room
     * @param roomC room
     * @param tr tile row
     * @param tc tile col
     */
    //public HealthUpgrade(RoomData rd,int r, int c, int tr, int tc)
     //HealthUpgrade(RoomData rd,int r, int c, int tr, int tc)
    public HealthUpgrade(int w, int h)
    {
        //super(rd, r, c, tr, tc);
        super(w,h);
        FRAME_COUNT=3;
        FRAME_PREFIX = "health_upgrade/upgrade";
        animDelay=20;
        frames = loadFrames();
        setImage(frames[0]);
    }
    protected void upgrade(Player p)
    {
        GameWorld gw = (GameWorld) getWorld();
        PromptManager pm = gw.getPromptManager();
        if (pm != null)
        {
            pm.showUpgrade(p, this);
        }
    }

}