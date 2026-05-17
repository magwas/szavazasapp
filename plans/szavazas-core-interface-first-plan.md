# szavazas-core interface-first two-phase plan

## Goal
Define the future interface boundary now, limited to:
- the app-facing boundary between [`app`](../app) and [`szavazas-core`](../szavazas-core)
- the production interfaces that current tests execute against in [`szavazas-core/src/test/java`](../szavazas-core/src/test/java)

Phase 1 changes only these external interfaces and adds compatibility layers where needed.
Phase 2 refactors internals of [`szavazas-core/src/main/java`](../szavazas-core/src/main/java) to the final compliant architecture without requiring further changes in app or tests.

## Design constraints
- App and tests must not need additional edits during Phase 2.
- Phase 1 may change app/test code once to the new stable boundary.
- Internal service decomposition, Java conversion, Dagger wiring, and AGENTS compliance are deferred behind this boundary.
- Only externally used entrypoints and test-executed production APIs are in scope.

## Current externally used production interfaces

### App-facing
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)

### Test-executed production APIs
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4)
- [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6)
- [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt:6)
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)
- [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6)
- [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7)

## Stable target interfaces for Phase 1

## 1. Top-level ballot processing boundary
Replace direct external dependence on the current callback constructor shape of [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25) with a stable synchronous API.

### New stable external interface
Create a new production interface:
- `BallotProcessingApi`

Proposed shape:
- `fun process(planar: Planar<GrayU8>): BallotProcessingOutcome`

Where `BallotProcessingOutcome` is a sealed-style result carrier represented in Kotlin now and convertible later to Java:
- success with ballot result payload
- failure with error message payload

Proposed payload types:
- `BallotResultData`
- `BallotErrorData`
- `BallotProcessingOutcome`

### Why
- removes callback semantics from the stable boundary
- lets Phase 2 swap orchestration internals freely
- gives app and tests a single stable return contract
- allows legacy [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25) to remain temporarily as an adapter if needed

### Phase 1 migration expectation
App and integration tests move from:
- constructing [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- capturing `onResult` and `onError`

to:
- constructing or obtaining `BallotProcessingApi`
- inspecting returned `BallotProcessingOutcome`

### Compatibility adapter
Keep [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25) temporarily, but redefine it as an adapter over `BallotProcessingApi`.
This preserves old internal behavior during Phase 1 while making the new interface the only boundary app/tests should use going forward.

## 2. QR processing boundary
Current external seam [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) is callback-based and exists mostly for tests.

### New stable external interface
Create:
- `QrProcessingApi`

Proposed shape:
- `fun detect(image: GrayU8): QrProcessingOutcome`

Proposed payloads:
- `QrResultData`
- `QrProcessingOutcome`

### Why
- removes callback machinery from the stable test seam
- allows [`SyncQRProcessor`](../szavazas-core/src/test/java/SyncQRProcessor.kt) to be replaced once in Phase 1 and then never touched again
- allows [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10) to become an implementation detail in Phase 2

### Compatibility adapter
Keep [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) temporarily as an adapter or legacy shim during Phase 1 if needed, but tests should migrate to `QrProcessingApi` as the stable seam.

## 3. Debug image output boundary
[`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) is the main app/test extension point for debug output.

### Stable target interface
Keep the concept, but make the external contract explicit and future-proof.

Preferred Phase 1 shape:
- `interface DebugImageOutput`

Methods:
- `fun saveGray(image: GrayU8, fileName: String)`
- `fun savePlanar(image: Planar<GrayU8>, fileName: String)`

### Why
- removes `Any` typing from the long-term boundary
- makes app and test adapters stable before internal refactor
- allows Phase 2 internals to route all debug output through explicit methods

### Compatibility adapter
Keep [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) as a temporary adapter around `DebugImageOutput` during Phase 1 if needed. The app and tests should migrate to `DebugImageOutput` once, then keep that unchanged during Phase 2.

## 4. Result data naming boundary
Current externally visible payloads are Kotlin data classes with names tied to old internals.

### Stable target payload names
Introduce stable outward-facing payloads now:
- `BallotResultData`
- `BallotErrorData`
- `BallotProcessingOutcome`
- `QrResultData`
- `QrErrorData`
- `QrProcessingOutcome`

### Why
- decouples outward names from the temporary implementation classes like [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4) and [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt:6)
- allows Phase 2 to replace internals while preserving payload names used by app/tests

### Compatibility adapter
Old types can be mapped to new outward payloads inside adapters during Phase 1.

## 5. Interfaces executed directly by unit tests
### [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6)
### [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7)

These are already close to the long-term shape and do not need a boundary redesign for the two-phase plan.

Decision:
- keep both interfaces as-is for the stable test boundary
- allow Phase 2 internal refactor around them without changing the test surface

## Recommended Phase 1 migration targets
App and tests should end Phase 1 using only these stable APIs:
- `BallotProcessingApi`
- `BallotProcessingOutcome`
- `BallotResultData`
- `BallotErrorData`
- `QrProcessingApi`
- `QrProcessingOutcome`
- `QrResultData`
- `QrErrorData`
- `DebugImageOutput`
- existing [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6)
- existing [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7)

They should stop depending directly on:
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25) as the primary API
- [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) as the primary API
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10) as a selected external implementation
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) as a loose `Any`-based contract
- legacy result payload types at the app/test boundary

## Phase 2 guarantee enabled by this design
Once Phase 1 is complete, Phase 2 can:
- replace internals of [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- delete or internalize [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6)
- hide [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- split services and wrappers freely
- convert Kotlin implementation code to Java
- introduce Dagger and AGENTS-compliant layering

without further edits in app or tests, because the stable boundary already carries the final semantics.

## Practical recommendation
Do not modify internal implementation yet. In the next step, change only the external-facing contracts and the immediate app/test consumers to the stable interfaces above, while keeping adapters over the current implementation. That yields a frozen boundary for the later compliant rewrite.
