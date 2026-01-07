import greenfoot.*;

/**
 * ChaseEnemy moves toward the player.
 */
public class ChaserEnemy extends Enemy {



    /**
     * Creates a chasing enemy.
     *
     * @param target the Player to chase
     */
    public ChaserEnemy(Player target) 
    {
        super(target);
        speed=2;
    }

    /**
     * Computes movement toward the player.
     *
     * @return {dx, dy}
     */
    protected int[] computeMove() 
    {
        if (player==null)
        {
            return new int[]{0, 0};
        }

        int dx =player.getX() - getX();
        int dy =player .getY() - getY();

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.0001)
        {
            return new int[]{0, 0};   
        }

        int mx = (int)Math.round((dx / dist) * speed);
        int my = (int)Math.round((dy / dist) * speed);

        return new int[]{mx, my};
    }
}