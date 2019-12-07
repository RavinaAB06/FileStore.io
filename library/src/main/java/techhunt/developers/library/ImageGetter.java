package techhunt.developers.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.Album;
import techhunt.developers.library.model.Media;
import techhunt.developers.library.provider.AlbumsHelper;
import techhunt.developers.library.provider.CPHelper;
import techhunt.developers.library.provider.HandlingAlbums;

public class ImageGetter {

    @SuppressLint("CheckResult")
    public static void getImageAlbums(Context context, boolean isHidden, AlbumCallback callback) {
        SQLiteDatabase db = HandlingAlbums.getInstance(context).getReadableDatabase();
        CPHelper.getAlbums(context, isHidden, new ArrayList<String>(), AlbumsHelper.getSortingMode(), AlbumsHelper.getSortingOrder())
                .subscribeOn(Schedulers.io())
                .map(album -> album.withSettings(HandlingAlbums.getSettings(db, album.getPath())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        album -> {
                            if (callback != null)
                                callback.onAlbumAdd(album);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            if (callback != null)
                                callback.onError(throwable);
                        },
                        () -> {
                            db.close();

                            if (callback != null)
                                callback.onComplete();
                        });
    }

    @SuppressLint("CheckResult")
    public static void getAlbumsImage(Context context, Album album, MediaCallback callback) {
        CPHelper.getMedia(context, album)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(media -> {
                            if (media.getFile() != null) {
                                if (callback != null)
                                    callback.onMediaAdd(media);
                            }
                        },
                        throwable -> {
                            Log.wtf("asd", throwable);
                            if (callback != null)
                                callback.onError(throwable);
                        },
                        () -> {
                            if (callback != null)
                                callback.onComplete();
                        });
    }

    @SuppressLint("CheckResult")
    public void getAllImages(Context context, MediaCallback callback) {
        SQLiteDatabase db = HandlingAlbums.getInstance(context).getReadableDatabase();
        CPHelper.getAllMediaFromMediaStore(context, AlbumsHelper.getSortingMode(), AlbumsHelper.getSortingOrder())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        media -> {
                            if (media.getFile() != null)
                                if (callback != null)
                                    callback.onMediaAdd(media);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            if (callback != null)
                                callback.onError(throwable);
                        },
                        () -> {
                            db.close();
                            if (callback != null)
                                callback.onComplete();
                        });
    }

    public interface AlbumCallback {
        void onAlbumAdd(Album album);

        void onError(Throwable throwable);

        void onComplete();
    }

    public interface MediaCallback {
        void onMediaAdd(Media media);

        void onError(Throwable throwable);

        void onComplete();
    }
}
