# Flight Path Simplification — Technical Assignment for [FlySto](https://www.flysto.net/home)

## Task Overview
### Objective:
Given a list of many points (typically thousands) representing a recorded flight path, where each point is sampled at
approximately one point per second, identify and remove unnecessary points to simplify the path.

### Key Requirements
- The original path may contain long, straight segments.
- The simplified path must **never deviate** from the original path by **more than a given distance**.
- The goal is to **reduce the number of points** in the path.
- The solution **does not need to produce a minimal number of points**.
- The algorithm should be **efficient** and **scalable** for **large input sizes**.

## Solution
The solution, including algorithmic approach and design details, 
is documented in: [PathSimplifier.md](PathSimplifier/PathSimplifier.md)

## Building the Application
To build the project using `Gradle`:
```bash
./gradlew build
```
To build only the `PathSimplifier` module:
```bash
./gradlew :PathSimplifier:build
```

## Running the Application
You can run the application in two ways:

1. Run from JAR, after building the project:
```bash
java -jar build/libs/PathSimplifier.jar <args>
```

2. Run via Gradle:
```bash
./gradlew :PathSimplifier:run --args="<args>"
```

### Usage Example
Display the help message:

```bash
java -jar build/libs/PathSimplifier.jar --help
```
Or:
```bash
./gradlew :PathSimplifier:run --args="--help"
```

## CLI Usage
```text
Usage: PathSimplifierApp [-hV] -d=<maxDeviation> [-f=<filePath>] [-o=<outputDirectory>] 
                         [-a=<algorithms>[,<algorithms>...]]... [-g=<generationParams> [<generationParams>]]...
A Kotlin CLI app for simplifying flight paths (lat/lng, WGS84)
  -a, --algorithms=<algorithms>[,<algorithms>...]
                          List of algorithms to run (comma-separated). Default: all existing algorithms.
  -d, --deviation=<maxDeviation>
                          Maximum allowed deviation in kilometers.
  -f, --file=<filePath>   Path to input csv file.
  -g, --generate=<generationParams> [<generationParams>]
                          Amount of major way points and total amount of waypoints to generate.
  -h, --help              Show this help message and exit.
  -o, --outputDir=<outputDirectory>
                          Path to output directory
  -V, --version           Print version information and exit.
```

### Usage examples
#### Input flight path simplifying
```bash
java -jar ./PathSimplifier/build/libs/PathSimplifier.jar --deviation 0.1 --outputDir testResults/short --file ./PathSimplifier/src/main/resources/Short\ Flight\ Data.csv
```
Output:
```log
[2025-07-21 18:14:59.846] [INFO] FlightDataReader:16 - Reading flight path from file: ./PathSimplifier/src/main/resources/Short Flight Data.csv
[2025-07-21 18:14:59.850] [INFO] FlightDataReader:80 - Colum headers associated with latitude: 'lat', longitude: 'lon'
[2025-07-21 18:14:59.865] [WARN] FlightDataReader:58 - Skipping row with invalid params: CSVRecord [comment='null', recordNumber=11287, values=[45.3735695, 16816]]
[2025-07-21 18:14:59.866] [WARN] FlightDataReader:58 - Skipping row with invalid params: CSVRecord [comment='null', recordNumber=12140, values=[45868, 16.4837685]]
[2025-07-21 18:14:59.867] [WARN] FlightDataReader:58 - Skipping row with invalid params: CSVRecord [comment='null', recordNumber=12507, values=[46125, 16.3950214]]
[2025-07-21 18:14:59.871] [INFO] FlightDataReader:40 - Successfully parsed 15745 points
[2025-07-21 18:14:59.871] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV filtered_input.csv
[2025-07-21 18:14:59.902] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/short/filtered_input.csv
[2025-07-21 18:14:59.903] [INFO] Simplifier:12 - Simplifying path by Greedy algorithm, with max deviation = 0.1km
[2025-07-21 18:15:01.724] [INFO] Simplifier:21 - Path simplification by Greedy algorithm is done
[2025-07-21 18:15:01.728] [INFO] Simplifier:22 - Original points: 15745, Simplified: 44, Reduction: 99%
[2025-07-21 18:15:01.731] [INFO] Simplifier:28 - Original distance: 1025.8022825873236, Simplified: 1024.7515982997525, Losses: 0%
[2025-07-21 18:15:01.731] [INFO] Simplifier:34 - Total time: 1821ms
[2025-07-21 18:15:01.732] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV simplified_path_Greedy.csv
[2025-07-21 18:15:01.732] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/short/simplified_path_Greedy.csv
[2025-07-21 18:15:01.732] [INFO] Simplifier:12 - Simplifying path by Douglas-Peucker algorithm, with max deviation = 0.1km
[2025-07-21 18:15:01.753] [INFO] Simplifier:21 - Path simplification by Douglas-Peucker algorithm is done
[2025-07-21 18:15:01.753] [INFO] Simplifier:22 - Original points: 15745, Simplified: 48, Reduction: 99%
[2025-07-21 18:15:01.756] [INFO] Simplifier:28 - Original distance: 1025.8022825873236, Simplified: 1025.2831230005731, Losses: 0%
[2025-07-21 18:15:01.756] [INFO] Simplifier:34 - Total time: 21ms
[2025-07-21 18:15:01.756] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV simplified_path_Douglas-Peucker.csv
[2025-07-21 18:15:01.756] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/short/simplified_path_Douglas-Peucker.csv
```

#### Generated flight path simplifying
```bash
java -jar ./PathSimplifier/build/libs/PathSimplifier.jar --deviation 1.0 --outputDir testResults/generated --generate 50 5000
```
Output:
```log
WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.
[2025-07-21 18:22:08.598] [INFO] FlightDataGenerator:14 - Creating sample flight path with 50 major points and total amount 5000
[2025-07-21 18:22:08.601] [INFO] FlightDataGenerator:46 - Generated 5000 sample points
[2025-07-21 18:22:08.601] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV filtered_input.csv
[2025-07-21 18:22:08.617] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/generated/filtered_input.csv
[2025-07-21 18:22:08.619] [INFO] Simplifier:12 - Simplifying path by Greedy algorithm, with max deviation = 1.0km
[2025-07-21 18:22:08.630] [INFO] Simplifier:21 - Path simplification by Greedy algorithm is done
[2025-07-21 18:22:08.632] [INFO] Simplifier:22 - Original points: 5000, Simplified: 2305, Reduction: 53%
[2025-07-21 18:22:08.634] [INFO] Simplifier:28 - Original distance: 563871.2671539702, Simplified: 563728.075133407, Losses: 0%
[2025-07-21 18:22:08.634] [INFO] Simplifier:34 - Total time: 11ms
[2025-07-21 18:22:08.634] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV simplified_path_Greedy.csv
[2025-07-21 18:22:08.639] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/generated/simplified_path_Greedy.csv
[2025-07-21 18:22:08.639] [INFO] Simplifier:12 - Simplifying path by Douglas-Peucker algorithm, with max deviation = 1.0km
[2025-07-21 18:22:08.659] [INFO] Simplifier:21 - Path simplification by Douglas-Peucker algorithm is done
[2025-07-21 18:22:08.659] [INFO] Simplifier:22 - Original points: 5000, Simplified: 2543, Reduction: 49%
[2025-07-21 18:22:08.660] [INFO] Simplifier:28 - Original distance: 563871.2671539702, Simplified: 563867.0506755802, Losses: 0%
[2025-07-21 18:22:08.660] [INFO] Simplifier:34 - Total time: 20ms
[2025-07-21 18:22:08.660] [INFO] FlightDataWriter:14 - Initiate writing flight path to CSV simplified_path_Douglas-Peucker.csv
[2025-07-21 18:22:08.663] [INFO] FlightDataWriter:34 - CSV written to /Users/ttaaa/IdeaProjects/FlySto/testResults/generated/simplified_path_Douglas-Peucker.csv```
