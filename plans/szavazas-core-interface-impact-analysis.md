# szavazas-core external interface impact analysis

## Scope of analysis
This document analyzes how code outside production `szavazas-core` currently uses production interfaces from [`szavazas-core/src/main/java`](../szavazas-core/src/main/java), focusing on:
- the Android app module in [`app/src`](../app/src)
- tests in [`szavazas-core/src/test/java`](../szavazas-core/src/test/java)

It then maps those usages to the interface changes expected from a strict [`AGENTS.md`](../AGENTS.md:1) compliant Java refactor.

## App module usage of szavazas-core

### Direct production types referenced by the app
The app currently imports and uses these production interfaces/classes:
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)

### Current app integration shape
[`MainActivity`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:20) constructs the core entrypoint directly via [`buildBallotProcessor()`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:51).

Current constructor contract of [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25):
- callback `onResult`
- callback `onError`
- optional injected-at-construction QR implementation `qrProcessor`
- optional debug saver `debugSaver`

[`MainActivity.processBallot()`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:81) then calls [`BallotProcessor.process()`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:38) with `Planar<GrayU8>`.

[`AndroidImageSaver`](../app/src/main/java/hu/kdea/szavazas/AndroidImageSaver.kt:12) implements [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) and accepts the very loose [`save()`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:11) contract with `Any` plus filename.

### How these app-facing interfaces change in a compliant refactor

#### [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
Current role:
- mutable orchestrator class
- direct dependency creation
- callback-based result/error delivery
- public method [`process()`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:38)

Compliant refactor impact:
- likely replaced by a one-method service such as `BallotProcessingService.apply` plus optional delegate/glue wrapper
- constructor callbacks disappear because service constructors may only perform injection
- errors/results must be returned as immutable data, not pushed through callbacks
- `MainActivity` can no longer `new` the whole dependency graph manually if strict constructor injection is applied end-to-end

Expected new app-facing contract:
- app obtains a Dagger-provided delegate or top-level service from glue
- app calls something like `apply(planar)`
- return type becomes a result record such as `BallotProcessingResultData` or `Optional<BallotResultData>` plus failure data

Practical app changes:
- [`buildBallotProcessor()`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:51) becomes a Dagger component lookup or factory call
- [`toastOnUi()`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:58) will consume returned data instead of callback side effects
- [`processBallot()`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:81) must branch on returned success/failure objects

#### [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
Current role:
- app can directly choose the QR implementation and pass it into [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)

Compliant refactor impact:
- likely no longer app-facing
- becomes an internal wrapper or service behind Dagger wiring
- callback-based [`IQRProcessor.detect()`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:7) disappears

Practical app changes:
- [`MainActivity`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:20) should stop importing or constructing [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- QR decoding becomes an internal implementation detail of the core component

#### [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)
Current role:
- external extension point implemented by the app in [`AndroidImageSaver`](../app/src/main/java/hu/kdea/szavazas/AndroidImageSaver.kt:12)
- weakly typed `save(Any, String)` API

Compliant refactor impact:
- under AGENTS this is correctly a wrapper concern, but it should become Java and probably more explicit
- the broad `Any` parameter is a weak contract and may split into typed wrapper methods or typed image payload data
- if kept externally implementable, the app still provides the wrapper implementation, but under Java and constructor injection

Expected new app-facing contract:
- either a Java wrapper interface/class with explicit image methods
- or app no longer implements it directly, and instead provides a platform wrapper bound into Dagger glue

Practical app changes:
- [`AndroidImageSaver`](../app/src/main/java/hu/kdea/szavazas/AndroidImageSaver.kt:12) likely becomes a Java or Kotlin glue/wrapper adapter implementing a stricter API
- callsites inside core stop treating debug save as `Any`

## Test usage of szavazas-core

### Direct production types referenced by tests
Tests currently reference these production interfaces/classes:
- [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4)
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)
- [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6)
- [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt:6)
- [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6)
- [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7)
- utility constants and data carriers reachable from those APIs

### Integration-style test surface
[`BallotTestExecutor`](../szavazas-core/src/test/java/BallotTestExecutor.kt:12) uses:
- [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4) as the captured success value
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) via [`AwtImageSaver`](../szavazas-core/src/test/java/AwtImageSaver.kt:11)
- direct manual construction of [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)
- callback capture for success and failure

[`SyncQRProcessor`](../szavazas-core/src/test/java/SyncQRProcessor.kt:8) exists specifically to implement the callback-based [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) test seam.

### Unit-test surface
[`MorphologicalClosingServiceTest`](../szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingServiceTest.java:12) manually constructs [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6) with a mocked [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7). This test style is already close to AGENTS expectations.

## How test-facing interfaces change in a compliant refactor

### [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4)
Current role:
- Kotlin `data class`
- success payload for callback

Compliant refactor impact:
- renamed to a Java record ending with `Data`, likely `BallotResultData`
- test assertions must use the new type and new package names if reorganized

### [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6) and [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt:6)
Current role:
- callback-based seam around QR decode
- test adapter in [`SyncQRProcessor`](../szavazas-core/src/test/java/SyncQRProcessor.kt:8)

Compliant refactor impact:
- interface likely disappears entirely
- decode becomes direct-return service/wrapper composition
- `SyncQRProcessor` becomes unnecessary or gets replaced by a wrapper stub / service stub
- [`QrResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/QRResult.kt:6) likely becomes `QrResultData` or is collapsed into `QrData`

### [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4)
Current role:
- implemented in tests by [`AwtImageSaver`](../szavazas-core/src/test/java/AwtImageSaver.kt:11)

Compliant refactor impact:
- tests may still provide a wrapper implementation, but signature will likely become stricter and Java-based
- image debug output could also be moved behind a dedicated wrapper stub instead of real file output for many tests

### [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6) and [`MorphologicalClosingWrapper`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingWrapper.java:7)
Current role:
- already close to compliant service/wrapper shape

Compliant refactor impact:
- likely minimal, though package names or auxiliary constants may change
- current testing pattern of manual constructor injection and wrapper stub should survive almost unchanged

## Compatibility summary

### Interfaces likely to disappear from external consumers
- [`IQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/IQRProcessor.kt:6)
- direct construction of [`ZXingQRProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/ZXingQRProcessor.kt:10)
- callback constructor contract of [`BallotProcessor`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessor.kt:25)

### Interfaces likely to survive but with renamed/retyped forms
- [`BallotResult`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.kt:4) to `BallotResultData`
- [`ImageSaver`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug/ImageSaver.kt:4) to a stricter Java wrapper API
- [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6) with minor adjustments only

### Highest migration cost for external code
1. [`MainActivity`](../app/src/main/java/hu/kdea/szavazas/MainActivity.kt:20) because it manually wires the whole pipeline and depends on callback behavior.
2. [`BallotTestExecutor`](../szavazas-core/src/test/java/BallotTestExecutor.kt:12) because it mirrors the same callback-centric construction pattern.
3. [`SyncQRProcessor`](../szavazas-core/src/test/java/SyncQRProcessor.kt:8) because its only reason to exist disappears in the compliant design.
4. [`AndroidImageSaver`](../app/src/main/java/hu/kdea/szavazas/AndroidImageSaver.kt:12) and [`AwtImageSaver`](../szavazas-core/src/test/java/AwtImageSaver.kt:11) because the debug save contract will likely be typed differently.

## Recommended compatibility strategy before refactor
1. Freeze a single top-level external contract for app consumption, preferably a Dagger-provided one-method delegate/service.
2. Decide whether debug save remains an app/test extension point or becomes fully internal glue.
3. Replace callback-based success/error delivery with a single immutable result type before deep algorithm refactoring, so app and tests can migrate once.
4. Introduce adapter layers temporarily if needed:
   - legacy callback adapter around the new top-level service
   - legacy debug saver adapter around the new wrapper API
5. Keep wrapper/service constructor injection patterns matching the already good example in [`MorphologicalClosingService`](../szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/MorphologicalClosingService.java:6).
