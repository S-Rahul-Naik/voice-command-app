# Build APK on Replit

## Quick Start (5 minutes)

### Step 1: Import to Replit
1. Go to https://replit.com/
2. Sign up/Login (free)
3. Click "+ Create Repl"
4. Choose "Import from GitHub"
5. Paste: `https://github.com/S-Rahul-Naik/voice-command-app`
6. Click "Import from GitHub"

### Step 2: Build the APK
1. Wait for Replit to load your project
2. Click the **"Run"** button (green play button at top)
3. Wait 5-10 minutes for build to complete
4. Look for: **"BUILD SUCCESSFUL"**

### Step 3: Download APK
1. In Replit file explorer (left side)
2. Navigate to: `app/build/outputs/apk/debug/`
3. Right-click `app-debug.apk`
4. Click **"Download"**

### Step 4: Install on Phone
1. Transfer APK to your Android phone
2. Open the APK file on phone
3. Allow "Install from unknown sources" if prompted
4. Install the app

### Step 5: Test
1. Open the app
2. Grant microphone permission
3. Tap "Start Listening"
4. Say: **"open YouTube"**
5. YouTube should open directly! 🎉

## Troubleshooting

If build fails in Replit, try these commands in the Shell:
```bash
chmod +x gradlew
./gradlew clean
./gradlew assembleDebug
```

The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
