import json, re, sys
filename = sys.argv[1]
f = open(filename,'r')
x = re.sub('\"search_results\":\s*}','\"search_results\":{\"bounding_rects\":[],\"image_ids\":[],\"scores\":[],\"tags\":[],\"type\":\"SEARCH_RESULTS\"}}',f.read())
data = json.loads(x)
f.close()
with open(filename, 'w') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)
    f.close()
