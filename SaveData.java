/**
 * SaveData stores information that will be saved.
 *
 */
public class SaveData
{
    public int roomR;
    public int roomC;

    public int lastRoomR;
    public int lastRoomC;
    
    //track records
    public int roomsCleared;
    public int playerHealth;
    
    //collectibles
    public int coins=0;
    public int score=0;

    //currently handed by GameMap class
    //public String visited;
    //public String cleared;
    
    

    public SaveData()
    {
    }
}