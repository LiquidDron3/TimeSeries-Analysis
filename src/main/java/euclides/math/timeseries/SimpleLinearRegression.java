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
import static euclides.math.LinAlgArrays.vector;

/**
 * Simple Linear Regression.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public final class SimpleLinearRegression implements Model {

    /**
     * The model parameters
     */
    private double alpha = Double.NaN;
    private double beta = Double.NaN;

    @Override
    public String modelName() {
        return "simple linear regression";
    }

    @Override
    public int modelRequirements() {
        return 2;
    }

    @Override
    public double init(double[] yi) {
        //
        // check input
        //
        final int datasize = check(yi);
        if (datasize < modelRequirements()) {
            throw new IllegalArgumentException("not enough initial values");
        }
        //
        // estimate model parameter
        //
        double[] xi = Model.xi(datasize);
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double sxy = 0.0;
        for (int i = 0; i < datasize; i++) {
            sx += xi[i];
            sy += yi[i];
            sxx += (xi[i]) * (xi[i]);
            sxy += (xi[i]) * (yi[i]);
        }
        this.beta = (datasize * sxy - sx * sy) / (datasize * sxx - sx * sx);
        this.alpha = sy / datasize - this.beta * sx / datasize;
        //
        // model approximations
        //
        double[] fi = vector(datasize);
        for (int i = 0; i < datasize; i++) {
            fi[i] = eval(xi[i]);
        }
        return Model.coefficientOfDetermination(yi, fi);
    }

    @Override
    public double eval(double x) {
        return this.alpha + x * this.beta;
    }

    @Override
    public String toString() {
        return this.modelName() + " { alpha=" + this.alpha + ", beta="
                + this.beta + " }";
    }
}
