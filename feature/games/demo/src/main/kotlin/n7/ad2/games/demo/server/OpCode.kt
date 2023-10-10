package n7.ad2.games.demo.server

import n7.ad2.games.demo.server.OpCode.MASK_OP_CODE

internal object OpCode {
    internal const val MASK_OP_CODE = 0b00001111

    internal const val OPCODE_CONTINUATION = 0x0
    internal const val OPCODE_TEXT = 0x1
    internal const val OPCODE_BINARY = 0x2
    internal const val OPCODE_CLOSE = 0x8
    internal const val OPCODE_PING = 0x9
    internal const val OPCODE_PONG = 0xa
}

internal fun getOpCode(byte: Int): Int {
    return byte and MASK_OP_CODE
}
