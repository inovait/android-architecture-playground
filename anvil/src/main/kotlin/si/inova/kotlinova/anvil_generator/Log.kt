package si.inova.kotlinova.anvil_generator

import java.io.File

/**
 * Utility for debugging, because println calls
 * are not forwarded to the gradle output.
 *
 * Do not use for debugging.
 */
fun log(codeGenDir: File, text: String) {
   File(codeGenDir, "anvil_genetator_log.txt").appendText("$text\n")
}
