import greenfoot.*;

/**
 * SkeletonEnemy:
 *  extends Enemy (movement/HP/contact damage)
 *  also attacks (uses CombatActor's attack animation system)
 *  wanders using WanderAround
 *  shoots an EnemyArrow on hitFrame
 *  
 * It wanders around like Wander Enemy but fire arrow at a random time interval and when facing target.
 *  
 *  @author:Owen Kung
 *  @version:Jan 2026
 */
public class SkeletonEnemy extends Enemy
{
    //wander logic
    private WanderAround wonderAround=new WanderAround();

    //attack settings
    private int attackRange=200;

    //random attack interval in frames
    private int attackIntervalMin=120; 
    private int attackIntervalMax=240; 
    private int attackTimer=0;
    private int attackPower=1;

    public SkeletonEnemy(Player target)
    {
        super(target);

        speed=2;
        spriteW=60;
        spriteH=70;

        //enemy HP
        //maxHealth=5;
        //health=maxHealth;

        //attack animation timing
        atkAnimDelay=2;
        hitFrame=4;

        //keep cooldown 0
        attackCooldownMax=0;

        //walking frames
        loadDirectionalFrames("enemy/skeleton/walking", 4);

        //attack frames
        loadAttackFrames("enemy/skeleton/attack", 12);

        scheduleNextAttack();
    }

    public void act()
    {
        if (GameWorld.isPaused()) return;
        if (getWorld() == null) return;
        if (player == null || player.getWorld() == null) return;

        //countdown freeze
        //stop movement  contact damage while frozen
        if (freezeTimer > 0)
        {
            showText("Stoned",Color.YELLOW,getX(),getY()+50,false);
            freezeTimer--;
            return; 
        }
        
        //Enemy contact cooldown
        if (hitCooldown > 0) hitCooldown--;

        //Combat cooldown
        if (attackCooldown > 0) attackCooldown--;

        //random interval countdown
        if (attackTimer > 0) attackTimer--;

        //if attacking: only animate attack
        if (attacking)
        {
            doAttackAnim();
            return;
        }

        //normal movement + touch damage
        regularMovement();
        handlePlayerContact();

        //start attack?
        if (attackCooldown == 0 && attackTimer == 0 && wantsToAttack())
        {
            startAttack();
        }
    }

    /**
     * Wandering movement.
     */
    protected int[] computeMove()
    {
        return wonderAround.nextMove(speed);
    }

    /**
     * Only attack if player is within range.
     */
    private boolean wantsToAttack()
    {
        int dx=player.getX() - getX();
        int dy=player.getY() - getY();
        return (dx * dx + dy * dy) <= (attackRange * attackRange);
    }

    /**
     * Start attack: face player, then use CombatActor attack animation,
     * then schedule next random attack.
     */
    protected void startAttack()
    {
        faceTarget();
        super.startAttack();
        scheduleNextAttack();
    }

    /**
     * Called exactly once on hitFrame by CombatActor
     */
    protected void onAttackHit()
    {
        if (getWorld() == null) return;

        int dirX=0;
        int dirY=0;

        if (dir == UP) dirY=-1;
        else if (dir == DOWN) dirY=1;
        else if (dir == LEFT) dirX=-1;
        else dirX=1;

        int offset=25;
        int sx=getX() + dirX * offset;
        int sy=getY() + dirY * offset;

        SoundManager.playArrowSound();
        getWorld().addObject(new EnemyArrow(dirX, dirY,getAttackPower()), sx, sy);
    }

    private void scheduleNextAttack()
    {
        int min=Math.min(attackIntervalMin, attackIntervalMax);
        int max=Math.max(attackIntervalMin, attackIntervalMax);
        attackTimer=min + Greenfoot.getRandomNumber(max - min + 1);
    }
    private int getAttackPower()
    {
        if(getWorld()==null)
        {
            return attackPower;
        }
        int difficultyLevel=((GameWorld) getWorld()).difficultyLevel();
         
        if (difficultyLevel == 0) 
        {
            return attackPower;
        }
        if (difficultyLevel == 1)
        {
            return attackPower*2;
        }
        
        if (difficultyLevel == 2)
        {
            return attackPower*3;   
        }
   
        return attackPower*4;
    }
    private void faceTarget()
    {
        int dx=player.getX() - getX();
        int dy=player.getY() - getY();

        if (Math.abs(dx) >= Math.abs(dy))
        {
            dir=(dx < 0) ? LEFT : RIGHT;
        }
        else
        {
            dir=(dy < 0) ? UP : DOWN;
        }
    }
    protected void playAttackSoundEffect()
    {
        //SoundManager.playArrowSound();
    }
    protected void playEndOfLifeSoundEffect()
    {
        //SoundManager.playZombieSound();
    }
}