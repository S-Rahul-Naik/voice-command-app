# 🚀 Build APK with GitHub Actions - Step by Step

## ✅ Prerequisites:
- GitHub account (free): https://github.com/signup
- Git installed on your PC

---

## 📋 **STEP 1: Create GitHub Account**

1. Go to https://github.com/signup
2. Sign up with your email
3. Verify your email
4. Login to GitHub

---

## 📋 **STEP 2: Install Git (if not installed)**

### Check if Git is installed:
```powershell
git --version
```

### If not installed, download from:
https://git-scm.com/download/win

Install with default settings.

---

## 📋 **STEP 3: Create GitHub Repository**

1. Go to https://github.com/new
2. **Repository name**: `voice-command-app`
3. **Description**: "Voice command Android app"
4. Select: **Public** (free builds only work for public repos)
5. **DON'T** check "Initialize with README"
6. Click: **"Create repository"**

---

## 📋 **STEP 4: Upload Your Code to GitHub**

Open PowerShell in the android-app folder and run these commands:

```powershell
# Navigate to your project
cd C:\Users\Lenovo\Desktop\Sandesh\android-app

# Initialize Git
git init

# Add all files
git add .

# Commit files
git commit -m "Initial commit - Voice Command App"

# Add your GitHub repository
git remote add origin https://github.com/YOUR_USERNAME/voice-command-app.git

# Push to GitHub
git branch -M main
git push -u origin main
```

**Replace `YOUR_USERNAME` with your actual GitHub username!**

### If asked for credentials:
- **Username**: Your GitHub username
- **Password**: Use a **Personal Access Token** (not your GitHub password)
  - Create token at: https://github.com/settings/tokens
  - Click "Generate new token (classic)"
  - Select scopes: `repo`, `workflow`
  - Copy the token and use it as password

---

## 📋 **STEP 5: Wait for GitHub Actions to Build**

1. After pushing, go to your GitHub repository:
   `https://github.com/YOUR_USERNAME/voice-command-app`

2. Click on the **"Actions"** tab

3. You'll see a workflow running: **"Build Android APK"**

4. Wait 5-10 minutes for it to complete (green checkmark ✅)

5. Click on the completed workflow

6. Scroll down to **"Artifacts"**

7. Download: **"voice-command-app"** (this is your APK!)

---

## 📋 **STEP 6: Install APK on Your Phone**

1. **Unzip the downloaded file** - you'll find `app-debug.apk`

2. **Transfer to phone**:
   - USB cable: Copy to phone storage
   - Email: Email it to yourself
   - Google Drive: Upload and download on phone
   - WhatsApp: Send to yourself

3. **Install on phone**:
   - Tap the APK file
   - If prompted, enable "Install from unknown sources"
   - Tap "Install"

4. **Grant permissions**:
   - Open the app
   - Grant microphone permission when asked

5. **Test it!**:
   - Tap "Start"
   - Say: **"open YouTube"**
   - YouTube app should open DIRECTLY! 🎉

---

## 🆘 **Troubleshooting:**

### Problem: "git: command not found"
**Solution**: Install Git from https://git-scm.com/download/win

### Problem: "Permission denied" when pushing
**Solution**: Use a Personal Access Token instead of password
- Go to: https://github.com/settings/tokens
- Generate new token with `repo` and `workflow` scopes
- Use token as password

### Problem: Build failed on GitHub
**Solution**: 
- Go to Actions tab
- Click on the failed workflow
- Check the error logs
- Usually it's a typo in file paths

### Problem: APK won't install on phone
**Solution**:
- Go to Settings → Security → Unknown sources
- Enable installation from unknown sources
- Try installing again

---

## 🎯 **Quick Command Summary:**

```powershell
cd C:\Users\Lenovo\Desktop\Sandesh\android-app
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/voice-command-app.git
git branch -M main
git push -u origin main
```

---

## 📝 **Next Steps After Installation:**

1. Open the app
2. Grant microphone permission
3. Tap "Start"
4. Say any of these commands:
   - **"open YouTube"**
   - **"open Gmail"**
   - **"open WhatsApp"**
   - **"open Instagram"**
   - **"play Despacito"**
   - **"call Mom"**

The apps will open DIRECTLY without Play Store! 🚀

---

**Ready to start? Let me know if you need help with any step!**
