# Translator App

Welcome to the Translator App! This is an open-source Android application built using Kotlin, leveraging Google's ML Kit for real-time language translation. The app supports multiple languages and uses the flavor dimension feature in Android to manage language-specific configurations effectively.

## Features

- **Real-time Language Translation**: Translate text across supported languages using Google ML Kit.
- **Multi-language Support**: Includes translations for English, Urdu, Hindi, Marathi, and Arabic.
- **Dynamic Language Downloads**: Downloads and manages language models dynamically for offline usage.
- **Flavor Dimensions for Languages**: Uses Android flavors to manage configurations for each language (e.g., Urdu, Arabic, Hindi, Marathi).
- **Open Source**: Fully accessible source code for learning and contributions.

## Getting Started

### Prerequisites

- **Android Studio**: Latest stable version.
- **Kotlin**: Configured in your Android Studio environment.
- **Gradle**: Latest version compatible with Android Studio.
- **Git**: For cloning the repository.

### Clone the Repository

```bash
$ git clone https://github.com/yourusername/translator-app.git
$ cd translator-app
```

### Project Structure

The app uses the **flavor dimension** feature in Android. Flavors are defined to handle different language-specific configurations:

- **`urdu`**: Flavor with the downloaded model for Urdu language translation.
- **`arabic`**: Flavor with the downloaded model for Arabic language translation.
- **`hindi`**: Flavor with the downloaded model for Hindi language translation.
- **`marathi`**: Flavor with the downloaded model for Marathi language translation.

### Configuring Flavors

To understand how flavors are set up, check the `build.gradle` file in the app module:

```kotlin
flavorDimensions += "version"

productFlavors {
        create("english_to_urdu") {
            dimension = "version"
            applicationId = "com.englishto.urdu.translator"
            versionCode = 1
            versionName = "1.0.1"
            buildConfigField("String", "FLAVOR", "\"english_to_urdu\"")
        }
        create("english_to_hindi") {
            dimension = "version"
            applicationId = "com.englishto.hindi.translator"
            versionCode = 1
            versionName = "1.0.1"
            buildConfigField("String", "FLAVOR", "\"english_to_hindi\"")
        }
```

### Build and Run

1. Open the project in Android Studio.
2. Sync the Gradle files.
3. Select the desired flavor (e.g., `urdu`, `arabic`, `hindi`, or `marathi`) from the **Build Variants** panel.
4. Run the app on an emulator or a physical device.

## Using the App

1. Choose the source and target languages.
2. Enter text to be translated.
3. View the real-time translated output.
4. Languages can be downloaded for offline translation from the app settings.

## Supported Languages

- English
- Urdu
- Hindi
- Marathi
- Arabic

## Contributing

We welcome contributions to enhance this project! Here’s how you can contribute:

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes and push them to your fork.
4. Open a pull request for review.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

If you have any questions or suggestions, feel free to open an issue or reach out via email at your.email@example.com.

## Acknowledgments

- [Google ML Kit](https://developers.google.com/ml-kit) for the language translation API.
- The Android and Kotlin developer community for continuous support.

