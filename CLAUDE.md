# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

StudentHandbook is a native Android application for Bulacan State University (BULSU) that provides students with essential university information through an interactive dashboard interface. The app is built using Java and Android SDK with Material Design components.

## Build & Run Commands

### Build the application
```bash
./gradlew build
```

### Run unit tests
```bash
./gradlew test
```

### Run instrumentation tests
```bash
./gradlew connectedAndroidTest
```

### Clean build
```bash
./gradlew clean
```

### Build debug APK
```bash
./gradlew assembleDebug
```

### Build release APK
```bash
./gradlew assembleRelease
```

### Install on connected device
```bash
./gradlew installDebug
```

## Architecture

### Project Structure
- **Package**: `com.example.studenthandbook`
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Language**: Java 11
- **Build System**: Gradle with Kotlin DSL

### Main Components

**MainActivity** (`app/src/main/java/com/example/studenthandbook/MainActivity.java`)
- Single activity application serving as the entry point
- Displays the main dashboard with categorized information cards
- Uses `dashboard.xml` layout

**Dashboard Layout** (`app/src/main/res/layout/dashboard.xml`)
- Material Design card-based UI using ConstraintLayout
- Bottom navigation bar with floating BULSU logo centerpiece
- Search functionality at the top
- Scrollable content area with categorized sections:
  - About University (History, Hymn and March)
  - Administration and Staff (Faculty, Admin Council, Directory)
  - Academic Information (General Provisions, Course Curriculum, Program Offerings, Academic Regulations)
  - Student Life and Governance (Student Council, Student Affairs, External Affairs, Student Organizations)
  - Institutional and Support Services (Downloadable Forms, Institutional Services)

### Key Dependencies
- AndroidX AppCompat
- Material Design Components
- ConstraintLayout
- CardView
- JUnit for testing
- Espresso for UI testing

## Design System

### Color Scheme
- Primary color: `#00562C` (BULSU green)
- Background: White
- Navigation bar: BULSU green with white icons

### UI Patterns
- CardView components with 12dp corner radius and 3dp elevation
- Bottom navigation with 4 icons and centered floating logo
- Nested ScrollView for main content area
- Cards arranged in 2-column grid layout
- Material Design ripple effects on interactive elements

## Development Guidelines

### Adding New Cards to Dashboard
1. Add new string resources in `app/src/main/res/values/strings.xml`
2. Create card in appropriate section of `dashboard.xml` following the existing CardView pattern
3. Ensure consistent sizing (minHeight="80dp") and styling
4. Add click handlers in `MainActivity.onCreate()`

### Testing
- Unit tests go in `app/src/test/`
- Instrumentation tests go in `app/src/androidTest/`
- Test runner configured: AndroidJUnitRunner

### Git Workflow
Current branch: master
Recent commits show dashboard UI development focus
