#!/bin/bash
echo
for ID in {1..130}
do
   curl -X DELETE localhost:4212/index/images/$ID
done