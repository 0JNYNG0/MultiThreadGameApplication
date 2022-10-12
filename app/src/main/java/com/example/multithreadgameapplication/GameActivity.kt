package com.example.multithreadgameapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.multithreadgameapplication.databinding.ActivityGamescreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class GameActivity: AppCompatActivity() {

    private lateinit var binding: ActivityGamescreenBinding
    private lateinit var gameModel: GameViewModel

    private var isStarted = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamescreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        gameModel = ViewModelProvider(this)[GameViewModel::class.java]

        // onWindowFocusChanged 오버라이드 함수에서 뷰모델의 변수 초기화 작업

        // 플레이어 움직임 관찰 후 x, y 값 위치 변경
        gameModel.playerPosition.observe(this) {
            binding.player.x = it.x
            binding.player.y = it.y
        }


        // 메인 게임 쓰레드 루프?
        lifecycleScope.launchWhenResumed {
            while(true) {

                withContext(Dispatchers.Default) {
                    // update

                    gameModel.onUpdate()
                    delay(1000 / 45)
                }
            }
        }

        val handler = Handler(Looper.getMainLooper())


        // Timer Thread
        Thread {
            var limitMinute = 2
            var limitSeconds = 30

            // 1초씩 감소하면서 00:00초가 되면 게임 종료를 실행해줘야 함
            while(isStarted) {
                val min = if(limitMinute < 10) "0$limitMinute" else limitMinute.toString()
                val sec = if(limitSeconds < 10) "0$limitSeconds" else limitSeconds.toString()

                handler.post {
                    binding.timerText.text = "$min : $sec"
                }
                limitSeconds--

                // 1분 이상일 때, 0초가 지나면 분을 감소시키면서 59초로 변경
                if(limitMinute > 0 && limitSeconds < 0) {
                    limitSeconds = 59
                    limitMinute--
                }

                Thread.sleep(1000)
                // 0분일 때, 0초가 되면 타이머를 멈추고 게임 종료 화면으로 넘기기
                if(limitMinute == 0 && limitSeconds < 0) {
                    isStarted = false
                    val intent = Intent(this, GameOverActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        binding.player.x

        // Game 모델 요소 사이즈 초기화
        gameModel.setSizeConfig(
            binding.gameMap.width.toFloat(),
            binding.gameMap.height.toFloat(),
            binding.floor2.width.toFloat(),
            binding.floor2.height.toFloat(),
            binding.player.width.toFloat(),
            binding.player.height.toFloat(),
            binding.controller.width,
            binding.controller.height,
            binding.controllerBall.width,
            binding.controllerBall.height,
            binding.player.x,
            binding.player.y
        )

        // 컨트롤러 조이스틱 터치 감지하여 해당 위치에 ControllerBall 옮기고 뷰모델에 이벤트 액션, 위치 전달
        binding.controller.setOnTouchListener {_, event ->
            // Log.d("controller", "Touched Controller!")


            if(event.x + binding.controller.x < binding.controller.x && event.y + binding.controller.y < binding.controller.y){
                binding.controllerBall.x = binding.controller.x - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y - binding.controllerBall.width / 2
            }
            else if(event.x + binding.controller.x < binding.controller.x && event.y + binding.controller.y > binding.controller.y + binding.controller.height) {
                binding.controllerBall.x = binding.controller.x - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y + binding.controller.height - binding.controllerBall.width / 2
            }
            else if(event.x + binding.controller.x > binding.controller.x + binding.controller.width && event.y + binding.controller.y < binding.controller.y) {
                binding.controllerBall.x = binding.controller.x + binding.controller.width - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y - binding.controllerBall.width / 2
            }
            else if(event.x + binding.controller.x > binding.controller.x + binding.controller.width && event.y + binding.controller.y > binding.controller.y + binding.controller.height) {
                binding.controllerBall.x = binding.controller.x + binding.controller.width - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y + binding.controller.height - binding.controllerBall.width / 2
            }
            else if(event.x + binding.controller.x < binding.controller.x) {
                binding.controllerBall.x = binding.controller.x - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y + event.y - binding.controllerBall.height / 2
            }
            else if(event.x + binding.controller.x > binding.controller.x + binding.controller.width) {
                binding.controllerBall.x = binding.controller.x + binding.controller.width - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y + event.y - binding.controllerBall.height / 2
            }
            else if(event.y + binding.controller.y < binding.controller.y) {
                binding.controllerBall.y = binding.controller.y - binding.controllerBall.width / 2
                binding.controllerBall.x = binding.controller.x + event.x - binding.controllerBall.width / 2
            }
            else if(event.y + binding.controller.y > binding.controller.y + binding.controller.height) {
                binding.controllerBall.y = binding.controller.y + binding.controller.height - binding.controllerBall.width / 2
                binding.controllerBall.x = binding.controller.x + event.x - binding.controllerBall.width / 2
            }
            else {
                binding.controllerBall.x = binding.controller.x + event.x - binding.controllerBall.width / 2
                binding.controllerBall.y = binding.controller.y + event.y - binding.controllerBall.height / 2
            }


            gameModel.processUserInput(
                event.action,
                event.x,
                event.y
            )

            return@setOnTouchListener true
        }
    }
}