package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;
import org.junit.Test;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * Checks the behavior of the {@link FromPublisherStageFactory} class.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class FromPublisherStageFactoryTest extends StageTestBase {

    private final FromPublisherStageFactory factory = new FromPublisherStageFactory();

    @Test
    public void create() throws ExecutionException, InterruptedException {
        List<Integer> list = ReactiveStreams.fromPublisher(Flowable.fromArray(1, 2, 3)).toList().run().toCompletableFuture().get();
        assertThat(list).containsExactly(1, 2, 3);

        Optional<Integer> res = ReactiveStreams.fromPublisher(Flowable.just(25)).findFirst().run().toCompletableFuture().get();
        assertThat(res).contains(25);

        Optional<?> empty = ReactiveStreams.fromPublisher(Flowable.empty()).findFirst().run().toCompletableFuture().get();
        assertThat(empty).isEmpty();

        try {
            ReactiveStreams.fromPublisher(Flowable.error(new Exception("Boom"))).findFirst().run()
                    .toCompletableFuture().get();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Boom");
        }
    }

    @Test(expected = NullPointerException.class)
    public void createWithoutStage() {
        factory.create(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void createWithoutFunction() {
        factory.create(null, () -> null);
    }
}