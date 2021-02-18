# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.9.BUILD-SNAPSHOT/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.9.BUILD-SNAPSHOT/maven-plugin/reference/html/#build-image)
* [Spring Data Elasticsearch (Access+Driver)](https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#boot-features-elasticsearch)
* [Azure Support](https://github.com/Microsoft/azure-spring-boot/tree/master/azure-spring-boot)

#NodeJS
Download - 64-bit "Windows Binary (.zip)" file from https://nodejs.org/en/download/
Set path - Download\node-v14.15.5-win-x64

#Docker Steps
1. docker pull docker.elastic.co/elasticsearch/elasticsearch:7.11.1

2. docker pull docker.elastic.co/kibana/kibana:7.11.1

3. docker-compose up -d (to shutdown - docker-compose down)