/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2013 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.legacy.translate;

import ij.ImagePlus;
import ij.measure.Calibration;
import imagej.data.Dataset;
import net.imglib2.meta.Axes;

/**
 * Synchronizes metadata bidirectionally between a {@link Dataset} and an
 * {@link ImagePlus}.
 * 
 * @author Barry DeZonia
 */
public class MetadataHarmonizer implements DataHarmonizer {

	/** Sets a {@link Dataset}'s metadata to match a given {@link ImagePlus}. */
	@Override
	public void updateDataset(final Dataset ds, final ImagePlus imp) {
		ds.setName(imp.getTitle());
		// copy calibration info where possible
		final int xIndex = ds.dimensionIndex(Axes.X);
		final int yIndex = ds.dimensionIndex(Axes.Y);
		final int cIndex = ds.dimensionIndex(Axes.CHANNEL);
		final int zIndex = ds.dimensionIndex(Axes.Z);
		final int tIndex = ds.dimensionIndex(Axes.TIME);
		final Calibration cal = imp.getCalibration();
		if (xIndex >= 0) {
			ds.setCalibration(cal.pixelWidth, xIndex);
			ds.setUnit(cal.getXUnit(), xIndex);
		}
		if (yIndex >= 0) {
			ds.setCalibration(cal.pixelHeight, yIndex);
			ds.setUnit(cal.getYUnit(), yIndex);
		}
		if (cIndex >= 0) {
			// OLD and likely bad
			// ds.setCalibration(1, cIndex);
		}
		if (zIndex >= 0) {
			ds.setCalibration(cal.pixelDepth, zIndex);
			ds.setUnit(cal.getZUnit(), zIndex);
		}
		if (tIndex >= 0) {
			ds.setCalibration(cal.frameInterval, tIndex);
			ds.setUnit(cal.getTimeUnit(), tIndex);
		}
		// no need to ds.update() - these calls should track that themselves
	}

	/** Sets an {@link ImagePlus}' metadata to match a given {@link Dataset}. */
	@Override
	public void updateLegacyImage(final Dataset ds, final ImagePlus imp) {
		imp.setTitle(ds.getName());
		// copy calibration info where possible
		final Calibration cal = imp.getCalibration();
		final int xIndex = ds.dimensionIndex(Axes.X);
		final int yIndex = ds.dimensionIndex(Axes.Y);
		final int cIndex = ds.dimensionIndex(Axes.CHANNEL);
		final int zIndex = ds.dimensionIndex(Axes.Z);
		final int tIndex = ds.dimensionIndex(Axes.TIME);
		if (xIndex >= 0) {
			cal.pixelWidth = ds.calibration(xIndex);
			cal.setXUnit(ds.unit(xIndex));
		}
		if (yIndex >= 0) {
			cal.pixelHeight = ds.calibration(yIndex);
			cal.setYUnit(ds.unit(yIndex));
		}
		if (cIndex >= 0) {
			// nothing to set on IJ1 side
		}
		if (zIndex >= 0) {
			cal.pixelDepth = ds.calibration(zIndex);
			cal.setZUnit(ds.unit(zIndex));
		}
		if (tIndex >= 0) {
			cal.frameInterval = ds.calibration(tIndex);
			cal.setTimeUnit(ds.unit(tIndex));
		}
	}
}
