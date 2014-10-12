package com.young.util;

/**
 * Author: landerlyoung
 * Date:   2014-10-11
 * Time:   23:56
 * Life with passion. Code with creativity!
 */
public class Sizeof {
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int INT = 4;
    public static final int LONG = 8;
    public static final int FLOAT = 4;
    public static final int DOUBLE = 8;

    public static final int OBJECT = -1;

    public static int sizeof(Object o) {
        if (o instanceof byte[]) {
            return BYTE * ((byte[]) o).length;
        } else if (o instanceof short[]) {
            return SHORT * ((short[]) o).length;
        } else if (o instanceof int[]) {
            return INT * ((int[]) o).length;
        } else if (o instanceof long[]) {
            return LONG * ((long[]) o).length;
        } else if (o instanceof float[]) {
            return FLOAT * ((float[]) o).length;
        } else if (o instanceof double[]) {
            return DOUBLE * ((double[]) o).length;
        } else {
            return OBJECT;
        }
    }

    public static int sizeof(byte b) {
        return BYTE;
    }

    public static int sizeof(short i) {
        return SHORT;

    }

    public static int sizeof(int i) {
        return INT;
    }

    public static int sizeof(long i) {
        return LONG;
    }

    public static int sizeof(float i) {
        return FLOAT;

    }

    public static int sizeof(double i) {
        return DOUBLE;
    }
}
