import greenfoot.*;

/**
 * A stationary boss that periodically performs a "summon" animation.
 *
 * Behaviour:
 *  The boss does not move
 *  After a random interval, it starts a summon animation.
 *  When the summon animation finishes, it spawns a small wave of enemies
 *    in the current room using SpawnerSystem.randomFloomSpawnInRoom(RoomData).
 *  It then returns to an idle state and waits for the next interval.
 *
 */
public class SummonerBoss extends Enemy
{
   
    //summon timer setting
    private int summonTimer;
    //inimum frames to wait before summoning again
    private int minInterval=180; 
    //maximum frames to wait before summoning again.
    private int maxInterval=300; 

    //animations
    private GreenfootImage[] summonFrames;
    private int summonAnimDelay=35;  //adjust this to match the sound effect timing

    //summoning state
    private boolean summoning=false;

    //How many minions to spawn 
    //after each summon animation finishes
    private int minionsPerSummon=GameConfig.SUMMOMER_BOSS_MINION_SPWAN;

    /**
     * Creates a SummonerBoss that targets the given Player (required by Enemy).
     *
     * @param target the player this boss (and its minions) will target/track
     */
    public SummonerBoss(Player target)
    {
        super(target);

        spriteW=160;
        spriteH=125;

        //boos health
        maxHealth=100;
        health=maxHealth;

        //no damage
        //otherwise sword and axe warrior can't kill boss
        contactDamage=0;

        //health bar
        HP_BAR_W=spriteW-20;
        HP_BAR_H=10;
        HP_BAR_Y_OFFSET=90;

        //loadFramesRequired("pathPrefix", count) loads:
        summonFrames=loadFramesRequired("enemy/boss/summon/summon", 8);

        //iIdle image: first summon frame.
        setImage(summonFrames[0]);

        //start the boss in the waiting state.
        scheduleNextSummon();
    }

    /**
     * movement for boss is always 0, 0
     *
     * @return {0,0} so the boss never moves
     */
    protected int[] computeMove()
    {
        return new int[]{0, 0};
    }

    /**
     * Main update loop:
     * - Handles pause
     * - Either counts down to summon OR plays the summon animation
     * - Spawns minions only at the end of the animation
     */
    public void act()
    {

        if (GameWorld.isPaused()) return;

        if (getWorld()==null) return;

       
        //summoner enemy does not hit
        //if (hitCooldown > 0) hitCooldown--;

        //landing
        if(getY()<=GameConfig.roomCenterY())
        {
            SoundManager.playDescendingSound();
            //avoid landing into Blocker in the ro
            if (!getWorld().getObjectsAt(getX(),   
                                        GameConfig.roomCenterY(), 
                                        Blocker.class).isEmpty())
            {
               setLocation(GameConfig.roomCenterX()/2,getY()+5);
            }
            else
            {
                setLocation(getX(),getY()+5);
                
                //already in the room centre, play once
                if(getY()>=GameConfig.roomCenterY())
                {
                    SoundManager.stopDescendingSound();
                    SoundManager.playSummonerBossFightSound();

                }
            }
        }
   
        //summoner is big
        //when player touches
        //it does not takeDamage of player
        handlePlayerContact();

        if (summoning)
        {
            playSummonAnimation();
        }
        else
        {
            summonTimer--;
            if (summonTimer <= 0)
            {
                startSummon();
            }
        }
    }
    protected void handlePlayerContact() 
    {
        //do nothing
        //otherwise, axe or sword player has now way to kill the boos
        return;
    }

    /**
     * Schedules the next summon by setting summonTimer to a random value
     * between minInterval and maxInterval (inclusive).
     */
    private void scheduleNextSummon()
    {
        summonTimer=minInterval + Greenfoot.getRandomNumber(maxInterval - minInterval + 1);
    }

    /**
     * Enters the summoning state and resets animation counters.
     * The actual spawn happens only when the animation finishes.
     */
    private void startSummon()
    {
        summoning=true;

        //start with the first frame
        frameIndex=0;
        animTimer=0;

        setImage(summonFrames[0]);
    }

    /**
     * Advances the summon animation.
     *
     * When the last frame finishes, this method:
     * - switches back to idle
     * - spawns minions in the current room
     * - schedules the next summon timer
     */
    private void playSummonAnimation()
    {
        animTimer++;

        //only change frame when reach delay value
        if (animTimer < summonAnimDelay)
        {
            return; 
        }

        animTimer=0;
        frameIndex++;

        //if reached the end of the animation
        //do the spawn and reset to idle.
        if (frameIndex >= summonFrames.length)
        {
            summoning=false;
            setImage(summonFrames[0]); //idle image

            //spawn happens
            //at the end of the animation.
            spawnMinions();

            //sart waiting for the next summon.
            scheduleNextSummon();
            return;
        }

        //play only once during the animation
        SoundManager.playSummonerBossSound();//
    
        //show the next frame.
        setImage(summonFrames[frameIndex]);
    }
    /**
     * Spawns minions around the boss
     *
     * Strategy:
     *  try random points in a square around the boss (radius).
     *  prevent points that are out of bounds or inside blockers.
     *  when fail to find a valid spot after many tries
     *  use SpawnerSystem.randomFloomSpawnInRoom(currentRoomData).
     */
    private void spawnMinions()
    {
        GameWorld gw=(GameWorld) getWorld();
        RoomData rd=gw.getCurrentRoomData();
    
        int radius=120; //where to spawn the minions
    
        //how many random tries per minion
        int triesPerMinion=30;
    
        for (int i=0; i < minionsPerSummon; i++)
        {
            int[] point=findSpawnPointNearBoss(radius, triesPerMinion);
    
            //if no good point found near the boss, use room-floor helper
            if (point==null)
            {
                point=SpawnerSystem.randomFloomSpawnInRoom(rd);
            }
    
            //spawn a random minion type
            int choice=Greenfoot.getRandomNumber(3);
            if (choice==0)
            {
                getWorld().addObject(new ZombieEnemy(player), point[0], point[1]);
            }
            else if (choice==1)
            {
                getWorld().addObject(new WanderEnemy(player), point[0], point[1]);
            }
            else
            {
                getWorld().addObject(new SkeletonEnemy(player), point[0], point[1]);
            }
        }
    }
    
    /**
     * find a valid spawn location near the boss.
     *  inside world bounds
     *  not at a Blocker
     *
     * @param radius:   maximum distance from the boss
     * @param maxTries: how many random tries before giving up
     * @return:         {x,y} locatiom or null
     */
    private int[] findSpawnPointNearBoss(int radius, int maxTries)
    {
        World w=getWorld();
        if (w==null) return null;
    
        for (int t=0; t < maxTries; t++)
        {
            int x=getX() + Greenfoot.getRandomNumber(radius * 2 + 1) - radius;
            int y=getY() + Greenfoot.getRandomNumber(radius * 2 + 1) - radius;
    
            //Keep inside world bounds 
            if (x < 20 || y < 20 || 
                x > w.getWidth() - 20 || 
                y > w.getHeight() - 20)
            {
                continue;
            }
    
            //skip if location is at a blocker.
            if (!w.getObjectsAt(x, y, Blocker.class).isEmpty())
            {
                continue;
            }
    
            return new int[]{x, y};
        }
    
        return null;
    }
    protected void playAttackSoundEffect()
    {
        //SoundManager.playZombieSound();
    }
    protected void playEndOfLifeSoundEffect()
    {
        //to prevent hearing summoning sound after boss dead
        SoundManager.stopSummonerBossSound();
        SoundManager.playSummonBossDisappear();
    }
}