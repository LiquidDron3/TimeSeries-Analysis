/*
 * Fraunhofer Austria & CGV - Java Repository
 * 
 *   Maintained by
 *      Torsten Ullrich
 *      torsten.ullrich@fraunhofer.at
 * 
 *   Copyright
 *      Fraunhofer Austria Research GmbH,
 *      Geschaeftsbereich Visual Computing Graz, Austria.
 * 
 *      Institut f. ComputerGraphik und WissensVisualisierung
 *      Technische Universitaet Graz, Austria.
 * 
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; version 2
 *   of the License.
 * 
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 * 
 */
package euclides.math.timeseries;

import static euclides.math.LinAlgArrays.check;

/**
 * Constant regression.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public final class ConstantRegression implements Model {

    /**
     * The model parameter
     */
    private double average = Double.NaN;

    @Override
    public String modelName() {
        return "constant regression";
    }

    @Override
    public int modelRequirements() {
        return 1;
    }

    @Override
    public double init(double[] yi) {
        //
        // check input
        //
        final int datasize = check(yi);
        if (datasize < modelRequirements())
            throw new IllegalArgumentException("not enough initial values");
        //
        // estimate model parameter
        //
        double sum = 0.0;
        for (int i = 0; i < datasize; i++) {
            sum += yi[i];
        }
        this.average = sum / datasize;
        //
        // Constant regression is the baseline model 
        // that always predicts the mean.
        //
        return 0.0;
    }

    @Override
    public double eval(double x) {
        return this.average;
    }

    @Override
    public String toString() {
        return this.modelName() + " { average=" + this.average + " }";
    }
}
