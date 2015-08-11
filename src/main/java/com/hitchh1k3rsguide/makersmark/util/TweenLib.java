package com.hitchh1k3rsguide.makersmark.util;

public class TweenLib
{

    public static double linear(double a, double b, double t)
    {
        if (t < 0.0)
        {
            t = 0.0;
        }
        if (t > 1.0)
        {
            t = 1.0;
        }
        return a + ((b - a) * t);
    }

    public static double quadInOut(double a, double b, double t)
    {
        t *= 2;
        if (t < 1)
        {
            return (b - a) / 2 * t * t + a;
        }
        else
        {
            --t;
            return (a - b) / 2 * (t * (t - 2) - 1) + a;
        }
    }
}
