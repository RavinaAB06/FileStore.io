package techhunt.developers.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.Album;
import techhunt.developers.library.model.Media;
import techhunt.developers.library.provider.CPHelper;

public class MediaHelper {

    private static Uri external = MediaStore.Files.getContentUri("external");

    public static Observable<Media> deleteMedia(Context context, Media media) {
        return Observable.create(subscriber -> {
            try {
                internalDeleteMedia(context, media);
                subscriber.onNext(media);
            } catch (Exception e) {
                subscriber.onError(e);
                Log.e("deleteMedia: ", "" + e);
            }
            subscriber.onComplete();
        });
    }

    @SuppressLint("CheckResult")
    public static Observable<Album> deleteAlbum(Context context, Album album) {
        return Observable.create(subscriber -> {

            ArrayList<Observable<Media>> sources = new ArrayList<>(album.getCount());

            CPHelper.getMedia(context, album)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            media -> sources.add(MediaHelper.deleteMedia(context.getApplicationContext(), media)),
                            subscriber::onError,
                            () -> Observable.mergeDelayError(sources)
                                    .observeOn(AndroidSchedulers.mainThread(), true)
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(
                                            item -> {
                                            },
                                            subscriber::onError,
                                            () -> {
                                                subscriber.onNext(album);
                                                subscriber.onComplete();
                                            })
                    );
        });
    }

    public static boolean internalDeleteMedia(Context context, Media media) throws Exception {
        File file = new File(media.getPath());
        StorageHelper.deleteFile(context, file);
        context.getContentResolver().delete(external, MediaStore.MediaColumns.DATA + "=?", new String[]{file.getPath()});
        return true;
    }

    public static boolean renameMedia(Context context, Media media, String newName) {
        boolean success = false;
        try {
            File from = new File(media.getPath());
            File to = new File(StringUtils.getPhotoPathRenamed(media.getPath(), newName));
            if (success = StorageHelper.moveFile(context, from, to)) {
                context.getContentResolver().delete(external,
                        MediaStore.MediaColumns.DATA + "=?", new String[]{from.getPath()});

                scanFile(context, new String[]{to.getAbsolutePath()});
                media.setPath(to.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean moveMedia(Context context, Media media, String targetDir) {
        boolean success = false;
        try {
            File from = new File(media.getPath());
            File to = new File(targetDir, from.getName());
            if (success = StorageHelper.moveFile(context, from, to)) {

                context.getContentResolver().delete(external,
                        MediaStore.MediaColumns.DATA + "=?", new String[]{from.getPath()});


                scanFile(context, new String[]{StringUtils.getPhotoPathMoved(media.getPath(), targetDir)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean copyMedia(Context context, Media media, String targetDir) {
        boolean success = false;
        try {
            File from = new File(media.getPath());
            File to = new File(targetDir);
            if (success = StorageHelper.copyFile(context, from, to))
                scanFile(context, new String[]{StringUtils.getPhotoPathMoved(media.getPath(), targetDir)});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), path, null, null);
    }
}