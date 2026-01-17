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
        animDelay = 6;

        spriteW = 70;
        spriteH = 80;

        //walking frames
        loadDirectionalFrames("enemy/wander/", 4);
        left = mirrorImage(right);

        //attack frames
        //loadAttackFrames("player/bullet_warrior/attack", 3); 
        
        dir = DOWN;
        setImage(framesFor(dir)[0]);
       
    }

    /**
     * Computes wandering movement using WanderBrain.
     */
    protected int[] computeMove()
    {
        return wonderAround.nextMove(speed);
    }
    protected void playAttackSoundEffect()
    {
        //SoundManager.playZombieSound();
    }
    protected void playEndOfLifeSoundEffect()
    {
        //SoundManager.playZombieSound();
    }
}