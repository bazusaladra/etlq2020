package test


import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedSupplier
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider
import org.spockframework.util.IoUtil
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response
import java.time.Duration

class AppClient {

    Client client = ClientBuilder.newClient()
    WebTarget webTarget = client.target("http://localhost:8080/")

    def shutdown() {
        client.close()
    }

    Response getLiveStatus(Closure checkClosure) {
        CheckedSupplier<Response> check = {
            def response = webTarget.request().get()
            checkClosure(response)
        }
        withFailsafe(check)
    }

    Map sendDataFile(String filePath, Closure checkClosure) {
        def body = IoUtil.getText(new ClassPathResource(filePath).getInputStream())
        CheckedSupplier<Map> check = {
            def entity = Entity.entity(body, MediaType.TEXT_PLAIN_VALUE)
            def response = webTarget.path("/send").request().post(entity)
            checkClosure(response)
            response.readEntity(HashMap.class)
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
