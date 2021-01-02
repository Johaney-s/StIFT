Stellar isochrone fitting tool
==============================
Author: Johana Supíková
Should you have any questions or recommendations,
please contact me at xsupikov@fi.muni.cz.
Any feedback is highly appreciated.
==============================

Default grid data is extracted from http://stev.oapd.inaf.it/cgi-bin/cmd.
PARSEC tracks (Bressan et al. (2012))
and COLIBRI tracks (Marigo et al. (2013)).

StIFT finds 4 neighbours for input coordinates,
interpolates to create line points and interpolates
again to obtain result estimation.

The method is described by (Malkov et al. (2010))
https://doi.org/10.1111/j.1365-2966.2009.15696.x

Uncertainty is computed as a combination of standard deviation
and interpolation error. If any is invalid or set to be hidden,
the SD or Err labels are crossed out in the result respectively.
Enlarge the width of the window if you can't see them.

To upload custom grid, choose Grid > Upload new grid.
Grid data need to be grouped by MASS and sorted by evolutionary
status (phase). StIFT shows only every 4th point in the line chart,
but all points are taken into account when doing the computation.
Accepted format is a .txt file WITHOUT A HEADER
line with space or ',' delimiter between attributes. To obtain correct
uncertainty results, please follow the results table's header
for specification of values representation:
Teff[lg] Lum[lg] Age[dex] Rad Mass Phase
(example)
3.66943 -0.72127 8.23306 10.66660 0.75000 5.0000000000
3.66932 -0.72112 8.26683 10.66690 0.75000 5.0005302886
3.66922 -0.72095 8.30172 10.66718 0.75000 5.0011231535
3.66912 -0.72077 8.33767 10.66747 0.75000 5.0017859950
...

To upload input data, choose Data > Upload input data file.
Provide a .txt file without header with lines containing either
TEMPERATURE LUMINOSITY
(example)
3.944 1.508
4.053 2.383
...
or
TEMPERATURE LUMINOSITY TEMPuncertainty LUMuncertainty
(example)
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
