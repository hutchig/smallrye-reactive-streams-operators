package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.smallrye.reactive.streams.Engine;
import io.smallrye.reactive.streams.operators.ProcessingStage;
import io.smallrye.reactive.streams.operators.ProcessingStageFactory;
import io.smallrye.reactive.streams.utils.recovery.OnErrorResumeWith;
import org.eclipse.microprofile.reactive.streams.spi.Graph;
import org.eclipse.microprofile.reactive.streams.spi.Stage;

import java.util.Objects;
import java.util.function.Function;

/**
 * Implementation of the {@link Stage.OnErrorResumeWith} stage.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class OnErrorResumeWithStageFactory implements ProcessingStageFactory<Stage.OnErrorResumeWith> {

    @SuppressWarnings("unchecked")
    @Override
    public <IN, OUT> ProcessingStage<IN, OUT> create(Engine engine, Stage.OnErrorResumeWith stage) {
        Function<Throwable, Graph> function = Objects.requireNonNull(stage).getFunction();
        Objects.requireNonNull(function);

        return source -> (Flowable<OUT>) RxJavaPlugins.onAssembly(
                new OnErrorResumeWith<>(source, err -> {
                    Graph graph = function.apply(err);
                    return Flowable.fromPublisher(
                            Objects.requireNonNull(engine.buildPublisher(Objects.requireNonNull(graph))));
                }));
    }
}
