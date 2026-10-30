package n7.ad2.android

interface DrawerPercentListener {

    /** The listener gets the current percent right away and then every change while the drawer moves. */
    fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?)

    /**
     * Removes [listener] only if it is still the current one: when tabs switch, the new screen subscribes
     * before the old one is disposed, and clearing blindly would unsubscribe the new screen.
     */
    fun removeDrawerPercentListener(listener: (percent: Float) -> Unit)
}
