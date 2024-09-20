package edu.stanford.spezi.core.notification.notifier

import javax.inject.Qualifier

interface Notifications {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TargetActivity
}
