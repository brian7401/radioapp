package com.radio.app.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.radio.app.BuildConfig;
import com.radio.app.Config;
import com.radio.app.services.PlaybackStatus;
import com.radio.app.services.RadioManager;
import com.radio.app.R;
import com.google.android.material.navigation.NavigationView;
import com.radio.app.services.metadata.Metadata;
import com.radio.app.services.parser.UrlParser;
import com.radio.app.utilities.PermissionsFragment;
import com.radio.app.utilities.Tools;

import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, PermissionsFragment, Tools.EventListener {

    RadioManager radioManager;
    String radio_url = Config.RADIO_STREAM_URL;
    RoundedImageView albumArtView;
    ImageView bgImageView;
    FloatingActionButton buttonPlayPause;
    EqualizerView equalizerView;
    Tools tools;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        tools = new Tools(this);
        initializeUIElements();

        new Handler().postDelayed(() -> buttonPlayPause.performClick(), 1000);

        albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        bgImageView.setImageResource(Tools.BACKGROUND_IMAGE_ID);

        radioManager = RadioManager.with();

        AsyncTask.execute(() -> {
            radio_url = (UrlParser.getUrl(radio_url));
            this.runOnUiThread(() -> {

            });
        });

        if (isPlaying()) {
            onAudioSessionId(RadioManager.getService().getAudioSessionId());
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }

        if (id == R.id.privacy_policy) {

            String url = "file:///android_asset/privacy_policy.html";
            String name ="Privacy Policy";
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            startActivity(intent);

        }

        if (id == R.id.email) {
            emailus();

        }

        if (id == R.id.call) {
            callus();

        }

        if (id == R.id.share) {
            shareapp();

        }

        if (id == R.id.rate) {
            rateapp();

        }

        if (id == R.id.exit) {
            exitDialog();

        }

        /*--------------------- Social links -----------------*/

        if (id == R.id.facebook) {
            String url = getString(R.string.facebook_url);
            String name ="Our Facebook Page";
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            startActivity(intent);

        }

        if (id == R.id.instagram) {
            String url = getString(R.string.instagram_url);
            String name ="Our Instagram Page";
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            startActivity(intent);



        }

        if (id == R.id.twitter) {
            String url = getString(R.string.twitter_url);
            String name ="Our Twitter Handle";
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            startActivity(intent);

        }

        if (id == R.id.youtube) {
            String url = getString(R.string.youtube_url);
            String name ="Our YouTube Channel";
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            startActivity(intent);

        }

        /*--------------------- Social links -----------------*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //open settings activity from toolbar
        if (item.getItemId() == R.id.action_share) {
            shareapp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 121) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(), "Permission not granted.\nWe can't pause music when phone ringing.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            exitDialog();
        }
    }


    /****************************** App intents *****************************/

    private void emailus() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",  getString(R.string.radio_station_email), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.email_message);
        startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
    }

    private void callus() {
        String phone = getString(R.string.radio_station_phone);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void rateapp(){
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }

    }

    private void shareapp(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out the " + R.string.app_name + " at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }



    @Override
    public void onEvent(String status) {
        switch (status) {
            case PlaybackStatus.LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case PlaybackStatus.ERROR:
                makeSnackBar(R.string.error_retry);
                break;
        }

        if (!status.equals(PlaybackStatus.LOADING))
            progressBar.setVisibility(View.INVISIBLE);
        updateButtons();

    }

    @Override
    public void onAudioSessionId(Integer i) {
    }

    @Override
    public void onStart() {
        super.onStart();
        Tools.registerAsListener(this);
    }

    @Override
    public void onStop() {
        Tools.unregisterAsListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (!radioManager.isPlaying())
            radioManager.unbind(MainActivity.this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtons();
        radioManager.bind(MainActivity.this);
    }

    private void initializeUIElements() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

        equalizerView = findViewById(R.id.equalizer_view);

        albumArtView = findViewById(R.id.radio_album);
        bgImageView = findViewById(R.id.bg_image_view);

        if(Config.ALBUM_ART_DISK){
            albumArtView.setOval(true);
        }else {
            albumArtView.setOval(false);
        }

        buttonPlayPause = findViewById(R.id.playBtn);
        buttonPlayPause.setOnClickListener(this);

        equalizerView.stopBars();
        updateButtons();
    }

    private void updateButtons() {
        if (isPlaying() || progressBar.getVisibility() == View.VISIBLE) {
            //If another stream is playing, show this in the layout
            if (RadioManager.getService() != null && radio_url != null && !radio_url.equals(RadioManager.getService().getStreamUrl())) {
                buttonPlayPause.setImageResource(R.drawable.ic_play_white);
                //TODO findViewById(R.id.already_playing_tooltip).setVisibility(View.VISIBLE);
                //If this stream is playing, adjust the buttons accordingly
            } else {
                if (RadioManager.getService() != null && RadioManager.getService().getMetaData() != null) {
                    onMetaDataReceived(RadioManager.getService().getMetaData(), RadioManager.getService().getAlbumArt());
                }
                buttonPlayPause.setImageResource(R.drawable.ic_pause_white);
                //TODO findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);
            }
        } else {
            //If this stream is paused, adjust the buttons accordingly
            buttonPlayPause.setImageResource(R.drawable.ic_play_white);
            //TODO findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);

            updateMediaInfoFromBackground(null, null);
        }

        if (isPlaying()) {
            equalizerView.animateBars();
        } else {
            equalizerView.stopBars();
        }

    }

    @Override
    public void onClick(View v) {
        requestStoragePermission();
    }

    private void startStopPlaying() {
        radioManager.playOrPause(radio_url);
        updateButtons();

    }

    private void stopService() {
        radioManager.stopServices();
        Tools.unregisterAsListener(this);
    }


    //@param info - the text to be updated. Giving a null string will hide the info.
    public void updateMediaInfoFromBackground(String info, Bitmap image) {

        TextView nowPlayingTitle = findViewById(R.id.now_playing);
        TextView nowPlaying = findViewById(R.id.radio_description);

        if (info != null)
            nowPlaying.setText(info);

        if (info != null && nowPlayingTitle.getVisibility() == View.GONE) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlaying.setVisibility(View.VISIBLE);
        } else if (info == null) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlayingTitle.setText(R.string.now_playing);
            nowPlaying.setVisibility(View.VISIBLE);
            nowPlaying.setText(R.string.app_name);
        }

        if (image != null) {
            albumArtView.setImageBitmap(image);
            bgImageView.setImageBitmap(image);
        } else {
            albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
            bgImageView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        }

    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public void onMetaDataReceived(Metadata meta, Bitmap image) {
        //Update the mediainfo shown above the controls
        String artistAndSong = null;
        if (meta != null && meta.getArtist() != null)
            artistAndSong = meta.getArtist() + " - " + meta.getSong();
        updateMediaInfoFromBackground(artistAndSong, image);
    }

    private boolean isPlaying() {
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }


    private void makeSnackBar(int text) {
        Snackbar bar = Snackbar.make(buttonPlayPause, text, Snackbar.LENGTH_SHORT);
        bar.show();
        ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(getResources().getColor(R.color.white));
    }



    public void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setPositiveButton(getResources().getString(R.string.quit), (dialogInterface, i) -> {
            stopService();
            MainActivity.this.finish();
        });
        dialog.setNegativeButton(getResources().getString(R.string.minimize), (dialogInterface, i) -> minimizeApp());
        dialog.setNeutralButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> {
        });
        dialog.show();
    }


    public void minimizeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void requestStoragePermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted

                        if (report.areAllPermissionsGranted()) {
                            if (!isPlaying()) {
                                if (radio_url != null) {
                                    startStopPlaying();
                                    //TODO showInterstitialAd();
                                    //Check the sound level
                                    AudioManager audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                                    int volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    if (volume_level < 2) {
                                        makeSnackBar(R.string.volume_low);
                                    }
                                } else {
                                    //The loading of urlToPlay should happen almost instantly, so this code should never be reached
                                    makeSnackBar(R.string.error_retry_later);
                                }
                            } else {
                                startStopPlaying();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(MainActivity.this, "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
