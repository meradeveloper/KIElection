package com.example.in.kielection;

import android.content.Context;

/**
 * Created by Localadmin on 2/28/2017.
 */

public class LoginTimer {

    private LoginTimerCallback timerCallback;
    private Context context;


    public LoginTimer(LoginTimerCallback timerCallback,Context context)
    {
        this.timerCallback=timerCallback;
        this.context=context;

    }

    public void setTimer(String time)
    {
        timerCallback.getTimer(time);
    }
}
