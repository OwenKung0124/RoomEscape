import greenfoot.*;

/**
 * zombieEnemy moves toward the player.
 * 
 * @author:Owen Kung, Clifton Lin
 * @version:2026 Jan
 */
public class ZombieEnemy extends Enemy 
{
    /**
     * Creates a chasing enemy.
     *
     * @param target the Player to chase
     */
    public ZombieEnemy(Player target) 
    {
        super(target);
        speed=2;
        hitCooldownFrames=90; //when in contact only damages every 90 frames, same as attack sound effect
        loadDirectionalFrames("enemy/zombie", 4);
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
        if (dist < 2)
        {
            return new int[]{0, 0};   
        }

        int movX = (int)Math.round((dx / dist) * speed);
        int movY = (int)Math.round((dy / dist) * speed);

        return new int[]{movX, movY};
    }
    protected void playAttackSoundEffect()
    {
        SoundManager.playZombieSound();
    }
    protected void playEndOfLifeSoundEffect()
    {
        //SoundManager.playZombieSound();
    }
}