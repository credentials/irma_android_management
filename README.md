# IRMA Android Management application

This Android App lets you manage the credentials on you IRMA card. In particular you can see which credentials you have, what attributes they contain, and when they will expire. Furthermore you can do some card related tasks like

 * change the management PIN code,
 * change the regular PIN, and
 * look at the logs on the card, to see what actions were performed on your credentials.

## Prerequisites

This library has the following dependencies.  All these dependencies will be automatically downloaded by gradle when building or installing the library.

External dependencies:

 * Android support library v.4

Internal dependencies:

 * [irma_android_library](https://github.com/credentials/irma_android_library/), The IRMA credentials API implementation for Idemix

The build system depends on gradle version at least 2.1, which is why we've included the gradle wrapper, so you always have the right version.

### Linking `irma_configuration`

Make sure to link irma_configuration into assets/ as the application will not work otherwise! As always, select the correct branch.

## Building

Run

    ./gradlew assemble

this will create the required `.apk`s and place them in `build/outputs/apk`. Make sure you linked `irma_configuration`.

## Installing on your own device

You can install the application to you own device by running

    ./gradlew installDebug
