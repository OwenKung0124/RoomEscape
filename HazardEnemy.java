import greenfoot.*;

/**
 * HazardEnemy:
 *  moves around in a dodge room
 *  if player touches the Hazard, the timer resets
 *  it does not damage the player
 *  it does not have health, as it does not combat
 *  it does not take dmage from the player
 *  
 *  @author
 *  @version:
 */
public class HazardEnemy extends Enemy
{
    private WanderAround wander = new WanderAround(20, 60);

    //hit cooldown so it doesn't reset timer every single frame
    private int touchCooldown = 0;
    private int touchCooldownMax = 30; 

    public HazardEnemy(Player target)
    {
        super(target);

        //moving speed, make it fast
        speed=5;
        animDelay=6;
        
        //image size
        spriteW = 60;
        spriteH = 70;

        //load animation frames
        loadDirectionalFrames("enemy", 2);

        //hazard should not hurt player
        contactDamage = 0;

        //still takes freeze from player's stone
    }

    /**
     * Hazard wandering movement.
     * uses wander logic
     */
    protected int[] computeMove()
    {
        return wander.nextMove(speed);
    }

    /**
     * Override Enemy contact logic:
     *  so no damage
     * - notify GameWorld to reset the dodge timer
     */
    protected void handlePlayerContact()
    {
        if (touchCooldown > 0)
        {
            touchCooldown--;   
        }

        if (touchCooldown == 0 && isTouching(Player.class))
        {
            touchCooldown = touchCooldownMax;
            World w = getWorld();
            if (w instanceof GameWorld)
            {
              ((GameWorld) w).onDodgeRoomHit();
            }
        }
    }

    /**
     * Prevent Enemy from creating a HP bar.
     */
    protected void addedToWorld(World world)
    {
        //do nothing on purpose (don't call super)
    }

    /**
     * Prevent Enemy HP bar cleanup logic (we never created one anyway).
     */
    protected void removedFromWorld(World world)
    {
        // do nothing
    }

    /**
     * Hazard cannot be damaged.
     */
    public void takeDamage(int amount)
    {
        // do nothing
    }

    /**
     * does not attack
     */
    protected void playAttackSoundEffect()
    {
        // no sound
    }

    /**
     * no end of life
     */
    protected void playEndOfLifeSoundEffect()
    {
        // no sound
    }
}