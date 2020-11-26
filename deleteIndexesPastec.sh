#!/bin/bash
echo
for ID in {1..100}
do
   curl -X DELETE http://vision.artresearch.net:4212/index/images/$ID
done