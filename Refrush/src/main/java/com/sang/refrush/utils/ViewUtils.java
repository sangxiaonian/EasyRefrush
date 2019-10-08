package com.sang.refrush.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * 作者： ${PING} on 2018/6/22.
 */

public class ViewUtils {
    /**
     * 判断View是否滑动到顶部
     * @param view
     * @return
     */
    public static boolean isViewReachTopEdge(View view) {
        if (view instanceof ViewGroup) {
            if (view.canScrollVertically(-1))
                return false;
            int count  = ((ViewGroup)view).getChildCount();
            for (int i = 0; i < count; i++) {
                if (!isViewReachTopEdge(((ViewGroup)view).getChildAt(i)))
                    return false;
            }
        }
        if (view.canScrollVertically(-1)) {
            return false;
        }
        return true;
    }

    /**
     * 判断View是否滑动到底部
     * @param view
     * @return
     */
    public static boolean isViewReachBottomEdge(View view) {
        if (view instanceof ViewGroup) {
            if (view.canScrollVertically(1))
                return false;
            int count  = ((ViewGroup)view).getChildCount();
            for (int i = 0; i < count; i++) {
                if (!isViewReachBottomEdge(((ViewGroup)view).getChildAt(i)))
                    return false;
            }
        }
        if (view.canScrollVertically(1)) {
            return false;
        }
        return true;
    }
}
