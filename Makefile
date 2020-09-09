build-local:
	gradlew build bootJar --console plain

start-local:
	java -jar ./build/libs/etlq2020.jar