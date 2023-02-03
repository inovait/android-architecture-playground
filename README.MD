# Empty Android project

Empty android project with basic architecture set up.

To create new Android project from that:

1. Clone this repo
2. Copy all files (except for the .git and this README.MD) into your new project's folder
3. Rename si.inova.androidarchitectureplayground folder (package)
4. Rename theme in `app/res/values/themes.xml`
5. Rename `namespace` and `applicatonId` in `app/build.gradle.kts`
6. Rename `rootProject.name` in `settings.gradle.kts
7. Rename `app_name` in `app/res/values/strings.xml`
8. Rename theme in `app/src/main/java/si/inova/impactwrap/tv/Theme.kt`

## Keystore

To generate keys, you can execute these command inside `keys` folder:

```bash
KEYSTORE_PASSWORD=$(dd if=/dev/random bs=3 count=20 2>/dev/null | md5sum | sed 's/ .*$//')
echo y | keytool -genkey -keystore ./release.jks -dname "ou=Inova IT" -alias app -storepass $KEYSTORE_PASSWORD -keyalg RSA -keysize 2048 -validity 2000
echo y | keytool -genkey -keystore ./debug.jks -dname "ou=Inova IT" -alias androiddebugkey -storepass android -keyalg RSA -keysize 2048 -validity 2000
echo "RELEASE_KEYSTORE_PASSWORD = '$KEYSTORE_PASSWORD"

echo

echo "DEBUG CERTIFICATE FINGERPRINTS:"
keytool -list -v -keystore "debug.jks" -alias androiddebugkey -storepass android -keypass android

echo "RELEASE CERTIFICATE FINGERPRINTS:"
keytool -list -v -keystore "release.jks" -alias app -storepass $KEYSTORE_PASSWORD -keypass $KEYSTORE_PASSWORD
```

Afterwards:

1. Enter generated keystore password into Github project's secrets (and ideally also store it somewhere else as a backup).
2. Enter generated SHA1 and SHA256 fingerprints into `app/build.gradle.kts`, so they can be used later.
