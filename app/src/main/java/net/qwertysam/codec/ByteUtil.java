package net.qwertysam.codec;


import android.util.Base64;

import net.qwertysam.percentage.PercentageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static byte[] bytesFromFile(String file) {
        File selectedFile = new File(file);
        try {
            InputStream in = new FileInputStream(selectedFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int buffersize = 1024;

            byte[] buffer = new byte[buffersize];

            long totalRead = 0;
            int previousPercentage = 0;

            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);

                totalRead += bytesRead;
                int newPercentage = PercentageUtil.getPercentage(totalRead, selectedFile.length());

                if (previousPercentage < newPercentage) {
                    System.out.println("Percentage Read: " + newPercentage + "%");
                    previousPercentage = newPercentage;
                }
            }

            byte[] bytes = bos.toByteArray();

            in.close();
            bos.close();
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> splitIntoSendables(String toSplit){
        return splitBy(toSplit, 140);
    }

    public static List<String> splitBy(String toSplit, int splitOffset){
        //return toSplit.split("(?<=\\G.{" + splitOffset + "})");
        List<String> toReturn = new ArrayList<String>();

        int index = 0;
        while (index < toSplit.length()){
            toReturn.add(toSplit.substring(index, Math.min(index + splitOffset, toSplit.length())));
            index+=splitOffset;
        }

        return toReturn;
    }

    public static String encodeBytes(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }

    public static byte[] decodeBytes(String base64Num) {
        return Base64.decode(base64Num, Base64.URL_SAFE);
    }
}
