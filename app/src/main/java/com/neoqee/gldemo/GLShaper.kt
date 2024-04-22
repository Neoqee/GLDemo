package com.neoqee.gldemo

import android.opengl.GLES20
import android.util.Log
import com.neoqee.gldemo.utils.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Point {
    private val vertexShaderCode =
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "  gl_PointSize = 40.0;" +
                "}"

    private val fragmentShaderCode =
                "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int
    private val BYTES_PER_FLOAT = 4
    /**
     * 顶点数据数组
     */
    private val POINT_DATA = floatArrayOf(
        // 点的x,y坐标（x，y各占1个分量）
        0f, 0f)
    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2

    private val mVertexData: FloatBuffer = getVertexBuffer(POINT_DATA)

    val color = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)

    init {
        val vertexShaderId = GLUtils.compileVertexShader(vertexShaderCode)
        val fragmentShaderId = GLUtils.compileFragmentShader(fragmentShaderCode)
        mProgram = GLUtils.linkProgram(vertexShaderId, fragmentShaderId)
    }

    fun draw() {
        GLES20.glUseProgram(mProgram)
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            mVertexData
        )
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun getVertexBuffer(array: FloatArray): FloatBuffer {
        // (number of coordinate values * 4 bytes per float)
        return ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(array)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
    }
}

class Shaper {
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "  gl_PointSize = 30.0;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int
    private val BYTES_PER_FLOAT = 4
    /**
     * 顶点数据数组
     */
    private val POINT_DATA = floatArrayOf(
        // 点的x,y坐标（x，y各占1个分量）
        0f, 0f,
        0f, 0.5f,
        -0.5f, 0f,
        0f, 0f - 0.5f,
        0.5f, 0f - 0.5f,
        0.5f, 0.5f - 0.5f
    )
    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2
    private val DRAW_COUNT = POINT_DATA.size / POSITION_COMPONENT_COUNT

    private val mVertexData: FloatBuffer = getVertexBuffer(POINT_DATA)

    val color = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)

    init {
        val vertexShaderId = GLUtils.compileVertexShader(vertexShaderCode)
        val fragmentShaderId = GLUtils.compileFragmentShader(fragmentShaderCode)
        mProgram = GLUtils.linkProgram(vertexShaderId, fragmentShaderId)
    }

    fun draw() {
        GLES20.glUseProgram(mProgram)
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            mVertexData
        )
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
//        GLES20.glDrawArrays(GLES20.GL_LINES, 0, DRAW_COUNT)
        drawTriangle()
        drawLines()
        drawPoints()

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun drawTriangle() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, DRAW_COUNT)
    }

    private fun drawLines() {
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, DRAW_COUNT)
    }

    private fun drawPoints() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, DRAW_COUNT)
    }

    private fun getVertexBuffer(array: FloatArray): FloatBuffer {
        // (number of coordinate values * 4 bytes per float)
        return ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(array)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
    }
}

class PolygonShaper {
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "  gl_PointSize = 10.0;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int
    private val BYTES_PER_FLOAT = 4
    /**
     * 顶点数据数组
     */
    private val POINT_DATA = floatArrayOf(
        // 点的x,y坐标（x，y各占1个分量）
        0f, 0f,
        0f, 0.5f,
        -0.5f, 0f,
        0f, 0f - 0.5f,
        0.5f, 0f - 0.5f,
        0.5f, 0.5f - 0.5f
    )
    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2
    private val DRAW_COUNT = POINT_DATA.size / POSITION_COMPONENT_COUNT

    private var mVertexData: FloatBuffer? = null
    /**
     * 多边形的顶点数，即边数
     */
    private var mPolygonVertexCount = 3
    /**
     * 绘制所需要的顶点数
     */
    private lateinit var mPointData: FloatArray

    val color = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)

    /**
     * 多边形顶点与中心点的距离
     */
    private val RADIUS = 0.5f
    /**
     * 起始点的弧度
     */
    private val START_POINT_RADIAN = (2 * Math.PI / 4).toFloat()

    init {
        val vertexShaderId = GLUtils.compileVertexShader(vertexShaderCode)
        val fragmentShaderId = GLUtils.compileFragmentShader(fragmentShaderCode)
        mProgram = GLUtils.linkProgram(vertexShaderId, fragmentShaderId)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        val vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

//        GLES20.glVertexAttribPointer(
//            positionHandle,
//            POSITION_COMPONENT_COUNT,
//            GLES20.GL_FLOAT,
//            false,
//            0,
//            mVertexData
//        )
//        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        updateVertexData(positionHandle)
        drawShape(colorHandle)
        drawLine(colorHandle)
        drawPoint(colorHandle)
        updatePolygonVertexCount()

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun updateVertexData(aPositionLocation: Int) {
        // 边数+中心点+闭合点；一个点包含x、y两个向量
        mPointData = FloatArray((mPolygonVertexCount + 2) * 2)

        // 组成多边形的每个三角形的中心点角的弧度
        val radian = (2 * Math.PI / mPolygonVertexCount).toFloat()
        // 中心点
        mPointData[0] = 0f
        mPointData[1] = 0f
        // 多边形的顶点数据
        for (i in 0 until mPolygonVertexCount) {
            mPointData[2 * i + 2] = (RADIUS * Math.cos((radian * i + START_POINT_RADIAN).toDouble())).toFloat()
            mPointData[2 * i + 1 + 2] = (RADIUS * Math.sin((radian * i + START_POINT_RADIAN).toDouble())).toFloat()
        }
        // 闭合点：与多边形的第一个顶点重叠
        mPointData[mPolygonVertexCount * 2 + 2] = (RADIUS * Math.cos(START_POINT_RADIAN.toDouble())).toFloat()
        mPointData[mPolygonVertexCount * 2 + 3] = (RADIUS * Math.sin(START_POINT_RADIAN.toDouble())).toFloat()

        mVertexData = getVertexBuffer(mPointData)
        mVertexData!!.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, 0, mVertexData)
    }


    private fun drawShape(uColorLocation: Int) {
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mPolygonVertexCount + 2)
    }

    private fun drawPoint(uColorLocation: Int) {
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mPolygonVertexCount + 2)
    }

    private fun drawLine(uColorLocation: Int) {
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 1, mPolygonVertexCount)
    }

    /**
     * 更新多边形的边数
     */
    private fun updatePolygonVertexCount() {
        mPolygonVertexCount++
        mPolygonVertexCount = if (mPolygonVertexCount > 32) 3 else mPolygonVertexCount
    }

    private fun getVertexBuffer(array: FloatArray): FloatBuffer {
        // (number of coordinate values * 4 bytes per float)
        return ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(array)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
    }
}

// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {

    private val vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private val fragmentShaderCode =
                "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var mProgram: Int

    init {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
        Log.i("Neoqee", "Triangle mProgram = $mProgram")
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw(mvpMatrix: FloatArray) {
//        Log.i("Neoqee", "Triangle draw")
        GLES20.glUseProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)

            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            GLES20.glDisableVertexAttribArray(it)
        }

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}

// number of coordinates per vertex in this array
//const val COORDS_PER_VERTEX = 3
var squareCoords = floatArrayOf(
    -0.5f,  0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f,  0.5f, 0.0f       // top right
)

class Square2 {

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }
}
