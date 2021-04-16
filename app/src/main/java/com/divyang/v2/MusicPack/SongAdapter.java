package com.divyang.v2.MusicPack;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import com.divyang.v2.R;

public class SongAdapter extends BaseAdapter
{
    private Bitmap placeHolderBitmap;

    // Reference Declaration.
    private ArrayList<SongDetails> songArrayList;
    private LayoutInflater songInf;

    private class ViewHolder
    {
        TextView songView;
        TextView artistView;
        TextView durationView;
        ImageView trackArt;
    }

    public SongAdapter(ArrayList<SongDetails> songArrayList, Context songInf)
    {
        this.songArrayList = songArrayList;
        this.songInf = LayoutInflater.from(songInf);
    }

    @Override
    public int getCount()
    {
        return songArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = new ViewHolder();
        //get song using position
        SongDetails currSong = songArrayList.get(position);
        if( convertView == null )
        {
            //map to song layout
            convertView = songInf.inflate(R.layout.songs, parent, false);
        }
        //get title and artist views
        holder.artistView = (TextView)convertView.findViewById(R.id.songArtist);
        holder.songView = (TextView)convertView.findViewById(R.id.songTitle);
        holder.durationView = (TextView)convertView.findViewById(R.id.songDuration);
        holder.trackArt = (ImageView)convertView.findViewById(R.id.trackArt);
        //set position as tag
        convertView.setTag(position);
        //set title and artist strings
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());
        //set Duration.
        long milliSecDuration = currSong.getDuration();
        int minDuration = ((int)milliSecDuration/1000)/60;
        int secDuration = ((int)milliSecDuration/1000)%60;
        if (secDuration<10)
            holder.durationView.setText(minDuration+":"+
                    String.format(Locale.getDefault(),"%02d", secDuration));
        else
            holder.durationView.setText(minDuration+":"+secDuration);

        //Set TrackArt.
        /*BitmapLoaderTask loaderTask = new BitmapLoaderTask(holder.trackArt);
        loaderTask.execute(currSong.getMediaArtPath());*/
        Log.i("Check","Loking For cache with key "+Long.toString(currSong.getId()));
        Bitmap musicArtBitmap = HelperMusicClass.getBitmapFromCache(Long.toString(currSong.getId()));
        if( musicArtBitmap != null)
        {
            Log.i("Check","Populating Cached Bitmap");
            holder.trackArt.setImageBitmap(musicArtBitmap);
        }
        else if(HelperMusicClass.checkBitmapLoaderTask(currSong.getMediaArtPath(),holder.trackArt))
        {
            BitmapLoaderTask loaderTask = new BitmapLoaderTask(holder.trackArt);
            HelperMusicClass.AsynArtLoader artLoader = new HelperMusicClass.AsynArtLoader(holder.trackArt.getResources(),
                    placeHolderBitmap, loaderTask, currSong.getMediaArtPath(), currSong.getId());
            holder.trackArt.setImageDrawable(artLoader);
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}