package io.github.nanamochi.chio.protocol.versions

import io.github.nanamochi.chio.protocol.Protocol
import io.github.nanamochi.chio.protocol.derive

/** b1800 deprecates the usage of gzip compression inside packets. */
val B1800: Protocol =
    B1797.derive(1800) {
        disableCompression = true
    }
