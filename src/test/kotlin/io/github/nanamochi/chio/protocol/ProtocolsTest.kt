package io.github.nanamochi.chio.protocol

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ProtocolsTest {
    @Test
    fun `select returns the exact version when supported`() {
        assertEquals(334, Protocols.select(334).version)
    }

    @Test
    fun `select falls back to the closest lower version`() {
        assertEquals(282, Protocols.select(283).version)
    }

    @Test
    fun `select clamps builds outside the supported range`() {
        assertEquals(282, Protocols.select(1).version)
        assertEquals(425, Protocols.select(999).version)
    }
}
