import greenfoot.*;
/**
 * Graphic buttons for Music and Sound
 * 
 * @author:
 */
public class SoundToggleButton extends Actor
{
    public static final int TYPE_MUSIC = 0;
    public static final int TYPE_SFX   = 1;

    private int type;

    //filenames
    private static final String MUSIC  = "setting/music_on.png";
    private static final String SFX    = "setting/sfx_on.png";

    private static final int width = 150;
    private static final int height = 150;

    //cached images so we don't reload every click
    private GreenfootImage musicImg;
    private GreenfootImage sfxImg;

    public SoundToggleButton(int type)
    {
        this.type = type;

        //load once
        musicImg  = new GreenfootImage(MUSIC);
        sfxImg    = new GreenfootImage(SFX);

        musicImg.scale(width, height);
        sfxImg.scale(width, height);

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
        if (type == TYPE_MUSIC)
        {
            setImage(musicImg);
            if(SoundManager.isMusicOn())
                getImage().setTransparency(255);
            else
                getImage().setTransparency(120);
        }
        else
        {
            setImage(sfxImg);
            if(SoundManager.isSfxOn())
                getImage().setTransparency(255);
            else
                getImage().setTransparency(120);
        }
    }
}