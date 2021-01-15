#!/bin/bash
echo

max_id=100000

ID=$(expr $max_id + 1)


while [ $ID -lt 100100 ]
do
	if [[ $(expr $ID % 10) -eq 0 ]]; then
		echo $ID
	fi
   
   ID=$(expr $ID + 1)
done