import greenfoot.*;
import java.util.*;

/**
 * HitBox is a shortlived invisible hitbox.
 * It removes enemies it touches.
 * 
 * @author:Clifton Lin
 * @version:2026 Jan
 */
public class HitBox extends SuperSmoothMover
{
    private int life;
    private int attackPower;
    
    /*
     * Creates an invisible hitbox.
     *
     * @param w          hitbox width
     * @param h          hitbox height
     * @param lifeFrames how many act() frames it lasts
     * @oaram attackPower passed by the player
     */
    public HitBox(int w, int h, int lifeFrames, int attackPower)
    {
        life = lifeFrames;
        this.attackPower=attackPower;

        //invisible rectangle
        GreenfootImage img = new GreenfootImage(w, h);
        img.setTransparency(0);
        setImage(img);
        
        //increase attackcount
        GameWorld.attackCount++;
    }

    public void act()
    {
        //remove enemies touched
        ArrayList<Enemy> enemies = (ArrayList<Enemy>)getIntersectingObjects(Enemy.class);
        if (enemies != null && enemies.size() > 0)
        {
            for (Enemy e : enemies)
            {
                if (e != null && e.getWorld() != null)
                {
                    e.takeDamage(attackPower);
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