#!/bin/bash
echo
for ID in {1000000..1000050}
do
   curl -X DELETE http://vision.artresearch.net:4212/index/images/$ID
done