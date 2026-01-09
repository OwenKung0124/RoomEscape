import greenfoot.*;

/**
 * WanderEnemy walks in random directions and changes direction randomly.
 */
public class WanderEnemy extends Enemy
{
    private WanderAround wonderAround = new WanderAround();

    public WanderEnemy(Player target)
    {
        super(target);
        //must set the speed, otherwise won't move
        speed=4;  
       
    }

    
    /**
     * Computes wandering movement using WanderBrain.
     */
    protected int[] computeMove()
    {
        return wonderAround.nextMove(speed);
    }
}