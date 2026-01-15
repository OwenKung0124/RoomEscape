import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StoryPlayer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StoryPlayer extends Player
{
    private static final int FRAME_DELAY = 5;
    
    private GreenfootImage[] walkRight;
    private GreenfootImage[] walkLeft;
    private int frame = 0;
    private int frameCounter = 0;
    private int fixedY;
    
    public StoryPlayer(){
        speed = 3;
        loadImages();
        setImage(walkRight[0]);
    }
    
    private void loadImages(){
        walkRight = new GreenfootImage[9];
        walkLeft = new GreenfootImage[9];
        
        int width = 150;
        int height = 150;
        
        for(int i = 0; i < 9; i++){
            walkRight[i] = new GreenfootImage("player/axe_warrior/walking/right" + (i + 1) + ".png");
            walkRight[i].scale(width, height);
            
            walkLeft[i] = new GreenfootImage(walkRight[i]);
            walkLeft[i].mirrorHorizontally();
        }
    }
    
    @Override
    public void addedToWorld(World w){
        fixedY = getY();
    }
    
    private void updateAnimation(boolean left, boolean right){
        if(!left && !right){
            frame = 0;
            frameCounter = 0;
            setImage(walkRight[0]);
            return;
        }
        
        frameCounter++;
        if(frameCounter >= FRAME_DELAY){
            frameCounter = 0;
            frame = (frame + 1) % walkRight.length;
        }
        
        if(right){
            setImage(walkRight[frame]);
        }else if(left){
            setImage(walkLeft[frame]);
        }
    }
    
    /**
     * Lock Y position every frame
     */
    @Override
    public void act(){
        int dx = 0;
        
        boolean left = Greenfoot.isKeyDown("a");
        boolean right = Greenfoot.isKeyDown("d");
        
        if(left){
            dx -= speed;
        }
        if(right){
            dx += speed;
        }
        
        updateAnimation(left, right);
        setLocation(getX() + dx, fixedY);
    }
    
    @Override protected boolean wantsToAttack(){
        return false;
    }
    
    @Override
    protected void onAttackHit(){
        
    }
}
