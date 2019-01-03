package io.smallrye.reactive.streams.operators;

import io.smallrye.reactive.streams.Engine;
import org.eclipse.microprofile.reactive.streams.operators.spi.Stage;

public class TerminalOperator<T extends Stage> extends Operator<T> {

    private TerminalStageFactory<T> factory;


    public TerminalOperator(Class<T> clazz, TerminalStageFactory<T> factory) {
        super(clazz);
        this.factory = factory;
    }

    public <I,O> TerminalStage<I, O> create(Engine engine, T stage) {
        return factory.create(engine, stage);
    }
}
