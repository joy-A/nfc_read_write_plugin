package com.nfc_read_write.nfc_read_write_plugin;

import java.io.UnsupportedEncodingException;

/**
 * 转换类 Created by Administrator on 2015/9/9.
 */
public class ConvertUtil {

    public static int BytesToInt(byte[] arrData, int iOffset, int iCount) {
        int iResult = 0;
        if (iCount == 1)
            iResult = arrData[iOffset] & 0x000000FF;
        else if (iCount == 2) {

            iResult = ((arrData[iOffset] & 0x000000FF) << 8) + (arrData[iOffset + 1] & 0x000000FF);

        } else if (iCount == 3) {

            iResult = ((arrData[iOffset] & 0x000000FF) << 16) + ((arrData[iOffset + 1] & 0x000000FF) << 8) + (arrData[iOffset + 2] & 0x000000FF);

        } else if (iCount == 4) {

            iResult = ((arrData[iOffset] & 0x000000FF) << 24) + ((arrData[iOffset + 1] & 0x000000FF) << 16) + ((arrData[iOffset + 2] & 0x000000FF) << 8) + (arrData[iOffset + 3] & 0x000000FF);
        }

        return iResult;
    }
    /**
     * 将整数转换为布尔型
     *
     * @param value 整数
     * @return 证书大于0返回true 否则返回false
     */
    public static boolean convertIntToBoolean(int value) {
        return value > 0 ? true : false;
    }

    /**
     * 将整数转为字节数组
     *
     * @param i 整数
     * @return 字节数组
     * @author Mark
     * @date 2015年8月11日 上午10:02:32
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 将字节数组转为整数 如[0,0,0,31]转为31
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return 整数
     * @author Mark
     * @date 2015年8月11日 上午10:02:50
     */
    public static int byteArrayToInt(byte[] bytes, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 将字节数组转换为整数 如[31，0，0，0]转为31
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return 整数
     * @author Mark
     * @date 2015年8月11日 上午10:03:59
     */
    public static int byteArrayReverseToInt(byte[] bytes, int offset) {
        int value = 0;
        for (int i = 4; i > 0; i--) {
            int shift = (i - 1) * 8;
            value += (bytes[offset + i - 1] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 将字节数组转换为float 如[0, 0, -96, 65]转为20.0
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return 整数
     * @author Mark
     * @date 2015年8月11日 上午10:03:59
     */
    public static float byteArrayReverseToFloat(byte[] bytes, int offset) {
        int l;
        l = bytes[offset + 0];
        l &= 0xff;
        l |= ((long) bytes[offset + 1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[offset + 2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[offset + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * 将float转byte[] 如20.0转换为[0, 0, -96, 65]
     *
     * @param f
     * @return
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

    /**
     * byte数组类型转换为long型
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return
     */
    public static long bytesToLong(byte[] bytes, int offset) {
        return (0xffL & (long) bytes[offset]) | (0xff00L & ((long) bytes[offset + 1] << 8))
                | (0xff0000L & ((long) bytes[offset + 2] << 16)) | (0xff000000L & ((long) bytes[offset + 3] << 24))
                | (0xff00000000L & ((long) bytes[offset + 4] << 32))
                | (0xff0000000000L & ((long) bytes[offset + 5] << 40))
                | (0xff000000000000L & ((long) bytes[offset + 6] << 48))
                | (0xff00000000000000L & ((long) bytes[offset + 7] << 56));
    }

    /**
     * 将long型转换为字节数组
     *
     * @param data long数
     * @return 字节数组
     */
    public static byte[] longToBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    /**
     * 将double转换为字节数组
     *
     * @param data double数
     * @return 字节数组
     */
    public static byte[] doubleToBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return longToBytes(intBits);
    }

    /**
     * 将byte数组转换为double
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return double数值
     */
    public static double getDouble(byte[] bytes, int offset) {
        long l = bytesToLong(bytes, offset);
        return Double.longBitsToDouble(l);
    }

    /**
     * 将字节数组转为String格式
     *
     * @param data      字节数组
     * @param offset    偏移量，从第几个字节开始算起
     * @param byteCount 需要转换的字节的长度
     * @param charset   编码 utf-8 gbk
     * @return
     * @throws UnsupportedEncodingException
     * @author Mark
     * @date 2015年8月29日 下午3:16:49
     */
    public static String byteArrayReverseToString(byte[] data, int offset, int byteCount, String charset)
            throws UnsupportedEncodingException {
        return new String(data, offset, byteCount, charset);
    }

    /**
     * 将字节数组转为String格式
     *
     * @param data      字节数组
     * @param offset    偏移量，从第几个字节开始算起
     * @param byteCount 需要转换的字节的长度
     * @param charset   编码 utf-8 gbk
     * @return
     * @throws UnsupportedEncodingException
     * @author Mark
     * @date 2015年8月29日 下午3:16:49
     */
    public static String byteArrayToString(byte[] data, int offset, int byteCount, String charset) {
        try {
            return new String(data, offset, byteCount, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取终止符（'\0'）的下标即（有效字节的个数）
     *
     * @param data   原始字节数组
     * @param offset 偏移量
     * @return 终止字节所在的位置
     * @author Mark
     * @date 2015年8月29日 下午4:16:21
     */
    public static int getByteCount(byte[] data, int offset, int defaultCount) {
        if (data == null || data.length <= offset) {
            return 0;
        }
        for (int i = offset; i < data.length; i++) {
            if ('\0' == data[i]) {
                return i - offset;
            }
        }
        return defaultCount;
    }

    /**
     * 将字符串转化为true
     *
     * @param str 字符串信息
     * @return 当字符串为空，<=0 为false时为false 其他为true
     */
    public static boolean strToBoolean(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        if ("false".equals(str.toLowerCase())) {
            return false;
        } else if ("true".equals(str.toLowerCase())) {
            return true;
        }
        try {
            int res = Integer.parseInt(str);
            if (res <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将System.Text.Encoding.Unicode.GetBytes("你好123")的字节数组转换为字符串
     *
     * @param buffer
     * @param offerSet
     * @param count
     * @return
     */
    public static String utf16ByteArrayToString(byte[] buffer, int offerSet, int count) {
        byte[] data = new byte[count];
        for (int i = 0; i < count; i += 2) {
            data[i] = buffer[i + offerSet + 1];
            data[i + 1] = buffer[i + offerSet];
        }
        String res;
        try {
            res = new String(data, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            res = "";
        }
        return res.trim();
    }

    /**
     * 字节数组转BCD码
     *
     * @param arrSource
     * @param offset
     * @param len
     * @return
     */
    public static String bytesToBCDString(byte[] arrSource, int offset, int len) {
        String result = "";
        if (arrSource != null && arrSource.length >= (offset + len)) {
            for (int i = offset; i <= offset + len - 1; i++) {
                String strHex = Integer.toHexString(arrSource[i] & 0xFF);
                result += strHex.length() == 1 ? "0" + strHex : strHex;
            }
        }
        return result;
    }

    /**
     * BCD码转换入目标字节数组中
     *
     * @param bcdString   要存入数组的BCD编码字符串
     * @param arrDest     目标数组
     * @param destOffset  目标数组中的起始偏移量
     * @param destByteLen 目标数组中药存入的字节个数
     * @return 存入完成后的目标数组
     */
    public static byte[] bcdStringToBytes(String bcdString, byte[] arrDest, int destOffset, int destByteLen) {
        for (int i = 0; i <= destByteLen - 1; i++) {
            int byt = Integer.valueOf(bcdString.substring(i * 2, i * 2 + 2), 16);
            arrDest[destOffset + i] = (byte) byt;
        }
        return arrDest;
    }    /**
     * BCD码转换入目标字节数组中
     *
     * @param bcdString   要存入数组的BCD编码字符串
     * @param destOffset  目标数组中的起始偏移量
     * @param destByteLen 目标数组中药存入的字节个数
     * @return 存入完成后的目标数组
     */
    public static byte[] bcdStringToBytes(String bcdString, int destOffset, int destByteLen) {
        byte[] arrDest = new byte[destByteLen];
        for (int i = 0; i <= destByteLen - 1; i++) {

            int byt = Integer.valueOf(bcdString.substring(i * 2, i * 2 + 2), 16);
            arrDest[destOffset + i] = (byte) byt;
        }
        return arrDest;
    }
    public static byte[] str2Bcd(String asc,int len) {
         int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
            System.out.format("%02X\n", bbt[p]);
        }
        return bbt;
    }

    public static byte int2Byte(int i) {
        return Byte.parseByte(Integer.toString(i));
    }
}
