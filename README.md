# 📱 Battery Status Monitor App

A simple Android application built using **Kotlin** that displays:

- 🔋 Battery Percentage  
- ⚡ Charging / Discharging Status  

This app uses **native Android APIs** (`BatteryManager` + `ACTION_BATTERY_CHANGED`) and **no third-party plugins**, as required.

---

## 🚀 Features

- Manual button to fetch battery info  
- Shows battery percentage in real-time  
- Detects charging or discharging  
- Uses modern `BatteryManager` API (Android 5.0+)  
- Fallback logic for OEMs that return invalid battery values  
- Lightweight XML-based UI  

---

## 🛠 Tech Stack

- **Kotlin**
- **Android Studio**
- **XML Layouts**
- **BatteryManager API**
- **BroadcastReceiver (Intent Filter)**
