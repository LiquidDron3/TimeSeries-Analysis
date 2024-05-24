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
import static euclides.math.LinAlgArrays.cholesky;
import static euclides.math.LinAlgArrays.matrix;
import static euclides.math.LinAlgArrays.mul;
import static euclides.math.LinAlgArrays.toStringMaple;
import static euclides.math.LinAlgArrays.transpose;
import static euclides.math.LinAlgArrays.vector;

/**
 * The implementation of a polynomial (linear) regression.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public final class PolynomialRegression implements Model {

    /**
     * The model parameters
     */
    private final int order;
    private double[] polynomial;

    /**
     * The constructor takes the order of the polynomial to fit.
     *
     * @param k The order of the polynomial.
     */
    public PolynomialRegression(int k) {
        //
        // check input
        //
        if (k <= 0) {
            throw new IllegalArgumentException("argument has to be positive");
        }
        //
        this.order = k;
        this.polynomial = null;
    }

    @Override
    public String modelName() {
        return "polynomial regression of order k=" + this.order;
    }

    @Override
    public int modelRequirements() {
        return this.order + 1;
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
        double[][] X = vandermonde(xi, this.order);
        double[][] XT = transpose(X);
        double[][] XTX = mul(XT, X);
        double[] XTy = mul(XT, yi);
        this.polynomial = cholesky(XTX, XTy);
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
        //
        // discard non-initialized evaluations
        //
        if ((this.polynomial == null)) {
            return Double.NaN;
        }
        //
        double fx = 0.0;
        for (int j = 0; j < this.polynomial.length; j++) {
            fx += this.polynomial[j] * Math.pow(x, j);
        }
        return fx;
    }

    @Override
    public String toString() {
        String coeffs = toStringMaple(this.polynomial);
        return this.modelName() + " { coefficients=" + coeffs + " }";
    }

    private static double[][] vandermonde(double[] coefficients, int order) {
        //
        // check input
        //
        final int rows = check(coefficients);
        if (order <= 0) {
            throw new IllegalArgumentException("argument has to be positive");
        }
        //
        double[][] result = matrix(rows, order + 1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < order + 1; j++) {
                result[i][j] = Math.pow(coefficients[i], j);
            }
        }
        //
        return result;
    }
}
