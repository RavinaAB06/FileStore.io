package techhunt.developers.library;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.ApkFiles;

public class ApkGetter {

    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file.getName()));
    }

    private static String getExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    public static ArrayList<File> getAPKLibrary(Context context) {
        ArrayList<File> list = new ArrayList<>();
        Uri uri = MediaStore.Files
                .getContentUri("external");
        String[] data = new String[]{MediaStore.Files.FileColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, data, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));
                if (file.exists())
                    if (FileUtil.FileType.APK == FileUtil.FileType.getFileType(file))
                        list.add(file);
            }
            cursor.close();
        }
        return list;
    }

    public static void getApks(Context context, ApkCallback callback) {
        getAppFromStorage(getAPKLibrary(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(media -> {
                            if (media.getFile() != null)
                                if (callback != null) callback.onApkAdd(media);
                        },
                        throwable -> {
                            if (callback != null) callback.onError(throwable);
                        },
                        () -> {
                            if (callback != null) callback.onComplete();
                        });
    }


    private static Observable<ApkFiles> getAppFromStorage(List<File> files) {
        return Observable.create(subscriber -> {
            try {
                if (files != null && files.size() > 0)
                    for (File file : files)
                        subscriber.onNext(new ApkFiles(file));
                subscriber.onComplete();

            } catch (Exception err) {
                subscriber.onError(err);
            }
        });
    }

    public interface ApkCallback {
        void onApkAdd(ApkFiles apkFile);

        void onError(Throwable throwable);

        void onComplete();
    }
}
