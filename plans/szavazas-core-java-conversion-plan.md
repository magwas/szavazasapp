# szavazas-core Java conversion plan

## Scope
- Convert only production sources under `szavazas-core/src/main/java` from Kotlin to Java.
- Prioritize strict compliance with [`AGENTS.md`](../AGENTS.md:1), even when this requires redesigning public APIs, introducing more files, and reshaping packages.
- Leave tests in `szavazas-core/src/test/java` for adaptation after production migration.

## Current-state findings
- The module in [`szavazas-core/build.gradle.kts`](../szavazas-core/build.gradle.kts:1) is mixed Java and Kotlin, so a strict Java conversion will also require removing Kotlin production compilation from the module or at least removing Kotlin production sources after replacement.
- Most production units are Kotlin `class`, `object`, `data class`, or interface definitions under [`szavazas-core/src/main/java`](../szavazas-core/src/main/java).
- Existing production code is not structurally compliant with [`AGENTS.md`](../AGENTS.md:1):
  - orchestration and logic are concentrated in large classes such as [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25), [`BoofCVArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/BoofCVArucoDetector.kt:24), [`GridDetectionOrchestrator`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectionOrchestrator.kt:15), and [`XDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XDetector.kt:10)
  - several interfaces are behavioural rather than repository or delegate shaped, for example [`IArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/IArucoDetector.kt:7), [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6), [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4), and [`PreprocessingStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/PreprocessingStep.kt:5)
  - Kotlin `data class` types include methods or have names not ending in `Data`, for example [`Rect`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Rect.kt:4)
  - Kotlin `object` singletons such as [`Crop`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Crop.kt:5), [`ImageNormalizer`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/ImageNormalizer.kt:8), [`Inverter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Inverter.kt:5), [`Logger`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.kt:3), and projection helpers mix utility logic with direct static access patterns that conflict with service and wrapper rules
  - constructors currently create dependencies directly instead of using constructor injection, for example [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
  - callback-based APIs and function-typed properties rely on Kotlin features with no direct compliant Java equivalent, especially [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:26), [`IQRProcessor.detect`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:7), and [`Logger.delegate`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.kt:4)

## Target architecture
- Replace Kotlin production files with Java files only.
- Reclassify production code into AGENTS-compliant unit types:
  - `Data` records for immutable values
  - `Service` classes for every piece of logic, each with one public `apply` method
  - `Wrapper` classes for BoofCV, ZXing, logging, and debug image output access
  - `Constants` interfaces for thresholds and magic values
  - `State` only where transient mutable context is unavoidable
  - `Glue` for Dagger modules and components needed to wire the pipeline
- Break monolithic classes into many small services, targeting one responsibility per service and keeping each service within the mandated size ceiling.
- Replace direct object utility calls with injectable services or wrappers.
- Introduce Dagger wiring to create top-level entrypoints instead of internal `new` calls.

## Required interface redesign
- Replace [`IArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/IArucoDetector.kt:7) with a combination of wrapper and service units, for example:
  - `ArucoScanWrapper`
  - `BallotCornerDetectionService`
  - `BallotWarpService`
  - `BottomMarkerTopProjectionService`
- Replace [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) callback style with synchronous data-returning services and wrappers, for example:
  - `QrDecodeWrapper`
  - `QrDecodeService.apply` returning `Optional<QrData>` or nullable-equivalent Java result handling
- Replace [`PreprocessingStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/PreprocessingStep.kt:5) pipeline polymorphism with either:
  - separate preprocessing services composed by a pipeline delegate or service, or
  - wrappers for primitive operations and a dedicated pipeline service
- Replace [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) with a wrapper because it is external I/O.
- Replace function callback entrypoints in [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25) with data-returning services and optional wrapper/delegate based notification handled outside core logic.
- Rename data holders so every immutable carrier ends with `Data`, for example:
  - `Rect` to `RectData`
  - `Point` to `PointData`
  - `GridRegion` already nearly compliant but should become `GridRegionData`
  - `ProjectionData`, `AxisPeaks`, `QrData`, `BallotResult`, `PreprocessResult`, `CellDebugData` need naming normalization where needed

## Anticipated problems
1. **This is not a syntax-only Kotlin-to-Java port.** Strict compliance forces a domain redesign, so many current types will disappear or be split.
2. **File count will increase substantially.** Large classes must be decomposed into many small services, wrappers, constants, and records.
3. **Public API breakage is expected.** Existing constructors, callback signatures, and class names are incompatible with the mandated architecture.
4. **Tests will break immediately after production migration.** Existing tests reference Kotlin types, old names, static utility shapes, and non-compliant APIs.
5. **Build configuration must change.** Kotlin production compilation may need to be removed from [`szavazas-core/build.gradle.kts`](../szavazas-core/build.gradle.kts:1) once all production files are Java, while tests may still require Kotlin support if left unchanged.
6. **Dagger integration work is unavoidable.** Current direct instantiation patterns violate constructor injection requirements.
7. **BoofCV and ZXing adaptation will be tedious.** Their operations are currently embedded inside logic-heavy classes and need wrapper extraction without leaking external concerns back into services.
8. **Some AGENTS rules may force awkward micro-services.** Algorithms such as grid detection, projection analysis, and perspective correction are larger than the allowed service size and need staged decomposition.
9. **Data model churn will cascade across packages.** Renaming `Rect`, `Point`, and similar types will touch almost every production file.
10. **Debug rendering needs a decision.** Rendering and image saving combine logic plus I/O today, so they will likely split into pure geometry services plus save wrappers.
11. **Kotlin nullability semantics must be redesigned explicitly.** Java equivalents will need `Optional`, nullable conventions, or result records.
12. **Singleton utilities and companion objects have no compliant direct equivalent.** They must become injected services, wrappers, or constants.

## Recommended migration sequence
1. Freeze target package map and naming rules for all `Data`, `Service`, `Wrapper`, `Constants`, `State`, and `Glue` units.
2. Convert foundational immutable models and constants first.
3. Extract external-operation wrappers for logging, image save, BoofCV marker scan, perspective transform, binary morphology, skeletonization, and ZXing decode.
4. Decompose preprocessing, projection, grid detection, QR decode, and X-mark detection into small services.
5. Rebuild ballot orchestration as a Dagger-wired top-level service plus optional delegate.
6. Remove obsolete Kotlin production files and update Gradle.
7. Run and then repair tests in a separate implementation pass.

## Proposed execution todo for implementation mode
- Inventory and rename all production `Data` carriers to AGENTS-compliant Java records
- Design Dagger glue for the production pipeline in `szavazas-core`
- Replace singleton utilities with injected Java services or wrappers
- Split ArUco processing into wrappers and small services
- Split QR preprocessing and decode into wrappers and small services
- Split projection and grid detection into wrappers and small services
- Split X-mark detection and debug generation into wrappers and small services
- Rebuild ballot preprocessing and ballot processing as strict one-method services
- Remove Kotlin production sources and align Gradle with Java-only production
- Adapt or replace broken tests in a later dedicated pass
