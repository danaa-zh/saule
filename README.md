# Saule — Online Shop

Android e-commerce application built with Java for the Samsung Innovation Campus Java course.

## Tech Stack

- **Language:** Java
- **UI:** XML Layouts, ViewBinding, ConstraintLayout, RecyclerView
- **Backend:** Firebase Authentication, Cloud Firestore
- **Image Hosting:** Cloudinary (cloud name: `dkll2ypza`)
- **Image Loading:** Glide 4.16
- **Architecture:** Repository Pattern, SRP

## Features

- Splash screen with authentication state check
- Email/password sign in and sign up
- Google Sign-In
- Home screen with featured products and categories
- Catalog with category filter and product search
- Product detail screen with color selection
- Add to cart / remove from cart
- Favorites (toggle and view)
- Cart screen with quantity management and total price
- My Orders screen
- Profile screen
- Settings screen
- Bottom navigation with 4 tabs

## Project Structure

```
app/src/main/java/com/example/onlineshop/
├── ShopApplication.java          # Cloudinary init
├── AppGlideModule.java
├── activities/
│   ├── SplashActivity.java
│   ├── SignInActivity.java
│   ├── SignUpActivity.java
│   ├── MainActivity.java
│   ├── ProductDetailActivity.java
│   ├── CartActivity.java
│   ├── MyOrdersActivity.java
│   └── SettingsActivity.java
├── ui/
│   ├── fragments/
│   │   ├── HomeFragment.java
│   │   ├── CatalogFragment.java
│   │   ├── FavoritesFragment.java
│   │   └── ProfileFragment.java
│   └── adapter/
│       ├── ProductAdapter.java
│       ├── CartAdapter.java
│       ├── CategoryHomeAdapter.java
│       └── CategoryCatalogAdapter.java
├── model/
│   ├── Product.java
│   ├── Category.java
│   ├── CartItem.java
│   └── User.java
└── repository/
    ├── AuthRepository.java
    ├── ProductRepository.java
    ├── CartRepository.java
    ├── FavoritesRepository.java
    └── FirebaseCallback.java
```

## Firestore Collections

```
products/
  {productId}: name, description, price, categoryId,
               imageUrls[], colors[], rating,
               reviewCount, stockCount, featured, createdAt

categories/
  {categoryId}: name, imageUrl, sortOrder, productCount

users/
  {uid}/
    cart/{productId}: productId, productName, productImage,
                      price, quantity, selectedColor
    favorites/{productId}: productId, productName,
                           productImage, price, rating
```

## Setup

1. Clone the repository
2. Place your `google-services.json` in the `app/` directory
3. Add your SHA-1 fingerprint in Firebase Console → Project Settings for Google Sign-In
4. Sync Gradle and run

## Requirements

- Android Studio Hedgehog or newer
- Android SDK 24+
- Active Firebase project with Auth and Firestore enabled