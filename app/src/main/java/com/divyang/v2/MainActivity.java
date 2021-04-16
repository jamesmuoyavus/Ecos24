package com.divyang.v2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//User Added imports.
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.divyang.v2.SettingsPack.Settings;
import com.divyang.v2.TabsContent.*;
import com.divyang.v2.MusicPack.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////PERMISSION FUNCTIONS/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private final int REQUEST_PERMISSION_READ_EXT_STORAGE = 1;

    private void showPhoneStatePermission(final String permission)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            Log.i("Check","Granting permissions for Pre Marshmallow");
            onCreatePermissionGranted();
        }
        else
        {
            final String permissionDetails = "Dumbo! if you won't allow App to access Storage then How are you planning to listen to your Stored Musics. AssHole";
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
                {
                    showExplanation("Permission Needed", permissionDetails,
                            permission, REQUEST_PERMISSION_READ_EXT_STORAGE);
                }
                else
                {
                    requestPermission(permission, REQUEST_PERMISSION_READ_EXT_STORAGE);
                }
            }
            else
            {
                onCreatePermissionGranted();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_READ_EXT_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    onCreatePermissionGranted();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Permission is Mandatory!", Toast.LENGTH_SHORT).show();
                    //Send's User to App Settings page to grant Permissions.
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_READ_EXT_STORAGE);
                    finish();
                }
        }
    }

    private void showExplanation(String title,String message,final String permission,final int permissionRequestCode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode)
    {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////PERMISSION FUNCTIONS END///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////


    // Sliding Up Pannel.
    /*private SlidingUpPanelLayout slideLayout;
    private ImageView slideMusicArt;
    private SeekBar slideMusicSeekbar;*/

    //Variable Declaration.
    private DrawerLayout sideMenu;
    TextView userNameSideFloatingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("Check","onCreate function");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        showPhoneStatePermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // Substitute method for onCreate().
    private void onCreatePermissionGranted()
    {
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////NAVIGATION DRAWER//////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        NavigationView navigationMenu = (NavigationView) findViewById(R.id.sideFloatingMenuContainer);
        //Attaching the current Activity to the Navigation Drawer.
        navigationMenu.setNavigationItemSelectedListener(this);

        //Object to contain the Android ActionBar used.
        final ActionBar actionBar = getSupportActionBar();
        //This enables the home button on actionbar.
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Changes the home button to Hamburger icon.
        sideMenu = (DrawerLayout) findViewById(R.id.sideMenu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this , sideMenu , R.string.openDrawer , R.string.closeDrawer );
        //Adds the DrawerToggle to the Drawer.
        sideMenu.addDrawerListener(toggle);
        toggle.syncState();

        userNameSideFloatingMenu = (TextView) findViewById(R.id.sideFloatingMenu_userName);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////TABBED LAYOUT////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        SectionsPagerAdapter mSectionsPagerAdapter;
        ViewPager mViewPager;

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////SLIDING UP PANEL/////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*initSlidingUpPanel();
        initPanelListner();*/
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////MUSIC SERVICES/////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Fetch music from device
        final Context mainContext = this;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HelperMusicClass.getSongList(mainContext);
            }
        }).start();

        // Create MediaBrowserService.
        ElementsAll.mediaBrowser = new MediaBrowser(this, new ComponentName(this,
                BackgroundMediaService.class),connectionCallback, null);
        ElementsAll.mediaBrowser.connect();

        //Starting the Background Service.
        ElementsAll.serviceIntent = new Intent(this, BackgroundMediaService.class);
        startService(ElementsAll.serviceIntent);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate( R.menu.overflowmenu , menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        //Handle navigation view item Click.
        sideMenu = (DrawerLayout) findViewById(R.id.sideMenu);
        switch(item.getItemId())
        {
            case R.id.nav_hostMusic :
                sideMenu.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_musicPlayer :
                sideMenu.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_speakerMenu :
                sideMenu.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_setting :
                Intent i = new Intent(this , Settings.class );
                startActivity(i);
                sideMenu.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_quit :
                sideMenu.closeDrawer(GravityCompat.START);
                Toast.makeText(getBaseContext(), "App Exited!!",Toast.LENGTH_SHORT).show();
                finish(); //super.onBackPressed();
                return true;

            default: return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        sideMenu = (DrawerLayout) findViewById(R.id.sideMenu);
        if( sideMenu.isDrawerOpen(GravityCompat.START) )
        {
            sideMenu.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (ElementsAll.back_pressed + ElementsAll.TIME_DELAY > System.currentTimeMillis())
            {
                finish();
                //super.onBackPressed();
            }
            else
                Toast.makeText(getBaseContext(), "Press once again to exit!",Toast.LENGTH_SHORT).show();

            ElementsAll.back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy()
    {
        ElementsAll.activity = false;
        Log.i("Check", "Activity Stopped --> "+ElementsAll.activity);

        if( ElementsAll.mediaBrowser!=null )
        {
            ElementsAll.mediaBrowser.disconnect();
        }
        if( BackgroundMediaService.mediaPlayer != null )
        {
            if( !BackgroundMediaService.mediaPlayer.isPlaying() )
                stopService(ElementsAll.serviceIntent);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home :
                if( sideMenu.isDrawerOpen(GravityCompat.START))
                    sideMenu.closeDrawer(GravityCompat.START);
                else
                    sideMenu.openDrawer(GravityCompat.START);
                break;

            case R.id.overFlow_search :
                return true;

            case R.id.overFlow_share :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Sub Class for TabLayout.
    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            /*// getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);*/

            //Return the Current Tab.
            switch(position)
            {
                case 0:
                    return new SongsTab();
                case 1:
                    return new FoldersTab();
                case 2:
                    return new PlaylistTab();
                case 3:
                    return new ArtistTab();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch(position)
            {
                case 0:
                    return "Songs";
                case 1:
                    return "Folders";
                case 2:
                    return "PlayList";
                case 3:
                    return "Artist";
            }
            return null;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 4;
        }
    }


    /////////////////////////////////////Connection CallBack's//////////////////////////////////////
    private MediaBrowser.ConnectionCallback connectionCallback =
            new MediaBrowser.ConnectionCallback()
            {
                @Override
                public void onConnected()
                {
                    // Create a MediaController with the token for the MediaSession.
                    MediaController mediaController = new MediaController
                            (MainActivity.this, ElementsAll.mediaBrowser.getSessionToken());
                    // Save the controller.
                    setMediaController(mediaController);

                    // Register a Callback to stay in sync
                    mediaController.registerCallback(mediaControllerCallback);
                }
            };
    private MediaController.Callback mediaControllerCallback = new MediaController.Callback()
    {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackState state)
        {
            super.onPlaybackStateChanged(state);

            switch( state.getState() )
            {
                case PlaybackState.STATE_BUFFERING:
                    break;

                case PlaybackState.STATE_CONNECTING:
                    break;

                case PlaybackState.STATE_ERROR:
                    break;

                case PlaybackState.STATE_FAST_FORWARDING:
                    break;

                case PlaybackState.STATE_NONE:
                    break;

                case PlaybackState.STATE_REWINDING:
                    break;

                case PlaybackState.STATE_SKIPPING_TO_NEXT:
                    break;

                case PlaybackState.STATE_SKIPPING_TO_PREVIOUS:
                    break;

                case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM:
                    break;

                case PlaybackState.STATE_STOPPED:
                    break;

                case PlaybackState.STATE_PLAYING:
                    ElementsAll.currentState = ElementsAll.STATE_PLAYING;
                    break;

                case PlaybackState.STATE_PAUSED:
                    ElementsAll.currentState = ElementsAll.STATE_PAUSED;
                    break;
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
