package com.hitchh1k3rsguide.makersmark.util;

public class Tracker
{

    Object trackedValue = null;

    public Tracker(Object value)
    {
        trackedValue = value;
    }

    public boolean hasChanged(Object value)
    {
        if (trackedValue == null)
        {
            trackedValue = value;
            return false;
        }
        if (!trackedValue.equals(value))
        {
            trackedValue = value;
            return true;
        }
        return false;
    }

}