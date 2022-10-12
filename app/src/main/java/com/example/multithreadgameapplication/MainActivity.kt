package com.example.multithreadgameapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.multithreadgameapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }


        val handler = Handler(Looper.getMainLooper())
        var minute = 0
        var second = 0


        Thread() {
            while (true) {
                Thread.sleep(1000)
                handler.post {
                    val minText = if (minute < 10) "0$minute" else "$minute"
                    val secText = if (second < 10) "0$second" else "$second"
                    binding.timer.text = "$minText : $secText"
                }

                second++
                if (second >= 60) {
                    second = 0
                    minute++
                }
            }

        }.start()
    }
}