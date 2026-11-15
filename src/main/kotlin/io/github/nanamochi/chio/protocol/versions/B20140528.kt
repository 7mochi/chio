package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/**
 * b20140528 allows for 16-player multiplayer matches, if the protocol version is set to 19 or
 * higher.
 */
val B20140528: Protocol =
    B20130801.derive(20140528) {
        protocolVersion = 18
    }
