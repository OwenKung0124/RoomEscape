import greenfoot.*;

/**
 * RoomRenderer draws and builds 
 *  the room visuals
 *  collision borders
 *  doors.
 *
 * it does not decide door lock rules
 * it does not spawn enemies/coins.
 * 
 * it only builds the room structure.
 */
public class RoomRenderer 
{

    private final GameWorld world;
    private final GameMap map;

    public RoomRenderer(GameWorld world, GameMap map) 
    {
        this.world = world;
        this.map = map;
    }

    /**
     * Builds everything that visually belongs to the room:
     * - background + side panel
     * - doors on the border
     * - tile-based objects.
     */
    public void buildRoom(int r, int c) 
    {
        loadRoomBg(r, c);
        addBorderDoors(r, c);
        buildObjectsFromTiles(r, c);
        buildShopObjects(r, c); //shop objects can't be build from tiles
    }
    private void loadRoomBg(int r, int c)
    {
        GreenfootImage bg = new GreenfootImage(world.getWidth(), world.getHeight());
    
        //side panel area
        int panelX = GameConfig.ROOM_X + GameConfig.ROOM_W;
        int panelW = world.getWidth() - panelX;
    
        bg.setColor(new Color(40, 40, 40));
        bg.fillRect(panelX, 0, panelW, world.getHeight());
    
        //Draw the room background to cover the entire room area
        String bgName = map.getBgName(r, c);
        if (bgName != null) 
        {
            GreenfootImage roomBg = new GreenfootImage(bgName);
    
            roomBg.scale(panelX, world.getHeight());
            bg.drawImage(roomBg, 0, 0);
        } 
        else 
        {
            //no bg file
            bg.setColor(Color.DARK_GRAY);
            bg.fillRect(0, 0, panelX, world.getHeight());
        }
        world.setBackground(bg);
    }
    /**
     * /shop objects can't be build from tiles
     * 
     */ 
    private void buildShopObjects(int r, int c)
    {
        char roomType = map.getRoomType(r, c);
        if(roomType!='S')
        {
            return;
        }
        
        //don't put too close to entrances
        AttackUpgrade attackUpgrade = new AttackUpgrade(240,190);
        world.addObject(attackUpgrade, 250,550);
        
        HealthUpgrade healthUpgrade = new HealthUpgrade(240,190);
        world.addObject(healthUpgrade, 700,250);
        
        StoneSkill stoneSkill = new StoneSkill(240,190);
        world.addObject(stoneSkill, 700,550);
    }
    /**
     * Builds wall, blockers using the RoomData tile layout.
     *
     */
    public void buildObjectsFromTiles(int r, int c)
    {
        RoomData rd = map.getRoomData(r, c);
        if (rd == null) return;
    
        for (int tr = 0; tr < GameConfig.MAP_ROWS; tr++)
        {
            for (int tc = 0; tc < GameConfig.MAP_COLS; tc++)
            {
                int code = rd.tiles[tr][tc];
    
                int x1 = GameConfig.tileLeft(tc);
                int x2 = GameConfig.tileRight(tc);
                int y1 = GameConfig.tileTop(tr);
                int y2 = GameConfig.tileBottom(tr);
    
                int w = x2 - x1;
                int h = y2 - y1;
                
                //centre x y for adding objects
                int x = x1 + w / 2;
                int y = y1 + h / 2;
            
                if (code == GameConfig.DOOR) 
                {
                    //use addBorderDoors
                    continue;
                }
    
                if (code == GameConfig.INTERIOR_WALL)
                {
                    world.addObject(new InteriorWall(w, h), x, y);
                }
                else if (code == GameConfig.EXTERIOR_WALL)
                {
                    world.addObject(new ExteriorWall(w, h), x, y);
                }
                else if (code == GameConfig.COIN) 
                {
                    world.addObject(new Coin(rd,r, c, tr, tc), x, y);
                }
                else if (code == GameConfig.STATUE) 
                {
                    Statue s = new Statue(w,h);
                    //to allow status to sit 
                    //on the floor" instead of centered
                    int dy = (s.getImage().getHeight() / 2) - (h / 2);
                    world.addObject(s, x, y-dy);
                }
                else if (code == GameConfig.LION_RIGHT) 
                {
                    LionRight lion = new LionRight(w,h);
                    
                    //to allow status to sit 
                    //on the floor" instead of centered
                    int dy = (lion.getImage().getHeight() / 2) - (h / 2);
                    
                    world.addObject(lion, x, y-dy);          
                }
                else if (code == GameConfig.LION_LEFT) 
                {
                    LionLeft lion = new LionLeft(w,h);
                    
                    //to allow status to sit 
                    //on the floor" instead of centered
                    int dy = (lion.getImage().getHeight() / 2) - (h / 2);
                    
                    world.addObject(lion, x, y-dy); 
                }
                else if (code == GameConfig.FIRE) 
                {
                    Fire fire = new Fire(w,h);
                    
                    int dy = (fire.getImage().getHeight() / 2) - (h / 2);
                    
                    world.addObject(fire, x, y-dy);
                }
            }
        }
    }
    /**
     * Places Door actors based on:
     *   Neighbor room exists in the RoomMap grid
     *   The tile layout has DOOR (3) markers on the edge.
     */
    private void addBorderDoors(int r, int c)
    {
        RoomData rd = map.getRoomData(r, c);
        if (rd == null) return;
    
        //UP edge (row 0): look for DOOR markers on the top border
        if (map.hasRoom(r - 1, c))
        {
            int x = findDoorCentreX(rd, 0);
            if (x != -1)
            {
                int y = GameConfig.tileCenterY(0);
                world.addObject(new Door("UP", -1, 0), x, y);
            }
        }
    
        //DOWN edge (last row)
        if (map.hasRoom(r + 1, c))
        {
            int tr = GameConfig.MAP_ROWS - 1;
            int x = findDoorCentreX(rd, tr);
            if (x != -1)
            {
                int y = GameConfig.tileCenterY(tr);
                world.addObject(new Door("DOWN", 1, 0), x, y);
            }
        }
    
        //LEFT edge (col 0)
        if (map.hasRoom(r, c - 1))
        {
            int y = findDoorCenterY(rd, 0);
            if (y != -1)
            {
                int x = GameConfig.tileCenterX(0);
                world.addObject(new Door("LEFT", 0, -1), x, y);
            }
        }
    
        //RIGHT edge (last col)
        if (map.hasRoom(r, c + 1))
        {
            int tc = GameConfig.MAP_COLS - 1;
            int y = findDoorCenterY(rd, tc);
            if (y != -1)
            {
                int x = GameConfig.tileCenterX(tc);
                world.addObject(new Door("RIGHT", 0, 1), x, y);
            }
        }
    }

    /**
     * Finds the center X (in pixels) of the FIRST continuous run of DOOR tiles
     * on a specific row (used for top/bottom borders).
     *
     * @param rd the room data
     * @param tr which row (0 for top, MAP_ROWS-1 for bottom)
     * @return center X in pixels, or -1 if no DOOR tiles on that edge
     */
    private int findDoorCentreX(RoomData rd, int tr)
    {
        int start = -1;
        int end = -1;
    
        for (int tc = 0; tc < GameConfig.MAP_COLS; tc++)
        {
            if (rd.tiles[tr][tc] == GameConfig.DOOR)
            {
                if (start == -1) start = tc;
                end = tc;
            }
            else
            {
                //if we already started a run, stop at the first gap
                if (start != -1) break;
            }
        }
    
        if (start == -1) return -1;
    
        // center of the run using exact tile math
        int leftPx = GameConfig.tileLeft(start);
        int rightPx = GameConfig.tileRight(end);
        return leftPx + (rightPx - leftPx) / 2;
    }
    
    /**
     * Finds the center Y (in pixels) of the FIRST continuous run of DOOR tiles
     * on a specific column (used for left/right borders).
     *
     * @param rd the room data
     * @param tc which column (0 for left, MAP_COLS-1 for right)
     * @return center Y in pixels, or -1 if no DOOR tiles on that edge
     */
    private int findDoorCenterY(RoomData rd, int tc)
    {
        int start = -1;
        int end = -1;
    
        for (int tr = 0; tr < GameConfig.MAP_ROWS; tr++)
        {
            if (rd.tiles[tr][tc] == GameConfig.DOOR)
            {
                if (start == -1) start = tr;
                end = tr;
            }
            else
            {
                if (start != -1) break;
            }
        }
    
        if (start == -1) return -1;
    
        int topPx = GameConfig.tileTop(start);
        int bottomPx = GameConfig.tileBottom(end);
        return topPx + (bottomPx - topPx) / 2;
    }

    /**
     * Walkable tiles are allowed to be an opening.
     *
     * UPDATE:
     * - CLEAR_WALL (3) is treated as walkable so Doors spawn on code=3.
     */
    private boolean isWalkable(int code)
    {
        return code == GameConfig.FLOOR
            || code == GameConfig.COIN
            || code == GameConfig.DOOR;
    }

    private boolean hasTopOpening(RoomData rd, int[] cols)
    {
        int tr = 0;
        for (int tc : cols) 
        {
            if (!isWalkable(rd.tiles[tr][tc])) return false;
        }
        return true;
    }

    private boolean hasBottomOpening(RoomData rd, int[] cols)
    {
        int tr = GameConfig.MAP_ROWS - 1;
        for (int tc : cols) 
        {
            if (!isWalkable(rd.tiles[tr][tc])) return false;
        }
        return true;
    }

    private boolean hasLeftOpening(RoomData rd, int[] rows)
    {
        int tc = 0;
        for (int tr : rows) 
        {
            if (!isWalkable(rd.tiles[tr][tc])) return false;
        }
        return true;
    }

    private boolean hasRightOpening(RoomData rd, int[] rows)
    {
        int tc = GameConfig.MAP_COLS - 1;
        for (int tr : rows) {
            if (!isWalkable(rd.tiles[tr][tc])) return false;
        }
        return true;
    }

    /**
     * Door opening width in tiles:
     * - If MAP_COLS is even, use 2 tiles in the middle
     * - If odd, use 1 tile in the middle
     */
    private int[] getDoorCols()
    {
        int cols = GameConfig.MAP_COLS;
        int mid = cols / 2;

        if (cols % 2 == 0) {
            return new int[]{mid - 1, mid};
        }
        return new int[]{mid};
    }

    /**
     * Door opening height in tiles:
     * - If MAP_ROWS is even, use 2 tiles in the middle
     * - If odd, use 1 tile in the middle
     */
    private int[] getDoorRows()
    {
        int rows = GameConfig.MAP_ROWS;
        int mid = rows / 2;

        if (rows % 2 == 0) {
            return new int[]{mid - 1, mid};
        }
        return new int[]{mid};
    }

    private int edgeCenterXFromCols(int[] cols)
    {
        int sum = 0;
        for (int tc : cols) sum += GameConfig.tileCenterX(tc);
        return sum / cols.length;
    }

    private int edgeCenterYFromRows(int[] rows)
    {
        int sum = 0;
        for (int tr : rows) sum += GameConfig.tileCenterY(tr);
        return sum / rows.length;
    }
}