---
id: 0001
title: Implementation Plan for Receipt Printer
stage: plan
date: 2025-12-29T10:00:26.177Z
surface: project
model: claude-3.5-sonnet
feature: 001-quick-amount-receipt-printer
branch: 001-quick-amount-receipt-printer
user: abdi
command: /sp.plan
labels: [planning, android, bluetooth, thermal-printer, clean-architecture]
links:
  spec: specs/001-quick-amount-receipt-printer/spec.md
  ticket: N/A
  adr: N/A
  pr: N/A
files:
  - path: .specify/memory/constitution.md
    action: created
    reason: Defined constitutional principles for Clean Architecture and pure native implementation
  - path: specs/001-quick-amount-receipt-printer/spec.md
    action: created
    reason: Complete feature specification with functional requirements, acceptance tests
  - path: specs/001-quick-amount-receipt-printer/plan.md
    action: created
    reason: Comprehensive implementation plan with phases 0-2, constitution checks
  - path: specs/001-quick-amount-receipt-printer/research.md
    action: created
    reason: Technical research resolving ESC/POS, Bluetooth, permissions decisions
  - path: specs/001-quick-amount-receipt-printer/data-model.md
    action: created
    reason: Complete data model with domain entities, validation rules, state flows
  - path: specs/001-quick-amount-receipt-printer/contracts/bluetooth-service.md
    action: created
    reason: Bluetooth service interface contract with error scenarios
  - path: specs/001-quick-amount-receipt-printer/contracts/receipt-formatter.md
    action: created
    reason: Receipt formatter contract with ESC/POS command specifications
  - path: specs/001-quick-amount-receipt-printer/quickstart.md
    action: created
    reason: Developer quickstart guide with setup, testing, troubleshooting
  - path: .github/agents/copilot-instructions.md
    action: created
    reason: Updated agent context with Kotlin, Android, Jetpack Compose technologies
tests:
  - none yet (planning phase)
---

## Prompt

Create detailed implementation plan for Naeem Documentation App following the strategy provided.

PROJECT CONTEXT:
- Constitution: Android Bluetooth Thermal Receipt Printer with Clean Architecture
- Spec: Quick Amount Receipt Printer with hardcoded receipt format
- Strategy: Pure Native Implementation (No External Printer Libraries)

REQUIRED PHASES:

PHASE 1: FOUNDATION & MANIFEST
Agent: @Architect
- Configure build.gradle (Module) for Jetpack Compose
- Setup AndroidManifest.xml with permissions (Legacy: BLUETOOTH, BLUETOOTH_ADMIN; Android 12+: BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
- Add strings.xml with app_name: "Naeem Documentation"

PHASE 2: BLUETOOTH MANAGER (CORE LOGIC)
Agent: @Protocol_Specialist
- Create BluetoothHelper.kt
- Function: getPairedDevices() -> Returns list of name/MAC
- Function: connectToDevice(macAddress) -> Returns BluetoothSocket
- Persistence: saveLastPrinter(mac) and getLastPrinter() using SharedPreferences
- Logic: On app launch, check saved MAC and attempt background connection

PHASE 3: ESC/POS COMMAND BUILDER
Agent: @Protocol_Specialist
- Create ReceiptBuilder.kt
- Methods: reset(), align(center: Boolean), bold(on: Boolean), text(str: String), largeText(str: String), feed(lines: Int)
- Integration: Map exact receipt layout from spec

PHASE 4: UI IMPLEMENTATION
Agent: @UI_Dev
- Create MainScreen.kt
- State Management: printerStatus (Disconnected, Connecting, Ready), amountInput
- Components: Top Bar, Status Bar (Connected/Disconnected), Input Field (Number pad), Print Button, Device Selection Dialog

PHASE 5: WIRING & PERMISSIONS
Agent: @Architect
- MainActivity.kt with Runtime Permission logic (ActivityResultLauncher)
- Initialize BluetoothHelper
- Wire print() function to UI

ADDITIONAL REQUIREMENTS FROM CONSTITUTION:
- All socket operations on Dispatchers.IO
- Error handling with user-facing Toast/Snackbar
- AndroidManifest declarations
- Thread safety with Coroutines

## Response snapshot

Successfully created comprehensive implementation plan following spec-driven development workflow:

1. **Constitution Created**: Defined 6 core principles (Clean Architecture, Pure Native, Coroutines, Error Handling, Android Best Practices, Simplicity) with technical constraints, performance standards, and governance rules.

2. **Feature Specification**: Complete spec with functional requirements (amount input, printer connection, receipt printing, error handling), non-functional requirements (performance, compatibility, usability), acceptance tests, and UI mockup.

3. **Implementation Plan**: 
   - Technical context documented (Kotlin 1.9+, Jetpack Compose, Android 8.0+)
   - Constitution check passed at initial gate (all 6 principles compliant)
   - Project structure defined following Clean Architecture
   - Phases 0-2 outlined (Research, Design, Tasks)

4. **Phase 0 Research**: Resolved all technical unknowns:
   - ESC/POS: 10 core commands selected (universal compatibility)
   - Bluetooth: SPP UUID, RFCOMM socket pattern
   - Permissions: API-level conditional strategy
   - State management: ViewModel + StateFlow + Compose
   - Error handling: 8 categorized scenarios with user messages

5. **Phase 1 Design**:
   - Data model: 8 entities with validation (PrinterDevice, PrinterStatus, Receipt, PrintResult, MainUiState, PrinterPreferences, ESCPOSCommands, BluetoothConstants)
   - Contracts: BluetoothPrinterService interface, ReceiptFormatter interface
   - Quickstart: Complete developer guide with setup, testing, troubleshooting

6. **Agent Context Updated**: Ran `.specify/scripts/bash/update-agent-context.sh copilot` to add Kotlin, Jetpack Compose, Coroutines technologies.

7. **Constitution Re-check**: Verified post-design compliance (all principles pass, design artifacts validated).

8. **Committed Changes**: 9 files created/modified, feature branch ready for Phase 2 (task breakdown via `/sp.tasks`).

## Outcome

- ✅ Impact: Complete planning deliverables for 001-quick-amount-receipt-printer feature
- 🧪 Tests: No tests yet (planning phase, implementation tests defined in plan)
- 📁 Files: 9 files (constitution, spec, plan, research, data-model, 2 contracts, quickstart, agent-instructions)
- 🔁 Next prompts: Execute `/sp.tasks` to generate tasks.md with atomic implementation tasks
- 🧠 Reflection: Followed SDD workflow precisely, all gates passed, no constitutional violations, ready for implementation

## Evaluation notes (flywheel)

- Failure modes observed: None - workflow executed cleanly
- Graders run and results (PASS/FAIL): Constitution Check (PASS), Technical Context (PASS), Design Compliance (PASS)
- Prompt variant (if applicable): N/A (standard /sp.plan workflow)
- Next experiment (smallest change to try): N/A (proceeding to /sp.tasks)
