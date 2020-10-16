#!/bin/bash
echo
# http://localhost:9999/blazegraph/sparql

# Construct Query Evaluation
#java -jar target/PhotoSimilarity-0.1-assembly.jar -q $1 -e $2

# Pastec Photo Similarity Evaluation & Indexing
{
	ID=1
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
} > ./PhotoSimilarity-Workspace/Pastec-IDs/pastecIDs.json
# save ids in a file
echo -e $IDs > ./PhotoSimilarity-Workspace/Pastec-IDs/pastecIDs.ttl