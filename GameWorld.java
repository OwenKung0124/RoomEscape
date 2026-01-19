import greenfoot.*;
import java.util.*;

/**
 * GameWorld is a room-to-room battle game.
 *
 * @author:     Owen Kung
 * @version:    Jan 2026
 */
public class GameWorld extends World 
{

    //game pausle related variables
    private static boolean paused=false; //pause state
    private boolean lastEsc=false; //ESC just-pressed detection
    private PauseOverlay pauseUI; //pause overlay actor

    //RoomMap stores all data related to a room
    private GameMap map;

    //Systems that handle specific responsibilities
    private RoomRenderer renderer;
    private DoorSystem doorSystem;
    private SpawnerSystem spawner;
    
    //handling game play prompts for user input
    private PromptManager promptManager;
    private TextLabel gameMessage;
    private int messageTimer=0;
    
    //(1,1), starting room
    private int roomR=1;
    private int roomC=1;

    //The room immediately BEFORE the current room 
    //(for backtracking)
    private int lastRoomR;
    private int lastRoomC;

    //these objects does not get removed when new room loads
    private Player player;
    private MiniMap minimap;
    private HealthBar playerBar;
    
    private int roomsClearedCount=0;
    private int totalRoomsToClear=0;
    
    //session only game data
    //not saving into file
    public static int enemiesKilled=0;
    public static int attackCount=0;
    public static int playerTimeFrames=0;
    public static int numOfHealthUpgrade=0;
    public static int numOfAttackUpgrade=0;
    public static int numOfStoneUpgrade=0;
    public static int numOfStoneUsed=0;

    //combat timer
    private boolean combatTimerActive = false;
    private int combatTimerFrames = 0;

    //trap room related variables/constants
    private boolean trapActive = false;
    private int trapTimerFrames = 0;
    private boolean trapPenaltyDone = false;
    
    //dodge room related variables/constants
    private boolean dodgeActive = false;
    private int dodgeTimerFrames = 0;
 
    
    private GameData data;   //this data is passed around between world and is used to save to file when needed
    
    public GameWorld()
    {
        this(GameConfig.WARRIOR_AXE, false, null);   //default warrior selection, new game, no save data passed
    }
    /**
     * Constructs the world
     * initializes room data
     * spawns Playe, MiniMap,
     * loads the starting room.
     */
    public GameWorld(int warriorType,boolean resume, GameData data)
    {
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        
        //if there's already an existing savedata
        if(data!=null)
        {
            this.data=data;
        }
        else
        {
            this.data=new GameData();
        }
        map=new GameMap();

        renderer=new RoomRenderer(this, map);
        doorSystem=new DoorSystem(this, map);
        spawner=new SpawnerSystem(this, map);

        //add minimap to the side panel
        minimap=new MiniMap();
        addObject(minimap, GameConfig.MINIMAP_X, GameConfig.MINIMAP_Y);
    
        //warrior chosen
        if (warriorType==GameConfig.WARRIOR_BULLET)
        {
            player=new BulletWarrior();
        }
        else if (warriorType==GameConfig.WARRIOR_AXE)
        {
            player=new AxeWarrior();
        }
        else
        {
            player=new SwordWarrior();
        }
        addObject(player, GameConfig.roomCenterX(), GameConfig.roomCenterY());
        
        int barW=GameConfig.SIDE_PANEL_W - 30;
        int barH=18;
        int panelCenterX=GameConfig.PLAY_W + GameConfig.SIDE_PANEL_W / 2;
        int barY=GameConfig.WORLD_H - 25;
        //HealthBar(HasHealth unit, Actor follow, int width, int height, boolean followTarget, int yOffset)
        playerBar=new HealthBar(player, null, barW, barH, false, 0);    
        addObject(playerBar, panelCenterX, barY);
        
        
        //set total number of room to clean
        //must clean all the combat,boss,trap,dodge (any room except t and s)
        totalRoomsToClear=map.countTotalForRoomType('C')
                        +map.countTotalForRoomType('R')
                        +map.countTotalForRoomType('D');
        
        //pick starting room:
        //resume,load roomR/roomC visited/cleared into map
        //new game,start at first room
        int[] start=map.findFirstRoom();
    
        roomR=start[0];
        roomC=start[1];
    
        //new game clear old session data
        if(!resume)
        {
            clearSessionData();  //clear static variables   
        }
        //whatever data saved
        //must also update this section
        if (resume)
        {
            GameData loadedData=SaveManager.load(map);
            
            if(loadedData!=null)
            {
                 data=loadedData;
            }
 
            if (data != null && map.hasRoom(data.roomR, data.roomC))
            {
                roomR=data.roomR;
                roomC=data.roomC;
        
                if (map.hasRoom(data.lastRoomR, data.lastRoomC))
                {
                    lastRoomR=data.lastRoomR;
                    lastRoomC=data.lastRoomC;
                }
                else
                {
                    lastRoomR=roomR;
                    lastRoomC=roomC;
                }
            }
            
            roomsClearedCount=data.roomsCleared;
            player.setHealth(data.playerHealth);
            player.setCoinCount(data.coins);
            player.setScore(data.score);
            player.setStoneSkillCount(data.stones);
            if(player instanceof AxeWarrior)
            {
                 player.setAttackPower(data.axeAttackPower);
            }
            if(player instanceof BulletWarrior)
            {
                 player.setAttackPower(data.bulletAttackPower);
            }
            if(player instanceof SwordWarrior)
            {
                 player.setAttackPower(data.swordAttackPower);
            }
            
            //RoomData, cleared and visited already handled in SavvaManger
            //by RoomData and GameMap classes
        }

        //prompt manager for handling prompts during game play
        promptManager = new PromptManager();
        addObject(promptManager, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY());
        
        //game message
        //-1 means never removed by TextLabel from the world
        //instead use textlabel'setVisible or not to show the game message
        gameMessage=new  TextLabel("", 22, Color.YELLOW,-1); 
        addObject(gameMessage, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY()-50);
        
        //start in center only at the beginning
        loadRoom(roomR, roomC, 0, 0); 
        
        setPaintOrder(
            FloatingText.class,
            PauseOverlay.class,
            //Player.class,
            Decoration.class,   //the statue image actor
            Enemy.class,
            SummonerBoss.class
        ); 
        
        //image Icons for side panels
        int hudX=GameConfig.ROOM_X + GameConfig.ROOM_W + GameConfig.SIDE_PANEL_W / 2;
        addObject(new ImageIcon("difficulty_icon.png","Difficulty Level",35,35,255),hudX-110,385);  
        addObject(new ImageIcon("attacks_icon.png","# 0f Attacks",35,35,255),hudX-110,420);
        addObject(new ImageIcon("enemy_defeated_icon.png","Enemy Defeated",35,35,255),hudX-110,455);
        addObject(new ImageIcon("attack_power_icon.png","Attack Power",35,35,255),hudX-110,490);
        addObject(new ImageIcon("coin.png","Coins Earned",35,35,255),hudX-110,525);
        addObject(new ImageIcon("stone_icon.png","Stone Skill Earned",35,35,255),hudX-110,565);
        addObject(new ImageIcon("score_icon.png","Score",40,20,255),hudX-110,595);
        addObject(new ImageIcon("time_icon.png","Time Lapsed",40,20,255),hudX-110,630);
          
        //SoundManager.playGameMusic();
    }
    /**
     * pause on/off and shows/hides the pause overlay.
     */
    private void pauseSwtich()
    {
        paused=!paused;
    
        if (paused)
        {
            //add overlay in the center of the room
            pauseUI=new PauseOverlay();
            //when pause, make sure no sounds are playing
            SoundManager.stopAllBossSounds();
            addObject(pauseUI, GameConfig.roomCenterX(), GameConfig.roomCenterY());
        }
        else
        {
            //remove overlay
            if (pauseUI != null && pauseUI.getWorld() != null)
            {
                removeObject(pauseUI);
            }
            pauseUI=null;
        }
    }
    /**
     * exit to setup screen.
     */
    private void exitToSetup()
    {  
        saveToFile();
        
        paused=false;
        Greenfoot.setWorld(new SettingWorld(data));
    }
    /**
     * exit to setup screen, but end the game
     *
     */
    private void saveAndLeave()
    {
        saveToFile();
        Greenfoot.setWorld(new SettingWorld(data));
        paused=false;
        Greenfoot.stop();
    }
    public void onPlayerDefeated()
    {
        save();
        //start from new game only
        SaveManager.deleteSave();
        paused = false;
        Greenfoot.setWorld(new DefeatWorld(data));
    }
    public void onPlayerVictory()
    {
        save();
        //start from new game only
        SaveManager.deleteSave();
        paused = false;
        Greenfoot.setWorld(new VictoryWorld(data));
    }
    private void save()
    {
        if(data==null)
        {
            return;
        }
        
        data.roomR=roomR;
        data.roomC=roomC;
        data.lastRoomR=lastRoomR;
        data.lastRoomC=lastRoomC;
    
        data.playerHealth=player.getHealth();
        data.roomsCleared=roomsClearedCount;
        data.coins=player.getCoinCount();
        data.score=player.getScore();
        data.stones=player.getStoneSkillCount();
        if(player instanceof AxeWarrior)
        {
            data.axeAttackPower=player.getAttackPower();
        }
        if(player instanceof BulletWarrior)
        {
            data.bulletAttackPower=player.getAttackPower();
        }
        if(player instanceof SwordWarrior)
        {
            data.swordAttackPower=player.getAttackPower();
        }
 
    }
    private void saveToFile()
    {
        save();
        
        //roomCleared,roomVisited,roomData handled by
        //GameMap, RoomData in SavaMAnager
        SaveManager.save(data,map);
    }
    /**
     * - Marks a room cleared when enemies reach 0
     * - Updates HUD, the side panel
     * - Updates door lock states and physical door blockers
     */
     public void act() 
     {         
        //keep track how long the player stays in game world
        playerTimeFrames++;
        
        //esc=just pressed
        boolean esc=Greenfoot.isKeyDown("escape");
        boolean escJustPressed=esc && !lastEsc;
        lastEsc=esc;
    
        if (escJustPressed)
        {
              pauseSwtich();
        }
    
        if (paused)
        {
            if (Greenfoot.isKeyDown("q"))
            {
                exitToSetup();
            }
            if (Greenfoot.isKeyDown("s"))
            {
                //saveAndLeave();   //<<<<<<<may open this feature later
            }
            return;
        }
        
        //only combat/bossrooms needs enemy count to clear
        if ( (map.isCombatRoom(roomR, roomC) || map.isBossRoom(roomR, roomC) ) && 
               countEnemies()==0 && 
              !map.isCleared(roomR, roomC))
        {
            if (map.markCleared(roomR, roomC))
            {
                roomsClearedCount++;
            }
        }
        
        //HUD text in side panel;
        int hudX=GameConfig.ROOM_X + GameConfig.ROOM_W + GameConfig.SIDE_PANEL_W / 2;
        showText("Room: (" + roomR + "," + roomC + ")", hudX, 30);
        showText("Enemies: " + countEnemies(), hudX, 55);
        showText("Rooms Cleared: " + roomsClearedCount + " / " + totalRoomsToClear, hudX, 80);
        //showText("Press ESC to Exit\n Press Space to Attack\n Press k to Stone Enemy", hudX, 250);
        showText(": "+ difficultyLevel(), hudX-60,385);
        showText(": " + attackCount, hudX-60, 420);
        showText(": " + enemiesKilled, hudX-60, 455);
        showText(": "+player.getAttackPower(),hudX-60,490);
        showText(": " +  player.getCoinCount(),hudX-60, 525);
        showText(": "+  player.getStoneSkillCount(),hudX-60, 565);
        showText(": " + player.getScore(), hudX-60, 595);
        showText(": " +playerTimeFrames/60+ " Seconds", hudX-10, 630);
        showText("" + player.getHealth(), hudX+115, 655);
        
        
        //track for gameMessage
        if (messageTimer>=0)
        {
            messageTimer--;
        
            if (messageTimer == 0 && gameMessage != null)
            {
                gameMessage.setVisible(false);
            }
        }
        
        //win check
        //show in the window of the room
        if(roomsClearedCount>=totalRoomsToClear)
        {
            onPlayerVictory();
            Greenfoot.setWorld(new VictoryWorld(data)); 
        }

        //rooms with time sensitive
        //needs to be updated
        updateCombatTimer();
        updateTrapRoom();
        updateDodgeRoom();
        
        
        //isRoomUnloced
        //only check escape condition for C and B rooms
        //as they both lock the room for battle
        boolean unlockedNow=isRoomUnlocked();

        //unlock all doors if cleared, otherwise only the back door
        doorSystem.updateDoorStates(roomR, roomC, lastRoomR, lastRoomC, unlockedNow);

        //block door gaps while locked, but not the back door gap
        doorSystem.syncDoorBlockers(roomR, roomC, lastRoomR, lastRoomC, unlockedNow);
    }  
    /**
     * all seession varibles to be cleared for new game
     */
    private void clearSessionData()
    {
            enemiesKilled=0;
            attackCount=0;
            playerTimeFrames=0;
            numOfHealthUpgrade=0;
            numOfAttackUpgrade=0;
            numOfStoneUpgrade=0;
            numOfStoneUsed=0;
    }
    /**
     * Scale games difficutly level based on rooms cleared
     * and on player's attack power
     */
    public int difficultyLevel()
    {
        if (roomsClearedCount <= 1)
        {
             return 0;   
        }
        if (roomsClearedCount <= 3)
        {
            return 1;
        }
        if (roomsClearedCount <= 5)
        {
            return 2;
        }
        
        //more than 5 rooms
        return 3;
    }
    /**
     * Loads a room.
     * removes everything except player and MiniMap
     * draws the room background and the side panel
     * builds interior walls and border walls
     * places doors on the border
     * spawns enemies if needed
     *
     * @param r room row
     * @param c room col
     */
    private void loadRoom(int r, int c, int enterDr, int enterDc)
    {
        //remove everything except player, minimap, player status baar, prompt manager
        ArrayList<Actor> all=new ArrayList<Actor>(getObjects(Actor.class));
        for (Actor a : all) 
        {
             if (a != player && 
                 a != minimap && 
                 a != playerBar && 
                 a != promptManager && 
                 a != gameMessage && 
                 !(a instanceof ImageIcon))
             {
                removeObject(a);   
             }
        }


        //when changing room
        //stop all boss sounds
        SoundManager.stopAllBossSounds();
        
        //Door blockers are owned by DoorSystem
        doorSystem.onRoomLoaded();

        //set visited of 
        //the current room to true
        map.setVisited(r, c);

        //Background + walls + doors
        renderer.buildRoom(r, c);

        //add object using map e.g. statue 
        //moved to renderer to buildobject from tiles
        //spawner.spawnObjectsFromTiles(r, c);

        //Place the player where he enters the room with
        //unless it's the first room
        //the player will appear in the middle
        placeAtEntrance(enterDr, enterDc);
        
        //if in a trap room
        //trapRoomActive startTimer
        //but once cleared
        //treat as a by pass room

        startTrapIfNeeded(r, c);
        startDodgeIfNeeded(r, c);

        //Spawn enemies only if room not cleared
        spawner.spawnEnemiesIfNeeded(r, c, player);
        //must come after spawning enemies
        //so that the allowed time can be allocated properly
        startCombatTimerIfNeeded(r, c);

        //Spawn hazard enemy only if room not cleared and is dodge room
        spawner.spawnHazardsIfNeeded(r, c, player);
        
        //blocker rebuild
        boolean unlocked=isRoomUnlocked();
        doorSystem.updateDoorStates(roomR, roomC, lastRoomR, lastRoomC, unlocked);
        doorSystem.syncDoorBlockers(roomR, roomC, lastRoomR, lastRoomC, unlocked);
    }

    /**
     * places the player close to the door it enterred from
     */
    private void placeAtEntrance(int enterDr, int enterDc)
    {
        if (enterDr==0 && enterDc==0 ) 
        {   
            if(roomsClearedCount==0)
            {
                //only place in the cetnre for the first room/new game
                player.setLocation(GameConfig.roomCenterX(), GameConfig.roomCenterY());
                return;
            }
            else
            {   //else place on the side when resume
                player.setLocation(GameConfig.roomCenterX()-300, GameConfig.roomCenterY()-50);
                return;
            }
        }
    
        int halfW=player.getImage().getWidth() / 2;
        int halfH=player.getImage().getHeight() / 2;
    
        //push player far enough
        //so dthey are not touchng the Door actor
        //or the graphics will blink
        //not sure if was the reason
        int offsetX=GameConfig.BORDER_THICK + halfW + GameConfig.PLAYER_DOOR_OFFSET_X;  
        int offsetY=GameConfig.BORDER_THICK + halfH + GameConfig.PLAYER_DOOR_OFFSET_Y;  
    
        //find the door that leaads to this room 
        Door backDoor=findDoor(-enterDr, -enterDc);
    
        if (backDoor==null)
        {
            int midX=GameConfig.roomCenterX();
            int midY=GameConfig.roomCenterY();
            player.setLocation(midX, midY);
            return;
        }
    
        int x=backDoor.getX();
        int y=backDoor.getY();
    
        //move player inward from that door's border position
        if (backDoor.getDr()==-1 && backDoor.getDc()==0)
        {   //top door
            y += offsetY;
        }
        else if (backDoor.getDr()==1 && backDoor.getDc()==0)
        {   // bottom door
            y -= offsetY;
        }
        else if (backDoor.getDr()==0 && backDoor.getDc()==-1)
        {   //left door
            x += offsetX;
        }
        else if (backDoor.getDr()==0 && backDoor.getDc()==1)
        {   //right door
            x -= offsetX;
        }
    
        player.setLocation(x, y);
    }

    /**
     * try to move to an adjacent room.
     *
     * if current room cleared, can move to any neighbor room.
     * if not cleared, can only move back to the last room.
     *
     * @param dr:change in room row
     * @param dc: change in room col
     */
    public void tryMove(int dr, int dc) 
    {
        //calculate the target room  in the grid
        //dr=change in row (up/down), 
        //dc=change in col (left/right)
        int nr=roomR + dr;   //neighbor row
        int nc=roomC + dc;   //neighbor column

        //If there is no room in that direction, do nothing
        if (!map.hasRoom(nr, nc)) 
        {
            return;   
        }

        //check if the player is trying to go back to the previous room
        boolean goingBack=(nr==lastRoomR && nc==lastRoomC);

        //trap room escape logic in tryMove
        if(trapActive && trapTimerFrames>=0 && ! goingBack)
        {
            map.markCleared(roomR,roomC);
            roomsClearedCount++;
            trapActive=false;
            trapTimerFrames=0;
            player.addScore(GameConfig.TRAP_REWARD_SCORE);  //increase score
            SoundManager.playRewardSound(); //audio feedback
            showMessage("Rewarded Score(+"+GameConfig.TRAP_REWARD_SCORE+")",120);
        }
        
        //the room is still locked
        //not going back, block the move.
        if (!isRoomUnlocked() && !goingBack) 
        {
            return;   
        }

        //save the current room rc before moving
        int oldR=roomR;
        int oldC=roomC;

        //move to the new room
        roomR=nr;
        roomC=nc;

        //update last room
        lastRoomR=oldR;
        lastRoomC=oldC;

        //load the new room
        loadRoom(roomR, roomC, dr, dc);

    }
    /**
     * @return number of Enemy actors currently in the world
     */
    private int countEnemies() 
    {
        return getObjects(Enemy.class).size();
    }
    /**
     * @return true non-combat room or when enemy count=0
     */
    public boolean isRoomUnlocked()
    {
        //boss/combat rooms: unlock when enemies gone
        if (map.isCombatRoom(roomR, roomC) || map.isBossRoom(roomR, roomC))
        {
            return countEnemies() == 0;
        }
    
        // dodge room (D): locked until cleared
        if (map.isDodgeRoom(roomR, roomC))
        {
            return map.isCleared(roomR, roomC);
        }
    
        //normal rooms: always open
        return true;
    }
    /**
     * Finds the Door in the current room that matches (dr, dc).
     */
    private Door findDoor(int dr, int dc)
    {
        for (Door d : getObjects(Door.class))
        {
            if (d.getDr()==dr && d.getDc()==dc) return d;
        }
        return null;
    }
    /**
     * Allows any actor to check if the game is paused.
     */
    public static boolean isPaused()
    {
        return paused;
    }
    
    /**
     * @return the number of room rows in the world grid
     */
    public int getRows()
    {
        return map.getRows();
    }
    
    /**
     * @return the number of room columns in the world grid
     */
    public int getCols()
    {
        return map.getCols();
    }
    
    /**
     * @return the current room row the player is in
     */
    public int getRoomR()
    {
        return roomR;
    }
    
    /**
     * @return the current room column the player is in
     */
    public int getRoomC()
    {
        return roomC;
    }
    
    /**
     * Checks whether a room exists at the given grid location.
     *
     * @param r:room row
     * @param c:room column
     * @return true if that room exists
     */
    public boolean roomExists(int r, int c)
    {
        return map.hasRoom(r, c);
    }
    
    /**
     * Checks whether the given room has been visited before.
     *
     * @param r:room row
     * @param c:room column
     * @return true if visited
     */
    public boolean wasVisited(int r, int c)
    {
        return map.wasVisited(r, c);
    }
    
    /**
     * Checks whether the given room has been cleared (enemies defeated).
     *
     * @param r:room row
     * @param c:room column
     * @return true if cleared
     */
    public boolean isCleared(int r, int c)
    {
        return map.isCleared(r, c);
    }
    /**
     * Allows actors (like Boss) to access the current room's RoomData.
     */
    public RoomData getCurrentRoomData()
    {
        return map.getRoomData(roomR, roomC);
    }
    public int getRoomsClearedCount()
    {
        return roomsClearedCount;
    }
    public char getRoomType(int r, int c)
    {
        return map.getRoomType(r, c); 
    }
    public GameMap getGameMap()
    {
        return map;
    }
    /**
     * Make sure game music starts after restarted
     */
    public void started()
    {
        //SoundManager.playGameMusic();
    }
    /**
     * Make sure game music stops when paused
     */
    public void stopped()
    {
        SoundManager.stopAll();
    }
     /**
     * @return promptManager
     */
    public PromptManager getPromptManager()
    {
        return promptManager;
    }
    public boolean isBossRoom()
    {
        //if this is the last battle room, then this is the boss room
        return map.isBossRoom(roomR,roomC);
    
    }
    /**
     * if player is in the combat room, start combat room timer
     */
    private void startCombatTimerIfNeeded(int r, int c)
    {
        combatTimerActive=false;
        combatTimerFrames=0;
    
        //Only time normal combat rooms
        if (!map.isCombatRoom(r, c))
        {
             return;   
        }
    
        //If already cleared, no timer
        if (map.isCleared(r, c))
        {
             return;   
        }
    
        //must be called after enemy is spawned
        int enemyCount = countEnemies();
    
        //if somehow zero enemies, no timer needed
        if (enemyCount <= 0)
        {
             return;   
        }
    
        //calculated allowed time
        int frames = GameConfig.COMBAT_BASE_TIME + enemyCount * GameConfig.COMBAT_PER_ENEMY_TIME;
        if (frames>GameConfig.COMBAT_MAX_TIME)
        {
            frames=GameConfig.COMBAT_MAX_TIME;   
        }
    
        //if it's a boos is spawned
        if( getObjects(SummonerBoss.class).size()>0)
        {
            frames=GameConfig.COMBAT_BOSS_TIME;
        }
        
        combatTimerActive = true;
        combatTimerFrames = frames;
    
        showMessage("COMBAT ROOM!\nDefeat all enemies\n before time runs out!", 150);
    
    }
    /**
     * once in a combat room, combat timer starts
     */
    private void updateCombatTimer()
    {
        if (!combatTimerActive)
        {
            return;   
        }
    
        //if room got cleared, stop the timer
        if (countEnemies() == 0)
        {
            combatTimerActive = false;
            combatTimerFrames = 0;
            return;
        }
    
        showMessage("Combat Time Remain: "+ (combatTimerFrames/60)+"",60);
        combatTimerFrames--;
    
        //time up,instant defeat
        if (combatTimerFrames <= 0)
        {
            combatTimerActive = false;
            combatTimerFrames = 0;
            onPlayerDefeated();
        }
    }
    /**
     * start trap room rules if it's a trap room
     * 
     * @param r     the roomR
     * @param c     the roomC
     */
    private void startTrapIfNeeded(int r, int c)
    {
        trapActive = false;
        trapPenaltyDone = false;
        trapTimerFrames = 0;
        
        //if it's a trap room is not cleared, start trap room game
        //if it's penalied or reward
        //then this room is markCleared in tryMove
        //second time here, it won't trigger
        if (map.isTrapRoom(r, c) && !map.isCleared(r, c))
        {
            trapActive = true;
            trapTimerFrames = GameConfig.TRAP_TIME_FRAMES;
 
            showMessage("Rush!\n You must leave the room \n from the other sie in\n"
                        +(GameConfig.TRAP_TIME_FRAMES/60)+" s!\n"
                        +"Success->Score (+"+GameConfig.TRAP_REWARD_SCORE+")\n"+
                        "Failure->HP(-"+GameConfig.TRAP_PENALTY_HP +")", 
                        180);

        }
    }
    private void updateTrapRoom()
    {
        if (!trapActive) return;
    
        trapTimerFrames--;
    
        //dispaly time to escape count down
        if (trapTimerFrames < GameConfig.TRAP_TIME_FRAMES - 200 && trapTimerFrames>0)
        {
            showMessage("Rush Timer: " + (trapTimerFrames / 60), 60);
        }
    
        //failed to escape in time
        //penalty applied
        //the reward part is dealt in tryMove
        //as the actor will leave the room w/o actually touching the door
        //leaving touching door and room leaving to tryMove
        if (trapTimerFrames <= 0 && !trapPenaltyDone)
        {
            trapPenaltyDone = true;
            trapTimerFrames=0;
            player.takeDamage(GameConfig.TRAP_PENALTY_HP);
            
            //trap room only play once
            //regardless if passed or not
            map.markCleared(roomR,roomC);
            roomsClearedCount++;

            SoundManager.playFailSound();
            showMessage("Too slow! -" + GameConfig.TRAP_PENALTY_HP + " HP", 120);
        }
    }
    /**
     * start dodge room logic if in dodge room and is not cleared
     */
    private void startDodgeIfNeeded(int r, int c)
    {
        dodgeActive = false;
        dodgeTimerFrames = 0;
    
        // only start if this is a dodge room AND not cleared yet
        if (map.getRoomType(r, c) == 'D' && !map.isCleared(r, c))
        {
            dodgeActive = true;
            dodgeTimerFrames=GameConfig.DODGE_TIME_FRAMES;
    
            showMessage("DODGE ROOM!\nDon't touch hazards \n for 10 seconds!", 150);
        }
    }
    /**
     * update dogge room play
     */
    private void updateDodgeRoom()
    {
        if (!dodgeActive)
        {
            return;
        }
    
        //countdown
        dodgeTimerFrames--;
    
        //show timer (don’t spam every frame, just show after the intro)
        if (dodgeTimerFrames<GameConfig.DODGE_TIME_FRAMES - 180)
        {
            showMessage("Dodge Timer: " + (dodgeTimerFrames / 60), 60);
        }
    
        //success
        if (dodgeTimerFrames <= 0)
        {
            dodgeActive = false;
            dodgeTimerFrames = 0;
    
            if (!map.isCleared(roomR, roomC))
            {
                map.markCleared(roomR, roomC);
                roomsClearedCount++;
    
                player.addScore(GameConfig.DODGE_REWARD_SCORE); 
                SoundManager.playRewardSound(); //audio feedback
                
                //remove Hazard Visually
                //remove all hazards so player sees it's finished
                ArrayList<HazardEnemy> hazards =(ArrayList<HazardEnemy>) getObjects(HazardEnemy.class);
                for (HazardEnemy h : hazards)
                {
                    removeObject(h);
                }
                
                showMessage("Success! +" + GameConfig.DODGE_REWARD_SCORE + " score", 150);
            }
        }
    }
    /**
     * Called by HazardEnemy to reset the timer
     */
    public void onDodgeRoomHit()
    {
        if (!dodgeActive) return;
        
        SoundManager.playOuchSound();
        dodgeTimerFrames=GameConfig.DODGE_TIME_FRAMES;
        showMessage("Ouch! Timer reset!", 90);
    }
    /**
     * Helper method for games to show game messages on the side panel
     * 
     * @param msg message to display
     * @param how long to display for
     */  
    public void showMessage(String msg, int frames)
    {
        if (gameMessage==null)
        { 
            gameMessage = new TextLabel("", 22, Color.YELLOW,-1); 
            addObject(gameMessage, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY()-50);
        }
    
        //audio alert
        SoundManager.playMessageSound();
        gameMessage.setText(msg);     
        gameMessage.setVisible(true);
        messageTimer = frames;
    }
}