package io.github.nanamochi.chio.packet.types

@JvmInline
value class SlotStatus(val bits: Int) {
    operator fun plus(other: SlotStatus): SlotStatus = SlotStatus(bits or other.bits)

    operator fun contains(other: SlotStatus): Boolean =
        other.bits == 0 || (bits and other.bits) == other.bits

    fun hasPlayer(): Boolean = (bits and HasPlayer.bits) != 0

    companion object {
        val Open = SlotStatus(1 shl 0)
        val Locked = SlotStatus(1 shl 1)
        val NotReady = SlotStatus(1 shl 2)
        val Ready = SlotStatus(1 shl 3)
        val NoMap = SlotStatus(1 shl 4)
        val Playing = SlotStatus(1 shl 5)
        val Complete = SlotStatus(1 shl 6)
        val Quit = SlotStatus(1 shl 7)

        val HasPlayer = NotReady + Ready + NoMap + Playing + Complete
    }
}
