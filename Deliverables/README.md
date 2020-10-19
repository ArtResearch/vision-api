# Getting Pastec Ready

Clone Docker container for Pastec
```bash
$ git clone https://github.com/oki/docker-pastec.git
$ cd docker-pastec
$ docker build -t pastec .
$ docker run -ti -p 4212:4212 pastec
```

## Run API

Query : a file containing the construct query, where the resulting object should be the image url.
Endpoint : the endpoint, where you wish to harvest the data from.

```bash
$ ./run.sh [query] [endpoint]
```