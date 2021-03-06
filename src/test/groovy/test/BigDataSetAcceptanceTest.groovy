package test

import org.junit.Assert
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import test.AppClient

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals

@Stepwise
class BigDataSetAcceptanceTest extends Specification {

    def shouldRestartDb = true

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
        def response = client.sendDataFile("big_dataset/inputDataNo1.csv",
                { Response response -> assertEquals(200, response.status) })

        then:
        response["loaded"] == 23198
    }

    def "should return total Clicks for a Facebook Datasource"() {
        given:

        when:
        def response = client.queryData("big_dataset/queryNo1.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 1
        (response["results"] as List).contains([_id: null, Clicks: 172576])
    }

    def "should return total Clicks for a given Datasource for a given Date range"() {
        given:

        when:
        def response = client.queryData("big_dataset/queryNo2.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 3
        (response["results"] as List).contains([_id: [Datasource: "Facebook Ads"], Clicks: 1201])
        (response["results"] as List).contains([_id: [Datasource: "Twitter Ads"], Clicks: 17138])
        (response["results"] as List).contains([_id: [Datasource: "Google Ads"], Clicks: 319])
    }

    def "should return Click-Through Rate (CTR) per Datasource and Campaign"() {
        given:

        when:
        def response = client.queryData("big_dataset/queryNo3.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 185
        (response["results"] as List).contains([_id: [Datasource: "Twitter Ads", Campaign: "Mitgliedschaft KiMi"], ClickThroughRate: 0.04147421424014242d])
    }

    def "should return Impressions over time (daily) for November 2019"() {
        given:

        when:
        def response = client.queryData("big_dataset/queryNo4.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 30
        (response["results"] as List).contains([_id: [Daily: "2019-11-12T00:00:00.000+00:00"], Impressions: 275228])
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
        if (shouldRestartDb) {
            execCommand("make", "restart-deps")
        }
        if (isRunningOnWindows()) {
            execCommand("make", "build-local-windows")
        } else {
            execCommand("make", "build-local")
        }
        applicationProcess = execCommand(false,
                ["MONGODB_HOST": "localhost"],
                "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006",
                "-jar", "./build/libs/etlq2020.jar").get()
    }

    void stopStack() {
        applicationProcess?.destroy()
    }

    private static boolean isRunningOnWindows() {
        System.getProperty("os.name").toLowerCase().indexOf("win") >= 0
    }
}
