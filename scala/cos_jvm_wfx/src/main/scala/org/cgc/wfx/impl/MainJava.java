package org.cgc.wfx.impl;

public class MainJava {


    private static int S_IFDIR = 0040000;
    private static int S_IFLNK = 0120000;

    public static void main(String[] args){
        System.out.println("S_IFDIR: " +String.valueOf(S_IFDIR) );
        System.out.println("S_IFLNK: " +String.valueOf(S_IFLNK) );
    }
}
