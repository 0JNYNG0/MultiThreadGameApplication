package com.example.multithreadgameapplication

import android.graphics.PointF
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt



class GameViewModel: ViewModel() {
    private var mapWidth = 0F
    private var mapHeight = 0F
    private var floorWidth = 0F
    private var floorHeight = 0F
    private var playerWidth = 0F
    private var playerHeight = 0F
    private var controllerWidth = 0
    private var controllerHeight = 0
    private var controllerBallWidth = 0
    private var controllerBallHeight = 0

    private var dirX = 0
    private var dirY = 0

    private var playerSpeed = 10
    private var normalizedDiagonal = sqrt(2.0)

    val playerPosition = MutableLiveData(PointF())
    val score = MutableLiveData(0)


    fun setSizeConfig(map_width: Float, map_height: Float, floor_width: Float, floor_height: Float, player_width: Float, player_height: Float, controller_width: Int, controller_height: Int, controllerBall_width: Int, controllerBall_height: Int, posX: Float, posY: Float) {
        mapWidth = map_width
        mapHeight = map_height
        floorWidth = floor_width
        floorHeight = floor_height
        playerWidth = player_width
        playerHeight = player_height
        controllerWidth = controller_width
        controllerHeight = controller_height
        controllerBallWidth = controllerBall_width
        controllerBallHeight = controllerBall_height
        playerPosition.value?.x = posX
        playerPosition.value?.y = posY
    }

    fun processUserInput(action: Int, eventX: Float, eventY: Float) {
        when (action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                dirX = 0
                dirY = 0
            }
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val centerX = controllerWidth / 2F
                val centerY = controllerHeight / 2F

                dirX = getDir(eventX, centerX)
                dirY = getDir(eventY, centerY)
            }
        }
    }

    private fun getDir(pos: Float, std: Float): Int {
        val deadZone = 20
        return when {
            pos > std + deadZone -> 1
            pos > std - deadZone -> 0
            else -> -1
        }
    }

    fun onUpdate() {
        updatePlayerPosition()
    }


    private fun updatePlayerPosition() {
        val maxX = mapWidth - (mapWidth - floorWidth)/2 - floorWidth/9 - playerWidth
        val maxY = mapHeight - (mapHeight - floorHeight * 4)/2 - floorHeight - playerHeight
        playerPosition.value?.let { newPosition ->
            val speed: Float = if (dirX != 0 && dirY != 0) {
                (playerSpeed / normalizedDiagonal).toFloat()
            } else {
                playerSpeed.toFloat()
            }

            newPosition.x = newPosition.x + (speed * dirX)
            newPosition.y = newPosition.y + (speed * dirY)

            when {
                newPosition.x < (mapWidth - floorWidth)/2 && newPosition.y < (mapHeight - floorHeight * 4)/2 + floorHeight -> {
                    newPosition.x = (mapWidth - floorWidth)/2
                    newPosition.y = (mapHeight - floorHeight * 4)/2 + floorHeight
                }
                newPosition.x < (mapWidth - floorWidth)/2 && newPosition.y > maxY -> {
                    newPosition.x = (mapWidth - floorWidth)/2
                    newPosition.y = maxY
                }
                newPosition.x > maxX && newPosition.y < (mapHeight - floorHeight * 4)/2 + floorHeight -> {
                    newPosition.x = maxX
                    newPosition.y = (mapHeight - floorHeight * 4)/2 + floorHeight
                }
                newPosition.x > maxX && newPosition.y > maxY -> {
                    newPosition.x = maxX
                    newPosition.y = maxY
                }
                newPosition.x < (mapWidth - floorWidth)/2 -> newPosition.x = (mapWidth - floorWidth)/2
                newPosition.x > maxX -> newPosition.x = maxX
                newPosition.y < (mapHeight - floorHeight * 4)/2 + floorHeight -> newPosition.y = (mapHeight - floorHeight * 4)/2 + floorHeight
                newPosition.y > maxY -> newPosition.y = maxY
            }

            playerPosition.postValue(newPosition)
        }
    }

}