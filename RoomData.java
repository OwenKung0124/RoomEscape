import java.util.StringTokenizer;

/**
 * RoomData stores the tile map for ONE room.
 * Each room must have its own RoomData, so changes (coins collected, etc.)
 * only affect that one room.
 * 
 * @author:Owen Kung, Cartis Lee
 * @version Jan 2026
 */
public class RoomData 
{

    //rhe tile information for the room
    public int[][] tiles;

    /**
     * creates a room using a template map
     *
     * @param template: the template tiles for a room layout
     */
    public RoomData(int[][] template) 
    {
        tiles=deepCopy(template);
    }

    /**
     * makes a deep copy of a 2D int array.
     * this prevents rooms from sharing the same tile array.
     */
    private int[][] deepCopy(int[][] src) 
    {
        int[][] copied=new int[src.length][];
        for (int r=0; r < src.length; r++) 
        {
            copied[r]=new int[src[r].length];
            for (int c=0; c < src[r].length; c++) 
            {
                copied[r][c]=src[r][c];
            }
        }
        return copied;
    }
    /**
     * Set particular tile @ tr tc to floor type where code=floorCode
     * 
     * @param tr:           the tile row
     * @param tc:           the tile column
     * @paran floorCode:    new floorcode
     */
    public void setTile(int tr, int tc, int floorCode)
    {
       tiles[tr][tc]=floorCode; //permanently set the particular to be item from that room’s layout 
    }
    /**
     * Translate this room's tiles into a string.
     * Format: rows separated by '/', cols separated by ','
     * Example: "2,2,2/2,0,0/..."
     */
    public String exportTiles()
    {
        if (tiles==null || tiles.length==0) return "";

        String exportStr="";        
        for (int r=0; r < tiles.length; r++) 
        {
            if (r > 0)
            {
                exportStr += "/";
            }
            for (int c=0; c < tiles[r].length; c++) 
            {
                if (c > 0)
                {
                    exportStr += ",";   
                }
                exportStr += tiles[r][c];
            }
        }

        return exportStr;
    }

    /**
     * Translate a tile string back into this room's tiles.
     */
    public void importTiles(String tileInfoString)
    {
        if (tileInfoString==null) return;
        
        tileInfoString=tileInfoString.trim();
        if (tileInfoString.length()==0) return;
        if (tiles==null || tiles.length==0) return;
        
        StringTokenizer rowTok=new StringTokenizer(tileInfoString, "/");
        int r=0;
        while (rowTok.hasMoreTokens() && r < tiles.length)
        {
            String rowStr=rowTok.nextToken();
            if (rowStr==null) 
            { 
                r++; 
                continue; 
            }
        
            rowStr=rowStr.trim();
            if (rowStr.length()==0) 
            { 
                r++; 
                continue; 
            }
        
            StringTokenizer colStr=new StringTokenizer(rowStr, ",");
            int c=0;
        
            while (colStr.hasMoreTokens() && c < tiles[r].length)
            {
                String cell=colStr.nextToken();
                if (cell != null)
                {
                    cell=cell.trim();
                    try
                    {
                        tiles[r][c]=Integer.parseInt(cell);
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println(cell+" can't be converted to number.");
                        tiles[r][c]=GameConfig.FLOOR; //assume it's a floor
                    }
                }
                
                c++;
            }
        
            r++;
        }
    }
}