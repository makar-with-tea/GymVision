package ru.hse.gymvision.domain.model

data class GymSchemeModel(
    val id: Int,
    val name: String,
    var image: ByteArray?,
    val clickableTrainers: List<ClickableTrainer>,
    val clickableCameras: List<ClickableCamera>,
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GymSchemeModel
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = clickableTrainers.hashCode()
        result = 31 * result + clickableCameras.hashCode()
        result = 31 * result + id
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "GymSchemeModel(name=$name, clickableTrainers=$clickableTrainers, clickableCameras=$clickableCameras)"
    }
}
