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

method=pastec


# PASTEC METHOD
port=4212

python3 sntxnorm.py "./PhotoSimilarity-Workspace/IDs/2022-02-24T09-31-13_pastecIDs.json"

# Update Pharos
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -e $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -json_file "./PhotoSimilarity-Workspace/IDs/2022-02-24T09-31-13_pastecIDs.json" -pharosModel "./PhotoSimilarity-Workspace/IDs/2022-02-24T09-31-13_pastecIDs.ttl"
# Create model to be uploaded to vision
java -jar target/PhotoSimilarity-0.1-assembly.jar -m $method -p $endpoint -pharos_user $pharos_user -pharos_password $pharos_password -e $vision_endpoint -vision_user $vision_user -vision_password $vision_password -visionModel "./PhotoSimilarity-Workspace/IDs/2022-02-24T09-31-13_pastecIDs.json" -log "./PhotoSimilarity-Workspace/Logs/2022-02-24T09-31-13_pastec_error.log"

# upload models to vision
files="./PhotoSimilarity-Workspace/Model/2022-02-24T09-31-13_pastec/*"
for i in $files
do
		echo "Uploading: $i"
        curl -u $vision_user:$vision_password -X POST -H 'Content-Type:application/x-trig' --data-binary @$i ${vision_blazegraph}'?keepSourceGraphs=true'
        sleep 0.1
done
