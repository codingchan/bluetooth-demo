package com.example.canoestudent;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static int get_signed_int(byte[] data) {
        int temp = 0;

        // temp = (int) ((data[3] << 24) | (data[2] << 16) | (data[1] << 8) | data[0]);
        String hex =  byteToHexString(data[3]) + byteToHexString(data[2])
                + byteToHexString(data[1]) + byteToHexString(data[0]);
        temp = (int) Long.parseLong(hex, 16);

        return temp;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bArray
     * @return
     */
    public static List<String> bytesToHexStringList(byte[] bArray) {
        List<String> list = new ArrayList<>();
        String s;
        String tmp;
        for (int i = 0; i < bArray.length; i++) {
            s = "";
            tmp = Integer.toHexString(0xFF & bArray[i]);
            if (tmp.length() < 2)
                s = "0";
            s += tmp.toUpperCase();
            list.add(s);
        }
        return list;
    }

    public static String byteToHexString(byte b) {
        String sTemp;
        sTemp = Integer.toHexString(0xFF & b);
        if (sTemp.length() < 2) {
            sTemp = "0" + sTemp.toUpperCase();
        } else {
            sTemp = sTemp.toUpperCase();
        }

        return sTemp;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = 0; i < count; i++) {
            bs[i] = src[begin + i];
        }
        return bs;
    }
}
