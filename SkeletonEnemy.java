import greenfoot.*;

/**
 * AttackingWonderEnemy:
 * - wanders around using WanderAround
 * - can also attack with attack animation (handled by AttackingEnemy)
 * - attacks every 2–5 seconds (random) while the player is in range
 *
 * NOTE:
 * - Random attack timing (2–5s) is already handled by your AttackingEnemy class
 *   using attackTimer + scheduleNextAttack().
 * - This class only provides: wandering movement + attack conditions + hit effect.
 */
public class SkeletonEnemy extends AttackingEnemy
{
    //wander logic (composition)
    private WanderAround wonderAround = new WanderAround();

    //attack settings
    private int attackRange = 920;   //start attacking if player is within this distance
    private int attackDamage = 1;    //damage on hitFrame

    //melee hit check settings (hit point in front of enemy)
    //private int meleeReach = 35;     //how far in front the hit point is
    //private int meleeRadius = 30;    //how close player must be to that point

    public SkeletonEnemy(Player target)
    {
        super(target);

        speed = 2;
        spriteW=60;
        spriteH=70;

        //enemy HP (make sure health matches maxHealth)
        maxHealth = 5;
        health = maxHealth;

        //attack animation timing (you can tweak)
        atkAnimDelay = 2;
        hitFrame = 4;

        //optional: keep cooldown, but timer already spaces attacks out
        //you can set this to 0 if you only want timer-based spacing
        attackCooldownMax = 0;

        //2–5 seconds random attack interval (frames)
        attackIntervalMin = 120; //2s
        attackIntervalMax = 300; //5s
        scheduleNextAttack();    //start with a random delay

        //walking frames:
        loadDirectionalFrames("enemy/skeleton/walking", 4);

        //attack frames
        loadAttackFrames("enemy/skeleton/attack", 12);
    }

    /**
     * Computes wandering movement using WanderAround.
     */
    protected int[] computeMove()
    {
        return wonderAround.nextMove(speed);
    }

    /**
     * AI rule: only attack if player is close enough.
     * (Timing is controlled by attackTimer in AttackingEnemy.)
     */
    protected boolean wantsToAttack()
    {
        //System.out.println("wants to attck");
        
        if (target == null || target.getWorld() == null) return false;

        int dx = target.getX() - getX();
        int dy = target.getY() - getY();

        return (dx * dx + dy * dy) <= (attackRange * attackRange);
    }
    protected boolean wantsToAttack_for_testing()
    
    {
        return true;
    }
    /**
     * Attack effect at hitFrame:
     */
    protected void onAttackHit_old()
    {
        if (getWorld() == null) return;
    
        int dirX = 0;
        int dirY = 0;
    
        if (dir == UP) dirY = -1;
        else if (dir == DOWN) dirY = 1;
        else if (dir == LEFT) dirX = -1;
        else dirX = 1;
    
        int offset = 25;
        int sx = getX() + dirX * offset;
        int sy = getY() + dirY * offset;
    
        getWorld().addObject(new EnemyArrow(dirX, dirY), sx, sy);
    }
    protected void onAttackHit()
{
    if (getWorld() == null) return;

    int dirX = 0;
    int dirY = 0;

    if (dir == UP) dirY = -1;
    else if (dir == DOWN) dirY = 1;
    else if (dir == LEFT) dirX = -1;
    else dirX = 1;

    // create arrow first so we can use its image size
    EnemyArrow arrow = new EnemyArrow(dirX, dirY);

    int pad = 4;

    // offset based on BOTH enemy size and arrow size (prevents overlap)
    int ox = 0;
    int oy = 0;

    if (dirX != 0)
    {
        ox = (getImage().getWidth() / 2) + (arrow.getImage().getWidth() / 2) + pad;
    }
    if (dirY != 0)
    {
        oy = (getImage().getHeight() / 2) + (arrow.getImage().getHeight() / 2) + pad;
    }

    int sx = getX() + dirX * ox;
    int sy = getY() + dirY * oy;

    // clamp spawn inside the room bounds so Bullet won't delete it instantly
    int halfW = arrow.getImage().getWidth() / 2;
    int halfH = arrow.getImage().getHeight() / 2;

    sx = Math.max(GameConfig.roomLeft() + halfW,  Math.min(GameConfig.roomRight() - halfW, sx));
    sy = Math.max(GameConfig.roomTop()  + halfH,  Math.min(GameConfig.roomBottom() - halfH, sy));

    getWorld().addObject(arrow, sx, sy);
}
}