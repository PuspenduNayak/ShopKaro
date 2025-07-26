# Shop Karo 🛒

**Shop Karo** is a modern, feature-rich e-commerce application for Android built with Kotlin and Jetpack Compose. It delivers a seamless shopping experience for customers while providing powerful administrative tools for store management. The app leverages Firebase for robust backend services including authentication, real-time database, and cloud storage.

## 🌟 Features

### Customer Experience
- **🔐 Secure Authentication** - Email/password registration and login with Firebase Auth
- **📱 Intuitive Product Browsing** - Browse by category or explore the entire catalog
- **🔍 Detailed Product Views** - Rich product descriptions, pricing, and image galleries
- **🛒 Smart Shopping Cart** - Add, remove, and modify product quantities with real-time updates
- **💳 Seamless Checkout** - Integrated Razorpay payment gateway for secure transactions
- **📋 Order Management** - Complete order history with status tracking
- **👤 Profile Management** - Edit personal information and manage delivery addresses

### Admin Dashboard
- **📊 Comprehensive Dashboard** - Dedicated admin interface for store management
- **➕ Product Management** - Add new products with detailed information and images
- **📦 Inventory Overview** - View and manage all products in the store
- **🔒 Secure Access** - Role-based authentication with secure logout functionality

## 🚀 Quick Start

### Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (Arctic Fox or later)
- **JDK 11** or higher
- **Firebase Project** with enabled services
- **Razorpay Account** for payment processing

### Installation Guide

#### 1. Clone the Repository
```bash
git clone https://github.com/puspendunayak/ShopKaro.git
cd ShopKaro
```

#### 2. Open in Android Studio
1. Launch Android Studio
2. Select **"Open an existing project"**
3. Navigate to the cloned repository and open it
4. Wait for Gradle sync to complete

#### 3. Firebase Configuration

##### Create Firebase Project
1. Visit the [Firebase Console](https://console.firebase.google.com/)
2. Click **"Create a project"** and follow the setup wizard
3. Add an Android app with package name: `com.example.easyshop`
4. Download `google-services.json` and place it in the `app/` directory

##### Enable Firebase Services
- **Authentication**: Enable Email/Password provider
- **Firestore Database**: Create in production mode
- **Storage**: Enable for product images (optional)

#### 4. Database Structure Setup

Your Firestore database should follow this structure:

```
├── users/{userId}
│   ├── uid: string
│   ├── email: string
│   ├── name: string
│   ├── address: string
│   ├── cartItems: map
│   └── isAdmin: boolean
│
├── data/stock/products/{productId}
│   ├── id: string
│   ├── title: string
│   ├── description: string
│   ├── price: string
│   ├── actualPrice: string
│   ├── category: string
│   └── images: array[string]
│
└── orders/{orderId}
    ├── id: string
    ├── date: timestamp
    ├── userId: string
    ├── items: map
    ├── status: string
    └── address: string
```

#### 5. Admin User Setup
1. Register a new user through the app
2. Navigate to Firestore Console → `users` collection
3. Find your user document
4. Change `isAdmin` field from `false` to `true`

#### 6. Razorpay Configuration
1. Create a [Razorpay account](https://razorpay.com/)
2. Get your API keys from the dashboard
3. Add them to your app configuration

#### 7. Build and Run
```bash
./gradlew clean build
```
Run the app on an emulator or physical device.

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| **Backend** | [Firebase](https://firebase.google.com/) |
| **Authentication** | Firebase Auth |
| **Database** | Cloud Firestore |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) |
| **Payments** | [Razorpay](https://razorpay.com/) |

## 📁 Project Structure

```
app/
├── src/main/java/com/example/easyshop/
│   ├── data/           # Data layer (repositories, models)
│   ├── ui/             # UI components and screens
│   ├── viewmodel/      # ViewModels for MVVM architecture
│   ├── navigation/     # Navigation components
│   └── utils/          # Utility classes and helpers
├── src/main/res/       # Resources (layouts, strings, etc.)
└── google-services.json
```

## 🔧 Configuration

### Firebase Rules
Ensure your Firestore security rules allow proper access:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Products are readable by all, writable by admins
    match /data/stock/products/{productId} {
      allow read: if true;
      allow write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.isAdmin == true;
    }
    
    // Orders are readable/writable by the user who owns them
    match /orders/{orderId} {
      allow read, write: if request.auth != null && 
        resource.data.userId == request.auth.uid;
    }
  }
}
```

## 🐛 Troubleshooting

### Common Issues

**Build Errors**
- Ensure `google-services.json` is in the correct location
- Check that all Firebase services are enabled
- Verify Gradle sync completed successfully

**Authentication Issues**
- Confirm Email/Password authentication is enabled in Firebase
- Check network connectivity
- Verify package name matches Firebase configuration

**Database Connection**
- Ensure Firestore is enabled and configured
- Check security rules allow proper access
- Verify network permissions in AndroidManifest.xml

## 🚧 Roadmap

### Upcoming Features
- [ ] **Advanced Product Management** - Edit and delete products from admin panel
- [ ] **Search & Filtering** - Enhanced product discovery with search and filters
- [ ] **User Reviews** - Customer reviews and ratings system
- [ ] **Wishlist** - Save products for later purchase
- [ ] **Push Notifications** - Order updates and promotional notifications
- [ ] **Analytics Dashboard** - Sales and user behavior insights for admins
- [ ] **Multi-language Support** - Localization for different regions

### Technical Improvements
- [ ] **Enhanced State Management** - Implement more sophisticated state handling
- [ ] **Offline Support** - Cache products for offline browsing
- [ ] **Performance Optimization** - Image caching and lazy loading improvements
- [ ] **Testing** - Comprehensive unit and integration tests

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

Please ensure your code follows the project's coding standards and includes appropriate tests.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Puspendu Nayak** - *Initial work* - [@puspendunayak](https://github.com/puspendunayak)

## 🙏 Acknowledgments

- Firebase team for excellent backend services
- Jetpack Compose team for modern UI toolkit
- Razorpay for seamless payment integration
- Open source community for inspiration and support

---

**⭐ Star this repository if you find it helpful!**

*Last updated: July 18, 2025*
