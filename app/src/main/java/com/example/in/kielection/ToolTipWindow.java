package com.example.in.kielection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.PopupWindow;

import com.example.in.kielection.Common.Utils;

/**
 * Created by Localadmin on 2/28/2017.
 */
public class ToolTipWindow extends PopupWindow implements PopupWindow.OnDismissListener {

    private long closeDelayTime;
    private Handler mHandler;
    Runnable closeRunnable;
    private OnDismissListener mOnDismissListener;
    Context context;

    public ToolTipWindow(Context context) {
        super(context);
        super.setOnDismissListener(this);
    }

    public ToolTipWindow(Context context, long closeDelayTime) {
        this(context);
        this.context=context;
        this.closeDelayTime = closeDelayTime;
        onShow();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }


    //override all show or update method of super class that you use tho call onshow
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        onShow();
        super.showAtLocation(parent, gravity, x, y);
    }


    public void onShow() {
        schedule();
    }

    private void schedule() {
        if (closeDelayTime > 0) {
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.removeCallbacks(closeRunnable);
            mHandler.postDelayed(closeRunnable, closeDelayTime);
        }
    }


    @Override
    public void onDismiss() {
        if (mHandler != null) {
            mHandler.removeCallbacks(closeRunnable);
        }
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }

        closeRunnable = new Runnable() {

            @Override
            public void run() {
                dismiss();
                Activity activity = (Activity) context;

            }
        };

    }
}