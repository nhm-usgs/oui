package oui.util;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.io.FileInputStream;
import java.io.IOException;

public class OuiClassLoader extends ClassLoader {

    static private OuiClassLoader cl = null;
    
    static public OuiClassLoader factory () {
        if (cl == null) {cl = new OuiClassLoader ();}
        return cl;
    }
    
   private Hashtable<String,Class> classes = new Hashtable<String,Class> ();

   public Class loadClass (String name, boolean resolve) throws ClassNotFoundException {

      Class cl = (Class)classes.get (name);

      if (cl == null) {
         try {
            return findSystemClass (name);
         } catch (ClassNotFoundException e) {
         } catch (NoClassDefFoundError e) {
         }

         byte[] classBytes = loadClassBytes (name);
         if (classBytes == null)
            throw new ClassNotFoundException (name);

         cl = defineClass (name, classBytes, 0, classBytes.length);
         if (cl == null)
            throw new ClassNotFoundException (name);

         classes.put (name, cl);
      }

      if (resolve) resolveClass (cl);
 
      return (cl);
   }

   private byte[] loadClassBytes (String name) {
      String cname = name.replace('.', '/') + ".class";
      FileInputStream in = null;

      try {
         in = new FileInputStream(cname);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int ch;
         while ((ch = in.read()) != -1)
            buffer.write(ch);
         return buffer.toByteArray();
      } catch (IOException e) {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e2) {}
         }
         return null;
      }
   }
}


          
