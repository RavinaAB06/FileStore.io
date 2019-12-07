package techhunt.developers.library.utils;

import android.database.Cursor;

public interface CursorHandler<T> {

    T handle(Cursor cu);

    static String[] getProjection() {
        return new String[0];
    }
}