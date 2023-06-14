package com.mse.datafabric.utils;

import java.awt.*;

public class MacNoSleep {

    public static void main(String[] args) throws AWTException {
        while (true) {
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int x = (int) b.getX();
            int y = (int) b.getY();
            Robot r = new Robot();
            r.mouseMove(x,y);
            r.delay(500);
        }
    }
}