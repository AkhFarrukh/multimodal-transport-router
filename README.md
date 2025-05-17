## Prerequisites

- Java 24 JDK or newer
- Maven 3.6 or newer

## Memory Requirements
- At least 4GB of available RAM (8GB recommended)

## Building the Project

Build the project using Maven. Run the following command in the root directory of the project:

```bash
 mvn clean package
```

This will create an executable JAR file in the `target` directory.

## Running the Application

Place the GTFS folder in the root directory of the project. The code is written specifically for the exact same names
of files and folders that are found on UV website.

Your directory structure should look like this:

```
.
├── GTFS
│   ├── DELIJN
│   │   ├── routes.csv
│   │   ├── stops.csv
│   │   ├── stop_times.csv
│   │   └── trips.csv
│   ├── SNCB
│   │   ├── routes.csv
│   │   ├── stops.csv
│   │   ├── stop_times.csv
│   │   └── trips.csv
│   ├── STIB
│   │   ├── routes.csv
│   │   ├── stops.csv
│   │   ├── stop_times.csv
│   │   └── trips.csv
│   └── TEC
│       ├── routes.csv
│       ├── stops.csv
│       ├── stop_times.csv
│       └── trips.csv
├── pom.xml
├── README.md
├── src
```

You should increase the default memory allocation for java to at least 4GB.
It is recommended to use 8GB for better performance if possible.

To do that you can put following argument after the `java` command when running the application:

For maximum heap size of 8GB, use:
```
-Xmx8g
```
For maximum heap size of 4GB, use:
```
-Xmx4g
```

Run the application using (Example with 8GB of heap space):
```bash
java -Xmx8g -jar target/STIB-1.0.jar [ Alveringem Nieuwe Herberg - Aubange - 10:30:00 ]
```
This is an example with values that were used in the assignment.



### Command Format

The basic command format is:

```
bash [departure stop - destination stop - HH:MM:SS] TRANSPORT_TYPE_EXCLUSION
```
### Parameters
Where:
- `departure stop` and `destination stop` are exact names of the stops as they appear in the GTFS data.
- `HH:MM:SS` is the time in 24+ hour format when you want to start your journey.

### Optional Parameters

You can add the following options after the main command in any order, but they must be separated by spaces:

- `METRO`: Penalizes usage of metro lines from the route
- `TRAM`: Penalizes usage of  tram lines from the route
- `BUS`: Penalizes usage of  bus lines from the route
- `TRAIN`: Penalizes usage of  train lines from the route
- `CHANGES`: Adds penalty for route changes (prefers routes with fewer transfers)

### Examples


```bash
# Basic route search
java -Xmx8g -jar target/STIB-1.0.jar [BOILEAU - ARSENAL - 10:00:00]
```

```bash
# Exclude metro and prefer fewer changes
java -Xmx8g -jar target/STIB-1.0.jar [BOILEAU - JANSON - 10:00:00] METRO CHANGES
```

```bash
# Find route without using buses
java -Xmx8g -jar target/STIB-1.0.jar [BOILEAU - DELTA - 09:30:00] BUS
```

