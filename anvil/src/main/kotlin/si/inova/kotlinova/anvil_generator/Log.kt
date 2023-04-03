package si.inova.kotlinova.anvil_generator

import java.io.File

/**
 * Utility for debugging, because println calls
 * are not forwarded to the gradle output.
 *
 * Do not use in production
 */
@Suppress("unused")
fun log(codeGenDir: File, text: String) {
   codeGenDir.mkdirs()
   File(codeGenDir, "anvil_genetator_log.txt").appendText("$text\n")
}
