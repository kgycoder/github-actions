# Todo List — iOS 스타일 안드로이드 Todo 앱

깔끔한 블랙 & 화이트, iOS/Mac 감성의 모던한 Todo 앱입니다.
Jetpack Compose + Room + AccessibilityService 기반으로 제작되었습니다.

## 핵심 기능

- **Todo 작성/관리**: 앱 안에서 하단 시트로 빠르게 추가, 스와이프로 삭제
- **알림 연동**: Todo를 추가하면 커스텀 디자인의 알림이 표시되고, 알림의 원형 체크 버튼을 누르면 즉시 완료 처리됩니다
- **접근성 버튼 실행**: 설정 > 접근성에서 "Todo"를 켜면 시스템 접근성 버튼이 생깁니다. 이 버튼을 누르면 현재 보고 있는 화면 위에 오버레이 카드가 뜨고, 바로 할 일을 입력할 수 있습니다 (`TYPE_ACCESSIBILITY_OVERLAY` 사용, 별도의 "다른 앱 위에 표시" 권한이 필요 없습니다)
- **영속성**: Room 데이터베이스에 저장되어 앱을 껐다 켜도 상태가 유지되고, 재부팅 후에도 활성 Todo의 알림이 복원됩니다 (`BootReceiver`)

## 디자인

- 퓨어 블랙 & 화이트 테마, 둥근 모서리(16~24dp), 여백 중심의 iOS 스타일 레이아웃
- 스프링 기반의 유기적인 애니메이션 (체크 버튼 바운스, 리스트 아이템 배치 애니메이션)
- iOS 스타일 스와이프-삭제 (`SwipeToDismissBox`)
- 알림 UI도 앱과 동일한 원형 체크 버튼 디자인 언어를 사용하는 커스텀 `RemoteViews` 레이아웃

## 프로젝트 구조

```
app/src/main/java/com/minimal/todo/
├── data/                 # Room Entity / Dao / Database / Repository
├── notification/         # 알림 생성, 완료 처리 BroadcastReceiver, 부팅 복원
├── accessibility/        # 접근성 버튼 + 오버레이 서비스
├── ui/
│   ├── theme/            # 색상, 타이포그래피, MaterialTheme
│   ├── components/       # 원형 체크 버튼, TodoRow(스와이프), 추가 바텀시트
│   ├── TodoListScreen.kt
│   └── TodoViewModel.kt
├── MainActivity.kt
└── TodoApplication.kt
```

## 빌드 방법

### GitHub Actions (자동)
`main` 브랜치에 push하거나 PR을 열면 `.github/workflows/android-build.yml`이 자동으로
Debug/Release APK를 빌드하고, Actions 실행 결과의 **Artifacts**에 APK를 업로드합니다.
로컬에 Gradle Wrapper를 커밋하지 않고, CI에서 `gradle/actions/setup-gradle`로 Gradle 8.7을
직접 프로비저닝하는 방식이라 별도 설정 없이 바로 동작합니다.

### 로컬(Android Studio)
1. 이 폴더를 Android Studio에서 "Open" (Gradle sync 시 Wrapper가 없으므로 Studio가
   자동으로 Wrapper 생성 여부를 묻습니다 — 예를 눌러 생성하거나, `gradle wrapper` 커맨드를
   한 번 실행해 주세요)
2. `minSdk 26` 이상 기기/에뮬레이터에서 Run

## 접근성 버튼 켜는 법 (사용자 안내)

1. 앱을 한 번 실행합니다
2. 기기 설정 → 접근성 → 설치된 앱 → **Todo** → 사용 켜기
3. 화면 하단(또는 내비게이션 바 옆)에 접근성 버튼 아이콘이 나타납니다
4. 어떤 화면에서든 이 버튼을 누르면 Todo 빠른 입력 오버레이가 뜹니다

## 참고

- `minSdk = 26` (Android 8.0) — 접근성 버튼 API와 적응형 아이콘을 사용하기 위한 최소 버전입니다
- Android 13(API 33) 이상에서는 알림 표시를 위해 최초 실행 시 알림 권한을 요청합니다
- 완료된 Todo는 삭제되지 않고 "완료됨" 섹션에 접힌 상태로 보관되며, 다시 탭하면 되돌릴 수 있습니다
