package hu.kdea.szavazas.ballotprocessor

object Logger {
    var delegate: (String, String) -> Unit = { tag, msg -> kotlin.io.println("$tag: $msg") }

    fun d(tag: String, msg: String) = delegate(tag, msg)
    fun e(tag: String, msg: String) = delegate(tag, msg)
}