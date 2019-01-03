package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.smallrye.reactive.streams.Engine;
import io.smallrye.reactive.streams.operators.ProcessingStage;
import io.smallrye.reactive.streams.operators.ProcessingStageFactory;
import io.smallrye.reactive.streams.utils.Casts;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;
import org.reactivestreams.Processor;

import java.util.Objects;

/**
 * Implementation of the {@link Stage.ProcessorStage} stage ({@code via} operators).
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class ProcessorStageFactory implements ProcessingStageFactory<Stage.ProcessorStage> {

    @Override
    public <I, O> ProcessingStage<I, O> create(Engine engine, Stage.ProcessorStage stage) {
        Processor<I, O> processor = Casts.cast(Objects.requireNonNull(
                Objects.requireNonNull(stage).getRsProcessor())
        );

        return source -> Flowable.defer(() -> {
            Flowable<O> flowable = Flowable.fromPublisher(processor);
            source.safeSubscribe(processor);
            return flowable;
        });
    }
}
