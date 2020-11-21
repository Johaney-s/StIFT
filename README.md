# Stellar Isochrone Fitting Tool

## Overview
StIFT is a tool for estimating stellar characteristics. :sun_with_face:<br />
The estimation is generaly described in
> Sichevskij, S.G.: „Determining basic characteristics of stars from evolutionary computations“, Astronomy Reports (2017), vol. 61: 193. https://doi.org/10.1134/S1063772917030076

## Features
- Supported input: filling form, clicking graph or uploading input file
- The output is shown in the result table
- Uploading custom grid data or using default grid data
- Graph representation of grid data
- Exporting result data
- Filtering results based on phase

## Running the application
Build with `mvn package`.<br />
In target folder run with `java -jar stift-1.0-SNAPSHOT.jar` or open the .jar file directly.

## Detailed description
This app is the topic of my bachelor's thesis at FI MUNI. It should be useful to astrophysicists as some stellar characteristics cannot be directly observed and need to be estimated from known data. The process of estimation is called isochrone fitting and is step by step described in
> MALKOV, O. Yu.; SICHEVSKIJ, S. G.; KOVALEVA, D. A.: „Parametrization of single and binary stars“, Monthly Notices of the Royal Astronomical Society. (2009), vol. 401, no. 1: 695.  https://doi.org/10.1111/j.1365-2966.2009.15696.x

The application groups grid data based on similar mass and estimates results for given input.
TextOnly class can be used as main in order to get intermediate results of interpolation (change x and y accordingly).
That includes the points of 4-angled figure, line and final interpolation.
