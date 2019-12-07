package techhunt.developers.library.filter;

import java.io.File;
import java.io.FileFilter;

public class FoldersFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}