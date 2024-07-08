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

import static euclides.math.LinAlgArrays.copy;
import static euclides.math.LinAlgArrays.cholesky;
import static euclides.math.LinAlgArrays.matrix;
import static euclides.math.LinAlgArrays.mul;
import static euclides.math.LinAlgArrays.toStringMaple;
import static euclides.math.LinAlgArrays.transpose;
import static euclides.math.LinAlgArrays.vector;
import static euclides.math.LinAlgArrays.check;

/**
 * Autoregressive model.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public class AutoRegression implements Model {

    /**
     * The model parameters
     */
    private final int p;
    private double mu;
    private double[] phi;
    private double[] yi;

    /**
     * The constructor takes the degree of the auto-regression.
     *
     * @param ar The specification parameter of the AR model.
     */
    public AutoRegression(int ar) {
        //
        // check input
        //
        if (ar <= 0) {
            throw new IllegalArgumentException("argument has to be positive");
        }
        //
        this.p = ar;
        this.mu = Double.NaN;
        this.phi = null;
        this.yi = null;
    }

    @Override
    public String modelName() {
        return "autoregressive (AR) model with parameter p=" + this.p;
    }

    @Override
    public int modelRequirements() {
        return 2 * this.p + 1;
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
        // estimate model parameter mu by average
        //
        double sum = 0.0;
        for (double y : yi) {
            sum += y;
        }
        //
        return init(yi, sum / datasize);
    }

    public double init(double[] yi, double center) {
        //
        // check input
        //
        final int datasize = check(yi);
        if (datasize < modelRequirements()) {
            throw new IllegalArgumentException("not enough initial values");
        }
        //
        // set first model parameter and copy history
        //
        this.mu = center;
        this.yi = vector(this.p);
        for (int i = 0; i < this.p; i++) {
            this.yi[i] = yi[datasize - this.p + i];
        }
        //
        // estimate phi by linear regression and ordinary least squares
        //
        double[][] A = matrix(datasize - this.p, this.p);
        double[] b = vector(datasize - this.p);
        //
        for (int r = 0; r < datasize - this.p; r++) {
            for (int c = 0; c < this.p; c++) {
                A[r][c] = yi[r + c] - this.mu;
            }
            b[r] = yi[this.p + r] - this.mu;
        }
        double[][] AT = transpose(A);
        double[][] ATA = mul(AT, A);
        double[] ATb = mul(AT, b);
        this.phi = cholesky(ATA, ATb);
        //
        // in case of a failure, return nan
        //
        for (double d : this.phi) {
            if (!Double.isFinite(d)) {
                return Double.NaN;
            }
        }
        //
        // model approximations fi
        //
        double[] fi = vector(datasize);
        //
        // copy initial values
        //
        // for (int i = 0; i < this.p; i++) fi[i] = yi[i];
        System.arraycopy(yi, 0, fi, 0, this.p);
        //
        // estimate future values
        //
        for (int i = this.p; i < datasize; i++) {
            double f = 0;
            for (int j = 0; j < this.p; j++) {
                f += this.phi[j] * (fi[i - this.p + j] - this.mu);
            }
            fi[i] = f + this.mu;
        }
        //
        // remove initial values, resp., shift yi and fi
        //
        double[] fiTail = vector(datasize - this.p);
        double[] yiTail = vector(datasize - this.p);
        for (int i = 0; i < datasize - this.p; i++) {
            fiTail[i] = fi[this.p + i];
            yiTail[i] = yi[this.p + i];
        }
        //
        return Model.coefficientOfDetermination(yiTail, fiTail);
    }

    @Override
    public double eval(double x) {
        //
        // discard non-initialized evaluations
        //
        if ((this.phi == null) || (this.yi == null)) {
            return Double.NaN;
        }
        //
        // the AR model is not suited to evaluate historic values
        //
        if ((x < 0)) {
            return Double.NaN;
        }
        //
        // determine number of iteration steps
        //
        int steps = (int) Math.floor(x);
        //
        // start with historic values and iterate:
        //
        double[] fi = copy(this.yi);
        for (int i = 0; i < steps; i++) {
            //
            // in each step the model is evaluated and ...
            //
            double f = 0;
            for (int j = 0; j < this.p; j++) {
                f += this.phi[j] * (fi[fi.length - this.p + j] - this.mu);
            }
            //
            // ... the internal state is updated; i.e. the
            // historic values are shifted and the new value is added
            //
            for (int j = 1; j < this.p; j++) {
                fi[j - 1] = fi[j];
            }
            fi[this.p - 1] = f + this.mu;
        }
        //
        // return last, most current value
        //
        return fi[this.p - 1];
    }

    @Override
    public String toString() {
        return this.modelName() + " { mu=" + this.mu + ", coefficients="
                + toStringMaple(this.phi) + ", initial values="
                + toStringMaple(this.yi) + " }";
    }
}
