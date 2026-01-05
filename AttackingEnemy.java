import greenfoot.*;

/**
 * AttackingEnemy is an Enemy that can:
 * - play a one-shot directional attack animation (up/down/left/right)
 * - trigger an attack effect exactly once at a chosen animation frame (hitFrame)
 *
 * Subclasses must implement:
 * - wantsToAttack(): when AI decides to start attacking
 * - onAttackHit(): what happens at hitFrame (melee damage / shoot bullet / etc.)
 *
 * Movement is still controlled by Enemy.computeMove() in subclasses.
 */
public abstract class AttackingEnemy extends Enemy
{
    //attack images
    protected GreenfootImage[] atkUp;
    protected GreenfootImage[] atkDown;
    protected GreenfootImage[] atkLeft;
    protected GreenfootImage[] atkRight;

    //attack states
    //true while playing attack animation
    protected boolean attacking = false;

    //Attack animation speed control (ticks)/
    protected int atkAnimTick = 0;

    //how many act() calls per attack frame. Smaller = faster
    protected int atkAnimDelay = 2;

    //current attack frame index (0-based)
    protected int atkFrameIndex = 0;

    //frame index where the hit/effect happens (0-based)
    protected int hitFrame = 4;

    /** Ensures onAttackHit() runs only once per attack. */
    protected boolean hitDone = false;

    //attack cool down
    //current remaining cooldown frames
    protected int attackCooldown = 0;

    //cooldown applied after an attack finishes
    protected int attackCooldownMax = 80;

    //2–5 seconds in frames (assuming ~60 acts/sec)
    protected int attackIntervalMin = 120; 
    protected int attackIntervalMax = 300; 

    //countdown until next allowed attack start
    protected int attackTimer = 0;

    public AttackingEnemy(Player target)
    {
        super(target);
        scheduleNextAttack(); //start with a random delay (2–5s)
    }

    /**
     * Loads directional attack frames from a folder prefix:
     *   folder + "/up1..N.png"
     *   folder + "/down1..N.png"
     *   folder + "/right1..N.png"
     * Left is auto-mirrored from right.
     *
     * Example: loadAttackFrames("enemy/skeleton/attack", 6);
     */
    protected void loadAttackFrames(String folder, int frameCount)
    {
        atkUp    = loadFramesRequired(folder + "/up", frameCount);
        atkDown  = loadFramesRequired(folder + "/down", frameCount);
        atkRight = loadFramesRequired(folder + "/right", frameCount);
        atkLeft  = mirrorImage(atkRight);
    }

    /**
     * Picks the next random time until we can start another attack.
     * Range: [attackIntervalMin, attackIntervalMax] inclusive.
     */
    protected void scheduleNextAttack()
    {
        int min = Math.min(attackIntervalMin, attackIntervalMax);
        int max = Math.max(attackIntervalMin, attackIntervalMax);

        int span = max - min + 1;
        attackTimer = min + Greenfoot.getRandomNumber(span);
    }

    //AI rule: should we start an attack now? */
    protected abstract boolean wantsToAttack();

    //attack effect at hitFrame
    protected abstract void onAttackHit();

    /**
     * Overridden act():
     * - If attacking: play attack animation only
     * - Else: do normal Enemy movement/animation, then maybe start attack
     */
    public void act()
    {
        if (GameWorld.isPaused()) return;
        if (getWorld() == null) return;

        // if player is gone, do nothing
        if (target == null || target.getWorld() == null) return;

        //contact cooldown in Enemy
        if (hitCooldown > 0) hitCooldown--;

        //attack cooldown
        if (attackCooldown > 0) attackCooldown--;

        //random interval countdown
        if (attackTimer > 0) attackTimer--;

        //if currently attacking, do not wander
        //just animate attack
        if (attacking)
        {
            doAttackAnim();
            return;
        }

        regularMovement();

        //rouch-damage
        handlePlayerContact();

        //start attack if ready
        if (attackCooldown == 0 && attackTimer == 0 && wantsToAttack())
        {
            startAttack();
        }
    }

    /**
     * Starts a new attack:
     * - face player
     * - reset attack animation indexes
     */
    protected void startAttack()
    {
        faceTarget4Dir();

        attacking = true;
        atkAnimTick = 0;
        atkFrameIndex = 0;
        hitDone = false;

        GreenfootImage[] frames = attackFramesFor(dir);
        if (frames != null && frames.length > 0)
        {
            setImage(frames[0]);
        }

        //pick next random delay (2–5s) after starting this attack
        scheduleNextAttack();
    }

    /**
     * Plays the one-shot attack animation.
     * Calls onAttackHit() once at hitFrame.
     */
    protected void doAttackAnim()
    {
        GreenfootImage[] frames = attackFramesFor(dir);
        if (frames == null || frames.length == 0)
        {
            // No frames => cancel safely
            attacking = false;
            attackCooldown = attackCooldownMax;
            setImage(framesFor(dir)[0]);
            return;
        }

        // Trigger attack effect ONCE at hitFrame
        int safeHit = Math.min(hitFrame, frames.length - 1);
        if (!hitDone && atkFrameIndex == safeHit)
        {
            hitDone = true;
            onAttackHit();
        }

        //change animation based on delay
        atkAnimTick++;
        if (atkAnimTick < atkAnimDelay)
        {
            setImage(frames[atkFrameIndex]);
            return;
        }

        atkAnimTick = 0;
        atkFrameIndex++;

        //attack when frames finished
        if (atkFrameIndex >= frames.length)
        {
            attacking = false;
            attackCooldown = attackCooldownMax;
            resetAnim(); // reset walking animation
            setImage(framesFor(dir)[0]);
            return;
        }

        setImage(frames[atkFrameIndex]);
    }

    //Returns attack frames array matching current direction
    protected GreenfootImage[] attackFramesFor(int direction)
    {
        if (direction == UP) return atkUp;
        if (direction == DOWN) return atkDown;
        if (direction == LEFT) return atkLeft;
        return atkRight;
    }

    //Face player in 4-dir (UP/DOWN/LEFT/RIGHT). */
    protected void faceTarget4Dir()
    {
        int dx = target.getX() - getX();
        int dy = target.getY() - getY();

        if (Math.abs(dx) >= Math.abs(dy))
        {
            dir = (dx < 0) ? LEFT : RIGHT;
        }
        else
        {
            dir = (dy < 0) ? UP : DOWN;
        }
    }
}