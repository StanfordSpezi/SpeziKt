# HealthConnectOnFHIR

The HealthConnectOnFHIR library provides a mapper that converts supported [Android Health Connect](https://health.google/health-connect-android/) Records to corresponding [HL7(r) FHIR(r) R4 Observations](https://hl7.org/fhir/r4/observation.html) with standardized codes (e.g. [LOINC](https://loinc.org/)).

For more information, please refer to the API documentation.

## Mapping Table

| Health Connect Record         | FHIR Observation Category | FHIR Observation Code     | Unit       | Display                                    |
|-------------------------------|---------------------------|---------------------------|------------|--------------------------------------------|
| ActiveCaloriesBurnedRecord    | Activity                  | 41981-2                   | kcal       | Calories burned                            |
| BloodGlucoseRecord            |                           | 41653-7                   | mg/dL      | Glucose Glucometer (BldC) [Mass/Vol]       |
| BloodPressureRecord           | Vital Signs               | 85354-9                   | mmHg       | Blood pressure panel with all children optional |
| BodyFatRecord                 |                           | 41982-0                   | %          | Percentage of body fat Measured            |
| BodyTemperatureRecord         | Vital Signs               | 8310-5                    | Cel        | Body temperature                           |
| HeartRateRecord               | Vital Signs               | 8867-4                    | /min       | Heart rate                                 |
| HeightRecord                  | Vital Signs               | 8302-2                    | m          | Body height                                |
| OxygenSaturationRecord        | Vital Signs               | 59408-5                   | %          | Oxygen saturation in Arterial blood by Pulse oximetry |
| RespiratoryRateRecord         | Vital Signs               | 9279-1                    | /min       | Respiratory rate                           |
| StepsRecord                   | Activity                  | 55423-8                   | steps      | Number of steps                            |
| WeightRecord                  | Vital Signs               | 29463-7                   | kg         | Body weight                                |


## Installation

HealthConnectOnFHIR can be installed into your Android Studio project [via Jitpack](https://jitpack.io/#StanfordSpezi/SpeziKt/healthconnectonfhir).

## Usage

```kotlin
// Initialize the mapper
val mapper = RecordToObservationMapperImpl()

// Query a `Record` from Health Connect
val record =  // ..
    
// Map the record to an HL7 FHIR Observation    
val observation = mapper.map(record)
```

## Example

First, you will need to configure your application to use Android Health Connect. For more information, please see the [official documentation](https://developer.android.com/health-and-fitness/guides/health-connect).

```kotlin
// Initialize a `HealthConnectClient` (see Health Connect docs for full details)
val healthConnectClient = HealthConnectClient.getOrCreate(context)

// Define a time range for the query
val startTime = Instant.parse("2023-05-01T00:00:00Z")
val endTime = Instant.parse("2023-06-01T00:00:00Z")

// Query a list of `WeightRecord`s from Health Connect
val result = healthConnectClient.readRecords(
    ReadRecordsRequest(
        recordType = WeightRecord::class,
        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
    )
).records

// Initialize the mapper
val mapper = RecordToObservationMapperImpl()

// Convert each weight record to a FHIR Observation
result.forEach { weightRecord ->
    val observations = mapper.map(weightRecord)
    observations.forEach { observation ->
        // Do something with the observation
    }
}
```

## License

This project is licensed under the MIT license.

## Contributors

This project is developed as a part of the Stanford Biodesign for Digital Health projects at Stanford. See CONTRIBUTORS.md for a full list of all HealthConnectOnFHIR contributors.

## Notices

Health Connect is a registered trademark of Google. FHIR is a registered trademark of Health Level Seven International.