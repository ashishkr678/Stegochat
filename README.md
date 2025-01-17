Work is on Progress....
Site Map for StegoChat
1. Authentication Pages (Public)
•	Login Page:
o	Fields: Username, Password.
o	Features:
	"Forgot Password?" (Java-based OTP/email recovery).
	"Remember Me" checkbox.
	Security:
	JWT Authentication: The backend issues a JWT token upon successful login.
	Two-Factor Authentication (2FA): Email-based or OTP-based second step.
•	Sign-Up Page:
o	Fields: Full Name, Username, Email, Password, Confirm Password.
o	Features:
	Username availability check (via Java backend).
	Terms & Conditions checkbox.
	CAPTCHA verification.
	Security:
	Password encryption before storing using BCrypt or PBKDF2 (Java).
________________________________________
2. Profile Page (Personal & Friends')
•	Personal Profile:
o	Editable fields: Profile picture, bio, contact info.
o	Activity log: Shared images, audio, and chats.
o	Security:
	JWT Authorization: Protects routes by validating the JWT token passed from the frontend.
•	Friend Profile:
o	View friend's bio, mutual friends, and shared activity.
o	"Unfriend" button.
________________________________________
3. Search Friends Page
•	Purpose: Search for other users by username.
•	Features:
o	Search bar with live suggestions (React frontend querying Java backend).
o	Send friend requests via Java-secured APIs.
o	Status: "Request Sent" or "Add Friend."
o	Security:
	Rate Limiting: Ensure API requests are rate-limited to prevent abuse.
	Input Validation: Ensure user input is validated and sanitized to prevent SQL Injection and other common attacks.
________________________________________
4. Friend Request Management Page
•	Purpose: Handle incoming and outgoing friend requests.
•	Sections:
o	Received Requests: Accept or decline requests.
o	Sent Requests: Cancel requests.
o	Security:
	Role-based Access Control (RBAC): Ensure only authorized users can manage requests.
	Session Management: Ensure that a user’s session is valid before performing any request.
________________________________________
5. Chat Page
•	Default Mode: Text-to-Text Chat (End-to-End Encrypted).
•	Features:
o	Chat list: All friends with recent chats.
o	Chat Window:
	Toggle between text, stego-image, and stego-audio modes.
	Send/receive encrypted text, images, or audio.
o	Security:
	End-to-End Encryption: Messages and media are encrypted using AES-256 for the content and RSA for key exchange.
•	Stego Features:
o	Sender embeds messages into an image/audio and sends.
o	Receiver decodes the message directly in the chat.
o	Steganography: Media files are encoded using a Java backend and decoded via Python.
________________________________________
6. Media Decoder Tool (Private)
•	Purpose: Decode saved stego-images or audio.
•	Access Control:
o	Only the recipient can decode media files.
o	Tool verifies file ownership before decoding.
o	Security:
	File Integrity: Ensure that files have not been tampered with during transit via HMAC (Hash-based Message Authentication Code).
•	Features:
o	Upload image/audio files.
o	Enter decryption key (optional).
o	Display extracted message or file.
________________________________________
7. Notifications Page
•	Sections:
o	Friend Requests.
o	New Messages.
o	Alerts for shared media.
o	Security:
	Notifications are encrypted and securely delivered via WebSockets.
________________________________________
8. Settings Page
•	Sections:
o	Account Settings: Change username, email, or password.
o	Privacy Settings:
	Block users.
	Control who can send friend requests.
o	Stego Preferences: Default toggle for text/image/audio in chats.
o	AI Preferences: Enable/Disable AI features like summarization and sentiment analysis.
________________________________________
9. Logout
•	Purpose: Log out and clear session data.
•	Feature: Prompt for confirmation before logging out.
________________________________________
Java (Security) Features
1. Authentication
•	JWT Tokens:
o	Access Token: Issued on successful login, valid for a limited time.
o	Refresh Token: Used to obtain a new access token without requiring re-login.
o	JWT Validation: Ensures that only valid tokens can access restricted resources.
2. Encryption & Decryption
•	AES-256: Used for encrypting messages, stego-media, and files during transmission.
•	RSA: Utilized for public-key encryption during message key exchanges.
•	BCrypt: For hashing user passwords before storing them in the database.
•	Secure File Storage: Java backend ensures secure storage of stego-media, ensuring it is accessible only to the recipient.
3. Secure APIs
•	Rate Limiting: To prevent abuse of endpoints, especially during friend requests or searching for users.
•	Session Management: Ensure that sessions are tracked and unauthorized access is prevented.
•	Input Validation: Validate and sanitize all user inputs to avoid security vulnerabilities like SQL Injection or XSS (Cross-site Scripting).
4. User Authorization
•	Role-Based Access Control (RBAC): Ensures that only authorized users can access sensitive features (e.g., admin panel or file uploads).
•	Two-Factor Authentication (2FA): Adds an extra layer of security for users via OTP-based verification.
________________________________________
Python (AI Models) (Optionals)
1. AI Features
•	Text Summarization:
o	Use models like GPT or BERT to generate concise summaries of chat conversations.
o	Input: Full chat history.
o	Output: A summarized version of the conversation, stored in the user’s profile.
•	Sentiment Analysis:
o	Perform sentiment analysis on incoming messages to tag them as positive, neutral, or negative.
o	Input: Text message.
o	Output: Sentiment tags displayed next to the message.
•	Interesting Message Suggestions:
o	Use GPT models to suggest engaging responses based on the conversation’s context.
o	Input: The current message history.
o	Output: Suggested reply displayed in the chat interface.
2. Steganography Encoding/Decoding
•	Stego-Image/Audio:
o	Python is used for encoding and decoding messages hidden in images or audio files (leveraging libraries like Stegano for image or pydub for audio).
o	Encoding: The message is embedded into the media file.
o	Decoding: The receiver extracts the hidden message from the media file using the Python backend.
3. Model Integration
•	API Endpoints: Python models will be exposed as RESTful API services (using Flask or FastAPI) to be consumed by the frontend.
•	Communication: The frontend sends requests to the Python API, which processes the data and returns results in real time.

