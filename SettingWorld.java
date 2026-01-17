import greenfoot.*;

/**
 * SettingWorld lets the user pick a warrior then to start a new game or resume an old game
 * It also allows for turning music on/off
 */
public class SettingWorld extends World
{
    private int selectedType = GameConfig.WARRIOR_AXE;

    //warrior selecttion
    private WarriorSelectIcon axeIcon;
    private WarriorSelectIcon bulletIcon;
    private WarriorSelectIcon swordIcon;
    
    //new game or resume
    private StartButton newGameBotton;
    private StartButton resumeBotton;
    
    private GameData data;
    private TextLabel warriorTextLabel;

    public SettingWorld()
    {
        this(null);
    }
    /**
     * Constructor method for setting world
     * 
     * Warrior Seelection
     * Player State
     * Music/sftx on/off
     */
    public SettingWorld(GameData data)
    {
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        this.data = data;
        
        GreenfootImage bg = new GreenfootImage("setting/setting_bg.png");
        bg.scale(GameConfig.WORLD_W, GameConfig.WORLD_H);
        setBackground(bg);

        //showText("Click a warrior image to select", GameConfig.WORLD_W / 2, 100);
        showText("Click a warrior image to select",30,Color.WHITE,GameConfig.WORLD_W / 2, 100);

        //selections
        axeIcon = new WarriorSelectIcon("player/axe_warrior/walking/down", 4,GameConfig.WARRIOR_AXE,245,260);
        bulletIcon = new WarriorSelectIcon("player/bullet_warrior/walking/down",4, GameConfig.WARRIOR_BULLET,245,260);
        swordIcon = new WarriorSelectIcon("player/sword_warrior/walking/down",4, GameConfig.WARRIOR_SWORD,245,260);
        addObject(axeIcon, GameConfig.WORLD_W / 2 - 275, 375);
        addObject(bulletIcon, GameConfig.WORLD_W / 2+10 , 375);
        addObject(swordIcon, GameConfig.WORLD_W / 2 + 270, 375);

        //new game and resume buttons (images)
        newGameBotton = new StartButton("setting/start.png", StartButton.MODE_NEW_GAME);
        resumeBotton  = new StartButton("setting/resume.png", StartButton.MODE_RESUME);

        addObject(newGameBotton, GameConfig.WORLD_W / 2, 635);
        addObject(resumeBotton,  GameConfig.WORLD_W / 2 + 260, 635);
        
        //sound effects toggles
        addObject(new SoundToggleButton(SoundToggleButton.TYPE_MUSIC), GameConfig.WORLD_W/2 +400, 100);
        addObject(new SoundToggleButton(SoundToggleButton.TYPE_SFX),   GameConfig.WORLD_W/2 +520, 100);

        //disable resume if no save exists
        resumeBotton.setEnabled(SaveManager.hasSave());

        //default highlight
        updateHighlights();

        //show state on the top status bar
        showState();     
       
    }
    private void showState()
    {
        if(data==null && SaveManager.hasSave())
        {
            data=SaveManager.load();
        }
        if(data!=null)
        {
            showText(""+data.score,40, Color.WHITE,250, 100);
            //showText(""+data.roomsCleared,600, 70);
            showText(""+data.playerHealth,40, Color.WHITE,225, 200);
            
            //display attack power
            showText(""+data.axeAttackPower,40, Color.WHITE,370, 560);
            showText(""+data.bulletAttackPower,40, Color.WHITE,660, 560);
            showText(""+data.swordAttackPower,40, Color.WHITE,950, 560);
        }
        else
        {
            showText(""+0,40,Color.WHITE,250,100);
            //showText(""+data.roomsCleared,600, 70);
            showText(""+GameConfig.DEFAULT_MAX_HP,40, Color.WHITE,225, 200);
            
            //display attack power
            showText(""+GameConfig.WARRIOR_AXE_DEFAULT_ATTACK,40, Color.WHITE,370, 560);
            showText(""+GameConfig.WARRIOR_BULLET_DEFAULT_ATTACK,40, Color.WHITE,660, 560);
            showText(""+GameConfig.WARRIOR_SWORD_DEFAULT_ATTACK,40,Color.WHITE,950, 560);

        }
    }
    /**
     * called by Warrior Selection to highlight which warrior is selected
     */
    public void chooseWarrior(int type)
    {
        selectedType = type;
        updateHighlights();
    }
    /**
     * called but start button to start a brand new game.
     */
    public void startNewGame()
    {
        SoundManager.playStartSound();
        SaveManager.deleteSave();
        //pass null for SaveData
        Greenfoot.setWorld(new GameWorld(selectedType, false, null));
    }

    /**
     * called by resume button to resume a saved game
     */
    public void resumeGame()
    {
        SoundManager.playStartSound();
        if (!SaveManager.hasSave())
        {
            showText("No save found.", GameConfig.WORLD_W / 2, 470);
            return;
        }

        Greenfoot.setWorld(new GameWorld(selectedType, true,data));
    }

    /**
     * updates button visuals so the selected one looks highlighted.
     */
    private void updateHighlights()
    {
        axeIcon.setSelected(selectedType == GameConfig.WARRIOR_AXE);
        bulletIcon.setSelected(selectedType == GameConfig.WARRIOR_BULLET);
        swordIcon.setSelected(selectedType == GameConfig.WARRIOR_SWORD);

        String selectedStr="";
        if(selectedType==GameConfig.WARRIOR_AXE)
        {
            selectedStr="Axe Warrior";
        }
        if(selectedType==GameConfig.WARRIOR_BULLET)
        {
            selectedStr="Bullet Warrior";
        }
        if(selectedType==GameConfig.WARRIOR_SWORD)
        {
            selectedStr="Sword Warrior";
        }
        //showText(String msg, int size,Color color, int x, int y)
        showText(
            "Selected: " + selectedStr,
            //30,
            //Color.WHITE,
            GameConfig.WORLD_W / 2,
            135
        );
    }
    private  void showText(String msg, int size,Color color, int x, int y)
    {
        addObject(new TextLabel(msg,size,color,-1),x,y);
    }
    /**
     * Make sure game music starts after restarted
     */
    public void started()
    {
        SoundManager.playGameMusic();
    }
    /**
     * Make sure game music stops when paused
     */
    public void stopped()
    {
        SoundManager.stopAll();
    }
}