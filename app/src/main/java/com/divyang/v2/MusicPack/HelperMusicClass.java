package com.divyang.v2.MusicPack;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.divyang.v2.ElementsAll;
import com.divyang.v2.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;

public abstract class HelperMusicClass
{
    // Function To Handle Intent for The Button Press for Media Control.
    static void handleIntent(MediaSession mediaSession, Intent intent)
    {
        if(mediaSession != null && intent != null &&
                "android.intent.action.MEDIA_BUTTON".equals(intent.getAction()) &&
                intent.hasExtra("android.intent.extra.KEY_EVENT"))
        {
            KeyEvent ke = intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            MediaController mediaController = mediaSession.getController();
            mediaController.dispatchMediaButtonEvent(ke);
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    //Cache for Music Art Bitmap.
    private static LruCache<String,Bitmap> artCache;
    ////////////////////////////////////Functions For Cache/////////////////////////////////////////
    static Bitmap getBitmapFromCache(String key)
    {
        return artCache.get(key);
    }

    static void setBitmapToCache(String key, Bitmap bitmap)
    {
        if( key != null && bitmap != null)
        {
            artCache.put(key,bitmap);
        }
        else
            Log.d("Cache Error","Key/Bitmap Null");
        /*if(getBitmapFromCache(key)!= null)
        {
            Log.i("Check","Setting Cache for key "+key+" in Main Activity");
            artCache.put(key,bitmap);
        }*/
    }

    public static void initialiseCache()
    {
        //Cache Initialisation.
        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize/10;
        Log.d("Cache Info","Creating Cache with Size "+cacheSize);
        artCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getByteCount()/1024;
            }
        };
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    // Concurrency helper Function.
    static boolean checkBitmapLoaderTask(String path, ImageView imageView)
    {
        BitmapLoaderTask bitmapLoaderTask = getBitmapLoaderTask(imageView);
        if(bitmapLoaderTask != null)
        {
            final String imageViewAssoPath = bitmapLoaderTask.getConcurencyPath();
            if(imageViewAssoPath != null)
            {
                if(!imageViewAssoPath.equals(path))
                {
                    bitmapLoaderTask.cancel(true);
                }
                else
                {
                    //BitmapLoaderTask path is same as the imageview is expecting so do NOTHING.
                    return false;
                }
            }
        }
        return true;
    }

    static BitmapLoaderTask getBitmapLoaderTask(ImageView imageView)
    {
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof AsynArtLoader)
        {
            AsynArtLoader asynArtLoader = (AsynArtLoader) drawable;
            return asynArtLoader.getBitmapLoaderTask();
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////CLASSES/////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * Helper Class For Concurrency Problem of ListView With AsyncTask.
     */
    static class AsynArtLoader extends BitmapDrawable
    {
        final WeakReference<BitmapLoaderTask> taskWeakReference;

        AsynArtLoader(Resources res, Bitmap bitmap, BitmapLoaderTask bitmapLoaderTask, String path,
                      Long id)
        {
            super(res, bitmap);
            taskWeakReference = new WeakReference<>(bitmapLoaderTask);
            bitmapLoaderTask.execute(path,Long.toString(id));
        }

        BitmapLoaderTask getBitmapLoaderTask()
        {
            return taskWeakReference.get();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////ENDS///////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////


    static byte[] getMediaArtByteArray(String path)
    {
        MediaMetadataRetriever metaDataArt = new MediaMetadataRetriever();
        try
        {
            metaDataArt.setDataSource(path);
        }
        catch (IllegalArgumentException e)
        {
            Log.i("Check","Exception Thrown");
        }
        return metaDataArt.getEmbeddedPicture();
    }

    /////////////////////////////Function To Get SongList From Storage//////////////////////////////
    public static void getSongList(Context context)
    {
        //Meta data for Songs.
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver resolver = context.getContentResolver();
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        //String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        //String[] selectionArgsMp3 = new String[]{ mimeType };
        //String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        //String[] projection = null;
        Cursor cursor = resolver.query(musicUri, null/*projection*/, selection, null/*selectionArgsMp3*/, null/*sortOrder*/);

        //Declarations to be used.
        int titleCol;
        int idColumn;
        int artistColumn;
        int durationColumn;
        long id;
        String title;
        String artist;
        long duration;
        int defaultResourceId;
        SongDetails song;

        //Usage for Bitmap.
        String path;

        if (cursor != null && cursor.moveToFirst())
        {
            try
            {
                do
                {
                    //metaData.setDataSource(this,);
                    //get Columns
                    titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    //Get values corresponding to above columnIndex's.
                    id = cursor.getLong(idColumn);
                    title = cursor.getString(titleCol);
                    artist = cursor.getString(artistColumn);
                    duration = cursor.getLong(durationColumn);
                    defaultResourceId = R.drawable.default_cover_art;

                    //Try at mediaMetaDataRetriever.
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    //Formatting Details to Fit View Page Correctly.
                    StringBuilder titleBuild = new StringBuilder();
                    titleBuild.append(title);
                    if(titleBuild.length() > 21)
                    {
                        titleBuild.setLength(21);
                        title = titleBuild.toString()+"...";
                    }
                    else
                        title = titleBuild.toString();
                    StringBuilder artistBuild = new StringBuilder();
                    artistBuild.append(artist);
                    if(artistBuild.length() > 20)
                    {
                        artistBuild.setLength(20);
                        artist = artistBuild.toString()+"...";
                    }
                    else
                        artist = artistBuild.toString();

                    //Pass these value to SongDetails Class to initialise it.
                    song = new SongDetails(title, artist, id, duration, path, defaultResourceId);
                    //Add to the list
                    ElementsAll.songDetailArrayList.add(song);
                } while (cursor.moveToNext());
            }
            finally {
                cursor.close();
            }
        }
        //Sort's the songs list alphabetically according to their title.
        Collections.sort(ElementsAll.songDetailArrayList, new Comparator<SongDetails>()
        {
            public int compare(SongDetails a, SongDetails b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////Functions For Decoding byte[]///////////////////////////////
    static Bitmap decodeBitmapFrombyteArray(byte[] ArtbyteArray, int reqWidth, int reqHeight)
    {
        if(ArtbyteArray != null)
        {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            BitmapFactory.decodeByteArray(ArtbyteArray, 0, ArtbyteArray.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(ArtbyteArray, 0, ArtbyteArray.length, options);
        }
        else
            return null;

    }

    private static int calculateInSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
