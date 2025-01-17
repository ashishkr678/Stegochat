# StegoChat - Work In Progress 🚧

StegoChat is a secure chat application with advanced features like steganography and AI-powered tools for enhanced communication. Below is a brief overview of its structure and features.

---

## **1. Authentication Pages (Public)**
- **Login Page**
  - Fields: Username, Password.
  - Features:
    - "Forgot Password?" (OTP/Email recovery).
    - "Remember Me" checkbox.
  - **Security:**
    - JWT Authentication (Access and Refresh Tokens).
    - Two-Factor Authentication (2FA) via Email/OTP.
- **Sign-Up Page**
  - Fields: Full Name, Username, Email, Password, Confirm Password.
  - Features:
    - Username availability check.
    - Terms & Conditions checkbox.
    - CAPTCHA verification.
  - **Security:**
    - Password encryption using BCrypt.

---

## **2. Profile Pages**
- **Personal Profile**
  - Editable: Profile picture, bio, contact info, activity log.
  - **Security:** JWT-protected routes.
- **Friend Profile**
  - View: Friend’s bio, mutual friends, shared activity.
  - Action: "Unfriend."

---

## **3. Search Friends Page**
- **Purpose:** Search for users by username.
- **Features:**
  - Search bar with live suggestions.
  - Send friend requests via secure APIs.
  - Request Status: "Request Sent" or "Add Friend."
- **Security:**
  - Rate Limiting to prevent API abuse.
  - Input Validation to prevent SQL Injection.

---

## **4. Friend Request Management Page**
- **Sections:**
  - **Received Requests:** Accept or Decline.
  - **Sent Requests:** Cancel requests.
- **Security:**
  - Role-Based Access Control (RBAC).
  - Valid session checks before requests.

---

## **5. Chat Page**
- **Default Mode:** Text-to-Text Chat (End-to-End Encrypted).
- **Features:**
  - Chat list displaying friends with recent chats.
  - Chat Window:
    - Toggle between text, stego-image, and stego-audio modes.
    - Send/receive encrypted text, images, or audio.
- **Security:**
  - End-to-End Encryption (AES-256 for content, RSA for key exchange).
- **Stego Features:**
  - Embed and decode messages hidden in images/audio (Java backend for encoding, Python for decoding).

---

## **6. Media Decoder Tool (Private)**
- **Purpose:** Decode saved stego-images or audio files.
- **Features:**
  - Upload image/audio files for decoding.
  - Option to enter decryption key.
  - Displays extracted message or file.
- **Security:**
  - File Integrity validation via HMAC.
  - Restricted access to recipients only.

---

## **7. Notifications Page**
- **Sections:**
  - Friend Requests.
  - New Messages.
  - Alerts for shared media.
- **Security:** Encrypted and securely delivered via WebSockets.

---

## **8. Settings Page**
- **Sections:**
  - Account Settings: Change username, email, or password.
  - Privacy Settings: Block users, control friend request permissions.
  - Stego Preferences: Default toggle for text/image/audio chats.
  - AI Preferences: Enable/Disable AI features (e.g., summarization, sentiment analysis).

---

## **9. Logout**
- **Purpose:** Securely log out and clear session data.
- **Feature:** Prompt confirmation before logout.

---

## **10. Security Features (Java)**
- **Authentication:**
  - JWT Tokens for Access and Refresh.
  - Secure Password Hashing (BCrypt).
- **Encryption:**
  - AES-256 for messages and media.
  - RSA for public-key encryption.
- **Secure APIs:**
  - Rate Limiting and Input Validation.
  - Session Management and RBAC.

---

## **11. AI Features (Python)**
- **Optional:**
  - Text Summarization (e.g., GPT/BERT models).
  - Sentiment Analysis for incoming messages.
  - Interesting Message Suggestions using GPT models.
- **Steganography:**
  - Encoding/decoding messages in images/audio (Stegano, pydub libraries).
- **Integration:**
  - RESTful APIs using Flask/FastAPI.

---
