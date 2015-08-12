MVN_IMG = maven:3.3.3-jdk-8
run: mvn_cache
	docker run -it --rm \
		--volumes-from mvn_cache \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(shell pwd):/usr/src/app \
		-w /usr/src/app \
		$(MVN_IMG) \
		mvn docker:start docker:stop
mvn_cache:
	- docker run --name mvn_cache -v /root/.m2/repository $(MVN_IMG) /bin/true
run_boot2docker_https:
	docker run -it --rm \
		--volumes-from mvn_cache \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(shell pwd):/usr/src/app \
		-w /usr/src/app \
		--net=host \
		-e DOCKER_HOST=tcp://boot2docker:2376 \
		-e DOCKER_TLS_VERIFY=1 \
		-e DOCKER_CERT_PATH=/boot2docker/certs/boot2docker-vm \
		-v $(DOCKER_CERT_PATH):/boot2docker/certs/boot2docker-vm \
		$(MVN_IMG) \
		mvn docker:start docker:stop