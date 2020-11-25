#!/usr/bin/sh
#
# Examlple of using options in scripts
#

if [ $# -eq 1 ]
then
	source $1
else
	while getopts ":q:e:m:h:p:u:w:r:s:" opt; do
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
			u) 
				pharos_user="$OPTARG" >&2
				;;
			w) 
				pharos_password="$OPTARG" >&2
				;;
			r) 
				vision_user="$OPTARG" >&2
				;;
			s) 
				vision_password="$OPTARG" >&2
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
echo $pharos_user
echo $pharos_password
echo $vision_user
echo $vision_password


# method=${method:-Pastec}

# if [[ "$method" == "Pastec" ]]; then
    # echo "is pastec."
# elif [[ "$method" == "Match" ]]; then
	# echo "is match"
# fi