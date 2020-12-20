Stellar isochrone fitting tool
==============================
Author: Johana Supíková
Should you have any questions or recommendations,
please contact me at xsupikov@fi.muni.cz.
==============================

StIFT finds 4 neighbours for input coordinates,
interpolates to create a line points and interpolates
again to obtain result estimation.

To upload custom grid, choose Grid > Upload new grid.
Accepted format is a .txt file without a header line with
space delimiter between attributes. To obtain correct uncertainty
results, please follow the results table's header for specification
of values representation:
Teff[lg] Lum[lg] Age[dex] Rad Mass Phase

To upload input data, choose Data > Upload input data file.
Provide a .txt file without header with lines containing either
TEMPERATURE LUMINOSITY
or
TEMPERATURE LUMINOSITY TEMPuncertainty LUMuncertainty
attributes separated by space. Missing uncertainties attributes
will be set to 0.