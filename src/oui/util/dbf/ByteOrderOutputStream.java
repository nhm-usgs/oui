/*
 * $Id: ByteOrderOutputStream.java,v 1.1 1999/08/19 17:20:02 markstro Exp $ 
 * $Author: markstro $
 * $Date: 1999/08/19 17:20:02 $
 *
 */ 

package oui.util.dbf;

import java.io.*;

public class ByteOrderOutputStream extends DataOutputStream {

   public ByteOrderOutputStream(OutputStream outputStream) {    
      super(outputStream); 
   }

   public void skip (int i) throws IOException {
      byte[] buf = new byte[i];
      for (int ii = 0; ii < i; ii++)
         buf[ii] = (byte)0;

      write (buf, 0, i);
   }

   public void writeBigEndInt (int i) throws IOException {
      writeInt (i);            
   }

   public void writeBigEndUnsignedByteArray (byte[] b)  throws IOException {    
      write (b, 0, b.length);
   }

   public void writeBigEndUnsignedShort (int i)  throws IOException {    
      writeShort (i);
   }

   public void writeBigEndDouble (double d) throws IOException {
      writeDouble (d);
   }

   private final byte[] buffer4 = new byte[4];

   public void writeLittleEndInt (int i) throws IOException {
      buffer4[0] = (byte)((i >> 0) & 255);
      buffer4[1] = (byte)((i >> 8) & 255);
      buffer4[2] = (byte)((i >> 16) & 255);
      buffer4[3] = (byte)((i >> 24) & 255);

      write (buffer4, 0, 4);
   }

   public void writeLittleEndInt (int[] i) throws IOException {

      byte[] buffer = new byte[i.length * 4];

      int k = 0;
      for (int j = 0; j < i.length; j++, k += 4) {
         buffer[k + 0] = (byte)((i[j] >> 0) & 255);
         buffer[k + 1] = (byte)((i[j] >> 8) & 255);
         buffer[k + 2] = (byte)((i[j] >> 16) & 255);
         buffer[k + 3] = (byte)((i[j] >> 24) & 255);
      }
      write (buffer, 0, buffer.length);
   }

   public void writeLittleEndUnsignedShort (int i) throws IOException {      
      byte[] val = new byte[2];

      val[0] = (byte)((i >> 0) & 255);
      val[1] = (byte)((i >> 8) & 255);
      write (val, 0, val.length);
   }


   private final byte[] buffer8 = new byte[8];
   public void writeLittleEndDouble (double d) throws IOException {

      long l = Double.doubleToLongBits (d);

      buffer8[0] = (byte)((l >> 0) & 255);
      buffer8[1] = (byte)((l >> 8) & 255);
      buffer8[2] = (byte)((l >> 16) & 255);
      buffer8[3] = (byte)((l >> 24) & 255);
      buffer8[4] = (byte)((l >> 32) & 255);
      buffer8[5] = (byte)((l >> 40) & 255);
      buffer8[6] = (byte)((l >> 48) & 255);
      buffer8[7] = (byte)((l >> 56) & 255);

      write (buffer8, 0, 8);
    }

   public void writeLittleEndDouble (double[] d) throws IOException {

      byte[] buffer = new byte[d.length * 8];

      int k = 0;
      for (int i = 0; i < d.length; i++, k += 8) {

         long l = Double.doubleToLongBits (d[i]);

         buffer[k + 0] = (byte)((l >> 0) & 255);
         buffer[k + 1] = (byte)((l >> 8) & 255);
         buffer[k + 2] = (byte)((l >> 16) & 255);
         buffer[k + 3] = (byte)((l >> 24) & 255);
         buffer[k + 4] = (byte)((l >> 32) & 255);
         buffer[k + 5] = (byte)((l >> 40) & 255);
         buffer[k + 6] = (byte)((l >> 48) & 255);
         buffer[k + 7] = (byte)((l >> 56) & 255);
      }

      write (buffer, 0, buffer.length);
   }
}
