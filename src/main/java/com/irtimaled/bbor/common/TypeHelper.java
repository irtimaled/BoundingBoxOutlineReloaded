package com.irtimaled.bbor.common;

public class TypeHelper {
    public static int combineHashCodes(int... hashCodes) {
        final int prime = 31;
        int result = 0;
        for (int hashCode : hashCodes) {
            result = prime * result + hashCode;
        }
        return result;
    }
}
