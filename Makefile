compile-all:
	make compile-core && make compile-client && make compile-server

compile-core:
	cd core/ && ./mvnw clean install && ./mvnw compile

compile-client:
	make compile-core && cd client/ && ./mvnw clean install && ./mvnw compile

compile-server:
	make compile-core && cd server/ && ./mvnw clean install && ./mvnw compile

run-client:
	cd client/ && ./mvnw exec:java

run-server:
	cd server/ && ./mvnw spring-boot:run

make test-all:
	make test-core && make test-server

test-core:
	cd core/ && ./mvnw test

test-server:
	cd server/ && ./mvnw test