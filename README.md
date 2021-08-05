# ArtVision API #

A Java API that processes image uls and sends them through Pastec/Match (or any other CV API) to be indexed and analysed that results in pairs of similar images.

## Pastec/Match

Make sure Pastec/Match is running on a server.

## Run script

The script runs the Java API created, that processes the construct query and parses the images according to the model.

### Script Requirements

The run.sh script should be in the same folder as the PhotoSimilarity.jar.

### Script Configuration Variables

You can use a configuration file (ie. config.conf) to initialize the API. Below are presented the configuration variables:

* **query [required]** : a file containing the construct query, where the resulting object should be the image url.
* **endpoint [required]** : the endpoint, where you wish to harvest the data from.
* **pharos_user [optional]** : the username for the endpoint.
* **pharos_password [optional]** : the password for the endpoint.
* **vision_endpoint [required]** : the vision endpoint, where you wish save your visual similarity data.
* **vision_user [optional]** : the username for the vision endpoint.
* **vision_password [optional]** : the password for the vision endpoint.
* **method [required]** : method used (Pastec/Match).
* **host [optional]** : host IP address of the server that runs Pastec/Match.
* **port [required]** : port that runs the method API.

for usernames and passwords, you can use the authentication.env.template (removing the extention template)

### Bash commands

```bash
$ ./run.sh [config]
```
Example :
```bash
$ ./run.sh config.conf
```

### Outputs

* **./PhotoSimilarity-Workspace/Graphs/[timestamp]_graph.ttl** : graph created with construct query.
* **./PhotoSimilarity-Workspace/Graphs/image_uris.ttl** : image uris (urls) resulted from construct query.
* **./PhotoSimilarity-Workspace/IDs/[timestamp]_[method]IDs.json** : method's searcher Responses.
* **./PhotoSimilarity-Workspace/IDs/[timestamp]_[method]IDs.ttl** : statements where the subject is the image uri the property is custom:has_index and the object is the method's index. 
* **./PhotoSimilarity-Workspace/Model/[timestamp]_model.ttl** : resulting model for the the similarity method.
* **./PhotoSimilarity-Workspace/Logs/[timestamp]_[method]_error.json** : contains logs of images that reported an error.
