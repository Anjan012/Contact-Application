# Smart Contacts

**Smart Contacts** is a modern Android mobile application developed for efficient contact management. It allows users to add, view, edit, search, and delete contacts with a clean and intuitive Material Design 3 interface.

### Introduction

This project, **"Smart Contacts"**, was created and submitted in partial fulfillment of the requirements for the course **Mobile Programming (BCA 6th Semester)**.  

As per the course requirements, all laboratory works were completed and a mini project was developed using the core Android concepts learned throughout the semester. A reference project was provided by the instructor[](https://github.com/sujan-poudel-03/Smart-Contacts.git) to guide the project structure, implementation of Android concepts, and proper usage of Git version control.

This application strictly follows the minimum scope specified: **CRUD operations** for contacts using **SQLite database**, **RecyclerView** for displaying contact list, and **activity-based navigation**. The project demonstrates practical application of key topics such as Material Design components, background threading, form validation, intent handling, and clean code practices.

Special emphasis was given to using **proper Git practices** with meaningful and phased commits to clearly show the development process.

---

## ✨ Features

- ✅ Add new contacts with proper validation
- ✅ Edit existing contacts
- ✅ View detailed contact information with beautiful collapsing toolbar
- ✅ Delete contacts with confirmation dialog
- ✅ Real-time search (by name, company, or phone number)
- ✅ Click-to-call and click-to-email functionality
- ✅ Colorful avatar initials with 5 different background colors
- ✅ Empty state handling with friendly messages
- ✅ Smooth scrolling experience with FAB shrink/expand effect
- ✅ Material Design 3 components and modern UI/UX

## 📱 Screenshots
<img width="307" height="680" alt="image" src="https://github.com/user-attachments/assets/669d6ece-667c-4fc9-a7ba-b327d0511097" />
screenshots/main.png) 

<img width="301" height="678" alt="image" src="https://github.com/user-attachments/assets/ca682d98-04e3-4abc-b168-f8be979289ae" />
(screenshots/add.png) 

<img width="308" height="678" alt="image" src="https://github.com/user-attachments/assets/c1756b7b-7321-4594-b906-21c3b1a29590" />
(screenshots/userDetail.png) 

## 🛠️ Technologies Used

- **Language**: Java
- **Platform**: Android
- **UI Framework**: Material Design 3
- **Database**: SQLite
- **Architecture**: Multi-Activity + RecyclerView + Adapter Pattern
- **Threading**: Custom `AppExecutor` (Background + Main Thread)
- **IDE**: Android Studio

### Dependencies
- Material Components (`com.google.android.material:material`)
- RecyclerView
- AppCompat
- CardView

## 📁 Project Structure
<img width="321" height="397" alt="image" src="https://github.com/user-attachments/assets/bc40d81a-7f4d-4ced-be65-7d3ba74a1047" />


## 🚀 Key Implementation Highlights

- **MVVM-like separation** using Activities, Model, Adapter, and Database Helper
- **Background thread execution** using `AppExecutor` to prevent UI blocking
- **Live avatar preview** in Add/Edit screen using `TextWatcher`
- **Deterministic avatar colors** based on contact ID
- **Real-time search** with `SearchView` and `onQueryTextChange`
- **Input validation** (First name & Phone required, Email format check)
- **Intent handling** for calling and sending email directly from app

## 🗄️ Database Schema

<img width="903" height="228" alt="image" src="https://github.com/user-attachments/assets/6a759819-b964-4417-b32d-0faaaf45e1c0" />
(contactTableimage.png)

**Table**: `contacts`
- `id` → INTEGER PRIMARY KEY AUTOINCREMENT
- `first_name` → TEXT NOT NULL
- `last_name` → TEXT
- `company` → TEXT
- `phone` → TEXT NOT NULL
- `email` → TEXT

## 📋 How to Run the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/Anjan012/Contact-Application.git

Open the project in Android Studio
Wait for Gradle sync to complete
Run the app on an emulator or physical device (Minimum SDK: Android 5.0+)

📌 Future Enhancements

Add support for contact photos/images
Dark mode support
Export contacts as CSV or vCard
Import contacts from phone book
Favorite contacts section
Backup and restore functionality
Cloud synchronization (Firebase)

🎯 Learning Outcomes
This project helped me understand:

Working with SQLite database in Android
RecyclerView and custom adapters
Material Design 3 components
Proper threading practices (avoiding ANR)
Form validation and user experience design
Intent handling for implicit actions (call & email)


Developed by
Anjan khadka
Tribhuwan University (Roll no): 113102067
Swastik College
Mini Project – Smart Contacts
Submitted in April 2026
