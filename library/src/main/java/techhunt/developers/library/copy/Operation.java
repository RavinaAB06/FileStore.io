package techhunt.developers.library.copy;

import android.app.Activity;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Operation {

    public interface Callback {
        void onProgressUpdate(int i, int fileCount, int progress);

        void onComplete(boolean isComplete);
    }

    public static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public class CopyMoveFilesTask extends AsyncTask<Boolean, String, Boolean> {

        private Activity activity;
        private List<File> fileList;
        private File destFile;
        private boolean isMove;
        private int fileCount = 0;
        private Callback callback;

        public CopyMoveFilesTask(Activity activity, List<File> fileList, File destFile, boolean isMove, Callback callback) {
            this.activity = activity;
            this.fileList = fileList;
            this.destFile = destFile;
            this.isMove = isMove;
            this.callback = callback;
        }

        public boolean createOrExistsFile(File file) {
            if (file == null) return false;
            if (file.exists()) return file.isFile();
            if (!createOrExistsDir(file.getParentFile())) return false;
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Boolean... voids) {

            for (int i = 0; i < fileList.size(); i++) {
                fileCount = i + 1;
                File srcFile = fileList.get(i);
                if (srcFile == null || new File(destFile + "/" + srcFile.getName()) == null)
                    return false;
                if (!srcFile.exists() || !srcFile.isFile()) return false;
                if (new File(destFile + "/" + srcFile.getName()).exists() && new File(destFile + "/" + srcFile.getName()).isFile())
                    return false;
                if (!createOrExistsDir(new File(destFile + "/" + srcFile.getName()).getParentFile()))
                    return false;

                try {
                    InputStream is = new FileInputStream(srcFile);
                    if (new File(destFile + "/" + srcFile.getName()) == null || is == null)
                        return false;
                    if (!createOrExistsFile(new File(destFile + "/" + srcFile.getName())))
                        return false;
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(destFile + "/" + srcFile.getName()), false));
                        byte[] data = new byte[1024];
                        int len;
                        long fileLength = srcFile.length();
                        long total = 0;
                        while ((len = is.read(data, 0, 1024)) != -1) {
                            total += len;
                            publishProgress("" + (int) ((total * 100) / fileLength));
                            os.write(data, 0, len);
                        }

                        os.flush();
                        os.close();
                        is.close();

                        if (isMove) {
//                            StorageHelper.deleteFile(activity, srcFile);
                            activity.getContentResolver().delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA + "=?", new String[]{srcFile.getPath()});
                        }
                        if (fileList.size() == fileCount)
                            return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("IOException", ": " + e);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
//                        CloseUtils.closeIO(is, os);
                    }
                } catch (FileNotFoundException e) {
                    Log.e("FileNotFoundException", ": " + e);
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (callback != null) {
                callback.onProgressUpdate(Integer.parseInt(progress[0]), fileCount, fileList.size());
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (callback != null) {
                callback.onComplete(aBoolean);
            }
        }
    }
}