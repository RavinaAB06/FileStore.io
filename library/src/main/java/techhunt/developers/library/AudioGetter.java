package techhunt.developers.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.FilesModel;

public class AudioGetter {

    public static ArrayList<File> getAudioLibrary(Context context) {
        ArrayList<File> list = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] data = new String[]{MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor cursor = context.getContentResolver().query(uri, data, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));
                if (file.exists()) list.add(file);
            }
            cursor.close();
        }
        return list;
    }

    @SuppressLint("CheckResult")
    public static void getAudios(Context context, ApkCallback callback) {
        getAudioromStorage(getAudioLibrary(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(media -> {
                            if (media.getFile() != null)
                                if (callback != null) callback.onAudioAdd(media);
                        },
                        throwable -> {
                            if (callback != null) callback.onError(throwable);
                        },
                        () -> {
                            if (callback != null) callback.onComplete();
                        });
    }


    private static Observable<FilesModel> getAudioromStorage(List<File> files) {
        return Observable.create(subscriber -> {
            try {
                if (files != null && files.size() > 0)
                    for (File file : files)
                        subscriber.onNext(new FilesModel(file));
                subscriber.onComplete();
            } catch (Exception err) {
                subscriber.onError(err);
            }
        });
    }

    public interface ApkCallback {
        void onAudioAdd(FilesModel model);

        void onError(Throwable throwable);

        void onComplete();
    }
}
