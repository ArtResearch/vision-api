#!/bin/bash
echo
# http://localhost:9999/blazegraph/sparql

# Construct Query Evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $1 -e $2

# Pastec Photo Similarity Evaluation & Indexing
ID=1
while IFS= read -r line; do
	# search image through indexes in pastec
	search=$(curl -X POST -d '{"url":"'$line'"}' http://localhost:4212/index/searcher)
	echo $search
	# add image index
    index=$(curl -X PUT -d '{"url":"'$line'"}' http://localhost:4212/index/images/$ID)
	echo $index
	IDs+="<${line}> <https://pharos.artresearch.net/custom/has_index> <http://localhost:4212/index/images/${ID}>.\n"
#	echo -e $IDs > ./PhotoSimilarity-Workspace/Pastec-IDs/pastecIDs
	# increament the id
	ID=$(expr $ID + 1)
done < ./PhotoSimilarity-Workspace/Graphs/image_uris

# save ids in a file
echo -e $IDs > ./PhotoSimilarity-Workspace/Pastec-IDs/pastecIDs
