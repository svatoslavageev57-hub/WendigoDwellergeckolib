# Wendigo Dweller — Minecraft 1.20.1 Forge

This version is prepared for GitHub Actions.

## Project requirements
- Minecraft 1.20.1
- Forge 47.4.22
- Java 17
- GeckoLib 4.8.3
- Simple Voice Chat API 2.6.0
- Simple Voice Chat Forge runtime 2.6.10

## Build on GitHub
1. Create a GitHub repository.
2. Upload the **contents of this folder** so that `build.gradle` is in the repository root.
3. Open **Actions**.
4. Select **Build Wendigo Dweller**.
5. Click **Run workflow**.
6. After it finishes, open the successful run.
7. Under **Artifacts**, download `WendigoDweller-1.0.0`.
8. Unzip the artifact to get the mod `.jar`.

The workflow installs Gradle 8.8 automatically, so `gradlew` is not required for GitHub Actions.

## Important folder layout

The repository root must look like this:

WendigoDweller/
├── .github/
│   └── workflows/
│       └── build.yml
├── build.gradle
├── settings.gradle
├── gradle.properties
└── src/
    └── main/
        ├── java/
        └── resources/

Do not upload only the individual `.java` files to the repository root.

## Dependencies in Minecraft
Install Forge 1.20.1, GeckoLib, and Simple Voice Chat on the client/server as appropriate. The mod JAR does not bundle Simple Voice Chat.
