package com.kido.alienclock.clock.theme;

import com.kido.alienclock.clock.enumaration.NumericFormat;

public class NumericTheme {

    private int clockBackground;

    private int valuesFont;
    private int valuesColor;
    private int valuesColorDate;

    private boolean showBorder;
    private int borderColor;

    private int borderRadiusRx;
    private int borderRadiusRy;

    private NumericFormat numericFormat;

    private boolean numericShowSeconds;


    public int getClockBackground() {
        return clockBackground;
    }

    public int getValuesFont() {
        return valuesFont;
    }

    public int getValuesColor() {
        return valuesColor;
    }
    public int getValuesColorDate() {
        return valuesColorDate;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public int getBorderColor() {
        return borderColor;
    }


    public int getBorderRadiusRx() {
        return borderRadiusRx;
    }

    public int getBorderRadiusRy() {
        return borderRadiusRy;
    }

    public NumericFormat getNumericFormat() {
        return numericFormat;
    }

    public boolean isNumericShowSeconds() {
        return numericShowSeconds;
    }

    private NumericTheme(NumericThemeBuilder numericThemeBuilder) {


        this.clockBackground = numericThemeBuilder.clockBackground;

        this.valuesFont = numericThemeBuilder.valuesFont;
        this.valuesColor = numericThemeBuilder.valuesColor;

        this.showBorder = numericThemeBuilder.showBorder;
        this.borderColor = numericThemeBuilder.borderColor;
        this.borderRadiusRx = numericThemeBuilder.borderRadiusRx;
        this.borderRadiusRy = numericThemeBuilder.borderRadiusRy;

        this.numericFormat = numericThemeBuilder.numericFormat;

        this.numericShowSeconds = numericThemeBuilder.numericShowSeconds;
    }

    public static class NumericThemeBuilder {


        private int clockBackground;

        private int valuesFont;
        private int valuesColor;

        private boolean showBorder;
        private int borderColor;
        private int borderRadiusRx;
        private int borderRadiusRy;

        private NumericFormat numericFormat;

        private boolean numericShowSeconds;



        public NumericThemeBuilder setClockBackground(int clockBackground) {
            this.clockBackground = clockBackground;
            return this;
        }

        public NumericThemeBuilder setValuesFont(int valuesFont) {
            this.valuesFont = valuesFont;
            return this;
        }

        public NumericThemeBuilder setValuesColor(int valuesColor) {
            this.valuesColor = valuesColor;
            return this;
        }

        public NumericThemeBuilder setShowBorder(boolean showBorder) {
            this.showBorder = showBorder;
            return this;
        }

        public NumericThemeBuilder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }


        public NumericThemeBuilder setBorderRadius(int borderRadiusRx, int borderRadiusRy) {
            this.borderRadiusRx = borderRadiusRx;
            this.borderRadiusRy = borderRadiusRy;
            return this;
        }

        public NumericThemeBuilder setNumericFormat(NumericFormat numericFormat) {
            this.numericFormat = numericFormat;
            return this;
        }

        public NumericThemeBuilder setNumericShowSeconds(boolean numericShowSeconds) {
            this.numericShowSeconds = numericShowSeconds;
            return this;
        }

        public NumericTheme build() {
            return new NumericTheme(this);
        }
    }
}
