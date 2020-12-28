# Stellar Isochrone Fitting Tool

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
- Standard deviation estimation
- Interpolation error estimation
- Total uncertainty estimation
- Text mode

## Requirements
Running the application requires Java 11, consider updating JRE in case relevant error message pops up.

## Running the application
Build with `mvn package`.<br />
In target folder run with `java -jar stift-1.0-SNAPSHOT.jar` or open the .jar file directly.

## Detailed description
This app is the topic of my bachelor's thesis at FI MUNI. It should be useful to astrophysicists as some stellar characteristics cannot be directly observed and need to be estimated from known data. The process of estimation is called isochrone fitting and is step by step described in
> MALKOV, O. Yu.; SICHEVSKIJ, S. G.; KOVALEVA, D. A.: "Parametrization of single and binary stars", Monthly Notices of the Royal Astronomical Society. (2009), vol. 401, no. 1: 695.  https://doi.org/10.1111/j.1365-2966.2009.15696.x

The grid data is grouped by mass and sorted by evolutionary status (phase). The default grid data is extracted from http://stev.oapd.inaf.it/cgi-bin/cmd.
After the input is entered, four neighbours are found in the grid data and interpolated for line points estimation. Line points are then interpolated for final result estimation. Error is computed from the partial derivation of the interpolation formulas, and the standard deviation is estimated from estimations of points created by taking into account input uncertainties.

## Text mode
Text mode is available for getting results of computation steps, which includes selected neighbours, line points, mean value, error estimation, estimations for points within the uncertainty area, standard deviation and final uncertainty. To run the text mode, use

`java -jar stift-1.0-SNAPSHOT.jar text TEMPERATURE LUMINOSITY [TEMPuncertainty LUMuncertainty]`.
