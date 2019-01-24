FROM clojure:lein-2.8.3
EXPOSE 8080
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN lein uberjar
CMD ["java", "-jar", "/usr/src/app/target/generator-0.1.0-SNAPSHOT-standalone.jar"]
