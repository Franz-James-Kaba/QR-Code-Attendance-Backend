# 📌 QR Code Attendance Admin Frontend

A modern, responsive admin dashboard built with **Angular 19** for managing QR code-based attendance systems. This application features robust authentication, dynamic layouts, and reusable components styled with **TailwindCSS**.

---

## 🚀 Features

### 🔐 Authentication
- Secure **JWT-based login system**
- Password reset functionality 🔄
- Route guards for protected pages 🛡️
- Automatic token refresh 🔃
- Session management 🔒

### 🖥️ Layout & Navigation
- Responsive **Admin Dashboard** 📊
- **Collapsible sidebar navigation** 📌
- **Mobile-friendly design** 📱
- **Dynamic header with search functionality** 🔍
- **Smooth transitions and animations** 🎨

### ⚙️ Components
- Reusable **form inputs with validation** ✅
- Custom **buttons with loading states** ⏳
- **Loading indicators & spinners** 🔄
- **Error handling pages (404, 401, etc.)** 🚫
- **Toast notifications (coming soon)** 🔔

### 🔄 State Management
- **NgRx implementation** for state management 🎭
- **Authentication state handling** 🔐
- **Loading state management** ⏳
- **Error state handling** ❌

---

## 📌 Prerequisites

Make sure you have the following installed before running the project:

- **Node.js** (v18 or later) 🌍
- **npm** (v9 or later) 📦
- **Angular CLI** (v19.1.7) 🅰️

---

## 🔧 Installation & Setup

1️⃣ **Clone the repository:**
```bash
git clone [<repository-url>](https://github.com/Franz-James-Kaba/QR-Code-Attendance/tree/main/Frontend-Code/admin-frontend)
cd admin-frontend
```

2️⃣ **Install dependencies:**
```bash
npm install
```

3️⃣ **Start the development server:**
```bash
ng serve --open
```

4️⃣ **Open in browser:** 🌐
```
http://localhost:4200/
```

---

## 🏗️ Project Structure

```
src/
├── app/
│   ├── core/               # Core functionality
│   │   ├── guards/         # Route guards
│   │   ├── interceptors/   # HTTP interceptors
│   │   ├── services/       # Core services
│   │   └── store/          # NgRx store
│   ├── features/           # Feature modules
│   │   ├── auth/           # Authentication feature
│   │   └── dashboard/      # Dashboard feature
│   ├── layouts/            # Layout components
│   │   ├── admin-layout/   # Admin dashboard layout
│   │   └── auth-layout/    # Authentication layout
│   └── shared/            # Shared components
└── assets/                # Static assets
```

---

## 🌎 Environment Configuration

This application uses two environment files:

- `environment.ts` - Development environment 🛠️
- `environment.prod.ts` - Production environment 🚀

Ensure these files are configured with appropriate **API endpoints** and environment-specific variables.

---

## 🔑 Key Components

### 🛠️ Authentication Components
- **`LoginComponent`** - Handles user login 🔑
- **`ResetPasswordComponent`** - Handles password reset 🔄

### 🏗️ Layout Components
- **`AdminLayoutComponent`** - Main dashboard layout 📊
- **`AuthLayoutComponent`** - Authentication pages layout 🛂

### 🧩 Shared Components
- **`ButtonComponent`** - Reusable button 🖲️
- **`InputFieldComponent`** - Form input fields ✏️
- **`LoadingComponent`** - Loading indicators ⏳
- **`SidebarComponent`** - Navigation sidebar 📂

### 🔄 State Management (NgRx)
- **Actions** - Defined in `auth.actions.ts` 🎬
- **Reducers** - Located in `auth.reducer.ts` 📉
- **Effects** - Handled in `auth.effects.ts` 🎭
- **Selectors** - Managed in `auth.selectors.ts` 🔍

---

## 🎨 Styling
- **TailwindCSS** for utility-first styling 🎨
- **Custom CSS modules** for component-specific styles 🎭
- **Responsive design breakpoints** 📱
- **Dark mode support (coming soon)** 🌑

---

## 🔒 Security Features
- **Route guards** for protected routes 🛡️
- **HTTP interceptors** for token management 🛑
- **Form validation** ✅
- **XSS protection** 🛑
- **CSRF protection** (when implemented with backend) 🔄

---

## 💡 Best Practices
✅ Follows **Angular style guide** 📘
✅ Implements **lazy loading** for better performance 🚀
✅ Uses **TypeScript strict mode** 🔍
✅ Implements **proper error handling** ❌
✅ Follows **reactive programming patterns** with RxJS 🔄

---

## 🤝 Contributing

We welcome contributions! 🚀 Follow these steps:

1️⃣ **Fork the repository** 🍴
2️⃣ **Create a feature branch** 🌱
3️⃣ **Commit your changes** 💾
4️⃣ **Push to the branch** 🚀
5️⃣ **Create a Pull Request** 🔄

---

## 📜 License

This project is licensed under the **MIT License** 📜.

---

## 📩 Support
For support, reach out via **email: support@example.com** 📧.
