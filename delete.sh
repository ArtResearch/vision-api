#!/bin/bash
echo
for ID in {1..100}
do
   curl -X DELETE http://localhost:4212/index/images/$ID
done