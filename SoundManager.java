import greenfoot.*;

public class SoundManager
{
    //user preferences
    private static boolean musicOn = false;
    private static boolean sfxOn   = false;

    //game music
    private static GreenfootSound gameMusic = new GreenfootSound("dark_ambient.mp3");

    //summoner boss
    private static GreenfootSound summonerBoss = new GreenfootSound("summoner_boss.mp3");
    private static GreenfootSound bossDisappear = new GreenfootSound("boss_disappear.mp3");
    private static GreenfootSound bossFight = new GreenfootSound("fight.mp3");
    private static GreenfootSound bossDescending = new GreenfootSound("descending.mp3");
    
     //door sound
    private static GreenfootSound door = new GreenfootSound("door_open.mp3");
    
    //zombie sfx
    private static int zombieSoundIndex;
    private static GreenfootSound zombieSounds[] = {
        new GreenfootSound("zombie_bite.mp3"),
        new GreenfootSound("zombie_bite.mp3"),
        new GreenfootSound("zombie_bite.mp3"),
        new GreenfootSound("zombie_bite.mp3")
    };
    
    //axe sfx
    private static int axeSoundIndex;
    private static GreenfootSound axeSounds[] = {
        new GreenfootSound("axe.mp3"),
        new GreenfootSound("axe.mp3"),
        new GreenfootSound("axe.mp3"),
        new GreenfootSound("axe.mp3")
    };

    //sword sfx
    private static int swordSoundIndex;
    private static GreenfootSound swordSounds[] = {
        new GreenfootSound("sword.mp3"),
        new GreenfootSound("sword.mp3"),
        new GreenfootSound("sword.mp3"),
        new GreenfootSound("sword.mp3")
    };
    
    //arrow sfx
    private static int arrowSoundIndex;
    private static GreenfootSound arrowSounds[] = {
        new GreenfootSound("arrow.mp3"),
        new GreenfootSound("arrow.mp3"),
        new GreenfootSound("arrow.mp3"),
        new GreenfootSound("arrow.mp3")
    };

    //bullet sfx
    private static int bulletSoundIndex;
    private static GreenfootSound bulletSounds[] = {
        new GreenfootSound("bullet.mp3"),
        new GreenfootSound("bullet.mp3"),
        new GreenfootSound("bullet.mp3"),
        new GreenfootSound("bullet.mp3")
    };
     //coin
    private static int coinSoundIndex;
    private static GreenfootSound coinSounds[] = {
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3")
    };

    private SoundManager() {}

    public static boolean isMusicOn()
    { 
        return musicOn; 
    }
    public static boolean isSfxOn()  
    { 
        return sfxOn;   
    }
    public static void setMusicOn(boolean on)
    {
        musicOn = on;
        if (!musicOn) 
        {
            stopGameMusic(); 
        }

    }

    public static void setSfxOn(boolean on)
    {
        sfxOn = on;
    }

    public static void toggleMusic()
    {
        setMusicOn(!musicOn);
        //setMusicOn(musicOn);
    }

    public static void toggleSfx()
    {
        setSfxOn(!sfxOn);
    }

    public static void playGameMusic()
    {
        if (!musicOn) return;

        gameMusic.setVolume(20);
        if (!gameMusic.isPlaying()) 
        {
            gameMusic.playLoop();
        }
    }
    public static void playSummonerBossSound()
    {
         if (!sfxOn) return;

        summonerBoss.setVolume(30);
        if (!summonerBoss.isPlaying()) 
        {
            summonerBoss.play();
        }
    }
    public static void stopSummonerBossSound()
    {
         if (!sfxOn) return;

        if (summonerBoss.isPlaying()) 
        {
            summonerBoss.stop();
        }
    }
    public static void playSummonerBossFightSound()
    {
         if (!sfxOn) return;

        if (!bossFight.isPlaying()) 
        {
            bossFight.play();
        }
    }
    public static void playSummonBossDisappear()
    {
        if (!sfxOn) return;

        bossDisappear.setVolume(40);
        if (!bossDisappear.isPlaying()) 
        {
            bossDisappear.play();
        }
    }
    public static void playDescendingSound()
    {
        if (!sfxOn) return;

        bossDescending.setVolume(40);
        if (!bossDescending.isPlaying()) 
        {
            bossDescending.play();
        }
    }
    public static void stopDescendingSound()
    {
        if (!sfxOn) return;

        bossDescending.setVolume(10);
        if (bossDescending.isPlaying()) 
        {
            bossDescending.stop();
        }
    }
    public static void stopAllBossSounds()
    {
        if(summonerBoss.isPlaying())
        {
             summonerBoss.stop();
             
        }
        if(bossDisappear.isPlaying())
        {
             bossDisappear.stop();
             
        }
        if(bossFight.isPlaying())
        {
             bossFight.stop();
             
        }
        if(bossDescending.isPlaying())
        {
             bossDescending.stop();
             
        }
    }
        public static void playDoorOpenSound()
    {
         if (!sfxOn) return;

        door.setVolume(30);
        if (!door.isPlaying()) 
        {
            door.play();
        }
    }
    public static void stopGameMusic()
    {
        if (gameMusic != null && gameMusic.isPlaying()) 
        {
            gameMusic.stop();
        }
    }
    public static void playZombieSound()
    {
        if (!sfxOn) return;
        
        zombieSoundIndex=arraySoundPlay(zombieSounds,zombieSoundIndex,30);
    }
    public static void playAxeSound()
    {
        if (!sfxOn) return;
        
        axeSoundIndex=arraySoundPlay(axeSounds,axeSoundIndex,30);
    }
    public static void playSwordSound()
    {
        if (!sfxOn) return;
        
        swordSoundIndex=arraySoundPlay(swordSounds,swordSoundIndex,30);
    }
    public static void playArrowSound()
    {
        if (!sfxOn) return;
        
        arrowSoundIndex=arraySoundPlay(arrowSounds,arrowSoundIndex,30);
    }
    public static void playBulletSound()
    {
        if (!sfxOn) return;
        
        bulletSoundIndex=arraySoundPlay(bulletSounds,bulletSoundIndex,30);
    }
    public static void playCoinSound()
    {
        if (!sfxOn) return;
        
        coinSoundIndex=arraySoundPlay(coinSounds,coinSoundIndex,30);
    }
    
    private static int arraySoundPlay(GreenfootSound sounds[], int index, int volumn)
    {
        if (!sfxOn) return 0;
        
        try
        {
            sounds[index].setVolume(30);
            sounds[index].play();
        }
        catch(ArrayIndexOutOfBoundsException ao)
        {
            //use the first one
            index=9;
            sounds[index].setVolume(volumn);
            sounds[index].play();
        }

        index++;
        if (index == sounds.length) index = 0;
        
        return index;
    }
}