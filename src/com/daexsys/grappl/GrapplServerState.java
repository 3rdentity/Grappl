package com.daexsys.grappl;

public class GrapplServerState {
    public static long timeStarted = System.currentTimeMillis();

    public static boolean testingMode = false;

    public static void setup() {
        timeStarted = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println("12abcdef".hashCode());
    }
}
