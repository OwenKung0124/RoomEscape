import greenfoot.*;

/**
 * Animated HealthUpgrade to upgrade player's attack power
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
    public HealthUpgrade(RoomData rd,int r, int c, int tr, int tc)
    {
        super(rd, r, c, tr, tc);
        FRAME_PREFIX = "health_upgrade/upgrade";
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