#!/bin/bash


dc(){
    # https://github.com/docker/compose/pull/5684
   docker-compose --compatibility \
        -f docker/dgraph.yml \
        -f docker/pgsql.yml \
        -f docker/dc.yml \
        "$@"
}

up(){
    dc up -d --build
}

down(){
    dc down 
}

drop_dgraph(){
    docker volume ls
    docker volume rm docker_moviequery.imdb.dgraph
    docker volume ls
}

drop_postgres(){
    docker volume ls
    docker volume rm docker_moviequery.imdb.postgres
    docker volume ls
}

term(){
   dc exec $1 bash -c "bash;"
}

permis(){
    sudo chmod 777 ./.data/imdb.rdf/*
}

"$@"