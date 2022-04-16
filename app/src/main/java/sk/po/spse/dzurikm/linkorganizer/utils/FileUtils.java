package sk.po.spse.dzurikm.linkorganizer.utils;

import android.util.Log;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;

public class FileUtils {
    /**
     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile
     *            - FileInputStream for the file to copy from.
     * @param toFile
     *            - FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public static void copyFile(InputStream fromFile, OutputStream toFile) {
        String data = "";

        BufferedInputStream inputStream = new BufferedInputStream(fromFile);
        try {
            while (inputStream.available() > 0) {

                // Read the byte and
                // convert the integer to character
                char c = (char)inputStream.read();
                data += c;

            }

            Log.i("data",data);

            try (Writer w = new OutputStreamWriter(toFile, "UTF-8")) {
                w.write(data);
            }
            catch (Exception e) {e.printStackTrace();}
        }

        catch (Exception e) {e.printStackTrace();}

        Log.i("data",data);

    }

}
