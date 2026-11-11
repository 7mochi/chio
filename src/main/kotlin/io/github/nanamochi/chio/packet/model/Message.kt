package io.github.nanamochi.chio.packet.model

private val CHAT_LINK_MODERN = Regex("""\[(https?://[^\s\]]+)\s+((?:[^\[\]]|\[[^\[\]]*])*)]""")

data class Message
@JvmOverloads
constructor(
    val sender: String,
    val content: String,
    val target: String,
    val senderId: Int = -1,
) {
    val isDirectMessage: Boolean
        get() = !target.startsWith("#")

    /** Converts the modern `[url text]` form to `(text)[url]`, which older clients render. */
    val contentMarkdownFormatted: String
        get() =
            CHAT_LINK_MODERN.replace(content) { match ->
                "(${match.groupValues[2]})[${match.groupValues[1]}]"
            }
}
