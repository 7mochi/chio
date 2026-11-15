package io.github.nanamochi.chio.protocol

import io.github.nanamochi.chio.protocol.versions.B1183
import io.github.nanamochi.chio.protocol.versions.B1365
import io.github.nanamochi.chio.protocol.versions.B1600
import io.github.nanamochi.chio.protocol.versions.B1788
import io.github.nanamochi.chio.protocol.versions.B1796
import io.github.nanamochi.chio.protocol.versions.B1797
import io.github.nanamochi.chio.protocol.versions.B1800
import io.github.nanamochi.chio.protocol.versions.B1817
import io.github.nanamochi.chio.protocol.versions.B1820
import io.github.nanamochi.chio.protocol.versions.B20120518
import io.github.nanamochi.chio.protocol.versions.B20120703
import io.github.nanamochi.chio.protocol.versions.B20120723
import io.github.nanamochi.chio.protocol.versions.B20120725
import io.github.nanamochi.chio.protocol.versions.B20120806
import io.github.nanamochi.chio.protocol.versions.B20120818
import io.github.nanamochi.chio.protocol.versions.B20121023
import io.github.nanamochi.chio.protocol.versions.B20121028
import io.github.nanamochi.chio.protocol.versions.B20121203
import io.github.nanamochi.chio.protocol.versions.B20121207
import io.github.nanamochi.chio.protocol.versions.B20121211
import io.github.nanamochi.chio.protocol.versions.B20121212
import io.github.nanamochi.chio.protocol.versions.B20121221
import io.github.nanamochi.chio.protocol.versions.B20121224
import io.github.nanamochi.chio.protocol.versions.B20121225
import io.github.nanamochi.chio.protocol.versions.B20130118
import io.github.nanamochi.chio.protocol.versions.B20130131
import io.github.nanamochi.chio.protocol.versions.B20130209
import io.github.nanamochi.chio.protocol.versions.B20130303
import io.github.nanamochi.chio.protocol.versions.B20130418
import io.github.nanamochi.chio.protocol.versions.B20130509
import io.github.nanamochi.chio.protocol.versions.B20130604
import io.github.nanamochi.chio.protocol.versions.B20130801
import io.github.nanamochi.chio.protocol.versions.B20140528
import io.github.nanamochi.chio.protocol.versions.B20140716
import io.github.nanamochi.chio.protocol.versions.B20140731
import io.github.nanamochi.chio.protocol.versions.B20141104
import io.github.nanamochi.chio.protocol.versions.B20150826
import io.github.nanamochi.chio.protocol.versions.B20150915
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
import io.github.nanamochi.chio.protocol.versions.B365
import io.github.nanamochi.chio.protocol.versions.B374
import io.github.nanamochi.chio.protocol.versions.B388
import io.github.nanamochi.chio.protocol.versions.B402
import io.github.nanamochi.chio.protocol.versions.B425
import io.github.nanamochi.chio.protocol.versions.B452
import io.github.nanamochi.chio.protocol.versions.B470
import io.github.nanamochi.chio.protocol.versions.B487
import io.github.nanamochi.chio.protocol.versions.B489
import io.github.nanamochi.chio.protocol.versions.B490
import io.github.nanamochi.chio.protocol.versions.B504
import io.github.nanamochi.chio.protocol.versions.B535
import io.github.nanamochi.chio.protocol.versions.B558
import io.github.nanamochi.chio.protocol.versions.B591
import io.github.nanamochi.chio.protocol.versions.B613
import io.github.nanamochi.chio.protocol.versions.B634
import io.github.nanamochi.chio.protocol.versions.B659
import io.github.nanamochi.chio.protocol.versions.B695
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
            put(365, B365)
            put(374, B374)
            put(388, B388)
            put(402, B402)
            put(425, B425)
            put(452, B452)
            put(470, B470)
            put(487, B487)
            put(489, B489)
            put(490, B490)
            put(504, B504)
            put(535, B535)
            put(558, B558)
            put(591, B591)
            put(613, B613)
            put(634, B634)
            put(659, B659)
            put(695, B695)
            put(1183, B1183)
            put(1365, B1365)
            put(1600, B1600)
            put(1788, B1788)
            put(1796, B1796)
            put(1797, B1797)
            put(1800, B1800)
            put(1817, B1817)
            put(1820, B1820)
            put(20120518, B20120518)
            put(20120703, B20120703)
            put(20120723, B20120723)
            put(20120725, B20120725)
            put(20120806, B20120806)
            put(20120818, B20120818)
            put(20121023, B20121023)
            put(20121028, B20121028)
            put(20121203, B20121203)
            put(20121207, B20121207)
            put(20121211, B20121211)
            put(20121212, B20121212)
            put(20121221, B20121221)
            put(20121224, B20121224)
            put(20121225, B20121225)
            put(20130118, B20130118)
            put(20130131, B20130131)
            put(20130209, B20130209)
            put(20130303, B20130303)
            put(20130418, B20130418)
            put(20130509, B20130509)
            put(20130604, B20130604)
            put(20130801, B20130801)
            put(20140528, B20140528)
            put(20140716, B20140716)
            put(20140731, B20140731)
            put(20141104, B20141104)
            put(20150826, B20150826)
            put(20150915, B20150915)
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
