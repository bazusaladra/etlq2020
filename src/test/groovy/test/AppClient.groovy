package test

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedRunnable
import net.jodah.failsafe.function.CheckedSupplier
import org.apache.http.HttpResponse
import org.junit.Assert

import java.time.Duration

import static org.spockframework.util.CollectionUtil.mapOf

class AppClient {

    def client = new RESTClient("http://localhost:8080/")

    HttpResponseDecorator getLiveStatus(Closure checkClosure) {
        CheckedSupplier<HttpResponseDecorator> check = {
            client.get([:], checkClosure)
        }
        withFailsafe(check)
    }

    static <T> T withFailsafe(CheckedSupplier<T> check, int maxDurationInSeconds = 120) {
        Failsafe.with(new RetryPolicy()
                .onFailedAttempt({ Exception e -> e.printStackTrace() })
                .withDelay(Duration.ofSeconds(2))
                .withMaxDuration(Duration.ofSeconds(120)))
                .get(check)
    }
}
