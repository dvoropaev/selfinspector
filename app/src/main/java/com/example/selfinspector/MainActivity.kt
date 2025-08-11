package com.example.selfinspector

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioFiles: List<String>
    private var currentIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioFiles = assets.list("instructions")?.filter { it.endsWith(".wav") }?.sorted() ?: emptyList()

        findViewById<android.widget.Button>(R.id.newButton).setOnClickListener {
            playRandom()
        }

        findViewById<android.widget.Button>(R.id.repeatButton).setOnClickListener {
            playNext()
        }
    }

    private fun playRandom() {
        if (audioFiles.isEmpty()) return
        currentIndex = Random.nextInt(audioFiles.size)
        playCurrent()
    }

    private fun playNext() {
        if (audioFiles.isEmpty()) return
        currentIndex = (currentIndex + 1) % audioFiles.size
        playCurrent()
    }

    private fun playCurrent() {
        try {
            mediaPlayer?.release()
            val fileName = audioFiles[currentIndex]
            val afd = assets.openFd("instructions/$fileName")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
