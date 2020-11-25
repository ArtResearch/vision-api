#!/usr/bin/sh
#
# Examlple of using options in scripts
#

if [ $# -eq 1 ]
then
	source $1
else
	while getopts ":q:e:m:h:p:" opt; do
		case $opt in
			q)
				query="$OPTARG" >&2
				;;
			e)
				endpoint="$OPTARG" >&2
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


# host=${host:-localhost}
# port=${port:-4212}
method=${method,,}
echo $query
echo $endpoint
echo $method
echo $host
echo $port


# method=${method:-Pastec}

# if [[ "$method" == "Pastec" ]]; then
    # echo "is pastec."
# elif [[ "$method" == "Match" ]]; then
	# echo "is match"
# fi