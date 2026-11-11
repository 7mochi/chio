# Chio

**Chio (Bancho I/O)** is a Kotlin library for serializing and deserializing osu! Bancho packets. It
targets osu! versions from 2008 through 2026, and coverage is still growing. It's a port of
[chio.py](https://github.com/Lekuruu/chio.py) and
[chio-sharp](https://github.com/Lekuruu/chio-sharp). The API is designed for Kotlin and can also
be used from Java.

## Usage

Here is an example of how to use chio as a library:

### Kotlin

```kotlin
// Use the client streams, or buffers for this example.
val sink: Sink = Buffer()
val source: Source = Buffer()

// Pick the protocol for the client version.
val protocol = Protocols.select(340)

// Build the user that will log in.
val info =
    UserInfo(
        id = 2,
        name = "peppy",
        presence = UserPresence(),
        stats = UserStats(),
        status = UserStatus(),
    )

// Send the login reply, presence and stats.
val outgoing = sink.buffered()
outgoing.writeBanchoPacket(protocol, LoginReplyPacket(info.id))
outgoing.writeBanchoPacket(protocol, UserPresencePacket(info))
outgoing.writeBanchoPacket(protocol, UserStatsPacket(info))

// Join #osu and greet the player.
outgoing.writeBanchoPacket(protocol, IrcJoinPacket("#osu"))
outgoing.writeBanchoPacket(protocol, MessagePacket(Message("BanchoBot", "Hello!", "#osu")))
outgoing.flush()

// Read the next packet from the client.
val incoming = source.buffered()
val packet = incoming.readBanchoPacket(protocol)
```

### Java

```java
// Use the client streams, or buffers for this example.
Sink sink = new Buffer();
Source source = new Buffer();

// Pick the protocol for the client version.
Protocol protocol = Protocols.select(340);

// Build the user that will log in.
UserInfo info =
        new UserInfo(2, "peppy", new UserPresence(), new UserStatus(), new UserStats());

// Send the login reply, presence and stats.
ProtocolIO.writeBanchoPacket(sink, protocol, new LoginReplyPacket(info.getId()));
ProtocolIO.writeBanchoPacket(sink, protocol, new UserPresencePacket(info));
ProtocolIO.writeBanchoPacket(sink, protocol, new UserStatsPacket(info));

// Join #osu and greet the player.
ProtocolIO.writeBanchoPacket(sink, protocol, new IrcJoinPacket("#osu"));
ProtocolIO.writeBanchoPacket(
        sink, protocol, new MessagePacket(new Message("BanchoBot", "Hello!", "#osu")));
sink.flush();

// Read the next packet from the client.
InboundPacket packet = ProtocolIO.readBanchoPacket(source, protocol);
```

Custom protocols use a `ProtocolBuilder`. `Protocols.select` is for the built-in versions; this
example uses `283`, which is not registered in `Protocols`.

### Kotlin

```kotlin
val custom =
    protocol(283) {
        decode(PacketType.OsuExit) { ExitPacket }
        encode<LoginReplyPacket>(PacketType.BanchoLoginReply) { writeInt(it.reply) }
        encodeEmpty<PingPacket>(PacketType.BanchoPing)
    }
```

### Java

```java
ProtocolBuilder builder = new ProtocolBuilder(283);
builder.decode(PacketType.OsuExit, (protocol1, reader) -> ExitPacket.INSTANCE);
builder.encode(
        LoginReplyPacket.class,
        PacketType.BanchoLoginReply,
        (writer, reply) -> writer.writeInt(reply.getReply()));
builder.encodeEmpty(PingPacket.class, PacketType.BanchoPing);
Protocol custom = builder.build();
```

## Build

```bash
just --list
just build
just check
just test
just format
just lint
just clean
```

## Thanks to

- [Levi](https://github.com/Lekuruu) for creating and maintaining
  [chio.py](https://github.com/Lekuruu/chio.py),
  [chio-sharp](https://github.com/Lekuruu/chio-sharp) and
  [bancho-documentation](https://github.com/Lekuruu/bancho-documentation).
