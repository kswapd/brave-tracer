# brave-tracer v1.0

-------------------
![logo](http://dcits.com/statics/images/dcits/logo.png)

## Introduction
 `dubbo` distributed system traced by zipkin server, which is based on [Google Dapper](http://research.google.com/pubs/pub36356.html) paper

## Software architecture

### 1. zipkin
  Use `zipkin` as restful server to receive restful sampling datea from client agent.
### 2. brave
  Use brave-core as client agent to sample rpc(netty) or restful(jetty) data to zipkin server.
### 3. elasticsearch
  Use elasticsearch as backend storage of zipkin.
### 4. spark
  Use spark to analyze data of elasticsearch.


## Install

### 1. zookeeper service for dubbo
  We use docker-compose files  to install zookeeper in docker environment, [Download docker-compose file here](https://gitee.com/kswapd/docker-devops.git),run:
  ```
    docker-compose up -d
  ```
### 2. zipkin & elasticsearch & spark service

  We use docker-compose files  to install all above services in docker environment, [Download docker-compose file here](https://gitee.com/kswapd/docker-devops.git),run

  ```
  docker-compose -f docker-zipkin/docker-compose.yml -f docker-zipkin/docker-compose-elasticsearch.yml up -d
  ```

### 3. Run dubbo provider and consumer


  * 3.1  Run `mvn clean install` to compile all modules.
  * 3.2  Run dubbo-provider-bar module:

```
        cd dubbo-provider-bar/target
        java -jar dubbo-provider-bar-1.0-SNAPSHOT.jar
```

  * 3.3 Run dubbo-provider module, this module also use dubbo-provider-bar as provider:

```
         cd dubbo-provider/target
         java -jar dubbo-provider-1.0-SNAPSHOT.jar
```

  * 3.4 Run dubbo-consumer module:

```
        cd dubbo-consumer/target
        java -jar dubbo-consumer-1.0-SNAPSHOT.jar
```

### How to use

  After installation steps,  the distributed tracing data has been sent to zipkin server, open url `http://{zipkin-server-address}:9411`,
you will get dubbo rpc monitor data.


## Development Instructions
**MUST** follow the instructions of **NOTE** section.
* Please install `JDK 1.8` before build the project.
* **MUST NOT** add any domain logics to this project.
* **MUST NOT** push any jar files, use maven dependency instead.
* **MUST NOT** push any unnecessary binary files.
* **MUST** push source code with meaningful message `git commit -m "meaningful message"`.
* **MUST** import `codequality/codestyle-formatter.xml`, and **format source code** (CTRL+SHIFT+F) and **organize imports** (CTRL+SHIFT+O) before commit.
* **MUST** use standard `JavaDoc Tags` on all java source code.
* **SHOULD** use `English` in JavaDoc, comments and any source code related resources **as possible**.
* **SHOULD** follow [Java Coding Conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf) and [Java Style Guide](https://google.github.io/styleguide/javaguide.html) if you haven't to improve code quality.


## Contributor

### 1. Fork by kongxw@dcits.com
### 2. Pull Request



## Problems
 1. If meet dependency problem, run `mvn clean install`


## Other resource

* [Google Dapper](http://research.google.com/pubs/pub36356.html)
