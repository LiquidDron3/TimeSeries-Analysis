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
import static euclides.math.LinAlgArrays.copy;
import static euclides.math.LinAlgArrays.toStringMaple;
import static euclides.math.LinAlgArrays.vector;

/**
 * Autoregressive integrated moving average model.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public class AutoRegressiveIntegratedMovingAverage implements Model {

    /**
     * The model parameters
     */
    private final int d;
    private double[] yi;
    private double[] di;
    //
    private Model arma = null;

    public AutoRegressiveIntegratedMovingAverage(int ar, int i, int ma) {
        //
        // check input
        //
        if (ar <= 0) {
            throw new IllegalArgumentException(
                    "first argument has to be positive");
        }
        if (i <= 0) {
            throw new IllegalArgumentException(
                    "second argument has to be positive");
        }
        if (ma < 0) {
            throw new IllegalArgumentException(
                    "third argument has to be semi-positive");
        }
        if (ar < ma) {
            throw new IllegalArgumentException(
                    "argument #3 (MA) has to be less than or equal to #1 (AR)");
        }
        //
        this.d = i;
        this.yi = null;
        this.di = null;
        //
        if (ma == 0) {
            this.arma = new AutoRegression(ar);
        } else {
            this.arma = new AutoRegressiveMovingAverage(ar, ma);
        }
    }

    @Override
    public String modelName() {
        return "autoregressive integrated moving average (ARIMA) model "
                + "with d=" + this.d + " using [" + this.arma.modelName() + "]";
    }

    @Override
    public int modelRequirements() {
        return this.arma.modelRequirements() + d;
    }

    @Override
    public double init(double[] yi) {
        final int datasize = check(yi);
        if (datasize < modelRequirements()) {
            throw new IllegalArgumentException("not enough initial values");
        }
        this.yi = copy(yi);
        this.di = vector(this.d);
        //
        double[] delta = yi;
        for (int i = 0; i < this.d; i++) {
            this.di[i] = delta[datasize - 1 - i];
            delta = difference(delta);
        }
        //
        return this.arma.init(delta);
    }

    @Override
    public double eval(double x) {
        //
        // discard non-initialized evaluations
        //
        if ((this.yi == null) || (this.di == null) || (this.arma == null)) {
            return Double.NaN;
        }
        //
        // the ARIMA model is not suited to evaluate historic values
        //
        if (x < 0) {
            return Double.NaN;
        }
        //
        // determine number of iteration steps
        //
        int steps = (int) Math.floor(x);
        double[] zs = vector(steps);
        for (int i = 0; i < steps; i++) {
            zs[i] = this.arma.eval(i);
        }
        //
        // if the base model returns nan, skip
        //
        for(double d : zs) {
            if (!Double.isFinite(d)) {
                return Double.NaN;
            }
        }
        //
        for (int i = this.d - 1; i >= 0; i--) {
            zs = integration(this.di[i], zs);
        }
        return zs[zs.length - 1];
    }

    private static double[] difference(double[] yi) {
        final int datasize = check(yi);
        double[] zi = vector(datasize - 1);
        for (int i = 0; i < datasize - 1; i++) {
            zi[i] = yi[i + 1] - yi[i];
        }
        return zi;
    }

    private static double[] integration(double base, double[] zi) {
        final int datasize = check(zi);
        double[] yi = vector(datasize);
        yi[0] = base;
        for (int i = 1; i < datasize; i++) {
            yi[i] = yi[i - 1] + zi[i];
        }
        return yi;
    }

    @Override
    public String toString() {
        return this.modelName() + " { d=" + this.d + ", delta offsets="
                + toStringMaple(this.di) + ", initial values="
                + toStringMaple(this.yi) + ", base model="
                + this.arma.toString() + " }";
    }
}
