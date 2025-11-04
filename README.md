# 🧸 Creche Management App

## 📖 Overview
The **Creche Management App** is an **Android application** designed to **digitize the traditional paper-based systems** used in creches and early childhood centers.  
It replaces manual record keeping with a **centralized, secure, and user-friendly mobile platform** for managing children’s information, attendance, communication, events, and payments.

By moving away from paper registers and handwritten notes, this system improves efficiency, reduces human error, ensures accurate data management, and enhances communication between **administrators and parents**.

---

## 🧩 Key Features

### 👩‍💼 Admin Module
- **Dashboard Overview:** Quickly view total parents, attendance stats, and messages.  
- **User Management:** Create, update, or delete parent accounts securely.  
- **Attendance Tracking:** View live and historical attendance records from any day.  
- **Messaging Oversight:** Monitor parent–creche conversations.  
- **Event Creation:** Schedule and share upcoming events such as trips, meetings, or holidays.  

📋 *The Admin module gives full control of creche operations, removing the need for paper logs and binders.*

---

### 👨‍👩‍👧 Parent Module
- **Profile Access:** View your child’s details such as age, allergies, and attendance history.  
- **Event Viewing:** See all upcoming events directly within the app.  
- **Built-In Messaging:** Chat directly with creche staff through the app.  
- **Secure Payments:** Pay monthly fees or event costs via **PayFast integration**.  

📱 *Parents stay informed and connected through one convenient mobile app.*

---

### 🕒 Attendance Module
- **Digital Check-In & Check-Out:** Admins can mark attendance using a clean digital interface.  
- **Automatic Date & Time Stamps:** Each entry records exact check-in/out times.  
- **Error-Free Reporting:** Reduces mistakes common in manual registers.  
- **Cloud Sync:** Attendance data is automatically uploaded to Firebase Firestore.  

🧾 *Attendance management is now fully paperless, efficient, and backed up online.*

---

### 💬 Messaging System
- **Real-Time Chat:** Parents and admins can exchange messages instantly.  
- **Offline Handling:** If internet is lost, messages show a clear failure notice—no crashes.  
- **Firebase Integration:** All messages are securely stored in **Firestore**.  
- **Clean Chat Interface:** Simple, intuitive design for daily communication.  

💬 *This replaces paper notes or third-party chat tools with a built-in communication system.*

---

### 🎉 Events Module
- **Create and Manage Events:** Admins can post events with title, date, and description.  
- **View Upcoming Events:** Parents can see all upcoming activities directly in the app.  

📅 *No more printed newsletters—just instant, digital updates.*

---

### 💳 PayFast Payments Integration
- **Secure Payments:** Parents can make fee or event payments through **PayFast**, South Africa’s trusted payment gateway.  
- **Automatic Confirmation:** Transactions update instantly in Firestore.  
- **Digital Receipts:** Payment history is stored electronically for both parties.  
- **Transparency & Safety:** All financial data handled via encrypted PayFast APIs.  

💰 *Financial management becomes traceable, fast, and paper-free.*

---

## 🗄️ Database Overview

The app uses **Firebase Cloud Firestore** as its main **real-time NoSQL database**.  
All data is stored in structured collections and synchronized instantly across devices.

### 🔥 Firestore Collections:

#### 👥 Users
Stores both parent and admin profiles.

**Fields Example:**
```json
{
  "address": "12 nowhere street",
  "allergyDetails": "Pollen",
  "childDob": "14-03-2004",
  "childGender": "Male",
  "childName": "Teshar",
  "email": "justeen@test.com",
  "hasAllergies": "Yes",
  "parentName": "Justeen",
  "phoneNumber": "0622345627",
  "role": "parent",
  "uid": "2j5zjEy9EAYdaMBh1NnZ5gDA7353"
}
````

---

#### 🕒 Attendance

Stores daily attendance records.

**Example Fields:**

```json
{
  "childName": "Teshar",
  "date": "2025-03-14",
  "status": "Present",
  "timestamp": "2025-03-14T08:05:00"
}
```

---

#### 💬 Messages

Stores chat messages between parents and admins.

**Example Fields:**

```json
{
  "senderId": "2j5zjEy9EAYdaMBh1NnZ5gDA7353",
  "receiverId": "admin123",
  "message": "Good morning! How was Teshar today?",
  "timestamp": "2025-03-14T14:32:00"
}
```

---

#### 🎉 Events

Stores all events posted by the admin.

**Example Fields:**

```json
{
  "eventTitle": "Sports Day",
  "description": "Annual fun sports day for kids and parents!",
  "date": "2025-04-12"
}
```

---

#### 💳 Payments

Stores PayFast payment records.

**Example Fields:**

```json
{
  "parentId": "2j5zjEy9EAYdaMBh1NnZ5gDA7353",
  "amount": 1200,
  "reference": "INV-2025-001",
  "status": "Completed"
}
```

---

🔐 **Firebase Authentication** ensures that only verified users (Admins and Parents) can access their respective data and functionalities.

---

## ⚙️ Technology Stack

| Component              | Technology                           |
| ---------------------- | ------------------------------------ |
| **Language**           | Kotlin                               |
| **IDE**                | Android Studio                       |
| **Backend & Database** | Firebase Authentication + Firestore  |
| **Payments**           | PayFast API Integration              |
| **UI Design**          | XML Layouts with Material Components |

---

## 🧠 Installation & Setup

### 🔧 Prerequisites

* [Android Studio](https://developer.android.com/studio)
* A [Firebase](https://firebase.google.com/) account
* A [PayFast Merchant Account](https://www.payfast.co.za/)
* Git installed on your system

---

### 🪄 Steps to Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/ST10294145/CrecheManagement_App.git

# 2. Open the project in Android Studio

# 3. Connect Firebase
#   - Go to Tools > Firebase > Authentication and Firestore
#   - Link the app with your Firebase project

# 4. Configure PayFast
#   - Add your merchant credentials to your PayFast configuration file

# 5. Build and run
#   - Select an Android device or emulator
#   - Click Run ▶️ in Android Studio
```
---

## 👨‍💻 Developers

**Code Core Corporation Team**  
Built with ❤️ using **Kotlin**, **Firebase**, and **PayFast**.

