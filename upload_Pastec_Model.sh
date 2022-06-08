#!/bin/bash

source authentication.env

# upload models to vision
files="./PhotoSimilarity-Workspace/Model/*/*"
for i in $files
do
		echo "Uploading: $i"
		# echo ${vision_blazegraph}
        curl -u $vision_user:$vision_password -X POST -H 'Content-Type:application/x-trig' --data-binary @$i ${vision_blazegraph}'?keepSourceGraphs=true'
        sleep 0.1
done
