#!/bin/bash
echo
for ID in {1000000..1002000}
do
   curl -X DELETE http://vision.artresearch.net:4212/index/images/$ID
done