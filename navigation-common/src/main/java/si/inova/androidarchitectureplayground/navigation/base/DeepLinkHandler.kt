package si.inova.androidarchitectureplayground.navigation.base

import android.net.Uri
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

fun interface DeepLinkHandler {
   fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction?
}

class MultiDeepLinkHandler : DeepLinkHandler {
   private val handlers = ArrayList<DeepLinkHandler>()

   fun matchDeepLink(vararg patterns: String, mapper: (args: Map<String, String>, startup: Boolean) -> NavigationInstruction?) {
      handlers += DeepLinkHandler { uri: Uri, startup: Boolean -> uri.matchDeepLink(*patterns) { mapper(it, startup) } }
   }

   fun customHandler(block: DeepLinkHandler) {
      handlers += block
   }

   override fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction? {
      return handlers.asSequence().mapNotNull { it.handleDeepLink(uri, startup) }.firstOrNull()
   }
}

@Suppress("UnusedReceiverParameter") // This parameter is used to limit autocomplete scope
fun DeepLinkHandler.handleMultipleDeepLinks(
   uri: Uri,
   startup: Boolean,
   block: MultiDeepLinkHandler.() -> Unit
): NavigationInstruction? {
   val multiDeepLinkHandler = MultiDeepLinkHandler()
   block(multiDeepLinkHandler)

   return multiDeepLinkHandler.handleDeepLink(uri, startup)
}
