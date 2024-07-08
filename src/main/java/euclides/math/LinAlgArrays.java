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
package euclides.math;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * This class implements linear algebra algorithms using arrays only.
 *
 * @author Torsten Ullrich
 * @version 4.1
 */
public final class LinAlgArrays {

    //////////////////////////////////////////////////////////////////////////
    //
    // Constructor-like methods.
    //
    //////////////////////////////////////////////////////////////////////////
    /**
     * This method creates a double-array based vector.
     *
     * @param rows The number of elements.
     * @return A double-array of size <code>rows</code> with elements
     *         initialized to <code>0.0</code>.
     */
    public static double[] vector(int rows) {
        //
        // check input
        //
        if (rows < 1) {
            throw new IllegalArgumentException(ERROR_POSITIVE);
        }
        //
        // create vector
        //
        double[] v = new double[rows];
        for (int i = 0; i < rows; i++) {
            v[i] = 0.0;
        }
        return v;
    }

    // (* \newpage *)
    /**
     * This method creates a 2d-double-array based matrix.
     *
     * @param rows The number of rows.
     * @param cols The number of columns.
     * @return A 2d-double-array of size <code>rows</code> times
     *         <code>cols</code> with elements initialized to <code>0.0</code>.
     */
    public static double[][] matrix(int rows, int cols) {
        //
        // check input
        //
        if (rows < 1) {
            throw new IllegalArgumentException(ERROR_POSITIVE);
        }
        if (cols < 1) {
            throw new IllegalArgumentException(ERROR_POSITIVE);
        }
        //
        // create matrix
        //
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] = 0.0;
            }
        }
        return m;
    }

    /**
     * This method copies a double-array based vector.
     *
     * @param vector The vector to copy.
     * @return A deep copy.
     */
    public static double[] copy(double[] vector) {
        //
        // check input
        //
        final int rows = check(vector);
        //
        // copy
        //
        double[] result = vector(rows);
        // for (int i = 0; i < rows; i++) result[i] = vector[i];
        System.arraycopy(vector, 0, result, 0, rows);
        //
        return result;
    }

    /**
     * This method copies a 2d-double-array based matrix.
     *
     * @param matrix The matrix to copy.
     * @return A deep copy.
     */
    public static double[][] copy(double[][] matrix) {
        //
        // check input
        //
        final long intint = check(matrix);
        final int rows = (int) (intint >> 32);
        final int cols = (int) intint;
        //
        // multiply
        //
        double[][] result = matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            // for (int j = 0; j < cols; j++) result[i][j] = matrix[i][j];
            System.arraycopy(matrix[i], 0, result[i], 0, cols);
        }
        //
        return result;
    }

    // (* \newpage *)
    //////////////////////////////////////////////////////////////////////////
    //
    // Consistency checks.
    //
    //////////////////////////////////////////////////////////////////////////
    /**
     * This routine checks whether a double array is a valid vector. In detail,
     * the array must be non-null, have a positive length, and all elements must
     * be finite values. If any of these requirements is not met, an illegal
     * argument exception is thrown.
     *
     * @param vector The vector to check.
     * @return The size / dimension of the vector.
     */
    public static int check(double[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException(ERROR_1D_ARRAY);
        }
        if (vector.length < 1) {
            throw new IllegalArgumentException(ERROR_SIZE);
        }
        //
        for (int i = 0; i < vector.length; i++) {
            if (!Double.isFinite(vector[i])) {
                throw new IllegalArgumentException(ERROR_NON_FINITE);
            }
        }
        //
        return vector.length;
    }

    /**
     * This routine checks whether a double 2D-array is a valid matrix. In
     * detail, the array must be non-null, have positive lengths for all rows
     * and columns, and all elements must be finite values. If any of these
     * requirements is not met, an illegal argument exception is thrown. The
     * returned value encodes the matrix sizes (number of rows and columns) in a
     * <code>long</code> value. To store two integers <code>x</code>,
     * <code>y</code> in a long integer <code>l</code> the method calculates
     * <code>long l = (((long)x) &#x3C;&#x3C; 32) | (y &#x26; 0xffffffffL)</code>,
     * which can be reversed via
     * <code>int x = (int)(l &#x3E;&#x3E; 32); int y = (int)l;</code>.
     *
     * @param matrix The matrix to check.
     * @return The size / dimension of the matrix.
     */
    public static long check(double[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException(ERROR_2D_ARRAY);
        }
        if (matrix.length < 1) {
            throw new IllegalArgumentException(ERROR_SIZE);
        }
        //
        int rows = matrix.length;
        int cols = Integer.MAX_VALUE;
        for (int r = 0; r < rows; r++) {
            if (matrix[r] == null) {
                throw new IllegalArgumentException(ERROR_1D_ARRAY);
            }
            if (matrix[r].length < 1) {
                throw new IllegalArgumentException(ERROR_SIZE);
            }
            //
            for (int c = 0; c < matrix[r].length; c++) {
                if (!Double.isFinite(matrix[r][c])) {
                    throw new IllegalArgumentException(ERROR_NON_FINITE);
                }
            }
            //
            cols = (cols < matrix[r].length) ? cols : matrix[r].length;
        }
        //
        long intint = (((long) rows) << 32) | (cols & 0xffffffffL);
        return intint;
    }

    // (* \newpage *)
    //////////////////////////////////////////////////////////////////////////
    //
    // Arithmetic methods.
    //
    //////////////////////////////////////////////////////////////////////////
    /**
     * Vector addition.
     *
     * @param vector0 Summand.
     * @param vector1 Summand.
     * @return Sum.
     */
    public static double[] add(double[] vector0, double[] vector1) {
        //
        // check input
        //
        final int rows = check(vector0);
        if (rows != check(vector1)) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // add
        //
        double[] result = vector(rows);
        for (int i = 0; i < rows; i++) {
            result[i] = vector0[i] + vector1[i];
        }
        //
        return result;
    }

    /**
     * Matrix addition.
     *
     * @param matrix0 Summand.
     * @param matrix1 Summand.
     * @return Sum.
     */
    public static double[][] add(double[][] matrix0, double[][] matrix1) {
        //
        // check input
        //
        final long intint0 = check(matrix0);
        final long intint1 = check(matrix1);
        if (intint0 != intint1) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        final int rows = (int) (intint0 >> 32);
        final int cols = (int) intint0;
        //
        // add
        //
        double[][] result = matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix0[i][j] + matrix1[i][j];
            }
        }
        //
        return result;
    }

    // (* \newpage *)
    /**
     * Vector subtraction.
     *
     * @param vector0 Minuend.
     * @param vector1 Subtrahend.
     * @return Difference.
     */
    public static double[] sub(double[] vector0, double[] vector1) {
        //
        // check input
        //
        final int rows = check(vector0);
        if (rows != check(vector1)) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // subtract
        //
        double[] result = vector(rows);
        for (int i = 0; i < rows; i++) {
            result[i] = vector0[i] - vector1[i];
        }
        //
        return result;
    }

    /**
     * Matrix subtraction.
     *
     * @param matrix0 Minuend.
     * @param matrix1 Subtrahend.
     * @return Difference.
     */
    public static double[][] sub(double[][] matrix0, double[][] matrix1) {
        //
        // check input
        //
        final long intint0 = check(matrix0);
        final long intint1 = check(matrix1);
        if (intint0 != intint1) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        final int rows = (int) (intint0 >> 32);
        final int cols = (int) intint0;
        //
        // sub
        //
        double[][] result = matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix0[i][j] - matrix1[i][j];
            }
        }
        //
        return result;
    }

    // (* \newpage *)
    /**
     * Multiplication.
     *
     * @param scalar Factor.
     * @param vector Factor.
     * @return Product.
     */
    public static double[] mul(double scalar, double[] vector) {
        //
        // check input
        //
        final int rows = check(vector);
        //
        // multiply
        //
        double[] result = vector(rows);
        for (int i = 0; i < rows; i++) {
            result[i] = scalar * vector[i];
        }
        //
        return result;
    }

    /**
     * Multiplication.
     *
     * @param vector Factor.
     * @param scalar Factor.
     * @return Product.
     */
    public static double[] mul(double[] vector, double scalar) {
        return mul(scalar, vector);
    }

    /**
     * Multiplication.
     *
     * @param scalar Factor.
     * @param matrix Factor.
     * @return Product.
     */
    public static double[][] mul(double scalar, double[][] matrix) {
        //
        // check input
        //
        final long intint = check(matrix);
        final int rows = (int) (intint >> 32);
        final int cols = (int) intint;
        //
        // multiply
        //
        double[][] result = matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        //
        return result;
    }

    /**
     * Multiplication.
     *
     * @param matrix Factor.
     * @param scalar Factor.
     * @return Product.
     */
    public static double[][] mul(double[][] matrix, double scalar) {
        return mul(scalar, matrix);
    }

    // (* \newpage *)
    /**
     * Multiplication.
     *
     * @param matrix Factor.
     * @param vector Factor.
     * @return Product.
     */
    public static double[] mul(double[][] matrix, double[] vector) {
        //
        // check input
        //
        final long intint = check(matrix);
        final int matrixRows = (int) (intint >> 32);
        final int matrixCols = (int) intint;
        final int vectorRows = check(vector);
        if (vectorRows != matrixCols) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // multiply
        //
        double[] result = vector(matrixRows);
        for (int i = 0; i < matrixRows; i++) {
            double sum = 0.0;
            for (int j = 0; j < matrixCols; j++) {
                sum = sum + (matrix[i][j] * vector[j]);
            }
            result[i] = sum;
        }
        //
        return result;
    }

    /**
     * Multiplication.
     *
     * @param matrix0 Factor.
     * @param matrix1 Factor.
     * @return Product.
     */
    public static double[][] mul(double[][] matrix0, double[][] matrix1) {
        //
        // check input
        //
        final long intint0 = check(matrix0);
        final int rows0 = (int) (intint0 >> 32);
        final int cols0 = (int) intint0;
        final long intint1 = check(matrix1);
        final int rows1 = (int) (intint1 >> 32);
        final int cols1 = (int) intint1;
        if (cols0 != rows1) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // create matrix and perform multiplication
        //
        double[][] result = matrix(rows0, cols1);
        for (int i = 0; i < rows0; i++) {
            for (int j = 0; j < cols1; j++) {
                double sum = 0.0;
                for (int k = 0; k < cols0; k++) {
                    sum = sum + (matrix0[i][k] * matrix1[k][j]);
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    // (* \newpage *)
    /**
     * Dot product, inner product, scalar product.
     *
     * @param vector0 Factor.
     * @param vector1 Factor.
     * @return Product.
     */
    public static double innerProduct(double[] vector0, double[] vector1) {
        //
        // check input
        //
        final int rows = check(vector0);
        if (rows != check(vector1)) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // dot product
        //
        double result = 0.0;
        for (int i = 0; i < rows; i++) {
            result = result + (vector0[i] * vector1[i]);
        }
        //
        return result;
    }

    /**
     * Transposition.
     *
     * @param matrix The matrix to flip.
     * @return The flipped matrix.
     */
    public static double[][] transpose(double[][] matrix) {
        //
        // check input
        //
        final long intint = check(matrix);
        final int rows = (int) (intint >> 32);
        final int cols = (int) intint;
        //
        // transpose
        //
        double[][] result = matrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        //
        return result;
    }

    // (* \newpage *)
    /**
     * Inversion. This routine implements the Gauss-Jordan algorithm. Please
     * note that the implementation does not check whether the input matrix has
     * full rank resp. is invertable. If the matrix cannot be inverted, no
     * exception is thrown and the result is undefined; i.e. it contains
     * infinite or not-a-number elements.
     *
     * @param matrix The matrix to invert.
     * @return The inverted matrix.
     */
    public static double[][] inverse(double[][] matrix) {
        //
        // check input
        //
        final long intint = check(matrix);
        final int rows = (int) (intint >> 32);
        final int cols = (int) intint;
        if (rows != cols) {
            throw new IllegalArgumentException(ERROR_INCOMPATIBLE);
        }
        //
        // copy matrix and create identity
        //
        final int size = rows;
        double[][] a = copy(matrix);
        double[][] b = matrix(size, size);
        double[][] x = matrix(size, size);
        int index[] = new int[size];
        for (int i = 0; i < size; ++i) {
            index[i] = i;
            b[i][i] = 1;
        }
        //
        // gauss jordan elimination
        //
        double[] c = vector(size);
        for (int i = 0; i < size; ++i) {
            double max = 0;
            for (int j = 0; j < size; ++j) {
                max = Math.max(max, Math.abs(a[i][j]));
            }
            c[i] = max;
        }
        //
        // upper triangle transformation
        //
        int pivot = 0;
        for (int i = 0; i < size - 1; ++i) {
            double pivot0 = 0;
            for (int j = i; j < size; ++j) {
                double pivot1 = Math.abs(a[index[j]][i]);
                pivot1 /= c[index[j]];
                if (pivot1 > pivot0) {
                    pivot0 = pivot1;
                    pivot = j;
                }
            }
            //
            // pivot order
            //
            int tmp = index[i];
            index[i] = index[pivot];
            index[pivot] = tmp;
            for (int j = i + 1; j < size; ++j) {
                final double factor = a[index[j]][i] / a[index[i]][i];
                a[index[j]][i] = factor;
                for (int k = i + 1; k < size; ++k) {
                    a[index[j]][k] -= factor * a[index[i]][k];
                }
            }
        }
        // (* \newpage *)
        //
        // update ratios
        //
        for (int i = 0; i < size - 1; ++i) {
            for (int j = i + 1; j < size; ++j) {
                for (int k = 0; k < size; ++k) {
                    b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }
        //
        // backward substitutions
        //
        for (int i = 0; i < size; ++i) {
            x[size - 1][i] = b[index[size - 1]][i]
                    / a[index[size - 1]][size - 1];
            for (int j = size - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < size; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    // (* \newpage *)
    /**
     * Least Squares Solution using the Cholesky algorithm. This routine is
     * intented to solve an over-determined linear system of equations Ax=b
     * using the least squares approach; i.e. by solving the system At Ax=At b
     * using the transpose matrix At of A.
     *
     * @param ATA The matrix product At A.
     * @param ATb The vector product At b.
     * @return A least squares solution; or undefined (containing infinite or
     *         not-a-number elements), if requirements are not met.
     */
    public static double[] cholesky(double[][] ATA, double[] ATb) {
        //
        // check input
        //
        final long intint = check(ATA);
        final int rows = (int) (intint >> 32);
        final int cols = (int) intint;
        if (rows != cols) {
            throw new IllegalArgumentException(
                    "argument has to be symmetric, positive definit");
        }
        //
        final int dim = check(ATb);
        if (dim != cols) {
            throw new IllegalArgumentException(
                    "arguments must have the same dimension");
        }
        //
        // store Cholesky decomposition in lower triangular matrix L
        //
        double[][] L = matrix(dim, dim);
        //
        for (int i = 0; i < dim; i++) {
            double sum = 0;
            for (int j = 0; j < i; j++) {
                sum += L[i][j] * L[i][j];
            }
            L[i][i] = Math.sqrt(ATA[i][i] - sum);
            for (int j = i + 1; j < dim; j++) {
                sum = 0;
                for (int k = 0; k < i; k++) {
                    sum += L[j][k] * L[i][k];
                }
                L[j][i] = (1.0 / L[i][i] * (ATA[j][i] - sum));
            }
        }
        //
        // solve inplace in two steps
        //
        double[] x = copy(ATb);
        //
        // 1. solve L*y = ATb for y by forward substitution (result in x)
        //
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < i; j++) {
                x[i] -= x[j] * L[i][j];
            }
            x[i] /= L[i][i];
        }
        //
        // 2. solve LT*x = y for x by back substitution
        //
        for (int i = dim - 1; i >= 0; i--) {
            for (int j = i + 1; j < dim; j++) {
                x[i] -= x[j] * L[j][i];
            }
            x[i] /= L[i][i];
        }
        return x;
    }

    // (* \newpage *)
    //////////////////////////////////////////////////////////////////////////
    //
    // Printing methods.
    //
    //////////////////////////////////////////////////////////////////////////
    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the default implementation uses an ASCII printer.
     *
     * @param vector The vector to print.
     * @return A <code>String</code> to print.
     */
    public static String toString(double[] vector) {
        return toStringASCII(vector);
    }

    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the default implementation uses an ASCII printer.
     *
     * @param matrix The matrix to print.
     * @return A <code>String</code> to print.
     */
    public static String toString(double[][] matrix) {
        return toStringASCII(matrix);
    }

    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a source code fragment of valid C/C++/Java
     * code.
     *
     * @param vector The vector to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringCode(double[] vector) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid vectors)
        //
        if (vector == null)
            return "null";
        if (vector.length < 1)
            return "{ }";
        final int rows = vector.length;
        //
        // define format
        //
        final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                Locale.US);
        otherSymbols.setDecimalSeparator('.');
        final DecimalFormat formatter = new DecimalFormat("0.0#########E0",
                otherSymbols);
        formatter.setGroupingUsed(false);
        //
        // create string
        //
        StringBuilder str = new StringBuilder();
        str.append("{ ");
        str.append(formatter.format(vector[0]));
        for (int i = 1; i < rows; i++) {
            str.append(", ").append(formatter.format(vector[i]));
        }
        str.append(" }");
        //
        return str.toString();
    }

    // (* \newpage *)
    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a source code fragment of valid C/C++/Java
     * code.
     *
     * @param matrix The matrix to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringCode(double[][] matrix) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid matrices)
        //
        if (matrix == null)
            return "null";
        if (matrix.length < 1)
            return "null";
        int rows = matrix.length;
        //
        // create string
        //
        StringBuilder str = new StringBuilder();
        str.append("{ ");
        str.append(toStringCode(matrix[0]));
        for (int i = 1; i < rows; i++) {
            str.append(", ").append(toStringCode(matrix[i]));
        }
        str.append(" }");
        //
        return str.toString();
    }

    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a source code fragment of valid Maple code.
     *
     * @param vector The vector to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringMaple(double[] vector) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid vectors)
        //
        if (vector == null)
            return "undefined";
        if (vector.length < 1)
            return "[ ]";
        final int rows = vector.length;
        //
        // write vector
        //
        StringBuilder str = new StringBuilder();
        str.append("[ ");
        str.append(vector[0]);
        for (int i = 1; i < rows; i++) {
            str.append(", ").append(vector[i]);
        }
        str.append(" ]");
        //
        return str.toString();
    }

    // (* \newpage *)
    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a source code fragment of valid Maple code.
     *
     * @param matrix The matrix to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringMaple(double[][] matrix) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid matrices)
        //
        if (matrix == null)
            return "undefined";
        if (matrix.length < 1)
            return "undefined";
        int rows = matrix.length;
        //
        // write output
        //
        StringBuilder str = new StringBuilder();
        str.append("[ ");
        str.append(toStringMaple(matrix[0]));
        for (int i = 1; i < rows; i++) {
            str.append(", ").append(toStringMaple(matrix[i]));
        }
        str.append(" ]");
        //
        return str.toString();
    }

    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a pretty printed ASCII version.
     *
     * @param vector The vector to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringASCII(double[] vector) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid vectors)
        //
        if (vector == null)
            return "undefined";
        if (vector.length < 1)
            return "[ ]";
        final int rows = vector.length;
        //
        // determine max length
        //
        int maxLength = Double.toString(vector[0]).length();
        for (int i = 1; i < rows; i++) {
            int length = Double.toString(vector[i]).length();
            maxLength = (length > maxLength) ? length : maxLength;
        }
        //
        // write output
        //
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            str.append("[ ");
            //
            String strValue = Double.toString(vector[i]);
            int padding = maxLength - strValue.length();
            for (int j = 0; j < padding; j++) {
                str.append(" ");
            }
            str.append(strValue);
            //
            str.append(" ]\n");
        }
        //
        return str.toString();
    }

    // (* \newpage *)
    /**
     * This method creates a <code>String</code>-based representation for
     * printing; the result will be a pretty printed ASCII version.
     *
     * @param matrix The matrix to print.
     * @return A <code>String</code> to print.
     */
    public static String toStringASCII(double[][] matrix) {
        //
        // check input (do not use check routine in order to enable debug
        // printing of
        // invalid matrices)
        //
        if (matrix == null)
            return "undefined";
        if (matrix.length < 1)
            return "undefined";
        int rows = matrix.length;
        int cols = matrix[0].length;
        for (int i = 0; i < matrix.length; i++)
            cols = Math.max(cols, matrix[i].length);
        //
        // determine max length
        //
        int maxLength = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String strValue = "err";
                try {
                    strValue = Double.toString(matrix[i][j]);
                } catch (Exception e) {
                }
                int length = strValue.length();
                maxLength = (length > maxLength) ? length : maxLength;
            }
        }
        //
        // write string
        //
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            str.append("[");
            for (int j = 0; j < cols; j++) {
                String strValue = "err";
                try {
                    strValue = Double.toString(matrix[i][j]);
                } catch (Exception e) {
                }
                int padding = maxLength - strValue.length() + 1;
                for (int k = 0; k < padding; k++) {
                    str.append(" ");
                }
                str.append(strValue);
            }
            str.append(" ]\n");
        }
        //
        return str.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private static final String ERROR_1D_ARRAY;
    private static final String ERROR_2D_ARRAY;
    private static final String ERROR_SIZE;
    private static final String ERROR_POSITIVE;
    private static final String ERROR_INCOMPATIBLE;
    private static final String ERROR_NON_FINITE;
    //
    static {
        ERROR_1D_ARRAY = "The parameter has to be an array.";
        ERROR_2D_ARRAY = "The parameter has to be a 2d array.";
        ERROR_SIZE = "The parameter has to have a positive size.";
        ERROR_POSITIVE = "The parameter has to positive.";
        ERROR_INCOMPATIBLE = "The parameters are size incompatible.";
        ERROR_NON_FINITE = "The parameter elements contain non-finite values.";
    }

    private LinAlgArrays() {
        //
        // no construction
        //
    }
}
