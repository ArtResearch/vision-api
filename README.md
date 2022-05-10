# ArtVision API #


A Java API that processes image URLs from the main Pharos endpoint and sends them through Pastec/Match (or any other CV API) to be indexed and analysed. The results are transformed into turtle files that are then uploaded to the ArtVision endpoint (vision.artresearch.net). These image pairs can then be reviewed by collection curators and matched up. The matching process materializes owl:sameAs relationships between artworks that result in merged records on the main Pharos platform.

## API Architecture

![fig2](https://user-images.githubusercontent.com/6654854/134366819-ad8b03cf-f1ec-4c2a-ac7f-8043e4af51ba.png)

## Pastec/Match

Make sure Pastec/Match is running on a server and this the endpoint is accessible from this API.

## Run script

The script runs the Java API, it processes the construct query and parses the images according to the model.

### Script Requirements

The run.sh script should be in the same folder as the PhotoSimilarity.jar.

### Script Configuration Variables

You can use a configuration file (ie. config.conf) to initialize the API. Sample configuration variables:

* **query [required]** : a file containing the construct query, where the resulting object should be the image url.
  * Example Query
  ``` 
  PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX crmdig: <http://www.ics.forth.gr/isl/CRMdig/>
  PREFIX custom: <https://artresearch.net/custom/>
  CONSTRUCT {
      ?s custom:has_image ?image.
  } WHERE {
    ?s <https://artresearch.net/custom/has_provider> <https://artresearch.net/resource/marburg/source/Marburg>.
    ?s <https://artresearch.net/resource/fr/Work_depicted_by_Photo> ?photo.
    ?photo <http://www.cidoc-crm.org/cidoc-crm/P129i_is_subject_of>  ?image.
    ?image <http://www.cidoc-crm.org/cidoc-crm/P2_has_type> <https://artresearch.net/resource/marburg/type/4F6A2899-2950-328C-B172-54AD813EE993>.
    #BIND
    FILTER NOT EXISTS {?image <https://artresearch.net/resource/vocab/vision/pastec/has_index> ?index.}
  } LIMIT 2500
```  
The query must expose the variable ?s and ?image. In the query can also bee specified based on the provider. 
* **endpoint [required]** : the endpoint where you wish to harvest the data from.
* **pharos_user [optional]** : the username for the endpoint.
* **pharos_password [optional]** : the password for the endpoint.
* **vision_endpoint [required]** : the ArtVision endpoint, where you wish save your visual similarity data.
* **vision_user [optional]** : the username for the ArtVision endpoint.
* **vision_password [optional]** : the password for the ArtVision endpoint.
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
