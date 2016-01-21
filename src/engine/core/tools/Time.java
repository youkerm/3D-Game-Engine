package engine.core.tools;

import org.lwjgl.Sys;

/**
 * Created by Mitch on 1/17/2016.
 */
public class Time {

    public static long getCurrentTime() {
        return Sys.getTime()*1000/ Sys.getTimerResolution();
    }

}
