package com.sang.refrush.adapter;

import android.content.Context;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.sang.refrush.holder.BaseHolder;
import com.sang.refrush.holder.PeakHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者： ${PING} on 2017/9/4.
 * 带看记录使用的ViewPager
 */

public abstract class XAdapter<T> extends RecyclerView.Adapter {

    public Context context;
    protected List<T> list;
    protected List<PeakHolder> heads;
    protected List<PeakHolder> foots;
    protected final int HEADTYPE = 100000;
    protected final int FOOTTYPE = 200000;

    public XAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        heads = new ArrayList<>();
        foots = new ArrayList<>();
    }


    public void addHeard(PeakHolder heardHolder) {
        heads.add(heardHolder);
    }

    public void addHeard(int index, PeakHolder heardHolder) {
        heads.add(index, heardHolder);
    }

    public void removeHeard(PeakHolder heardHolder) {
        heads.remove(heardHolder);
    }

    public void removeHeard(int index) {
        heads.remove(index);
    }

    public void addFoot(PeakHolder heardHolder) {
        foots.add(heardHolder);
    }

    public void addFoot(int index, PeakHolder heardHolder) {
        foots.add(index, heardHolder);
    }

    public List<PeakHolder> getFoots() {
        return foots;
    }

    public void removeFoot(int index) {
        foots.remove(index);
    }

    public void removeFoot(PeakHolder heardHolder) {

        if (foots.contains(heardHolder)) {
            foots.remove(heardHolder);
            notifyDataSetChanged();
        }
    }
    public void notifyItem(int position){
        position += heads.size();
        notifyItemChanged(position);
    }

    public void notifyItemAdd(int position) {
        position += heads.size();
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public void notifyItemChangedWithOutHead(int position) {
        position += heads.size();
        notifyItemChanged(position);
    }

    public void notifyItemDeleted(int position) {
        position += heads.size();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }


    @Override
    public int getItemViewType(int position) {
        if (position < heads.size()) {
            return position + HEADTYPE;
        } else if (position >= heads.size() + getItemSize()) {
            return FOOTTYPE + position - heads.size() - getItemSize();
        } else {
            position -= heads.size();
        }

        return getViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType >= HEADTYPE && viewType < FOOTTYPE) {
            return heads.get(viewType - HEADTYPE);
        } else if (viewType >= FOOTTYPE) {
            return foots.get(viewType - FOOTTYPE);

        } else {
            return initHolder(parent, viewType);
        }
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if (viewType >= FOOTTYPE) {//脚布局
            PeakHolder holder1 = (PeakHolder) holder;
            holder1.initView(viewType - HEADTYPE);
        } else if (viewType >= HEADTYPE) {//头布局
            PeakHolder holder1 = (PeakHolder) holder;
            holder1.initView(viewType - FOOTTYPE);
        } else {//一般布局
            position -= heads.size();
            BaseHolder holder1 = (BaseHolder) holder;
            holder1.initView(holder1.getItemView(), position, getItemData(position));
        }


    }


    @Override
    public int getItemCount() {
        return getItemSize() +
                heads.size() +
                foots.size();
    }

    /**
     * 初始化ViewHolder,{@link XAdapter#onCreateViewHolder(ViewGroup, int)}处,用于在非头布局\脚布局\刷新时候
     * 调用
     *
     * @param parent   父View,即为RecycleView
     * @param viewType holder类型,在{@link XAdapter#getItemViewType(int)}处使用
     * @return BaseHolder或者其父类
     */
    public abstract BaseHolder<T> initHolder(ViewGroup parent, final int viewType);

    /**
     * 初始化XAdapter 的viewType,且此处已经经过处理,去除Header等的影响,可以直接从0开始使用
     *
     * @param position 当前item的Position(从0开始)
     * @return
     */
    public int getViewType(int position) {
        return 0;
    }

    /**
     * 获取出去头布局和脚布局的item的数量
     * @return
     */
    public int getItemSize(){
        return list==null?0:list.size();
    }


    public T getItemData(int position) {
        return list==null?null:list.get(position);
    }


    public List<PeakHolder> getHeads() {
        return heads;
    }
}
