#!/bin/bash
echo
for ID in {1000000..1000050}
do
   curl -X DELETE -F filepath=$ID http://vision.artresearch.net:8888/delete
done