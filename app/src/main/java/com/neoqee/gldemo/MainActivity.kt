package com.neoqee.gldemo

import android.hardware.Camera
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * 用于解决不同长宽比视频的适配问题，让GL绘制尽量处于填充满容器的状态
     */
    private lateinit var contentWrapper: AspectFrameLayout
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: L11_1_CameraRenderer
    private var isFront = true
    private val camera = CameraApi14()
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentWrapper = findViewById(R.id.cameraWrapper)
        glSurfaceView = findViewById(R.id.activity_camera_surfaceView)

        glSurfaceView.setEGLContextClientVersion(2)
        renderer = L11_1_CameraRenderer(this, camera)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        glSurfaceView.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onStop() {
        super.onStop()
        camera.close()
    }

    private fun openCamera() {
        camera.open(if (isFront) Camera.CameraInfo.CAMERA_FACING_FRONT else Camera.CameraInfo.CAMERA_FACING_BACK)
        camera.preview()
        glSurfaceView.requestRender()
    }

    override fun onClick(p0: View?) {

    }

    private fun checkPermission() {

    }
}