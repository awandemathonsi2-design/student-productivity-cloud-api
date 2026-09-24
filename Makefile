.PHONY: docker-up compile test package run

docker-up:
	docker compose up -d postgres

compile:
	mvn compile

test:
	mvn test

package:
	mvn clean package -DskipTests

run:
	java -jar target/student-productivity-cloud-api-1.0-SNAPSHOT.jar