#!/bin/bash
echo
# Script Options
if [ $# -eq 0 ]
then
        echo -e "Missing options!\n"
        exit 0
fi
while getopts ":q:e:m:h:p:v:" opt; do
	case $opt in
		q)
			query="$OPTARG" >&2
			;;
		e)
			endpoint="$OPTARG" >&2
			;;
		v)
			vision_endpoint="$OPTARG" >&2
			;;
		m)
			method="$OPTARG" >&2
			;;
		h)
			host="$OPTARG" >&2
			;;
		p)
			port="$OPTARG" >&2
			;;
		\?)
			echo "Invalid option: -$OPTARG" >&2
			exit 1
			;;
		:)
			echo "Option -$OPTARG requires an argument." >&2
			exit 1
			;;
	esac
done
# Optional arguments default configuration
host=${host:-localhost}
port=${port:-4212}
method=${method,,}
now=$(date +"%Y-%m-%dT%H-%M-%S")
# Construct query evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $query -e $endpoint -m $method
# Request existing indexes
image_ids=$(curl -X GET http://$host:$port/index/imageIds)
max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids $image_ids)

IDs+="<https://pharos.artresearch.net/resource/custom/visual_similarity> {\n"
#Pastec Photo Similarity Evaluation & Indexing
{
	ID=$(expr $max_id + 1)
	echo -e "{\"results\" : ["
	while read line || [ -n "$line" ]; do
		# Search through the images in pastec with the image url
		search=$(curl -X POST -d '{"url":"'$line'"}' http://$host:$port/index/searcher)
		echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": ${search} },"
		# Add image in Pastec
		index=$(curl -X POST -d '{"url":"'$line'"}' http://$host:$port/index/images/$ID)
		# Generate ttl file
		IDs+="\t<${line}> <https://pharos.artresearch.net/custom/${method}/has_index> <http://${host}:${port}/index/images/${ID}>.\n"
		ID=$(expr $ID + 1)
	done < ./PhotoSimilarity-Workspace/Graphs/image_uris
	echo -e "{}]}"
} > "./PhotoSimilarity-Workspace/IDs/${now}_pastecIDs.json"
# Save indexes in a file
IDs+="}"
echo -e $IDs > "./PhotoSimilarity-Workspace/IDs/${now}_pastecIDs.ttl"
# Update Pharos
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharosModel "./PhotoSimilarity-Workspace/IDs/${now}_pastecIDs.ttl"
# Create model and update Vision
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $vision_endpoint -visionModel ./PhotoSimilarity-Workspace/IDs/