/**
 * RoomMap stores all "map data" for the whole game:
 * - which rooms exist (grid)
 * - which background each room uses
 * - per-room tile data (RoomData)
 * - visited / cleared state
 *
 * GameWorld asks RoomMap for information, but RoomMap itself does not spawn Actors.
 */
public class GameMap {

    //Room grid map 
    //(1 = room exists, 0 = no room)
    private int[][] grid = 
    {
        {1, 1, 0, 1},
        {0, 1, 1, 1},
        {0, 1, 0, 1},
        {1, 1, 1, 1}
    };

    /** Background image name per room (same size as grid). */
    private String[][] roomBg = 
    {
        {"bg/general_bg.jpg",   "bg/general_bg.jpg",    null,                   "bg/general_bg.jpg"},
        {null,                  "bg/general_bg.jpg",    "bg/general_bg.jpg",    "bg/general_bg.jpg"},
        {null,                  "bg/general_bg.jpg",    null,                   "bg/general_bg.jpg"},
        {"bg/general_bg.jpg",   "bg/general_bg.jpg",    "bg/general_bg.jpg",    "bg/general_bg.jpg"}
    };

    //cleared[r][c]
    //whether this room was cleared before
    private boolean[][] cleared;

    //visited[r][c]
    //whether this room was visited before
    private boolean[][] visited;

    //ile data per room (same indexing as grid)
    private RoomData[][] rooms;

    public GameMap() 
    {
        //initialize to false default value
        cleared = new boolean[grid.length][grid[0].length];
        visited = new boolean[grid.length][grid[0].length];

        rooms = new RoomData[grid.length][grid[0].length];
        initRooms();
    }

    /**
     * Creates a separate RoomData for every room that exists in the grid.
     * Even if they share the same layout template, each room gets its own copy
     * so coins/walls changes do not affect other rooms.
     */
    private void initRooms()
    {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
    
                if (grid[r][c] != 1) {
                    rooms[r][c] = null;
                    continue;
                }
    
                int[][] layout = createLayoutFor(r, c);
    
                //for those floor layout not defined
                //make it all floor so it still works
                if (layout == null)
                {
                    layout = makeAllFloorLayout();
                }
    
                rooms[r][c] = new RoomData(layout);
            }
        }
    }

    /**
     * Returns the 16x9 tile layout for a specific room (r,c).
     * Each room should return its own int[][] (FLOOR/WALL/EXTERIOR_WALL/CLEAR_WALL/COIN/etc).
     */
    private int[][] createLayoutFor(int r, int c)
    {
        if (r == 0 && c == 0) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r == 0 && c == 1) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 0 && c == 2) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,11,0,0,0,0,0,11,1,0,0,0,0,2},
                {2,0,0,0,11,0,0,11,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 0 && c == 3) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        //starting room
        if (r == 1 && c == 1) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,11,21,0,0,0,0,0,11,21,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,11,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 1 && c == 2) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,11,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,11,0,0,21,0,0,0,0,0,11,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,11,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r == 1 && c == 3) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,0,0,0,0,2},
                {3,0,11,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,11,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 2 && c == 1) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 2 && c == 3) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,0,1,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,21,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 3 && c == 0 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r == 3 && c == 1 ){
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,0,1,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r == 3 && c == 2 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r == 3 && c == 3 ){
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
       

        // TODO: define every room you want (r,c)
        return null;
    }

    //Makes a safe default 16x9 layout: all walkable floor
    private int[][] makeAllFloorLayout()
    {
        int[][] out = new int[GameConfig.MAP_ROWS][GameConfig.MAP_COLS];
        for (int tr = 0; tr < GameConfig.MAP_ROWS; tr++) 
        {
            for (int tc = 0; tc < GameConfig.MAP_COLS; tc++) out[tr][tc] = GameConfig.FLOOR;
        }
        return out;
    }

    /**
     * Checks if a room exists at (r,c).
     *
     * @param r row
     * @param c col
     * @return true if inside bounds and grid cell is 1
     */
    public boolean hasRoom(int r, int c) 
    {
        return r >= 0 && r < grid.length
            && c >= 0 && c < grid[0].length
            && grid[r][c] == 1;
    }

    public String getBgName(int r, int c) 
    {
        return roomBg[r][c];
    }

    public RoomData getRoomData(int r, int c) {
        return rooms[r][c];
    }

    public void setVisited(int r, int c) {
        visited[r][c] = true;
    }

    public boolean wasVisited(int r, int c) {
        return visited[r][c];
    }

    public boolean isCleared(int r, int c) {
        return cleared[r][c];
    }

    /**
     * Mark room as cleared. 
     * Returns true if this call changed it from false -> true.
     */
    public boolean markCleared(int r, int c) 
    {
        if (!cleared[r][c]) {
            cleared[r][c] = true;
            return true;
        }
        return false;
    }
    /**
     * Finds a safe starting room.
     *
     * Priority:
     * - Use the "starting room" (1,1) if it exists
     * - Otherwise return the first existing room found in the grid
     *
     * @return an int array {roomR, roomC}
     */
    public int[] findFirstRoom()
    {
        //Preferred starting room (you labelled this in createLayoutFor)
        if (hasRoom(1, 1))
        {
            return new int[] {1, 1};
        }
    
        //scan for the first existing room
        for (int r = 0; r < grid.length; r++)
        {
            for (int c = 0; c < grid[0].length; c++)
            {
                if (grid[r][c] == 1)
                {
                    return new int[] {r, c};
                }
            }
        }
    
        //Safety fallback 
        return new int[] {0, 0};
    }
    // ===== MiniMap helpers =====
    public int getRows() { return grid.length; }
    public int getCols() { return grid[0].length; }
}