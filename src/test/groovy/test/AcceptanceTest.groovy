package test


import org.junit.Assert
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import test.AppClient

import javax.ws.rs.core.Response

import static org.junit.Assert.*

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
@Stepwise
class AcceptanceTest extends Specification {

    @Shared
    def client = new AppClient()
    @Shared
    Process applicationProcess

    def setupSpec() {
        startStack()
        verifyApplicationStarted()
    }

    def cleanupSpec() {
        client.shutdown()
        stopStack()
    }

    def "should load CSV file through the endpoint"() {
        given:

        when:
        def response = client.sendDataFile("inputDataNo1.csv",
                { Response response -> assertEquals(200, response.status) })

        then:
        response["loaded"] == 23198
    }

    def verifyApplicationStarted() {
        client.getLiveStatus({ Response response ->
            assertEquals(200, response.status)
            def map = response.readEntity(HashMap.class)
            assertEquals(true, map["live"])
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
