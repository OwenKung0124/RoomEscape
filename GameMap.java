import java.util.StringTokenizer;

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
    //(1=room exists, 0 = no room)
    private int[][] grid=
    {
        {1, 1, 0, 1},
        {0, 1, 1, 1},
        {0, 1, 0, 1},
        {1, 1, 1, 1}
    };

    private char[][] roomType=
    {
        {'T', 'B', 'N', 'T'},
        {'N', 'B', 'B', 'S'},
        {'N', 'B', 'N', 'B'},
        {'T', 'B', 'B', 'T'}
    };
    //background image name per room
    //all the same for now
    private String[][] roomBg=
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

    //tile data per room
    private RoomData[][] rooms;

    public GameMap() 
    {
        //initialize to false default value
        cleared=new boolean[grid.length][grid[0].length];
        visited=new boolean[grid.length][grid[0].length];

        rooms=new RoomData[grid.length][grid[0].length];
        initRooms();
    }

    /**
     * Creates a separate RoomData for every room that exists in the grid.
     * Even if they share the same layout template, each room gets its own copy
     * so coins/walls changes do not affect other rooms.
     */
    private void initRooms()
    {
        for (int r=0; r < grid.length; r++) {
            for (int c=0; c < grid[0].length; c++) {
    
                if (grid[r][c] != 1) {
                    rooms[r][c]=null;
                    continue;
                }
    
                int[][] layout=createLayoutFor(r, c);
    
                //for those floor layout not defined
                //make it all floor so it still works
                if (layout== null)
                {
                    layout=makeAllFloorLayout();
                }
    
                rooms[r][c]=new RoomData(layout);
            }
        }
    }

    /**
     * Returns the 16x9 tile layout for a specific room (r,c).
     * Each room should return its own int[][] (FLOOR/WALL/EXTERIOR_WALL/CLEAR_WALL/COIN/etc).
     */
    private int[][] createLayoutFor(int r, int c)
    {
        if (r== 0 && c==0) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,11,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,11,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,11,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,11,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,11,0,0,0,0,0,0,0,0,0,3},
                {2,0,1,1,0,0,11,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,11,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r==0 && c==1) {
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
        if (r==0 && c==2) {
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
        if (r==0 && c==3) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,11,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,11,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,11,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,11,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,11,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,11,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,11,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        //starting room
        if (r==1 && c==1) {
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
        if (r==1 && c==2) {
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
        if (r==1 && c==3) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,0,1,0,0,0,0,0,2},
                {3,0,11,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,11,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==2 && c==1) {
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
        if (r==2 && c==3) {
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
        if (r==3 && c==0 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,11,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,11,0,0,0,0,0,11,11,0,0,0,0,0,2},
                {2,0,0,11,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,11,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,11,0,0,0,11,0,0,0,0,0,2},
                {2,0,1,1,0,0,11,0,11,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,11,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==1 ){
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
        if (r==3 && c==2 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,0,0,0,0,0,1,1,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,1,1,0,0,0,0,0,1,1,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==3 ){
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,11,0,0,0,0,0,0,0,0,11,0,0,0,0,2},
                {2,0,11,0,0,0,0,0,0,0,11,0,0,0,0,2},
                {3,0,0,11,0,0,0,0,0,0,11,0,0,0,0,2},
                {3,0,0,11,0,0,0,0,0,0,11,0,0,0,0,2},
                {2,0,0,11,0,0,0,0,0,0,11,0,0,0,0,2},
                {2,0,0,11,0,0,0,0,0,11,0,0,0,0,0,2},
                {2,0,11,0,0,0,0,0,11,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
       

        // TODO: define every room you want (r,c)
        return null;
    }

    //Makes a safe default 16x9 layout: all walkable floor
    private int[][] makeAllFloorLayout()
    {
        int[][] out=new int[GameConfig.MAP_ROWS][GameConfig.MAP_COLS];
        for (int tr=0; tr < GameConfig.MAP_ROWS; tr++) 
        {
            for (int tc=0; tc < GameConfig.MAP_COLS; tc++) out[tr][tc]=GameConfig.FLOOR;
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
            && grid[r][c]==1;
    }

    public String getBgName(int r, int c) 
    {
        return roomBg[r][c];
    }

    public RoomData getRoomData(int r, int c) {
        return rooms[r][c];
    }

    public void setVisited(int r, int c) {
        visited[r][c]=true;
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
            cleared[r][c]=true;
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
        //preferred starting room
        if (hasRoom(1, 1))
        {
            return new int[] {1, 1};
        }
    
        //scan for the first existing room
        for (int r=0; r < grid.length; r++)
        {
            for (int c=0; c < grid[0].length; c++)
            {
                if (grid[r][c]==1)
                {
                    return new int[] {r, c};
                }
            }
        }
    
        return new int[] {0, 0};
    }
    public char getRoomType(int r, int c)
    {
        return roomType[r][c];
    }
    
    /**
     * Combat room rule:
     * 'B' means batlle room in roomType[][].
     */
    public boolean isCombatRoom(int r, int c)
    {
        return hasRoom(r, c) && getRoomType(r, c) == 'B';
    }

    /**
     * Export visited[][] into a string like: 1101/0111/0101/1111
     */
    public String exportVisited()
    {
        String exportStr = "";

        for (int r = 0; r < getRows(); r++)
        {
            if (r > 0) 
            {
                exportStr += "/";
            }
            for (int c = 0; c < getCols(); c++)
            {
                if (visited[r][c]) 
                {
                    exportStr += "1";
                } 
                else 
                {
                    exportStr += "0";
                }
            }
        }
        
        return exportStr;
    }
    
    /**
     * Export cleared[][] into a string like: 1000/0100/0000/0000
     */
    public String exportCleared()
    {
        StringBuilder sb=new StringBuilder();
    
        for (int r=0; r < getRows(); r++)
        {
            if (r > 0) sb.append('/');
    
            for (int c=0; c < getCols(); c++)
            {
                sb.append(cleared[r][c] ? '1' : '0');
            }
        }
    
        return sb.toString();
    }
    
    /**
     * Import visited string and apply to visited[][].
     * Only sets true for '1'. Leaves everything else as-is/false.
     */
    public void importVisited(String data)
    {
        if (data==null) return;
        
        data=data.trim();
        if (data.length()==0) return;
    
        String[] rows=data.split("/");
    
        for (int r=0; r < rows.length && r < getRows(); r++)
        {
            String row=rows[r];
    
            for (int c=0; c < row.length() && c < getCols(); c++)
            {
                if (row.charAt(c)=='1')
                {
                    visited[r][c]=true;
                }
            }
        }
    }
    /**
     * Import cleared string and apply to cleared[][].
     * Also sets visited=true when a room is cleared (same behavior as your old applyCleared).
     */
    public void importCleared(String data)
    {
        if (data == null) return;

        data = data.trim();
        if (data.length() == 0) return;
        
        StringTokenizer rows = new StringTokenizer(data, "/");
        int r = 0;
        while (rows.hasMoreTokens() && r < getRows())
        {
            String row = rows.nextToken();
            if (row == null) { r++; continue; }
        
            int maxC = Math.min(row.length(), getCols());
        
            for (int c = 0; c < maxC; c++)
            {
                if (row.charAt(c) == '1')
                {
                    cleared[r][c] = true;
                    visited[r][c] = true;
                }
            }
        
            r++;
        }
    }
    public int getRows()
    { 
        return grid.length; 
    }
    public int getCols()
    { 
        return grid[0].length; 
    }
}