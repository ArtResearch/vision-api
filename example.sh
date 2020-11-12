#!/usr/bin/sh
#
# Examlple of using options in scripts
#
. ./config.conf
if [ $# -eq 0 ]
then
        echo -e "Missing options!\n"
        exit 0
fi

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
host=${host:-localhost}
port=${port:-4212}
method=${method,,}
echo $query
echo $endpoint
echo $method
echo $host
echo $port