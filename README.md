# Photo Similarity API #

The objective of this project was to create a Java API that processes image uls and sends them through Pastec to be indexed and analysed that results in pairs of similar images.

## Pastec

Make sure Pastec is running on a server.

## Run script

The script runs the Java API created, that processes the construct query and generates the model.

### Script Requirements

The run.sh script should be in the same folder as the PhotoSimilarity.jar.

### Script Arguments

* **Query [required]** : a file containing the construct query, where the resulting object should be the image url.
* **Endpoint [required]** : the endpoint, where you wish to harvest the data from.
* **Host [optional]** : host IP address of the server that runs Pastec (default host is set as localhost).
* **Port [optional]** : port that is set for Pastec (default port is set as 4212).

### Bash commands

```bash
$ ./run.sh [query] [endpoint] [host] [port]
```
Example :
```bash
$ ./run.sh query.txt http://localhost:9999/blazegraph/sparql
```

### Outputs

* **./PhotoSimilarity-Workspace/Graphs/[timestamp]_graph.ttl** : graph created with construct query.
* **./PhotoSimilarity-Workspace/Graphs/image_uris.ttl** : image uris (urls) resulted from construct query.
* **./PhotoSimilarity-Workspace/Pastec-IDs/[timestamp]_pastecIDs.json** : Pastec searcher Responses.
* **./PhotoSimilarity-Workspace/Pastec-IDs/[timestamp]_pastecIDs.ttl** : statements where the subject is the image uri the property is custom:has_index and the object is the pastec index. 
* **./PhotoSimilarity-Workspace/Model/[timestamp]_model.ttl** : resulting model for the the similarity method Pastec.