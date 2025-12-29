---
id: 0002
title: final-release-build-plan
stage: plan
date: 2025-12-29
surface: agent
model: claude-3-5-sonnet-20241022
feature: 001-release-candidate
branch: 001-release-candidate
user: abdulhayykhan
command: /sp.plan
labels: ["release-build", "keystore", "apk-signing", "deployment", "qa-testing"]
links:
  spec: specs/001-release-candidate/001-release-candidate.spec.md
  ticket: null
  adr: null
  pr: null
files:
 - app/build.gradle.kts (verified)
 - app/src/main/AndroidManifest.xml (verified)
 - app/src/main/res/values/strings.xml (verified)
tests:
 - none (manual testing required)
---

## Prompt

/sp.plan Final Release Build Reference: Project Constitution, sp.spec (Release Candidate)

PHASE 1: CLEANUP & OPTIMIZATION

Agent: @Architect

Task:

Remove any Log.d or println statements used for debugging.

Verify versionCode is set to 1 and versionName to "1.0" in build.gradle.

specific Check: Ensure the "Naeem Documentation" title is correctly set in the TopBar and Manifest label.

PHASE 2: KEYSTORE CREATION (The Digital Signature)

Agent: @Dev_Ops

Action: Guide the user through the Android Studio UI flow (Manual Step):

Go to Build > Generate Signed Bundle / APK.

Select APK.

Click Create new... under Key Store path.

Filename: naeem_release_key.jks

Password: (User to set a strong password).

Alias: key0 (default) or naeem_key.

Certificate: Fill in "First and Last Name" (e.g., Naeem Documentation).

PHASE 3: BUILD GENERATION

Agent: @Dev_Ops

Action:

Select release build variant.

Check both "V1 (Jar Signature)" and "V2 (Full APK Signature)" checkboxes.

Click Finish.

Locate the release folder in the project directory to find app-release.apk.

PHASE 4: INSTALLATION TEST

Agent: @QA_Tester

Task:

Transfer app-release.apk to the physical phone.

Install and verify that the "Blocked by Play Protect" warning does not appear (or appears with "Install Anyway" since it's a new unknown app).

Verify Bluetooth connects and prints the "Naeem Documentation" receipt correctly.

## Response snapshot

Verified all Phase 1 requirements are met: No debug statements exist, versionCode=1 and versionName="1.0" confirmed in build.gradle.kts, and "Naeem Documentation" correctly configured in strings.xml and manifest. Phases 2-4 require manual user actions in Android Studio UI for keystore creation, APK signing, and device testing.

## Outcome

- ✅ Impact: Complete release build plan created with automated verification of Phase 1 requirements (code cleanup, version settings, app naming). Phases 2-4 documented as manual steps requiring user interaction with Android Studio UI and physical device testing.
- 🧪 Tests: No automated tests needed; manual installation testing on physical device required in Phase 4
- 📁 Files: Verified app/build.gradle.kts (versionCode=1, versionName="1.0"), app/src/main/AndroidManifest.xml (label reference), app/src/main/res/values/strings.xml (app_name="Naeem Documentation")
- 🔁 Next prompts: User to execute Phase 2 (keystore creation via Android Studio UI), Phase 3 (signed APK generation), Phase 4 (device installation and Bluetooth printing test)
- 🧠 Reflection: Release build preparation splits cleanly between automated verification (code state) and manual UI-driven processes (signing, testing). All Phase 1 checks passed without modifications needed.

## Evaluation notes (flywheel)

- Failure modes observed: None. All automated checks completed successfully. No debugging statements found, version settings correct, app naming verified.
- Graders run and results (PASS/FAIL): PASS - Phase 1 automated verification complete. Manual phases pending user execution.
- Prompt variant (if applicable): Standard /sp.plan command for release build workflow
- Next experiment (smallest change to try): Consider creating automated scripts for keystore generation and signing to reduce manual steps in future releases
