import greenfoot.*;

/**
 * HealthBar draws a health bar for any HasHealth actor
 * can follow an actor or display at a fix place on screen
 * 
 * @author:
 * @version:
 */
public class HealthBar extends Actor
{
    private CombatActor unit;      //read HP from this
    private Actor follow;        //follow this actor's position
    private boolean followTarget;

    private int barW;
    private int barH;
    private int yOffset;

    /**
     * @param unit         actor that has health
     * @param follow       actor to follow
     * @param width        bar width
     * @param height       bar height
     * @param followTarget true to follow follow actor closely
     * @param yOffset      y offset from the actor to display
     */
    public HealthBar(CombatActor unit, Actor follow, int width, int height, boolean followTarget, int yOffset)
    {
        this.unit = unit;
        this.follow = follow;
        this.barW = width;
        this.barH = height;
        this.followTarget = followTarget;
        this.yOffset = yOffset;

        setImage(new GreenfootImage(barW, barH));
        updateImage();
    }
    /**
     * following actor and updage images
     */    
    public void act()
    {
        if (getWorld() == null) return;

        //remove the bar, if actor is dead or removed
        if (followTarget)
        {
            if (follow == null || follow.getWorld() == null)
            {
                getWorld().removeObject(this);
                return;
            }
            setLocation(follow.getX(), follow.getY() + yOffset);
        }

        updateImage();
    }

    //redraw based on unit HP ratio
    private void updateImage()
    {
        GreenfootImage img = getImage();
        img.clear();

        //background and border
        img.setColor(Color.DARK_GRAY);
        img.fillRect(0, 0, barW, barH);

        img.setColor(Color.WHITE);
        img.drawRect(0, 0, barW - 1, barH - 1);

        if (unit == null)
        {
            return;
        }

        int hp = unit.getHealth();
        int max = unit.getMaxHealth();
        //System.out.println("hp:="+hp);
        //System.out.println("max:="+max);
        
        if (max <= 0) 
        {
            return;
        }

        int innerX = 3;
        int innerY = 3;
        int innerW = barW - 6;
        int innerH = barH - 6;

        double ratio = (double) hp / (double) max;
        if (ratio < 0) 
        {
            ratio=0;
        }
        if (ratio > 1) 
        {
            ratio=1;
        }

        int fillW = (int) Math.round(innerW * ratio);

        //choose color by percent
        //be more visual
        Color fill;
        if (ratio > 0.6)
        {
            fill = Color.GREEN;   
        }
        else if (ratio > 0.3) 
        {
            fill = Color.YELLOW;   
        }
        else
        {
            fill = Color.RED;   
        }

        //bar background  and fill
        img.setColor(Color.BLACK);
        img.fillRect(innerX, innerY, innerW, innerH);

        img.setColor(fill);
        img.fillRect(innerX, innerY, fillW, innerH);
        
        
    }
}