package io.github.nanamochi.chio.protocol.encode

import io.github.nanamochi.chio.binary.BinaryWriter
import io.github.nanamochi.chio.packet.model.Message

private fun Message.contentToWrite(formatChatLinks: Boolean): String =
    if (formatChatLinks) contentMarkdownFormatted else content

private fun BinaryWriter.writeMessageContent(message: Message, formatChatLinks: Boolean) {
    writeString(message.sender)
    writeString(message.contentToWrite(formatChatLinks))
}

fun BinaryWriter.writeMessageV282(message: Message, formatChatLinks: Boolean) {
    writeMessageContent(message, formatChatLinks)
}

fun BinaryWriter.writeMessageV294(message: Message, formatChatLinks: Boolean) {
    writeMessageContent(message, formatChatLinks)
    writeBoolean(message.isDirectMessage)
}

fun BinaryWriter.writeMessageV320(message: Message, formatChatLinks: Boolean) {
    writeMessageContent(message, formatChatLinks)
    writeString(message.target)
}
