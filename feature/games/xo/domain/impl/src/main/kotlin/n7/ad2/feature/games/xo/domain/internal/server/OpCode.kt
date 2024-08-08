package n7.ad2.feature.games.xo.domain.internal.server

/**
 * RFC-6455#sedtion-11.8
 *
 *      |Opcode  | Meaning                             | Reference |
 *     -+--------+-------------------------------------+-----------|
 *      | 0      | Continuation Frame                  | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 *      | 1      | Text Frame                          | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 *      | 2      | Binary Frame                        | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 *      | 8      | Connection Close Frame              | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 *      | 9      | Ping Frame                          | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 *      | 10     | Pong Frame                          | RFC 6455  |
 *     -+--------+-------------------------------------+-----------|
 */
internal object OpCode {
    internal const val OPCODE_CONTINUATION = 0
    internal const val OPCODE_TEXT = 1
    internal const val OPCODE_BINARY = 2
    internal const val OPCODE_CLOSE = 8
    internal const val OPCODE_PING = 9
    internal const val OPCODE_PONG = 10
}

