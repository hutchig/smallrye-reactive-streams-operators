package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;
import org.junit.After;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the behavior of the {@link FlatMapCompletionStageFactory}.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class FlatMapCompletionStageFactoryTest extends StageTestBase {

    private final FlatMapCompletionStageFactory factory = new FlatMapCompletionStageFactory();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @After
    public void cleanup() {
        executor.shutdown();
    }

    @Test
    public void create() throws ExecutionException, InterruptedException {
        Flowable<Integer> flowable = Flowable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.computation());

        List<String> list = ReactiveStreams.fromPublisher(flowable)
                .filter(i -> i < 4)
                .flatMapCompletionStage(this::square)
                .flatMapCompletionStage(this::asString)
                .toList()
                .run().toCompletableFuture().get();

        assertThat(list).containsExactly("1", "4", "9");
    }


    private CompletionStage<Integer> square(int i) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        executor.submit(() -> cf.complete(i * i));
        return cf;
    }

    private CompletionStage<String> asString(int i) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        executor.submit(() -> cf.complete(Objects.toString(i)));
        return cf;
    }

    @Test(expected = NullPointerException.class)
    public void createWithoutStage() {
        factory.create(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void createWithoutFunction() {
        factory.create(null, () -> null);
    }

    @Test(expected = NullPointerException.class)
    public void testInjectingANullCompletionStage() {
        AtomicReference<Subscriber<? super String>> reference = new AtomicReference<>();
        Publisher<String> publisher = reference::set;

        ReactiveStreams.fromPublisher(publisher)
                .flatMapCompletionStage(s -> (CompletionStage<String>) null)
                .toList()
                .run()
                .toCompletableFuture();

        reference.get().onNext("a");
    }

    @Test(expected = NullPointerException.class)
    public void testInjectingANullItem() {
        AtomicReference<Subscriber<? super String>> reference = new AtomicReference<>();
        Publisher<String> publisher = reference::set;

        ReactiveStreams.fromPublisher(publisher)
                .flatMapCompletionStage(s -> (CompletionStage<String>) null)
                .toList()
                .run()
                .toCompletableFuture();

        reference.get().onNext(null);
    }

}