import greenfoot.*;
/**
 * Manage All the Sounds in the projects
 * 
 * @authro:
 */
public class SoundManager
{
    //user preferences
    private static boolean musicOn = true;
    private static boolean sfxOn   = true;

    //game music
    private static GreenfootSound gameMusic = new GreenfootSound("dark_ambient_2.mp3");
    //private static GreenfootSound gameMusic = new GreenfootSound("anna_theme.mp3");
    //private static GreenfootSound gameMusic = new GreenfootSound("game_music.mp3");
    private static GreenfootSound defeat = new GreenfootSound("defeat.mp3");
    private static GreenfootSound help = new GreenfootSound("fire.mp3");
    private static GreenfootSound victory = new GreenfootSound("victory.mp3");
    
    //summoner boss
    private static GreenfootSound summonerBoss = new GreenfootSound("summoner_boss.mp3");
    private static GreenfootSound bossDisappear = new GreenfootSound("boss_disappear.mp3");
    private static GreenfootSound bossFight = new GreenfootSound("fight.mp3");
    private static GreenfootSound bossDescending = new GreenfootSound("descending.mp3");
    
     //door sound
    private static GreenfootSound door = new GreenfootSound("door_open.mp3");
    
     //typig sound
    private static GreenfootSound typing = new GreenfootSound("typing.mp3");
    
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
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3"),
        new GreenfootSound("coin.mp3")
    };
    
    //game start/resum
    private static int startSoundIndex;
    private static GreenfootSound startSounds[] = {
        new GreenfootSound("game_start.mp3"),
        new GreenfootSound("game_start.mp3")
    };
    
    //game message
    private static int messageIndex;
    private static GreenfootSound messageSounds[] = {
        new GreenfootSound("prompt_message.mp3"),
        new GreenfootSound("prompt_message.mp3")
    };
    
     //health upgrade
    private static int healthIndex;
    private static GreenfootSound healthSounds[] = {
        new GreenfootSound("health_upgrade.mp3"),
        new GreenfootSound("health_upgrade.mp3")
    };
    
    private SoundManager() {}

    /**
     * @return: is music is on/off
     */
    public static boolean isMusicOn()
    { 
        return musicOn; 
    }
    /**
     * Wreturn: is sound effect on/off
     */
    public static boolean isSfxOn()  
    { 
        return sfxOn;   
    }
    /**
     * setter method to set if music on/off
     */
    public static void setMusicOn(boolean on)
    {
        musicOn = on;
        if (!musicOn) 
        {
            stopGameMusic(); 
        }

    }
    /**
     * setter method to set if sound effect i on/off
     */
    public static void setSfxOn(boolean on)
    {
        sfxOn = on;
    }
    /**
     * turn music on/off
     */
    public static void toggleMusic()
    {
        musicOn = !musicOn;
    
        if (musicOn)
        {
            playGameMusic();   // start RIGHT NOW
        }
        else
        {
            stopGameMusic();   // stop RIGHT NOW
        }
    }
    /**
     * turn sound effect on/off
     */
    public static void toggleSfx()
    {
        setSfxOn(!sfxOn);
    }
    /**
     * play game backgrond music
     */
    public static void playGameMusic()
    {
        if (!musicOn) return;

        gameMusic.setVolume(30);
        if (!gameMusic.isPlaying()) 
        {
            gameMusic.playLoop();
        }
    }
    /**
     * sound that s played when boss is about to spawn minions
     */
    public static void playSummonerBossSound()
    {
         if (!sfxOn) return;

        summonerBoss.setVolume(35);
        if (!summonerBoss.isPlaying()) 
        {
            summonerBoss.play();
        }
    }
    /**
     * stop all SummonerBoss related sounds
     */
    public static void stopSummonerBossSound()
    {
         if (!sfxOn) return;

        if (summonerBoss.isPlaying()) 
        {
            summonerBoss.stop();
        }
    }
    /**
     * play summoner boss ready to fight sound
     */
    public static void playSummonerBossFightSound()
    {
         if (!sfxOn) return;

        if (!bossFight.isPlaying()) 
        {
            bossFight.play();
        }
    }
    /**
     * play summoner boss sound when defeated
     */
    public static void playSummonBossDisappear()
    {
        if (!sfxOn) return;

        bossDisappear.setVolume(40);
        if (!bossDisappear.isPlaying()) 
        {
            bossDisappear.play();
        }
    }
    /**
     * play sound when summoner boss descending to centre of the room
     */
    public static void playDescendingSound()
    {
        if (!sfxOn) return;

        bossDescending.setVolume(40);
        if (!bossDescending.isPlaying()) 
        {
            bossDescending.play();
        }
    }
    /**
     * stop boss descending sound
     */
    public static void stopDescendingSound()
    {
        if (!sfxOn) return;

        bossDescending.setVolume(10);
        if (bossDescending.isPlaying()) 
        {
            bossDescending.stop();
        }
    }
     /**
     * stop all boss related sound effects
     */
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
     /**
     *  play open door sound when a room is cleared, door opens
     */
    public static void playDoorOpenSound()
    {
         if (!sfxOn) return;

        door.setVolume(30);
        if (!door.isPlaying()) 
        {
            door.play();
        }
    }
    /**
     * stop game background music
     */
    public static void stopGameMusic()
    {
        if (gameMusic != null && gameMusic.isPlaying()) 
        {
            gameMusic.stop();
        }
    }
     /**
     * play zoombie sound when zombie is touching the player
     */
    public static void playZombieSound()
    {
        if (!sfxOn) return;
        
        zombieSoundIndex=arraySoundPlay(zombieSounds,zombieSoundIndex,30);
    }
    /**
     * play axeSound when axe is swung
     *
     */
    public static void playAxeSound()
    {
        if (!sfxOn) return;
        
        axeSoundIndex=arraySoundPlay(axeSounds,axeSoundIndex,30);
    }
     /**
     * play swordSound when sword is swung
     *
     */
    public static void playSwordSound()
    {
        if (!sfxOn) return;
        
        swordSoundIndex=arraySoundPlay(swordSounds,swordSoundIndex,30);
    }
    /**
     * play arrowSound when aroow is shoot
     *
     */
    public static void playArrowSound()
    {
        if (!sfxOn) return;
        
        arrowSoundIndex=arraySoundPlay(arrowSounds,arrowSoundIndex,30);
    }
     /**
     * play bulletSound when bullet is shoot
     *
     */
    public static void playBulletSound()
    {
        if (!sfxOn) return;
        
        bulletSoundIndex=arraySoundPlay(bulletSounds,bulletSoundIndex,30);
    }
    /**
     * play coin sound when coin is collected when bullet is shoot
     *
     */
    public static void playCoinSound()
    {
        if (!sfxOn) return;
        
        coinSoundIndex=arraySoundPlay(coinSounds,coinSoundIndex,30);
    }
     /**
     * play typing sound when typwritter is typing
     */
    public static void playTypingSound()
    {
        typing.setVolume(30);
        if (!typing.isPlaying()) 
        {
            typing.play();
        }
    }
    /**
     * stop typig sound when type writter stops typing
     */
    public static void stopTypingSound()
    {
        if (typing.isPlaying()) 
        {
            typing.stop();
        }
    }
    /**
     * play defeat sound in defeat page
     */
    public static void playDefeatSound()
    {
        if (!musicOn) return;
        
        defeat.setVolume(30);
        if (!defeat.isPlaying()) 
        {
            defeat.play();
        }
    }
    /**
     * stop defea sound when not in defeat page
     */
    public static void stopDefeatSound()
    {
        if (!musicOn) return;
        
        if (defeat.isPlaying()) 
        {
            defeat.stop();
        }
    }
    /**
     * play help sound in help page
     */
    public static void playHelpSound()
    {
        if (!musicOn) return;
        
        help.setVolume(40);
        if (!help.isPlaying()) 
        {
            help.playLoop();
        }
    }
    /**
     * stop help sound when not in help page
     */
    public static void stopHelpSound()
    {
        if (!musicOn) return;
        
        if (help.isPlaying()) 
        {
            help.stop();
        }
    }
    /**
     * play victory sound in victory page
     */
    public static void playVictorySound()
    {
        if (!musicOn) return;
        
        victory.setVolume(40);
        if (!victory.isPlaying()) 
        {
            victory.playLoop();
        }
    }
    /**
     * stop victory sound when not in victory page
     */
    public static void stopVictorySound()
    {
        if (!musicOn) return;
        
        if (victory.isPlaying()) 
        {
            victory.stop();
        }
    }
    /**
     * play game start sound when new game or resume game is pressed
     */
    public static void playStartSound()
    {
        if (!sfxOn) return;
        
        startSoundIndex=arraySoundPlay(startSounds,startSoundIndex,50);
    }
     /**
     * play game start sound when new game or resume game is pressed
     */
    public static void playMessageSound()
    {
        if (!sfxOn) return;
        
        messageIndex=arraySoundPlay(messageSounds,messageIndex,30);
    }
     /**
     * play game start sound when new game or resume game is pressed
     */
    public static void playHealthSound()
    {
        if (!sfxOn) return;
        
        healthIndex=arraySoundPlay(healthSounds,healthIndex,30);
    }
    /*
     * Helper method to play sounds in array
     */
    private static int arraySoundPlay(GreenfootSound sounds[], int index, int volumn)
    {
        if (!sfxOn) return 0;
        if (sounds == null || sounds.length == 0) return 0;
        if (index < 0 || index >= sounds.length) index = 0;
        
        try
        {
            sounds[index].setVolume(volumn);
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
    /*
     * Helper method for stop greenfood sound
     */
    private static void stop(GreenfootSound sound)
    {
        if(sound==null)
        {
            return;
        }
        if(sound.isPlaying())
        {
            sound.stop();
        }
    }
    /**
     *  Stop all the sounds that may last more than 2s
     */
    public static void stopAll()
    {
           stop(gameMusic);
           stop(summonerBoss);
           stop(bossFight);
           stop(bossDisappear);
           stop(bossFight);
           stop(bossDescending);
           stop(door);
    }
}