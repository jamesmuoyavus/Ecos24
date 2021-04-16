package com.divyang.v2.MusicPack;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.divyang.v2.R;

import java.util.ArrayList;
import java.util.Locale;

public class SongRecyclerViewAdapter extends RecyclerView.Adapter<SongRecyclerViewAdapter.SongHolder>
{
    private Bitmap placeHolderBitmap;

    private ItemClickListenerForRecyclerView clickListnerForRecyclerView;

    // Reference Declaration.
    private ArrayList<SongDetails> songArrayList;

    public SongRecyclerViewAdapter(ArrayList<SongDetails> songArrayList)
    {
        this.songArrayList = songArrayList;
    }

    @Override
    public SongRecyclerViewAdapter.SongHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.i("Check","SongTab SongRecyclerViewAdapter onCreateViewHolder.");

        View inflatedLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songs, parent, false);
        return new SongHolder(inflatedLayout);
    }

    @Override
    public void onBindViewHolder(SongRecyclerViewAdapter.SongHolder holder, int position)
    {
        Log.i("Check","SongTab SongRecyclerViewAdapter onBindViewHolder.");
        SongDetails currSong = songArrayList.get(position);
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
        Log.i("Check","Looking For cache with key "+Long.toString(currSong.getId()));
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
    }

    @Override
    public int getItemCount()
    {
        return songArrayList.size();
    }

    public void setClickListner(ItemClickListenerForRecyclerView itemClickListnerForRecyclerView)
    {
        Log.i("Check","Setting click Listener for RecyclerView");
        this.clickListnerForRecyclerView = itemClickListnerForRecyclerView;
    }

    class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView songView;
        TextView artistView;
        TextView durationView;
        ImageView trackArt;

        SongHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(this);

            songView = (TextView)itemView.findViewById(R.id.songTitle);
            artistView = (TextView)itemView.findViewById(R.id.songArtist);
            durationView = (TextView)itemView.findViewById(R.id.songDuration);
            trackArt = (ImageView)itemView.findViewById(R.id.trackArt);
        }

        @Override
        public void onClick(View v)
        {
            Log.i("Check","onClick for RecyclerView Item called");
            if( clickListnerForRecyclerView != null )
            {
                clickListnerForRecyclerView.onItemClickForRecyclerView(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListenerForRecyclerView
    {
        void onItemClickForRecyclerView(View view, int position);
    }
}
