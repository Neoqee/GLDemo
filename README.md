# GLDemo

参考网上资料，学习使用的opengl demo工程

1. 参考资源
    - https://zhuanlan.zhihu.com/p/56031071
    - https://github.com/ChenLittlePing/LearningVideo
    - https://developer.android.com/develop/ui/views/graphics/opengl/draw?hl=zh-cn
    
## 自我总结

1. 在Android中使用OpenGL，默认使用的是OpenGL ES
2. Android提供了GLSurfaceView类，以此为基础简化使用
3. 真正实现渲染的是GLSurfaceView的一个内部接口GLSurfaceView.Renderer
4. Renderer基本使用
   - onSurfaceCreated 在这个方法中初始化资源
   - onSurfaceChanged 
   - onDrawFrame 在这个方法中渲染
5. OpenGL的其他基本属性
   - 顶点坐标 一组float数组表示，平面则2个为一组，三维的则3个为一组，这些数据定义一个将要绘制的形状范围
   - 顶点着色程序 用于渲染形状的顶点的 OpenGL ES 图形代码
   - Fragment着色器 用于使用颜色或纹理渲染形状面的 OpenGL ES 代码
   - 程序 OpenGL 着色语言 (GLSL) 编写的代码，类色于C语言，使用前需要编译、链接
   - 应用投影与相机矩阵 使用矩阵对预览进行适配转换，让画面看起来自然一点
   - 纹理坐标 我的理解是把要渲染的目标是一个平面，比如图片，此时把这个图片按照Android的View坐标系去建立，单位为1，然后和顶点坐标形成一个映射
   - 纹理对象 就是要渲染的图片或者视频帧，按照OpenGl定义的方式去处理，然后绑定
6. 绘制纹理时，需要2组坐标，一个是顶点坐标，一个是纹理坐标，以绘制图片来尝试理解：
   - 顶点坐标就表示这个图片接下来显示的大小，以4个点确认一个矩形，然后图片将被显示在这个矩形里
   - 纹理坐标，根据Android的显示方式，以图片的左上角建立坐标系，以百分比的方式表示图片的宽高，也以4个点确定一个矩形，将这部分截取然后显示在之前通过顶点坐标定义的矩形里，绘制的顺序取决于4个点的顺序
7. 渲染camera或者视频时，纹理绑定的类型为GL_TEXTURE_EXTERNAL_OES，图片是GL_TEXTURE_2D