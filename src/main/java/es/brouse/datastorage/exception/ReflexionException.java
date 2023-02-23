package es.brouse.datastorage.exception;

public class ReflexionException extends Throwable {
    public ReflexionException() {
        super();
    }

    public ReflexionException(String message) {
        super(message);
    }

    public ReflexionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflexionException(Throwable cause) {
        super(cause);
    }

    protected ReflexionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
