import greenfoot.*;
/**
 * PromptManager responsible for prompting a message in the sidepanel
 * Asking if player would like to upgrade certain feature
 * Usually u to upgrade d to decline
 * 
 * 
 * @author:
 * @version:
 * 
 */
public class PromptManager extends Actor
{
    //prompt types
    public static final int NONE=0;
    public static final int ATTACK_UPGRADE=1;
    public static final int HP_UPGRADE=2;
    public static final int STONE_POWER=3;

    private int type=NONE;
    private boolean active=false;

    private TextLabel label;
    private Player player;

    //for attack upgrade prompt
    private AttackUpgrade attackUpgrade;
    private AttackUpgrade attackUpgradeDeclined;
    private AttackUpgrade attackUpgradeAccepted;
    private int upgradeAmount;
    private int upgradeCost;
    
    //for health upgrade prompt
    private HealthUpgrade healthUpgrade;
    private HealthUpgrade healthUpgradeDeclined;
    private HealthUpgrade healthUpgradeAccepted;
    
    //for stoneskill upgrade prompt
    private StoneSkill stoneSkillUpgrade;
    private StoneSkill stoneSkillUpgradeDeclined;
    private StoneSkill stoneSkillUpgradeAccepted;

    //prevent multiple keey press
    private boolean uWasDown=false;
    private boolean dWasDown=false;
    
    //for game Messags
    //private TextLabel gameMessage;
    //private int messageTimer = 0;

    public PromptManager()
    {
        //set transparent images for prompt manager
        setImage(new GreenfootImage(1, 1));
        getImage().setTransparency(0);
    }
    /**
     * check to see if prompt is active, if not skip
     * 
     * listens to u, or d key press
     */
    public void act()
    {     
        
        if (!active) 
        {
            return;
        }

        boolean uDown=Greenfoot.isKeyDown("u");
        boolean dDown=Greenfoot.isKeyDown("d");

        //make sure it was not pressed multiple times
        boolean uPressed=uDown && !uWasDown;
        boolean dPressed=dDown && !dWasDown;

        uWasDown=uDown;
        dWasDown=dDown;

        if (uPressed)
        {
            accept();
            close();
        }
        else if (dPressed)
        {
            decline();
            close();
        }
    }
    /**
     * tells caller to see if the prompt is active
     * usually called by upgrade class e.g. attackupgrade/healtupgraade
     * 
     * Wreturn: tells if PromptManager is active and waiting for player input
     */
    public boolean isActive()
    {
        return active;
    }
    /**
     * Called by respective class to show upgrade
     * 
     * @param p             :player whose attack power to be upgraded
     * @param upgrade:      :the upgrade class that calls this method
     */
    public void showUpgrade(Player p, AttackUpgrade upgrade)
    {
        if (active)
        {
             return;   
        }
        if (p==null || upgrade==null) return;
        
        player=p;
        attackUpgrade=upgrade;

        //how much it takes to upgrade
        upgradeAmount=1;
        upgradeCost=p.getAttackPower() * 5;

        type=ATTACK_UPGRADE;
        active=true;
        if(p.getScore()<upgradeCost)
        {
            SoundManager.playMessageSound();
            showPrompt("Upgrade Attack Power.\n  Not Enogh Score.\nYou Need "+upgradeCost+ " Scores to Upgrade",30);
            type=NONE;
            active=false;
        }
        else if(p.getScore()<GameConfig.MINIMUM_HP_TO_UPGRADE)
        {
            SoundManager.playMessageSound();
            showPrompt("Upgrade Attack Power.\n Not Enough Score.\n You Need "+GameConfig.MINIMUM_HP_TO_UPGRADE+" Score to upgrade",30); 
            type=NONE;
            active=false;
        }
        else
        {
            SoundManager.playMessageSound();
             showPrompt("Upgrade Attack Power? (+" + upgradeAmount + ")\n"
            + "Cost: " + upgradeCost + " score\n"
            + "Press U=Upgrade, D=Decline",9999);

        }
                    
        //recognize key press, avoid multiple trigger
        uWasDown=Greenfoot.isKeyDown("u");
        dWasDown=Greenfoot.isKeyDown("d");
    }
     /**
     * Called by respective class to show upgrade
     * 
     * @param p             :player whose health power to be upgraded
     * @param upgrade:      :the upgrade class that calls this method
     */
    public void showUpgrade(Player p, HealthUpgrade upgrade)
    {
        if (active)
        {
             return;   
        }
        if (p==null || upgrade==null) return;
        
        player=p;
        healthUpgrade=upgrade;

        //upgrade helath by 10, cost 1 coin
        upgradeAmount=10;
        upgradeCost=1;

        type=HP_UPGRADE;
        active=true;
        if(p.getCoinCount()<=0)
        {
            SoundManager.playMessageSound();
            showPrompt("Health Upgrade.\n Need Coin to Upgrade",30);
            type=NONE;
            active=false;
        }
        else
        {
            SoundManager.playMessageSound();
            showPrompt("Upgrade Health HP? (+" + upgradeAmount + ")\n"
            + "Cost: " + upgradeCost + " coin\n"
            + "Press U=Upgrade, D=Decline",9999);

        }
                    
        //recognize key press, avoid multiple trigger
        uWasDown=Greenfoot.isKeyDown("u");
        dWasDown=Greenfoot.isKeyDown("d");
    }
     /**
     * Called by respective class to show upgrade
     * 
     * @param p             :player whose wants to aquire stone skill
     * @param upgrade:      :the upgrade class that calls this method
     */
    public void showUpgrade(Player p, StoneSkill upgrade)
    {
        if (active)
        {
             return;   
        }
        if (p==null || upgrade==null) return;
        
        player=p;
        stoneSkillUpgrade=upgrade;

        //aquire 1 stoneskill
        //cost 10 coins
        upgradeAmount=1;
        upgradeCost=10;

        type=STONE_POWER;
        active=true;
        if(p.getCoinCount()<=upgradeCost)
        {
            SoundManager.playMessageSound();
            showPrompt("Aquire Stone Skill.\n Need ("+upgradeCost+") Coin to Upgrade",30);
            type=NONE;
            active=false;
        }
        else
        {
            SoundManager.playMessageSound();
             showPrompt("Aquire Stone SKill? (+" + upgradeAmount + ")\n"
            + "Cost: " + upgradeCost + " coin\n"
            + "Press U=Upgrade, D=Decline",9999);

        }
                    
        //recognize key press, avoid multiple trigger
        uWasDown=Greenfoot.isKeyDown("u");
        dWasDown=Greenfoot.isKeyDown("d");
    }
    /**
     * when player accept the upgrade
     */
    private void accept()
    {
        if (type==ATTACK_UPGRADE)
        {
            if (player != null &&attackUpgrade!= null&& attackUpgrade.getWorld() != null)
            {
                boolean ok=player.upgradeAttackPower(upgradeAmount, upgradeCost);
                
                attackUpgrade.markAccepted(player);
                GameWorld.numOfAttackUpgrade++;  //keep track of upgrade
                if (ok &&  attackUpgrade.getWorld() != null)
                {
                    //attackUpgrade.getWorld().removeObject( attackUpgrade);
                }
            }
        }
        if (type==HP_UPGRADE)
        {
            if (player != null &&healthUpgrade!= null&& healthUpgrade.getWorld() != null)
            {
                boolean ok=player.upgradeHealthPower(upgradeAmount, upgradeCost);
                healthUpgrade.markAccepted(player);
                GameWorld.numOfHealthUpgrade++;
                if (ok &&  healthUpgrade.getWorld() != null)
                {
                    //don't remove, it's a stand
                    //healthUpgrade.getWorld().removeObject( healthUpgrade);
                }
            }
        }
        if (type==STONE_POWER)
        {
            if (player != null &&stoneSkillUpgrade!= null&& stoneSkillUpgrade.getWorld() != null)
            {
                boolean ok=player.aquireStoneSkill(upgradeAmount, upgradeCost);
                stoneSkillUpgrade.markAccepted(player);
                GameWorld.numOfStoneUpgrade++;
                if (ok &&  stoneSkillUpgrade.getWorld() != null)
                {
                    //stoneSkillUpgrade.getWorld().removeObject( healthUpgrade);
                }
            }
        }
    }
    /**
     * when player decline the upgrade
     */
    private void decline()
    {   
        if (attackUpgrade!= null)
        {
            attackUpgrade.markDeclined(player);
        }  
        if (healthUpgrade!= null)
        {
            healthUpgrade.markDeclined(player);
        }  
        if (stoneSkillUpgrade!= null)
        {
            stoneSkillUpgrade.markDeclined(player);
        }  
    }
    /**
     * For showing prompt messages in side panel
     */
    private void showPrompt(String msg,int frames)
    {
        removeLabel();
        label=new TextLabel(msg, 20, Color.YELLOW, frames);

        World w=getWorld();
        if (w != null)
        {
            w.addObject(label, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY()-50);
        }
    } 
    /**
     * Helper method for other classes to display game related messages
     * 
     * @param msg: message to display on the side panel
     * @param frames: for how many frames to display
     
    public void showMessage(String msg, int frames)
    {

        if (gameMessage==null)
        {
            //gameMessage = new TextLabel("", 26, Color.YELLOW,messageTimer); 
            gameMessage = new TextLabel("", 22, Color.YELLOW,-1); 
            getWorld().addObject(gameMessage, GameConfig.sidePanelCentreX(), GameConfig.sidePanelCentreY()-50);
        }
    
        gameMessage.setText(msg);     
        gameMessage.setVisible(true);
        messageTimer = frames;
    }
    */
    /**
     * close the prompt on the side panel
     */
    private void close()
    {
        removeLabel();
        active=false;
        type=NONE;

        player=null;
        attackUpgrade=null;
        healthUpgrade=null;
        stoneSkillUpgrade=null;
        //attackUpgradeDeclined=null;
    }
    private void removeLabel()
    {
        if (label != null && label.getWorld() != null)
        {
            label.getWorld().removeObject(label);
        }
        label=null;
    }
}