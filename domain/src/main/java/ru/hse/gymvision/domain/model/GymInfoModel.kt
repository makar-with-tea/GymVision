package ru.hse.gymvision.domain.model

data class GymInfoModel(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val image: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GymInfoModel
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}
