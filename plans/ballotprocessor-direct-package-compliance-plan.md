# Compliance Plan: `hu.kdea.szavazas.ballotprocessor` Direct Package

## Scope

This plan covers only files directly under [`hu.kdea.szavazas.ballotprocessor`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor), excluding subpackages such as [`aruco`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/aruco), [`common`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common), [`debug`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/debug), [`draw`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/draw), [`grid`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid), [`qr`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/qr), and [`x`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x).

## Decisions Already Made

- Remove legacy duplicates now.
- Standardize on `*Service` production logic classes.
- Standardize on `*Data` records.
- Remove direct-package legacy classes [`BallotPreprocessor.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.java), [`BallotResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java), and [`PreprocessResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java).

## Current File Review

| File | Unit Type | Status | Main Issues |
|---|---|---|---|
| [`BallotErrorData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotErrorData.java) | Data | OK | None |
| [`BallotPreprocessor.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.java) | Mixed | Remove | Legacy duplicate of [`BallotPreprocessService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessService.java) |
| [`BallotPreprocessService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessService.java) | Service | Refactor | Returns legacy [`PreprocessResult`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java); does not implement a constants interface |
| [`BallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingApi.java) | API / Glue-facing contract | Keep | Accept as public API contract used by Dagger glue |
| [`BallotProcessingOutcome.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcome.java) | Mixed | Remove | Replaced by [`BallotProcessingOutcomeData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcomeData.java) |
| [`BallotProcessingOutcomeData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcomeData.java) | Data | OK | None |
| [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java) | Service | Refactor | Too large; helper logic embedded; field naming rule violations; builds DTO manually |
| [`BallotResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java) | Data | Remove | Legacy duplicate of [`BallotResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.java) |
| [`BallotResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.java) | Data | Keep | Canonical ballot result DTO |
| [`DefaultBallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/DefaultBallotProcessingApi.java) | Delegate-like API adapter | Refactor | Should be explicitly treated as Glue or Delegate; naming and annotation need clarity |
| [`GrayPlanarToGrayService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GrayPlanarToGrayService.java) | Service | Minor refactor | Slightly too large for strict service rule; should implement constants interface if constants are extracted |
| [`GridConstants.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.java) | Constants | Refactor | Contains constants already identified as belonging in subpackage-specific constants units |
| [`LocaleState.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/LocaleState.java) | State | OK | None |
| [`Logger.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.java) | Wrapper-like legacy static utility | Refactor later or isolate | Static singleton pattern is non-compliant, but current wrapper indirection already exists in [`LoggerWrapper.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/LoggerWrapper.java) |
| [`MessageService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/MessageService.java) | Service | OK | None |
| [`PreprocessResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java) | Data | Remove | Legacy duplicate of [`PreprocessResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java) |
| [`PreprocessResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java) | Data | Keep | Canonical preprocess DTO |

## Required Refactoring Steps

### Step 1: Remove direct legacy duplicates

Delete:
- [`BallotPreprocessor.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessor.java)
- [`BallotResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java)
- [`PreprocessResult.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java)
- [`BallotProcessingOutcome.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcome.java)

Then update all direct and subpackage references to the canonical types:
- [`BallotPreprocessService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessService.java)
- [`BallotResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.java)
- [`PreprocessResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java)
- [`BallotProcessingOutcomeData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcomeData.java)

### Step 2: Standardize preprocess result usage

Refactor [`BallotPreprocessService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessService.java):
- Change return type from legacy [`PreprocessResult`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java) to [`PreprocessResultData`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java).
- Replace all constructor calls to use [`PreprocessResultData`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResultData.java).
- Update every caller, including [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java).

### Step 3: Standardize ballot result usage

Refactor all producers and consumers of ballot result objects:
- Replace [`BallotResult`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java) with [`BallotResultData`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.java).
- Update subpackage references such as [`XMarkDetectionResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectionResultData.java) and [`XMarkDetectionAndResultService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectionAndResultService.java).
- Remove redundant DTO conversion in [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java) once upstream already returns [`BallotResultData`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResultData.java).

### Step 4: Simplify processing outcome model

Remove legacy [`BallotProcessingOutcome.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcome.java) and keep only [`BallotProcessingOutcomeData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcomeData.java) as the outcome transport type.

### Step 5: Refactor [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java)

Bring it closer to service rules:
- Keep exactly one public method: [`apply()`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java:48).
- Rename dependency fields to match class names precisely:
  - `ballotPreprocess` → `ballotPreprocessService`
  - `qrCropPreprocessingService` → `preprocessQRCropService`
  - `gridRegionAndCheckboxDetectionService` already aligns with [`DetectGridRegionAndCheckboxService`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/grid/DetectGridRegionAndCheckboxService.java)
- Extract private helper services if needed so main service body stays within the intended LoC limit.
- Split exception-to-error mapping into a dedicated service if necessary.
- Return [`BallotProcessingOutcomeData`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcomeData.java) built from canonical DTOs only.

Recommended extracted services:
- `CreateBallotProcessingErrorService`
- `BuildBallotProcessingOutcomeService`
- `ProcessBallotPipelineService`

### Step 6: Clarify API adapter role

Refactor [`DefaultBallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/DefaultBallotProcessingApi.java):
- Treat it as Glue because it is an adapter exposed by Dagger through [`SzavazasCoreComponent.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/glue/SzavazasCoreComponent.java).
- Add [`@Glue`](konveyor/src/main/java/io/github/magwas/konveyor/annotations/Glue.java) annotation.
- Keep it thin, delegating directly to [`BallotProcessingService.apply()`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java:48).

### Step 7: Decide treatment of [`BallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingApi.java)

Recommended treatment:
- Keep as public API contract.
- Consider it part of Glue-facing API surface rather than a domain unit.
- No business logic should live there.

### Step 8: Reduce direct-package service size where practical

#### [`GrayPlanarToGrayService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GrayPlanarToGrayService.java)

Options:
- Keep as-is if line counting is interpreted pragmatically.
- Or extract grayscale coefficients into a new constants interface such as `GrayPlanarToGrayConstants`, and if needed extract per-pixel conversion into a helper service.

Recommended minimum change:
- Add constants interface only if refactoring is already touching the file.
- Keep constructor injection pattern unchanged.

#### [`MessageService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/MessageService.java)

No structural change required.

### Step 9: Clean direct-package constants ownership

Refactor [`GridConstants.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.java):
- Remove constants already allocated to subpackage-specific concerns, especially X-detection constants already planned for [`XDetectConstants.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XDetectConstants.java).
- Leave only grid-wide constants actually owned by the direct package or by the grid package plan.

Expected removal candidates from [`GridConstants.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.java):
- `X_MARGIN`
- `ERODE_KERNEL_SIZE`
- `ERODE_ITERATIONS`
- `MIN_BRANCHES`

### Step 10: Keep [`LocaleState.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/LocaleState.java) and [`BallotErrorData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotErrorData.java) unchanged

These already comply with their intended roles.

### Step 11: Isolate legacy static logging

[`Logger.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.java) is not compliant as a static singleton utility. Since [`LoggerWrapper.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/LoggerWrapper.java) already exists and subpackages are out of scope for this pass:
- Do not expand direct usage of [`Logger.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.java).
- Keep all new and refactored code dependent on [`LoggerWrapper.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/common/LoggerWrapper.java).
- Optionally schedule later replacement of [`Logger.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/Logger.java) with a proper wrapper-owned backend.

## Reference Updates Required Outside the Direct Package

Although this analysis targets only the direct package, the selected duplicate-removal strategy requires updating some external references:
- [`XMarkDetectionResultData.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectionResultData.java)
- [`XMarkDetectionAndResultService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectionAndResultService.java)
- Any file still referencing [`PreprocessResult`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/PreprocessResult.java)
- Any file still referencing [`BallotResult`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotResult.java)
- Any file still referencing [`BallotProcessingOutcome`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingOutcome.java)

## Suggested Execution Order

1. Update all references from legacy DTOs to canonical `*Data` records.
2. Refactor [`BallotPreprocessService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotPreprocessService.java).
3. Refactor [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java).
4. Refactor [`DefaultBallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/DefaultBallotProcessingApi.java).
5. Trim [`GridConstants.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/GridConstants.java).
6. Delete legacy duplicate files.
7. Run compile/test verification.

## Expected End State

After the refactor:
- The direct package uses only canonical records ending with `Data`.
- The duplicate legacy classes are removed.
- [`BallotProcessingService.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/BallotProcessingService.java) delegates more of its work into smaller services.
- [`DefaultBallotProcessingApi.java`](szavazas-core/src/main/java/hu/kdea/szavazas/ballotprocessor/DefaultBallotProcessingApi.java) is explicitly marked as Glue.
- Direct-package code no longer introduces new static singleton coupling.
- Constants ownership is aligned with package responsibilities.
