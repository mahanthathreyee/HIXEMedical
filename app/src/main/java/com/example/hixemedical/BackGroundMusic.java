package com.example.hixemedical;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class BackGroundMusic {
    private static MediaPlayer player;
    private static boolean keepMusicOn;
    private static boolean currentState = false;

    public static boolean musicServiceToggle(Context context){
        currentState = !currentState;
        if(currentState)
            iAmIn(context);
        else
            iAmLeaving();
        return currentState;
    }

    public static boolean getMusicStatus(){
        return currentState;
    }

    public static void iAmIn(Context context){
        if (player == null){
            player = MediaPlayer.create(context, R.raw.music);
            player.setLooping(true);

            try{
                player.prepare();
            }
            catch (IllegalStateException e){}
            catch (IOException e){}
        }

        if(!player.isPlaying()){
            player.start();
        }

        keepMusicOn= false;
    }

    public static void keepMusicOn(){
        keepMusicOn= true;
    }

    public static void iAmLeaving(){
        currentState = false;
        if(!keepMusicOn){
            try {
                player.pause();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}