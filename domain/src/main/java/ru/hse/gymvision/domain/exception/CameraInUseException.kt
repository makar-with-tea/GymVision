package ru.hse.gymvision.domain.exception

class CameraInUseException: Exception("Camera is already in use by another trainer")