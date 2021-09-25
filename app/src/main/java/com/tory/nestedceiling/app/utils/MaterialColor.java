package com.tory.nestedceiling.app.utils;

import com.tory.nestedceiling.app.R;


public enum MaterialColor {
    red(R.color.md_red_500,0xFFF44336),
    pink(R.color.md_pink_500, 0xFFE91E63),
    purple(R.color.md_purple_500, 0xFF9C27B0),
    deepPurple(R.color.md_deep_purple_500, 0xFF673AB7),
    indigo(R.color.md_indigo_500, 0xFF3F51B5),
    blue(R.color.md_blue_500, 0xFF2196F3),
    lightBlue(R.color.md_light_blue_500, 0xFF03A9F4),
    cyan(R.color.md_cyan_500, 0xFF00BCD4),
    teal(R.color.md_teal_500, 0xFF009688),
    green(R.color.md_green_500, 0xFF4CAF50),
    lightGreen(R.color.md_light_green_500, 0xFF8BC34A),
    lime(R.color.md_lime_500, 0xFFCDDC39),
    yellow(R.color.md_yellow_500, 0xFFFFEB3B),
    amber(R.color.md_amber_500, 0xFFFFC107),
    orange(R.color.md_orange_500, 0xFFFF9800),
    deepOrange(R.color.md_deep_orange_500, 0xFFFF5722),
    brown(R.color.md_brown_500, 0xFF795548),
    grey(R.color.md_grey_500, 0xFF9E9E9E),
    blueGrey(R.color.md_blue_grey_500, 0xFF607D8B);

    public static final int size = MaterialColor.values().length;

    public final int color;
    public final int resId;

    MaterialColor(int resId, int color) {
        this.color = color;
        this.resId = resId;
    }


    public static MaterialColor random() {
        return values()[(int)(Math.random() * size)];
    }


}
