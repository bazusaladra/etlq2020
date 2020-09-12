FROM openjdk:8

RUN apt-get update && apt-get install make

COPY . /usr/src/myapp

WORKDIR /usr/src/myapp

RUN make build-local

EXPOSE 8080

CMD ["make", "run-local"]