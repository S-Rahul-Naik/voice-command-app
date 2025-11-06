# 🎤 Voice Command App

A powerful Android voice assistant app that lets you control your phone and launch apps using voice commands. Built with Kotlin and Android's SpeechRecognizer API.

## 📱 Features

**50+ Voice Commands** across multiple categories:
- 🔦 Device Controls (Flashlight, WiFi, Bluetooth, Volume, Brightness, Airplane Mode)
- 📞 Communication (Calls, SMS, WhatsApp)
- 🎵 Media & Apps (YouTube, Spotify, Any App)
- ⏰ Productivity (Alarms, Timers, Reminders, Calendar)
- 🗺️ Navigation (Maps, Nearby Search, Weather)
- 📷 Camera & Photos
- 📰 Information (News, Translation, Calculator)

## 🚀 Getting Started

### Prerequisites
- Android device running Android 7.0 (API 24) or higher
- Microphone permission
- Internet connection for some features

### Installation

1. **Download the APK**
   - Go to [GitHub Actions](https://github.com/S-Rahul-Naik/voice-command-app/actions)
   - Click on the latest successful workflow run
   - Download the APK artifact

2. **Install on your device**
   - Enable "Install from Unknown Sources" in your device settings
   - Install the downloaded APK

3. **Grant Permissions**
   - On first launch, grant all requested permissions
   - Some features require specific permissions (microphone, contacts, SMS, etc.)

## 🎯 Voice Commands Guide

### 🔦 Device Controls

#### Flashlight/Torch
```
"Turn on flashlight"
"Turn off flashlight"
"Turn on torch"
"Turn off torch"
```

#### WiFi
```
"Turn on WiFi"        (opens WiFi settings on Android 10+)
"Turn off WiFi"       (opens WiFi settings on Android 10+)
```

#### Bluetooth
```
"Turn on Bluetooth"   (opens Bluetooth settings on Android 12+)
"Turn off Bluetooth"  (opens Bluetooth settings on Android 12+)
```

#### Volume
```
"Volume up"
"Volume down"
"Increase volume"
"Decrease volume"
"Mute"
```

#### Brightness
```
"Increase brightness" (opens display settings)
"Decrease brightness" (opens display settings)
"Turn up brightness"
"Turn down brightness"
```

#### Airplane Mode
```
"Turn on airplane mode"  (shows current status + opens settings)
"Turn off airplane mode" (shows current status + opens settings)
"Turn on flight mode"
```

### 📞 Communication

#### Phone Calls
```
"Call John"           (replace "John" with any contact name)
"Call Mom"
"Call [contact name]"
```

#### SMS/Text Messages
```
"Send a text to John hello"     (contact + message)
"Send text to Mom how are you"
"Text Sarah I'm coming"
```

#### WhatsApp
```
"WhatsApp John hello there"  (contact + message)
"WhatsApp Mom good morning"
```

### 🎵 Media & Apps

#### Open Any App
```
"Open YouTube"
"Open Gmail"
"Open Instagram"
"Open Chrome"
"Open [any app name]"
```

#### YouTube
```
"Play Singh Zara song"      (opens YouTube search)
"Play despacito"
"Play [song/video name] on YouTube"
```

#### Spotify
```
"Play shape of you on Spotify"  (opens Spotify search)
"Play [song name] on Spotify"
```

#### Google Search
```
"Search for pizza"
"Search best restaurants"
"Search [anything]"
```

### ⏰ Productivity

#### Alarms
```
"Set alarm for 7 AM"
"Set alarm for 6:30 PM"
"Set an alarm"
```

#### Timers
```
"Set timer for 5 minutes"
"Set timer for 2 hours"
"Set timer for 30 seconds"
```

#### Reminders
```
"Remind me to call mom"
"Set reminder to buy groceries"
"Set a reminder to workout"
```

#### Calendar
```
"Add meeting to calendar"
"Add doctor appointment to my calendar"
"What's my schedule"  (opens calendar)
```

### 🗺️ Navigation & Location

#### Navigation
```
"Navigate to Times Square"
"Navigate to home"
"Directions to Central Park"
```

#### Nearby Search
```
"Show me restaurants nearby"
"Show me hospitals nearby"
"Show me nearby gas stations"
```

#### Weather
```
"What's the weather"
"Weather"
```

### 📷 Camera & Photos

#### Take Photos
```
"Take a photo"
"Take a picture"
"Take a selfie"  (opens front camera)
```

#### Screenshots
```
"Take a screenshot"  (shows instructions: Power + Volume Down)
"Screenshot"
```

#### Gallery
```
"Show me photos"
"Open gallery"
```

### 📰 Information

#### News
```
"What's the news"
"News today"
"Latest news"
```

#### Translation
```
"Translate hello to Spanish"
"Translate [any text]"
```

#### Calculator
```
"What's 20 percent of 100"
"What's 15% of 200"
"What is 50 percent of 80"
"Open calculator"  (opens calculator app)
```

## 💡 Usage Tips

### ✅ Working Perfectly
- Opening apps
- Flashlight control
- Voice recognition
- Google search
- Volume control

### ⚠️ Requires User Interaction
- **YouTube/Spotify**: Opens search → You tap to play (normal behavior due to API restrictions)
- **WiFi/Bluetooth/Brightness**: Opens settings → You toggle manually (Android security)
- **Airplane Mode**: Shows status → You toggle manually (Android security)

### 📝 Important Notes

1. **Contact Names**: Use exact names from your contacts
   - ✅ "Call John" (if John is in contacts)
   - ❌ "Call Johnny" (if contact name is John)

2. **App Names**: Say the app name as it appears
   - ✅ "Open Instagram" 
   - ✅ "Open YouTube"

3. **SMS Format**: "Send text to [name] [message]"
   - ✅ "Send text to John hello there"
   - ❌ "Send John a text" (won't work)

4. **Calculations**: Must start with "What's"
   - ✅ "What's 20 percent of 100"
   - ❌ "Calculate 20 percent of 100"

## 🛠️ Technical Details

### Built With
- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 33 (Android 13)
- **Build System**: Gradle 7.5
- **Java**: JDK 11

### Key Android APIs
- SpeechRecognizer (voice input)
- AlarmClock (alarms/timers)
- CalendarContract (events/reminders)
- ContactsContract (phone contacts)
- CameraManager (flashlight)
- WifiManager, BluetoothAdapter
- SmsManager
- MediaStore (camera)

### Permissions
The app requests 25+ permissions including:
- Microphone (RECORD_AUDIO)
- Camera
- Contacts (READ_CONTACTS)
- Calendar (READ/WRITE_CALENDAR)
- SMS (SEND_SMS, READ_SMS)
- Phone (CALL_PHONE)
- Location (ACCESS_FINE_LOCATION)
- WiFi, Bluetooth
- Storage
- And more...

## 🏗️ Building from Source

### Prerequisites
- Android Studio or VS Code
- JDK 11
- Gradle 7.5
- Android SDK

### Build Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/S-Rahul-Naik/voice-command-app.git
   cd voice-command-app
   ```

2. **Set JAVA_HOME**
   ```bash
   # Windows PowerShell
   $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-11.0.21.9-hotspot"
   
   # Linux/Mac
   export JAVA_HOME=/path/to/jdk-11
   ```

3. **Build the APK**
   ```bash
   # Windows
   .\gradlew.bat assembleDebug
   
   # Linux/Mac
   ./gradlew assembleDebug
   ```

4. **Install on device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 🤖 CI/CD

The project uses **GitHub Actions** for automated builds:
- Builds APK on every commit
- Build time: ~2 minutes
- APK available in Actions artifacts

## 📊 Project Stats

- **Total Commands**: 50+
- **Code Lines**: ~1,470 lines (MainActivity.kt)
- **Total Commits**: 15+
- **App Size**: ~2MB
- **Supported Languages**: English

## 🐛 Known Limitations

1. **YouTube/Spotify**: Cannot auto-play songs due to API restrictions (same as Google Assistant)
2. **WiFi/Bluetooth Toggle**: On Android 10+ and 12+, can only open settings (Android security)
3. **Airplane Mode**: Cannot be toggled programmatically (Android security)
4. **Brightness**: Requires system settings permission, opens settings instead
5. **Screenshot**: Cannot be automated, shows instructions to use hardware buttons

## 🔮 Future Enhancements

- [ ] Multi-language support
- [ ] Custom wake word
- [ ] Offline voice recognition
- [ ] Command history
- [ ] Customizable commands
- [ ] Voice feedback responses
- [ ] Widget for quick access
- [ ] Background listening mode

## 🤝 Contributing

Contributions are welcome! Feel free to:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Developer

**Sandesh** / **S-Rahul-Naik**

## 🙏 Acknowledgments

- Android SpeechRecognizer API
- Google Maps API
- Material Design Components
- All open-source libraries used

## 📞 Support

If you encounter any issues or have questions:
1. Check the [Issues](https://github.com/S-Rahul-Naik/voice-command-app/issues) page
2. Create a new issue with details
3. Check the command guide above

---

**Made with ❤️ using Kotlin and Android Studio**

⭐ **Star this repo if you find it useful!**
