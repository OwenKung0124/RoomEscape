import greenfoot.*;
/**
 * Animated AttckUpgrade to upgrade player's attack power
 */
public class AttackUpgrade extends Upgrade
{
    public AttackUpgrade(RoomData rd, int r, int c, int tr, int tc)
    {
        super(rd, r, c, tr, tc);
        FRAME_PREFIX = "attack_upgrade/upgrade";
        frames = loadFrames();
        setImage(frames[0]);
    }
    protected void upgrade(Player p)
    {
        GameWorld gw = (GameWorld) getWorld();
        if (gw == null) return;

        PromptManager pm = gw.getPromptManager();
        if (pm != null) pm.showUpgrade(p, this);
    }
}