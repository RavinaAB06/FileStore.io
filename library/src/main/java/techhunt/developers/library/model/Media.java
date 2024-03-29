package techhunt.developers.library.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import techhunt.developers.library.utils.CursorHandler;
import techhunt.developers.library.utils.ArrayUtils;
import techhunt.developers.library.utils.CursorHandler;
import techhunt.developers.library.utils.MimeTypeUtils;
import techhunt.developers.library.utils.StringUtils;

public class Media implements CursorHandler, Parcelable {

    private static final String[] sProjection = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Images.Media.ORIENTATION
    };

    private static final int CURSOR_POS_DATA = ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.DATA);
    private static final int CURSOR_POS_DATE_TAKEN = ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.DATE_TAKEN);
    private static final int CURSOR_POS_MIME_TYPE = ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.MIME_TYPE);
    private static final int CURSOR_POS_SIZE = ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.SIZE);
    private static final int CURSOR_POS_DURATION = ArrayUtils.getIndex(sProjection, MediaStore.Video.Media.DURATION);
    private static final int CURSOR_POS_ORIENTATION = ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.ORIENTATION);

    private String path = null;
    private long dateModified = -1;
    private String mimeType = MimeTypeUtils.UNKNOWN_MIME_TYPE;
    private int orientation = 0;

    private String uriString = null;

    private long size = -1;
    private boolean selected = false;
    private String duration = "";

    public Media() {
    }

    public Media(String path, long dateModified) {
        this.path = path;
        this.dateModified = dateModified;
//        this.duration = getDuration();
        this.mimeType = MimeTypeUtils.getMimeType(path);
    }

    public Media(File file) {
        this(file.getPath(), file.lastModified());
        this.size = file.length();
//        this.duration = getDuration();
        this.mimeType = MimeTypeUtils.getMimeType(path);
    }

    public Media(String path) {
        this(path, -1);
    }

    public Media(Uri mediaUri) {
        this.uriString = mediaUri.toString();
        this.path = null;
        this.mimeType = MimeTypeUtils.getMimeType(uriString);
    }

    public Media(@NotNull Cursor cur) {
        this.path = cur.getString(CURSOR_POS_DATA);
        this.dateModified = cur.getLong(CURSOR_POS_DATE_TAKEN);
        this.mimeType = cur.getString(CURSOR_POS_MIME_TYPE);
        this.size = cur.getLong(CURSOR_POS_SIZE);
        this.duration = cur.getString(CURSOR_POS_DURATION);
        this.orientation = cur.getInt(CURSOR_POS_ORIENTATION);
    }

    @Override
    public Media handle(Cursor cu) {
        return new Media(cu);
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

    public boolean isGif() {
        return mimeType.endsWith("gif");
    }

    public boolean isImage() {
        return mimeType.startsWith("image");
    }

    public boolean isVideo() {
        return mimeType.startsWith("video");
    }

    public Uri getUri() {
        return uriString != null ? Uri.parse(uriString) : Uri.fromFile(new File(path));
    }

    public String getDisplayPath() {
        return path != null ? path : getUri().getEncodedPath();
    }

    public String getName() {
        return StringUtils.getPhotoNameByPath(path);
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public String getVideoDuration() {
        return convertMillieToHMmSs(Long.parseLong(duration));
    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

    public Long getDateModified() {
        return dateModified;
    }

//    public ObjectKey getSignature() {
//        return new ObjectKey(getDateModified() + getPath() + getOrientation());
//    }

    public int getOrientation() {
        return orientation;
    }

    @Deprecated
    public Bitmap getBitmap() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        return bitmap;
    }

    @Deprecated
    public boolean setOrientation(final int orientation) {
        this.orientation = orientation;
        // TODO: 28/08/16  find a better way
        // TODO update also content provider
        new Thread(new Runnable() {
            public void run() {
                int exifOrientation = -1;
                try {
                    ExifInterface exif = new ExifInterface(path);
                    switch (orientation) {
                        case 90:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                            break;
                        case 180:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        case 270:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_270;
                            break;
                        case 0:
                            exifOrientation = ExifInterface.ORIENTATION_NORMAL;
                            break;
                    }
                    if (exifOrientation != -1) {
                        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(exifOrientation));
                        exif.saveAttributes();
                    }
                } catch (IOException ignored) {
                }
            }
        }).start();
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Media)
            return getPath().equals(((Media) obj).getPath());

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
        dest.writeInt(this.orientation);
        dest.writeString(this.uriString);
        dest.writeLong(this.size);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.dateModified = in.readLong();
        this.mimeType = in.readString();
        this.orientation = in.readInt();
        this.uriString = in.readString();
        this.size = in.readLong();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}