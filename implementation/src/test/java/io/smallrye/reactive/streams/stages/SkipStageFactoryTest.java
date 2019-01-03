package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the behavior of the {@link SkipStageFactory} class.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class SkipStageFactoryTest extends StageTestBase {

    private final SkipStageFactory factory = new SkipStageFactory();

    @Test
    public void create() throws ExecutionException, InterruptedException {
        Flowable<Integer> flowable = Flowable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.computation());
        List<Integer> list = ReactiveStreams.fromPublisher(flowable).skip(5).toList().run()
                .toCompletableFuture().get();
        assertThat(list).hasSize(5).containsExactly(6, 7, 8, 9, 10);
    }

    @Test(expected = NullPointerException.class)
    public void createWithoutStage() {
        factory.create(null, null);
    }


}