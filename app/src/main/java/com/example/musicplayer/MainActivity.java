package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    //HashMap<String, String> songArtists;
    String[] songNames;
    String[] artists;
    Bitmap[] albumArts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.songListView);
        //songArtists = new HashMap<>();

        runtimePermission();
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findSongs (File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if(files != null) { //MAKES SURE FILES EXIST -- PREVENTS POTENTIAL CRASH
            for(File f: files) {
                if (f.isDirectory() && !f.isHidden() && f.getName().equals("Music")) { //CHECKS FOR MUSIC FOLDER IN DOWNLOADS FOLDER
                    arrayList.addAll(findSongs(f));
                } else {
                    if (f.getName().endsWith(".wav")) {
                        arrayList.add(f);
                    } else if (f.getName().endsWith(".mp3")) {
                        arrayList.add(f);
                    }
                }
            }
        }
        return arrayList;
    }

    void displaySongs() {
        final ArrayList<File> songs = findSongs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        //Environment.getExternalStorageDirectory()
        songNames = new String[songs.size()];
        artists = new String[songs.size()];
        albumArts = new Bitmap[songs.size()];
        for(int i = 0; i < songs.size(); ++i) {
            byte[] albumArt;
            //ImageView imageView;
            //imageView = (ImageView)findViewById(R.id.image);

            songNames[i] = songs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
            Log.d("success", songNames[i]);
            MediaMetadataRetriever mmr = (MediaMetadataRetriever) new MediaMetadataRetriever();
            Uri uri = (Uri)Uri.fromFile(songs.get(i));

            mmr.setDataSource(this, uri);

            albumArt = mmr.getEmbeddedPicture();
            if(albumArt != null) {
                albumArts[i] = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
             //   imageView.setImageBitmap(b);
            }
            else {
                albumArts[i] = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                //imageView.setBackgroundColor(Color.GRAY);
            }

            artists[i] = (String) mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if(artists[i] == null)
                artists[i] = "Unknown Artist";
            //songArtists.put(songName, artist);
        }
        //List<HashMap<String, String>> listItems = new ArrayList<>();
        //SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.song_list_item,
        //        new String[]{"First Line", "Second Line"},
        //        new int[]{R.id.text1, R.id.text2});


/*        Iterator it = songArtists.entrySet().iterator();
        while(it.hasNext()) {
            HashMap<String, String> resultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultMap.put("First Line", pair.getKey().toString());
            resultMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultMap);
        }*/

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, songNames);
        //listView.setAdapter(adapter);
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return songNames.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.song_list_item, null);
            TextView songText = myView.findViewById(R.id.text1);
            TextView songArtist = myView.findViewById(R.id.text2);
            ImageView songAlbumArt = myView.findViewById(R.id.image);
            songText.setSelected(true);
            songArtist.setSelected(true);
            songAlbumArt.setSelected(true);
            songText.setText(songNames[i]);
            songArtist.setText(artists[i]);
            songAlbumArt.setImageBitmap(albumArts[i]);

            return myView;
        }
    }
}