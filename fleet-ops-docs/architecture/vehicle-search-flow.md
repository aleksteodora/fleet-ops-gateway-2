# Vehicle Search Flow

## Business Flow

The vehicle search starts with the **Free Provider**. If the Free Provider cannot return a valid result, the request falls back to the **Premium Provider**.

```text
                         ┌─────────────────────┐
                         │   Vehicle Search    │
                         │       (VIN)         │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   FREE Provider     │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
              Response OK      NO_RESULTS       Exception
              + all fields                           │
                    │               │                │
                    │               └──────┐         │
                    ▼                      │         │
             ┌──────────────┐              │         │
             │    FOUND     │              │         │
             │    FREE      │              │         │
             └──────┬───────┘              │         │
                    │                      │         │
                    ▼                      ▼         ▼
             Save History            ┌──────────────────┐
             FOUND / FREE            │ PREMIUM Provider │
                    │                └────────┬─────────┘
                    │                         │
                    ▼              ┌──────────┼───────────────┐
                  RETURN           │          │               │
                VehicleData        ▼          ▼               ▼
                               FOUND       NO_RESULTS     Exception
                                  │            │              │
                                  ▼            ▼              ▼
                           Save History   Save History    Save History
                           FOUND/PREMIUM  NO_RESULTS/   THIRD_PARTY_DOWN
                                            PREMIUM
                                  │            │              │
                                  ▼            ▼              ▼
                                RETURN        RETURN         THROW
                               VehicleData  NO_RESULTS   ThirdPartyDownException

```

## Free Provider

```text
FREE
 ├── FOUND
 │     └── return FOUND
 │
 ├── NO_RESULTS
 │     └── fallback → PREMIUM
 │
 └── ERROR
       └── fallback → PREMIUM
```

Any Free Provider failure or lack of results leads to the Premium Provider.

## Premium Provider

```text
PREMIUM
 ├── FOUND
 │     └── return FOUND
 │
 ├── NO_RESULTS
 │     └── return NO_RESULTS
 │
 └── ERROR
       └── THIRD_PARTY_DOWN
```

The Premium Provider is the final fallback. If it does not return a valid result, the search ends with the corresponding final status.

## Final Response

The API exposes only the business outcome through `VehicleSearchResult`.

The provider used internally is **not exposed to the client**.

### Free Provider returns a valid result

```text
VehicleSearchResult
├── status: FOUND
└── vehicleData: <vehicle data>
```

The result is returned immediately.

### Free Provider returns no results

The search falls back to the Premium Provider.

If Premium finds a result:

```text
VehicleSearchResult
├── status: FOUND
└── vehicleData: <vehicle data>
```

If Premium also finds no result:

```text
VehicleSearchResult
├── status: NO_RESULTS
└── vehicleData: null
```

### Free Provider fails

The search falls back to the Premium Provider.

If Premium finds a result:

```text
VehicleSearchResult
├── status: FOUND
└── vehicleData: <vehicle data>
```

If Premium also fails:

```text
VehicleSearchResult
├── status: THIRD_PARTY_DOWN
└── vehicleData: null
```

## Summary

The business rule is:

```text
Free Provider
    ↓
    ├── FOUND
    │     └── VehicleSearchResult
    │           ├── status: FOUND
    │           └── vehicleData: <vehicle data>
    │
    └── NO_RESULTS / ERROR
             ↓
        Premium Provider
             ↓
        ├── FOUND
        │     └── VehicleSearchResult
        │           ├── status: FOUND
        │           └── vehicleData: <vehicle data>
        │
        ├── NO_RESULTS
        │     └── VehicleSearchResult
        │           ├── status: NO_RESULTS
        │           └── vehicleData: null
        │
        └── ERROR
              └── VehicleSearchResult
                    ├── status: THIRD_PARTY_DOWN
                    └── vehicleData: null
```

The client only receives the final business result. It does not need to know which provider was contacted or whether a fallback occurred.
