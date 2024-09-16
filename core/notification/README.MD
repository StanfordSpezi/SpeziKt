# Module notification

The notification module provides a way to show notifications to the user and handle FCM as well as
the notification settings.

## NotificationNotifier

`NotificationNotifier` is a class that is responsible for showing notifications to the user.

```kotlin
class NotificationNotifier {
    fun sendNotification(firebaseMessage: FirebaseMessage): Unit {
        // Show notification to the user
    }
}
```

`sendNotification(firebaseMessage: FirebaseMessage)` is a function that takes a message and shows a
notification to
the user.

## FCM

The `DeviceRegistrationService` is a internal class responsible for registering the device to
receive FCM
notifications and the `FCMNotificationService` is a internal class responsible for handling the FCM
notifications.

## NotificationSettings

The `NotificationSettings` is responsible for handling the notification settings. It provides a way
to enable or disable notifications and also groups the settings for the notification by sections.

## NotificationSettingScreen

The `NotificationSettingScreen` is a screen that shows the notification settings to the user. It
uses the `NotificationSettings` to get the settings and update them.
The `NotificationNavigationEvent` is a sealed class that represents the navigation events that can
be triggered by the `NotificationSettingScreen` to navigate to the `NotificationSettingScreen`.
