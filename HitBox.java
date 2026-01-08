import greenfoot.*;
import java.util.*;

/**
 * AxeSlash is a shortlived invisible hitbox.
 * It removes enemies it touches.
 *
 */
public class HitBox extends SuperSmoothMover
{
    private int life;

    private static final int DAMAGE_POWER=1;
    /*
     * Creates an invisible hitbox.
     *
     * @param w         hitbox width
     * @param h         hitbox height
     * @param lifeFrames how many act() frames it lasts
     */
    public HitBox(int w, int h, int lifeFrames)
    {
        life = lifeFrames;

        //invisible rectangle
        GreenfootImage img = new GreenfootImage(w, h);
        img.setTransparency(0);
        setImage(img);
    }

    public void act()
    {
        //remove enemies touched
        List<Enemy> enemies = getIntersectingObjects(Enemy.class);
        if (enemies != null && enemies.size() > 0)
        {
            for (Enemy e : enemies)
            {
                if (e != null && e.getWorld() != null)
                {
                    e.takeDamage(DAMAGE_POWER);
                    if(e.getHealth()<=0)
                    {
                        getWorld().removeObject(e);
                    }
                    
                }
            }
        }

        //countdown
        life--;
        if (life <= 0 && getWorld() != null)
        {
            getWorld().removeObject(this);
        }
    }
}