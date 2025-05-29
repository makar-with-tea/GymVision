package ru.hse.gymvision.domain.exception

class CameraInUseException: RuntimeException("Camera is already in use by another trainer")