package test

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseDecorator
import org.junit.Assert
import spock.lang.Shared
import spock.lang.Specification
 import test.AppClient

 /**
 * To set up the testing environment please run
 * make restart-deps && make build-local && make run-local
 * or
 * make restart-app
  *
  * In order to reduce the building time the test code builds and starts
  * the application on the local host instead of using docker-compose directly. The approach to
  * environment setup should be changed when machine with a better
  * performance gets available.
 */
class AcceptanceSpecification extends Specification {

    @Shared
    def client = new AppClient()
    @Shared
    Process applicationProcess

    def setupSpec() {
        startStack()
        verifyApplicationStarted()
    }

    def cleanupSpec() {
        stopStack()
    }

    def "two plus two should equal four"() {
        given:
        int left = 2
        int right = 2

        when:
        int result = left + right

        then:
        result == 4
    }

    def verifyApplicationStarted() {
        client.getLiveStatus({ HttpResponseDecorator response ->
            Assert.assertEquals(200, response.status)
            def map = new ObjectMapper().readValue(response.entity.getContent(), HashMap.class)
            Assert.assertEquals(true, map["live"])
        })
    }

    Optional<Process> execCommand(Boolean wait = true, Map<String, String> envs = [:], String... command) {
        ProcessBuilder builder = new ProcessBuilder()
        builder.command(command)
        builder.inheritIO()
        builder.environment().putAll(envs)
        def process = builder.start()
        if (wait) {
            def exitCode = process.waitFor()
            Assert.assertEquals(0, exitCode)
            return Optional.empty()
        } else {
            return Optional.of(process)
        }
    }

    void startStack() {
        execCommand("make", "restart-deps")
        if (isRunningOnWindows()) {
            execCommand("make", "build-local-windows")
        } else {
            execCommand("make", "build-local")
        }
        applicationProcess = execCommand(false,
                ["MONGODB_HOST": "localhost"],
                "java", "-XX:+UseG1GC", "-jar", "./build/libs/etlq2020.jar").get()
    }

    void stopStack() {
        applicationProcess?.destroy()
    }

    private boolean isRunningOnWindows() {
        System.getProperty("os.name").toLowerCase().indexOf("win") >= 0
    }
}
