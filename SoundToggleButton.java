import greenfoot.*;

public class SoundToggleButton extends Actor
{
    public static final int TYPE_MUSIC = 0;
    public static final int TYPE_SFX   = 1;

    private int type;

    //filenames
    private static final String MUSIC_ON  = "setting/music_on.png";
    private static final String MUSIC_OFF = "setting/music_off.png";
    private static final String SFX_ON    = "setting/sfx_on.png";
    private static final String SFX_OFF   = "setting/sfx_off.png";

    private static final int width = 90;
    private static final int height = 90;

    //cached images so we don't reload every click
    private GreenfootImage musicOnImg;
    private GreenfootImage musicOffImg;
    private GreenfootImage sfxOnImg;
    private GreenfootImage sfxOffImg;

    public SoundToggleButton(int type)
    {
        this.type = type;

        // load once
        musicOnImg  = new GreenfootImage(MUSIC_ON);
        musicOffImg = new GreenfootImage(MUSIC_OFF);
        sfxOnImg    = new GreenfootImage(SFX_ON);
        sfxOffImg   = new GreenfootImage(SFX_OFF);

        musicOnImg.scale(width, height);
        musicOffImg.scale(width, height);
        sfxOnImg.scale(width, height);
        sfxOffImg.scale(width, height);

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
            setImage(SoundManager.isMusicOn() ? musicOnImg : musicOffImg);
        }
        else
        {
            setImage(SoundManager.isSfxOn() ? sfxOnImg : sfxOffImg);
        }
    }
}