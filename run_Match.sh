#!/bin/bash
echo

# setting the authentication variables
source authentication.env

# Script Options
if [ $# -eq 1 ]
then
		# setting up configuration variables
        # source ./config.sh
		source $1
else
	while getopts ":q:e:m:h:p:v:" opt; do
		case $opt in
			q)
				query="$OPTARG" >&2
				;;
			e)
				endpoint="$OPTARG" >&2
				;;
			u) 
				pharos_user="$OPTARG" >&2
				;;
			w) 
				pharos_password="$OPTARG" >&2
				;;
			v)
				vision_endpoint="$OPTARG" >&2
				;;
			r) 
				vision_user="$OPTARG" >&2
				;;
			s) 
				vision_password="$OPTARG" >&2
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
fi
# Optional arguments default configuration
host=${host:-localhost}
method=${method,,}
now=$(date +"%Y-%m-%dT%H-%M-%S")

# echo $query
# echo $endpoint
# echo $pharos_user
# echo $pharos_password
# echo $vision_endpoint
# echo $vision_user
# echo $vision_password
# echo $method
# echo $host
# echo $port

method=match

# Construct query evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $query -p $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -e $vision_endpoint -vision_user $vision_user -vision_password $vision_password -m $method

# MATCH METHOD
port=8888
# Request existing indexes
image_ids=$(curl -X GET $host:$port/count)
image_ids=$(echo $image_ids | sed 's/ //g')
echo -e $image_ids > "./PhotoSimilarity-Workspace/${method}_ids.json"
max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids "./PhotoSimilarity-Workspace/${method}_ids.json" -m $method -e $endpoint -pharos_user $pharos_user -pharos_password $pharos_password)

IDs+="<https://pharos.artresearch.net/resource/graph/visual_similarity/${method}> {\n"
#Match Photo Similarity Evaluation & Indexing
{
	ID=$(expr $max_id + 1)
	echo -e "{\"results\" : ["
	while read line || [ -n "$line" ]; do
		# resize image (based on iiif model)
		url=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_url $line)
		# Search through the images in match with the image url
		search=$(curl -X POST -F url=$url $host:$port/search)
		#[SAME]
		echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": [${search}] },"
		# Add image in Match
		index=$(curl -X POST -F url=$url -F filepath=$ID $host:$port/add)
		# Generate ttl file
		IDs+="\t<${line}> <https://pharos.artresearch.net/resource/vocab/vision/${method}/has_index> \"${ID}\".\n"
		ID=$(expr $ID + 1)
	done < "./PhotoSimilarity-Workspace/Graphs/image_uris${method}"
	echo -e "{}]}"
} > "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json"
# Save indexes in a file
IDs+="}"
# Update Pharos
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -json_file "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json" -pharosModel "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.ttl"
# Create model to be uploaded to vision
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -p $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -e $vision_endpoint -vision_user $vision_user -vision_password $vision_password -visionModel "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json" -log "./PhotoSimilarity-Workspace/Logs/${now}_${method}_error.log"

# upload models to vision
files="./PhotoSimilarity-Workspace/Model/${now}_${method}/*"
for i in $files
do
		echo "Uploading: $i"
        curl -u $vision_user:$vision_password -X POST -H 'Content-Type:application/x-trig' --data-binary @$i ${vision_blazegraph}'?keepSourceGraphs=true'
        sleep 0.1
done
