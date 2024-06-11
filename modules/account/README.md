# Module account

This module provides Account management components.

# Package edu.stanford.spezi.module.account.login

The LoginScreen is a key component of the account module. It is responsible for handling user
authentication in Spezi Framework. This screen provides an interface for users to enter their email
and password to log into their account.

# Package edu.stanford.spezi.module.account.register

The RegisterScreen is a key component of the account module. It is responsible for handling user
registration in the Spezi Framework. This screen provides an interface for users to create a new
account
by entering their email, password, and other required information.

## Usage

To use the Account module in your project, add the following dependency to your `build.gradle` file:

```gradle
dependencies {
    implementation(":core:account")`
}
```

The Account module currently provides a Firebase implementation for user authentication. Other
authentication mechanism can be provided by implementing the `SignInHandler` interface.
It is also planned to support additional authentication providers in the future and also allow
custom user
input fields for registration.

In the current state of the module, the user can navigate between the LoginScreen and RegisterScreen
using the navigation component and providing the `AccountNavigationEvent`.