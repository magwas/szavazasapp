# Remaining szavazas-core Java conversion plan

## Current boundary status
Phase 1 boundary interfaces and adapters now exist in [`szavazas-core/src/main/java`](../szavazas-core/src/main/java):
- [`BallotProcessingApi`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingApi.kt)
- [`BallotProcessingOutcome`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcome.kt)
- [`BallotResultData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.kt)
- [`BallotErrorData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotErrorData.kt)
- [`DebugImageOutput`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/DebugImageOutput.kt)
- [`QrProcessingApi`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrProcessingApi.kt)
- [`QrProcessingOutcome`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrProcessingOutcome.kt)
- [`QrResultData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrResultData.kt)
- [`QrErrorData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrErrorData.kt)
- adapters:
  - [`LegacyBallotProcessorApiAdapter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/LegacyBallotProcessorApiAdapter.kt)
  - [`LegacyQrProcessorApiAdapter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/LegacyQrProcessorApiAdapter.kt)
  - [`LegacyImageSaverDebugImageOutputAdapter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/LegacyImageSaverDebugImageOutputAdapter.kt)

## Remaining production conversion goals
The remaining production code under [`szavazas-core/src/main/java`](../szavazas-core/src/main/java) still needs conversion to Java and/or compliant decomposition.

### 1. Replace or internalize legacy interfaces
Current legacy contracts still present:
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt)
- [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt)
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt)
- [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt)
- [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt)
- [`QrData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QrData.kt)
- [`PreprocessResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.kt)
- [`GridDetectionResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectionResult.kt)
- [`GridRegion`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridRegion.kt)
- [`ProjectionData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/projection/ProjectionData.kt)
- [`AxisPeaks`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/projection/AxisPeaks.kt)
- [`CellDebugData`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/CellDebugData.kt)

### 2. Convert foundational utility/data layer to Java
Likely first Java slice:
- [`Point`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Point.kt)
- [`Rect`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Rect.kt)
- [`GridConstants`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.kt)
- utility singleton objects:
  - [`Crop`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Crop.kt)
  - [`Inverter`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/Inverter.kt)
  - [`ImageNormalizer`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/ImageNormalizer.kt)
  - [`Logger`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.kt)
  - projection helpers

### 3. Decompose algorithm-heavy orchestration into service slices
Large logic units that cannot remain as-is under AGENTS:
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt)
- [`BallotPreprocessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.kt)
- [`BoofCVArucoDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco/BoofCVArucoDetector.kt)
- [`GridDetectionOrchestrator`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridDetectionOrchestrator.kt)
- [`GridBoundaryFinder`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridBoundaryFinder.kt)
- [`GridRegionExtractor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/GridRegionExtractor.kt)
- [`XDetector`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XDetector.kt)
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt)
- QR preprocessing classes in [`qr/preprocess`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess)

### 4. Convert remaining debug and helper structures
- debug renderers under [`debug`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug)
- especially [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt), [`GridOverlayDebugRenderer`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/GridOverlayDebugRenderer.kt), [`ProjectionDebugRenderer`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ProjectionDebugRenderer.kt), and [`XMarkDebugRenderer`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/XMarkDebugRenderer.kt)

## Recommended implementation order
1. Convert shared data and constants first.
2. Convert wrappers around external libraries next.
3. Split and convert QR preprocessing and QR decode.
4. Split and convert grid / X / projection logic.
5. Rebuild ballot orchestration last.
6. Remove or internalize legacy adapters once app/tests have been migrated to the stable boundary.

## Risks still present
- Kotlin files remain in production, so no full Java-only conversion exists yet.
- Legacy classes still overlap with the new stable boundary, so the codebase currently has both old and new API shapes.
- Several current adapters are provisional and may need replacement with cleaner Java-compatible bridges during the later compliant rewrite.
- The new stable types are present, but app and tests still target legacy call sites, so Phase 1 is not complete until consumers switch.

## Immediate next step
Start with the foundational Java slice: convert the shared data/constant/utility layer under [`common`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common), [`debug`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug), and [`GridConstants`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.kt) before touching orchestration classes.
