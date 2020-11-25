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
# Optional arguments default configuration for match testing
host=${host:-http://vision.artresearch.net}
port=${port:-8888}
method=${method:-Match}
method=${method,,}
endpoint=${endpoint:-http://localhost:9999/blazegraph/sparql}
vision_endpoint=${vision_endpoint:-https://vision.artresearch.net/sparql}

echo $host
echo $port
echo $method
echo $endpoint
echo $vision_endpoint

now=$(date +"%Y-%m-%dT%H-%M-%S")
# Construct query evaluation [SAME for both methods]
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $query -e $endpoint -m $method

# Request existing indexes
image_ids=$(curl -X GET $host:$port/list)
image_ids=$(echo $image_ids | sed 's/ //g')
#[SAME ON BOTH]
max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids $image_ids -m $method)

# [PARTS OF IT WILL CHANGE]
IDs+="<https://pharos.artresearch.net/resource/custom/visual_similarity> {\n"
#Pastec Photo Similarity Evaluation & Indexing
{
	ID=$(expr $max_id + 1)
	echo -e "{\"results\" : ["
	while read line || [ -n "$line" ]; do
	
	
		#[CHANGED]
		# Search through the images in pastec with the image url
		search=$(curl -X POST -F url=$line $host:$port/search)
		#[SAME]
		echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": [${search}] },"
		# Add image in Pastec [CHANGED]
		index=$(curl -X POST -F url=$line -F filepath=$ID $host:$port/add)
		# Generate ttl file [CHANGED]
		IDs+="\t<${line}> <https://pharos.artresearch.net/custom/${method}/has_index> <${host}:${port}/${method}/index/images/${ID}>.\n"
		ID=$(expr $ID + 1)
	done < ./PhotoSimilarity-Workspace/Graphs/image_uris
	echo -e "{}]}"
} > "./PhotoSimilarity-Workspace/IDs/${now}_matchIDs.json"
# Save indexes in a file
IDs+="}"
echo -e $IDs > "./PhotoSimilarity-Workspace/IDs/${now}_matchIDs.ttl"

# Update Pharos
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharosModel "./PhotoSimilarity-Workspace/IDs/${now}_matchIDs.ttl"








