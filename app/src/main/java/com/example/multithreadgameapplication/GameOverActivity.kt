package com.example.multithreadgameapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.multithreadgameapplication.databinding.ActivityGameoverBinding

class GameOverActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameoverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}