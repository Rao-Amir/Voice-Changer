# Hướng Dẫn Cho Người Mới - MVVM Compose Android App

## Giới Thiệu

Chào mừng bạn đến với dự án MVVM Compose! Đây là một ứng dụng Android được xây dựng với kiến trúc MVVM (Model-View-ViewModel) hiện đại, sử dụng Jetpack Compose cho UI và các công nghệ mới nhất từ Google. Tài liệu này sẽ giúp bạn hiểu cấu trúc dự án, các thành phần chính và cách làm việc hiệu quả với codebase.

## Yêu Cầu Kỹ Thuật

- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17 (tối thiểu)
- Kotlin 2.0.0 trở lên
- Gradle 8.0 trở lên

## Cấu Trúc Dự Án

Dự án được tổ chức theo module và package để tạo điều kiện cho việc phát triển, kiểm thử và bảo trì:

```
app/
  ├── src/main/
  │   ├── java/com/pixelzlab/app/
  │   │   ├── core/               # Các thành phần cốt lõi, sử dụng xuyên suốt ứng dụng
  │   │   │   ├── base/           # Các lớp cơ sở (BaseViewModel, BaseScreen, v.v.)
  │   │   │   ├── navigation/     # Định nghĩa điều hướng
  │   │   │   ├── network/        # Xử lý mạng và lỗi
  │   │   │   └── ui/             # Thành phần UI dùng chung
  │   │   │       └── state/      # Quản lý trạng thái UI
  │   │   ├── data/               # Tầng dữ liệu
  │   │   │   ├── local/          # Dữ liệu cục bộ (DataStore, Room)
  │   │   │   └── remote/         # Dữ liệu từ API
  │   │   │       ├── api/        # Định nghĩa API
  │   │   │       └── repository/ # Các repository
  │   │   ├── designsystem/       # Hệ thống thiết kế
  │   │   │   ├── component/      # Các thành phần UI tái sử dụng
  │   │   │   └── theme/          # Định nghĩa theme (màu sắc, typography, v.v.)
  │   │   ├── di/                 # Dependency Injection (Hilt)
  │   │   ├── feature/            # Các tính năng của ứng dụng
  │   │   │   ├── detail/         # Màn hình chi tiết
  │   │   │   ├── home/           # Màn hình chính
  │   │   │   └── splash/         # Màn hình khởi động
  │   │   ├── model/              # Các model
  │   │   │   ├── base/           # Model cơ sở
  │   │   │   └── entity/         # Các entity
  │   │   ├── startup/            # Khởi tạo ứng dụng với App Startup
  │   │   └── utils/              # Tiện ích
  │   │       └── extension/      # Các extension function
  │   └── res/                    # Tài nguyên (layout, drawable, v.v.)
  └── build.gradle.kts            # Cấu hình Gradle cho module app
```

## Kiến Trúc Ứng Dụng

Dự án tuân theo kiến trúc MVVM (Model-View-ViewModel) với Clean Architecture:

1. **UI Layer (View)**: Jetpack Compose UI
   - Các màn hình (Screen) và thành phần UI (Component)
   - Sử dụng UiState để quản lý trạng thái UI

2. **ViewModel Layer**: Quản lý logic nghiệp vụ và trạng thái
   - Kết nối UI với data layer
   - Xử lý các sự kiện từ người dùng
   - Quản lý trạng thái UI thông qua StateFlow

3. **Data Layer**: Quản lý dữ liệu
   - Repository: Trung gian giữa ViewModel và data sources
   - Remote Data Source: API services (Retrofit)
   - Local Data Source: DataStore, Room Database

## Các Công Nghệ Chính

- **Jetpack Compose**: Framework UI hiện đại dựa trên Kotlin
- **Coroutines & Flow**: Lập trình bất đồng bộ
- **Hilt**: Dependency Injection
- **Retrofit & OkHttp**: Network requests
- **Moshi**: JSON parsing
- **DataStore**: Lưu trữ dữ liệu cục bộ
- **App Startup**: Tối ưu khởi động ứng dụng
- **Material 3**: Design system
- **Coil**: Tải và hiển thị hình ảnh

## Hướng Dẫn Bắt Đầu

### 1. Clone và Mở Dự Án

```bash
git clone [repository-url]
cd mvvm-compose
```

Mở dự án trong Android Studio.

### 2. Hiểu Luồng Điều Hướng

Ứng dụng sử dụng Jetpack Navigation Compose để điều hướng giữa các màn hình. Tệp `AppNavigation.kt` trong package `core/navigation` định nghĩa toàn bộ điều hướng của ứng dụng.

### 3. Thêm Tính Năng Mới

Để thêm một tính năng mới:

1. Tạo package mới trong `feature/`
2. Tạo các file:
   - `FeatureScreen.kt`: UI với Compose
   - `FeatureViewModel.kt`: ViewModel cho tính năng
   - Các thành phần UI cụ thể của tính năng

3. Thêm điểm đến mới trong `AppDestination.kt`
4. Cập nhật điều hướng trong `AppNavigation.kt`

### 4. Quản Lý Trạng Thái UI

Dự án sử dụng mẫu UiState để quản lý trạng thái UI một cách nhất quán:

```kotlin
// Sử dụng UiState trong ViewModel
val uiState: StateFlow<UiState<YourData>> = _uiState

// Trong Composable
val state by viewModel.uiState.collectAsStateWithLifecycle()
when (state) {
    is UiState.Loading -> // Hiển thị loading
    is UiState.Success -> // Hiển thị dữ liệu
    is UiState.Error -> // Hiển thị lỗi
}
```

### 5. Làm Việc Với API

1. Định nghĩa endpoint trong `ApiService.kt`
2. Thêm repository method trong `AppRepository.kt`
3. Sử dụng repository trong ViewModel thông qua dependency injection

```kotlin
@Inject
lateinit var repository: AppRepository

fun fetchData() {
    viewModelScope.launch {
        repository.getData()
            .onStart { _uiState.value = UiState.Loading }
            .catch { e -> _uiState.value = UiState.Error(e) }
            .collect { data -> _uiState.value = UiState.Success(data) }
    }
}
```

## Quy Tắc Và Quy Ước

### Coding Style

- Tuân thủ [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Sử dụng lệnh `./gradlew ktlintCheck` để kiểm tra code style
- Sử dụng lệnh `./gradlew ktlintFormat` để tự động định dạng code

### Tên Biến và Hàm

- **Classes**: PascalCase (ví dụ: `HomeViewModel`)
- **Functions**: camelCase (ví dụ: `fetchUserData()`)
- **Variables**: camelCase (ví dụ: `userName`)
- **Constants**: UPPER_SNAKE_CASE (ví dụ: `MAX_RETRY_COUNT`)
- **Composable Functions**: PascalCase (ví dụ: `HomeScreen()`)

### Composable Functions

- Các hàm Composable chính nên có tiền tố là tên tính năng (ví dụ: `HomeScreen`)
- Các hàm Composable phụ nên có phạm vi private nếu chỉ sử dụng trong file
- Sử dụng tham số `modifier` làm tham số đầu tiên sau các tham số có giá trị mặc định

```kotlin
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Implementation
}
```

### Dependency Injection

- Sử dụng Hilt cho DI
- Định nghĩa các module trong package `di/`
- Sử dụng `@Inject` constructor khi có thể
- Sử dụng `@Provides` cho các trường hợp không thể inject trực tiếp

## Hiệu Suất và Tối Ưu

- Sử dụng `remember` và `derivedStateOf` để tránh tính toán lại không cần thiết
- Sử dụng `LaunchedEffect` cho các side effect
- Tránh allocation trong các hàm Composable
- Sử dụng `key` cho các item trong danh sách để tối ưu recomposition

## Debugging và Logging

- Sử dụng Timber cho logging
- Sử dụng Compose Preview cho phát triển UI nhanh chóng
- Sử dụng Layout Inspector để debug UI
- Sử dụng StrictMode để phát hiện các vấn đề hiệu suất

## Tài Nguyên Học Tập

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Material 3 Design](https://m3.material.io/)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)

## Liên Hệ Hỗ Trợ

Nếu bạn có bất kỳ câu hỏi hoặc gặp vấn đề, vui lòng liên hệ:

- Team Lead: [email@example.com]
- Tech Support: [support@example.com]

---

Chúc bạn có trải nghiệm tuyệt vời khi làm việc với dự án MVVM Compose! 