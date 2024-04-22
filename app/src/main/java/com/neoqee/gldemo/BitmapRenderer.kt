package com.neoqee.gldemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.neoqee.gldemo.utils.GLUtils
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BitmapRenderer(val context: Context): GLSurfaceView.Renderer {

    private val VERTEX_SHADER = """uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec2 a_TexCoord;
varying vec2 v_TexCoord;
void main()
{
    v_TexCoord = a_TexCoord;
    gl_Position = u_Matrix * a_Position;
}"""

    private val FRAGMENT_SHADER = """precision mediump float;
varying vec2 v_TexCoord;
uniform sampler2D u_TextureUnit;
void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);
}"""

    private val POSITION_COMPONENT_COUNT = 2
    private val TEX_VERTEX_COMPONENT_COUNT = 2

    /**
     * 顶点坐标
     */
    private val POINT_DATA = floatArrayOf(
        -0.5f, -0.5f,
        -0.5f, 0.5f,
        0.5f, 0.5f,
        0.5f, -0.5f
    )

    /**
     * 纹理坐标
     */
    private val TEX_VERTEX = floatArrayOf(
        0f, 0.5f,
        0f, 0f,
        0.5f, 0f,
        0.5f, 0.5f
    )

    private var mTextureId: Int = 0
    private var mProgram: Int = 0

    private val mVertexData: FloatBuffer

//    private var uTextureUnitLocation: Int = 0
    private val mTexVertexBuffer: FloatBuffer

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    init {
        mVertexData = BufferUtil.createFloatBuffer(POINT_DATA)
        mTexVertexBuffer = BufferUtil.createFloatBuffer(TEX_VERTEX)
        mVertexData.position(0)
        mTexVertexBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        // 开启纹理透明混合，这样才能绘制透明图片
        GLES20.glEnable(GL10.GL_BLEND)
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)

        mTextureId = GLUtils.loadTexture(context, R.drawable.pikachu)
        val vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER)
        val fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER)
        mProgram = GLUtils.linkProgram(vertexShaderId, fragmentShaderId)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }

        Log.i("Neoqee"," onSurfaceChanged -> $width, $height, ratio = $ratio")

        if (width > height) {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(
                projectionMatrix,
                0,
                -ratio,
                ratio,
                -1f,
                1f,
                3f,
                7f)
        }
        else {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(
                projectionMatrix,
                0,
                -1f,
                1f,
                -ratio,
                ratio,
                3f,
                7f)
        }

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.frustumM(
//            projectionMatrix,
//            0,
//            -ratio,
//            ratio,
//            -1f,
//            1f,
//            3f,
//            7f)
    }

    override fun onDrawFrame(gl: GL10) {
        // 重绘背景
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


        GLES20.glUseProgram(mProgram)
//        val aPositionLocation = getAttrib("a_Position")
//        mProjectionMatrixHelper = ProjectionMatrixHelper(program, "u_Matrix")
        // 纹理坐标索引
//        val aTexCoordLocation = getAttrib("a_TexCoord")
//        uTextureUnitLocation = getUniform("u_TextureUnit")

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoord")
        val vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix")
        val textureHandle = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        GLES20.glVertexAttribPointer(positionHandle, POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT, false, 0, mVertexData)
        GLES20.glVertexAttribPointer(texCoordHandle, TEX_VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer)
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, vPMatrix, 0)

        // 设置当前活动的纹理单元为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 将纹理ID绑定到当前活动的纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        // 将纹理单元传递片段着色器的u_TextureUnit
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POINT_DATA.size / POSITION_COMPONENT_COUNT)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}