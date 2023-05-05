package si.inova.androidarchitectureplayground.common.di

/**
 * Anvil scope marker class for bindings that should exist for the life of an application.
 *
 * This is an alternative to the Whetstone's *ApplicationScope* for non-android modules.
 */
class PureApplicationScope private constructor()
