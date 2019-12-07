# FileStore.io

This is powerful and easy to use library. You can access All files tree method to make your work easy. This library  developing time.

</br>

# Setup

This library requires `minSdkVersion` to `19` or above.

#### Step #1. Add the JitPack repository to your build file:

```gradle
allprojects {
    repositories {
	...
	maven { url "https://jitpack.io" }
    }
}
```

#### Step #2. Add the dependency.

```groovy
dependencies {
       implementation 'com.github.techhuntdevelopers:FileStore.io:1.0.0'
}
```

#### Step #2. Required permission.
```groovy
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

#### Step #2. Checking permission.
```groovy
private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 89);
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
                        //Write some code
                    } else {
                       //Permission deniedl̥
                    }
                }
                break;
        }
    }

```

#### Step #5. How to access.

```groovy
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
```
