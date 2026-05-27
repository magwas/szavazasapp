# szavazas-core Compliance Enforcement Report

## Critical Priority Fixes (Blocking Release)

🛑 **Service Unit Violations**
- `InverterService`: Static methods violating AGENTS.md §3.1  
  Required: Convert to `@Inject`able service with dependency constructor
- `ParseQrResultService`: Multiple public methods (§3.4 violation)  
  Required: 
  ```java
  // BEFORE:
  public class ParseQrResultService {
      public QrData apply(Result result) {...}
      public Result validateResult(Result r) {...}
      public int parsePositive(...) {...}
  }
  
  // AFTER:
  public class ParseQrResultService {
      @Inject public ParseQrResultService() {}
      public QrData apply(Result result) {        
          Result validated = validateResult(result);
          return new QrData(voteName(...), parsePositive(...));
      }
      private Result validateResult(...) {...}
      private int parsePositive(...) {...}
  }
  ```

🛑 **Wrapper Unit Violations**
- `LoggerWrapper`: Inheritance violation (§4.5)  
  Required: Replace inheritance with composition
  ```java
  // BEFORE:
  public class LoggerWrapper extends Logger {...}

  // AFTER: 
  public class LoggerWrapper {
      private final Logger delegate;
      @Inject public LoggerWrapper(Logger logger) {
          this.delegate = logger;
      }
      // Wrapper methods...
  }
  ```

## High Priority Issues

### Dependency Injection Violations
⚠️ `DecodeQRService` mixes framework binding with business logic (AGENTS.md §2.2)  
Required: Separate QR decoding logic from framework binding

### Test Code Violations
⚠️ Test utilities as classes instead of interfaces (AGENTS.md §9.3)  
Required: Convert all test utilities to interfaces

## Compliance Metrics

| Category       | Compliance | Critical Issues |
|----------------|------------|-----------------|
| Service Units  | 62%        | 3               |
| Wrapper Units  | 38%        | 2               |
| DI Compliance  | 71%        | 1               |
| Test Standards | 58%        | 1               |
