# Szavazas-Core Coding Rules Compliance Audit

Audit date: 2026-05-27
Module: `szavazas-core/src/main/java`
Rules reference: `AGENTS.md` (Konveyor Coding Style Rules — Dagger + Constructor Injection Edition)

---

## Phase 2 Fixes (2026-05-27)

The following trivially fixable violations have been resolved:

| Category | Before | After | Status |
|---|---|---|---|
| Section 3: Field naming (`Service` suffix on fields) | ~22 files | 0 | ✅ RESOLVED — All dependency field names now follow "remove `Service` suffix" rule |
| Section 7.1: Extra `TAG` field in `SerializeBallotResultService` | 1 | 0 | ✅ RESOLVED — Field removed, literal inlined |
| Section 10.1: Unused `mergeClosePeakService` in `FindRawPeakService` | 1 | 0 | ✅ RESOLVED — Dependency and field removed, constructor is now no-arg `@Inject` |
| Section 3.19: False positive (`crop` → `cropService`) | 1 | 0 | ✅ CLARIFIED — `CropService` → `crop` is correct per the "remove `Service` suffix" rule |

**Total:** ~62 violations resolved (60 field naming + 1 extra field + 1 unused dependency).

---

## Summary

| Category | Count |
|---|---|
| Files audited | 93 |
| Files with violations (original) | 55 |
| Files with violations (after Phase 2 fixes) | 43 |
| Total violation instances (original) | ~130 |
| Total violation instances (after Phase 2 fixes) | ~70 |

---

## 1. Units That Do Not Fit Any Standard Unit Type

### 1.1 `Logger.java` — ✅ RESOLVED (Phase 3)
- **Path:** `ballotprocessor/Logger.java`
- **Violation:** `final class` with private constructor and static delegate — a singleton anti-pattern. Does not match any defined unit type (Service, Wrapper, State, Data, Constant, Repository, Delegate, Glue).
- **Severity:** CRITICAL
- **Recommendation:** Remove this class. `LoggerWrapper` already wraps logging; inject `LoggerWrapper` everywhere instead.
- **Fixed:** `Logger.java` deleted. Static `delegate` and `setDelegate()` moved into `LoggerWrapper`. `MainActivity` updated to call `LoggerWrapper.setDelegate()`. Both `szavazas-core:compileJava` and `app:compileDebugJavaWithJavac` pass.

### 1.2 `InverterService.java`
- **Path:** `ballotprocessor/common/InverterService.java`
- **Violation:** `final class` with `private` constructor and `static` method. Service units must have `@Inject` on constructor and be injectable via Dagger.
- **Severity:** CRITICAL
- **Recommendation:** Remove `final`, make constructor `@Inject`, make `apply` an instance method.

### 1.3 `FindGridBoundaryService.java`
- **Path:** `ballotprocessor/grid/FindGridBoundaryService.java`
- **Violation:** `final class` with `private` constructor and `static` method. Same pattern as `InverterService`.
- **Severity:** CRITICAL
- **Recommendation:** Remove `final`, make constructor `@Inject`, make `apply` an instance method.

### 1.4 `ImageSaver.java`
- **Path:** `ballotprocessor/debug/ImageSaver.java`
- **Violation:** Plain interface without any unit type suffix. Does not match Wrapper, Repository, or any defined unit type.
- **Severity:** MAJOR
- **Recommendation:** If this wraps an external I/O concern, rename to `ImageSaverWrapper`. If a callback/strategy, consider whether it should be injected as a functional interface.

### 1.5 `GridOverlayDebugRenderer.java`
- **Path:** `ballotprocessor/debug/GridOverlayDebugRenderer.java`
- **Violation:** Class does not match any unit type. No `@Inject` constructor, contains constants (`COLOR_WHITE`, etc.) as static fields, constructor is not DI-compatible.
- **Severity:** CRITICAL
- **Recommendation:** Refactor to a Service (with `@Inject` constructor) or properly classify as Glue if framework-required.

### 1.6 `ProjectionDebugRenderer.java`
- **Path:** `ballotprocessor/debug/ProjectionDebugRenderer.java`
- **Violation:** Does not match any unit type. Has both `@Inject` and non-`@Inject` constructors, static constants, and creates its own dependencies (`new SetPixelService()`, `new DrawLineService()`, etc.) in the secondary constructor instead of injecting them.
- **Severity:** CRITICAL
- **Recommendation:** Refactor to Service with single `@Inject` constructor; inject all dependencies.

### 1.7 `XMarkDebugRenderer.java`
- **Path:** `ballotprocessor/debug/XMarkDebugRenderer.java`
- **Violation:** Does not match any unit type. Same pattern as `ProjectionDebugRenderer`: dual constructors, static constants, creates dependencies manually.
- **Severity:** CRITICAL
- **Recommendation:** Refactor to Service with single `@Inject` constructor.

### 1.8 `PreprocessingStep.java`
- **Path:** `ballotprocessor/qr/preprocess/PreprocessingStep.java`
- **Violation:** Plain interface without unit type suffix. Not a Wrapper (no external dependency), not a Repository.
- **Severity:** MAJOR
- **Recommendation:** Either rename with a proper suffix (e.g., Strategy pattern as a Delegate) or make each implementing class a proper Service.

---

## 2. Services Exceeding 25 Lines of Code

### 2.1 `BallotProcessingService.java` (~132 lines)
- **Path:** `ballotprocessor/BallotProcessingService.java`
- **Violation:** Far exceeds 25 LoC limit.
- **Recommendation:** Extract private methods (`process`, `nonconformities`, `add`) into separate Service units.

### 2.2 `VoteMetadataFromJsonService.java` (~44 lines)
- **Path:** `ballotprocessor/VoteMetadataFromJsonService.java`

### 2.3 `ImageNormalizerService.java` (~31 lines)
- **Path:** `ballotprocessor/common/ImageNormalizerService.java`

### 2.4 `DrawOvalService.java` (~83 lines)
- **Path:** `ballotprocessor/draw/DrawOvalService.java`

### 2.5 `FillOvalService.java` (~33 lines)
- **Path:** `ballotprocessor/draw/FillOvalService.java`

### 2.6 `OrchestrateGridDetectionService.java` (~86 lines)
- **Path:** `ballotprocessor/grid/OrchestrateGridDetectionService.java`

### 2.7 `ComputeRowProjectionService.java` (~30 lines)
- **Path:** `ballotprocessor/projection/ComputeRowProjectionService.java`

### 2.8 `FindRowBoundaryService.java` (~30+ lines)
- **Path:** `ballotprocessor/projection/FindRowBoundaryService.java`

### 2.9 `ReconstructEdgeService.java` (~48 lines)
- **Path:** `ballotprocessor/projection/ReconstructEdgeService.java`

### 2.10 `ParseQrResultService.java` (~63 lines)
- **Path:** `ballotprocessor/qr/ParseQrResultService.java`

### 2.11 `PreprocessQRCropService.java` (~35 lines)
- **Path:** `ballotprocessor/qr/PreprocessQRCropService.java`

### 2.12 `XMarkDetectionAndResultService.java` (~29 lines)
- **Path:** `ballotprocessor/x/XMarkDetectionAndResultService.java`

### 2.13 `XMarkDetectService.java` (~39 lines)
- **Path:** `ballotprocessor/x/XMarkDetectService.java`

### 2.14 `XDetectService.java` (~46 lines)
- **Path:** `ballotprocessor/x/XDetectService.java`

### 2.15 `ArucoMarkerDetectionService.java` (~53 lines)
- **Path:** `ballotprocessor/aruco/ArucoMarkerDetectionService.java`

### 2.16 `ArucoWarpService.java` (~67 lines)
- **Path:** `ballotprocessor/aruco/ArucoWarpService.java`

### 2.17 `PrepareReviewGridService.java` (~35 lines)
- **Path:** `ballotprocessor/review/PrepareReviewGridService.java`

### 2.18 `SerializeBallotResultService.java` (~116 lines)
- **Path:** `ballotprocessor/review/SerializeBallotResultService.java`

---

## 3. Field Naming Violations (Dependency field names must NOT keep `Service` suffix) — ✅ RESOLVED

Rule: "Field names follow the pattern: dependency class name lowercased, removing `Service` suffix."

**Status: All field naming violations have been fixed in Phase 2.** All dependency fields now follow the "remove `Service` suffix" convention. No remaining violations in this category.

> **Note on 3.19 `PreprocessQRCropService.java`:** The original report flagged `crop` as incorrect, suggesting `cropService`. This was a **false positive** — `CropService` → `crop` correctly follows the rule.

**Files fixed (22 total):**
`BallotPreprocessService.java`, `BallotProcessingService.java`, `VoteMetadataFromJsonService.java`, `DrawLineService.java`, `DrawOvalService.java`, `DrawRectangleService.java`, `DrawTextService.java`, `FillOvalService.java`, `RectFillService.java`, `DetectGridRegionAndCheckboxService.java`, `DetectGridService.java`, `ExtractGridRegionService.java`, `OrchestrateGridDetectionService.java`, `FindRawPeakService.java`, `FindRowBoundaryService.java`, `ReconstructEdgeService.java`, `QrProcessingService.java`, `XMarkDetectionAndResultService.java`, `XMarkDetectService.java`, `PrepareReviewGridService.java`, `SaveBallotResultService.java`

---

## 4. Services With Multiple Public `apply` Methods (Overloading)

Rule: "Has exactly one public method: `apply`."

### 4.1 `ComputeRowProjectionService.java`
- Two overloaded `apply(GrayU8, RectangleData, int, int)` and `apply(GrayU8)`

### 4.2 `MergeClosePeakService.java`
- Two overloaded `apply(List<Integer>)` and `apply(List<Integer>, int)`

### 4.3 `PairEdgeService.java`
- Two overloaded `apply(List<Integer>, int, int)` and `apply(List<Integer>, int, int, int)`

### 4.4 `SerializeBallotResultService.java`
- Two overloaded `apply(BallotResultData)` and `apply(String, BallotResultData)`

---

## 5. Services Not Using Constructor Injection / Missing `@Inject`

### 5.1 `AdaptiveBinarizeService.java`
- **Path:** `ballotprocessor/qr/preprocess/AdaptiveBinarizeService.java`
- **Violation:** No `@Inject` constructor. Instantiated via `new` in `PreprocessQRCropService`.

### 5.2 `EnhanceContrastService.java`
- **Path:** `ballotprocessor/qr/preprocess/EnhanceContrastService.java`
- **Violation:** No `@Inject` constructor. Instantiated via `new`.

### 5.3 `SharpenService.java`
- **Path:** `ballotprocessor/qr/preprocess/SharpenService.java`
- **Violation:** No `@Inject` constructor. Instantiated via `new`.

### 5.4 `PreprocessQRService.java`
- **Path:** `ballotprocessor/qr/preprocess/PreprocessQRService.java`
- **Violation:** No `@Inject` constructor. Constructor takes `ImageSaver` and `List<PreprocessingStep>` directly, not via Dagger.

---

## 6. Services Manually Creating Dependencies Instead of Injecting Them

### 6.1 `OrchestrateGridDetectionService.java`
- Creates `new SetPixelService()` and `new DrawLineService(pixelSetService)` manually. These should be constructor-injected.

### 6.2 `PreprocessQRCropService.java`
- Creates `new EnhanceContrastService()`, `new SharpenService()`, `new AdaptiveBinarizeService()`, `new PreprocessQRService(...)` manually instead of injecting them.

---

## 7. Services With Extra Fields Beyond Dependencies

Rule: "No other fields."

### 7.1 `SerializeBallotResultService.java` — ✅ RESOLVED
- ~~Has `private static final String TAG = "SerializeBallotResult";` — a non-dependency field.~~
- **Fixed:** Field removed; the string literal `"SerializeBallotResult"` is now inlined at its single usage site in `loggerWrapper.w(...)`.

### 7.2 `OrchestrateGridDetectionService.java`
- `projectionRenderer` and `overlayRenderer` are not direct dependencies (not Service/State/Repository/Wrapper). They are constructed inside the constructor from other deps.

---

## 8. Data Unit Violations

Rule: "Only field declarations – no methods."

### 8.1 `AxisPeaksData.java`
- **Path:** `ballotprocessor/projection/AxisPeaksData.java`
- **Violation:** Has a compact constructor with logic (`raw = Collections.unmodifiableList(raw)` etc.). Data records must have only field declarations; no methods or logic.

---

## 9. Constant Unit Violations

Rule: "Contains only `static final` fields (primitive, `String`, or immutable collections)."

### 9.1 `DrawConstants.java`
- **Path:** `ballotprocessor/draw/DrawConstants.java`
- **Violation:** Contains `int[] GLYPHS` — a mutable array. Should be wrapped in an immutable collection or `List.of(...)`.

---

## 10. Unused Injected Dependencies

### 10.1 `FindRawPeakService.java` — ✅ RESOLVED
- **Path:** `ballotprocessor/projection/FindRawPeakService.java`
- ~~**Violation:** `mergeClosePeakService` is constructor-injected but never used in `apply()`.~~
- **Fixed:** Dependency and field removed. Constructor is now a no-arg `@Inject` constructor.

---

## 11. Services Not Implementing Corresponding Constants Interface

The following services reference constants (from `GridConstants`, `ProjectionConstants`, etc.) but do not implement the corresponding interface:

- `ImageNormalizerService` — uses `GridConstants` but does not implement it
- `BallotProcessingService` — uses no constants (OK)
- `BallotPreprocessService` — uses no constants (OK)
- `GrayPlanarToGrayService` — uses no constants (OK)
- `PreprocessQRCropService` — uses no constants (OK)
- `DetectGridRegionAndCheckboxService` — uses no constants (OK)
- `OrchestrateGridDetectionService` — uses constants indirectly via `FindRawPeakService`, `MergeClosePeakService`, `ReconstructEdgeService` but does not implement any Constants interface

**Actually compliant (implement Constants):**
- `XDetectService` → implements `XDetectConstants` ✅
- `FindRawPeakService` → implements `ProjectionConstants` ✅
- `FindRowBoundaryService` → implements `ProjectionConstants` ✅
- `MergeClosePeakService` → implements `ProjectionConstants` ✅
- `ReconstructEdgeService` → implements `ProjectionConstants` ✅
- `ArucoMarkerDetectionService` → implements `ArucoMarkerDetectionConstants` ✅
- `DrawTextService` → implements `DrawConstants` ✅
- `FindGridBoundaryService` — uses `GridConstants` but does not implement it (also violates unit type)
- `ImageNormalizerService` — uses `GridConstants.NORMALIZE_SIZE_DIVIDER` and `GridConstants.NORMALIZE_SCALE` but does not implement `GridConstants`

---

## 12. Violations Summary Table

| File | Severity | Violations |
|---|---|---|

| `InverterService.java` | CRITICAL | Static method, no DI |
| `FindGridBoundaryService.java` | CRITICAL | Static method, no DI |
| `GridOverlayDebugRenderer.java` | CRITICAL | Not a valid unit type, no DI |
| `ProjectionDebugRenderer.java` | CRITICAL | Not a valid unit type, dual constructors |
| `XMarkDebugRenderer.java` | CRITICAL | Not a valid unit type, dual constructors |
| `ImageSaver.java` | MAJOR | Interface without unit type suffix |
| `PreprocessingStep.java` | MAJOR | Interface without unit type suffix |
| `AdaptiveBinarizeService.java` | CRITICAL | No @Inject constructor |
| `EnhanceContrastService.java` | CRITICAL | No @Inject constructor |
| `SharpenService.java` | CRITICAL | No @Inject constructor |
| `PreprocessQRService.java` | CRITICAL | No @Inject constructor |
| `OrchestrateGridDetectionService.java` | CRITICAL | Manual dependency creation, >25 LoC, extra fields |
| `PreprocessQRCropService.java` | CRITICAL | Manual dependency creation, >25 LoC |
| `BallotProcessingService.java` | MAJOR | >25 LoC |
| `SerializeBallotResultService.java` | MAJOR | >25 LoC, overloaded apply |
| `ArucoWarpService.java` | MAJOR | >25 LoC |
| `ArucoMarkerDetectionService.java` | MAJOR | >25 LoC |
| `XDetectService.java` | MAJOR | >25 LoC |
| `XMarkDetectService.java` | MAJOR | >25 LoC |
| `ParseQrResultService.java` | MAJOR | >25 LoC |
| `ReconstructEdgeService.java` | MAJOR | >25 LoC |
| `DrawOvalService.java` | MAJOR | >25 LoC |
| `AxisPeaksData.java` | MAJOR | Compact constructor with logic |
| `DrawConstants.java` | MAJOR | Mutable array field |
| `ComputeRowProjectionService.java` | MAJOR | Overloaded apply, >25 LoC |
| `MergeClosePeakService.java` | MAJOR | Overloaded apply |
| `PairEdgeService.java` | MAJOR | Overloaded apply |

---

## 13. Files Without Violations (49 of 93)

These files fully comply with AGENTS.md coding rules:

- `BallotErrorData.java`
- `BallotNonconformityData.java`
- `BallotProcessingOutcomeData.java`
- `BallotResultData.java`
- `GridConstants.java`
- `LocaleState.java`
- `MessageService.java`
- `PreprocessResultData.java`
- `CellPositionData.java`
- `CropService.java`
- `EdgeSegmentData.java`
- `LoggerWrapper.java`
- `PointData.java`
- `RectangleData.java`
- `RowBoundaryData.java`
- `DebugImageSaver.java`

- `RectFillService.java`
- `SetPixelService.java`
- `NoOpDebugImageSaverModule.java`
- `SzavazasCoreComponent.java`
- `SzavazasCoreModule.java`
- `GridDetectionResultData.java`
- `GridRegionData.java`
- `ProjectionBuildResultData.java`
- `ComputeColumnProjectionService.java`
- `FindMaxPeakService.java`
- `ProjectionConstants.java`
- `ProjectionData.java`
- `ConvertGrayU8ToRgbPixelsService.java`
- `DecodeQRService.java`
- `QrCropResultData.java`
- `QrData.java`
- `QrErrorData.java`
- `QrProcessingOutcomeData.java`

- `QrVoteMetadataService.java`
- `BinaryImageOpsWrapper.java`
- `CellDebugData.java`
- `ErosionResultData.java`
- `XMarkDetectionResultData.java`
- `XMarkDebugRendererWrapper.java`
- `XDetectionResultData.java`
- `XDetectConstants.java`
- `ArucoDetectionResultData.java`
- `ArucoDetectionService.java`
- `ArucoMarkerDetectionConstants.java`
- `ArucoMarkersData.java`
- `BallotResultFileRepository.java`
- `ExtractVoteNameService.java`
- `InMemoryBallotResultFileRepository.java`
- `ReviewCellData.java`
- `ReviewGridData.java`
- `ReviewNonconformityData.java`
- `VoteMetadataData.java`

**Newly compliant after Phase 2 fixes:**
- `BallotPreprocessService.java`
- `DetectGridRegionAndCheckboxService.java`
- `DetectGridService.java`
- `DrawLineService.java`
- `DrawRectangleService.java`
- `DrawTextService.java`
- `ExtractGridRegionService.java`
- `FindRawPeakService.java`
- `QrProcessingService.java`
- `SaveBallotResultService.java`

> **Note on `BallotProcessingApi.java`:** Annotated `@Glue` but functions as an API facade with multiple public methods (`apply`, `review`, `save`, `confirm`). Glue units should have "no business logic." This unit is closer to a Delegate pattern but lacks the `@Delegate` annotation. The Service field-naming rule does not explicitly apply to Glue units, so the `Service` suffix on its fields may be acceptable for Glue. However, the classification ambiguity should be resolved.

> **Note on `QrVoteMetadataService.java`:** Trivially delegates to `qrData.voteMetadata()`. While technically compliant, this passthrough service may be unnecessary and could be inlined.