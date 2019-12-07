package techhunt.developers.library.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;


import org.jetbrains.annotations.NotNull;

import java.io.File;

import techhunt.developers.library.utils.ArrayUtils;
import techhunt.developers.library.utils.CursorHandler;
import techhunt.developers.library.utils.MimeTypeUtils;

public class FilesModel implements CursorHandler, Parcelable {

    private static final String[] sProjection = new String[]{
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE
    };

    private static final int CURSOR_POS_DATA = ArrayUtils.getIndex(sProjection, MediaStore.Files.FileColumns.DATA);
    private static final int CURSOR_POS_DATE_TAKEN = ArrayUtils.getIndex(sProjection, MediaStore.Files.FileColumns.DATE_TAKEN);
    private static final int CURSOR_POS_MIME_TYPE = ArrayUtils.getIndex(sProjection, MediaStore.Files.FileColumns.MIME_TYPE);
    private static final int CURSOR_POS_SIZE = ArrayUtils.getIndex(sProjection, MediaStore.Files.FileColumns.SIZE);

    private String path = null;
    private long dateModified = -1;
    private String mimeType = MimeTypeUtils.UNKNOWN_MIME_TYPE;

    private String uriString = null;

    private long size = -1;
    private Drawable drawable = null;
    private boolean selected = false;

    public FilesModel() {
    }

    public FilesModel(String path, long dateModified) {
        this.path = path;
        this.dateModified = dateModified;
        this.mimeType = MimeTypeUtils.getMimeType(path);
    }

    public FilesModel(File file) {
        this(file.getPath(), file.lastModified());
        this.size = file.length();
        this.mimeType = MimeTypeUtils.getMimeType(path);
    }

    public FilesModel(String path) {
        this(path, -1);
    }

    public FilesModel(Uri mediaUri) {
        this.uriString = mediaUri.toString();
        this.path = null;
        this.mimeType = MimeTypeUtils.getMimeType(uriString);
    }

    public FilesModel(@NotNull Cursor cur) {
        this.path = cur.getString(CURSOR_POS_DATA);
        this.dateModified = cur.getLong(CURSOR_POS_DATE_TAKEN);
        this.mimeType = cur.getString(CURSOR_POS_MIME_TYPE);
        this.size = cur.getLong(CURSOR_POS_SIZE);
    }

    public Drawable getDrawable(){
        return drawable;
    }

    @Override
    public FilesModel handle(Cursor cu) {
        return new FilesModel(cu);
    }

    public static String[] getProjection() {
        return sProjection;
    }

    public void setUri(String uriString) {
        this.uriString = uriString;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    // TODO Calvin: Do not store adapter selection in a Media data class.
    // This does not belong here.
    public boolean isSelected() {
        return selected;
    }

    public boolean setSelected(boolean selected) {
        if (this.selected == selected) return false;
        this.selected = selected;
        return true;
    }

    public boolean toggleSelected() {
        selected = !selected;
        return selected;
    }

    public boolean isImage() {
        return mimeType.startsWith("image");
    }

    public boolean isVideo() {
        return mimeType.startsWith("video");
    }

    public boolean isAudio() {
        return (mimeType.startsWith("audio") || mimeType.startsWith("application/ogg"));
    }

    public boolean isApk() {
        return mimeType.startsWith("application/vnd.android.package-archive");
    }

    public boolean isDoc() {
        return (mimeType.startsWith("application/msword") || mimeType.startsWith("application/vnd.ms-word"));
    }

    public boolean isPpt() {
        return (mimeType.startsWith("application/vnd.ms-powerpoint") || mimeType.startsWith("application/vnd.openxmlformats-officedocument.presentationml"));
    }

    public boolean isZip() {
        return (mimeType.startsWith("application/zip"));
    }

    public boolean isRar() {
        return (mimeType.startsWith("application/rar"));
    }

    public boolean isExcel() {
        return (mimeType.startsWith("application/vnd.ms-excel") || mimeType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"));
    }

    public boolean isPdf() {
        return (mimeType.startsWith("application/pdf"));
    }

    public boolean isTxt() {
        return (mimeType.startsWith("text"));
    }

    public Uri getUri() {
        return uriString != null ? Uri.parse(uriString) : Uri.fromFile(new File(path));
    }

    public String getDisplayPath() {
        return path != null ? path : getUri().getEncodedPath();
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public Long getDateModified() {
        return dateModified;
    }

// TODO remove from here!
    @Deprecated
    public Bitmap getBitmap() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        return bitmap;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilesModel)
            return getPath().equals(((FilesModel) obj).getPath());

        return super.equals(obj);
    }

    @Deprecated
    private long getDateTaken() {
        return 1;
    }

    @Deprecated
    public boolean fixDate() {
        long newDate = getDateTaken();
        if (newDate != -1) {
            File f = new File(path);
            if (f.setLastModified(newDate)) {
                dateModified = newDate;
                return true;
            }
        }
        return false;
    }

    //</editor-fold>

    public File getFile() {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) return file;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.dateModified);
        dest.writeString(this.mimeType);
        dest.writeString(this.uriString);
        dest.writeLong(this.size);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected FilesModel(Parcel in) {
        this.path = in.readString();
        this.dateModified = in.readLong();
        this.mimeType = in.readString();
        this.uriString = in.readString();
        this.size = in.readLong();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<FilesModel> CREATOR = new Creator<FilesModel>() {
        @Override
        public FilesModel createFromParcel(Parcel source) {
            return new FilesModel(source);
        }

        @Override
        public FilesModel[] newArray(int size) {
            return new FilesModel[size];
        }
    };
}