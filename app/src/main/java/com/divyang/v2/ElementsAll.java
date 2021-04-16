package com.divyang.v2;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import com.divyang.v2.MusicPack.SongDetails;
import java.util.ArrayList;

public class ElementsAll
{
    //Activity Flag
    static boolean activity = false;
    //Shared SongArrayList.
    public static ArrayList<SongDetails> songDetailArrayList = new ArrayList<>();

    //Variable Declaration.
    static final int TIME_DELAY = 2000;
    static long back_pressed;

    static MediaBrowser mediaBrowser;
    static Intent serviceIntent;

    // Music State Variables.
    static final int STATE_PAUSED = 0;
    static final int STATE_PLAYING = 1;
    static int currentState;
}
