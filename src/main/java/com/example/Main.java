package com.example;

public class Main {
    public static void main(String[] args) {
       Blurrer b = new Blurrer();
       b.buildBlurKernel(6.0f);
       b.dumpBlurKernel();
    }
}