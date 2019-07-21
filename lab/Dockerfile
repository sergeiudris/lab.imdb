FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y sudo && \
    apt-get install -y curl && \
    apt-get install -y wget && \
    apt-get install -y git-core && \
    apt-get install -y gpg && \
    apt-get install -y rlwrap && \
    apt-get install -y software-properties-common

RUN apt-get update && \
    apt-get install -y openjdk-11-jdk

RUN curl -O https://download.clojure.org/install/linux-install-1.10.0.442.sh && \
    chmod +x linux-install-1.10.0.442.sh && \
    sudo ./linux-install-1.10.0.442.sh

WORKDIR /tmp

ENV LEIN_VERSION=2.9.1
ENV LEIN_INSTALL=/usr/local/bin/

# Download the whole repo as an archive
RUN mkdir -p $LEIN_INSTALL \
  && wget -q https://raw.githubusercontent.com/technomancy/leiningen/$LEIN_VERSION/bin/lein-pkg \
  # && echo "Comparing lein-pkg checksum ..." \
  # && echo "019faa5f91a463bf9742c3634ee32fb3db8c47f0 *lein-pkg" | sha1sum -c - \
  && mv lein-pkg $LEIN_INSTALL/lein \
  && chmod 0755 $LEIN_INSTALL/lein \
  && wget -q https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip \
  && wget -q https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip.asc \
  && rm leiningen-$LEIN_VERSION-standalone.zip.asc \
  && mkdir -p /usr/share/java \
  && mv leiningen-$LEIN_VERSION-standalone.zip /usr/share/java/leiningen-$LEIN_VERSION-standalone.jar

ENV PATH=$PATH:$LEIN_INSTALL
ENV LEIN_ROOT 1


ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz


RUN mkdir -p /opt/app
RUN mkdir -p /root/.m2
WORKDIR /opt/app

# COPY ./.env /opt/
# ADD ./.m2/settings.xml /root/.m2/


# COPY .  /opt/code/app
COPY project.clj  /opt/app
RUN lein deps

COPY .  .

EXPOSE 4001
EXPOSE 8080
EXPOSE 7878


# CMD ["bash","c", "nrepl_clj"]
CMD ["bash","c", "dev"]


