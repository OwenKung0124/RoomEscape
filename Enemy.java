import greenfoot.*;

/**
 * Enemy is the abastrct base class for all enemies.
 *
 * Subclasses need to implement computeMove().
 *
 * Required images in images/enemy/:
 * up1.png up2.png
 * down1.png down2.png
 * left1.png left2.png
 * right1.png right2.png
 */
public abstract class Enemy extends WalkingActor implements HasHealth
{

    //The player this enemy can interact with 
    //(chase and/or knockback)
    protected Player target;

    //Damage cooldown so enemy doesn't hit every single frame
    protected int hitCooldown = 0;

    //Knockback strength in pixels
    protected int knockBack = 20;

    //Knockback cooldown frames
    protected int hitCooldownFrames = 30;
    
    //damage to the player when this enemy touches them
    protected int contactDamage = 1;
    
    
    //enemy health
    protected int maxHealth = 5;
    protected int health = maxHealth;
    
    //health bar Health bar that follows this enemy
    private HealthBar hpBar;
    protected int HP_BAR_W = 40;
    protected int HP_BAR_H = 8;
   //Positive offset = bar appears UNDER the enemy
    protected int HP_BAR_Y_OFFSET = 35;

    public Enemy(Player target) 
    {
        this.target = target;
        animDelay=8;
        
        //use default sprite size
        //spriteW,spriteH
        spriteW=60;
        spriteH=65;
        

        loadDirectionalFrames("enemy", 2);
        dir = DOWN;
        //initial facing down
        setImage(framesFor(dir)[0]);
    }

    //subclass must implement this method
    protected abstract int[] computeMove();
    /**
     * Creates a health bar that follows underneath the enemy.
     */
    protected void addedToWorld(World world)
    {
        if (hpBar == null)
        {
            //HealthBar(HasHealth unit, Actor follow, int width, int height, boolean followTarget, int yOffset)
            hpBar = new HealthBar(this, this, HP_BAR_W, HP_BAR_H, true, HP_BAR_Y_OFFSET);
    
            //add bar at the correct starting position
            world.addObject(hpBar, getX(), getY() + HP_BAR_Y_OFFSET);
        }
    }
    /**
     * When enemy is removed
     * Ensures its health bar disappears too.
     */
    protected void removedFromWorld(World world)
    {
        if (hpBar != null && hpBar.getWorld() != null)
        {
            world.removeObject(hpBar);
        }
    }
    public void act() 
    {
         //freeze  while paused
        if (GameWorld.isPaused()) return;
    
        if (getWorld() == null)
        {
            return;   
        }
        

        if (hitCooldown > 0)
        {
            hitCooldown--;
        }

        regularMovement();
        
        handlePlayerContact();
    }

    protected void regularMovement()
    {
        
        int[] mv = computeMove(); //subclass decides movement
        int dx = mv[0];
        int dy = mv[1];

        boolean moving = false;
        if (dx != 0 || dy != 0)
        {
            moving=true;
        }

        if (moving) 
        {
            //from WalkingActor parent class
            updateDirectionFromMove(dx, dy);
            avoidWallMoving(dx, dy);
            animate(framesFor(dir));
        } 
        else 
        {
            //idle frame
            frameIndex = 0;
            setImage(framesFor(dir)[0]);
        }
    }
    /**
     * Updates the actor's facing direction based on movement (dx, dy).
     * If the facing direction changes, the animation is reset to frame 0.
     *
     * @param dx change in x (negative = left, positive = right)
     * @param dy change in y (negative = up, positive = down)
     */
    protected void updateDirectionFromMove(int dx, int dy)
    {
        int oldDir = dir;
    
        //decide whether horizontal or vertical movement is "stronger".
        //if |dx| >= |dy|, face LEFT/RIGHT; otherwise face UP/DOWN.
        if (Math.abs(dx) >= Math.abs(dy)) 
        {
            //horizontal direction
            if (dx < 0)
            {
                dir = LEFT;   
            }
            else if (dx > 0)
            {
                dir = RIGHT;   
            }
        }
        else 
        {
            //vertical direction
            if (dy < 0) 
            {
                dir = UP;   
            }
            else if (dy > 0)
            {
                dir = DOWN;   
            }
        }
    
        //If the direction changed
        //restart the animation so it looks clean
        if (dir != oldDir)
        {
            resetAnim();   
        }
    }

    /*/Damages/knocks the player on contact (cooldown-based). */
    protected void handlePlayerContact() 
    {
        if (target == null) 
        {
            return;
        }

        if (isTouching(Player.class) && hitCooldown == 0) 
        {
            hitCooldown = hitCooldownFrames;
            //deal damage to the player (player has its own invincibility too)
            if (target != null)
            {
                target.takeDamage(contactDamage);     
            }
            //distance from enemy -> player
            //int dx = target.getX() - getX();
            //int dy = target.getY() - getY();

            //double dist = Math.sqrt(dx * dx + dy * dy);
            //if (dist < 0.0001) dist = 1;

            //so that player and enemy does not stick
            //int kx = (int) Math.round((dx / dist) * knockBack);
            //int ky = (int) Math.round((dy / dist) * knockBack);

            //knockBack not working very wel yet
            //knockBackPlayer(kx, ky);
        }
    }

    //knockback helper
    //not working very well yet
    protected void knockBackPlayer(int kx, int ky) 
    {
        int oldX = target.getX();
        int oldY = target.getY();

        //x push
        target.setLocation(oldX + kx, oldY);
        //if (target.isTouching(Blocker.class)) target.setLocation(oldX, oldY);

        //y push
        int midX = target.getX();
        target.setLocation(midX, oldY + ky);
        // if (target.isTouching(Wall.class)) target.setLocation(midX, oldY);
    }
    
    //@return enemy current HP
    public int getHealth() 
    { 
        return health; 
    }
    
    //@return enemy max HP
    public int getMaxHealth() 
    { 
        return maxHealth;
    }
    
    /**
     * Damage this enemy. If HP reaches 0, remove it.
     *
     * @param amount damage amount
     */
    public void takeDamage(int amount)
    {
        if (amount <= 0) return;
    
        health -= amount;
        if (health < 0) health = 0;
    
        if (health <= 0 && getWorld() != null)
        {
            getWorld().removeObject(this);
        }
    }
}