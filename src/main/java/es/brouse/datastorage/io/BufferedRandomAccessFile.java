package es.brouse.datastorage.io;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class BufferedRandomAccessFile extends RandomAccessFile {
    public BufferedRandomAccessFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }
}
