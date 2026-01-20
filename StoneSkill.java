import greenfoot.*;

/**
 * Animated Stone Skill that player can aquire
 * 
 * @author:Cartis Lee
 * @version Jan 2026
 */
public class StoneSkill extends Upgrade
{
    /**
     * @param RoomDate rd
     * @param roomR room
     * @param roomC room
     * @param tr tile row
     * @param tc tile col
     */
    //public StoneSkill (RoomData rd,int r, int c, int tr, int tc)
    public StoneSkill(int w, int h)
    {
        //super(rd, r, c, tr, tc);
        super(w,h);
        FRAME_COUNT=1;
        FRAME_PREFIX = "stone_skill/upgrade";
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