package com.neoqee.gldemo.video

import android.app.Activity
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.neoqee.gldemo.R
import com.neoqee.gldemo.utils.FileUtil
import com.neoqee.gldemo.utils.TimeUtil

class VideoActivity : AppCompatActivity(), View.OnClickListener, VideoPlayer.PlayerListener, SeekBar.OnSeekBarChangeListener {
    private lateinit var glSurfaceView: GLSurfaceView
    /**
     * 用于解决不同长宽比视频的适配问题，让GL绘制尽量处于填充满容器的状态
     */
    private lateinit var contentWrapper: AspectFrameLayout

    private val REQUEST_PICK_VIDEO = 1124

    private lateinit var player: VideoPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var textProgress: TextView
    private lateinit var textDuration: TextView
    private lateinit var videoInfo: VideoInfo
    private lateinit var renderer: VideoRenderer

    /**
     * 是否处于拉拽进度条的状态
     */
    private var isTrackingTouching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        findViewById<Button>(R.id.pickVideoBtn).setOnClickListener {
            pickVideo()
        }

        contentWrapper = findViewById(R.id.activity_video_content_wrapper)
        glSurfaceView = findViewById(R.id.activity_video_surfaceView)

        seekBar = findViewById(R.id.activity_video_seekbar)
        seekBar.setOnSeekBarChangeListener(this)
        textProgress = findViewById(R.id.activity_video_text_progress)
        textDuration = findViewById(R.id.activity_video_text_duration)
        player = VideoPlayer()
        player.setListener(this)
        glSurfaceView.setEGLContextClientVersion(2)
        renderer = VideoRenderer(this, player)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        glSurfaceView.setOnClickListener(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_VIDEO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleImage(data)
            } else {
                Toast.makeText(this,"pick failure!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleImage(intent: Intent) {
        val clipData = intent.clipData
        if (clipData == null) {
            val uri = intent.data
            if (uri != null && !TextUtils.isEmpty(uri.path)) {
                val path: String?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    path = FileUtil.getPath(this.applicationContext, uri)
                } else {
                    path = uri.path
                }
                onVideoPicked(path)
                // /storage/emulated/0/DCIM/Camera/VID_20240422_191243.mp4
                Log.i("Neoqee", "pick video path = $path")
                return
            }
            return
        }
        for (i in 0 until clipData.itemCount) {
            val contentURI = clipData.getItemAt(i).uri
            val result: String?
            val cursor = contentResolver.query(contentURI, null, null, null, null)
            if (cursor == null) {
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
                cursor.close()
            }
            if (result != null) {
                Log.i("Neoqee", "pick video result = $result")
                onVideoPicked(result)
            }
        }
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        startActivityForResult(intent, REQUEST_PICK_VIDEO)
    }

    private fun onVideoPicked(path: String?) {
        if (path == null) {
            return
        }
        videoInfo = VideoInfo(path)
        renderer.videoInfo = videoInfo
        player.setDataSource(path)
        contentWrapper.setAspectRatio(1.0 * videoInfo.displayWidth / videoInfo.displayHeight)
        player.prepare()
        player.isLooping = true
        player.setOnPreparedListener {
            player.start()
            textDuration.text = TimeUtil.formatVideoTime(player.duration.toLong())
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }

    override fun onClick(v: View?) {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
        }
    }

    override fun onPlayerStart() {
    }

    override fun onPlayerStop() {
    }

    override fun onPlayerPause() {
    }

    override fun onPlayerUpdate(percent: Float) {
        if (!isTrackingTouching) {
            seekBar.progress = (percent * 100).toInt()
        }
        textProgress.text = TimeUtil.formatVideoTime(player.currentPosition.toLong())
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isTrackingTouching = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar == null) {
            return
        }
        player.seekTo((seekBar.progress / 100.0f * player.duration).toInt())
        isTrackingTouching = false
    }

}