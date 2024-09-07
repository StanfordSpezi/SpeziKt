# Module notification

The notification module provides a way to show notifications to the user and handle FCM as well as
the notification settings.

## Notifier

`NotificationNotifier` is an interface that defines the contract for a notifier responsible for
showing notifications:

```kotlin
interface NotificationNotifier {
    fun sendNotification(message: String)
}
```

`sendNotification(message: String)` is a function that takes a message and shows a notification to
the user.

## FCM

The `DeviceRegistrationService` is responsible for registering the device to receive FCM
notifications and the `FCMNotificationService` is responsible for handling the FCM notifications.

## Setting

The `NotificationSetting` is responsible for handling the notification settings. It provides a way
to enable or disable notifications.
