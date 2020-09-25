package interfaces.base;

import akka.Done;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CacheInterface {

    <T> Optional<T> get(String key);

    void set(String key, Object value);

    void set(String key, Object value, int expiration);

    void remove(String key);

    <T> CompletionStage<Optional<T>> getAsync(String key);

    CompletionStage<Done> setAsync(String key, Object value);

    CompletionStage<Done> setAsync(String key, Object value, int expiration);

    CompletionStage<Done> removeAsync(String key);

    CompletionStage<Done> removeAll();
}
