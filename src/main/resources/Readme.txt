Stellar isochrone fitting tool
https://github.com/Johaney-s/StIFT
version: 2.0, check for latest ^
==============================
Author: Johana Supíková
Should you have any questions or recommendations,
please contact me at xsupikov@fi.muni.cz.
Any feedback is highly appreciated.
==============================

Default grid data is extracted from http://stev.oapd.inaf.it/cgi-bin/cmd.
PARSEC and COLIBRI tracks Marigo et al. (2017).

StIFT finds 4 neighbours for input coordinates,
interpolates to create line points and interpolates
again to obtain result estimation.

The method is described by Malkov et al. (2010)
https://doi.org/10.1111/j.1365-2966.2009.15696.x

The uncertainty is computed using 1000 estimations
following the Monte Carlo simulations, used also by
S. G. Sichevskij (2017):
https://doi.org/10.1134/S1063772917030076
To turn off computing the uncertainty and speed the estimation up,
uncheck the Compute uncertainty option. Uncertainty that is equal
to 0 is overwritten to uncertainty coming from points in evolutionary line.

To upload custom grid, choose Grid > Upload new grid.
Any header lines need to start with '#' sign to be parsed correctly.
Grid data need to be grouped by initial mass and
sorted by evolutionary phase. The GUI shows only
a part of the grid data, but all points are taken
into account when computing. Accepted format is
a .txt file. Use space or ',' delimiter between
attributes. To obtain correct uncertainty results,
please follow the results table's header for
specification of values representation:
#Teff[lg K] Lum[lg Lsun] Age[dex yrs] Rad[Rsun] Mass[Msun] Phase
3.66943 -0.72127 8.23306 10.66660 0.75000 5.00000
3.66932 -0.72112 8.26683 10.66690 0.75000 5.00053
3.66922 -0.72095 8.30172 10.66718 0.75000 5.00112
3.66912 -0.72077 8.33767 10.66747 0.75000 5.00179
...
or
#Teff[lg K] Lum[lg Lsun] Age[dex yrs] Rad[Rsun] Mass[Msun] Phase Label
3.66943 -0.72127 8.23306 10.66660 0.75000 5.00000 1
3.66932 -0.72112 8.26683 10.66690 0.75000 5.00053 1
3.66922 -0.72095 8.30172 10.66718 0.75000 5.00112 1
3.66912 -0.72077 8.33767 10.66747 0.75000 5.00179 1
...
to specify how points should be separated into tracks - if no label
is provided to identify tracks, tracks are separated by mass delimiter (0.01).

To upload input data, choose Data > Upload input data file.
Provide a .txt file and comment out header lines with '#' symbol:
#TEMPERATURE LUMINOSITY
3.944 1.508
4.053 2.383
...
or
#TEMPERATURE LUMINOSITY TEMPuncertainty LUMuncertainty
3.944 1.508 0.014 0.023
4.053 2.383 0.008 0.027
...
attributes separated by space. Missing uncertainties attributes
will be set to 0.

For filtering results, use phase filter in the phase column header.
Results filtered out won't appear in the export file.

For ignoring certain phases in the interpolation, restrict them
using checkboxes. Keep in mind that the result can be estimated
in the phase that is filtered out (for example if phase 3 is ignored
but phases 2 and 4 are chosen as the nearest tracks).

The phase labels in the default grid file are described at:
https://people.sissa.it/~sbressan/CAF09_V1.2S_M36_LT/readme.txt
StIFT uses these:
#Value	Phase	Description
4	NEAR_ZAM This point is very near the ZAMS
5	MS_BEG	H burning fully active
6	POINT_B	Almost end of the H burning. Small contraction phase begins here for interm. & massive stars
7	POINT_C	Small contraction ends here and star move toward RG
8	RG_BASE	RG base
9	RG_BMP1	RGB bump in Low Mass Stars (marked also for other masses)
10	RG_BMP2	RGB bump end in Low Mass Stars (marked also for other masses)
11	RG_TIP	Helium Flash or beginning of HELIUM Burning in intermediate and massive stars

To export results that appear in the results table, use menu Data > Export data.
In the export file, you will find estimated parameters together with their
uncertainties and estimation method.

There are different estimation methods that could have been applied to
estimate mean values - NONE means no method could resolve the parameters,
STAR MATCH means the input point is too close to some of the points
in the grid and therefore it copies this grid point's values. SIDE MATCH
only implies that the point lies too close to a side of the 4-angled
figure and was interpolated using only 2 of the neighbours. FULL ESTIMATION
suggests the standard process of estimating was followed - all 4 neighbours
were repetitively interpolated. ZAMS INSIDER means additional point had to be
estimated along the ZAMS track before continuing to the standard estimation
method - simply because no lower right neighbour was found in the grid.
ZAMS OUTSIDER means that the point lies outside of the graph, but the input
values' uncertainties hit inside the graph.


Use text mode from command line to see the steps of computation:
$ java -jar file_name.jar text TEMP LUM [TEMPunc LUMunc] [GRID_FILE]

or use fast mode to process input file from the command line:
$ java -jar file_name.jar fast INPUT_FILE [GRID_FILE] EXPORT_FILE_NAME

==============================
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
==============================
