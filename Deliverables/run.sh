#!/bin/bash
echo
#http://localhost:9999/blazegraph/sparql
now=$(date +"%Y-%m-%dT%H-%M-%S")
#Construct Query Evaluation
java -jar PhotoSimilarity.jar -q $1 -e $2

image_ids=$(curl -X GET http://localhost:4212/index/imageIds)
max_id=$(java -jar PhotoSimilarity.jar -image_ids $image_ids)

#Pastec Photo Similarity Evaluation & Indexing
{
	ID=$(expr $max_id + 1)
	echo -e "{\"results\" : ["
	while read line || [ -n "$line" ]; do
		# search image through indexes in pastec
		# search and then add so that it will not get a hit with the same image.
		search=$(curl -X POST -d '{"url":"'$line'"}' http://localhost:4212/index/searcher)
		echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": ${search} },"
		# add image index
		index=$(curl -X PUT -d '{"url":"'$line'"}' http://localhost:4212/index/images/$ID)
		#	echo $index
		IDs+="<${line}> <https://pharos.artresearch.net/custom/has_index> <http://localhost:4212/index/images/${ID}>.\n"
		# increament the id
		ID=$(expr $ID + 1)
	done < ./PhotoSimilarity-Workspace/Graphs/image_uris
	echo -e "{}]}"

} > "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.json"
# save ids in a file
echo -e $IDs > "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.ttl"

java -jar PhotoSimilarity.jar -m "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.json"