# Photo Similarity API #

a Java API that writes a construct query to a ResearchSpace instance and fetches the URLs of images, and saves them to a file. It then adds each image to the index of a computer vision API for processing. Once images are indexed it retrieves visually similar images and inserts the similarity data into the ResearchSpace graph database. All of the data models are extensible and can be customized. The ResearchSpace instance then provides a user interface for browsing these results.

## Remote Visual Similarity API's

This is currenly implemented with the Pastec and Match API's, you can set the endpoint in the config file. Other API's can be easily added in the future.

## Run script

The Run.sh script runs the Java API, processes the construct query according to the model.

### Script Requirements

The run.sh script should be in the same folder as the PhotoSimilarity.jar.

### Script Configuration Variables

You can use a configuration file (ie. config.sh) to initialize the API. Below are presented the configuration variables:

* **query [required]** : a file containing the construct query, where the resulting object should be the image url.
* **endpoint [required]** : the endpoint, where you wish to harvest the data from.
* **pharos_user [optional]** : the username for the endpoint (default:admin).
* **pharos_password [optional]** : the password for the endpoint (default:admin).
* **vision_endpoint [required]** : the vision endpoint, where you wish save your visual similarity model data.
* **vision_user [optional]** : the username for the vision endpoint (default:admin).
* **vision_password [optional]** : the password for the vision endpoint (default:admin).
* **method [required]** : method used (Pastec/Match).
* **host [optional]** : host IP address of the server that runs Pastec/Match (default:localhost).
* **port [required]** : port that runs the method API.

### Bash commands

```bash
$ ./run.sh [config]
```
Example :
```bash
$ ./run.sh ./config.sh
```

### Outputs

* **./PhotoSimilarity-Workspace/Graphs/[timestamp]_graph.ttl** : graph created with construct query.
* **./PhotoSimilarity-Workspace/Graphs/image_uris.ttl** : image uris (urls) resulted from construct query.
* **./PhotoSimilarity-Workspace/IDs/[timestamp]_[method]IDs.json** : method's searcher Responses.
* **./PhotoSimilarity-Workspace/IDs/[timestamp]_[method]IDs.ttl** : statements where the subject is the image uri the property is custom:has_index and the object is the method's index. 
* **./PhotoSimilarity-Workspace/Model/[timestamp]_model.ttl** : resulting model for the the similarity method.
