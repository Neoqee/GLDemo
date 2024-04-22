package com.neoqee.gldemo.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

object GLUtils {

    private const val TAG = "GLUtils"

    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    fun compileShader(type: Int, shaderCode: String): Int {
        // 1. 创建一个新的着色器对象
        val shaderObjectId = GLES20.glCreateShader(type)

        // 2. 判断创建状态
        if (shaderObjectId == 0) {
            // 在OpenGL中，都是通过整型值去作为OpenGL对象的引用。之后进行操作的时候都是将这个整型值传回给OpenGL进行操作。
            // 返回值0代表着创建对象失败。
            Log.w(TAG, "Could not create new shader.")
            return 0
        }

        // 3. 将着色器代码上传到着色器对象中
        GLES20.glShaderSource(shaderObjectId, shaderCode)
        // 4. 编译着色器对象
        GLES20.glCompileShader(shaderObjectId)

        // 5. 获取编译状态：OpenGL将想要获取的值放入长度为1的数组的首位
        val compileStatus = IntArray(1){0}
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        // 打印编译的着色器信息
        Log.i(TAG, "Compiling source:" + "\n" + shaderCode + "\nResult: "
                + GLES20.glGetShaderInfoLog(shaderObjectId))

        // 6. 验证编译状态
        if (compileStatus[0] == 0) {
            // 如果编译失败，则删除创建的着色器对象
            GLES20.glDeleteShader(shaderObjectId)
            Log.w(TAG, "Compilation of shader failed.")

            // 7. 返回着色器对象：失败，为0
            return 0
        }

        // 7. 返回着色器对象：成功，非0
        return shaderObjectId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = GLES20.glCreateProgram()

        if (programObjectId == 0) {
            return 0
        }

        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)

        GLES20.glLinkProgram(programObjectId)

        val linkStatus = IntArray(1) {0}
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            GLES20.glDeleteShader(programObjectId)

            return 0
        }

        return programObjectId
    }

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjIds = intArrayOf(0)
        GLES20.glGenTextures(1, textureObjIds, 0)

        if (textureObjIds[0] == 0) {
            Log.w(TAG, "glGenTextures failure!")
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureObjIds, 0)
            Log.w(TAG, "decodeResource failure!")
            return 0
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        Log.i(TAG, "bitmap width = ${bitmap.width}, height = ${bitmap.height}")
        bitmap.recycle()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureObjIds[0]
    }

}