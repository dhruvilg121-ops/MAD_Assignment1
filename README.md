# Women Safety App 🚨

An Android application developed using **Kotlin** and **Android Studio** for Mobile Application Development (MAD) Assignment 1. The app serves as a rapid emergency utility configured to sound loud safety alarms, retrieve real-time GPS locations, send automated SOS text messages, and provide quick dial access to official national helpline services.

---

## 📱 Features

1. **One-Tap Emergency SOS Trigger**: 
   - Sounds a loopable, high-volume emergency alarm track.
   - Quietly retrieves real-time GPS coordinates (Latitude & Longitude) in the background.
   - Automatically dispatches an SMS message containing a Google Maps navigation link to your saved contact.
   - Triggers an instant popup window prompting the user to quickly place a phone call to the contact.

2. **Instant Loud Siren**:
   - A quick-access toggle directly from the primary dashboard to draw immediate bystander attention or deter external threats.
   - Forces device volume to maximum levels on activation.

3. **Rapid Emergency Helplines**:
   - Pre-configured, one-click launcher buttons to pre-fill the native Android dialer with national support networks:
     - **Police**: 112
     - **Ambulance**: 102
     - **Women Helpline**: 1091

4. **Dedicated Contact Manager**:
   - Built-in form processing screen to securely assign, review, or wipe the saved primary emergency contact details.

---

## 🛠️ Technical Frameworks Used

- **Language & Ideation**: Kotlin, Android SDK 14 (API Level 34+ target layout).
- **Data Cache Storage**: `SharedPreferences` to persistently log, update, and fetch the user's primary emergency contact details without a structural database.
- **Location Processing**: Integration with Android's system `LocationManager` querying active `GPS_PROVIDER` and `NETWORK_PROVIDER` coordinates.
- **Telephony Services**: Utilizes `SmsManager`'s multipart messaging interface (`sendMultipartTextMessage`) to ensure text packets containing links avoid cellular truncation.
- **Audio Operations**: Employs loop-enabled `MediaPlayer` pipelines managed dynamically alongside Android system `AudioManager` streams.

---

## 🔒 Runtime Permissions

The app securely requests user confirmation for the following permissions during system initialization:
- `Manifest.permission.SEND_SMS` (To automatically dispatch the SOS location message)
- `Manifest.permission.ACCESS_FINE_LOCATION` (To obtain accurate GPS location details)
- `Manifest.permission.ACCESS_COARSE_LOCATION` (Network location fallback)
- `Manifest.permission.CALL_PHONE` (To process the quick call triggers)

---

## 📂 Key Architecture Modules

```text
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/example/mad_assignment1
│   │   │   │   ├── MainActivity.kt        # Primary dashboard handling alarms, calls, and location SOS
│   │   │   │   └── AddContactActivity.kt  # Activity for adding/clearing emergency contact details
│   │   │   ├── res
│   │   │   │   ├── layout                 # view layout structures (activity_main.xml, activity_add_contact.xml)
│   │   │   │   ├── raw                    # high-decibel alarm.mp3 audio file
│   │   │   │   └── values                 # application strings.xml, colors.xml, and visual themes
│   │   │   └── AndroidManifest.xml        # Hardware permission declarations and workspace layout definitions
└── README.md                              # Project documentation
```

---
## 👤 Developer Details
- **Name**: Dhruvil Gandhi
- **Enrollment Number**: 25012012005


