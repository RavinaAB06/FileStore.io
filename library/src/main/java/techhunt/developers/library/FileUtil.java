package techhunt.developers.library;

import android.webkit.MimeTypeMap;

import java.io.File;

public class FileUtil {

    public static String getFileType(File file) {

        if (file.isDirectory())
            return "DIRECTORY";

        String mime = getMimeType(file);

        if (mime == null)
            return "MISC_FILE";

        if (mime.startsWith("audio"))
            return "AUDIO";

        if (mime.startsWith("application/vnd.android.package-archive"))
            return "APK";

        if (mime.startsWith("image"))
            return "IMAGE";

        if (mime.startsWith("video"))
            return "VIDEO";

        if (mime.startsWith("application/ogg"))
            return "AUDIO";

        if (mime.startsWith("application/msword"))
            return "DOC";

        if (mime.startsWith("application/vnd.ms-word"))
            return "DOC";

        if (mime.startsWith("application/vnd.ms-powerpoint"))
            return "PPT";

        if (mime.startsWith("application/vnd.ms-excel"))
            return "XLS";

        if (mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
            return "DOC";

        if (mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
            return "PPT";

        if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
            return "XLS";

        if (mime.startsWith("application/pdf"))
            return "PDF";

        if (mime.startsWith("text"))
            return "TXT";

        if (mime.startsWith("application/zip"))
            return "ZIP";

        if (mime.startsWith("application/rar"))
            return "RAR";

        if (mime.startsWith("application/json"))
            return "JSON";

        if (mime.startsWith("text/html"))
            return "HTML";

        if (mime.startsWith("image/gif"))
            return "GIF";

        return "MISC_FILE";
    }

    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file.getName()));
    }

    private static String getExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    public enum FileType {
        DIRECTORY, MISC_FILE, AUDIO, APK, IMAGE, VIDEO, DOC, PPT, XLS, PDF, TXT, ZIP, RAR, JSON, HTML, GIF;

        public static FileType getFileType(File file) {

            if (file.isDirectory())
                return FileType.DIRECTORY;

            String mime = getMimeType(file);

            if (mime == null)
                return FileType.MISC_FILE;

            if (mime.startsWith("audio"))
                return FileType.AUDIO;

            if (mime.startsWith("application/vnd.android.package-archive"))
                return FileType.APK;

            if (mime.startsWith("image"))
                return FileType.IMAGE;

            if (mime.startsWith("video"))
                return FileType.VIDEO;

            if (mime.startsWith("application/ogg"))
                return FileType.AUDIO;

            if (mime.startsWith("application/msword"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-word"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-powerpoint"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.ms-excel"))
                return FileType.XLS;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
                return FileType.XLS;

            if (mime.startsWith("application/pdf"))
                return FileType.PDF;

            if (mime.startsWith("text"))
                return FileType.TXT;

            if (mime.startsWith("application/zip"))
                return FileType.ZIP;

            if (mime.startsWith("application/rar"))
                return FileType.RAR;

            if (mime.startsWith("application/json"))
                return FileType.JSON;

            if (mime.startsWith("text/html"))
                return FileType.HTML;

            if (mime.startsWith("image/gif"))
                return FileType.GIF;

            return FileType.MISC_FILE;
        }
    }
}
