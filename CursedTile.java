import greenfoot.*;
public class CursedTile extends Actor
{
    private int delay;
    private int activeTimer = 25;
    private int phase;
    private boolean active = false;

    public CursedTile(int delayFrames, int phase)
    {
        delay = delayFrames;
        this.phase = phase;

        GreenfootImage img = new GreenfootImage(90, 90);
        img.setColor(new Color(180, 255, 0, 140));
        img.fillOval(0, 0, 90, 90);
        setImage(img);

        getImage().setTransparency(0); //invisible until active
    }

    public void act()
    {
        if (getWorld() == null) return;

        if (!active)
        {
            delay--;
            if (delay <= 0)
            {
                active = true;
                getImage().setTransparency(160);
            }
            return;
        }

        activeTimer--;

        Player p = (Player) getOneIntersectingObject(Player.class);
        if (p != null)
        {
            int dmg = (phase == 1) ? 1 : (phase == 2 ? 2 : 3);
            p.takeDamage(dmg); //use your real method name if different
        }

        if (activeTimer <= 0) getWorld().removeObject(this);
    }
}