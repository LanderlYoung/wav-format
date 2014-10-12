package com.young.format;

/**
 * Author: taylorcyang
 * Date:   2014-10-12
 * Time:   下午8:23
 * Life with passion. Code with creativity!
 */
public class FormatNotSupportException extends Exception {
    public FormatNotSupportException() {
        this(null);
    }

    public FormatNotSupportException(String info) {
        super(info);
    }
}
