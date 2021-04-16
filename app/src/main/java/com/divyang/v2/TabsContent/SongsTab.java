package com.divyang.v2.TabsContent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divyang.v2.ElementsAll;
import com.divyang.v2.MusicPack.HelperMusicClass;
import com.divyang.v2.MusicPack.SongRecyclerViewAdapter;
import com.divyang.v2.R;

public class SongsTab extends Fragment implements SongRecyclerViewAdapter.ItemClickListenerForRecyclerView
{
    //RecyclerView.
    private static SongRecyclerViewAdapter songRecyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Log.i("Check","onCreate SongTab");

        super.onCreate(savedInstanceState);

        // Initialise Cache for Bitmap.
        HelperMusicClass.initialiseCache();

        // Initialise SongViewAdapter
        songRecyclerViewAdapter = new SongRecyclerViewAdapter(ElementsAll.songDetailArrayList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Log.i("Check","onCreateView SongTab");

        final View rootView = inflater.inflate(R.layout.tab_songs_mainlayout, container, false);

        //Setting the Click Listener for the RecyclerView items.
        songRecyclerViewAdapter.setClickListner(this);
        // Initialise RecyclerView.
        RecyclerView songlistrecyclerview;
        songlistrecyclerview = (RecyclerView) rootView.findViewById(R.id.song_recyclerView);
        // Set corresponding layoutManager, Using weak Reference as Strong Reference causes error.
        songlistrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        songlistrecyclerview.setAdapter(songRecyclerViewAdapter);

        TextView sizeSongListView = (TextView) rootView.findViewById(R.id.size_song_listView);
        String songsInfo = ElementsAll.songDetailArrayList.size()+" Total Songs";
        sizeSongListView.setText(songsInfo);

        return rootView;
    }

    @Override
    public void onItemClickForRecyclerView(View view, int position)
    {
        final String mediaId = Integer.valueOf(position).toString();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                getActivity().getMediaController().getTransportControls().playFromMediaId(mediaId, null);
            }
        }).start();
        /*initSlidingUpPanel(view);
        initPanelListner();
        setSlidingPanelData(mediaId);*/
    }
}
