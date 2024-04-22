package com.neoqee.gldemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var mPoint: Point
    private lateinit var mShaper: Shaper
    private lateinit var mPolygonShaper: PolygonShaper
    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square2

    @Volatile
    var angle: Float = 0f

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
//        Matrix.setLookAtM(vPMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // 设置黑色背景
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)


        mPoint = Point()
        mShaper = Shaper()
        mPolygonShaper = PolygonShaper()
        mTriangle = Triangle()
        mSquare = Square2()
    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    private val rotationMatrix = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        // 重绘背景
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

//        mPoint.draw()
        mPolygonShaper.draw(vPMatrix)
    }

    private fun drawTriangle() {
        val scratch = FloatArray(16)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Create a rotation transformation for the triangle
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        mTriangle.draw(scratch)
    }
}