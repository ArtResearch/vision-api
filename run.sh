#!/bin/bash
echo
# Script Options
if [ $# -eq 1 ]
then
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
# echo $endpoint 			# https://pharos.artresearch.net/sparql
# echo $pharos_user		# admin
# echo $pharos_password	# pharosadmin
# echo $vision_endpoint	# https://vision.artresearch.net/sparql
# echo $vision_user		# vision
# echo $vision_password	# vision
# echo $method			# Pastec
# echo $host				# http://vision.artresearch.net
# echo $port				# 4212

# Construct query evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $query -e $endpoint -m $method -pharos_user $pharos_user -pharos_password $pharos_password

if [[ "$method" == "pastec" ]]; then
	# PASTEC METHOD
	# Request existing indexes
	image_ids=$(curl -X GET $host:$port/index/imageIds)
	max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids $image_ids -m $method)

	IDs+="<https://pharos.artresearch.net/resource/graph/visual_similarity/${method}> {\n"
	#Pastec Photo Similarity Evaluation & Indexing
	{
		ID=$(expr $max_id + 1)
		echo -e "{\"results\" : ["
		while read line || [ -n "$line" ]; do
			# resize image (based on iiif model)
			url=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_url $line)
			# Search through the images in pastec with the image url
			search=$(curl -X POST -d '{"url":"'$url'"}' $host:$port/index/searcher)
			echo -e "{\"image_id\": ${ID},\n\"image_url\": \"${line}\",\n\"search_results\": ${search} },"
			# Add image in Pastec (POST original)
			index=$(curl -X POST -d '{"url":"'$url'"}' $host:$port/index/images/$ID)
			# Generate ttl file
			IDs+="\t<${line}> <https://pharos.artresearch.net/resource/vocab/vision/${method}/has_index> <https://vision.artresearch.net:${port}/index/images/${ID}>.\n"
			if [[ $(expr $ID % 1000) -eq 0 ]]; then
				write_index=$(curl -X POST -d '{"type":"WRITE", "index_path":"/pastec/build/pastec-index/pharos.dat"}' ${host}:${port}/index/io)
			fi
			ID=$(expr $ID + 1)
		done < ./PhotoSimilarity-Workspace/Graphs/image_uris
		echo -e "{}]}"
	} > "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json"
	# Save indexes in a file
	IDs+="}"
	write_index=$(curl -X POST -d '{"type":"WRITE", "index_path":"/pastec/build/pastec-index/pharos.dat"}' ${host}:${port}/index/io)
elif [[ "$method" == "match" ]]; then
	# MATCH METHOD
	# Request existing indexes
	image_ids=$(curl -X GET $host:$port/list)
	image_ids=$(echo $image_ids | sed 's/ //g')
	max_id=$(java -jar target/PhotoSimilarity-0.1-assembly.jar -image_ids $image_ids -m $method)
	
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
		done < ./PhotoSimilarity-Workspace/Graphs/image_uris
		echo -e "{}]}"
	} > "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json"
	# Save indexes in a file
	IDs+="}"
fi
echo -e $IDs > "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.ttl"
# Update Pharos
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -pharosModel "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.ttl"
# Create model and update Vision
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -p $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -e $vision_endpoint -vision_user $vision_user -vision_password $vision_password -visionModel "./PhotoSimilarity-Workspace/IDs/${now}_${method}IDs.json"
