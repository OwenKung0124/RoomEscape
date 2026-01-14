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
    
    //default attack powers
    public int axeAttackPower=GameConfig.WARRIOR_AXE_DEFAULT_ATTACK;
    public int swordAttackPower=GameConfig.WARRIOR_SWORD_DEFAULT_ATTACK;
    public int bulletAttackPower=GameConfig.WARRIOR_BULLET_DEFAULT_ATTACK;
    
    
    //currently handed by GameMap class
    //public String visited;
    //public String cleared;

    public SaveData()
    {
    }
}