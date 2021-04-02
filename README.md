# Stellar Isochrone Fitting Tool
Regular user, head over to the [latest release](https://github.com/Johaney-s/StIFT/releases/latest) and download the stift.jar file.
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
Open downloaded app by either double-clicking the stift.jar file or using command `java -jar stift.jar`.

If you want to work with the source code, build with `mvn package`.
In target folder run with `java -jar stift-1.0-SNAPSHOT.jar` or open the .jar file directly.

## Detailed description
This app is the topic of my bachelor's thesis at FI MUNI. It should be useful to astrophysicists as some stellar characteristics cannot be directly observed and need to be estimated from known data. The process of estimation is called isochrone fitting and is step by step described in
> MALKOV, O. Yu.; SICHEVSKIJ, S. G.; KOVALEVA, D. A.: "Parametrization of single and binary stars", Monthly Notices of the Royal Astronomical Society. (2009), vol. 401, no. 1: 695.  https://doi.org/10.1111/j.1365-2966.2009.15696.x

Evolutionary tracks or isochrones can be imported as the grid data. The default grid data is extracted from [PARSEC STELLAR EVOLUTION CODE](https://people.sissa.it/~sbressan/parsec.html):
> BRESSAN, A. et al: "PARSEC: stellar tracks and isochrones with the PAdova and TRieste Stellar Evolution Code", Monthly Notices of the Royal Astronomical Society. (2012), vol. 427, no. 1: 127.  https://doi.org/10.1111/j.1365-2966.2012.21948.x

After the input is entered, four neighbours are found in the grid data and interpolated for line points estimation. Line points are then interpolated for final result estimation. The uncertainty is computed from the Monte Carlo simulation on a thousand of points with a normal distribution.

## Thanks
My thanks go to Ernst Paunzen for the insight and consultations and to Martin Kuba for technical advice and supervision.

## License

MIT License

Copyright (c) 2021 Johana Supíková

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

