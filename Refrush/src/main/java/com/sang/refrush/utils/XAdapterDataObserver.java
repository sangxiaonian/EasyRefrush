package com.sang.refrush.utils;


import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ${PING} on 2019/7/26.
 */
public class XAdapterDataObserver extends RecyclerView.AdapterDataObserver {
    @Override
    public void onChanged() {

    }

    public void onItemRangeChanged(int positionStart, int itemCount) {onChanged();}
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {onChanged();}
    public void onItemRangeRemoved(int positionStart, int itemCount) {onChanged();}
    public void onItemRangeInserted(int positionStart, int itemCount) {onChanged();}
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {onChanged();}
}
