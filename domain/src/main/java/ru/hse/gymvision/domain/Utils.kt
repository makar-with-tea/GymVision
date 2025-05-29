package ru.hse.gymvision.domain

enum class CameraMovement { // tilt
    UP, DOWN, STOP
}

enum class CameraRotation { // pan
    LEFT, RIGHT, STOP
}

enum class CameraZoom { // zoom
    IN, OUT, STOP
}
