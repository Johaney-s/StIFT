# Stellar Isochrone Fitting Tool
Regular user, head over to the [latest release](https://github.com/Johaney-s/StIFT/releases/latest) and download the .jar file.
Explore, or read the [manual](/Manual.md).
## Overview
StIFT is a tool for estimating stellar characteristics. :sun_with_face:<br />
Methods of estimation are discussed in
> Sichevskij, S. G.: "Determining basic characteristics of stars from evolutionary computations", Astronomy Reports (2017), vol. 61: 193. https://doi.org/10.1134/S1063772917030076

## Features
- Supported input: filling form, clicking graph or uploading input file
- Showing results in the result table
- Uploading custom grid data or using default grid data
- Graph representation of grid data
- Exporting result data
- Filtering results based on phase
- Filtering tracks based on phase
- Uncertainty estimation
- Text mode, fast mode

## Requirements
Running the application requires Java 11, consider updating JRE in case relevant error message pops up.

## Running the application
From the source code, build with `mvn package`.<br />
In target folder run with `java -jar stift-1.0-SNAPSHOT.jar` or open the .jar file directly.

## Detailed description
This app is the topic of my bachelor's thesis at FI MUNI. It should be useful to astrophysicists as some stellar characteristics cannot be directly observed and need to be estimated from known data. The process of estimation is called isochrone fitting and is step by step described in
> MALKOV, O. Yu.; SICHEVSKIJ, S. G.; KOVALEVA, D. A.: "Parametrization of single and binary stars", Monthly Notices of the Royal Astronomical Society. (2009), vol. 401, no. 1: 695.  https://doi.org/10.1111/j.1365-2966.2009.15696.x

The grid data is grouped by mass and sorted by evolutionary status (phase). The default grid data is extracted from [CMD web interface](http://stev.oapd.inaf.it/cgi-bin/cmd) containing
PARSEC and COLIBRI tracks (Marigo et al. (2017)).
After the input is entered, four neighbours are found in the grid data and interpolated for line points estimation. Line points are then interpolated for final result estimation. The uncertainty is computed from the Monte Carlo simulation on a thousand of points with a normal distribution.
