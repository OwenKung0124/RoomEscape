import greenfoot.*;

/**
 *  Sword Warrior:
 *      attacks with sword
 */
public class SwordWarrior extends Player
{
    //space pressed detection
    private boolean lastSpace = false;
    private boolean spaceJustPressed = false;

    public SwordWarrior()
    {
        //movement
        speed = 4;

        //walk animation timing
        animDelay = 6;

        //sprite size
        spriteW = 70;
        spriteH = 75;

        //walk frames
        loadDirectionalFrames("player/sword_warrior/walking", 4);
        left = mirrorImage(right);

        //attack frames
        loadAttackFrames("player/sword_warrior/attack", 10);

        //attack setting
        atkAnimDelay = 3;         //smalle, faster attack movement
        attackCooldownMax = 12;   //bigger, slower swing rate
        hitFrame = 2;             //damage happens on this frame

        //default facing
        dir = DOWN;
        setImage(framesFor(dir)[0]);
    }

    public void act()
    {
        //space just pressed detection
        //so holding space does not trigger multiple attack
        boolean space = Greenfoot.isKeyDown("space");
        spaceJustPressed = space && !lastSpace;
        lastSpace = space;

        super.act();
    }
    /**
     * start attack when space is pressed.
     */
    protected boolean wantsToAttack()
    {
        return spaceJustPressed;
    }

    /**
     * puts a short-lived hitbox in front of the warrior on the hit frame.
     */
    protected void onAttackHit()
    {
        if (getWorld() == null) return;

        //hitbox size (tune these)
        int boxW = 55;
        int boxH = 45;

        //how far in front of the player the hitbox is placed
        int distance = 35;

        int offSetX = 0;
        int offSetY = 0;

        if (dir == UP)
        {
            offSetY = -distance;
        }
        else if (dir == DOWN)
        {
            offSetY = distance;
        }
        else if (dir == LEFT)
        {
            offSetX = -distance;
        }
        else //RIGHT
        {
            offSetX = distance;
        }

        //puts a short-lived hitbox
        HitBox hitbox = new HitBox(boxW, boxH, 3);
        getWorld().addObject(hitbox, getX() + offSetX, getY() + offSetY);
    }
}