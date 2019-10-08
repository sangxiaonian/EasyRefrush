package com.sang.refrush.adapter;

import android.view.ViewGroup;

import com.sang.refrush.holder.BaseHolder;
import com.sang.refrush.holder.PeakHolder;

import java.util.List;


/**
 * 作者： ${PING} on 2019/8/7.
 */
public interface IXadapter<T> {
    void addHeard(PeakHolder heardHolder);

    void addHeard(int index, PeakHolder heardHolder);

    void removeHeard(PeakHolder heardHolder);

    void removeHeard(int index);

    void addFoot(PeakHolder heardHolder);

    void addFoot(int index, PeakHolder heardHolder);

    List<PeakHolder> getFoots();

    void removeFoot(int index);

    void removeFoot(PeakHolder heardHolder);


    void notifyItemAdd(int position);

    void notifyItemDeleted(int position);

    /**
     * 初始化ViewHolder,{@link XAdapter#onCreateViewHolder(ViewGroup, int)}处,用于在非头布局\脚布局\刷新时候
     * 调用
     *
     * @param parent   父View,即为RecycleView
     * @param viewType holder类型,在{@link XAdapter#getItemViewType(int)}处使用
     * @return BaseHolder或者其父类
     */
    BaseHolder<T> initHolder(ViewGroup parent, final int viewType);

    /**
     * 初始化XAdapter 的viewType,且此处已经经过处理,去除Header等的影响,可以直接从0开始使用
     *
     * @param position 当前item的Position(从0开始)
     * @return
     */
    int getViewType(int position);

    /**
     * 获取出去头布局和脚布局的item的数量
     *
     * @return
     */
    int getItemSize();


    T getItemData(int position);


    List<PeakHolder> getHeads();

}
