package com.example.coffeetrip.use;

public class zoomLevel {
    float returnValue;

    public double kilometer (float zoomLevel) {
        if (zoomLevel < 7) {
           returnValue = 768/2;
        } else if (7 <= zoomLevel && zoomLevel < 8) {
            returnValue = 384/2;
        } else if (8 <= zoomLevel && zoomLevel < 9) {
            returnValue = 192/2;
        } else if (9 <= zoomLevel && zoomLevel < 10) {
            returnValue = 96 / 2;
        } else if (10 <= zoomLevel && zoomLevel < 11) {
            returnValue = 48/2;
        } else if (11 <= zoomLevel && zoomLevel < 12) {
            returnValue = 24/2;
        } else if (12 <= zoomLevel && zoomLevel < 13) {
            returnValue = 12/2;
        } else if (13 <= zoomLevel && zoomLevel < 14) {
            returnValue =  6/2;
        } else if (14 <= zoomLevel && zoomLevel < 15) {
            returnValue = 3/2;
        } else if (15 <= zoomLevel && zoomLevel < 16) {
            returnValue = (float) 1.5/2;
        } else if (16 <= zoomLevel && zoomLevel < 17) {
            returnValue = (float) 0.75/2;
        } else if (17 <= zoomLevel && zoomLevel < 18) {
            returnValue = (float) 0.375/2;
        } else {
            returnValue = 188/2;
        }

        return returnValue;
    }
}
