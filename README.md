# CrecheConnect - Creche Management App

CrecheConnect is a mobile application built with **Android Studio (Kotlin)** that streamlines day-to-day operations of a creche.  
It provides role-based access for **Admins**, and **Parents**, with features for attendance, events, messaging, and reporting.  
The app is integrated with **Firebase Authentication** and **Firestore** for secure login, role management, and real-time data storage.

---

## 📱 Features

### 🔑 Authentication & Roles
- Firebase Authentication with **role-based accounts**:
  - **Admin**: Registers and manages Parent accounts, Manages attendance, posts events, and sends messages.
  - **Parents**: View child attendance, events, and receive messages.

### 👩‍🏫 Admin
- Create new Staff and Parent accounts.
- View attendance and reports.
- Manage overall system activities.
- Mark and view **attendance** for children.
- Post **events** (activities, announcements).
- Send and receive **messages** with parents.

### 👪 Parents
- View child’s **attendance history**.
- See **upcoming events**.
- Read and send **messages** to admin.

---

## 🛠️ Tech Stack

- **Android Studio** with Kotlin
- **Firebase Authentication** – secure login & account management
- **Firestore Database** – storage of attendance, events, messages, and products
- **RecyclerView** – display lists (attendance records, messages, events, products)
- **ConstraintLayout** – responsive UI design



