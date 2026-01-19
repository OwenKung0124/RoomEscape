import greenfoot.*;
import java.util.ArrayList;

/**
 * Player
 * - walk using WalkingActor's movement/facing/walk animation
 * - play a directional attack animation
 * - trigger a hit once at a chosen frame
 *
 * Subclass constructor call:
 *      loadDirectionalFrames()
 *      loadAttackFrames()
 *  Subclass implement wantsToAttack()
 *  Subclass implement onAttackHit()
 */
public abstract class Player extends CombatActor
{
    //health and damges
    //Maximum health the player can have. */
    protected int maxHealth = GameConfig.DEFAULT_MAX_HP;
    protected int health = maxHealth;
    
    //attack upgrade
    protected int attackPower = 1;
    
    //While > 0, 
    //the player cannot take damage again
    protected int hurtCooldown = 0;
     //how many frames before another damage can count 
    protected int hurtCooldownFrames = GameConfig.DEFAULT_INVINCIBILITY_FRAMES;
    
    //keep track of coins collected
    private int coins=0;
    private int score=0;
    
    //stone related fields
    private int stoneSkills=GameConfig.DEFAULT_STONE_COUNT;  //default
    private boolean keyWasDown = false;
    

    /**
     * Subclass can override
     * 
     * Parant class provides default movement
     *
     * @return int[]{dx, dy}
     */
    protected int[] computeMove()
    {
        if (attacking) 
        {
            return new int[] {0, 0};   
        }

        int dx = 0;
        int dy = 0;

        boolean leftKey  = Greenfoot.isKeyDown("left")  || Greenfoot.isKeyDown("a");
        boolean rightKey = Greenfoot.isKeyDown("right") || Greenfoot.isKeyDown("d");
        boolean upKey    = Greenfoot.isKeyDown("up")    || Greenfoot.isKeyDown("w");
        boolean downKey  = Greenfoot.isKeyDown("down")  || Greenfoot.isKeyDown("s");

        if (leftKey)
        {
            dx -= speed;   
        }
        if (rightKey)
        {
            dx += speed;
        }
        if (upKey)
        {
            dy -= speed;   
        }
        if (downKey) 
        {
            dy += speed;   
        }

        return new int[] {dx, dy};
    }
    

    /**
     * Subclass decides when to start an attack.
     *
     * @return true if attack should begin this frame
     */
    protected abstract boolean wantsToAttack();

    /**
     * Called exactly once during the attack animation at hitFrame.
     * Subclass defines the attack logic (melee hitbox, projectile, etc.)
     */
    protected abstract void onAttackHit();

    public void act()
    {
        //freeze while paused 
        if (GameWorld.isPaused()) return;
        
        //freeze player while PromptManager is waiting for a choice
        GameWorld gw = (GameWorld) getWorld();
        if (gw != null)
        {
            PromptManager pm = gw.getPromptManager(); 
            if (pm != null && pm.isActive() )
            {
                return;
            }
        }

        //reduce invincibility timer each frame
        if (hurtCooldown > 0) hurtCooldown--;
        
        //cooldown countdown
        if (attackCooldown > 0)
        {
            attackCooldown--;
        }

        //if attacking, only play attack animation
        if (attacking)
        {
            doAttackAnim();
            return;
        }

        //movement comes from subclass
        int[] mv = computeMove();
        int dx = mv[0];
        int dy = mv[1];

        boolean moving = (dx != 0 || dy != 0);

        //update facing direction
        int newDir = dir;
        if (dx < 0) 
        {
            newDir = LEFT;   
        }
        else if (dx > 0)
        {
            newDir = RIGHT;   
        }
        else if (dy < 0)
        {
            newDir = UP;   
        }
        else if (dy > 0) 
        {
            newDir = DOWN;   
        }

        if (newDir != dir)
        {
            dir = newDir;
            resetAnim();
        }

        //move and animate
        if (moving)
        {
            avoidWallMoving(dx, dy);
            animate(framesFor(dir));
            collect();  //keep track of collectibles
        }
        else
        {
            frameIndex = 0;
            setImage(framesFor(dir)[0]);
        }

        //start attack?
        if (attackCooldown == 0 && wantsToAttack())
        {
            startAttack();
        }
        
        //if k is pressed, trigger stone skill
        boolean keyDown = Greenfoot.isKeyDown("k"); 
        if (keyDown && !keyWasDown)
        {
            useStone();
        }
        keyWasDown = keyDown;
        
        //visual feedback to indicate player is hurt
        //once player got hit, it will flicker
        //to indicate not taking damage while flickering
        //for continuous touching, it would look like it takes damage while flickering
        if (hurtCooldown > 0)
        {
            if (hurtCooldown % 6 < 3) getImage().setTransparency(120);
            else getImage().setTransparency(255);
        }
        else
        {
            getImage().setTransparency(255);
        }

    }
    private void useStone()
    {
        
        if(stoneSkills<1)
        {
            if(getWorld()!=null)
            {
               // ((GameWorld)getWorld()).getPromptManager().showMessage("No More Stone Skills.",60);
                //((GameWorld)getWorld()).getPromptManager().showMessage("No More Stone Skills.",60);
                ((GameWorld)getWorld()).showMessage("No More Stone Skills.",60);
            }
            
           return;
        }
        //deduct
        stoneSkills--;
        GameWorld.numOfStoneUsed++;  //keep track
        
        ArrayList<Enemy> enemies= (ArrayList<Enemy>) getWorld().getObjects(Enemy.class);
        for(Enemy e: enemies)
        {
            e.freeze(GameConfig.STONE_TIME);//freeze for 120 frames
        }
        
    }
    /**
     * @return the player's current health
     */
    public int getHealth()
    {
        return health;
    }
    
    /**
     * @return the player's maximum health.
     */
    public int getMaxHealth()
    {
        return maxHealth;
    }
    
    /**
     * Heals the player by a given amount
     * without exceeding maxHealth.
     *
     * @param amount how much to heal (ignored if <= 0)
     */
    public void heal(int amount)
    {
        if (amount <= 0) return;
         //allow players to help unlimited health
        health += amount;

    }
    /**
     * setHealth of the player when resume the game
     */
    public void setHealth(int amount)
    {
        if (amount <= 0) return;
    
        //allow players to help unlimited health
        health=amount;

    }
    /**
     * Damages the player by a given amount.
     * Uses invincibility frames (hurtCooldown) to prevent rapid repeat damage.
     * If health reaches 0, the player dies and the game stops.
     *
     * @param amount how much damage to take (ignored if <= 0)
     */
    public void takeDamage(int amount)
    {
        if (amount <= 0) return;
    
        
        //if invincibility is active, ignore this damage
        if (hurtCooldown > 0)
        {
            //System.out.println("BLOCKED hit! hurtCooldown=" + hurtCooldown);
            return;
        }
    
        //start invincibility frames
        hurtCooldown = hurtCooldownFrames;
        //System.out.println("TOOK hit! amount=" + amount + " health(before)=" + health);
        
        //apply damage
        health -= amount;
        if (health < 0) health = 0;
    
        //if dead, end the game
        if (health <= 0)
        {
            World w = getWorld();
            
            if (w !=null)
            {
                ((GameWorld) w).onPlayerDefeated();
            }
            else
            {
                Greenfoot.setWorld(new DefeatWorld(null));
            }
        }
    }
    /*
     * keep track of collectible itemes
     */
    private void collect()
    {
        //collect any collectibles
        //player could touch many coins at the same time
        ArrayList<Coin> coinsTouched = (ArrayList<Coin>)getIntersectingObjects(Coin.class);
        if(coinsTouched!=null)
        {
            coins=coins+coinsTouched.size();
        }  
        if(isTouching(AttackUpgrade.class))
        {
          //handled in AttackUpgrade
          //the PrompManager will call upgradeAttackPower
          //if player wants to upgrade
        } 
        if(isTouching(HealthUpgrade.class))
        {
          //handled in HealthUpgrade
          //the PrompManager will call upgradeHealthPower
          //if player wants to upgrade
        } 
        if(isTouching(StoneSkill.class))
        {
          //handled in StoneSkill
          //the PrompManager will call upgradeStoneSkills
          //if player wants to upgrade
        } 
    }
    public int getCoinCount()
    {
        return coins;
    }
    public void setCoinCount(int coinCount)
    {
        coins=coinCount;
    }
    public int getScore()
    {
        return score;
    }
    public void setScore(int score)
    {
        this.score=score;
    }
    public void addScore(int score)
    {
        this.score+=score;
    }

    public int getAttackPower()
    {
        return attackPower;
    }
    public void setAttackPower(int attackPower)
    {
        this.attackPower=attackPower;
    }
    public int getStoneSkillCount()
    {
        return stoneSkills;
    }
     public void setStoneSkillCount(int count)
    {
        stoneSkills=count;
    }
    /**
     * Logic to upgrade Player's attack power
     * if successfully upgraded return true
     * 
     * @param upgrade:              amount of attack power to upgrade
     * @param scoresTakes:          amount of score needed to upgrade
     * @return upgradeAttackPower:  returns true if upgrade is scccessful
     */
    public boolean upgradeAttackPower(int upgrade, int scoresTaken)
    {
                                    
        //not enough score to upgrade
        if(scoresTaken>score)
        {
            return false;
        }
        
        //must be above this level to upgrade
        if(score<GameConfig.MINIMUM_HP_TO_UPGRADE)
        {
            return false;
        }
        
        //upgrade requires score to upgrade
        score=score-scoresTaken;
        
        //attack power upgrade
        attackPower+=upgrade;
        
        SoundManager.playCoinSound();
        showText("Successfully Upgraded",Color.GREEN,getX(),getY());
        
        return true;

    }
    /**
     * Logic to upgrade Player's health power
     * if successfully upgraded return true
     * 
     * @param upgrade:              amount of health power to upgrade
     * @param scoresTakes:          amount of coins needed to upgrade
     * @return upgradeAttackPower:  returns true if upgrade is scccessful
     */
    public boolean upgradeHealthPower(int upgrade, int coinsTaken)
    {
                                    
        //not enough score to upgrade
        if(coinsTaken>coins)
        {
            return false;
        }
        
        //deduct coins
        coins=coins-coinsTaken;
        
        //heal
        heal(upgrade);
        
        SoundManager.playHealthSound();
        showText("Successfully Healed",Color.GREEN,getX(),getY());
        
        return true;

    }
    /**
     * Logic to aquire Player's stone skill
     * if successfully upgraded return true
     * 
     * @param upgrade:              amount of stone skill to aquire
     * @param scoresTakes:          amount of coins needed to upgrade
     * @return upgradeAttackPower:  returns true if upgrade is scccessful
     */
    public boolean aquireStoneSkill(int upgrade, int coinsTaken)
    {
                                    
        //not enough score to upgrade
        if(coinsTaken>coins)
        {
            return false;
        }
        
        //deduct coins
        coins=coins-coinsTaken;
        
        //stoneSkill aquired
        stoneSkills++;
        
        SoundManager.playCoinSound();
        showText("Successfully Aquired Stone Skill",Color.GREEN,getX(),getY());
        
        return true;

    }
    /**
     * helper method for gameworld, used in trap room
     * 
     * @return if player touches the door
     * 
     */
    public boolean touchingDoor()
    {
        return isTouching(Door.class);
    }
    
}