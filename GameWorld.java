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
        //must clean all the combat room and boss room
        totalRoomsToClear=map.countTotalForRoomType('C')+map.countTotalForRoomType('B');
        
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

        //start in center only at the beginning
        loadRoom(roomR, roomC, 0, 0); 
        
        setPaintOrder(
            PauseOverlay.class,
            Decoration.class,   //the statue image actor
            Enemy.class,
            SummonerBoss.class
        ); 
        
        //prompt manager for handling prompts during game play
        promptManager = new PromptManager();
        addObject(promptManager, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY());
        
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
        save();
        
        paused=false;
        Greenfoot.setWorld(new SettingWorld(data));
    }
    /**
     * exit to setup screen, but end the game
     *
     */
    private void saveAndLeave()
    {
        save();
        Greenfoot.setWorld(new SettingWorld(data));
        paused=false;
        Greenfoot.stop();
    }
    public void onPlayerDefeated()
    {
        //save();
        //start from new game only
        SaveManager.deleteSave();
        paused = false;
        Greenfoot.setWorld(new DefeatWorld(data));
    }
    public void onPlayerVictory()
    {
        //save();
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
                //saveAndLeave();   //<<<<<<<open this feature later
            }
            return;
        }
        
        //only combat/bossrooms can be cleared
        if ( (map.isCombatRoom(roomR, roomC) || map.isBossRoom(roomR, roomC))&& 
            countEnemies()==0 && 
            !map.isCleared(roomR, roomC))
        {
            if (map.markCleared(roomR, roomC))
            {
                roomsClearedCount++;
            }
        }
        
        
        //HUD text in side panel
        int hudX=GameConfig.ROOM_X + GameConfig.ROOM_W + GameConfig.SIDE_PANEL_W / 2;
        showText("Room: (" + roomR + "," + roomC + ")", hudX, 30);
        showText("Enemies: " + countEnemies(), hudX, 55);
        showText("Rooms Cleared: " + roomsClearedCount + " / " + totalRoomsToClear, hudX, 80);
        showText("Press ESC to Exit\n Press Space to Attack\n Press k to Stone Enemy", hudX, 250);
        showText("Difficulty Level:"+ difficultyLevel(), hudX,450);
        showText("Attacks : " + attackCount, hudX, 475);
        showText("Enemies defeated: " + enemiesKilled, hudX, 500);
        showText("Time: " +playerTimeFrames/60+ " seconds", hudX, 525);
        showText("Coin Collected: " +  player.getCoinCount(),hudX, 550);
        showText("Stone Skill: "+  player.getStoneSkillCount(),hudX, 575);
        showText("Score: " + player.getScore(), hudX, 600);
        showText("Attack Power:"+player.getAttackPower(),hudX,625);
        showText("Heath Remain: " + player.getHealth(), hudX, 650);


        //Win check
        //show in the window of the room
        //if (roomsClearedCount >= GameConfig.WIN_ROOMS) 
        if(roomsClearedCount>=totalRoomsToClear)
        {
            Greenfoot.setWorld(new VictoryWorld(data)); 
            //showText("YOU WIN!", 
            //GameConfig.ROOM_X + GameConfig.ROOM_W / 2, 
            //GameConfig.ROOM_Y + GameConfig.ROOM_H / 2);
            //Greenfoot.stop();
        }

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
             if (a != player && a != minimap && a!=playerBar && a!=promptManager) 
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

        //Spawn enemies only if room not cleared
        spawner.spawnEnemiesIfNeeded(r, c, player);

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
        if (enterDr==0 && enterDc==0) 
        {
            player.setLocation(GameConfig.roomCenterX(), GameConfig.roomCenterY());
            return;
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
        //not a combat room, doors always open
        if (!map.isCombatRoom(roomR, roomC) && !map.isBossRoom(roomR,roomC))
        {
            return true;
        }
    
        //combat room
        //unlock when enemies are gone
        return countEnemies()==0;
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
     * Allows any cctor to check if the game is paused.
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
        SoundManager.playGameMusic();
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
}