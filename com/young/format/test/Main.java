package com.young.format.test;

import com.young.format.WavHeaderInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Author: landerlyoung
 * Date:   2014-10-12
 * Time:   17:19
 * Life with passion. Code with creativity!
 */
public class Main {
    public static void main(String[] args) {
        byte[] wavHeader = new byte[1024];
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File("path-to-a-wav-file"));
            int amount = in.read(wavHeader);
            WavHeaderInfo headerInfo = WavHeaderInfo.parseHeader(wavHeader, 0, amount);
            System.out.println(headerInfo);
            System.out.println(headerInfo.getDataSize());
            System.out.println(headerInfo.getFileSize());
            System.out.println(headerInfo.getHeaderSize());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
    }
}
