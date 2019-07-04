/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package software.amazon.ai.ndarray;

import java.nio.Buffer;
import software.amazon.ai.Context;
import software.amazon.ai.Translator;
import software.amazon.ai.TranslatorContext;
import software.amazon.ai.engine.Engine;
import software.amazon.ai.ndarray.types.DataDesc;
import software.amazon.ai.ndarray.types.DataType;
import software.amazon.ai.ndarray.types.Shape;
import software.amazon.ai.util.PairList;

/**
 * NDArray factories are used to create <I>NDArrays</I> (n-dimensional array on native engine).
 *
 * <p>NDFactory is implemented in each deep learning framework {@link Engine}. {@link NDArray}s are
 * resources that allocated in each deep learning framework's native memory space. NDFactory is the
 * key class that manages those native resources.
 *
 * <p>NDArray can only be created through NDFactory. By default, NDArray's lifecycle is attached to
 * the creator NDFactory. NDFactory itself implements {@link AutoCloseable}. When NDFactory is
 * closed, all the resource associated with it will be closed as well.
 *
 * <p>A typical place to obtain NDFactory is in {@link Translator#processInput(TranslatorContext,
 * Object)} or {@link Translator#processOutput(TranslatorContext, NDList)}.
 *
 * <p>The following is an example of how to use NDFactory:
 *
 * <pre>
 * public class MyTranslator implements Translator&lt;FloatBuffer, String&gt; {
 *
 *     &#064;Override
 *     public NDList processInput(TranslatorContext ctx, FloatBuffer input) {
 *         <b>NDFactory factory = ctx.getNDFactory();</b>
 *         NDArray array = <b>factory</b>.create(dataDesc);
 *         array.set(input);
 *         return new NDList(array);
 *     } // NDArrays created in this method will be closed after method return.
 * }
 * </pre>
 *
 * <p>NDFactory has a hierarchical structure; it has a single parent NDFactory and has child
 * NDFactories. When the parent NDFactory is closed, all children will be closed as well.
 *
 * <p>The Joule framework manage NDFactory's lifecycle by default. You only need to manage the user
 * created child NDFactory. Child NDFactory becomes useful when you create a large amount of
 * temporary NDArrays and want to free the resources earlier than parent NDFactory's lifecycle.
 *
 * <p>The following is an example of such a use case:
 *
 * <pre>
 * public class MyTranslator implements Translator&lt;List&lt;FloatBuffer&gt;&gt;, String&gt; {
 *
 *     &#064;Override
 *     public NDList processInput(TranslatorContext ctx, List&lt;FloatBuffer&gt; input) {
 *         NDFactory factory = ctx.getNDFactory();
 *         NDArray array = factory.create(dataDesc);
 *         for (int i = 0; i &lt; input.size(); ++i) {
 *             try (<b>NDFactory childFactory = factory.newSubFactory()</b>) {
 *                  NDArray tmp = <b>childFactory</b>.create(itemDataDesc);
 *                  tmp.put(input.get(i);
 *                  array.put(i, tmp);
 *             } // NDArray <i>tmp</i> will be closed here
 *         }
 *         return new NDList(array);
 *     }
 * }
 * </pre>
 *
 * <p>You can also close an individual NDArray. NDFactory won't double close them. In certain use
 * cases, you might want to return an NDArray outside of NDFactory's scope.
 *
 * @see NDArray
 * @see Translator
 * @see TranslatorContext#getNDFactory()
 */
public interface NDFactory extends AutoCloseable {

    /**
     * Creates an instance of {@link NDArray} with specified {@link Context}, {@link Shape}, and
     * {@link DataType}.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray create(Context context, Shape shape, DataType dataType);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc}.
     *
     * @param dataDesc the {@link DataDesc} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray create(DataDesc dataDesc);

    /**
     * Creates and initialize an instance of {@link NDArray} with specified {@link DataDesc}.
     *
     * @param dataDesc the {@link DataDesc} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data data to initialize the {@code NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray create(DataDesc dataDesc, Buffer data);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} and float array.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data the float array that needs to be set
     * @return new instance of {@link NDArray}
     */
    NDArray create(float[] data, Context context, Shape shape);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} and int array.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data the float array that needs to be set
     * @return new instance of {@link NDArray}
     */
    NDArray create(int[] data, Context context, Shape shape);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} and double array.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data the float array that needs to be set
     * @return new instance of {@link NDArray}
     */
    NDArray create(double[] data, Context context, Shape shape);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} and long array.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data the float array that needs to be set
     * @return new instance of {@link NDArray}
     */
    NDArray create(long[] data, Context context, Shape shape);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} and byte array.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param data the float array that needs to be set
     * @return new instance of {@link NDArray}
     */
    NDArray create(byte[] data, Context context, Shape shape);

    /**
     * Creates an instance of {@link NDArray} with specified {@link Shape} filled with zeros.
     *
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     * @see #zeros(Context, Shape, DataType)
     */
    default NDArray zeros(Shape shape) {
        return zeros(null, shape, null);
    }

    /**
     * Creates an instance of {@link NDArray} with specified {@link Context}, * {@link Shape}, and
     * {@link DataType} filled with zeros.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray zeros(Context context, Shape shape, DataType dataType);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} filled with zeros.
     *
     * @param dataDesc the {@link DataDesc} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray zeros(DataDesc dataDesc);

    /**
     * Creates an instance of {@link NDArray} with specified {@link Context}, {@link Shape}, and
     * {@link DataType} filled with ones.
     *
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray ones(Context context, Shape shape, DataType dataType);

    /**
     * Creates an instance of {@link NDArray} with specified {@link DataDesc} filled with ones.
     *
     * @param dataDesc the {@link DataDesc} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray ones(DataDesc dataDesc);

    /**
     * Returns evenly spaced values within a given interval.
     *
     * <p>Values are generated within the half-open interval ``[start, stop)`` (in other words, the
     * interval including `start` but excluding `stop`). For integer arguments the function is
     * equivalent to the Python built-in `range` function, but returns an instance of {@link
     * NDArray} rather than a list.
     *
     * @param start start of interval. The interval includes this value.
     * @param stop end of interval. The interval does not include this value.
     * @param step spacing between values.
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray arange(int start, int stop, int step, Context context, DataType dataType);

    /**
     * Returns evenly spaced values within a given interval in current context.
     *
     * <p>Values are generated within the half-open interval ``[start, stop)`` (in other words, the
     * interval including `start` but excluding `stop`). For integer arguments the function is
     * equivalent to the Python built-in `range` function, but returns an instance of {@link
     * NDArray} rather than a list.
     *
     * @param start start of interval. The interval includes this value.
     * @param stop end of interval. The interval does not include this value.
     * @param step spacing between values.
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    default NDArray arange(int start, int stop, int step, DataType dataType) {
        return arange(start, stop, step, getContext(), dataType);
    }

    /**
     * Returns evenly spaced values within a given interval in current context.
     *
     * <p>Values are generated within the half-open interval ``[start, stop)`` (in other words, the
     * interval including `start` but excluding `stop`). For integer arguments the function is
     * equivalent to the Python built-in `range` function, but returns an instance of {@link
     * NDArray} rather than a list.
     *
     * @param start start of interval. The interval includes this value.
     * @param stop end of interval. The interval does not include this value.
     * @param step spacing between values.
     * @return new instance of {@link NDArray}
     */
    default NDArray arange(int start, int stop, int step) {
        return arange(start, stop, step, getContext(), DataType.FLOAT32);
    }

    /**
     * Returns evenly spaced values within a given interval in current context with step 1.
     *
     * <p>Values are generated within the half-open interval ``[start, stop)`` (in other words, the
     * interval including `start` but excluding `stop`). For integer arguments the function is
     * equivalent to the Python built-in `range` function, but returns an instance of {@link
     * NDArray} rather than a list.
     *
     * @param start start of interval. The interval includes this value.
     * @param stop end of interval. The interval does not include this value.
     * @return new instance of {@link NDArray}
     */
    default NDArray arange(int start, int stop) {
        return arange(start, stop, 1, getContext(), DataType.INT32);
    }

    /**
     * Returns evenly spaced values starting from 0 in current context.
     *
     * <p>Values are generated within the half-open interval ``[start, stop)`` (in other words, the
     * interval including `start` but excluding `stop`). For integer arguments the function is
     * equivalent to the Python built-in `range` function, but returns an instance of {@link
     * NDArray} rather than a list.
     *
     * @param stop end of interval. The interval does not include this value.
     * @return new instance of {@link NDArray}
     */
    default NDArray arange(int stop) {
        return arange(0, stop, 1, getContext(), DataType.FLOAT32);
    }

    /**
     * Return evenly spaced numbers over a specified interval.
     *
     * <p>Returns num evenly spaced samples, calculated over the interval [start, stop].The endpoint
     * of the interval can optionally be excluded.
     *
     * @param start The starting value of the sequence.
     * @param stop The end value of the sequence.
     * @param num Number of samples to generate.
     * @param endpoint If True, stop is the last sample. Otherwise, it is not included.
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray linspace(double start, double stop, int num, boolean endpoint, Context context);

    /**
     * Return evenly spaced numbers over a specified interval in current context.
     *
     * <p>Returns num evenly spaced samples, calculated over the interval [start, stop].
     *
     * @param start The starting value of the sequence.
     * @param stop The end value of the sequence.
     * @param num Number of samples to generate.
     * @return new instance of {@link NDArray}
     */
    default NDArray linspace(double start, double stop, int num) {
        return linspace(start, stop, num, true, getContext());
    }

    /**
     * Draw random samples from a normal (Gaussian) distribution.
     *
     * <p>Samples are uniformly distributed over the half-open interval ``[low, high)`` (includes
     * low, but excludes high). In other words, any value within the given interval is equally
     * likely to be drawn by `uniform`
     *
     * @param low Lower boundary of the output interval. All values generated will be greater than
     *     or equal to low.
     * @param high Upper boundary of the output interval. All values generated will be less than
     *     high.
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray randomUniform(double low, double high, Shape shape, Context context, DataType dataType);

    /**
     * Draw random samples from a normal (Gaussian) distribution in current context.
     *
     * <p>Samples are uniformly distributed over the half-open interval ``[low, high)`` (includes
     * low, but excludes high). In other words, any value within the given interval is equally
     * likely to be drawn by `uniform`
     *
     * @param low Lower boundary of the output interval. All values generated will be greater than
     *     or equal to low.
     * @param high Upper boundary of the output interval. All values generated will be less than
     *     high.
     * @param shape the {@link Shape} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    default NDArray randomUniform(double low, double high, Shape shape) {
        return randomUniform(low, high, shape, getContext(), DataType.FLOAT32);
    }

    /**
     * Draw random samples from a normal (Gaussian) distribution. Samples are distributed according
     * to a normal distribution parametrized by *loc* (mean) and *scale* (standard deviation).
     *
     * @param loc Mean (centre) of the distribution.
     * @param scale Standard deviation (spread or "width") of the distribution.
     * @param shape Output shape.
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    NDArray randomNormal(double loc, double scale, Shape shape, Context context, DataType dataType);

    /**
     * Draw random samples from a normal (Gaussian) distribution. Samples are distributed according
     * to a normal distribution parametrized by mean = 0 and standard deviation = 1.
     *
     * @param shape Output shape.
     * @param context the {@link Context} of the {@link software.amazon.ai.ndarray.NDArray}
     * @param dataType the {@link DataType} of the {@link software.amazon.ai.ndarray.NDArray}
     * @return new instance of {@link NDArray}
     */
    default NDArray randomNormal(Shape shape, Context context, DataType dataType) {
        return randomNormal(0f, 1f, shape, context, dataType);
    }

    /**
     * Draw random samples from a normal (Gaussian) distribution. Samples are distributed according
     * to a normal distribution parametrized by mean = 0 and standard deviation = 1 in current
     * context.
     *
     * @param shape Output shape.
     * @return new instance of {@link NDArray}
     */
    default NDArray randomNormal(Shape shape) {
        return randomNormal(0f, 1f, shape, getContext(), DataType.FLOAT32);
    }

    /**
     * Draw samples from a multinomial distribution. The multinomial distribution is a multivariate
     * generalisation of the binomial distribution. Take an experiment with one of ``p`` possible
     * outcomes. An example of such an experiment is throwing a dice, where the outcome can be 1
     * through 6. Each sample drawn from the distribution represents n such experiments. Its values,
     * ``X_i = [X_0, X_1, ..., X_p]``, represent the number of times the outcome was ``i``.
     *
     * @param n Number of experiments.
     * @param pValues Probabilities of each of the p different outcomes. These should sum to 1
     *     (however, the last element is always assumed to account for the remaining probability, as
     *     long as ``sum(pvals[:-1]) &lt;= 1)``
     * @param shape Output shape
     * @return Returns the random NDArray
     */
    NDArray randomMultinomial(int n, NDArray pValues, Shape shape);

    /**
     * Return a single sample from a multinomial distribution. The multinomial distribution is a
     * multivariate generalisation of the binomial distribution. Take an experiment with one of
     * ``p`` possible outcomes. An example of such an experiment is throwing a dice, where the
     * outcome can be 1 through 6. Each sample drawn from the distribution represents n such
     * experiments. Its values, ``X_i = [X_0, X_1, ..., X_p]``, represent the number of times the
     * outcome was ``i``.
     *
     * @param n Number of experiments.
     * @param pValues Probabilities of each of the p different outcomes. These should sum to 1
     *     (however, the last element is always assumed to account for the remaining probability, as
     *     long as ``sum(pvals[:-1]) &lt;= 1)``
     * @return Returns the random NDArray
     */
    NDArray randomMultinomial(int n, NDArray pValues);

    /**
     * Returns parent NDFactory.
     *
     * @return parent NDFactory
     */
    NDFactory getParentFactory();

    /**
     * Creates a child NDFactory.
     *
     * <p>Child NDFactory will inherit default {@link Context} from this NDFactory.
     *
     * @return a child NDFactory
     */
    NDFactory newSubFactory();

    /**
     * Creates a child NDFactory with specified default {@link Context}.
     *
     * @param context default {@link Context}
     * @return a child NDFactory
     */
    NDFactory newSubFactory(Context context);

    /**
     * Returns default {@link Context} of this NDFactory.
     *
     * @return default {@link Context} of this NDFactory
     */
    Context getContext();

    /**
     * Attaches an NDArray or NDFactory to this factory.
     *
     * <p>Attached resource will be closed when this factory is closed.
     *
     * @param resource {@link AutoCloseable} resource to be attached
     */
    void attach(AutoCloseable resource);

    /**
     * Detaches an NDArray from this NDFactory's lifecycle.
     *
     * <p>The detached NDArray become un-managed, it's user's responsibility to close the resource.
     * Failed to close the resource has to wait on GC to be freed, and might cause out of native
     * memory.
     *
     * @param resource NDArray to be remove out of this NDFactory's lifecycle
     */
    void detach(AutoCloseable resource);

    /**
     * An engine specific generic invocation to native operator.
     *
     * <p>You should avoid using this function if possible. Since this function is engine specific,
     * using this API may cause portability issue. And user must be aware that native operation may
     * not compatible between each versions.
     *
     * @param operation native operation to performance
     * @param src array of source NDArray
     * @param dest array of destination to save output
     * @param params parameters to be passed to native operator
     * @throws IllegalArgumentException if operation is not supported by Engine
     * @throws software.amazon.ai.engine.EngineException if operation failed in native engine
     */
    void invoke(String operation, NDArray[] src, NDArray[] dest, PairList<String, ?> params);

    /**
     * An engine specific generic invocation to native operator.
     *
     * <p>You should avoid using this function if possible. Since this function is engine specific,
     * using this API may cause portability issue. And user must be aware that native operation may
     * not compatible between each versions.
     *
     * @param operation native operation to performance
     * @param src array of source NDArray
     * @param params parameters to be passed to native operator
     * @return output array of {@link NDArray}
     * @throws IllegalArgumentException if operation is not supported by Engine
     * @throws software.amazon.ai.engine.EngineException if operation failed in native engine
     */
    NDArray[] invoke(String operation, NDArray[] src, PairList<String, ?> params);

    /** {@inheritDoc} */
    @Override
    void close();
}
