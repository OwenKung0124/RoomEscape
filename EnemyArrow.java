import greenfoot.*;

/**
 * EnemyArrow is a Bullet that hits the Player instead of Enemy.
 * It reuses Bullet movement, wall collision, bounds removal, and lifetime.
 * 
 * @author Clifton Lin
 * @version Jan 2026
 */
public class EnemyArrow extends Bullet
{
    private int damagePower=1;

    /**
     * Create an arrow moving in a 4-direction unit vector.
     * dirX/dirY should be -1, 0, or 1 (same as Bullet).
     */
    public EnemyArrow(int dirX, int dirY,int damagePower){
        super(dirX, dirY, damagePower);
        
        speed = 2;

        // arrow sprite points LEFT by default
        if (dirX == 1){
            //RIGHT
            setImage(new GreenfootImage("enemy/skeleton/arrow.png"));
            getImage().mirrorHorizontally(); 
            getImage().scale(45, 20);
        }else if (dirX == -1){
            //LEFT
            setImage(new GreenfootImage("enemy/skeleton/arrow.png"));
            getImage().scale(45, 20);
        }else if (dirY == -1){
            //UP
            setImage(new GreenfootImage("enemy/skeleton/arrow_up.png"));
            getImage().scale(20, 45);  
        }else if (dirY == 1){
            //DOWN
            setImage(new GreenfootImage("enemy/skeleton/arrow_down.png"));
            getImage().scale(20, 45);  
        }  
  
    }

    /**
     * When the enemy's arrow hit the player: 
     */
    protected boolean handleHit(){
        Player p = (Player) getOneIntersectingObject(Player.class);
        if (p != null){
            p.takeDamage(getDamagePower());
            if (getWorld() != null){
                getWorld().removeObject(this);
            }
            return true;
        }
        return false;
    }
    
    /**
     * @return damage power of the enemy, based on difficulty level
     */
    public int getDamagePower()
    {
        if(getWorld() == null)
        {
            return damagePower;
        }
        int difficultyLevel = ((GameWorld) getWorld()).difficultyLevel();
         
        if (difficultyLevel == 0){
            return damagePower;
        }
        if (difficultyLevel == 1) {
            return damagePower * 2;
        }
        if (difficultyLevel == 2){
            return damagePower * 3;
        }
   
        return damagePower * 4;
    }
    
    
}