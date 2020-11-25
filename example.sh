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
method=${method,,}
now=$(date +"%Y-%m-%dT%H-%M-%S")

echo $query
echo $endpoint 			# https://pharos.artresearch.net/sparql
echo $pharos_user		# admin
echo $pharos_password	# pharosadmin
echo $vision_endpoint	# https://vision.artresearch.net/sparql
echo $vision_user		# vision
echo $vision_password	# vision
echo $method			# Pastec
echo $host				# http://vision.artresearch.net
echo $port				# 4212

# Construct query evaluation
java -jar target/PhotoSimilarity-0.1-assembly.jar -q $query -e $endpoint -m $method -pharos_user $pharos_user -pharos_password $pharos_password
