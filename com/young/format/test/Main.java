package com.young.format.test;

import com.young.format.WavHeaderInfo;

import java.io.Closeable;
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
            in = new FileInputStream(new File("sample.wav"));
            int amount = in.read(wavHeader);
            WavHeaderInfo headerInfo = WavHeaderInfo.parseHeader(wavHeader, 0, amount);
            System.out.println(headerInfo);
            System.out.println("Sample Rate:" + headerInfo.mFmtChunk.SampleRate);
            System.out.println("Bits Per Sample:" + headerInfo.mFmtChunk.BitsPerSample);
            System.out.println("Bit Rate:" + headerInfo.getBitRate());
            System.out.println("Duration:" + headerInfo.getDuration());
            System.out.println("Data Size:" + headerInfo.getDataSize());
            System.out.println("File Size:" + headerInfo.getFileSize());
            System.out.println("Header Size:" + headerInfo.getHeaderSize());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(in);
        }
    }

    private static boolean closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }
}
