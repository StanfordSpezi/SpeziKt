package edu.stanford.spezi.modules.notification

import edu.stanford.spezi.modules.navigation.NavigationEvent

sealed class NotificationNavigationEvent : NavigationEvent {

    data object NotificationSettings : NotificationNavigationEvent()
}
