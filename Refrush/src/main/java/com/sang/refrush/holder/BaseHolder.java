package com.sang.refrush.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ${PING} on 2017/8/29.
 */

public class BaseHolder<T> extends RecyclerView.ViewHolder {


    public Context mContext;
    /**
     * holder 的根View
     */
    protected View itemView;

    public BaseHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public BaseHolder(Context context,ViewGroup parent,  int layoutID) {
        this(LayoutInflater.from(context).inflate(layoutID, parent, false));
        this.mContext = context;
    }

    public BaseHolder(Context context, int layoutID) {
        this(LayoutInflater.from(context).inflate(layoutID, null, false));
        this.mContext = context;
    }

    /**
     * 获取holer 的根View
     *
     * @return
     */
    public View getItemView() {
        return itemView;
    }

    /**
     * 初始化数据
     *
     * @param itemView 根View
     * @param position 在RecycleView中的位置
     * @param data     当前的数据
     */
    public void initView(View itemView, int position, T data) {

    }
}
