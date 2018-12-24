# Fix span info lost in single thread

unzip ./brave-core-4.13.4.jar
javac -cp /Users/kongxiangwen/.m2/repository/io/zipkin/brave/brave-core/4.13.4/brave-core-4.13.4.jar ./ServerResponseInterceptor.java
javac -cp /Users/kongxiangwen/.m2/repository/io/zipkin/brave/brave-core/4.13.4/brave-core-4.13.4.jar ./ServerRequestInterceptor.java
javac -cp /Users/kongxiangwen/.m2/repository/io/zipkin/brave/brave-core/4.13.4/brave-core-4.13.4.jar ./ThreadLocalServerClientAndLocalSpanState.java
cp ../*.class com/github/kristofa/brave/

jar -cvf brave-core-4.13.4.jar ./
