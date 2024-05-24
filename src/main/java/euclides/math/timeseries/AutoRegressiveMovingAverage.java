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

import static euclides.math.LinAlgArrays.sub;
import static euclides.math.LinAlgArrays.check;
import static euclides.math.LinAlgArrays.cholesky;
import static euclides.math.LinAlgArrays.copy;
import static euclides.math.LinAlgArrays.matrix;
import static euclides.math.LinAlgArrays.mul;
import static euclides.math.LinAlgArrays.toStringMaple;
import static euclides.math.LinAlgArrays.transpose;
import static euclides.math.LinAlgArrays.vector;

/**
 * Autoregressive moving average model.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public class AutoRegressiveMovingAverage implements Model {

    /**
     * The model parameters
     */
    private final int p;
    private final int q;
    private double mu;
    private double[] phi;
    private double[] psi;
    private double[] eps;
    private double[] yi;

    /**
     * The constructor takes the degree of the auto-regression and of the moving
     * average.
     *
     * @param ar The specification parameter of the AR model.
     * @param ma The specification parameter of the MA model.
     */
    public AutoRegressiveMovingAverage(int ar, int ma) {
        //
        // check input
        //
        if (ar <= 0) {
            throw new IllegalArgumentException("argument has to be positive");
        }
        if (ma <= 0) {
            throw new IllegalArgumentException("argument has to be positive");
        }
        if (ar < ma) {
            throw new IllegalArgumentException(
                    "argument #2 (MA) has to be less than or equal to #1 (AR)");
        }
        //
        this.p = ar;
        this.q = ma;
        this.mu = Double.NaN;
        this.phi = null;
        this.psi = null;
        this.eps = null;
        this.yi = null;
    }

    @Override
    public String modelName() {
        return "autoregressive moving average (ARMA) model with parameters p="
                + this.p + ", q=" + this.q;
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
        // step #1: estimate intermediate residuum by linear regression
        //
        double[][] A_1 = matrix(datasize - this.p, this.p);
        double[] b_1 = vector(datasize - this.p);
        //
        for (int r = 0; r < datasize - this.p; r++) {
            for (int c = 0; c < this.p; c++) {
                A_1[r][c] = yi[r + c] - this.mu;
            }
            b_1[r] = yi[this.p + r] - this.mu;
        }
        //
        double[][] AT_1 = transpose(A_1);
        double[][] ATA_1 = mul(AT_1, A_1);
        double[] ATb_1 = mul(AT_1, b_1);
        double[] phi_1 = cholesky(ATA_1, ATb_1);
        //
        // in case of a failure, return nan
        //
        for (double d : phi_1) {
            if (!Double.isFinite(d)) {
                return Double.NaN;
            }
        }
        //
        // estimate residuals and copy history
        //
        double[] res = sub(mul(A_1, phi_1), b_1);
        this.eps = vector(this.p);
        for (int i = 0; i < this.p; i++) {
            this.eps[i] = res[datasize - 2 * this.p + i];
        }
        //
        // step #2: estimate model parameters by linear regression
        //
        double[][] A_2 = matrix(datasize - this.p - this.q, this.p + this.q);
        double[] b_2 = vector(datasize - this.p - this.q);
        //
        for (int r = 0; r < datasize - this.p - this.q; r++) {
            for (int c = 0; c < this.p; c++) {
                A_2[r][c] = yi[this.q + r + c] - this.mu;
            }
            for (int c = 0; c < this.q; c++) {
                A_2[r][this.p + c] = res[r + c];
            }
            b_2[r] = yi[this.p + this.q + r] - res[this.q + r] - this.mu;
        }
        double[][] AT_2 = transpose(A_2);
        double[][] ATA_2 = mul(AT_2, A_2);
        double[] ATb_2 = mul(AT_2, b_2);
        double[] phipsi = cholesky(ATA_2, ATb_2);
        //
        // in case of a failure, return nan
        //
        for (double d : phipsi) {
            if (!Double.isFinite(d)) {
                return Double.NaN;
            }
        }
        //
        this.phi = vector(this.p);
        // for(int i=0; i<this.p; i++) this.phi[i] = phipsi[i];
        System.arraycopy(phipsi, 0, this.phi, 0, this.p);
        //
        this.psi = vector(this.q);
        // for(int i=0; i<this.q; i++) this.psi[i] = phipsi[this.p + i];
        System.arraycopy(phipsi, this.p, this.psi, 0, this.q);
        //
        // model approximations fi
        //
        double[] fi = vector(datasize - this.p);
        double[] ri = vector(datasize - this.p);
        //
        // copy initial values
        //
        // for (int i = 0; i < datasize-this.p; i++) fi[i] = yi[this.p + i];
        System.arraycopy(yi, this.p, fi, 0, datasize - this.p);
        // for (int i = 0; i < datasize-this.p; i++) ri[i] = res[i];
        System.arraycopy(res, 0, ri, 0, datasize - this.p);
        //
        // estimate future values
        //
        for (int i = this.p; i < datasize - this.p; i++) {
            double f = 0;
            for (int j = 0; j < this.p; j++) {
                f += this.phi[j] * (fi[i - this.p + j] - this.mu);
            }
            for (int k = 0; k < this.q; k++) {
                f += this.psi[k] * ri[i - this.q + k];
            }
            fi[i] = f + this.mu;
            ri[i] = 0.0;
        }
        //
        // remove initial values, resp., shift yi and fi
        //
        double[] fiTail = vector(datasize - 2 * this.p);
        double[] yiTail = vector(datasize - 2 * this.p);
        for (int i = 0; i < datasize - 2 * this.p; i++) {
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
        if ((this.phi == null) || (this.psi == null) || (this.yi == null)
                || (this.eps == null)) {
            return Double.NaN;
        }
        //
        // the ARMA model is not suited to evaluate historic values
        //
        if (x < 0) {
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
        double[] res = copy(this.eps);
        for (int i = 0; i < steps; i++) {
            //
            // in each step the model is evaluated and ...
            //
            double f = 0;
            for (int j = 0; j < this.p; j++) {
                f += this.phi[j] * (fi[fi.length - this.p + j] - this.mu);
            }
            for (int k = 0; k < this.q; k++) {
                f += this.psi[k] * res[res.length - this.q + k];
            }
            //
            // ... the internal state is updated; i.e. the
            // historic values are shifted and the new value is added
            //
            for (int j = 1; j < this.p; j++) {
                fi[j - 1] = fi[j];
                res[j - 1] = res[j];
            }
            fi[this.p - 1] = f + this.mu;
            res[this.p - 1] = 0.0;
        }
        //
        // return last, most current value
        //
        return fi[this.p - 1];
    }

    @Override
    public String toString() {
        return this.modelName() + " { mu=" + this.mu + ", coefficients phi="
                + toStringMaple(this.phi) + ", coefficients psi="
                + toStringMaple(this.psi) + ", initial values="
                + toStringMaple(this.yi) + ", initial noise="
                + toStringMaple(this.eps) + " }";
    }
}
