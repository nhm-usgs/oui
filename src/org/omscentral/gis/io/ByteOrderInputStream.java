package org.omscentral.gis.io;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

public class ByteOrderInputStream extends DataInputStream {
    int val[];

    public ByteOrderInputStream(InputStream inputStream) {
        super(inputStream);
        val = new int[4];
    }

    public int readBigEndInt() throws IOException {
        return readInt();
    }

    public byte[] readBigEndUnsignedByteArray(int count) throws IOException {
        byte[] b = new byte[count];
        readFully(b);
        return b;
    }

    public int readBigEndUnsignedShort() throws IOException {
        return readUnsignedShort();
    }

    public double readBigEndDouble() throws IOException {
        return readDouble();
    }

    private final byte[] buffer4 = new byte[4];

    public int readLittleEndInt() throws IOException {
        readFully(buffer4);
        return ((((int) buffer4[0] & 255))
                | (((int) buffer4[1] & 255) << 8)
                | (((int) buffer4[2] & 255) << 16)
                | (((int) buffer4[3] & 255) << 24));
    }

    public int[] readLittleEndInt(int count) throws IOException {
        byte[] buffer = new byte[count * 4];
        readFully(buffer);

        int[] intBuffer = new int[count];
        int k = 0;
        for (int i = 0; i < count; i++, k += 4) {
            intBuffer[i]
                    = ((((int) buffer[k + 0] & 255))
                    | (((int) buffer[k + 1] & 255) << 8)
                    | (((int) buffer[k + 2] & 255) << 16)
                    | (((int) buffer[k + 3] & 255) << 24));
        }

        return intBuffer;
    }

    public int readLittleEndUnsignedShort() throws IOException {
        val[0] = readUnsignedByte();
        val[1] = readUnsignedByte();
        return (val[1] << 8) | val[0];
    }

    private final byte[] buffer8 = new byte[8];

    /**
     *
     * @return @throws java.io.IOException
     * @see
     */
    public double readLittleEndDouble() throws IOException {
        readFully(buffer8);
        return Double.longBitsToDouble((((long) buffer8[0] & 255))
                | (((long) buffer8[1] & 255) << 8)
                | (((long) buffer8[2] & 255) << 16)
                | (((long) buffer8[3] & 255) << 24)
                | (((long) buffer8[4] & 255) << 32)
                | (((long) buffer8[5] & 255) << 40)
                | (((long) buffer8[6] & 255) << 48)
                | (((long) buffer8[7] & 255) << 56));
    }

    public double[] readLittleEndDouble(int count) throws IOException {
        byte[] buffer = new byte[count * 8];
        readFully(buffer);

        double[] doubleBuffer = new double[count];
        int k = 0;
        for (int i = 0; i < count; i++, k += 8) {
            doubleBuffer[i] = Double.longBitsToDouble((((long) buffer[k + 0] & 255))
                    | (((long) buffer[k + 1] & 255) << 8)
                    | (((long) buffer[k + 2] & 255) << 16)
                    | (((long) buffer[k + 3] & 255) << 24)
                    | (((long) buffer[k + 4] & 255) << 32)
                    | (((long) buffer[k + 5] & 255) << 40)
                    | (((long) buffer[k + 6] & 255) << 48)
                    | (((long) buffer[k + 7] & 255) << 56));
        }
        return doubleBuffer;
    }
}
