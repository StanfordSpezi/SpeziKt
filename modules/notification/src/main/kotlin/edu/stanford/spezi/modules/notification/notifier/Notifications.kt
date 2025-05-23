package edu.stanford.spezi.modules.notification.notifier

import javax.inject.Qualifier

interface Notifications {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TargetActivity
}
