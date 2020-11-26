#!/bin/bash
echo
for ID in {1..100}
do
   curl -X DELETE -F filepath=$ID http://vision.artresearch.net:8888/delete
done