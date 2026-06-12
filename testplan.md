# PITest Mutation Test Plan

Generated from `szavazas-core/build/reports/pitest/mutations.xml` — **1356 surviving mutants across 59 classes**.

**Overall**: 3026 mutations generated, 1670 killed (55%). 347 mutations with no coverage. Test strength 62%.

## Rules

- Services have exactly one public method: `apply`. All tests **must** exercise `apply` only.
- `@DisplayName` describes **behavior** from the user's perspective — this is the ground truth of the functional specification.
- New `TestData` constants go into existing `TestData` interfaces. New `Stub` units where needed.
- No `when()`/`given()` in tests — stubbing belongs in `Stub` units.
- Tests extend `TestBase`.

---

## 1. XMarkDebugRendererService (167 survivors) — NEW

This debugs X-mark cell detection by rendering outlines, erosion, skeleton, and branch points onto the grid binary image. The 167 survivors are spread across its many private rendering methods (`binaryGridToCanvas`, `paintCellBackground`, `paintErodedDifference`, `paintSkeleton`, `paintBranchMarkers`, `paintBranchCount`, `grayColor`).

### 1.1 New tests in `XMarkDebugRendererServiceTest` (existing file)

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 1 | `returns immediately without rendering when the cell list is empty` | `apply`: isEmpty check, VoidMethodCallMutator on all draw calls |
| 2 | `renders cell outlines, extracted cells, skeleton, and branch points when no cells have erosion data` | `apply`: hasErodedCells branch false, all draw methods |
| 3 | `renders erosion overlay when at least one cell contains eroded cell data` | `hasErodedCells`: loop and null check, drawErosionOverlay call |
| 4 | `converts a binary image to a 3-band RGB canvas with correct black/white pixel mapping` | `binaryGridToCanvas`: InlineConstantMutators (0xFF, 0x00), NonVoidMethodCallMutator on get, pixelSet.apply |
| 5 | `renders cell backgrounds by inverting original cell pixel values` | `paintCellBackground`: !=0 negated, ternary 0/255 InlineConstant, grayColor arg |
| 6 | `marks eroded difference pixels where original is foreground and eroded is background in yellow` | `paintErodedDifference`: !=0 check, ==0 check, COLOR_YELLOW, pixelSet.apply |
| 7 | `renders skeleton pixels in magenta on the canvas` | `paintSkeleton`: !=0 check, COLOR_MAGENTA |
| 8 | `draws blue rectangle markers centered at each branch point with a 6x6 fill` | `paintBranchMarkers`: x-3/y-3 InlineConstants, 6 InlineConstant, COLOR_BLUE |
| 9 | `renders branch count and X-detected label in green for detected X and red for non-X` | `paintBranchCount`: ternary xDetected, COLOR_GREEN/COLOR_RED, +8/+2 InlineConstants, 12f |
| 10 | `encodes a grayscale value into an ARGB integer with full alpha channel` | `grayColor`: 0xFF<<24, <<16, <<8 InlineConstantMutators, MathMutators |

---

## 2. ProjectionDebugRendererService (153 survivors) — NEW

Draws projection line charts and peak/pair overlays for debugging grid detection. High survivors in `drawProjectionLine`, `drawPeaks`, `drawPairs`, `blankCanvas`, `maxOrOne`.

### 2.1 New tests in `ProjectionDebugRendererServiceTest` (existing file)

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 11 | `renders all four projection views: column projection, row projection, and both peak-and-pair views` | `apply`: all four draw call arguments, VoidMethodCallMutator |
| 12 | `handles an empty projection gracefully by rendering a warning message and returning` | `drawProjection`: maxVal<=0f branch, "Empty projection" text |
| 13 | `draws a projection line chart scaling the data to fill the canvas width and height` | `drawProjectionLine`: scaleX/scaleY MathMutators, InlineConstants (WIDTH, HEIGHT-10, HEIGHT-20), loop boundaries, x1/y1/x2/y2 |
| 14 | `positions peak markers at their correct scaled coordinates with red filled ovals` | `drawPeaks`: idx bounds check, idx*scaleX, COLOR_RED, 3/6 InlineConstants |
| 15 | `draws blue line segments connecting paired peaks across the projection` | `drawPairs`: start/end bounds check, lineDraw.apply args, COLOR_BLUE |
| 16 | `creates a white-filled blank canvas of the configured dimensions` | `blankCanvas`: WIDTH/HEIGHT InlineConstants, COLOR_WHITE |
| 17 | `returns the maximum value of a non-empty projection or 1.0 for an all-zero projection` | `maxOrOne`: MathMutator on >, max<=0f?1f ternary |

---

## 3. SerializeBallotResultService (147 survivors)

Increased from 88 — the new survivors are in `equals(JSONObject)`, `equals(JSONArray)`, `equals(Object)`, `equals(String)` recursive comparison methods and the `conflicts` method.

### 3.1 New TestData needed in `ReviewTestData`

```
VOTE_METADATA_VOTEID_DIFFERS  — same as VOTE_METADATA but with a different voteId
VOTE_METADATA_VOTENAME_DIFFERS — same as VOTE_METADATA but with a different voteName
VOTE_METADATA_CANDIDATECOUNT_DIFFERS — same as VOTE_METADATA but with different candidateCount
VOTE_METADATA_CANDIDATES_DIFFER — same as VOTE_METADATA but with different candidates list
VOTE_METADATA_SUPPORTCOLUMNCOUNT_DIFFERS — same as VOTE_METADATA but with different supportColumnCount
VOTE_METADATA_ISSUEDBALLOTIDS_DIFFER — same as VOTE_METADATA but with different issuedBallotIds
```

### 3.2 New tests in `SerializeBallotResultServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 18 | `preserves stored vote metadata and appends ballot when incoming ballot has a different voteId` | `conflicts`: voteId equals negated, optString removed, ArgumentPropagation, EQUAL_IF/ELSE |
| 19 | `preserves stored vote metadata and appends ballot when incoming ballot has a different voteName` | `conflicts`: voteName equals negated, optString removed, ArgumentPropagation, EQUAL_IF/ELSE |
| 20 | `preserves stored vote metadata and appends ballot when incoming ballot has a different candidateCount` | `conflicts`: candidateCount optInt removed, EQUAL_IF/ELSE |
| 21 | `preserves stored vote metadata and appends ballot when incoming ballot has different candidates` | `conflicts`: candidates optJSONArray removed, equals removed, EQUAL_IF/ELSE |
| 22 | `preserves stored vote metadata and appends ballot when incoming ballot has a different supportColumnCount` | `conflicts`: supportColumnCount optInt removed, EQUAL_IF/ELSE |
| 23 | `preserves stored vote metadata and appends ballot when incoming ballot has different issuedBallotIds` | `conflicts`: issuedBallotIds optJSONArray removed, equals removed, EQUAL_IF/ELSE |
| 24 | `recognizes vote metadata as matching when stored vote fields are in a different order within each JSON sub-object` | `equals(JSONObject)`: names loop, missing key check, nested equals |
| 25 | `detects mismatch when stored vote has a different number of top-level JSON keys` | `equals(JSONObject)`: length check negated, EQUAL_IF/ELSE |
| 26 | `produces the same serialized output whether the ballot is appended to an empty string or null content` | `apply`: raw() NonVoidMethodCallMutator, content null check, InlineConstantMutator |
| 27 | `detects mismatch when stored candidates array has different number of elements than incoming candidates` | `equals(JSONArray)`: length checks negated, EQUAL_IF/ELSE, InlineConstantMutator |
| 28 | `detects mismatch when stored candidates array has same length but different element values` | `equals(JSONArray)`: loop body mutations, get() removed, inner equals removed, EQUAL_IF/ELSE |
| 29 | `detects mismatch when stored candidates array contains a nested JSON object with different keys` | `equals(Object)`: JSONObject instanceof branch, equals(Object) removed call, BooleanFalse/TrueReturnVals |
| 30 | `detects mismatch when comparison reaches a non-JSON scalar value` | `equals(Object)`: scalar comparison branch, first.equals(second) removed call |
| 31 | `treats null field in stored vote as conflicting with a non-null incoming field of any type` | `equals(String)`: null-both check, equals on String removed, EQUAL_IF/ELSE, BooleanFalse/TrueReturnVals |

---

## 4. ArucoMarkerDetectionService (70 survivors)

Increased from 15 — new survivors are in the marker corner extraction (`get(0)`, `get(1)`, `get(2)`, `get(3)` from List.of), totalFound(), and logger calls.

### 4.1 New tests in `ArucoMarkerDetectionServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 32 | `returns null when the detected markers do not include all required ArUco IDs` | containsAll check negated, EQUAL_IF/ELSE, keySet removed, String::valueOf removed |
| 33 | `returns null when the marker collection returns null` | markers==null check negated, EQUAL_IF/ELSE |
| 34 | `returns a valid ArucoMarkersData with four ballot corners when all required markers are present` | VoidMethodCallMutator on detector::detect, totalFound() removed, loggerWrapper::d calls, NonVoidMethodCallMutator on collectArucoMarkers::apply |
| 35 | `constructs the top-left corner from the second point and top-right from the third point of their respective marker detections` | List.of with marker.get(0/1/2/3), InlineConstantMutators on indices |

---

## 5. DetectBallotNonconformitiesService (62 survivors)

Previously 1 survivor — now has many in the nonconformity check comparisons and message building.

### 5.1 New tests in `DetectBallotNonconformitiesServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 36 | `detects all nonconformities when vote metadata mismatch on voteId, voteName, candidateCount, candidates, supportColumnCount, and issuedBallotIds` | All != comparisons, equals() removed, contains() removed |
| 37 | `detects ballot ID not issued when raw ballot ID is not in the issued ballot IDs list` | contains(raw()) check, EQUAL_IF/ELSE |
| 38 | `detects no nonconformities when QR vote metadata exactly matches the ballot result vote metadata` | All != comparisons producing false, add() not called |
| 39 | `includes the message for each detected nonconformity in the result list` | message.get() removed calls, add() removed calls, List.copyOf() removed |

---

## 6. ValidateEmptySecondColumnService (59 survivors) — NEW

Validates that the second column is empty by checking that the first gap between edge pairs is approximately twice the average of the remaining gaps.

### 6.1 New test file `ValidateEmptySecondColumnServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 40 | `returns false when fewer than two edge pairs are provided` | pairs.size()<2 check negated, EQUAL_IF/ELSE, BooleanTrueReturnVals |
| 41 | `returns false when only two edge pairs result in an empty other-gaps list` | otherGaps.isEmpty() check, BooleanTrueReturnVals |
| 42 | `returns true when the first gap is approximately double the average of the remaining gaps within 50% tolerance` | Math.abs, 2*avgOtherGap, avgOtherGap*0.5, > comparison |
| 43 | `returns false when the first gap is significantly different from double the average of the remaining gaps` | Math.abs check, !(expr) negated |
| 44 | `computes the correct gap values between adjacent edge pair start positions` | gaps.add(), pairs.get(i+1).start() - pairs.get(i).start(), MathMutators |
| 45 | `computes the average of remaining gaps from the stream pipeline` | stream(), mapToInt(), average(), orElse(0.0), ArgumentPropagation |

---

## 7. DrawOvalRegion2Service (58 survivors)

MathMutator-heavy in the d2 update and dy/dx loop. Fewer than before (was 62) but still significant.

### 7.1 New tests in `DrawOvalServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 46 | `draws a very wide flat oval outline where region 2 covers most of the curve` | d2 decision branch, dy2 mutations, loop boundary dy>=0, plot4 calls |
| 47 | `draws a very tall narrow oval outline where region 2 runs quickly` | dy>=0 loop boundary negated, d2>0 branch, dy--/dx++ Increments/RemoveIncrements |
| 48 | `draws a circular outline where the region transition happens at a predictable quadrant boundary` | MathMutator (division→multiplication), a*a, b*b InlineConstants |
| 49 | `draws an oval outline with prime-number axes so the integer midpoint algorithm exercises all rounding branches` | InlineConstantMutators, MathMutators, ConditionalsBoundaryMutators |

---

## 8. GridOverlayDebugRendererService (55 survivors)

Renders binary grid overlay with column/row edge lines. Survivors in `renderBinaryToCanvas`, `drawColumnLines`, `drawRowLines`.

### 8.1 New tests in `GridOverlayDebugRendererServiceTest` (existing file)

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 50 | `renders a binary-to-canvas conversion with black-on-white pixel mapping over the ROI` | `renderBinaryToCanvas`: pixel==0 ternary, COLOR_WHITE/COLOR_BLACK, Planar constructor, loop boundaries |
| 51 | `draws vertical red lines at both start and end positions of each column edge segment offset by the projection offset` | `drawColumnLines`: start-offset, end-offset, lineDraw.apply args, COLOR_RED |
| 52 | `draws horizontal red lines at both start and end positions of each row edge segment offset by the projection offset` | `drawRowLines`: same mutations in row context |
| 53 | `composes all rendering steps and saves the final overlay image` | `apply`: all draw methods called, saver.apply |

---

## 9. DrawOvalRegion1Service (49 survivors)

MathMutator-heavy in d1, dx2, dy2. Similar to region 2.

### 9.1 New tests in `DrawOvalServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 54 | `transitions from region 1 to region 2 at the correct boundary when dx2 surpasses dy2` | dx2<dy2 loop condition, d1<0 branch, dx++/dy-- Increments/RemoveIncrements |
| 55 | `plots four symmetric pixels for every midpoint in all four quadrants` | plot4: all four pixelSet.apply calls, xc±dx, yc±dy |

---

## 10. OrchestrateGridDetectionService (48 survivors)

Orchestrates grid detection by chaining projection computation, peak finding, edge reconstruction, and box building.

### 10.1 New tests in `OrchestrateGridDetectionServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 56 | `computes total columns as expectedCols+1 when empty second column mode is enabled` | totalCols ternary, InlineConstant (1→0) |
| 57 | `calls projection renderer with both column and row axis peak data` | projectionRenderer.apply() VoidMethodCall, ArgumentPropagation on all args |
| 58 | `calls overlay renderer with edge pairs, ROI, and projection data` | overlayRenderer.apply() VoidMethodCall, ArgumentPropagation |
| 59 | `builds grid boxes from reconstructed row and column edges` | buildGridBoxes.apply() NonVoidMethodCall |
| 60 | `returns null when edge reconstruction fails, propagating the null through the pipeline` | colEdges/rowEdges null EQUAL_IF, null return |

---

## 11. ComputeGridProjectionsService (39 survivors)

Computes column and row projections with boundary clamping.

### 11.1 New tests in `ComputeGridProjectionsServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 61 | `clamps the search rectangle to within the image bounds when it extends beyond` | `ensureInside`: Math.max(0,x/y), Math.min(image.width/height) |
| 62 | `computes column and row projections with correct crop offsets and returns a ProjectionData` | computeColumnProjection/RowProjection.apply args, ProjectionData constructor, ProjectionBuildResultData constructor |
| 63 | `skips boundary computation when skipBoundaries is true, producing a full-height projection` | skipBoundaries branch, findRowBoundary.apply removed call |

---

## 12. AdaptiveBinarizeService (36 survivors)

Otsu thresholding with histogram accumulation and between-class variance.

### 12.1 New tests in `AdaptiveBinarizeServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 64 | `produces a binary output with exactly two distinct intensity values 0 and 255 for a multi-peak histogram` | Loop bounds, histogram index mutations |
| 65 | `thresholds an image where all pixels have the same intensity to a uniform binary output` | wB==0 continue branch, wF==0 break branch, sum1 loop, between>maximum mutations |
| 66 | `thresholds an image with a completely black left half and white right half` | between>maximum update, threshold assignment, sumB accumulation |
| 67 | `thresholds an image with incrementally increasing intensity from top to bottom` | All for-loop boundary mutations, between calculation mutations |

---

## 13. CollectArucoMarkersService (32 survivors)

Collects detected ArUco markers into a map keyed by marker ID.

### 13.1 New tests in `CollectArucoMarkersServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 68 | `collects all detected markers into a map keyed by marker ID with their corner point lists` | HashMap constructor, totalFound() removed, loop boundary, EQUAL_ELSE, EmptyObjectReturnVals |
| 69 | `returns an empty map when no markers are detected` | totalFound()==0 case, EmptyObjectReturnVals |

---

## 14. ProcessBallotImageService (30 survivors)

Orchestrates the full pipeline from ballot preprocessing to result.

### 14.1 New tests in `ProcessBallotImageServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 70 | `returns null when ballot preprocessing fails to produce a valid warp result` | preprocess==null EQUAL_ELSE, null return |
| 71 | `returns null when QR processing fails to decode the QR code` | qrOutcome.isDecoded() EQUAL_ELSE, null return |
| 72 | `propagates nonconformity detections through to the result when vote metadata from file differs from QR metadata` | voteMetadataFromJson.apply() removed, ballotNonconformities.apply() removed |

---

## 15. ArucoWarpService (25 survivors)

Decreased from 40. Remaining survivors in InlineConstants and distance calculations.

### 15.1 New tests in `ArucoWarpServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 73 | `produces warped image where every pixel in every band is within the valid 0-255 range` | `warpToU8`: Math.min/Math.max removed, ArgumentPropagation, InlineConstants |
| 74 | `computes the Euclidean distance between two points correctly regardless of which point is first` | `distance`: both MathMutators (subtraction→addition) |
| 75 | `handles markers placed at the extreme corners of the image resulting in zero-distance edges` | `apply`: EQUAL_ELSE on homography/distortion check |

---

## 16. ParseVoteMetadataJsonService (23 survivors) — NEW

Parses a JSON vote metadata object into a VoteMetadataData record.

### 16.1 New test file `ParseVoteMetadataJsonServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 76 | `parses all vote metadata fields from a fully populated JSON object` | getString, getInt, getJSONArray, VoteMetadataData constructor ArgumentPropagation |
| 77 | `converts a JSON string array with multiple elements into a List of strings` | `strings`: loop boundaries, array.getString, array.length(), List.copyOf() |
| 78 | `handles an empty candidates array producing an empty list` | array.length()==0, empty list case |
| 79 | `handles an empty issuedBallotIds array producing an empty list` | same for issuedBallotIds |

---

## 17. ComputeHomographyService (18 survivors)

### 17.1 New tests in `ComputeHomographyServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 80 | `maps four source corner points to a rectangular destination of the given width and height` | All InlineConstantMutators on 0.0→1.0, 1.0→2.0, width-1.0, height-1.0 |
| 81 | `produces a valid 3x3 homography matrix that correctly transforms each source point to its destination` | MathMutators (subtraction→addition), process() result check EQUAL_ELSE |
| 82 | `throws RuntimeException when the homography computation fails due to collinear source points` | process() false branch |

---

## 18. InverterService (17 survivors) — NEW

Inverts a grayscale image by subtracting each pixel from 255.

### 18.1 New test file `InverterServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 83 | `returns a new image with the same dimensions as the input` | width/height field access, GrayU8 constructor |
| 84 | `inverts every pixel value from 255-value so that black becomes white and white becomes black` | 255 - gray.get(), MathMutator, loop boundaries |
| 85 | `inverts mid-gray pixels correctly (e.g. 128 becomes 127)` | MathMutator on subtraction, pixelSet removed |
| 86 | `does not modify the original input image` | NonVoidMethodCallMutator on get (should be on input, not output) |

---

## 19. ExtractGridRegionService (17 survivors)

### 19.1 New tests in `ExtractGridRegionServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 87 | `returns null when no valid grid boundary is detected` | boundary==null check negated, null return |
| 88 | `returns null when the computed crop region has zero or negative width` | cropWidth<=0 check negated, null return |
| 89 | `returns null when the computed crop region has zero or negative height` | cropHeight<=0 check negated, null return |
| 90 | `extracts a sub-image from the right side starting from the QR centre, inverts it, and normalizes` | inverter.apply() call, imageNormalizer.apply() call, loop bounds |
| 91 | `normalizes the cropped grid region and includes the crop offset and QR centre position in the result` | GridRegionData constructor fields |

---

## 20. FillOvalService (17 survivors)

### 20.1 New tests in `FillOvalServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 92 | `fills an oval region where the scanline width varies symmetrically from narrow at top to wide in the middle` | a/b computation, sqrt(1-dy²/b²), dx*2 scanline width, fillScanline.apply removed call |
| 93 | `skips rows outside the vertical bounds of the oval without drawing any pixels` | Math.abs(dy)>b check negated |
| 94 | `fills a perfectly circular region producing symmetric scanlines across the centre` | MathMutators on division→multiplication |

---

## 21. FindRawPeakService (15 survivors)

### 21.1 New tests in `FindRawPeakServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 95 | `returns only peaks whose projection value exceeds the relative threshold computed from the maximum value` | maxValue loop, maxValue*THRESHOLD, Math.max with min threshold |
| 96 | `identifies a plateau of equal values as multiple adjacent peaks` | >= comparison on both neighbors |
| 97 | `ignores peaks when the computed threshold is less than the absolute minimum threshold` | Math.max branch where PEAK_THRESHOLD_MIN wins |

---

## 22. VoteMetadataFromJsonService (14 survivors)

### 22.1 New tests in `VoteMetadataFromJsonServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 98 | `returns the fallback vote metadata when the file content is null` | content==null check, fallback return |
| 99 | `returns the fallback vote metadata when the file content is blank` | isBlank() check, fallback return |
| 100 | `parses vote metadata from a JSON file located by the extracted vote name` | extractVoteName.apply(), ballotResultFileRepository.apply(), JSONObject constructor, parseVoteMetadataJson.apply() |

---

## 23. ReconstructEdgeService (13 survivors)

### 23.1 New tests in `ReconstructEdgeServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 101 | `returns null when the number of paired edges does not match the expected number of column pairs` | pairs.size()!=expectedPairs check negated |
| 102 | `returns null when adjacent column pairs overlap in the scan direction` | pairs[i].end()>=pairs[i+1].start() check |
| 103 | `returns null when the empty second column validation fails` | validateEmptySecondColumn.apply check negated |
| 104 | `computes the average column width from the projection length and total column count` | projection.length/totalColumns, minGap/maxGap computation |

---

## 24. DecodeQRService (13 survivors)

### 24.1 New tests in `DecodeQRServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 105 | `falls back to global histogram binarization when hybrid binarization fails to decode the QR code` | hybrid!=null check negated, decode branch |
| 106 | `returns null when neither binarization method can decode the QR code` | null return from decode, hybrid/null branch |

---

## 25. FindBranchPointsService (12 survivors)

### 25.1 New test file `FindBranchPointsServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 107 | `returns points where a skeleton pixel has at least three neighbor pixels marking a branch junction` | neighbourCount: all NegateConditionalsMutator, EQUAL_IF/ELSE, MathMutator, ArgumentPropagation |
| 108 | `returns an empty list for a skeleton with only endpoints and no branch points` | >=3 check, count accumulation |
| 109 | `ignores pixels on the border of the image where the full 8-neighborhood cannot be evaluated` | dx==0 && dy==0 continue check, loop boundaries |

---

## 26. FindRowBoundaryService (12 survivors)

### 26.1 New tests in `FindRowBoundaryServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 110 | `computes crop boundaries by finding the strongest peak in the upper half for top and lower half for bottom` | findMaxPeak.apply call arguments, projection.length/2 |
| 111 | `clamps crop boundaries to valid image range when peaks are at the extreme edges` | Math.max(0, top+MARGIN), Math.min(length-1, bottom-MARGIN) |

---

## 27. ConvertPlanarU8ToF32Service (12 survivors)

### 27.1 New test file `ConvertPlanarU8ToF32ServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 112 | `converts a 3-band Planar<GrayU8> to Planar<GrayF32> with identical pixel values in each band` | All NegateConditionalsMutator, RemoveConditionalMutator_ORDER_ELSE, InlineConstants, get/set removed, ArgumentPropagation |

---

## 28. BuildGridBoxesService (12 survivors) — NEW

Builds grid cell rectangles from the Cartesian product of row and column edge segments.

### 28.1 New test file `BuildGridBoxesServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 113 | `builds a grid of rectangles from the Cartesian product of row edges and column edges` | Nested loops, RectangleData constructor (col.start, row.start, col.end-col.start, row.end-row.start), MathMutators |
| 114 | `returns an empty list when either row or column edge list is empty` | Empty list product case, ArrayList constructor |
| 115 | `computes correct width and height for each cell from edge segment deltas` | end()-start() MathMutators, subtraction→addition |

---

## 29. FindGridBoundaryService (11 survivors)

### 29.1 New tests in `FindGridBoundaryServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 116 | `returns null when fewer than two peaks are found in the projection` | peaks.size() < 2 check negated |
| 117 | `clamps crop boundaries to be within valid image range even when the strongest peaks are at the edges` | Math.max(0, topPeak+MARGIN), Math.min(height-1, bottomPeak-MARGIN) |
| 118 | `uses a fixed margin from the bottom marker when computing the search region` | calcSearchBottomY: markerTopY!=null branch, Math.min |
| 119 | `falls back to 95% of image height as the search boundary when no marker top is available` | calcSearchBottomY: markerTopY==null branch, height*0.95 |

---

## 30. DrawTextService (10 survivors)

### 30.1 New tests in `DrawTextServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 120 | `draws a multi-character string where each character advances the cursor by the fixed character width` | character loop boundaries, cursorX+=CHAR_ADVANCE, text.charAt() |
| 121 | `skips characters outside the printable ASCII range without advancing the cursor` | printable range condition negated, EQUAL_IF/ELSE |
| 122 | `draws a glyph where some bit rows are all zero and some have set bits` | glyph bit loop: row loop boundary, (line & (1<<row))!=0 check |

---

## 31. EnhanceContrastService (9 survivors)

### 31.1 New tests in `EnhanceContrastServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 123 | `stretches the intensity range of an image to span from 0 to 255` | min/max finding loops, scale computation, value adjustment |
| 124 | `returns a clone of the input when all pixels have the same intensity` | max<=min check negated, clone() branch |
| 125 | `clamps adjusted pixel values that would exceed the 0-255 range after stretching` | Math.max(0, Math.min(255, adjusted)) |

---

## 32. DrawRectangleService (8 survivors)

### 32.1 New tests in `DrawRectangleServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 126 | `draws a rectangle outline where all four corners connect without gaps` | All corner coordinate computations: x+w-1, y+h-1, MathMutators |
| 127 | `draws a 1x1 pixel rectangle that produces a single filled pixel at the given coordinates` | Same with w=1, h=1 |

---

## 33. RectFillService (8 survivors)

### 33.1 New tests in `RectFillServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 128 | `fills a rectangle region when the coordinates are fully within the image bounds` | Math.max(0,x), Math.max(0,y), Math.min(image.width,x+w), Math.min(image.height,y+h) ArgumentPropagation |
| 129 | `clamps a rectangle that extends beyond the image boundary to only fill pixels within the image` | All clamp branches with out-of-bounds values |
| 130 | `fills nothing when the rectangle is entirely outside the image to the left or top` | xEnd<=xStart / yEnd<=yStart through clamp |

---

## 34. XDetectService (7 survivors)

### 34.1 New tests in `XDetectServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 131 | `filters detected X-mark candidates to include only those with a sufficient number of connected component pixels` | ConditionalsBoundary on pixel threshold, ORDER_IF |
| 132 | `returns not-detected when the eroded cell has fewer connected components than the minimum` | eroded removed call, branchPoints removed call |

---

## 35. PrepareReviewGridService (7 survivors)

### 35.1 New tests in `PrepareReviewGridServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 133 | `generates a grid of review cells covering all candidate/support-column combinations` | ConditionalsBoundaryMutators, InlineConstants, EQUAL_IF on cell index |
| 134 | `places support cells in the third column position relative to the first support column` | screenColumn: ballotColumn==0?0:ballotColumn+1, +1 InlineConstant |
| 135 | `marks cells as checked when the cell position matches a detected X-mark` | checkedCells Set, row() + ":" + screenColumn(col()) |

---

## 36. DetectGridRegionAndCheckboxService (7 survivors)

### 36.1 New tests in `DetectGridRegionAndCheckboxServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 136 | `returns null when the grid region cannot be extracted from the image` | EQUAL_IF on grid region null check |
| 137 | `computes checkbox positions offsetting from the crop top and QR centre position` | cropTop removed, qrCentreX removed, supportColumnCount/candidateCount removed, MathMutator, InlineConstant |

---

## 37. PairEdgeService (6 survivors)

### 37.1 New tests in `PairEdgeServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 138 | `pairs peaks into edge segments when their distance falls within the specified gap range` | ConditionalsBoundaryMutator, gap>=minGap && gap<=maxGap EQUAL_IF |
| 139 | `limits the search for a pair partner to the configured maximum look-ahead distance` | Math.min(i+maxLookAhead, peaks.size()) ORDER_IF, InlineConstant |

---

## 38. PreprocessQRCropService (6 survivors)

### 38.1 New tests in `PreprocessQRCropServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 140 | `crops the QR region from the right centre portion of the warped ballot and adjusts bounding box coordinates by the crop offset` | InlineConstants on crop dimensions, ArgumentPropagation on crop/preprocessQR |

---

## 39. DrawLineService (5 survivors)

### 39.1 New tests in `DrawLineServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 141 | `draws a steep vertical-ish line where the error term updates along the y-axis more often than the x-axis` | e2>=dy and e2<=dx ConditionalsBoundaryMutator, InlineConstant 2→3 |
| 142 | `draws a line that ends exactly where it starts producing a single dot` | loop termination when x==x1 && y==y1 on first iteration |

---

## 40. ImageNormalizerService (5 survivors)

### 40.1 New tests in `ImageNormalizerServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 143 | `applies local mean thresholding with a region width proportional to the maximum image dimension` | maxDim/Math.max, regionWidth computation, MathMutator |
| 144 | `normalizes the binary output after thresholding by filling small gaps` | normalizeBinary.apply removed call |

---

## 41. MergeClosePeakService (4 survivors)

### 41.1 New tests in `MergeClosePeakServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 145 | `merges peaks that are closer than the minimum distance into their average position` | ConditionalsBoundaryMutator, InlineConstant 2→3, merged.add(start/count) |
| 146 | `returns the input unchanged when all peaks are farther apart than the minimum distance` | Collections::sort removed, loop branch where merge not triggered |

---

## 42. ComputeQrBoundingBoxService (4 survivors)

### 42.1 New tests in `ComputeQrBoundingBoxServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 147 | `computes the bounding rectangle that encloses all QR finder pattern points` | InlineConstants on Integer.MIN_VALUE, Math::max ArgumentPropagation |

---

## 43. DetectGridService (4 survivors)

### 43.1 New tests in `DetectGridServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 148 | `detects grid columns from the edge projection and grid rows from the row projection` | All InlineConstantMutators on 0→1 and 1→0 |

---

## 44. XMarkDetectService (3 survivors)

### 44.1 New tests in `XMarkDetectServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 149 | `accumulates detected X-mark positions and reports the detection count per cell` | list.add removed, list.size() removed, xDetected removed |

---

## 45. XMarkDetectionAndResultService (3 survivors)

### 45.1 New tests in `XMarkDetectionAndResultServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 150 | `builds a ballot result from the grid region projection input, QR centre, and crop offset` | projectionInput removed, qrCentreX removed, cropTop removed |

---

## 46. BuildBallotResultService (3 survivors)

### 46.1 New test file `BuildBallotResultServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 151 | `constructs a ballot result with support marks matching the expected support column count` | supportColumnCount removed, List::size removed, List::of removed |

---

## 47. CheckBallotNonconformitiesService (3 survivors)

### 47.1 New tests in `CheckBallotNonconformitiesServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 152 | `checks a ballot result for nonconformities and returns the result with any issues attached` | detectNonconformities.apply removed, BallotResultData constructor removed |

---

## 48. SharpenService (2 survivors)

Reduced from 18 — only 2 InlineConstantMutators remain.

### 48.1 New tests in `SharpenServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 153 | `clamps sharpened pixel values to the 0-255 range when the unsharp mask produces values outside the valid range` | Math.max(0, Math.min(255, value)), InlineConstants on 0 and 255 |

---

## 49. QrVoteMetadataService (2 survivors) — NEW

Simple delegate that extracts vote metadata from QrData.

### 49.1 New test file `QrVoteMetadataServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 154 | `returns the vote metadata from the QR data object` | NonVoidMethodCallMutator on qrData.voteMetadata() |
| 155 | `returns null when the QR data has null vote metadata` | NullReturnValsMutator |

---

## 50. BallotPreprocessService (2 survivors)

### 50.1 New tests in `BallotPreprocessServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 156 | `returns null when ArUco marker detection fails` | EQUAL_IF on preprocess null check |
| 157 | `returns null when perspective warp removal fails` | EQUAL_IF on warp null check |

---

## 51. NormalizeBinaryService (2 survivors)

### 51.1 New tests in `NormalizeBinaryServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 158 | `inverts isolated black pixels in white regions and isolated white pixels in black regions` | NegateConditionalsMutator on !=0 check, ArgumentPropagation on get |

---

## 52. ParseQrFieldService (2 survivors)

### 52.1 New tests in `ParseQrFieldServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 159 | `parses a QR field only when the extracted string is not blank` | isBlank removed, EQUAL_ELSE on isBlank check |

---

## 53. ExtractQrVoteIdService (2 survivors)

### 53.1 New tests in `ExtractQrVoteIdServiceTest`

| # | @DisplayName | Kills mutants in |
|---|-------------|-----------------|
| 160 | `extracts the vote ID substring before the first underscore separator` | ConditionalsBoundaryMutator, ORDER_ELSE on substring index |

---

## 54. Remaining single-digit survivors (1 each)

| # | Class | @DisplayName | Kills |
|---|-------|-------------|-------|
| 161 | `PreprocessQRService` | `applies preprocessing steps to a QR crop and returns the resulting image` | ArgumentPropagation on sharpenService::apply |
| 162 | `DrawOvalService` | `transitions from region 1 to region 2 of the midpoint ellipse algorithm producing a continuous outline` | VoidMethodCallMutator on drawOvalRegion2::apply |
| 163 | `SaveBallotResultService` | `saves a ballot result through the repository` | MemberVariableMutator in `<init>` |
| 164 | `ExtractVoteNameService` | `extracts the vote name from the ballot filename by removing the trailing ballot ID suffix` | ConditionalsBoundaryMutator on substring |
| 165 | `ArucoDetectionService` | `returns null when marker detection does not find the required set of ArUco markers` | EQUAL_ELSE on markers==null check |
| 166 | `FillScanlineService` | `fills a horizontal scanline from the start to end column inclusive` | ConditionalsBoundaryMutator on px<=xEnd |

---

## Summary

| Category | Count |
|----------|-------|
| Total surviving mutants | 1356 |
| Total classes | 59 |
| New tests proposed | 166 |
| New TestData constants needed | 6 (in `ReviewTestData`) |
| New Test files needed | 7 (`ValidateEmptySecondColumnServiceTest`, `ParseVoteMetadataJsonServiceTest`, `InverterServiceTest`, `BuildGridBoxesServiceTest`, `FindBranchPointsServiceTest`, `ConvertPlanarU8ToF32ServiceTest`, `QrVoteMetadataServiceTest`, `BuildBallotResultServiceTest`) |

### Priority order

1. **XMarkDebugRendererService** (167) — largest single class; debug renderer with many private methods
2. **ProjectionDebugRendererService** (153) — similar debug renderer with projection chart drawing
3. **SerializeBallotResultService** (147) — JSON deep comparison logic, requires TestData constants
4. **ArucoMarkerDetectionService** (70) — marker corner extraction and validation
5. **DetectBallotNonconformitiesService** (62) — nonconformity detection with message building
6. **ValidateEmptySecondColumnService** (59) — new class, gap comparison logic
7. **DrawOvalRegion1+2** (107) — exercised through DrawOvalService, needs diverse input shapes
8. **GridOverlayDebugRendererService** (55) — debug overlay rendering
9. **OrchestrateGridDetectionService** (48) — orchestrates grid detection chain
10. **ComputeGridProjectionsService** (39) — projection computation with boundary clamping
11. **AdaptiveBinarizeService** (36) — Otsu thresholding with histogram
12. **CollectArucoMarkersService** (32) — marker collection into map
13. **ProcessBallotImageService** (30) — full pipeline orchestration
14. **ArucoWarpService** (25) — warping and pixel clamping
15. **ParseVoteMetadataJsonService** (23) — new class, JSON parsing
16. **Remaining 43 classes** (329) — various services