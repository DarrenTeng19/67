package com.example._7.inventory;

public enum Rotation {
    DEGREE_0,
    DEGREE_90,
    DEGREE_180,
    DEGREE_270;

    public Rotation nextClockwise() {
        return switch(this) {
            case DEGREE_0 -> DEGREE_90;
            case DEGREE_90 -> DEGREE_180;
            case DEGREE_180 -> DEGREE_270;
            case DEGREE_270 -> DEGREE_0;
        };
    }
}
