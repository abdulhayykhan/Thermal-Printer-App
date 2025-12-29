# How to Install Gradle on Ubuntu

## Prerequisites

Before installing Gradle, ensure you have Java installed:

```bash
java -version
```

If Java is not installed, install it first:

### Install Java 11+ (Recommended)

```bash
# Update package lists
sudo apt update

# Install OpenJDK 11 (recommended for Android development)
sudo apt install -y openjdk-11-jdk

# Or install OpenJDK 17 (newer alternative)
sudo apt install -y openjdk-17-jdk

# Verify installation
java -version
javac -version
```

Expected output:
```
openjdk version "11.0.x" 2024-...
OpenJDK Runtime Environment (build 11.0.x+...)
OpenJDK 64-Bit Server VM (build 11.0.x+..., mixed mode, sharing)
```

---

## Option 1: Install Gradle via apt (Easiest)

### Step 1: Update Package Lists
```bash
sudo apt update
```

### Step 2: Install Gradle
```bash
sudo apt install -y gradle
```

### Step 3: Verify Installation
```bash
gradle --version
```

Expected output:
```
------------------------------------------------------------
Gradle 8.x.x
------------------------------------------------------------
...
```

**Pros**: Easy, automatic updates
**Cons**: May not be the latest version available

---

## Option 2: Install Gradle Manually (Recommended for Android Development)

This gives you control over the exact version (8.0 or newer recommended).

### Step 1: Download Gradle

```bash
# Create a directory for gradle (optional, but recommended)
mkdir -p ~/gradle-installs

# Download Gradle 8.0 (or latest version)
cd ~/gradle-installs
wget https://services.gradle.org/distributions/gradle-8.0-bin.zip

# Or use curl if wget is not available
curl -o gradle-8.0-bin.zip https://services.gradle.org/distributions/gradle-8.0-bin.zip
```

**Alternative versions**:
- Gradle 8.7 (latest): `https://services.gradle.org/distributions/gradle-8.7-bin.zip`
- Gradle 8.5: `https://services.gradle.org/distributions/gradle-8.5-bin.zip`

### Step 2: Extract Gradle

```bash
# Extract to /opt (requires sudo)
sudo unzip ~/gradle-installs/gradle-8.0-bin.zip -d /opt/

# Or extract to home directory (no sudo needed)
unzip ~/gradle-installs/gradle-8.0-bin.zip -d ~/gradle-installs/
```

### Step 3: Create Symbolic Link (Optional but recommended)

```bash
# If extracted to /opt/
sudo ln -s /opt/gradle-8.0 /opt/gradle

# If extracted to home directory
ln -s ~/gradle-installs/gradle-8.0 ~/gradle-installs/gradle
```

### Step 4: Set Environment Variables

Edit your shell profile to add Gradle to PATH:

**For bash** (usually `~/.bashrc`):
```bash
nano ~/.bashrc
```

**For zsh** (usually `~/.zshrc`):
```bash
nano ~/.zshrc
```

Add these lines at the end:
```bash
# Gradle
export GRADLE_HOME=/opt/gradle-8.0
# OR if installed in home directory:
# export GRADLE_HOME=$HOME/gradle-installs/gradle-8.0

export PATH=$GRADLE_HOME/bin:$PATH
```

### Step 5: Reload Shell Configuration

```bash
# For bash
source ~/.bashrc

# For zsh
source ~/.zshrc

# Or just restart terminal
```

### Step 6: Verify Installation

```bash
gradle --version
```

Expected output:
```
------------------------------------------------------------
Gradle 8.0
------------------------------------------------------------
Build time:   2023-02-14 20:35:47 UTC
Revision:     a0d811a10ab6f4b6d923fc098f2f74f8f67ab932
Kotlin:       1.8.10
Groovy:       3.0.13
Ant:          Apache Ant(TM) version 1.10.11 compiled on July 10 2021
JVM:          11.0.x (...)
OS:           Linux x.x.x amd64
```

---

## Option 3: Use Gradle Wrapper in Project (Recommended for this project)

Instead of installing Gradle globally, use the **Gradle wrapper** bundled with the project.

### Why Use Gradle Wrapper?
- ✅ Project-specific version (no conflicts)
- ✅ No global installation needed
- ✅ Automatic download on first use
- ✅ Reproducible builds across different machines

### Generate Gradle Wrapper

If the project doesn't have a wrapper:

```bash
cd ~/projects/thermal-printer-app

# Using globally installed gradle
gradle wrapper --gradle-version 8.0

# Or manually download wrapper files
mkdir -p gradle/wrapper
curl -o gradle/wrapper/gradle-wrapper.jar \
  https://services.gradle.org/distributions/gradle-8.0-bin.zip

# Download wrapper scripts
curl -o gradlew \
  https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.sh

curl -o gradlew.bat \
  https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradlew.bat

chmod +x gradlew
```

### Use Gradle Wrapper

Once created, use `./gradlew` instead of `gradle`:

```bash
./gradlew --version
./gradlew clean
./gradlew assembleRelease
```

---

## Verification Checklist

After installation, verify everything works:

```bash
# Check Java
java -version
# Expected: OpenJDK 11 or higher

# Check Gradle
gradle --version
# Expected: Gradle 8.0 or higher

# Check GRADLE_HOME (if manually installed)
echo $GRADLE_HOME
# Expected: /opt/gradle-8.0 or ~/gradle-installs/gradle-8.0

# Check PATH
which gradle
# Expected: /opt/gradle-8.0/bin/gradle or ~/gradle-installs/gradle-8.0/bin/gradle
```

---

## Next Steps for Your Project

Now that Gradle is installed:

```bash
cd ~/projects/thermal-printer-app

# Generate Gradle wrapper (if not already present)
gradle wrapper --gradle-version 8.0

# Clean build
./gradlew clean

# Build release APK
./gradlew assembleRelease --info
```

---

## Troubleshooting

### Issue: "gradle: command not found"

**Solution 1**: Install via apt
```bash
sudo apt update
sudo apt install -y gradle
```

**Solution 2**: Verify PATH is set correctly
```bash
# If manually installed
echo $GRADLE_HOME
# Should show /opt/gradle-8.0 or similar

# If empty, add to ~/.bashrc or ~/.zshrc and reload
export GRADLE_HOME=/opt/gradle-8.0
export PATH=$GRADLE_HOME/bin:$PATH
```

**Solution 3**: Reload shell
```bash
source ~/.bashrc  # or source ~/.zshrc
```

### Issue: "Java not found"

**Solution**: Install Java first
```bash
sudo apt update
sudo apt install -y openjdk-11-jdk
java -version
```

### Issue: "Permission denied" when running gradlew

**Solution**: Make wrapper executable
```bash
chmod +x gradlew
./gradlew --version
```

### Issue: "gradle-wrapper.jar not found"

**Solution**: Generate wrapper again
```bash
cd ~/projects/thermal-printer-app
gradle wrapper --gradle-version 8.0
```

### Issue: Build fails with "Unsupported Java version"

**Solution**: Ensure Java 11+ is installed
```bash
java -version
# Must be 11 or higher

# If using Java 8, upgrade
sudo apt install -y openjdk-11-jdk
sudo update-alternatives --config java
# Select Java 11
```

---

## Environment Variables Reference

After installation, these variables should be set:

```bash
# View all Java/Gradle related variables
env | grep -E "JAVA|GRADLE"

# Should show:
# GRADLE_HOME=/opt/gradle-8.0 (or your installation path)
# JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 (or your JDK path)
```

---

## Recommended: Installation Summary for Your System

For Ubuntu with this project, here's the recommended sequence:

```bash
# 1. Install Java
sudo apt update
sudo apt install -y openjdk-11-jdk

# 2. Install Gradle (via apt is easiest)
sudo apt install -y gradle

# 3. Verify
java -version
gradle --version

# 4. Navigate to project
cd ~/projects/thermal-printer-app

# 5. Generate wrapper (if needed)
gradle wrapper --gradle-version 8.0

# 6. Use wrapper for builds
./gradlew clean assembleRelease --info
```

---

## Resources

- [Gradle Official Download Page](https://gradle.org/releases/)
- [Gradle Installation Guide](https://gradle.org/install/)
- [Ubuntu Java Installation](https://ubuntu.com/tutorials/install-jdk-ubuntu)
- [Android Development Prerequisites](https://developer.android.com/studio/install)

---

**Last Updated**: 2025-12-29
