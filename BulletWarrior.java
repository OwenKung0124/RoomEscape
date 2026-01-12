import greenfoot.*;

/**
 * BulletWarrior:
 * - Walks with keyboard
 * - Attacks with SPACE
 * - Shoots a Bullet on the hit frame
 */
public class BulletWarrior extends Player
{
    private boolean lastSpace = false;
    private boolean spaceJustPressed = false;

    public BulletWarrior()
    {
        speed = 4;
        animDelay = 6;

        spriteW = 70;
        spriteH = 80;

        //walking frames
        loadDirectionalFrames("player/bullet_warrior/walking", 4);
        left = mirrorImage(right);

        //attack frames
        loadAttackFrames("player/bullet_warrior/attack", 3); 

        //attack tuning
        atkAnimDelay = 2;
        attackCooldownMax = 10;
        hitFrame = 1;

        dir = DOWN;
        setImage(framesFor(dir)[0]);
    }

    public void act()
    {
        boolean space = Greenfoot.isKeyDown("space");
        spaceJustPressed = space && !lastSpace;
        lastSpace = space;

        super.act();
    }

    protected boolean wantsToAttack()
    {
        return spaceJustPressed;
    }

    protected void onAttackHit()
    {
        if (getWorld() == null) return;

        int dirX = 0, dirY = 0;
        if (dir == UP) dirY = -1;
        else if (dir == DOWN) dirY = 1;
        else if (dir == LEFT) dirX = -1;
        else dirX = 1;

        int reach = 30;
        int bx = getX() + dirX * reach;
        int by = getY() + dirY * reach;
        
        SoundManager.playBulletSound();
        getWorld().addObject(new Bullet(dirX, dirY), bx, by);
    }
}