package org.omscentral.gis.io;

import java.util.Hashtable;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class RemoteZipFile {
    final static int MAX_MISSES = 10;
    
    public static InputStream getEntryData(URL zipFileURL, String entry) throws Exception{
        int missCount = 0;
        int pos = 0;
        int bytesRead = 0;
        ZipEntry ze;

        try (ZipInputStream zi = new ZipInputStream(zipFileURL.openStream())) {
            while ((ze = zi.getNextEntry()) != null) {
                if (ze.getName().equals(entry)) {
                    System.out.println("Found: " + ze.getName());
                    int bytesToRead = (int) ze.getSize();
                    byte data[] = new byte[bytesToRead];
                    while (bytesToRead > 0  &&  bytesRead != -1) {
                        bytesRead = zi.read(data, pos, bytesToRead);
                        if (bytesRead == 0) missCount++; else missCount=0;
                        if (missCount == MAX_MISSES) return null;
                        pos += bytesRead;
                        bytesToRead -=  bytesRead;
                    }
                    zi.close();
                    return (bytesToRead == 0) ? new ByteArrayInputStream (data)
                            : null;
                }
                zi.closeEntry();
            }
        }
        return null;
    }

    public static Hashtable getEntryData(URL zipFileURL) throws IOException {

        ZipEntry ze;

        Hashtable<String,ByteArrayInputStream> table = new Hashtable<>();
        try (ZipInputStream zi = new ZipInputStream(zipFileURL.openStream())) {
            while ((ze = zi.getNextEntry()) != null) {
                int bytesToRead = (int) ze.getSize();
                byte data[] = new byte[bytesToRead];
                int bytesRead = 0;
                int missCount = 0;
                int pos = 0;
                while (bytesToRead > 0  &&  bytesRead != -1) {
                    bytesRead = zi.read(data, pos, bytesToRead);
                    if (bytesRead == 0) missCount++; else missCount=0;
                    if (missCount == MAX_MISSES) return null;
                    pos += bytesRead;
                    bytesToRead -=  bytesRead;
                }
                table.put(ze.getName(), new ByteArrayInputStream (data));
                zi.closeEntry();
            }
        }
        return table;
    }

    public static void main(String arg[]) throws Exception {

         DataInputStream d = new DataInputStream(getEntryData(new URL("file:/C:/shapes/mbuluzi.zip"), "mbuluzi.dbf"));
System.err.println(d);
                         d = new DataInputStream(getEntryData(new URL("file:/C:/shapes/mbuluzi.zip"), "mbuluzi.shp2"));
System.err.println(d);
    }
}
