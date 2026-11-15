package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b20150915 drops support for 8-player multiplayer matches entirely. */
val B20150915: Protocol =
    B20150826.derive(20150915) {
        protocolVersion = 19
        slotSize = 16
    }
