package androidx.fragment.app

fun FragmentManager.setContainerAvailable(containerView: FragmentContainerView) {
   onContainerAvailable(containerView)
}
