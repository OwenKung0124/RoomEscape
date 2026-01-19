
import greenfoot.*;

/**
 *  Axe Warrior uses Axe to kill enemy
 */
public class AxeWarrior extends Player
{
    //space"just presse detection
    private boolean lastSpace=false;
    private boolean spaceJustPressed=false;

    public AxeWarrior()
    {
        //movement
        speed=4;
        
        //attackPower default
        attackPower=GameConfig.WARRIOR_AXE_DEFAULT_ATTACK;
        
        //walk animation timing
        animDelay=6;

        //sprite size
        spriteW=80;
        spriteH=85;

        //walk frames
        loadDirectionalFrames("player/axe_warrior/walking", 4);  //more than 4, the axe warrior's horizontal movement looks weird
        left=mirrorImage(right);

        //attack frames
        loadAttackFrames("player/axe_warrior/attack", 5);

        //attack tuning
        atkAnimDelay=5;         //smaller=faster swing
        attackCooldownMax=12;   //bigger=slower swing rate
        hitFrame=2;             //damage happens on this frame

        //default facing
        dir=DOWN;
        setImage(framesFor(dir)[0]);
    }

    public void act()
    {
        //space just pressed detection
        //so holding space does not cause multiple attack
        boolean space=Greenfoot.isKeyDown("space");
        spaceJustPressed=space && !lastSpace;
        lastSpace=space;

        super.act();
    }
    /**
     * Start attack when SPACE is pressed.
     */
    protected boolean wantsToAttack()
    {
        return spaceJustPressed;
    }

    /**
     * Spawn a short-lived hitbox in front of the player on the hit frame.
     */
    protected void onAttackHit()
    {
        if (getWorld()==null) return;


        //hitbox size
        int boxW=55;
        int boxH=45;

        //distance
        int distance=35;

        int offsetx=0;
        int offsety=0;

        if (dir==UP)
        {
            offsety=-distance;
        }
        else if (dir==DOWN)
        {
            offsety=distance;
        }
        else if (dir==LEFT)
        {
            offsetx=-distance;
        }
        else //RIGHT
        {
            offsetx=distance;
        }

        //put a short-lived hitbox 
        HitBox hitbox=new HitBox(boxW, boxH, 1,getAttackPower());
        SoundManager.playAxeSound();
        getWorld().addObject(hitbox, getX() + offsetx, getY() + offsety);
    }
}