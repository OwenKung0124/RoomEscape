import greenfoot.*;

public class SoundToggleButton extends Actor
{
    public static final int TYPE_MUSIC = 0;
    public static final int TYPE_SFX   = 1;

    private int type;

    public SoundToggleButton(int type)
    {
        this.type = type;
        updateImage();
    }

    public void act()
    {
        if (Greenfoot.mouseClicked(this))
        {
            if (type == TYPE_MUSIC)
            {
                SoundManager.toggleMusic();
            }
            else 
            {
                SoundManager.toggleSfx(); 
            }

            updateImage();
        }
    }

    private void updateImage()
    {
        GreenfootImage img = new GreenfootImage(100, 45);
        img.setColor(Color.BLACK);
        img.fill();

        img.setColor(Color.WHITE);
        img.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);

        String label;
        if (type == TYPE_MUSIC) 
        {
            label = "Music: " + (SoundManager.isMusicOn() ? "ON" : "OFF");
        } else {
            label = "SFX: " + (SoundManager.isSfxOn() ? "ON" : "OFF");
        }

        img.drawString(label, 30, 40);
        setImage(img);
    }
}