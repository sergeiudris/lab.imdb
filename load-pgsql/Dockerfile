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
    # apt-get install postgresql postgresql-contrib && \
    sudo apt-get install -y postgresql-client

WORKDIR /opt/app

# VOLUME . /opt/code/app

# RUN curl http://static.aligulac.com/aligulac.sql.gz -o ./data/aligulac.sql.gz
# RUN wget http://static.aligulac.com/aligulac.sql.gz -P ./.data 
# RUN wget http://static.aligulac.com/aligulac.sql.gz -P ./.data 
# RUN wget http://static.aligulac.com/aligulac.sql.gz 

# ADD c ./

RUN psql --version
RUN useradd -ms /bin/bash postgres
USER postgres