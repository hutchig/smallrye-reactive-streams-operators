package io.smallrye.reactive.streams.operators;

import io.smallrye.reactive.streams.Engine;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;

/**
 * Factory to create {@link PublisherStage} instances.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@FunctionalInterface
public interface PublisherStageFactory<T extends Stage> {

    /**
     * Creates the instance.
     *
     * @param engine the reactive engine
     * @param stage  the stage
     * @param <O>  output data
     * @return the created processing stage, should never be {@code null}
     */
    <O> PublisherStage<O> create(Engine engine, T stage);

}
