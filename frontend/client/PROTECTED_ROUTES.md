# Protected Routes System

## 📋 Overview

Hệ thống bảo vệ route sử dụng token từ API login để kiểm soát truy cập vào các trang yêu cầu authentication.

## 🏗️ Architecture

### **Components:**

1. **RouteGuard** (`components/route-guard.tsx`)

   - Wrapper component bảo vệ toàn bộ ứng dụng
   - Kiểm tra authentication trên mỗi route change
   - Tự động redirect về home nếu không có quyền

2. **AuthGuard** (`components/auth-guard.tsx`)

   - Có thể dùng để bảo vệ specific routes
   - Hiển thị loading spinner khi checking auth
   - Redirect nếu không authenticated

3. **ProtectedRouteNotification** (`components/protected-route-notification.tsx`)
   - Hiển thị thông báo khi user cố truy cập protected route
   - Auto-hide sau 5 giây
   - Slide-in animation

### **Utilities:**

**`lib/auth-utils.ts`** - Authentication helpers:

- `isProtectedRoute(pathname)` - Check if route is protected
- `isPublicRoute(pathname)` - Check if route is public
- `isAuthenticated()` - Check if user has valid token
- `setRedirectPath(path)` - Save intended destination before login
- `getRedirectPath()` - Get saved redirect path
- `clearRedirectPath()` - Clear saved path

### **Hooks:**

**`hooks/useAuthRedirect.ts`** - Custom hook for auth redirect logic

## 🔐 Route Configuration

### Public Routes (No authentication required):

```typescript
const publicRoutes = ["/", "/movies", "/authenticate"];
```

### Protected Routes (Authentication required):

```typescript
const protectedRoutes = ["/booking", "/profile", "/my-tickets", "/payment"];
```

## 🚀 Usage

### 1. Global Setup (Already configured)

In `app/layout.tsx`:

```tsx
import { RouteGuard } from "@/components/route-guard";
import { ProtectedRouteNotification } from "@/components/protected-route-notification";

export default function RootLayout({ children }) {
  return (
    <html>
      <body>
        <ThemeProvider>
          <RouteGuard>
            <Header />
            <ProtectedRouteNotification />
            {children}
            <Footer />
          </RouteGuard>
        </ThemeProvider>
      </body>
    </html>
  );
}
```

### 2. Add New Protected Route

Edit `lib/auth-utils.ts`:

```typescript
export const protectedRoutes = [
  "/booking",
  "/profile",
  "/my-tickets",
  "/payment",
  "/your-new-route", // Add here
];
```

### 3. Protect Specific Component

Use `AuthGuard`:

```tsx
import { AuthGuard } from "@/components/auth-guard";

export default function MyProtectedPage() {
  return (
    <AuthGuard>
      <div>Protected content here</div>
    </AuthGuard>
  );
}
```

### 4. Use Auth Utils

```tsx
import { isAuthenticated, setRedirectPath } from "@/lib/auth-utils";

// Check if user is logged in
if (isAuthenticated()) {
  // User is authenticated
}

// Save intended destination
const handleBooking = () => {
  if (!isAuthenticated()) {
    setRedirectPath("/booking/123");
    // Show login modal
  }
};
```

## 🔄 Authentication Flow

### Login Flow:

1. User clicks "Book Now" on `/movies` (public route)
2. Redirects to `/booking/123` (protected route)
3. `RouteGuard` detects no token
4. Saves `/booking/123` to sessionStorage
5. Redirects to `/` (home)
6. Shows notification: "Please login to access this page"
7. User clicks Login button
8. Enters credentials
9. After successful login:
   - Token saved to localStorage
   - Reads saved path from sessionStorage
   - Redirects to `/booking/123`
   - Clears sessionStorage

### Logout Flow:

1. User clicks "Logout" in header
2. `clearAuthData()` removes token and user info
3. Updates `isAuthenticated` state
4. Redirects to home `/`

## 📱 Header Integration

Header component shows different UI based on auth state:

**Not Authenticated:**

- Login button
- Sign Up button

**Authenticated:**

- Profile dropdown with:
  - My Profile link
  - My Tickets link
  - Logout button

## 🎯 Example: Profile Page

`app/profile/page.tsx`:

```tsx
export default function ProfilePage() {
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      router.push("/");
      return;
    }

    // Fetch user info
    getMyInfo().then(setUserInfo);
  }, []);

  // Render profile...
}
```

## 🔧 Token Management

### Storing Token:

```typescript
// After successful login
setToken(response.data.result.token);
```

### Reading Token:

```typescript
const token = getToken();
```

### Using Token in API Calls:

```typescript
// In httpClient interceptor
httpClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### Clearing Token:

```typescript
// On logout
clearAuthData(); // Removes both token and user info
```

## 🛡️ Security Features

1. **Client-side token validation** - Check token existence before route access
2. **Automatic redirect** - Unauthorized users redirected to safe routes
3. **Session persistence** - Saves intended destination for better UX
4. **Secure storage** - Token stored in localStorage (SSR-safe)
5. **Auto logout** - Can implement token expiration check

## 📊 Route States

| State                | Has Token | Route Type | Behavior                             |
| -------------------- | --------- | ---------- | ------------------------------------ |
| ✅ Authenticated     | Yes       | Public     | Access granted                       |
| ✅ Authenticated     | Yes       | Protected  | Access granted                       |
| ⚠️ Not Authenticated | No        | Public     | Access granted                       |
| ❌ Not Authenticated | No        | Protected  | Redirect to home + Show notification |

## 🎨 UI/UX Features

1. **Loading State** - Spinner while checking authentication
2. **Notifications** - User-friendly messages for auth issues
3. **Smooth Transitions** - Animations for notifications
4. **Smart Redirects** - Return to intended page after login
5. **Dropdown Menu** - Easy access to profile and logout

## 🔍 Debugging

Enable auth logs:

```typescript
// In RouteGuard component
console.log("Current route:", pathname);
console.log("Is protected:", isProtectedRoute(pathname));
console.log("Is authenticated:", isAuthenticated());
console.log("Token:", getToken());
```

## 📝 Notes

- Token is stored in localStorage with key: `customer_token`
- Redirect path is stored in sessionStorage: `redirectAfterLogin`
- All auth checks are client-side (Next.js App Router)
- For server-side protection, implement middleware
- Token should be validated on backend for each API call

## 🚧 Future Improvements

- [ ] Token expiration check
- [ ] Refresh token mechanism
- [ ] Server-side route protection (middleware)
- [ ] Role-based access control (RBAC)
- [ ] Remember me functionality
- [ ] Session timeout warning
