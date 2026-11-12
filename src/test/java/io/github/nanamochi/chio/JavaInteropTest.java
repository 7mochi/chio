package io.github.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nanamochi.chio.packet.PacketType;
import io.github.nanamochi.chio.packet.inbound.ExitPacket;
import io.github.nanamochi.chio.packet.inbound.InboundPacket;
import io.github.nanamochi.chio.packet.model.Match;
import io.github.nanamochi.chio.packet.model.Message;
import io.github.nanamochi.chio.packet.model.ReplayFrame;
import io.github.nanamochi.chio.packet.outbound.LoginReplyPacket;
import io.github.nanamochi.chio.packet.outbound.PingPacket;
import io.github.nanamochi.chio.packet.types.ButtonState;
import io.github.nanamochi.chio.packet.types.Country;
import io.github.nanamochi.chio.packet.types.Mode;
import io.github.nanamochi.chio.packet.types.Mods;
import io.github.nanamochi.chio.packet.types.SlotTeam;
import io.github.nanamochi.chio.protocol.LegacyPacketFormat;
import io.github.nanamochi.chio.protocol.PacketIdMappingV282;
import io.github.nanamochi.chio.protocol.Protocol;
import io.github.nanamochi.chio.protocol.ProtocolBuilder;
import io.github.nanamochi.chio.protocol.ProtocolIO;
import io.github.nanamochi.chio.protocol.Protocols;
import java.util.List;
import kotlinx.io.Buffer;
import org.junit.jupiter.api.Test;

class JavaInteropTest {

    @Test
    void modelsAndValueClassesAreUsable() {
        Message message = new Message("BanchoBot", "Hello", "#osu");
        Match match = new Match();
        ReplayFrame frame = new ReplayFrame(new ButtonState(1), 1f, 2f, 3);

        assertEquals("#osu", message.getTarget());
        assertEquals(0, match.getId());
        assertEquals(1, frame.getButtonState().getBits());
        assertEquals(9, Mods.fromBits(9).getBits());
        assertEquals(PacketType.BanchoLoginReply, PacketType.from(5));
        assertEquals(Mode.Osu, Mode.from(0));
    }

    @Test
    void customProtocolBuilderIsUsable() {
        ProtocolBuilder builder = new ProtocolBuilder(283);
        builder.decode(PacketType.OsuExit, (_, _) -> ExitPacket.INSTANCE);
        builder.encode(
                LoginReplyPacket.class,
                PacketType.BanchoLoginReply,
                (writer, packet) -> writer.writeInt(packet.getReply()));
        builder.encodeEmpty(PingPacket.class, PacketType.BanchoPing);

        Protocol protocol = builder.build();

        assertTrue(protocol.encode(new LoginReplyPacket(3)).length > 0);
    }

    @Test
    void publicApiConveniencesWork() {
        Protocol protocol = Protocols.select(282);

        assertEquals(349, Protocols.getLatest().getVersion());
        assertEquals(282, Protocols.getInitial().getVersion());
        assertTrue(Protocols.getVersions().contains(340));

        assertEquals(Mode.Osu, Mode.fromAlias("std"));
        assertEquals(SlotTeam.Blue, SlotTeam.Red.getOpposite());
        assertEquals(Country.US, Country.fromAcronym("US"));

        assertTrue(protocol.encodeAll(List.of(new LoginReplyPacket(3))).length > 0);
        assertEquals(0, protocol.decodeAll(new byte[0]).size());
        assertEquals(0, protocol.decodeAll(new Buffer()).size());
        assertTrue(
                protocol.encodeRaw(PacketType.BanchoLoginReply, new byte[] {3, 0, 0, 0}).length
                        > 0);
    }

    @Test
    void protocolIoReadsAndWritesFrames() {
        Protocol protocol = Protocols.select(282);

        Buffer source = new Buffer();
        LegacyPacketFormat.INSTANCE.write(
                source,
                PacketIdMappingV282.INSTANCE.rawId(PacketType.OsuExit),
                new byte[0],
                false,
                false);

        InboundPacket packet = ProtocolIO.readBanchoPacket(source, protocol);
        assertEquals(ExitPacket.INSTANCE, packet);

        Buffer sink = new Buffer();
        ProtocolIO.writeBanchoPacket(sink, protocol, new LoginReplyPacket(3));
        assertTrue(sink.getSize() > 0);
    }
}
