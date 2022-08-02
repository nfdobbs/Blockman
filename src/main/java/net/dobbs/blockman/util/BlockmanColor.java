package net.dobbs.blockman.util;

import net.minecraft.entity.boss.BossBar;

public class BlockmanColor {
    public int red;
    public int green;
    public int blue;
    public int alpha;

    public BlockmanColor(int redSet, int greenSet, int blueSet, int alphaSet)
    {
        red = redSet;
        green = greenSet;
        blue = blueSet;
        alpha = alphaSet;
    }

    public float getRedFloat()
    {
        return (float) red/255F;
    }

    public float getGreenFloat()
    {
        return (float) green/255F;
    }

    public float getBlueFloat()
    {
        return (float) blue/255F;
    }

    public float getAlphaFloat()
    {
        return (float) alpha/255F;
    }
}
