package com.augment.golden.bulbcontrol;

public interface Changeable {

    public void changePower();

    public void changeBrightness();

    public void changeHue();

    public void changeSaturation();

    public void changeKelvin();

    public void changeState();

    public void setHue(int hue);
    public void setSaturation(int saturation);
    public void setKelvin(int kelvin);
    public void setBrightness(int brightness);
    public void setOn(boolean on);
}
