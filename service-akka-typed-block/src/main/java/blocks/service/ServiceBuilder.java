package blocks.service;

import akka.actor.typed.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.time.Clock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;

public class ServiceBuilder {
    private final Map<BlockRef<?>, Block<?>> blocks = new HashMap<>();
    private final Map<BlockRef<?>, Set<BlockRef<?>>> blockDependencies = new HashMap<>();
    private Function<ActorSystem<?>, LoggingAdapter> requestsLoggerCreator = system -> Logging.getLogger(system.classicSystem(), "http-metrics");
    private Function<RequestLoggingDetails, String> requestsMessageFunction = RequestMetrics.DEFAULT_MESSAGE_FUNCTION;
    private Optional<Function<BlockContext, Runnable>> requestsStartNotificationRunnableCreator = Optional.empty();
    private Optional<Function<BlockContext, Runnable>> requestsEndNotificationRunnableCreator = Optional.empty();

    private ServiceBuilder() {

    }

    public static ServiceBuilder newService() {
        return new ServiceBuilder();
    }

    public <T> ServiceBuilder withBlock(final BlockRef<T> blockRef, final Block<T> block, final BlockRef<?>... dependencies) {
        blocks.put(blockRef, block);
        blockDependencies.put(blockRef, new HashSet<>(asList(dependencies)));
        return this;
    }

    public ServiceBuilder withRequestLogger(final Function<ActorSystem<?>, LoggingAdapter> creatorFunction) {
        this.requestsLoggerCreator = creatorFunction;
        return this;
    }

    public ServiceBuilder withRequestsMessageFunction(final Function<RequestLoggingDetails, String> messageFunction) {
        this.requestsMessageFunction = messageFunction;
        return this;
    }

    public ServiceBuilder withRequestsStartNotificationRunnableCreator(final Function<BlockContext, Runnable> runnableCreator) {
        this.requestsStartNotificationRunnableCreator = Optional.of(runnableCreator);
        return this;
    }

    public ServiceBuilder withRequestsEndNotificationRunnableCreator(final Function<BlockContext, Runnable> runnableCreator) {
        this.requestsEndNotificationRunnableCreator = Optional.of(runnableCreator);
        return this;
    }

    public ActorSystem<ServiceProtocol.Message> start(final Clock clock,
                                                      final ServiceConfig config) {
        return ActorSystem.create(ServiceActor.behavior(clock, config, blocks, blockDependencies, requestsLoggerCreator, requestsMessageFunction, requestsStartNotificationRunnableCreator, requestsEndNotificationRunnableCreator), "service");
    }
}
