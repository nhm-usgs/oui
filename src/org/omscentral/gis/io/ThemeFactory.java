package org.omscentral.gis.io;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedList;
import java.util.Iterator;

import org.omscentral.gis.model.*;
import org.omscentral.gis.util.GISUtililites;

public class ThemeFactory {
    static String ioPackageName = "org.omscentral.gis.io";
    static String themePackageName = "org.omscentral.gis.model";
    static Map<String, String> readers = new HashMap<>(3);
    static Map<String, String> themes = new HashMap<>(3);
    static Map<String, String[]> fileAssoc = new HashMap<>(3);

    static {
        readers.put(".dbf", "DbfFileParser");
        readers.put(".shp", "ShpFileParser");
        readers.put(".dat", "ArcGridReader");

        themes.put(ioPackageName + "." + "DbfFileParser", "VectorTheme");
        themes.put(ioPackageName + "." + "ShpFileParser", "VectorTheme");
        themes.put(ioPackageName + "." + "ArcGridReader", "FPRasterTheme");

        fileAssoc.put(".shp", new String[]{".dbf"});
        fileAssoc.put(".dbf", new String[]{".shp"});
        fileAssoc.put(".dat", new String[]{});
    }

    public static File[] findFiles(String baseDir) throws IOException {
        File directory = new File(baseDir);
        if (!directory.isDirectory()) {
            throw new IOException(baseDir + " is not a valid directory");
        }
        Set keys = readers.keySet();
        Object key = null;
        LinkedList<File> files = new LinkedList<>();
        File[] dir = directory.listFiles();
        Iterator it;
        for (File dir1 : dir) {
            it = readers.keySet().iterator();
            while (it.hasNext()) {
                if (dir1.toString().endsWith((String) it.next())) {
                    files.add(dir1);
                    break;
                }
            }
        }

        File[] retval = new File[files.size()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (File) files.get(i);
        }
        return retval;
    }

    public static File[] findFiles(String baseDir, String themeName) throws IOException {
        File directory = new File(baseDir);
        if (!directory.isDirectory()) {
            throw new IOException(baseDir + " is not a valid directory");
        }
        Set keys = readers.keySet();
        Object key;
        LinkedList<File> files = new LinkedList<>();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            key = i.next();
            File file = new File(baseDir + "/" + themeName + (String) key);
            if (file.exists()) {
                files.add(file);
            }
        }
        File[] retval = new File[files.size()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (File) files.get(i);
        }
        return retval;
    }

    public static File[] findFiles(String baseDir, String themeName, String ext) throws IOException {
        File directory = new File(baseDir);
        if (!directory.isDirectory()) {
            throw new IOException(baseDir + " is not a valid directory");
        }
        LinkedList<File> files = new LinkedList<>();
        File[] children = directory.listFiles();
        String[] exts = (String[]) fileAssoc.get(ext);
        File file = new File(baseDir + "/" + themeName + ext);
        if (file.exists()) {
            files.add(file);
        }
        for (String ext1 : exts) {
            file = new File(baseDir + "/" + themeName + ext1);
            if (file.exists()) {
                files.add(file);
            }
        }
        File[] retval = new File[files.size()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (File) files.get(i);
        }
        return retval;
    }

    public static Theme createTheme(File f) throws IOException {
        String s = f.toString();
        int i = s.lastIndexOf(File.separator);
        return createTheme(s.substring(0, i), s.substring(i + 1));
    }

    public static void writeTheme(Theme t, File f) throws IOException {
        if (t instanceof RasterTheme) {
            if (!f.getAbsolutePath().endsWith(".dat")) {
                f = new File(f.getAbsolutePath() + ".dat");
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            RasterTheme rt = (RasterTheme) t;
            ArcGridWriter.write(f, rt.getRasterModel());
        } else {
            throw new java.lang.IllegalArgumentException(
                    "Only able to write RasterThemes"
            );
        }
    }

    public static Theme createTheme(String baseDir, String themeName) throws IOException {
        int dot;
        File[] files;
        if ((dot = themeName.lastIndexOf(".")) > -1) {
            files = findFiles(baseDir, themeName.substring(0, dot), themeName.substring(dot));
        } else {
            files = findFiles(baseDir, themeName);
        }
        if (files.length == 0) {
            throw new IOException("Could not read file : " + baseDir + themeName);
        }
        Object[] readers = new Object[files.length];
        for (int i = 0; i < readers.length; i++) {
            readers[i] = createReaderForFile(files[i].toString());
        }
        Theme theme = createFromReaders(readers);
        theme.setName(themeName);
        return theme;
    }

    private static Theme createFromReaders(Object[] readers) throws IOException {
        String classToUse = null;
        for (Object reader : readers) {
            Class readerClass = reader.getClass();
            String themeClass = (String) ThemeFactory.themes.get(readerClass.getName());
            themeClass = themePackageName + "." + themeClass;
            if (classToUse == null) {
                classToUse = themeClass;
            }
            if (!classToUse.equals(themeClass)) {
                throw new IOException("Could not find correct readers");
            }
        }
        try {
            Class themeClass = Class.forName(classToUse);
            if (classToUse.equals(VectorTheme.class.getName())) {
                DbfFileParser dfp;
                ShpFileParser sfp;
                if (readers[0] instanceof ShpFileParser) {
                    sfp = (ShpFileParser) readers[0];
                    dfp = (DbfFileParser) readers[1];
                } else {
                    sfp = (ShpFileParser) readers[1];
                    dfp = (DbfFileParser) readers[0];
                }
                return new VectorTheme(sfp.createModel(), dfp.createModel());
            } else if (classToUse.equals(FPRasterTheme.class.getName())) {
                ArcGridReader agr;
                agr = (ArcGridReader) readers[0];
                return new FPRasterTheme(agr.createModel());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Theme createImageTheme(String name, byte[] bytes, double ulx, double uly, double lrx, double lry) {
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(bytes);
        javax.swing.ImageIcon i = new javax.swing.ImageIcon(image);
        double[] ul = {ulx, uly};
        double[] lr = {lrx, lry};
        GISUtililites.lonLatToUTM(ul);
        GISUtililites.lonLatToUTM(lr);
        double w = Math.abs(ul[0] - lr[0]);
        double h = Math.abs(ul[1] - lr[1]);
        System.out.println("width " + w + " height " + h);
        Theme imageTheme = new ImageTheme(name, image, new double[][]{{w / i.getIconWidth(), 0}, {0, -h / i.getIconHeight()}}, new double[]{ul[0], ul[1]});
        return imageTheme;
    }

    public static Object createReaderForFile(String fileName) throws IOException {
        String className = (String) readers.get(getFileExtension(fileName));
        Class<?> readerClass;
        try {
            readerClass = Class.forName(ioPackageName + "." + className);
        } catch (java.lang.ClassNotFoundException e) {
            throw new IOException(
                    "A reader was not found for file ending in "
                    + getFileExtension(fileName));
        }
        try {
            Class[] consClassArgs = {(new String()).getClass()};
            Object[] args = {fileName};
            java.lang.reflect.Constructor cons
                    = readerClass.getConstructor(consClassArgs);
            Object reader = cons.newInstance(args);
            return reader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFileExtension(String fileName) {
        int index;
        if ((index = fileName.lastIndexOf(".")) > -1) {
            return fileName.substring(index);
        } else {
            return "";
        }
    }

    private static String getExtensionless(String fileName) {
        int index;
        if ((index = fileName.lastIndexOf(".")) > -1) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     *
     * @param args
     * @throws java.io.IOException
     */
    static public void main(String[] args) throws IOException {
        // Theme t = ThemeFactory.createTheme("/home/en/jbproject/omsPanel","stream.shp");
        Theme t = ThemeFactory.createTheme(new File("/home/en/jbproject/omsPanel/east-elv.dat"));
        System.out.println(t);
        File[] f = ThemeFactory.findFiles("/home/en/jbproject/omsPanel");
        for (File f1 : f) {
            System.out.println(f1);
        }
        System.out.println(ThemeFactory.createTheme(new File("/home/en/jbproject/omsPanel/stream.dbf")));
    }
}
