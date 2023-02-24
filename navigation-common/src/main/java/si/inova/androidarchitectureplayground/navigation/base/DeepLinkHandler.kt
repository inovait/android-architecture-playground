package si.inova.androidarchitectureplayground.navigation.base

import android.net.Uri
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

fun interface DeepLinkHandler {
   fun handleDeepLink(uri: Uri): NavigationInstruction?
}

class MultiDeepLinkHandler : DeepLinkHandler {
   private val handlers = ArrayList<DeepLinkHandler>()

   fun matchDeepLink(vararg patterns: String, mapper: (Map<String, String>) -> NavigationInstruction?) {
      handlers += DeepLinkHandler { it.matchDeepLink(*patterns, mapper = mapper) }
   }

   fun customHandler(block: DeepLinkHandler) {
      handlers += block
   }

   override fun handleDeepLink(uri: Uri): NavigationInstruction? {
      return handlers.asSequence().mapNotNull { it.handleDeepLink(uri) }.firstOrNull()
   }
}

@Suppress("UnusedReceiverParameter") // This parameter is used to limit autocomplete scope
fun DeepLinkHandler.handleMultipleDeepLinks(uri: Uri, block: MultiDeepLinkHandler.() -> Unit): NavigationInstruction? {
   val multiDeepLinkHandler = MultiDeepLinkHandler()
   block(multiDeepLinkHandler)

   return multiDeepLinkHandler.handleDeepLink(uri)
}
