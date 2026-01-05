import greenfoot.*;

/**
 * EnemyArrow is a Bullet that hits the Player instead of Enemy.
 * It reuses Bullet movement, wall collision, bounds removal, and lifetime.
 */
public class EnemyArrow extends Bullet
{
    private int DAMAGE_LEVEL = 5;

    /**
     * Create an arrow moving in a 4-direction unit vector.
     * dirX/dirY should be -1, 0, or 1 (same as Bullet).
     */
    public EnemyArrow(int dirX, int dirY)
    {
        super(dirX, dirY);

        speed=1;
        //replace the default circle image with an arrow sprite
        //change this path to your real arrow image
        setImage(new GreenfootImage("enemy/skeleton/arrow.png"));
        
        GreenfootImage img = getImage();

        // arrow sprite points LEFT by default
        if (dirX == 1)
        {
            img.mirrorHorizontally(); // RIGHT
            img.scale(45, 10);
        }
        else if (dirX == -1)
        {
            //LEFT
            img.scale(45, 10);
        }
        else if (dirY == -1)
        {
            //UP
            img.rotate(90);
            img.scale(10, 45);
        }
        else if (dirY == 1)
        {
            //DOWN
            img.rotate(270);
            img.scale(10, 45);
        }

        setImage(img);
    }

    /**
     * Hook override: enemy arrow hits Player, not Enemy.
     */
    protected boolean handleHit()
    {
        Player p = (Player) getOneIntersectingObject(Player.class);
        if (p != null)
        {
            p.takeDamage(DAMAGE_LEVEL);

            if (getWorld() != null)
            {
                getWorld().removeObject(this);
            }
            return true;
        }
        return false;
    }
    
    
}