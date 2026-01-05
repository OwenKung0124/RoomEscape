/**
 * RoomData stores the tile map for ONE room.
 * Each room must have its own RoomData, so changes (coins collected, etc.)
 * only affect that one room.
 */
public class RoomData 
{

    //The tile grid for this room 
    //(each cell is a tile code like FLOOR/WALL/COIN/STATUE
    public int[][] tiles;

    /**
     * Creates a room using a TEMPLATE map, but makes a deep copy so it's independent.
     *
     * @param template the template tiles for a room layout
     */
    public RoomData(int[][] template) 
    {
        tiles = deepCopy(template);
    }

    /**
     * Makes a deep copy of a 2D int array.
     * This prevents rooms from sharing the same tile array.
     */
    private int[][] deepCopy(int[][] src) 
    {
        int[][] out = new int[src.length][];
        for (int r = 0; r < src.length; r++) 
        {
            out[r] = new int[src[r].length];
            for (int c = 0; c < src[r].length; c++) 
            {
                out[r][c] = src[r][c];
            }
        }
        return out;
    }
    /**
     * Set Particular tile @ tr tc to floor type where code=floorCode
     * 
     * @param tr the tile row
     * @param tc the tile column
     * @paran floorCode new floorcode
     */
    public void setTile(int tr, int tc, int floorCode)
    {
       tiles[tr][tc] = floorCode; //permanently set the particular to be item from that room’s layout 
    }
}