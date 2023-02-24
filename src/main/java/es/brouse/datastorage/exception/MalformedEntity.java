package es.brouse.datastorage.exception;

public class MalformedEntity extends Throwable {
    public MalformedEntity() {
        super();
    }

    public MalformedEntity(String message) {
        super(message);
    }

    public MalformedEntity(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedEntity(Throwable cause) {
        super(cause);
    }

    protected MalformedEntity(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
