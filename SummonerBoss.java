import greenfoot.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A stationary boss that performs a summon animation.
 *  Uses a Queue (LinkedList) to schedule summon waves
 *  Each wave is an int[3]:
 *      [0] delayFrames  
 *      [1] count        
 *      [2] type         
 *
 * Behaviour:
 * - Boss does not move (except landing + relocate)
 * - It waits based on the next wave's delay
 * - It plays summon animation
 * - When animation ends, it spawns the wave's minions
 * - Then it loads the next wave from the queue
 *
 * @author:     Owen Kung, Cartis Lee
 * @version:    Jan 2026
 */
public class SummonerBoss extends Enemy
{
    //summon animation related
    private GreenfootImage[] summonFrames;
    private int summonAnimDelay = 35; // adjust to match sound timing
    private boolean summoning = false;

    //landing related
    private boolean landed = false;

    //for managing wave for spawns
    private Queue<int[]> waveQueue = new LinkedList<int[]>();

    //wave related data
    //loaded from queue
    private int summonTimer = 0;          //counts down until next summon starts
    private int currentWaveCount = 0;     //how many minions to spawn at end of animation
    private int currentWaveType = 3;      //minion type

    /**
     * Creates a SummonerBoss that targets the given Player.
     */
    public SummonerBoss(Player target)
    {
        super(target);

        spriteW = 160;
        spriteH = 125;

        //boss health
        baseMaxHealth=GameConfig.SUMMONER_BOSS_MAX_HEALTH;
        maxHealth=baseMaxHealth;
        health=maxHealth;

        //contact damage
        //contact boss does not damage to player
        //othewise axe and sword warrior can't kill boss
        contactDamage = 1;

        //health bar
        HP_BAR_W = spriteW - 20;
        HP_BAR_H = 10;
        HP_BAR_Y_OFFSET = 90;

        //load summon frames
        summonFrames = loadFramesRequired("enemy/boss/summon/summon", 8);
        setImage(summonFrames[0]); // idle image

        //build the schedule 
        buildWaveSchedule();
        loadNextWaveFromQueue();
    }

    /**
     * Boss never moves by AI.
     */
    protected int[] computeMove()
    {
        return new int[]{0, 0};
    }

    public void act()
    {
        if (GameWorld.isPaused()) return;
        if (getWorld() == null) return;

        //frozen by stone skill
        if (freezeTimer > 0)
        {
            showText("Stoned", Color.YELLOW, getX(), getY() + 100, false);
            freezeTimer--;
            return;
        }

        //Landing
        if (!landed)
        {
            if (getY() <= GameConfig.roomCenterY())
            {
                SoundManager.playDescendingSound();

                if (!getWorld().getObjectsAt(getX(), GameConfig.roomCenterY(), Blocker.class).isEmpty())
                {
                    setLocation(GameConfig.roomCenterX() / 2, getY() + 5);
                }
                else
                {
                    setLocation(getX(), getY() + 5);

                    if (getY() >= GameConfig.roomCenterY())
                    {
                        SoundManager.stopDescendingSound();
                        SoundManager.playSummonerBossFightSound();
                        landed = true;
                    }
                }
            }
            return;
        }

        //Summon
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

    /**
     * Boss does not damage player by contact handling.
     */
    protected void handlePlayerContact()
    {
        return;
    }

    /**
     * Build a wave schedule  into the queue.
     */
    private void buildWaveSchedule()
    {
        waveQueue.clear();

        //int[]{delayFrames, count, type}
        waveQueue.add(new int[]{120, GameConfig.SUMMOMER_BOSS_MINION_SPAWN, 0}); // after 2 sec, 1 zombie
        waveQueue.add(new int[]{180, GameConfig.SUMMOMER_BOSS_MINION_SPAWN+1, 3}); // another 3 sec, 2 random
        waveQueue.add(new int[]{240, GameConfig.SUMMOMER_BOSS_MINION_SPAWN+2, 1}); // another 4 sec, 3 wander
        waveQueue.add(new int[]{300, GameConfig.SUMMOMER_BOSS_MINION_SPAWN+3, 3}); // another 4 sec, 4 random
        waveQueue.add(new int[]{360, GameConfig.SUMMOMER_BOSS_MINION_SPAWN+4, 2}); // another 6 sec, 5 skeleton
    }

    /**
     * Take next wave from queue. 
     * If queue empty, rebuild it
     */
    private void loadNextWaveFromQueue()
    {
        if (waveQueue.isEmpty())
        {
            buildWaveSchedule();
        }

        int[] wave = waveQueue.poll(); //pop next wave

        if (wave == null)
        {
            //default
            summonTimer = 180;
            currentWaveCount = 2;
            currentWaveType = 3;
            return;
        }

        summonTimer = wave[0];
        currentWaveCount = wave[1];
        currentWaveType = wave[2];
    }
    private void startSummon()
    {
        summoning = true;

        frameIndex = 0;
        animTimer = 0;

        setImage(summonFrames[0]);

        // play once at the start
        SoundManager.playSummonerBossSound();
    }
    private void playSummonAnimation()
    {
        animTimer++;

        if (animTimer < summonAnimDelay)
        {
            return;
        }

        animTimer = 0;
        frameIndex++;

        if (frameIndex >= summonFrames.length)
        {
            summoning = false;
            setImage(summonFrames[0]); // back to idle

            //spawn the wave that was scheduled
            spawnMinions(currentWaveCount, currentWaveType);

            //to avoid player staying on the spot
            //targeting the boss
            relocate();

            //load next scheduled wave
            loadNextWaveFromQueue();
            return;
        }

        setImage(summonFrames[frameIndex]);
    }

    /**
     * Spawn minions for this wave.
     * 
     * @param count: how many to spawn
     * @param type: what type to spawn
     */
    private void spawnMinions(int count, int type)
    {
        GameWorld gw = (GameWorld) getWorld();
        RoomData rd = gw.getCurrentRoomData();

        int radius = 100;
        int triesPerMinion = 30;

        for (int i = 0; i < count; i++)
        {
            int[] point = findSpawnPointNearBoss(radius, triesPerMinion);
            if (point == null)
            {
                point = SpawnerSystem.randomFloomSpawnInRoom(rd);
            }

            int choice = type;

            //type 3 means random mix
            if (type == 3)
            {
                choice = Greenfoot.getRandomNumber(3); // 0/1/2
            }

            if (choice == 0)
            {
                getWorld().addObject(new ZombieEnemy(player), point[0], point[1]);
            }
            else if (choice == 1)
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
     * find a safe spot near boss, avoid wall or blocker objects
     */
    private int[] findSpawnPointNearBoss(int radius, int maxTries)
    {
        World w = getWorld();
        if (w == null) return null;

        for (int t = 0; t < maxTries; t++)
        {
            int x = getX() + Greenfoot.getRandomNumber(radius * 2 + 1) - radius;
            int y = getY() + Greenfoot.getRandomNumber(radius * 2 + 1) - radius;

            if (x < 20 || y < 20 || x > w.getWidth() - 20 || y > w.getHeight() - 20)
            {
                continue;
            }

            if (!w.getObjectsAt(x, y, Blocker.class).isEmpty())
            {
                continue;
            }

            return new int[]{x, y};
        }

        return null;
    }
    
    /**
     * relocate boss from time to time to avoid player staying stationary targeting boss
     */
    private void relocate()
    {
        int[] newLocation = findSpawnPointNearBoss(100, 30);
        if (newLocation != null)
        {
            setLocation(newLocation[0], newLocation[1]);
        }
    }
    protected void playAttackSoundEffect()
    {
        // boss doesn't attack
    }
    protected void playEndOfLifeSoundEffect()
    {
        SoundManager.stopSummonerBossSound();
        SoundManager.playSummonBossDisappear();
    }
}