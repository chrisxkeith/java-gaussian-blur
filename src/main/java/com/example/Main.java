package com.example;

public class Main {
    public static void main(String[] args) {
        Blurrer b = new Blurrer();
        for (float r = 2.0f; r < 8.0; r += 2.0) {
            b.buildBlurKernel(r);
            b.dumpBlurKernel();
            System.out.println("");
        }
    }
}