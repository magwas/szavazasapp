# Konveyor Coding Style Rules (Dagger + Constructor Injection Edition)

**NOTE:** ALL rules below are MANDATORY. This edition supersedes previous Spring‑based rules. Constructor injection is now the standard; Dagger is the DI framework.

## Core Principles

- All program logic resides in **Service** units.
- External dependencies (I/O, network, OS services) are wrapped by **Wrapper** units.
- Constants go to **Constant** units.
- Data structures are **Data** units (immutable records).
- Persistent or modifiable collections of data are managed by **Repository** units.
- Transient state is kept in **State** units.
- OOP APIs are provided by **Delegate** units.
- Framework‑required classes are **Glue** units.
- Testing is first‑class: **Test**, **TestData**, **Stub**, **TestUtil** units.

## Dependency Injection with Dagger

- Use **constructor injection** exclusively for all injectable types. 
- Annotate constructors with `@Inject`.
- Dependencies are stored as `private final` fields.
- Dagger modules (`@Module`) and components (`@Component`) are allowed as infrastructure (treated as Glue).
- No field injection (`@Inject` on fields) is allowed – constructor injection only.

## Unit Definitions (Production Code)

### Service Unit
- Holds application logic.
- Class name ends with `Service`. Name is a verb (e.g. SelectBestFitService)
- **Has exactly one public method: `apply`** (no other public methods).
- Implements a corresponding `Constants` interface (for constants used by the service).
- Dependencies are constructor parameters, annotated with `@Inject`, stored in `private final` fields.
- Field names follow the pattern: dependency class name lowercased, removing `Service` suffix.  
  Example: `private final UserRepository userRepository;`
- No other fields.
- Maximum 25 lines of code (LoC). Refactor longer services into smaller services or util classes.
- External operations that hinder testing (network, filesystem, console, OS services) must be injected as dependencies (ultimately a `Wrapper`).

### Constant Unit
- Holds constants. Name is the name of the package or the class the constants relate to (e.g. SelectBestFitConstants)
- Interface with name ending in `Constants`.
- Contains only `static final` fields (primitive, `String`, or immutable collections).

### Data Unit
- Describes immutable data structures. Name is a noun (e.g. RectangleData).
- `record` with name ending in `Data`.
- Only field declarations – no methods.
- Collections and maps use immutable types (e.g., `List.of`, `Map.of`, or Guava immutable collections).

### State Unit
- Holds transient, non‑persistent state. Name is a noun (e.g. BallotState).
- Class name ends with `State`.
- Annotated with `@Inject` on a default or empty constructor (if no dependencies) or constructor with dependencies.
- Fields are `public` (or `private` with getters – but `public` is preferred for simplicity).
- Collections/maps that require modification belong in a `Repository`, not `State`.
- Corresponding `Stub` unit exists (see below).

### Repository Unit
- Manages persistent data or collections that are modified. Name is a noun (e.g. BallotRepository).
- Interface name ends with `Repository`.
- Defines CRUD methods appropriate for the domain (no extension of Spring Data interfaces).  
  Example:
  ```java
  public interface UserRepository {
      Optional<UserData> findById(String id);
      UserData save(UserData user);
      void deleteById(String id);
      List<UserData> findAll();
  }
  ```
- Implementation class is named `Default<Name>Repository` (or `InMemory<Name>Repository`) and is injectable via Dagger (constructor with `@Inject`).
- Production implementation may use a database; wrapper may be used underneath.
- Corresponding `Stub` unit for testing.

### Delegate Unit
- Provides an OOP API over data + logic. Name is part of public API, always ask for it.
- Class annotated with `@io.github.magwas.konveyor.annotations.Delegate`.
- Contains zero or one `self` field of a `State` unit.
- Dependencies are constructor parameters (same as Service) and stored in `private final` fields.
- For each dependency field there is exactly one method that calls the service’s `apply` with `self` as the first argument (if `self` exists).
- No other business logic.

### Wrapper Unit
- Wraps external dependencies (I/O, network, OS, third‑party libraries) to make services testable.
- Do not create a Wrapper for an interface whose implementation is provided by the caller (e.g., a callback or strategy injected via Dagger). Only wrap external dependencies that the module itself controls (I/O, network, OS, third-party libraries).
- Group wrapped interfaces by functionality. This sometimes correlate with dependency.
- Class name ends with `Wrapper`.
- Annotated with `@Inject` on constructor (may be default constructor if no dependencies).
- Can have `public` non‑static fields and methods.
- Can have `private static` fields.
- No other constructors, fields, or methods beyond those allowed.
- May implement a `Constants` interface, but no other dependencies to the rest of the source code.
- Corresponding `Stub` unit.
- No real logic, just answers with some TestData, if needed by inspecting parameters

### Glue Unit
- Required by an external framework or API (e.g., Dagger component, module, or JAX-RS resource).
- Annotated with `@io.github.magwas.konveyor.annotations.Glue`.
- No business logic.
- Dagger `@Module` and `@Component` interfaces/classes are considered Glue.

## Unit Definitions (Test Code)

Test code resides in packages corresponding to the tested code, with `.test` appended.

### TestData Unit
- Provides test data constants. Name is the data unit nae or the package (e.g. RectangleTestData).
- Interface name ends with `TestData`.
- Contains only `static final` constants.
- For each `Data` unit there is one corresponding `TestData` unit containing instances of that `Data` type and possibly collections thereof.
- For random/generic test data, one unit per package (e.g., `CryptoTestData`).
- For specialised data (e.g., SVG path strings), a dedicated unit (e.g., `SVGPathTestData`).

### Stub Unit
- Provides a Mockito mock for a `Service`, `State`, `Repository`, or `Wrapper` unit.
- Name starts with the stubbed unit name and ends with `Stub`. (e.g. SelectBestFitStub)
- Either:
  - Annotated with `@io.github.magwas.konveyor.testing.IndirectlyTested` and has an empty body (for state units and simple services whose main path is tested elsewhere), **or**
  - Contains a `public static <StubbedType> stub()` method that returns a fully configured Mockito mock. All stubbing is defined inside `stub()`, never in tests.
- No constructor, no other methods.
- No fields except for stubs of `Wrapper` units – those may have `public static` fields exposing inner mocks.
- Constants from implemented `TestData` interfaces.
- If different return values are needed based on environment state, the stub must consult `TestBase.environmentState` (see below).

### Test Unit
- Tests a specific behaviour of a `Service` or a bug.
- Class name: `<ServiceName><BehaviourNameOrBugId>Test`. Behaviour name may be omitted if the test covers the whole service. (e.g. SelectBestFit2445Test)
- Each discovered bug has a dedicated test unit reproducing the bug.
- The tested service is instantiated manually using its constructor, passing mocks obtained from `Stub` units.  
  Example: `userService = new UserService(userRepositoryStub, emailWrapperStub);`
- The service instance is stored in a field named according to the service name (lowercased, without `Service` suffix).  
  Example: `private UserService userService;`
- Extends `io.github.magwas.konveyor.testing.TestBase`.  
  `TestBase` provides:
  - `public static String environmentState` (reset to `null` before each test)
  - `void given(String newState)` – sets `environmentState`
  - `void setUp()` (throws `Throwable`) – may be overridden for custom test setup
- Tests use `@DisplayName` to document the tested behaviour.
- All constants from implemented `TestData` interfaces.
- No `when()` or `given()` calls in tests – stubbing belongs in `Stub` units.
- No test data creation – use `TestData` units.
- No helper methods – use `TestUtil` units.
- Only allowed members: fields (the service under test, plus any mocks needed for constructor arguments), `@BeforeEach`/`@AfterEach` methods, and test methods annotated with `@Test`.

### TestUtil Unit
- Provides test helper methods and complex assertions.
- Class name ends with `TestUtil`.
- Static methods only.

## Internationalisation (i18n)

- User‑visible strings are internationalised.
- Exception messages and log messages are **not** internationalised.
- Strings are stored in `src/main/resources/messages.properties`.
- Use standard `ResourceBundle` with `Locale` obtained from a `LocaleState` (a `State` unit holding the current locale).
- A `MessageService` (or similar) may wrap `ResourceBundle` and is injectable where needed.
- In services that need i18n, inject `MessageService` and `LocaleState`.

## Miscellaneous Rules

- **Constructors** are allowed **only for dependency injection** and must contain no logic other than assigning parameters to `final` fields.  
  Default (no‑arg) constructors are allowed for `State`, `Wrapper`, and `Data` (records have implicit constructors).  
  Constructors in `Glue`, `Delegate`, and test code are unrestricted.
- **No constructor** in `Constant` (interface) or `Data` (records are fine).
- **No field injection** – do not use `@Inject` on fields.
- **No comments** in code. If a comment seems necessary, refactor into a well‑named method.
- Each unit (class, interface, record) resides in its own file.
- No file contains more than one top‑level unit.
- Never create Spring configuration classes or beans. Use Dagger modules and components instead.
- Dagger `@Provides` methods are allowed inside modules (which are Glue units).

