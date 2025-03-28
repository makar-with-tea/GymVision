package ru.hse.gymvision.domain.model

data class GymSchemeModel(
    var image: ByteArray,
    val clickableTrainers: List<ClickableTrainer>,
    val clickableCameras: List<ClickableCamera>,
    val serverUrl: String
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GymSchemeModel

        if (!image.contentEquals(other.image)) return false
        if (clickableTrainers != other.clickableTrainers) return false
        if (clickableCameras != other.clickableCameras) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clickableTrainers.hashCode()
        result = 31 * result + clickableCameras.hashCode()
        return result
    }

    override fun toString(): String {
        return "GymSchemeModel(clickableTrainers=$clickableTrainers, clickableCameras=$clickableCameras)"
    }
}
