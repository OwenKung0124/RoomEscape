import java.io.*;
import java.util.Scanner;

/**
 * SaveManager reads/writes simple save data to a text file.
 *
 * Saved data 
 * - roomR, roomC
 * - visited grid
 * - cleared grid
 * - lastRoomR,lastRoomC
 *
 * File format example:
 * roomR=1
 * roomC=2
 * visited=1101/0111/0101/1111
 * cleared=1000/0100/0000/0000
 * playerHealt=5
 * roomsCleared=2
 * coins=10;
 * tiles=0,0:2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2/2,1.....,0,.....2,2,2
 */
public class SaveManager
{

    /**
     * @return true if a save file exists
     */
    public static boolean hasSave()
    {
        //simple check
        //no exception to catch
        //because has not tried to read files.
        File file = new File(GameConfig.SAVE_FILE);
        
        //check to see if file exists
        //and file size >0
        if(file.exists() && file.length()>0)
        {
            return true;
        }
        return false;
    }

    /**
     * Deletes the save file (used for "New Game").
     */
    public static void deleteSave()
    {
        File f = new File(GameConfig.SAVE_FILE);
        if (f.exists())
        {
            f.delete();
        }
    }

    /**
     * Saves progress to file using SaveData.
     *
     * @param data all info to save
     */
    public static void save(SaveData data, GameMap map)
    {
        if (data == null) return;
    
        try
        {
            PrintWriter out = new PrintWriter(new FileWriter(GameConfig.SAVE_FILE));
    
            out.println("roomR=" + data.roomR);
            out.println("roomC=" + data.roomC);
            out.println("lastRoomR=" + data.lastRoomR);
            out.println("lastRoomC=" + data.lastRoomC);
            out.println("playerHealth=" + data.playerHealth);
            out.println("roomsCleared=" + data.roomsCleared);
            out.println("coins=" + data.coins);
            out.println("score=" + data.score);
            
            if(map!=null)
            {
                out.println("visited=" + (map == null ? "" : map.exportVisited()));
                out.println("cleared=" + (map == null ? "" : map.exportCleared()));
                
                saveRoomData(map,out);
            }

            out.close();
        }
        catch (IOException e)
        {
            System.out.println("Save failed: " + e.getMessage());
        }
    }
    
    /**
     * Loads progress from file and applies visited/cleared onto the map.
     *
     * @param map map to apply visited/cleared into
     * @return SaveData if loaded, or null if missing/invalid
     */
    public static SaveData load(GameMap map)
    {
        if (map == null) return null;
        if (!hasSave()) return null;
    
        SaveData data = new SaveData();
    
        //defaults
        data.roomR = 0;
        data.roomC = 0;
        data.lastRoomR = 0;
        data.lastRoomC = 0;
    
        Scanner sc = null;
    
        try
        {
            sc = new Scanner(new File(GameConfig.SAVE_FILE));
    
            while (sc.hasNextLine())
            {
                String line = sc.nextLine().trim();
    
                if (line.startsWith("roomR="))
                {
                    data.roomR = parseIntSafe(line.substring("roomR=".length()), 0);
                }
                else if (line.startsWith("roomC="))
                {
                    data.roomC = parseIntSafe(line.substring("roomC=".length()), 0);
                }
                else if (line.startsWith("lastRoomR="))
                {
                    data.lastRoomR = parseIntSafe(line.substring("lastRoomR=".length()), data.roomR);
                }
                else if (line.startsWith("lastRoomC="))
                {
                    data.lastRoomC = parseIntSafe(line.substring("lastRoomC=".length()), data.roomC);
                }
                else if (line.startsWith("roomsCleared="))
                {
                    data.roomsCleared=parseIntSafe(line.substring("roomsCleared=".length()), 0);
                }
                else if (line.startsWith("coins="))
                {
                    data.coins=parseIntSafe(line.substring("coins=".length()), 0);
                }
                else if (line.startsWith("score="))
                {
                    data.score=parseIntSafe(line.substring("score=".length()), 0);
                }
                else if (line.startsWith("tiles="))
                {
                    //tiles=r,c:<encodedTiles>
                    //tiles import directly inti the RoomData
                    //only use SaveManager for saving
                    applyRoomTiles(map, line.substring("tiles=".length()));
                }
                else if (line.startsWith("cleared="))
                {
                    //data.cleared = line.substring("cleared=".length());
                    //cleare/visited directly handle import and export in map classs
                    //not using SaveData to transfer data
                    map.importCleared(line.substring("cleared=".length()));
                }
                else if (line.startsWith("visited="))
                {
                    //data.visited = line.substring("visited=".length());
                    //cleare/visited directly handle import and export in map classs
                    //not using SaveData to transfer data
                    map.importVisited(line.substring("visited=".length()));
                }
                else if (data!=null&&line.startsWith("playerHealth="))
                {
                    data.playerHealth=parseIntSafe(line.substring("playerHealth=".length()), 0);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Load failed, file not found: " + e.getMessage());
            return null;
        }
        finally
        {
            if (sc != null) sc.close();
        }
    
        return data;
    }
    public static SaveData load()
    {
        if (!hasSave()) return null;
    
        SaveData data = new SaveData();
    
        //defaults
        data.roomR = 0;
        data.roomC = 0;
        data.lastRoomR = 0;
        data.lastRoomC = 0;
    
        Scanner sc = null;
    
        try
        {
            sc = new Scanner(new File(GameConfig.SAVE_FILE));
    
            while (sc.hasNextLine())
            {
                String line = sc.nextLine().trim();
    
                if (line.startsWith("roomR="))
                {
                    data.roomR = parseIntSafe(line.substring("roomR=".length()), 0);
                }
                else if (line.startsWith("roomC="))
                {
                    data.roomC = parseIntSafe(line.substring("roomC=".length()), 0);
                }
                else if (line.startsWith("lastRoomR="))
                {
                    data.lastRoomR = parseIntSafe(line.substring("lastRoomR=".length()), data.roomR);
                }
                else if (line.startsWith("lastRoomC="))
                {
                    data.lastRoomC = parseIntSafe(line.substring("lastRoomC=".length()), data.roomC);
                }
                else if (line.startsWith("roomsCleared="))
                {
                    data.roomsCleared=parseIntSafe(line.substring("roomsCleared=".length()), 0);
                }
                else if (line.startsWith("coins="))
                {
                    data.coins=parseIntSafe(line.substring("coins=".length()), 0);
                }
                else if (line.startsWith("score="))
                {
                    data.score=parseIntSafe(line.substring("score=".length()), 0);
                }
                else if (data!=null&&line.startsWith("playerHealth="))
                {
                    data.playerHealth=parseIntSafe(line.substring("playerHealth=".length()), 0);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Load failed, file not found: " + e.getMessage());
            return null;
        }
        finally
        {
            if (sc != null) sc.close();
        }
    
        return data;
    }
    /**
     * Applies one "tiles=" record onto the map.
     * roomTiles format: r,c:<encodedTiles>
     */
    private static void applyRoomTiles(GameMap map, String roomTiles)
    {
        if (map == null) 
        {
            return; 
        }
        if (roomTiles == null)
        {
            return;   
        }

        int col = roomTiles.indexOf(':');
        if (col < 0)
        {
            return;   
        }

        String roomPart = roomTiles.substring(0, col).trim();
        String tilesPart = roomTiles.substring(col + 1).trim();

        int comma = roomPart.indexOf(',');
        if (comma < 0)
        {
            return;   
        }

        int r = parseIntSafe(roomPart.substring(0, comma), -1);
        int c = parseIntSafe(roomPart.substring(comma + 1), -1);
        if (r < 0 || c < 0)
        {
            return;   
        }
        if (!map.hasRoom(r, c)) 
        {
            return;   
        }

        RoomData rd = map.getRoomData(r, c);
        if (rd == null)
        {
            return;   
        }

        rd.importTiles(tilesPart);
    }
    private static void saveRoomData(GameMap map, PrintWriter out)
    {

        if (map != null)
        {
            for (int r = 0; r < map.getRows(); r++)
            {
                for (int c = 0; c < map.getCols(); c++)
                {
                    if (!map.hasRoom(r, c)) continue;

                    RoomData rd = map.getRoomData(r, c);
                    if (rd == null) continue;

                    out.println("tiles=" + r + "," + c + ":" + rd.exportTiles());
                }
            }
        }
    }
    /**
     * Parses an integer safely, returning a default value if invalid.
     */
    private static int parseIntSafe(String s, int defaultValue)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
        catch (NullPointerException e)
        {
            return 0;
        }
    }
}