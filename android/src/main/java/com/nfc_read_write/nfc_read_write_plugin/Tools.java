package com.nfc_read_write.nfc_read_write_plugin;
import android.nfc.tech.MifareClassic;
import android.util.SparseArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Tools {
    private static final String CHARSET = "GB2312";

    //byte 转十六进制
    public static String Bytes2HexStringInverse(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[size - 1 - i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    //byte 转十六进制
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 字节数组反转
     *
     * @param array
     * @return
     */
    public static byte[] byteArrayInverse(byte[] array) {
        byte[] data = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            data[data.length - 1 - i] = array[i];
        }
        return data;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    //十六进制转byte
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }
 
    /* byte[]转Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;

    }

    /* Int转byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    public static byte[] floatToBytes(float data) {
        return ByteBuffer.allocate(4).putFloat(data).array();
    }


    public static String bytesToBits(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            for (int i = 7; i >= 0; i--) {
                if (isBitSet(b, i)) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为无符号字节数组
     *
     * @param array
     * @return
     */
    public static byte[] byteArrayToUnSignByteArray(byte[] array) {
        byte[] res = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0) {
                res[i] = (byte) (array[i] + 255);
            } else {
                res[i] = array[i];
            }
        }
        return res;
    }

    private static Boolean isBitSet(byte b, int bit) {
        return (b & (1 << bit)) != 0;
    }




    /**
     * int转byte1 取int一个字节
     */
    public static byte[] intToByte1(int n) {

//        byte[] b = new byte[4];
//        b[3] = (byte) (n & 0xff);
//        b[2] = (byte) (n >> 8 & 0xff);
//        b[1] = (byte) (n >> 16 & 0xff);
//        b[0] = (byte) (n >> 24 & 0xff);

        byte[] b = new byte[1];

        b[0] = (byte) (n & 0xff);//取int最低位的一字节

        return b;
    }

    /**
     * int转byte2 取int两个字节
     */
    public static byte[] intToByte2(int n){

        byte[] b = new byte[2];

        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);

        return b;
    }

    /**
     * int转byte3 取int三个字节
     */
    public static byte[] intToByte3(int n) {

        byte[] b = new byte[3];

        b[2] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[0] = (byte) (n >> 16 & 0xff);

        return b;
    }

    /**
     * int转byte4 取int四个字节
     */
    public static byte[] intToByte4(int n){
        byte[] b=new byte[4];

        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }


    /**
     * 将float转byte[] 如20.0转换为[0, 0, -96, 65]
     */
    public static byte[] floatToByte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }
    public static SparseArray<String> readSector(MifareClassic mifareClassic, Integer sectorIndex,boolean decrypt,byte[]arrKey,byte[]arrIV){
        SparseArray<String> sectorAsHex = new SparseArray< >();
        Integer firstBlock  = mifareClassic.sectorToBlock(sectorIndex);
        Integer lastBlock = firstBlock + 4;
        for (int i = firstBlock; i <lastBlock; i++) {
            try {
            byte [] blockBytes  = mifareClassic.readBlock(i);
                
              String data=  ConvertUtil.byteArrayToString(blockBytes,0,blockBytes.length,CHARSET);
                System.out.println("解密前:::"+data);
                if (decrypt)data=DESKeyIVUtil.decrypt(String.valueOf(data),arrKey,arrIV);

                sectorAsHex.append(i,data);
             } catch ( Exception e) {
            }
        }

        return sectorAsHex;
    }
    ///string  to byte
    public static byte[] String2Bytes(String src) {
        byte[] data14 = new byte[16];

        try {
            byte[] checkMachines = src.getBytes(CHARSET);
            if (checkMachines.length>16){
                System.arraycopy(checkMachines, 0, data14, 0,16);

            }else {
                System.arraycopy(checkMachines, 0, data14, 0, checkMachines.length);

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data14;
    }
}
