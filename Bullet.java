import greenfoot.*;

/**
 * Bullet class
 *
 * What it does:
 * - Moves straight using a velocity (vx, vy)
 * - Disappears if it hits a Blocker (wall)
 * - Disappears if it leaves the room
 * - Destroys the first Enemy it touches, then disappears
 * - Disappears after a short lifetime so it can't exist forever
 */
public class Bullet extends SuperSmoothMover
{
    //bullet velocity
    private int vx;
    private int vy;

    //bullet movement speed
    protected int speed = 8;

    //life time of bullet in frames
    private int life = 60;
    private int attackPower=0;

    /**
     * Constructs a Bullet moving in direction (dirX, dirY).
     *
     * dirX and dirY should be -1, 0, or 1.
     * - (1, 0)  = right
     * - (-1, 0) = left
     * - (0, -1) = up
     * - (0, 1)  = down
     *
     * @param dirX direction in x (-1, 0, 1)
     * @param dirY direction in y (-1, 0, 1)
     */
    public Bullet(int dirX, int dirY,int attackPower)
    {
        //calculate velocity
        vx = dirX * speed;
        vy = dirY * speed;
        this.attackPower=attackPower;

        //Simple circle bullet image
        GreenfootImage img = new GreenfootImage(10, 10);
        img.setColor(Color.WHITE);
        img.fillOval(0, 0, 10, 10);
        setImage(img);
    }

    /**
     * Runs each frame:
     * - move bullet
     * - check collisions
     * - remove bullet if needed
     */
    public void act()
    {
        //freeze while paused 
        if (GameWorld.isPaused()) return;
if (!GameWorld.allowSlowUpdate()) return;
        //removed already, do nothing
        if (getWorld() == null) return;

        //Move straight
        setLocation(getX() + vx, getY() + vy);

        //1) Remove if hit a wall (Blocker includes walls/statues/etc)
        if (isTouching(Blocker.class))
        {
            getWorld().removeObject(this);
            return;
        }

        //remove if outside the room rectangle
        //prevents bullets from flying into the side panel forever.
        World ww = getWorld();
        if (ww instanceof GameWorld)
        {
            if (getX() < GameConfig.roomLeft() || getX() > GameConfig.roomRight()
             || getY() < GameConfig.roomTop()  || getY() > GameConfig.roomBottom())
            {
                getWorld().removeObject(this);
                return;
            }
        }

        //enemy hit
        if (handleHit()) 
        {
             return;   
        }

        //lifetime countdown (auto-remove after life frames)
        life--;
        if (life <= 0 && getWorld() != null)
        {
            getWorld().removeObject(this);
        }
    }
    /**
     * Hook for handling what the bullet hits.
     * Default behavior: destroy the first Enemy it touches.
     * Subclasses can override this to hit Player instead (enemy projectiles).
     *
     * @return true if the bullet handled a hit and should stop acting this frame
     */
    protected boolean handleHit()
    {
        Enemy e = (Enemy) getOneIntersectingObject(Enemy.class);
        if (e != null)
        {
            //the takeDamage, will handle remove enemy when health<=0
            e.takeDamage(attackPower);
            getWorld().removeObject(this);
            return true;
        }
        return false;
    }
}