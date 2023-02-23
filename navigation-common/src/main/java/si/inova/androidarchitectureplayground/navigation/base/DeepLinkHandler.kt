package si.inova.androidarchitectureplayground.navigation.base

import android.net.Uri
import si.inova.androidarchitectureplayground.navigation.keys.NavigationKey

fun interface DeepLinkHandler {
   fun handleDeepLink(uri: Uri): NavigationKey?
}

class MultiDeepLinkHandler : DeepLinkHandler {
   private val handlers = ArrayList<DeepLinkHandler>()

   fun matchDeepLink(pattern: String, mapper: (Map<String, String>) -> NavigationKey?) {
      handlers += DeepLinkHandler { it.matchDeepLink(pattern, mapper) }
   }

   fun customHandler(block: DeepLinkHandler) {
      handlers += block
   }

   override fun handleDeepLink(uri: Uri): NavigationKey? {
      return handlers.asSequence().mapNotNull { it.handleDeepLink(uri) }.firstOrNull()
   }
}

@Suppress("UnusedReceiverParameter") // This parameter is used to limit autocomplete scope
fun DeepLinkHandler.handleMultipleDeepLinks(uri: Uri, block: MultiDeepLinkHandler.() -> Unit): NavigationKey? {
   val multiDeepLinkHandler = MultiDeepLinkHandler()
   block(multiDeepLinkHandler)

   return multiDeepLinkHandler.handleDeepLink(uri)
}
