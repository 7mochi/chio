package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.AccountRestrictedPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20140716Test {
    private val protocol = Protocols.select(20140716)

    @Test
    fun `account restricted is empty`() {
        protocol.assertPacket("68000000000000", protocol.encode(AccountRestrictedPacket))
    }
}
