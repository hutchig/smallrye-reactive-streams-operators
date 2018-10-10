package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.smallrye.reactive.streams.Engine;
import io.smallrye.reactive.streams.operators.ProcessingStage;
import io.smallrye.reactive.streams.operators.ProcessingStageFactory;
import org.eclipse.microprofile.reactive.streams.spi.Stage;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Implementation of {@link Stage.Filter} stage.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class FilterStageFactory implements ProcessingStageFactory<Stage.Filter> {

    @SuppressWarnings("unchecked")
    @Override
    public <IN, OUT> ProcessingStage<IN, OUT> create(Engine engine, Stage.Filter stage) {
        Objects.requireNonNull(stage);
        Predicate predicate = Objects.requireNonNull(stage.getPredicate());
        return source -> (Flowable<OUT>) source.filter(predicate::test);
    }
}
