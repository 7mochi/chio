package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.protocol.versions.B282
import io.github.nanamochi.chio.protocol.versions.B291
import io.github.nanamochi.chio.protocol.versions.B294
import io.github.nanamochi.chio.protocol.versions.B296
import io.github.nanamochi.chio.protocol.versions.B298
import io.github.nanamochi.chio.protocol.versions.B312
import io.github.nanamochi.chio.protocol.versions.B320
import io.github.nanamochi.chio.protocol.versions.B323
import io.github.nanamochi.chio.protocol.versions.B334
import io.github.nanamochi.chio.protocol.versions.B338
import io.github.nanamochi.chio.protocol.versions.B340
import io.github.nanamochi.chio.protocol.versions.B342
import io.github.nanamochi.chio.protocol.versions.B349
import io.github.nanamochi.chio.protocol.versions.B354
import java.util.NavigableMap
import java.util.TreeMap

/** Maps client build numbers to protocol descriptions. */
object Protocols {
    private val clients: NavigableMap<Int, Protocol> =
        TreeMap<Int, Protocol>().apply {
            put(282, B282)
            put(291, B291)
            put(294, B294)
            put(296, B296)
            put(298, B298)
            put(312, B312)
            put(320, B320)
            put(323, B323)
            put(334, B334)
            put(338, B338)
            put(340, B340)
            put(342, B342)
            put(349, B349)
            put(354, B354)
        }

    @JvmStatic
    val latest: Protocol
        get() = clients.lastEntry().value

    @JvmStatic
    val initial: Protocol
        get() = clients.firstEntry().value

    @JvmStatic
    val versions: Set<Int>
        get() = clients.keys

    /**
     * Returns the exact version when supported, otherwise the closest lower one. Builds newer than
     * the newest supported version get the newest protocol. Older builds get the oldest.
     */
    @JvmStatic
    fun select(version: Int): Protocol {
        clients[version]?.let {
            return it
        }

        val floor = clients.floorEntry(version)?.value
        return floor ?: clients.firstEntry().value
    }
}
