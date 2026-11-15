package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.packet.outbound.LoginReplyPacket
import io.github.nanamochi.chio.protocol.Protocols
import io.github.nanamochi.chio.protocol.assertPacket
import org.junit.jupiter.api.Test

class B20130801Test {
    private val protocol = Protocols.select(20130801)

    @Test
    fun `inactive login announces the activation message before the invalid login reply`() {
        protocol.assertPacket(
            "180000590000000b57596f7572206163636f756e74206973206e6f7420796574206163746976617465642e20506c6561736520636865636b20796f757220656d61696c20666f722061637469766174696f6e20696e737472756374696f6e7321" +
                "05000004" +
                "000000ffffffff",
            protocol.encode(LoginReplyPacket(-4)),
        )
    }
}
