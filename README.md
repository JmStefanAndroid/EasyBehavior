# EasyBehavior
- 如果你正苦于实现一个酷炫的个人信息页面效果
- 如果产品要求你实现下拉放大背景图，上滑能看到详细信息
- 如果还要求一系列同步动画效果
- 通过Behavior实现它将是你的不二选择，本项目旨在帮助各位轻松实现自己的Behavior！
----------
### 注意：
- demo2已在master分支中移除，需要的话请前往backupv1分支，切换版本到低于26
- androidx适配版本已发布，请拉取androidx分支
----------
## 博文地址
  [例子1 链接](http://blog.csdn.net/gjm15881133824/article/details/73742219)
  [例子2 链接](http://blog.csdn.net/gjm15881133824/article/details/74946322)

----------
## DEMO下载
  https://fir.im/ckh1
  ----------
## 效果图
 ![EasyBehavior](/gif/EasyBehavior.gif) 
 ![CoAliBehavior](/gif/Coali.gif)
----------
## 例子的实现
注意：以下内容可能引起您的轻度不适（xing fen），请慎重阅读，例子中呢，用到了两个Behavior。</p>
 1.用户头像的放大以及缩小，按照上面的方法，我们可以很明白的知道实现步骤了
 

 - 继承

```
public class CircleImageInUsercBehavior extends CoordinatorLayout.Behavior<CircleImageView> {
```
 - 重写onDependentViewChanged，
```
    //当dependency变化的时候调用
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        //初始化一些基础参数
        init(parent, child, dependency);
        //计算比例
         ...
        //设置头像的大小
        ViewCompat.setScaleX(child, percent);
        ViewCompat.setScaleY(child, percent);
        return false;
    }

```
啊？这样就搞定了？是的！就是这么easy！！</br>
![这里写图片描述](http://img.blog.csdn.net/20170627112207279?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ2ptMTU4ODExMzM4MjQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)</br>
**那我有一个问题了，是不是说每一个view想要做跟随动画，都得创建一个相应的Behavior呢？答案很明显是NO~!**</br>
看完下一个例子你就会明白了</br>

----------

 2.这个Behavior用途主要有以下3点：

 - 控制背景图的放大以及回弹 
 - 中间middle部分跟随背景图的放大缩小做相应的移动
 - Toolbar的背景Alpha的改变
 
第一步：初始化参数，通过tag查找每一个View,这里需要注意，我们需要在布局文件中，每个相应的View都需要声明相同的tag 如 `android:tag="你的tag"`，当然，也可以用原始的findViewById，这里只是希望id改动时，我们的Behavior可以不受到影响

```
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
       ...
        if (mToolBar == null) {
            mToolBar = (Toolbar) parent.findViewWithTag(TAG_TOOLBAR);
        }
        ...
        abl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
       ...//实现Toolbar的背景变化
        });
       ...
    }

```
第二步：开始scale动画（下拉上划滑动过程中）

```
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        if (!isRecovering) {//未在回弹动画中，开始我们的变化动画
            if (...) {
                scale(child, target, dy);
                return;
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        if (velocityY > 100) {//当y速度>100,就秒弹回
            isAnimate = false;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
    }
```
第三步：松手的回弹

```
    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        recovery(abl);//回弹，这个方法详细请看源码
        super.onStopNestedScroll(coordinatorLayout, abl, target);
    }
```

ok，步骤就是这样，是不是很easy呢？


----------


附：AppBarLayout的跟随动画，不仅仅是上面的一种方式
我们也可以在逻辑代码中通过原生的Listener来实现

```
mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            //计算进度百分比
                float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
                ...//根据百分比做你想做的
            }
        });
```
## License
--------
```
Copyright (C) 2017 JmStefanAndroid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
