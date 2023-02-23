package es.brouse.datastorage.exception;

import java.io.File;

/**
 * This code has been inspired from the project <a href="https://github.com/jramoyo/indexed-file-reader">indexed-file-reader</a>
 */
public class FileIndexingException extends RuntimeException {
    public FileIndexingException(File file) {
        super("An exception happened while indexing " + file.getName());
    }

    public FileIndexingException(File file, Throwable cause) {
        super("An exception happened while indexing " + file.getName(), cause);
    }
}
