package ru.hse.gymvision.domain.model

data class GymSchemeModel(
    val id: Int,
    val name: String,
    var scheme: ByteArray?,
    val clickableTrainerModels: List<ClickableTrainerModel>,
    val clickableCameraModels: List<ClickableCameraModel>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GymSchemeModel
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = clickableTrainerModels.hashCode()
        result = 31 * result + clickableCameraModels.hashCode()
        result = 31 * result + id
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "GymSchemeModel(name=$name, clickableTrainers=$clickableTrainerModels, clickableCameras=$clickableCameraModels)"
    }
}
