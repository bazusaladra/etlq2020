package test

import org.junit.Assert
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import test.AppClient

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals

@Stepwise
class SmallDataSetAcceptanceTest extends Specification {

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
        def response = client.sendDataFile("small_dataset/inputDataNo1.csv",
                { Response response -> assertEquals(200, response.status) })

        then:
        response["loaded"] == 12
    }

    def "should return total Clicks for a Company A Datasource"() {
        given:

        when:
        def response = client.queryData("small_dataset/queryNo1.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 1
        (response["results"] as List).contains([_id: null, Clicks: 9])
    }

    def "should return total Clicks for a given Datasource for a given Date range"() {
        given:

        when:
        def response = client.queryData("small_dataset/queryNo2.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 2
        (response["results"] as List).contains([_id: [Datasource: "Company A"], Clicks: 5])
        (response["results"] as List).contains([_id: [Datasource: "Company B"], Clicks: 8])
    }

    def "should return Click-Through Rate (CTR) per Datasource and Campaign"() {
        given:

        when:
        def response = client.queryData("small_dataset/queryNo3.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 4
        (response["results"] as List).contains([_id: [Datasource: "Company A", Campaign: "Campaign 1"], ClickThroughRate: 3d / 30])
        (response["results"] as List).contains([_id: [Datasource: "Company A", Campaign: "Campaign 2"], ClickThroughRate: 5d / 30])
        (response["results"] as List).contains([_id: [Datasource: "Company B", Campaign: "Campaign 1"], ClickThroughRate: 9d / 40])
        (response["results"] as List).contains([_id: [Datasource: "Company B", Campaign: "Campaign 2"], ClickThroughRate: 5d / 30])
    }

    def "should return Impressions over time (daily) for 12-14 November 2019"() {
        given:

        when:
        def response = client.queryData("small_dataset/queryNo4.json",
                { Response response -> assertEquals(200, response.status) })

        then:
        (response["results"] as List).size() == 3
        (response["results"] as List).contains([_id: [Daily: "2019-11-12T00:00:00.000+00:00"], Impressions: 60])
        (response["results"] as List).contains([_id: [Daily: "2019-11-13T00:00:00.000+00:00"], Impressions: 40])
        (response["results"] as List).contains([_id: [Daily: "2019-11-14T00:00:00.000+00:00"], Impressions: 20])
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
//        execCommand("make", "restart-deps")
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
