# Error Handling Strategy - Frontend

## 📋 Backend Response Format

Backend luôn trả về response theo chuẩn:

```typescript
{
  code: number,    // 1000 = success, khác 1000 = error
  message: string, // Message từ backend (tiếng Anh hoặc đa ngôn ngữ)
  result?: T       // Data (nếu có)
}
```

## ✅ Strategy: Sử dụng Message từ Backend

### Lý do:

1. **Tính nhất quán**: Message được quản lý tập trung ở backend
2. **Dễ maintain**: Chỉ cần sửa 1 lần ở backend
3. **Đa ngôn ngữ**: Backend có thể trả message theo ngôn ngữ
4. **Chính xác hơn**: Backend hiểu rõ context lỗi

### ❌ KHÔNG nên:

```typescript
// ❌ Hardcode message ở FE
catch (error) {
  setError("Email hoặc mật khẩu không đúng");
}
```

### ✅ NÊN:

```typescript
// ✅ Sử dụng message từ backend
catch (error) {
  setError(getErrorMessage(error)); // Lấy message từ backend
}
```

## 🛠️ Implementation

### 1. Error Class (`lib/errors.ts`)

```typescript
export class ApiError extends Error {
  code: number;
  originalError?: any;

  constructor(code: number, message: string, originalError?: any) {
    super(message);
    this.code = code;
    this.originalError = originalError;
  }
}
```

### 2. HTTP Client với Interceptor (`configurations/httpClient.ts`)

```typescript
httpClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    // Kiểm tra code !== 1000 → throw error
    if (response.data.code !== 1000) {
      throw new ApiError(
        response.data.code,
        response.data.message,
        response.data
      );
    }
    return response;
  },
  (error) => {
    // Handle HTTP errors
    if (error.response?.data?.code) {
      throw new ApiError(
        error.response.data.code,
        error.response.data.message,
        error.response.data
      );
    }
    return Promise.reject(error);
  }
);
```

### 3. Helper Functions

```typescript
// Lấy error message
getErrorMessage(error: any): string

// Lấy error code
getErrorCode(error: any): number | null

// Kiểm tra ApiError
isApiError(error: any): boolean
```

## 📝 Usage Examples

### Example 1: Basic Login

```typescript
async function handleLogin(username: string, password: string) {
  try {
    const response = await login({ loginIdentifier: username, password });
    console.log("Success:", response);
  } catch (error) {
    const message = getErrorMessage(error); // Backend message
    alert(message); // "Invalid credentials" hoặc message từ backend
  }
}
```

### Example 2: React Component

```typescript
function LoginForm() {
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    try {
      await login(data);
      router.push("/dashboard");
    } catch (err) {
      setError(getErrorMessage(err)); // Hiển thị message từ backend
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="error">{error}</div>}
      {/* form fields */}
    </form>
  );
}
```

### Example 3: Xử lý theo Error Code

```typescript
async function handleAdvanced() {
  try {
    await someApiCall();
  } catch (error) {
    const code = getErrorCode(error);
    const message = getErrorMessage(error);

    // Có thể override message cho một số code đặc biệt
    if (code === 1001) {
      setError("User not found. Please register first.");
    } else if (code === 1002) {
      setError("Invalid password. Try again.");
    } else {
      setError(message); // Dùng message từ backend
    }
  }
}
```

### Example 4: Toast Notification

```typescript
async function handleWithToast() {
  try {
    await apiCall();
    toast.success("Success!");
  } catch (error) {
    toast.error(getErrorMessage(error)); // Backend message in toast
  }
}
```

## 🔄 Workflow

```
Backend Response (code !== 1000)
         ↓
HTTP Client Interceptor catches
         ↓
Creates ApiError with backend message
         ↓
Throw ApiError
         ↓
FE catch block
         ↓
getErrorMessage(error) → Returns backend message
         ↓
Display to user
```

## 🎯 Benefits

1. **Single Source of Truth**: Message được quản lý ở backend
2. **Easy Updates**: Sửa message một lần, tất cả client cập nhật
3. **Consistent UX**: User thấy message nhất quán
4. **I18n Ready**: Backend có thể trả message theo locale
5. **Less Code**: FE không cần define tất cả error messages

## 📌 Best Practices

### ✅ DO:

- Luôn dùng `getErrorMessage(error)` để lấy message
- Log error code với `getErrorCode(error)` để debug
- Chỉ override message khi thực sự cần thiết (UX đặc biệt)
- Hiển thị message từ backend trực tiếp cho user

### ❌ DON'T:

- Hardcode error messages ở FE
- Ignore backend messages
- Tự tạo message dựa trên error code (trừ trường hợp đặc biệt)

## 🔍 Debugging

```typescript
catch (error) {
  console.log("Error code:", getErrorCode(error));
  console.log("Error message:", getErrorMessage(error));
  console.log("Is API error?", isApiError(error));
  console.log("Full error:", error);
}
```

## 🌍 Internationalization (Future)

Backend có thể support đa ngôn ngữ:

```typescript
// Request with locale
headers: {
  "Accept-Language": "vi-VN" // or "en-US"
}

// Backend returns localized message
{
  code: 1001,
  message: "Tên đăng nhập hoặc mật khẩu không đúng", // vi-VN
  result: null
}
```

Frontend chỉ cần hiển thị message, không cần xử lý translation!
