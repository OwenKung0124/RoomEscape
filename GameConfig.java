/**
 * GameConfig stores the shared constants for the game
 * The static methods were originally from the GameWorld and moved here
 * work as helper methods for multiple classes the were originally in game world
 * 
 */
public class GameConfig 
{

    //World size (MAKE THIS WIDER)
    public static final int WORLD_W = 1200;  
    public static final int WORLD_H = 700;

    //Side panel stays the same
    public static final int SIDE_PANEL_W = 300;

    //playable zone width
    //left to the side panel
    public static final int PLAY_W = WORLD_W - SIDE_PANEL_W;

    //margins around the room inside the play zone
    public static final int ROOM_MARGIN_X = 5;
    public static final int ROOM_MARGIN_Y = 85;

    //room rectangle inside the playable zone
    public static final int ROOM_X = ROOM_MARGIN_X;
    public static final int ROOM_Y = ROOM_MARGIN_Y;
    public static final int ROOM_W = PLAY_W - ROOM_MARGIN_X * 2;
    public static final int ROOM_H = WORLD_H - ROOM_MARGIN_Y - 40;

    //Minimap
    //position inside the side panel
    public static final int MINIMAP_X = PLAY_W + SIDE_PANEL_W / 2;
    public static final int MINIMAP_Y = 160;
    //cell size and padding
    public static final int MINIMAP_CELL=15;
    public static final int MINIMAP_PAD=3;
    
    //Tiles
    public static final int MAP_COLS = 16;
    public static final int MAP_ROWS = 9;
    
    //Tiles constant
    //1-10 structure constants
    public static final int FLOOR = 0;
    public static final int INTERIOR_WALL  = 1;
    public static final int EXTERIOR_WALL = 2;
    public static final int DOOR = 3;
    //collectables starting with 11-20
    public static final int COIN = 11; 
    
    //station object 21-30
    public static final int STATUE = 21; 
    public static final int LION_RIGHT = 22;
    public static final int LION_LEFT = 23;
    public static final int FIRE = 24;

    //status constants
    public static final int STATUE_W = 115;
    public static final int STATUE_H = 180;
    public static final String STATUE_IMG = "statue/statue";
    
    //lion right
    public static final int LION_RIGHT_W = 90;
    public static final int LION_RIGHT_H = 80;
    public static final String LION_RIGHT_IMG = "lion_statue_right.png";
    
     //lion left
    public static final int LION_LEFT_W = 90;
    public static final int LION_LEFT_H = 80;
    public static final String LION_LEFT_IMG = "lion_statue_left.png";
    
     //fire
    public static final int FIRE_W = 30;
    public static final int FIRE_H = 60;
    public static final String FIRE_IMG = "fire/fire";
    
    //Door/border sizes
    public static final int BORDER_THICK = 10;
    public static final int DOOR_GAP_W = 90;
    public static final int DOOR_GAP_H = 120;

    public static final int ENEMIES_TO_SPAWN = 1;//this is for level 0
    //public static final int WIN_ROOMS = 6;
    
    //SummonerBoss
    //public static final int SUMMONER_BOSS_DOOR_CLEARED=1;
    public static final int SUMMOMER_BOSS_MINION_SPWAN=2; //2 for gampley, use smaller number for testingpublic static final int SUMMOMER_BOSS_MINION_SPWAN=1; 
    public static final int SUMMONER_BOSS_MAX_HEALTH=150;  //180 for game, other numbers for testing
    
    //Warrior Selection;
    public static final int WARRIOR_AXE=1;
    public static final int WARRIOR_BULLET=2;
    public static final int WARRIOR_SWORD=3;
    
    //Warrior default attack power
    public static final int WARRIOR_AXE_DEFAULT_ATTACK=2;
    public static final int WARRIOR_BULLET_DEFAULT_ATTACK=5;
    public static final int WARRIOR_SWORD_DEFAULT_ATTACK=2;
    
    public static final int PLAYER_DOOR_OFFSET_X=50; //as player gets bigger, this should get bigger too
    public static final int PLAYER_DOOR_OFFSET_Y=40;
    
    
    //Default max HP for the player.
    public static final int DEFAULT_MAX_HP=100;
    //the larger the number reduce damage 
    //to sword and axe warrior as they need to be very close to enemy to hit
    public static final int DEFAULT_INVINCIBILITY_FRAMES=45;  
    public static final int MINIMUM_HP_TO_UPGRADE=20;
    
    //default stone
     public static final int DEFAULT_STONE_COUNT=2;
    
    //stone time
    public static final int STONE_TIME=120;//frames
    
    //dodge constants
    public static final int DODGE_ROOM_HAZARD_COUNT = 8;
    
    //file for reloading data later
    public static final String SAVE_FILE = "save.txt";
    
     /**
     * @return  SidePanel centreX position
     */
    public static int sidePanelCentreX() 
    {
        return PLAY_W + SIDE_PANEL_W / 2;
    }
    /**
     * @return  SidePanel centreX position
     */
    public static int sidePanelCentreY() 
    {
        return  WORLD_H / 2;
    }
    /**
     * @return  left pixel of tile column tc
     */
    public static int tileLeft(int tc) 
    {
        return GameConfig.ROOM_X + (int)Math.round(tc * (GameConfig.ROOM_W / (double)GameConfig.MAP_COLS));
    }

    /**
     * @return  right pixel of tile column tc
     */
    public static int tileRight(int tc) 
    {
        return GameConfig.ROOM_X + (int)Math.round((tc + 1) * (GameConfig.ROOM_W / (double)GameConfig.MAP_COLS));
    }
    /**
     * @return  top pixel of tile row tr
     */
    public static int tileTop(int tr) 
    {
        return GameConfig.ROOM_Y + (int)Math.round(tr * (GameConfig.ROOM_H / (double)GameConfig.MAP_ROWS));
    }

    /**
     * @return  Bottom pixel of tile row tr (inside the room)
     */
    public static int tileBottom(int tr) 
    {
        return GameConfig.ROOM_Y + (int)Math.round((tr + 1) * (GameConfig.ROOM_H / (double)GameConfig.MAP_ROWS));
    }
    /**
     * @return  Center X pixel of tile column tc
     */
    public static int tileCenterX(int tc) 
    {
        int x1 = tileLeft(tc);
        int x2 = tileRight(tc);
        return x1 + (x2 - x1) / 2;
    }

     /**
     * @return  Center Y pixel of tile row tr
     */
    public static int tileCenterY(int tr) 
    {
        int y1 = tileTop(tr);
        int y2 = tileBottom(tr);
        return y1 + (y2 - y1) / 2;
    }
    /**
     * @return center X of the room in world pixels
     */
    public static int roomCenterX() 
    { 
        return GameConfig.ROOM_X + GameConfig.ROOM_W / 2; 
    }

    /**
     * @return center Y of the room in world pixels 
     */
    public static int roomCenterY() 
    { 
        return GameConfig.ROOM_Y + GameConfig.ROOM_H / 2; 
    }

    /** 
     * @return left boundary of the room rectangle 
     */
    public static int roomLeft()   
    { 
        return GameConfig.ROOM_X; 
    }

    /**
     * @return right boundary of the room rectangle 
     */
    public static int roomRight()  
    { 
        return GameConfig.ROOM_X + GameConfig.ROOM_W; 
    }

    /** 
     * @return top boundary of the room rectangle 
     */
    public static int roomTop()    
    { 
        return GameConfig.ROOM_Y; 
    }

    /** 
     * @return bottom boundary of the room rectangle 
     */
    public static int roomBottom() 
    { 
        return GameConfig.ROOM_Y + GameConfig.ROOM_H;
    }
    
}