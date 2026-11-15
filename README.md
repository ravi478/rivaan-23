# RIVAN CSC Manager — GitHub Actions

## GitHub upload

Upload the **contents of this ZIP** into the root of your GitHub repository.

Correct root should look like:

```text
RivanCscManager/
├── .github/
│   └── workflows/
│       └── build_apk.yml
├── app/
│   ├── build.gradle.kts
│   └── src/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

Do **not** upload the ZIP file itself as the project.

## Start the build

1. Open GitHub repository.
2. Open **Actions**.
3. Select **RIVAN CSC Manager - Android APK**.
4. Click **Run workflow**.
5. Select the branch and run it.

The workflow also runs automatically on every push and pull request.

## APK

After a successful build:

Actions → latest workflow run → **Artifacts** → `Rivan-CSC-Manager-debug`

Download the artifact ZIP and extract the APK.

## Important

This project intentionally uses GitHub Actions to install Gradle 8.5, so a local `gradle` installation is not required for the GitHub build.
