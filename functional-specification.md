# Functional Specification Derived from Test DisplayNames

This document is the functional specification derived from test [`@DisplayName`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test/SaveBallotResultServiceTest.java:43) values. The wording of each functional statement uses the exact test description text as the ground truth for expected behaviour.

## Module [`app`](app)

### Package [`hu.kdea.szavazas`](app/src/test/java/hu/kdea/szavazas)

#### Functional specification from [`AndroidSzavazasComponentSmokeTest`](app/src/test/java/hu/kdea/szavazas/AndroidSzavazasComponentSmokeTest.java)

- instantiates the Android production Dagger graph from unit tests

## Module [`szavazas-core`](szavazas-core)

### Package [`hu.kdea.szavazas.test`](szavazas-core/src/test/java/hu/kdea/szavazas/test)

#### Functional specification from [`JvmTestSzavazasComponentSmokeTest`](szavazas-core/src/test/java/hu/kdea/szavazas/test/JvmTestSzavazasComponentSmokeTest.java)

- instantiates the JVM test Dagger graph

### Package [`hu.kdea.szavazas.review.test`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test)

#### Functional specification from [`SerializeBallotResultServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test/SerializeBallotResultServiceTest.java)

- serializes ballot result into vote object and ballots array
- creates initial top level vote metadata when writing a new file
- appends ballot result to existing ballots array while preserving vote metadata
- preserves stored vote metadata and appends ballot when conflicting metadata is marked saveable
- throws a first class mismatch signal when appended ballot has conflicting metadata and save remains blocked
- preserves matching vote metadata when stored vote fields are reordered
- throws when existing content is invalid json

#### Functional specification from [`ExtractVoteNameServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test/ExtractVoteNameServiceTest.java)

- returns prefix before separator
- returns whole value without separator

#### Functional specification from [`PrepareReviewGridServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test/PrepareReviewGridServiceTest.java)

- prepares review grid with spacer column and checked cells
- keeps raw vote name without separator

#### Functional specification from [`SaveBallotResultServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/review/test/SaveBallotResultServiceTest.java)

- saves vote object with ballots using extracted vote name
- saves unchanged valid non-mismatch ballots without altering their serialized content
- saves initial top level vote metadata when no existing vote file is present
- saves reviewable metadata mismatch after confirmation while preserving stored vote metadata
- throws a first class mismatch signal and does not save when ballot metadata conflict remains save blocking
- confirms metadata mismatch ballot by saving a save-authorized copy while preserving stored vote metadata

### Package [`hu.kdea.szavazas.ballotprocessor.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/test)

#### Functional specification from [`BallotPreprocessServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/test/BallotPreprocessServiceTest.java)

- converts input planar to gray and optionally saves as debug capture
- returns null when ArUco detection returns null

#### Functional specification from [`GrayPlanarToGrayServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/test/GrayPlanarToGrayServiceTest.java)

- uses weighted formula for RGB-to-gray conversion
- output dimensions match input dimensions
- multiple pixels are converted independently

#### Functional specification from [`BallotProcessingServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/test/BallotProcessingServiceTest.java)

- returns ballot marker error when preprocess result is null
- returns QR error when adjusted QR is null and saves debug image
- returns grid region error when grid detection result is null
- returns successful ballot result from XMarkDetectionAndResultService and logs summary
- catches thrown exceptions and converts into error outcome with exception message
- catches exception with null message and uses fallback key
- documents the intended mismatch outcome when QR metadata conflicts with ballot processing metadata

#### Functional specification from [`MessageServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/test/MessageServiceTest.java)

- resolves known key from messages.properties for the active locale
- resolves another known key from messages.properties

### Package [`hu.kdea.szavazas.ballotprocessor.draw.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test)

#### Functional specification from [`DrawTextServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/DrawTextServiceTest.java)

- printable characters render pixels from the bitmap font
- non-printable characters are skipped while still advancing the cursor
- multiple characters advance by the constant character spacing
- the fontSize parameter is currently ignored, documenting existing behaviour

#### Functional specification from [`FillOvalServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/FillOvalServiceTest.java)

- non-positive width or height performs no drawing
- a normal oval fills interior scanlines symmetrically
- drawing near borders is safely clipped through pixel writes

#### Functional specification from [`DrawOvalServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/DrawOvalServiceTest.java)

- non-positive width or height performs no drawing
- an oval outline is drawn in all four quadrants
- symmetric points are emitted around the centre

#### Functional specification from [`SetPixelServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/SetPixelServiceTest.java)

- pixel is written into all three bands using RGB channel splitting
- out-of-bounds coordinates are ignored without modifying the image

#### Functional specification from [`DrawRectangleServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/DrawRectangleServiceTest.java)

- all four edges are drawn
- corners are included consistently
- width and height of one still produce a valid outline

#### Functional specification from [`RectFillServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/RectFillServiceTest.java)

- the full requested rectangle area is filled
- partially out-of-bounds rectangles are clipped to image bounds
- completely non-overlapping rectangles leave the image unchanged

#### Functional specification from [`DrawLineServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/draw/test/DrawLineServiceTest.java)

- horizontal line marks all expected pixels
- vertical line marks all expected pixels
- diagonal line marks all expected pixels
- both endpoints are included
- reverse-direction coordinates draw the same line

### Package [`hu.kdea.szavazas.ballotprocessor.x`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/x)

#### Functional specification from [`XMarkDetectionAndResultServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectionAndResultServiceTest.java)

- returns detection result with marks and ballot result
- returns empty marks when no X detected

#### Functional specification from [`XMarkDetectServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/x/XMarkDetectServiceTest.java)

- converts each checkbox from full-image coordinates to local crop coordinates before X detection
- detected X marks are converted to row and column positions using expectedCols
- debug data is accumulated and rendered once after processing all cells
- debug logging occurs when detection debug data is present

#### Functional specification from [`XDetectServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/x/XDetectServiceTest.java)

- returns not detected for too small rectangle
- returns debug data with no detection for empty inner cell
- returns detected when skeleton has enough branch points
- copies cropped cell into debug data before thinning

### Package [`hu.kdea.szavazas.ballotprocessor.common.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/common/test)

#### Functional specification from [`CropServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/common/test/CropServiceTest.java)

- returned image contains the exact selected rectangle
- width and height of crop match requested values
- border-aligned crops preserve source values correctly

#### Functional specification from [`ImageNormalizerServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/common/test/ImageNormalizerServiceTest.java)

- the output image has the same size as input
- output values are normalised to binary 0 or 255
- a high-contrast input produces both foreground and background values

### Package [`hu.kdea.szavazas.ballotprocessor.projection.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test)

#### Functional specification from [`MergeClosePeakServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/MergeClosePeakServiceTest.java)

- isolated peaks remain unchanged
- nearby peaks closer than threshold are averaged into one merged peak
- multiple merge groups are handled in sorted order
- single peak list returns unchanged
- empty list returns empty

#### Functional specification from [`PairEdgeServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/PairEdgeServiceTest.java)

- peaks are paired when the gap is inside min and max bounds
- already used peaks are not reused
- unmatched peaks are ignored
- maxLookAhead limits later pairing opportunities
- empty peaks list returns empty

#### Functional specification from [`FindRowBoundaryServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/FindRowBoundaryServiceTest.java)

- vertical projection is split into top and bottom halves for peak search
- returned crop bounds include boundary margin adjustments

#### Functional specification from [`FindMaxPeakServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/FindMaxPeakServiceTest.java)

- highest value index is returned within the requested inclusive range
- ties keep the first max encountered
- sub-range search returns correct index

#### Functional specification from [`FindRawPeakServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/FindRawPeakServiceTest.java)

- empty or non-positive projections return no peaks
- local maxima above computed threshold are returned with offset applied
- edge elements are ignored because scanning starts at index 1 and ends at length - 2
- no peaks when all values are below threshold

#### Functional specification from [`ReconstructEdgeServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/ReconstructEdgeServiceTest.java)

- raw peaks are merged, paired, and returned when pair count matches expectation
- null is returned when the pair count does not match expectedPairs
- overlapping edge segments are rejected
- emptySecondColumn enforces the wider first-gap consistency rule

#### Functional specification from [`ComputeRowProjectionServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/ComputeRowProjectionServiceTest.java)

- full-image projection sums each row correctly
- ROI-based projection sums only the selected area
- crop offset and cropped height are applied correctly

#### Functional specification from [`ComputeColumnProjectionServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/projection/test/ComputeColumnProjectionServiceTest.java)

- each output element is the column sum over the requested row range
- ROI x and y offsets are respected

### Package [`hu.kdea.szavazas.ballotprocessor.qr.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test)

#### Functional specification from [`DecodeQRServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test/DecodeQRServiceTest.java)

- returns null for undecodable input rather than throwing NotFoundException
- returns null for tiny image that cannot contain a QR code

#### Functional specification from [`QrProcessingServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test/QrProcessingServiceTest.java)

- converts grayscale to RGB pixels and passes to decoder with correct dimensions
- returns error when decoder returns null

#### Functional specification from [`PreprocessQRCropServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test/PreprocessQRCropServiceTest.java)

- derives QR crop rectangle from ballot width and height using fixed proportions
- returns null QR result when QR processing returns null
- translates QR bbox back into warped image coordinates

#### Functional specification from [`ConvertGrayU8ToRgbPixelsServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test/ConvertGrayU8ToRgbPixelsServiceTest.java)

- converts GrayU8 image to int[] ARGB pixel array

#### Functional specification from [`ParseQrResultServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/test/ParseQrResultServiceTest.java)

- parses QR result with valid text and points
- fails when QR text is missing support count
- fails when QR text is missing row count
- fails when support count is not numeric
- fails when row count is not numeric
- fails when support count is zero
- fails when row count is zero
- fails when support count is negative
- fails when row count is negative
- computes bounding box from single result point
- returns non-null result
- fails when result is null
- fails when QR text is null
- fails when result points are null
- fails when result points are empty
- fails when a result point is null

### Package [`hu.kdea.szavazas.ballotprocessor.qr.preprocess.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/test)

#### Functional specification from [`AdaptiveBinarizeServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/test/AdaptiveBinarizeServiceTest.java)

- output is binary 0 or 255 only
- a bimodal image is thresholded into foreground and background
- default threshold handling still returns a binary image for flat input

#### Functional specification from [`SharpenServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/test/SharpenServiceTest.java)

- sharpening preserves dimensions
- output values are clamped to 0..255
- a non-uniform image is altered relative to blurred neighbourhoods

#### Functional specification from [`EnhanceContrastServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/test/EnhanceContrastServiceTest.java)

- minimum input maps to 0 and maximum maps to 255
- intermediate values are scaled proportionally
- flat images return a clone rather than altering input values

#### Functional specification from [`PreprocessQRServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/qr/preprocess/test/PreprocessQRServiceTest.java)

- preprocessing steps are executed in order and each step receives the previous output
- the final returned image is the output of the last step
- debug saver is called after each step with the expected staged filename
- processing works when debug saver is null

### Package [`hu.kdea.szavazas.ballotprocessor.aruco.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/aruco/test)

#### Functional specification from [`ArucoDetectionServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/aruco/test/ArucoDetectionServiceTest.java)

- returns null when marker detection returns null
- passes detected markers to warp service with original planar image

#### Functional specification from [`ArucoMarkerDetectionServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/aruco/test/ArucoMarkerDetectionServiceTest.java)

- returns null when no markers are detected on a blank image

#### Functional specification from [`ArucoWarpServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/aruco/test/ArucoWarpServiceTest.java)

- output warped image dimensions are derived from averaged edge lengths
- marker top points are transformed and the smaller y is used as markerTopY

### Package [`hu.kdea.szavazas.ballotprocessor.grid.test`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/grid/test)

#### Functional specification from [`FindGridBoundaryServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/grid/test/FindGridBoundaryServiceTest.java)

- null is returned when fewer than two peaks are found
- the two strongest peaks are chosen even if they are not first in order
- cropTop and cropBottom are margin-adjusted and clamped to image limits
- markerTopY changes the lower search bound

#### Functional specification from [`ExtractGridRegionServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/grid/test/ExtractGridRegionServiceTest.java)

- null is returned when boundary detection fails
- null is returned when computed crop width or height is non-positive
- cropTop, qrCentreX, and normalised cropped image are returned when boundaries are valid
- the crop starts at qrCentreX and spans to the right edge of the source image

#### Functional specification from [`DetectGridServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/grid/test/DetectGridServiceTest.java)

- it requests detection over the full projection input rectangle
- empty orchestrator results are converted to null
- detected boxes are translated by qrCentreX and cropTop

#### Functional specification from [`DetectGridRegionAndCheckboxServiceTest`](szavazas-core/src/test/java/hu/kdea/szavazas/ballotprocessor/grid/test/DetectGridRegionAndCheckboxServiceTest.java)

- QR bbox values are converted into qrCenterX and qrBottom
- null is returned when region extraction fails
- debug image saving occurs for the projection input when saver is present
- checkbox results are wrapped in GridDetectionResultData and null detector output stays null
