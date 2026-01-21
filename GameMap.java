import java.util.StringTokenizer;

/**
 * RoomMap stores all "map data" for the whole game:
 * - which rooms exist (grid)
 * - which background each room uses
 * - per-room tile data (RoomData)
 * - visited / cleared state
 *
 * GameWorld asks RoomMap for information, but RoomMap itself does not spawn Actors.
 * 
 * @author:     Owen Kung, Cartis Lee
 * @version:    Jan 2026
 */
public class GameMap 
{

    //Room grid map 
    //1=room exists
    //0 = no room)
    private int[][] grid=
    {
        {1, 1, 0, 1},
        {0, 1, 1, 1},
        {0, 1, 0, 1},
        {1, 1, 1, 1}
    };

    //B=boss < no more boss room for now
    //T=treasure
    //C=combat
    //S=shop
    //R=rush room, must exit in certain time
    //D=must not touch moving actor in the room for certain time period
    //n=not a room
    private char[][] roomType=
    {
        {'C', 'D', 'N', 'C'},
        {'N', 'C', 'R', 'S'},
        {'N', 'C', 'N', 'C'},
        {'S', 'D', 'C', 'T'}
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
            //treasure room
            return new int[][] {
                {2,2,2,2,2,24,2,2,2,2,24,2,2,2,2,2},
                {2,0,21,0,0,0,0,0,0,0,0,0,0,21,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r==0 && c==1) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,21,0,0,0,0,0,0,0,0,0,0,21,0,2},
                {2,0,0,0,0,0,11,11,11,11,0,0,0,0,0,2},
                {3,0,0,0,0,0,11,22,23,11,0,0,0,0,0,22},
                {3,0,0,0,0,0,11,22,23,11,0,0,0,0,0,2},
                {2,0,0,0,0,0,11,0,0,11,0,0,0,0,0,22},
                {2,0,0,24,0,0,0,0,0,0,0,24,0,0,0,2},
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
                {2,0,0,11,0,0,0,0,0,11,0,0,0,0,0,2},
                {2,0,0,0,11,0,0,11,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==0 && c==3) {
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        //starting room
        if (r==1 && c==1) {
            return new int[][]  {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,21,0,0,0,0,21,0,0,0,0,3},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==1 && c==2) {
            //rush
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,11,24,11,0,0,0,0,0,11,24,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,22,0,0,24,0,0,23,0,0,0,3},
                {3,0,0,0,0,22,0,0,0,0,0,23,0,0,0,3},
                {2,0,0,0,0,0,0,0,23,0,0,24,0,0,0,2},
                {2,0,0,0,0,0,0,0,23,0,0,0,0,0,0,2},
                {2,0,0,0,11,24,11,0,0,0,0,0,0,0,11,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==1 && c==3) {
            //shop
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==2 && c==1) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,21,0,0,0,0,21,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==2 && c==3) {
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {2,0,0,0,24,0,0,0,0,0,0,0,24,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==0 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {23,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==1 ){
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,24,11,0,0,0,0,0,0,0,0,11,24,0,2},
                {2,0,0,0,0,11,0,0,0,0,11,0,0,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,11,0,11,0,0,11,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,24,0,0,0,0,0,0,24,0,0,0,2},
                {2,0,11,0,0,0,0,11,0,0,0,0,0,11,0,2},
                {2,2,2,2,2,2,2,24,24,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==2 ){
            return new int[][] {
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,24,0,0,0,0,0,0,0,0,24,0,0,2},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,24,0,0,0,0,0,0,0,0,24,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
        if (r==3 && c==3 ){
            return new int[][] {
                {2,2,2,2,2,2,2,3,3,2,2,2,2,2,2,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,22,0,0,0,0,0,0,23,0,0,0,2},
                {3,0,0,0,0,0,11,11,11,11,0,0,0,0,0,2},
                {3,0,0,0,0,0,11,11,11,11,0,0,0,0,0,2},
                {2,0,0,0,22,0,0,11,11,0,0,23,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
            };
        }
       
        return null;
    }

    //makes a safe default 16x9 layout: all walkable floor
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

    /**
     * get background image name for room r, c
     * 
     * @param r room row
     * @param c room column
     * @return background name of the room, r ,c
     */
    public String getBgName(int r, int c) 
    {
        return roomBg[r][c];
    }
    /**
     * get room laybout data for room r, c
     * 
     * @param r room row
     * @param c room column
     * @return RoomData room layout information for room R C
     */
    public RoomData getRoomData(int r, int c) {
        return rooms[r][c];
    }
    /**
     * set room r c as visited
     * 
     * @param r room row
     * @param c room column
     */
    public void setVisited(int r, int c) {
        visited[r][c]=true;
    }
    /**
     * check to see if room r c was visited
     * 
     * @param r room row
     * @param c room column
     * @boolean if room was visited return true
     */
    public boolean wasVisited(int r, int c) {
        return visited[r][c];
    }
    /**
     * check to see if room r c is cleared
     * 
     * @param r room row
     * @param c room column
     * @boolean if room was cleared return true
     */
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
    /**
     * get the room type for room r c
     * 
     * @param r room row
     * @param c room column
     * @return the room type of r c
     */
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
        return hasRoom(r, c) && getRoomType(r, c) == 'C';
    }
    /**
     *  Boss room rule:
     * 'M' means batlle room in roomType[][].
     */
    public boolean isBossRoom(int r, int c)
    {
//for testingif(r==2 && c==1) return true;


        //must be a battle room itself
        if (!isCombatRoom(r,c))
        {
            return false;
        }

        //count how many battle rooms remain uncleared
        int remaining = 0;

        for (int rr = 0; rr < getRows(); rr++)
        {
            for (int cc = 0; cc < getCols(); cc++)
            {
                if (hasRoom(rr, cc) && isCombatRoom(rr, cc))
                {
                    if (!isCleared(rr, cc))
                    {
                         remaining++;   
                    }
                }
            }
        }

        // If only THIS battle room remains uncleared, then it's the last one
        return (remaining == 1 && !isCleared(r, c));
    
        //return hasRoom(r, c) && getRoomType(r, c) == 'B';
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
        String exportStr = "";

        for (int r = 0; r < getRows(); r++)
        {
            if (r > 0) 
            {
                exportStr += "/";
            }
            for (int c = 0; c < getCols(); c++)
            {
                if (cleared[r][c]) 
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
     * Import visited string and apply to visited[][].
     * Only sets true for '1'. Leaves everything else as-is/false.
     */

    public void importVisited(String data)
    {
        if (data == null) return;
    
        data = data.trim();
        if (data.length() == 0) return;
    
        StringTokenizer st = new StringTokenizer(data, "/");
        int r = 0;
    
        while (st.hasMoreTokens() && r < getRows())
        {
            String row = st.nextToken();
    
            for (int c = 0; c < row.length() && c < getCols(); c++)
            {
                if (row.charAt(c) == '1')
                {
                    visited[r][c] = true;
                }
            }
    
            r++;
        }
    }
    /**
     * Import cleared string and apply to cleared[][].
     * Also sets visited=true when a room is cleared
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
                    //visited[r][c] = true;
                }
            }
        
            r++;
        }
    }
    /**
     * Count the number of room existis for room type type
     */
    public int countTotalForRoomType(char type)
    {
        int count = 0;
    
        for (int r = 0; r < getRows(); r++)
        {
            for (int c = 0; c < getCols(); c++)
            {
                if (hasRoom(r, c) && roomType[r][c] == type)
                {
                    count++;
                }
            }
        }
    
        return count;
    }
    /**
     * if current room is trap room with room type='R"
     * 
     * @return if current room is a trap room
     */
    public boolean isTrapRoom(int r, int c)
    {
        return getRoomType(r, c) == 'R';
    }
     /**
     * if current room is a dodge room with room type='D"
     * 
     * @return if current room is a dodge room
     */
    public boolean isDodgeRoom(int r, int c)
    {
        return getRoomType(r, c) == 'D';
    }
    /**
     * Helper methods for MiniMap
     * 
     * @return total rows in game map
     */
    public int getRows()
    { 
        return grid.length; 
    }
    /**
     * Helper methods for MiniMap
     * 
     * @return total columns in game map
     */
    public int getCols()
    { 
        return grid[0].length; 
    }
}