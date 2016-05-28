package pl.edu.pw.elka.rso.manage.util;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LongIO {

    public static Long readLong(String filePath) throws LongIOException {
        Path path = Paths.get(filePath);
        if(!Files.exists(path)) {
            throw new LongIOException("file not found");
        }
        try {
            return new Long(Files.readAllLines(path).get(0));
        } catch (IOException e) {
            throw new LongIOException("error while reading from file " + e.getMessage(), e);
        }
    }

    public static void writeLong(String filePath, Long numb) throws LongIOException {
        Path path = Paths.get(filePath);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(filePath), true);
            pw.println(numb);
        } catch (IOException e) {
            throw new LongIOException("error while writing to file " + e.getMessage(), e);
        } finally {
            if(pw != null) {
                pw.close();
            }
        }

    }


}
