package techhunt.developers.library;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ErrorCause {

    private String title;
    private ArrayList<String> causes;

    public ErrorCause(String title, ArrayList<String> causes) {
        this.title = title;
        this.causes = causes;
    }

    public ErrorCause(String title) {
        this.title = title;
        this.causes = new ArrayList<>(1);
    }

    public void addCause(String cause) {
        this.causes.add(cause);
    }

    public String getTitle() {
        return title;
    }

    public boolean hasErrors() {
        return causes.size() > 0;
    }

    public @Nullable
    ErrorCause get() {
        if (hasErrors()) return this;
        else return null;
    }

    public ArrayList<String> getCauses() {
        return causes;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(title).append("\n");

        for (String cause : causes) {
            b.append(cause).append("\n");
        }

        return b.toString();
    }

    public static ErrorCause fromThrowable(Throwable throwable) {
        if (throwable instanceof Exception)
            return ErrorCause.fromThrowable(throwable.getCause());
        else return new ErrorCause(throwable.getMessage());
    }
}
