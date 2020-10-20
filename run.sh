#!/bin/bash
echo
# Optional arguments default configuration
host=${3:-localhost}
port=${4:-4212}

now=$(date +"%Y-%m-%dT%H-%M-%S")
# Construct wuery evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $1 -e $2
# Request existing indexes
image_ids=$(curl -X GET http://$host:$port/index/imageIds)
max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids $image_ids)

#Pastec Photo Similarity Evaluation & Indexing
{
	ID=$(expr $max_id + 1)
	echo -e "{\"results\" : ["
	while read line || [ -n "$line" ]; do
		# Search through the images in pastec with the image url
		search=$(curl -X POST -d '{"url":"'$line'"}' http://$host:$port/index/searcher)
		echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": ${search} },"
		# Add image in Pastec
		index=$(curl -X PUT -d '{"url":"'$line'"}' http://$host:$port/index/images/$ID)
		# Generate ttl file
		IDs+="<${line}> <https://pharos.artresearch.net/custom/has_index> <http://${host}:${port}/index/images/${ID}>.\n"
		ID=$(expr $ID + 1)
	done < ./PhotoSimilarity-Workspace/Graphs/image_uris
	echo -e "{}]}"
} > "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.json"
# Save indexes in a file
echo -e $IDs > "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.ttl"
# Create model
java -jar target/PhotoSimilarity-0.1-assembly.jar -m "./PhotoSimilarity-Workspace/Pastec-IDs/${now}_pastecIDs.json"