 
  NestedScrolling 是Andorid 5.0推出的一个嵌套滑动机制，主要是利用 NestedScrollingParent 和 NestedScrollingChild 让父View和子View在滚动时互相协调配合，极大的方便了我们对于嵌套滑动的处理。通过 NestedScrolling 我们可以很简单的实现类似知乎首页，QQ空间首页等非常漂亮的交互效果。
  
  但是有一个问题，对于fling的传递，NestedScrolling的处理并不友好，child只是简单粗暴的将fling结果抛给parent。对于fling，要么child处理，要么parent处理。当我们想要先由child处理一部分，剩余的再交个parent来处理的时候，就显得比较乏力了； 
  老规矩，直接上图：


 在Andorid 8.0 ，推出了一个升级版本 NestedScrollingParent2 和 NestedScrollingChild2  ，友好的处理了fling的分配问题，可以实现非常丝滑柔顺的滑动效果，直接看图：

![](https://user-gold-cdn.xitu.io/2019/10/10/16db38ed192d0aa0?w=375&h=750&f=gif&s=280425)
在这个版本中，列表在消耗fling之后滑动到第一个item之后，将剩余的fling交个parent来处理，滑动出顶部的图片，整个流程非常流程，没有任何卡顿；接下来本文将详细的剖析一下NestedScrollingParent2 和 NestedScrollingChild2 的工作原理；

## 正文
 NestedScrollingParent 和 NestedScrollingChild 已经有很多的教程，大家可以自行学习，本片文章主要对 NestedScrollingParent2 和 NestedScrollingChild2 进行分析；
 
 ### 1、先了解API
  * NestedScrollingParent2
 ```
public interface NestedScrollingParent2 extends NestedScrollingParent {
        /**
     * 即将开始嵌套滑动，此时嵌套滑动尚未开始，由子控件的 startNestedScroll 方法调用
     *
     * @param child  嵌套滑动对应的父类的子类(因为嵌套滑动对于的父控件不一定是一级就能找到的，可能挑了两级父控件的父控件，child的辈分>=target)
     * @param target 具体嵌套滑动的那个子类
     * @param axes   嵌套滑动支持的滚动方向
     * @param type   嵌套滑动的类型，有两种ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     * @return true 表示此父类开始接受嵌套滑动，只有true时候，才会执行下面的 onNestedScrollAccepted 等操作
     */
    boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ScrollAxis int axes,
            @NestedScrollType int type);

    /**
     * 当onStartNestedScroll返回为true时，也就是父控件接受嵌套滑动时，该方法才会调用
     *
     * @param child
     * @param target
     * @param axes
     * @param type
     */
    void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ScrollAxis int axes,
            @NestedScrollType int type);

    /**
     * 在子控件开始滑动之前，会先调用父控件的此方法，由父控件先消耗一部分滑动距离，并且将消耗的距离存在consumed中，传递给子控件
     * 在嵌套滑动的子View未滑动之前
     * ，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,
            @NestedScrollType int type);
            
    /**
     * 在 onNestedPreScroll 中，父控件消耗一部分距离之后，剩余的再次给子控件，
     * 子控件消耗之后，如果还有剩余，则把剩余的再次还给父控件
     *
     * @param target       具体嵌套滑动的那个子类
     * @param dxConsumed   水平方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dyConsumed   垂直方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dxUnconsumed 水平方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param dyUnconsumed 垂直方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, @NestedScrollType int type);

     /**
     * 停止滑动
     *
     * @param target
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
  void onStopNestedScroll(@NonNull View target, @NestedScrollType int type);
 
 
}
 ```
 * NestedScrollingParent2
 
 ```
 public interface NestedScrollingChild2 extends NestedScrollingChild {

    /**
     * 开始滑动前调用，在惯性滑动和触摸滑动前都会进行调用，此方法一般在 onInterceptTouchEvent或者onTouch中，通知父类方法开始滑动
     * 会调用父类方法的 onStartNestedScroll onNestedScrollAccepted 两个方法
     *
     * @param axes 滑动方向
     * @param type 开始滑动的类型 the type of input which cause this scroll event
     * @return 有父视图并且开始滑动，则返回true 实际上就是看parent的 onStartNestedScroll 方法
     */
    boolean startNestedScroll(@ScrollAxis int axes, @NestedScrollType int type);

   /**
     * 子控件停止滑动，例如手指抬起，惯性滑动结束
     *
     * @param type 停止滑动的类型 TYPE_TOUCH，TYPE_NON_TOUCH
     */
    void stopNestedScroll(@NestedScrollType int type);

     /**
     * 判断是否有父View 支持嵌套滑动
     */
    boolean hasNestedScrollingParent(@NestedScrollType int type);

  /**
     * 在dispatchNestedPreScroll 之后进行调用
     * 当滑动的距离父控件消耗后，父控件将剩余的距离再次交个子控件，
     * 子控件再次消耗部分距离后，又继续将剩余的距离分发给父控件,由父控件判断是否消耗剩下的距离。
     * 如果四个消耗的距离都是0，则表示没有神可以消耗的了，会直接返回false，否则会调用父控件的
     * onNestedScroll 方法，父控件继续消耗剩余的距离
     * 会调用父控件的
     *
     * @param dxConsumed     水平方向嵌套滑动的子控件滑动的距离(消耗的距离)    dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dyConsumed     垂直方向嵌套滑动的子控件滑动的距离(消耗的距离)    dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param dxUnconsumed   水平方向嵌套滑动的子控件未滑动的距离(未消耗的距离)dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dyUnconsumed   垂直方向嵌套滑动的子控件未滑动的距离(未消耗的距离)dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param offsetInWindow 子控件在当前window的偏移量
     * @return 如果返回true, 表示父控件又继续消耗了
     */
    boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow,
            @NestedScrollType int type);

    /**
     * 子控件在开始滑动前，通知父控件开始滑动，同时由父控件先消耗滑动时间
     * 在子View的onInterceptTouchEvent或者onTouch中，调用该方法通知父View滑动的距离
     * 最终会调用父view的 onNestedPreScroll 方法
     *
     * @param dx             水平方向嵌套滑动的子控件想要变化的距离 dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dy             垂直方向嵌套滑动的子控件想要变化的距离 dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param consumed       父控件消耗的距离，父控件消耗完成之后，剩余的才会给子控件，子控件需要使用consumed来进行实际滑动距离的处理
     * @param offsetInWindow 子控件在当前window的偏移量
     * @param type           滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     * @return true    表示父控件进行了滑动消耗，需要处理 consumed 的值，false表示父控件不对滑动距离进行消耗，可以不考虑consumed数据的处理，此时consumed中两个数据都应该为0
     */
    boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
            @Nullable int[] offsetInWindow, @NestedScrollType int type);
}
  ```
  
  * 调用流程
  
 上面的API我已经做了很详细的注释，应该不难理解，梳理下拉，大概流程就是：
 
![](https://user-gold-cdn.xitu.io/2019/10/10/16db3a422eaab57d?w=1151&h=490&f=png&s=33346)

一般情况下，事件是从child的触摸事件开始的，

 1. 首先调用child.startNestedScroll()方法，此方法内部通过 NestedScrollingChildHelper 调用并返回parent.onStartNestedScroll()方法的结果，为true，说明parent接受了嵌套滑动，同时调用了parent.onNestedScrollAccepted()方法，此时开始嵌套滑动；
 
2. 在滑动事件中，child通过child.dispatchNestedPreScroll()方法分配滑动的距离，child.dispatchNestedPreScroll()内部会先调用parent.onNestedPreScroll()方法，由parent先处理滑动距离。
3. parent消耗完成之后，再将剩余的距离传递给child，child拿到parent使用完成之后的距离之后，自己再处理剩余的距离。
4. 如果此时子控件还有未处理的距离，则将剩余的距离再次通过 child.dispatchNestedScroll()方法调用parent.onNestedScroll()方法，将剩余的距离交个parent来进行处理
5. 滑动结束之后，调用 child.stopNestedScroll()通知parent滑动结束，至此，触摸滑动结束
6. 触摸滑动结束之后，child会继续进行惯性滑动，惯性滑动可以通过 Scroller 实现，具体滑动可以自己来处理，在fling过程中，和触摸滑动调用流程一样，需要注意type参数的区分，用来通知parent两种不同的滑动流程

至此， NestedScrollingParent2 和 NestedScrollingChild2 的流程和主要方法已经很清晰了；但是没有仅仅看到这里应该还有比较难以理解，毕竟没有代码的API和耍流氓没什么区别，接下来，还是上源码；

 https://juejin.im/post/5d9e8f9451882516d83478af
   
