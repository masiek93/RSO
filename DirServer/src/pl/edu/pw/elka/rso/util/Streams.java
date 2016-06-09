package pl.edu.pw.elka.rso.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {


    private static final Logger LOGGER = LoggerFactory.getLogger(Streams.class);

    public static long copy(InputStream in, OutputStream out, long expected)
            throws IOException {



        byte[] buffer = new byte[ 2048 ];
        int count;
        long total = 0;

        while (total < expected && (count = in.read(buffer, 0, expected-total > buffer.length ? buffer.length : (int)(expected-total))) > 0)
        {
            out.write(buffer, 0, count);
            total += count;
        }

        out.flush();



        return total;
    }
}
