MVN_IMG = maven:3.3.3-jdk-8
mvn_cache:
	- docker run --name mvn_cache $(MVN_IMG) /bin/true
run: mvn_cache
	docker run -it --rm \
		--volumes-from mvn_cache \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(shell pwd):/usr/src/app \
		-w /usr/src/app \
		$(MVN_IMG) \
		mvn docker:start docker:stop