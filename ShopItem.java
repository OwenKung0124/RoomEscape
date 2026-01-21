import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * This is the shop that cotains all the artifacts
 * 
 * @Cartis Lee
 * @version jan 2026
 */
public class ShopItem extends Actor
{
    // the item times heal = healing attack = incrase damge
    public static final int HEAL = 1;
    public static final int ARTIFACT = 0;
    
    
    private String name;
    private int type;
    private int cost;
    
    //about the artifacts
    private int artifactName;
    private int dmgChange;
    private int hpChange;
    
    private int healAmount; // amount of health that is oging be be healed
    
    private boolean lastE = false;
    
    private GreenfootImage artiImg;
    
    //constructor for artifacts
    public ShopItem(int artifactName, String name, int cost, int dmgChange, int hpChange){
        this.type = ARTIFACT;
        this.cost = cost;
        this.name = name;
        this.artifactName = artifactName;
        this.dmgChange = dmgChange;
        this.hpChange = hpChange;
        
        artiImg = artifactImage(artifactName);
        
        updateImage(false);
    }
    
    //constructor for medkit/healing
    public ShopItem(String name, int cost, int healAmount){
        this.type = HEAL;
        this.name = name;
        this.cost = cost;
        this.healAmount = healAmount;
        
        artiImg = new GreenfootImage("artifacts/heal.png");
        
        updateImage(false);
    }
    
    public void act()
    {
        if(getWorld() == null){
            return;
        }
        
        boolean touching = isTouching(Player.class);
        updateImage(touching);
    }
    
    private void buyItem(Player p){
        if(p == null){
            return;
        }
        // Not enough coins
        if (p.getCoinCount() < cost)
        {
            feedback("Not enough coins!\n Need " + cost + " coins.", 80);
            SoundManager.playFailSound();
            return;
        }
    
        // Already owned (artifact)
        if (type == ARTIFACT && p.hasArtifact(artifactName))
        {
            feedback("You already own " + name + ".", 80);
            SoundManager.playFailSound();
            return;
        }
        
        //this is paying the coins
        p.setCoinCount(p.getCoinCount() - cost);
        
        if(type == HEAL){
            p.heal(healAmount);
            feedback("Purchased " + name + "\n (+" + healAmount + " HP)", 120);
            SoundManager.playHealthSound();
        }else{
            p.setArtifactOwned(artifactName);
            p.setAttackPower(p.getAttackPower() + dmgChange);
            //p.heal(p.getMaxHealth() + hpChange);
            p.heal(hpChange);
            if(p.getHealth() < 1){
                p.setHealth(1);
            }
            feedback("Purchased " + name + "\n"+
                 " DMG (" + signed(dmgChange) + ")\n"+
                 " MaxHP (" + signed(hpChange)+")", 140);
            SoundManager.playCoinSound();
        }
        
        //this will tell the gameworld taht this shop item was already bought so that it doesnt come back a second tiem
        GameWorld gw = (GameWorld) getWorld();
        if(gw != null){
            gw.markShopSold(type, artifactName);
        }
        
        // this will removea the items after it is purcahsed
        if(getWorld() != null) getWorld().removeObject(this);
        
        }
    
    private void updateImage(boolean showE){
        GreenfootImage img = new GreenfootImage(160, 70);
        
        if(artiImg != null){
            GreenfootImage scaled = new GreenfootImage(artiImg);
            scaled.scale(40, 40);
            img.drawImage(scaled, 35, 25);
        }
        
        img.setColor(Color.WHITE);
        img.drawString(shortName(name), 10, 18);
        
        img.setColor(Color.YELLOW);
        img.drawString(cost + "c", 10, 42);
        
        if(type == ARTIFACT){
            img.setColor(Color.WHITE);
            img.drawString("DMG " + signed(dmgChange), 90, 30);
            img.drawString("HP " + signed(hpChange), 90, 52);
        }else{
            img.setColor(Color.WHITE);
            img.drawString("+HP " + healAmount, 90, 42);
        }
        
        if(showE){
            //feedback("Press E to Aquire the Artifacts",80);
            img.setColor(Color.WHITE);
            img.drawString("[E]", 125, 18);
        }
        
        setImage(img);
    }
    
    private String signed(int n){
        if(n >=0){
            return "+" + n;
        }
        return "" + n;
    }
    
    private String shortName(String s){
        if(s == null){
            return "?";
        }
        if(s.length() < 24){
            return s;
        }
        return s.substring(0, 24);
    }
    
    private boolean isClosestToPlayer(Player p){ // this method makes it so that when there are two artifacts next to each other you only buy the one closest to you.
        if(p == null || getWorld() == null){
            return false;
        }
        
        List<ShopItem> items = getWorld().getObjects(ShopItem.class);
        
        int bestDist = Integer.MAX_VALUE;
        ShopItem best = null;
        
        for(ShopItem s: items){
            int distx = s.getX() - p.getX();
            int disty = s.getY() - p.getY();
            int dist2 = distx*distx + disty*disty;
            
            if(best == null || dist2 < bestDist){
                bestDist = dist2;
                best = s;
            }else if (dist2 == bestDist){
                if(s.getX() < best.getX() || (s.getX() == best.getX() && s.getY() < best.getY())){
                    best = s;
                }
            }
        }
        
        return best == this;
    }
    
    public void buyFromWorld(Player p){
        buyItem(p);
    }
    
    private GreenfootImage artifactImage(int num){
        String file = "artifacts/default.png";
        
        if(num == 0){
            file = "artifacts/glassCannon.png";
        }else if(num == 1){
            file = "artifacts/cliftionPhone.png";
        }else if(num == 2){
            file = "artifacts/toe.png";
        }else if(num == 3){
            file = "artifacts/finger.png";
        }else if(num == 4){
            file = "artifacts/magicBlade.png";
        }else if(num == 5){
            file = "artifacts/CLT.png";
        }else if(num == 6){
            file = "artifacts/YinYang.png";
        }else if(num == 7){
            file = "artifacts/OA.png";
        }else if(num == 8){
            file = "artifacts/adam.png";
        }else if(num == 9){
            file = "artifacts/AdvancedStaff.png";
        }
        
        return new GreenfootImage(file);
    }
    private void feedback(String msg, int frames)
    {
        World w = getWorld();
        if (w instanceof GameWorld)
        {
            ((GameWorld) w).showMessage(msg, frames);
        }
    }
}