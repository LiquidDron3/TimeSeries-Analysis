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

import static euclides.math.LinAlgArrays.vector;

/**
 * The time series model.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public interface Model {

    /**
     * The model name.
     *
     * @return A human-readable name.
     */
    public String modelName();

    /**
     * The minimum number of samples to determine a model.
     *
     * @return The number of needed samples; the value must be positive.
     */
    public int modelRequirements();

    /**
     * Some model may need training.
     * 
     * @param x0 The explizit starting index of the training data.
     * @param yi The input data with the implicit indices
     *        <code>x0, x0+1, ...</code>.
     * @return The overall number of training samples of this model.
     */
    public default int training(double x0, double[] yi) {
        return 0;
    }

    /**
     * Some model may need training.
     * 
     * @return The completeness of the model training in percent; i.e. values
     *         less than or equal to zero indicate that the model is not ready
     *         to use. Values between 1 and 100 indicate the training deficit.
     *         The value 100 and values above show that the model is perfectly
     *         trained.
     */
    public default int trainingLevel() {
        return 100;
    }

    /**
     * Each model is initialized by previous, historic data.
     *
     * @param yi The input data with the implicit indices
     *        <code>-k, ... ,-2, -1, 0</code>.
     * @return Coefficient of determination.
     */
    public double init(double[] yi);

    /**
     * Having initialized the model, an arbitrary input value <code>x</code> can
     * be evaluated.
     *
     * @param x The input data.
     * @return The evaluation f(x).
     */
    public double eval(double x);

    // (* \newpage *)
    /**
     * Based on the specific time series model this method returns a forecast of
     * the next <code>prognosis</code> time steps.
     *
     * @param prognosis The number of time steps to predict.
     * @return The forecast with the implicit indices (time stamps)
     *         <code>1, ..., prognosis</code>.
     */
    public default double[] forecast(int prognosis) {
        //
        // check input
        //
        if (prognosis <= 0) {
            throw new IllegalArgumentException(
                    "parameter 'prognosis' has to be positive");
        }
        //
        // forecast
        //
        double[] estimation = vector(prognosis);
        for (int i = 0; i < prognosis; i++) {
            estimation[i] = eval(i + 1);
        }
        return estimation;
    }

    /**
     * Calculate the coefficient of determination based on the measured values
     * <code>yi</code> and the model values <code>fi</code>.
     *
     * @param yi The measured values (see <code>double init(double[]</code>).
     * @param fi The corresponding model values.
     * @return The coefficient of determination (or not-a-number in case of
     *         invalid data).
     */
    public static double coefficientOfDetermination(double[] yi, double[] fi) {
        //
        // check input
        //
        if ((yi == null) || (yi.length < 1) || (fi == null)
                || (yi.length != fi.length)) {
            return Double.NaN;
        }
        //
        // calculate mean of yi
        //
        double meanY = 0.0;
        for (int i = 0; i < yi.length; i++) {
            meanY = meanY + yi[i];
        }
        meanY = meanY / yi.length;
        //
        // calculate the total sum of squares and the residual sum of squares
        //
        double tot = 0.0;
        double res = 0.0;
        for (int i = 0; i < yi.length; i++) {
            tot = tot + (yi[i] - meanY) * (yi[i] - meanY);
            res = res + (yi[i] - fi[i]) * (yi[i] - fi[i]);
        }
        if (tot == 0) {
            return Double.NaN;
        }
        //
        return 1 - res / tot;
    }

    // (* \newpage *)
    /**
     * The implizit indices of the init data may be used explizitly.
     *
     * @param range The length of <code>init</code>'s <code>yi</code>.
     * @return The corresponding <code>xi</code>:
     *         <code>-k, ... ,-2, -1, 0</code>.
     */
    public static double[] xi(int range) {
        double[] result = new double[range];
        for (int i = 0; i < range; i++) {
            result[i] = i + 1 - range;
        }
        return result;
    }
}
