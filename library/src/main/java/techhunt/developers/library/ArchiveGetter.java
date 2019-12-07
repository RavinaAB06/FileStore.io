package techhunt.developers.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.ApkFiles;
import techhunt.developers.library.model.FilesModel;

public class ArchiveGetter {

    public static String getExtensionFromFilePath(String fullPath) {
        String[] filenameArray = fullPath.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public static List<File> getArchives(Context context) {
        List<File> list = new ArrayList<>();
        Uri uri = MediaStore.Files
                .getContentUri("external");
        String[] data = new String[]{MediaStore.Files.FileColumns.DATA};
        String[] types = new String[]{".zip", ".rar", ".tar", ".tar.gz", ".7z"};

        Cursor cursor = context.getContentResolver().query(uri, data, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));
                if (file.exists())
                    if (Arrays.asList(types).contains("." + getExtensionFromFilePath(file.getPath())) && !file.isDirectory()) {
                        list.add(file);
                    }
            }
            cursor.close();
        }
        return list;
    }

    @SuppressLint("CheckResult")
    public static void getArchives(Context context, ApkCallback callback) {
        getArchiveromStorage(getArchives(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(media -> {
                            if (media.getFile() != null)
                                if (callback != null) callback.onArchiveAdd(media);
                        },
                        throwable -> {
                            if (callback != null) callback.onError(throwable);
                        },
                        () -> {
                            if (callback != null) callback.onComplete();
                        });
    }


    private static Observable<FilesModel> getArchiveromStorage(List<File> files) {
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
        void onArchiveAdd(FilesModel model);

        void onError(Throwable throwable);

        void onComplete();
    }
}
