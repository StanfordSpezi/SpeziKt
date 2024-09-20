package edu.stanford.spezi.core.notification

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class NotificationNavigationEvent : NavigationEvent {

    data object NotificationSettings : NotificationNavigationEvent()
}
