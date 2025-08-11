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
    private val ratings = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioFiles = assets.list("instructions")?.filter { it.endsWith(".wav") }?.sorted() ?: emptyList()

        findViewById<android.widget.Button>(R.id.newButton).setOnClickListener {
            playRandom()
        }

        findViewById<android.widget.Button>(R.id.repeatButton).setOnClickListener {
            playCurrent()
        }

        findViewById<android.widget.Button>(R.id.doneButton).setOnClickListener {
            adjustRating(1)
            playRandom()
        }

        findViewById<android.widget.Button>(R.id.failButton).setOnClickListener {
            adjustRating(-2)
            playRandom()
        }
    }

    private fun playRandom() {
        if (audioFiles.isEmpty()) return
        val totalWeight = audioFiles.sumOf { file ->
            val rating = ratings[file] ?: 0
            (10 - rating).coerceAtLeast(1)
        }
        var r = Random.nextInt(totalWeight)
        var selectedIndex = 0
        for ((index, file) in audioFiles.withIndex()) {
            val weight = (10 - (ratings[file] ?: 0)).coerceAtLeast(1)
            if (r < weight) {
                selectedIndex = index
                break
            }
            r -= weight
        }
        currentIndex = selectedIndex
        playCurrent()
    }

    private fun playCurrent() {
        if (currentIndex !in audioFiles.indices) return
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

    private fun adjustRating(delta: Int) {
        if (currentIndex in audioFiles.indices) {
            val file = audioFiles[currentIndex]
            val adjustedDelta = if (delta < 0) delta * 2 else delta
            ratings[file] = (ratings[file] ?: 0) + adjustedDelta
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
