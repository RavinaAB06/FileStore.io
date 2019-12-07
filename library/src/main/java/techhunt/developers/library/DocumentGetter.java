package techhunt.developers.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import techhunt.developers.library.model.FilesModel;

public class DocumentGetter {

    String[] types = new String[]{".pdf", ".xml", ".html", ".htm", ".asm", ".text/x-asm", ".def", ".in", ".rc",
            ".list", ".log", ".pl", ".prop", ".properties", ".rc",
            ".doc", ".docx", ".xls", ".xlsx", ".msg", ".ppt", ".pptx", ".odt", ".pages", ".rtf", ".txt", ".wpd", ".wps", ".txt"};

    public static List<File> getDocuments(Context context, String[] extensions) {
        ArrayList<File> list = new ArrayList<>();
        Uri uri = MediaStore.Files
                .getContentUri("external");
        String[] data = new String[]{MediaStore.Files.FileColumns.DATA};


        Cursor cursor = context.getContentResolver().query(uri, data, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));
                if (file.exists())
                    if (Arrays.asList(extensions).contains("." + getExtensionFromFilePath(file.getPath())) && !file.isDirectory()) {
                        list.add(file);
                    }
            }
            cursor.close();
        }
        return list;
    }

    public static String getExtensionFromFilePath(String fullPath) {
        String[] filenameArray = fullPath.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    @SuppressLint("CheckResult")
    public static void getArchives(Context context, String[] extensions, ApkCallback callback) {
        getDocumentFromStorage(getDocuments(context, extensions))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(media -> {
                            if (media.getFile() != null)
                                if (callback != null) callback.onDocAdd(media);
                        },
                        throwable -> {
                            if (callback != null) callback.onError(throwable);
                        },
                        () -> {
                            if (callback != null) callback.onComplete();
                        });
    }


    private static Observable<FilesModel> getDocumentFromStorage(List<File> files) {
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
        void onDocAdd(FilesModel model);

        void onError(Throwable throwable);

        void onComplete();
    }
}
