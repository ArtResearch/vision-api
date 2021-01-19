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

echo $method
echo $endpoint
echo $pharos_user
echo $pharos_password

# Update Pharos
# java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -json_file "./PhotoSimilarity-Workspace/IDs/2021-01-19T10-18-26_pastecIDs.json" -pharosModel "./PhotoSimilarity-Workspace/IDs/2021-01-19T10-18-26_pastecIDs.ttl"
# Create model and update Vision
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -p $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -e $vision_endpoint -vision_user $vision_user -vision_password $vision_password -visionModel "./PhotoSimilarity-Workspace/IDs/2021-01-19T10-18-26_pastecIDs.json"
