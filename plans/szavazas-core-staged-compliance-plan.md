# `szavazas-core` staged compliance implementation plan

## Why the one-pass rewrite is unsafe

A full strict-compliance rewrite of [`szavazas-core/src/main/java`](../szavazas-core/src/main/java) cannot be responsibly completed in one pass from the current baseline because:

- Business logic is spread across many non-compliant unit types such as [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.java:27), [`BallotPreprocessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.java:9), [`QRDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRDetectorStep.java:7), [`GridDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectorStep.java:9), and [`XMarkDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectorStep.java:11).
- Some foundational value types are also non-compliant, such as [`Rect`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Rect.java:5), [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java:6), [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrResult.java:5), [`GridRegion`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridRegion.java:5), and [`CellDebugData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/CellDebugData.java:8).
- External boundaries are inconsistent, including [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.java:6), [`IArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/IArucoDetector.java:9), [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.java:3), and [`DebugImageOutputWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/DebugImageOutputWrapper.java:6).
- The Android integration in [`MainActivity`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:20) depends directly on removed-or-to-be-removed legacy APIs, so a safe rewrite must isolate `core` changes from app migration.

## Safer implementation strategy

Refactor the production tree in slices that each preserve compile-ability inside [`szavazas-core`](../szavazas-core), while allowing temporary incompatibility with [`app`](../app).

## Slice sequence

### Slice 1. Foundation and boundaries

Purpose:
Create compliant primitives that future services can depend on without touching the full ballot pipeline yet.

Scope:
- Add `LocaleState`
- Add `MessageService`
- Add [`messages.properties`](../szavazas-core/src/main/resources/messages.properties)
- Add Dagger Glue units for composition root
- Introduce new wrapper classes for QR decode, ArUco, and debug output
- Introduce new `Data` records that mirror legacy mutable carriers where needed

Deliverable:
A compile-ready compliance foundation that does not yet remove the legacy pipeline.

### Slice 2. QR pipeline replacement

Purpose:
Replace the current QR orchestration with compliant service units while keeping the rest of ballot processing temporarily on legacy classes.

Scope:
- Replace [`LegacyQrProcessorApiAdapter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/LegacyQrProcessorApiAdapter.java:8)
- Replace [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.java:17) with a wrapper-oriented boundary
- Replace [`QRDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRDetectorStep.java:7)
- Replace [`QRPreprocessingPipeline`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/QRPreprocessingPipeline.java:7) with a service chain
- Convert any QR-specific mutable results to `Data` records

Deliverable:
A new compliant [`QrProcessingApi`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrProcessingApi.java:5) implementation backed only by service and wrapper units.

### Slice 3. Ballot preprocessing replacement

Purpose:
Replace the current ballot preprocessing entry with services and wrappers, but still without touching grid and X detection orchestration.

Scope:
- Replace [`BallotPreprocessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.java:9)
- Replace [`IArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/IArucoDetector.java:9) with a proper wrapper boundary
- Introduce `GrayPlanarToGrayService`
- Introduce `BallotWarpService`
- Convert [`PreprocessResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java:5) usage to [`PreprocessResultData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java:5)

Deliverable:
A compliant ballot-preprocess layer with no constructor assembly and no mutable preprocess carrier.

### Slice 4. Grid and X detection replacement

Purpose:
Split the remaining heavy orchestration into compliant services and retire step classes.

Scope:
- Replace [`GridDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectorStep.java:9)
- Replace [`GridRegionExtractor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridRegionExtractor.java:10)
- Replace [`GridDetectionOrchestrator`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectionOrchestrator.java:20)
- Replace [`XMarkDetectorStep`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectorStep.java:11)
- Reclassify debug renderers either as wrappers or extract their business logic into services

Deliverable:
Separate compliant services for grid region extraction, grid detection, and X-mark detection.

### Slice 5. Final orchestration and legacy removal

Purpose:
Create the final compliant ballot-processing entry and delete the old structure.

Scope:
- Introduce `BallotProcessingService`
- Introduce a compliant implementation of [`BallotProcessingApi`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingApi.java:6)
- Delete [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.java:27)
- Delete [`LegacyBallotProcessorApiAdapter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/LegacyBallotProcessorApiAdapter.java:10)
- Delete remaining obsolete interfaces and adapters that violate the rule set

Deliverable:
The final strict-compliance production core, ready for a later `app` integration rewrite.

## Recommended immediate coding slice

Start with Slice 1 plus the minimum of Slice 2.

Reason:
- It creates compliant base abstractions first.
- It removes the least risky legacy adapter first.
- It gives a working pattern for the larger ballot refactor.
- It avoids deleting the entire pipeline before replacement units exist.

## Implementation todo list for the next code pass

- [ ] Add `LocaleState`, `MessageService`, and [`messages.properties`](../szavazas-core/src/main/resources/messages.properties)
- [ ] Add Dagger Glue units for `szavazas-core`
- [ ] Introduce new QR wrapper boundary and synchronous QR result `Data` flow
- [ ] Implement compliant QR services to replace legacy QR orchestration
- [ ] Migrate [`QrProcessingApi`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrProcessingApi.java:5) to the new compliant implementation
- [ ] Remove QR legacy adapter classes once replacement compiles
- [ ] Reassess remaining ballot pipeline classes before starting Slice 3
