package io.github.nanamochi.chio.packet.types

@JvmInline
value class Permissions(val bits: Int) {
    operator fun plus(other: Permissions): Permissions = Permissions(bits or other.bits)

    operator fun contains(other: Permissions): Boolean =
        other.bits == 0 || (bits and other.bits) == other.bits

    companion object {
        val NoPermissions = Permissions(0)
        val Regular = Permissions(1 shl 0)
        val BAT = Permissions(1 shl 1)
        val Supporter = Permissions(1 shl 2)
        val Friend = Permissions(1 shl 3)
        val Peppy = Permissions(1 shl 4)
        val Tournament = Permissions(1 shl 5)
    }
}
