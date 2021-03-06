build-local:
	./gradlew build -x test bootJar --console plain

build-local-windows:
	gradlew build -x test bootJar --console plain

run-local:
	MONGODB_HOST=localhost java -jar ./build/libs/etlq2020.jar

restart-app:
	make stop-app
	make start-app

start-app:
	docker-compose up --build -d

stop-app:
	docker-compose down --remove-orphans

restart-deps:
	make stop-deps
	make start-deps

start-deps:
	docker-compose up -d mongodb

stop-deps:
	docker-compose down --remove-orphans

acceptance-test:
	./gradlew test --console plain

acceptance-test-windows:
	gradlew test --console plain
