#RxBlur

![](./images/icon.png)

##@Deprecated

**简 介**
-----------------
在Android设备上,实现一个毛玻璃（高斯模糊）效果总是让人很棘手，不仅要考虑视觉效果，还要在性能和代码实现的复杂度上做出权衡。
这里归纳和总结了几种实现方案，并且用RxJava进行封装，方便以流的形式处理图片的加载和操作。

**项目结构**
-----------------

- **MainActivity：** RxJava + RxBinding + [RxViewStub](https://github.com/SmartDengg/RxViewStub) + BestBlur

- **PicassoBlurActivity：** RxJava + Picasso + Transformation + RenderScript

- **GlideBlurActivity：** RxJava + Glide + Transformation + RenderScript

- **RemoteBlurActivity：** RxJava + Retrofit + BestBlur

- **FastBlurActivity：** RxJava + Picasso + [FastBlur](http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html)

- **PaintBlurActivity：** RxJava + Picasso + PaintBlur

- **RxAnimatorBlurActivity：** RxJava + Picasso + Animator(TypeEvaluator)

**总结**
-----------------

运行项目，然后对比高斯效果，这里计算了每种模糊算法的所耗时长，方便您的选择，虽然BestBlur
在代码的复杂度上很难令人接受，但是在性能表现和模糊效果上还是令人满意的，所以，综合考虑，它是个不错的选择，而且，还可以设置模糊灰度。

如果您对RxJava更感兴趣，可以按照我的实现思路，扩展自己需要的“RxBinding”。

如果，您发现了它的不当之处或者更好地实现思路与方案，请联系我，谢谢。

**关于开发者**
-----------------

- 小鄧子 - Hi4Joker@gmail.com

[小鄧子的简书](http://www.jianshu.com/users/df40282480b4/latest_articles)
 
[小鄧子的慕课网专题](http://www.imooc.com/myclub/article/uid/2536335)

<a href="http://weibo.com/5367097592/profile?rightmod=1&wvr=6&mod=personinfo">
  <img alt="Follow me on Weibo" src="http://upload-images.jianshu.io/upload_images/268450-50e41e15ac29b776.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240" />
</a>

**参考自**
-----------------

- [RxJava](https://github.com/ReactiveX/RxJava) - ReactiveX

- [RxBinding](https://github.com/JakeWharton/RxBinding) - JakeWharton

- [StackBlurDemo](http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html) - Yahel Bouaziz
