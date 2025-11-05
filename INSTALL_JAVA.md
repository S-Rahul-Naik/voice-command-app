# ☕ Install Java JDK - Step by Step

## 🎯 Quick Install Guide for Windows

### **Step 1: Download Java JDK 11**

1. **Go to**: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html

   **OR use this direct link for easier installation:**
   
   **Microsoft OpenJDK 11** (Recommended - easier):
   https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-11

2. **Click**: Windows x64 MSI (around 160 MB)

3. **Download** the file

---

### **Step 2: Install Java**

1. **Run the downloaded file** (double-click)

2. **Click "Next"** through the installer

3. **Important**: Note the installation path (usually `C:\Program Files\Java\jdk-11`)

4. **Click "Install"** and wait

5. **Click "Close"** when done

---

### **Step 3: Set JAVA_HOME Environment Variable**

1. **Press** `Windows Key` + type "Environment Variables"

2. **Click**: "Edit the system environment variables"

3. **Click**: "Environment Variables" button (bottom)

4. **Under "System variables"**, click "New":
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-11` (or your install path)
   - Click "OK"

5. **Find "Path"** in System variables, click "Edit"

6. **Click "New"** and add: `%JAVA_HOME%\bin`

7. **Click "OK"** on all windows

---

### **Step 4: Verify Installation**

**Close and reopen PowerShell**, then run:

```powershell
java -version
```

You should see something like:
```
java version "11.0.x"
```

---

### **Step 5: Build Your APK!**

Once Java is installed and verified, run:

```powershell
cd C:\Users\Lenovo\Desktop\Sandesh\android-app
.\gradlew.bat assembleDebug
```

Your APK will be at:
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## ⚡ **FASTER OPTION: Use Chocolatey Package Manager**

If you have Chocolatey installed, just run:

```powershell
choco install openjdk11
```

Don't have Chocolatey? Install it first:
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

---

## 🆘 **Quick Links:**

- **Microsoft OpenJDK 11**: https://learn.microsoft.com/en-us/java/openjdk/download
- **Oracle JDK 11**: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
- **AdoptOpenJDK**: https://adoptium.net/temurin/releases/?version=11

---

## 📝 **After Installing:**

1. **Close all PowerShell/CMD windows**
2. **Open a NEW PowerShell**
3. **Run**: `java -version` to verify
4. **Then run**: `cd C:\Users\Lenovo\Desktop\Sandesh\android-app; .\gradlew.bat assembleDebug`
5. **Wait 5-10 minutes** for first build
6. **Get APK** from `app\build\outputs\apk\debug\app-debug.apk`

---

**Start with Step 1 above! Let me know when you've downloaded the JDK installer!** ☕
