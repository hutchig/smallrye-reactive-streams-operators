package io.smallrye.reactive.streams.stages;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the behavior of the {@link PeekStageFactory} when running from the Vert.x Context.
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class PeekStageFactoryTest extends StageTestBase {

    private final PeekStageFactory factory = new PeekStageFactory();

    @Test
    public void createOnVertxContext() {
        Flowable<Integer> flowable = Flowable.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.computation());

        List<Integer> squares = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        Set<String> threads = new LinkedHashSet<>();
        Callable<CompletionStage<List<String>>> callable = () ->
                ReactiveStreams.fromPublisher(flowable)
                        .filter(i -> i < 4)
                        .map(this::square)
                        .peek(squares::add)
                        .peek(i -> threads.add(Thread.currentThread().getName()))
                        .map(this::asString)
                        .peek(strings::add)
                        .peek(i -> threads.add(Thread.currentThread().getName()))
                        .toList()
                        .run();

        executeOnEventLoop(callable).assertSuccess(Arrays.asList("1", "4", "9"));
        assertThat(squares).containsExactly(1, 4, 9);
        assertThat(strings).containsExactly("1", "4", "9");
        assertThat(threads).hasSize(1).containsExactly(getCapturedThreadName());
    }


    private Integer square(int i) {
        return i * i;
    }

    private String asString(int i) {
        return Objects.toString(i);
    }

}