package blocks.health;

import akka.actor.typed.ActorRef;
import akka.japi.Pair;
import blocks.service.BlockStatus;

import java.util.Objects;
import java.util.function.Consumer;

public interface HealthProtocol {
    LogHealth LOG_HEALTH = new LogHealth();
    Stop STOP = new Stop();
    interface Message {

    }

    class GetHealth implements Message {
        public final ActorRef<Health> replyTo;

        public GetHealth(final ActorRef<Health> replyTo) {
            this.replyTo = replyTo;
        }
    }

    class Health implements Message {
        public final ServiceHealth serviceHealth;

        public Health(final ServiceHealth serviceHealth) {
            this.serviceHealth = serviceHealth;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Health health = (Health) o;
            return Objects.equals(serviceHealth, health.serviceHealth);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceHealth);
        }

        @Override
        public String toString() {
            return "Health{" +
                "serviceHealth=" + serviceHealth +
                '}';
        }
    }

    class RegisterComponent implements Message {
        public final String component;

        public RegisterComponent(final String component) {
            this.component = component;
        }
    }

    class UpdateComponentHealth implements Message {
        public final String component;
        public final ComponentHealth health;

        public UpdateComponentHealth(final String component, final ComponentHealth health) {
            this.component = component;
            this.health = health;
        }
    }

    class SubscribeToHealthChangeUpdates implements Message {
        public final String subscriberName;
        public final Consumer<Pair<Boolean, ComponentHealth>> consumerFunction;

        public SubscribeToHealthChangeUpdates(final String subscriberName, final Consumer<Pair<Boolean, ComponentHealth>> consumerFunction) {
            this.subscriberName = subscriberName;
            this.consumerFunction = consumerFunction;
        }
    }

    class UnSubscribeFromHealthChangeUpdates implements Message {
        public final String subscriberName;

        public UnSubscribeFromHealthChangeUpdates(final String subscriberName) {
            this.subscriberName = subscriberName;
        }
    }

    class RegisterBlock implements Message {
        public final String block;
        public final boolean mandatory;

        public RegisterBlock(final String block, final boolean mandatory) {
            this.block = block;
            this.mandatory = mandatory;
        }
    }

    class UpdateBlockStatus implements Message {
        public final String block;
        public final BlockStatus status;

        public UpdateBlockStatus(final String block, final BlockStatus status) {
            this.block = block;
            this.status = status;
        }
    }

    class LogHealth implements Message {
    }

    class Stop implements Message {
    }
}
