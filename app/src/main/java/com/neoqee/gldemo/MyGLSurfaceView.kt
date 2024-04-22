package com.neoqee.gldemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

//    private val renderer: MyGLRenderer
    private val renderer: BitmapRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

//        renderer = MyGLRenderer()
        renderer = BitmapRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

//        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return super.onTouchEvent(e)
//        val x: Float = e.x
//        val y: Float = e.y
//
//        when (e.action) {
//            MotionEvent.ACTION_MOVE -> {
//
//                var dx: Float = x - previousX
//                var dy: Float = y - previousY
//
//                // reverse direction of rotation above the mid-line
//                if (y > height / 2) {
//                    dx *= -1
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < width / 2) {
//                    dy *= -1
//                }
//
//                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
//                requestRender()
//            }
//        }
//
//        previousX = x
//        previousY = y
//        return true
    }

}