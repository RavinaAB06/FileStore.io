package techhunt.developers.filestoreio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import techhunt.developers.library.ApkGetter;
import techhunt.developers.library.ArchiveGetter;
import techhunt.developers.library.AudioGetter;
import techhunt.developers.library.DocumentGetter;
import techhunt.developers.library.ImageGetter;
import techhunt.developers.library.VideoGetter;
import techhunt.developers.library.model.Album;
import techhunt.developers.library.model.ApkFiles;
import techhunt.developers.library.model.FilesModel;
import techhunt.developers.library.model.Media;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    public Album albums;
    public Album videoAlbums;
    private final int PERMISSION_REQUEST_CODE = 89;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission())
            fatchData();
        else
            requestPermission();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        fatchData();
                    } else {
                        if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        } else {
                            finish();
                            System.exit(0);
                        }
                    }
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void fatchData() {

        /**Images Albums*/
        ImageGetter.getImageAlbums(MainActivity.this, false, new ImageGetter.AlbumCallback() {

            @Override
            public void onAlbumAdd(Album album) {
                albums = album;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                /**Image From Album*/
                ImageGetter.getAlbumsImage(MainActivity.this, albums, new ImageGetter.MediaCallback() {
                    @Override
                    public void onMediaAdd(Media media) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });


        /**Video Albums*/
        VideoGetter.getVideoAlbums(MainActivity.this, false, new VideoGetter.AlbumCallback() {
            @Override
            public void onAlbumAdd(Album album) {
                videoAlbums = album;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                /**Video From Album*/
                VideoGetter.getAlbumsImage(MainActivity.this, videoAlbums, new VideoGetter.MediaCallback() {
                    @Override
                    public void onMediaAdd(Media media) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });



        /**Audio*/
        AudioGetter.getAudios(MainActivity.this, new AudioGetter.ApkCallback() {
            @Override
            public void onAudioAdd(FilesModel model) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**Apk*/
        ApkGetter.getApks(MainActivity.this, new ApkGetter.ApkCallback() {
            @Override
            public void onApkAdd(ApkFiles apkFile) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**Archive*/
        ArchiveGetter.getArchives(MainActivity.this, new ArchiveGetter.ApkCallback() {
            @Override
            public void onArchiveAdd(FilesModel model) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**Document*/
        DocumentGetter.getArchives(MainActivity.this, new String[]{".pdf", ".doc"}, new DocumentGetter.ApkCallback() {
            @Override
            public void onDocAdd(FilesModel model) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
