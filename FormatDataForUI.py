import json
import re
import pandas as pd

ML_FILE = "formatted_detections.json"
FEATURE_MAP = {"shelter": 1, "bench":2, "sidewalk":3, "signage":4}
BUS_DATA_FILE = "data.txt"
OUTPUT_FILE_NAME = "data.json"

def generate_rating(objectIDs):
    shelterPresent = FEATURE_MAP["shelter"] in objectIDs
    benchPresent = FEATURE_MAP["bench"] in objectIDs
    sidewalkPresent = FEATURE_MAP["sidewalk"] in objectIDs
    signagePresent = FEATURE_MAP["signage"] in objectIDs


    if shelterPresent and benchPresent and sidewalkPresent:
        return 3 # ultra accessability
    elif (shelterPresent or benchPresent) and sidewalkPresent:
        return 2 #high accessability
    elif sidewalkPresent:
        return 1 #medium accessability
    else:
        return 0 #no accessability

if __name__ == "__main__":
    #load ai data
    with open(ML_FILE, "r") as file:
        data = json.load(file)

    #files names
    locations = [busStop for busStop in data.keys()]

    finalDataSet = {}
    #link ai data
    for busStop in locations:
        objectIDs = set()
        activeHeadings = set()
        for heading in ['0', '120', '240']:

            image = data[busStop][heading]

            # sidewalk ai results
            sidewalkFlag = image[0]["sidewalk"]  # flag is a key value pair encoded as a string
            # sidewalkFlag = image[0]  # split on color
            # sidewalkFlag = bool(sidewalkFlag[1])  # only retrieve boolean code

            if len(image) > 1:
                #busStopCV results
                for detection in image[1:]:
                    if detection["score"] > .8:
                        objectIDs.add(detection['class_id'])
                        activeHeadings.add(heading)

        #combined data to single structure
        finalDataSet[busStop] = {"features": [feature for feature in objectIDs]}
        if sidewalkFlag: finalDataSet[busStop]["features"].append(3)
        finalDataSet[busStop]["active headings"] = [heading for heading in activeHeadings]
        finalDataSet[busStop]["rating"] = generate_rating(finalDataSet[busStop]["features"])
        finalDataSet[busStop]["sidewalk"] = sidewalkFlag

    #load bus data
    busStopDF = pd.read_csv(BUS_DATA_FILE)

    #link bus stop data
    for busStop in locations:
        targetRow = busStopDF[busStopDF["stop_code"] == int(busStop)]
        finalDataSet[busStop]["latitude"] = targetRow["stop_lat"].iloc[0]
        finalDataSet[busStop]["longitude"] = targetRow["stop_lon"].iloc[0]
        finalDataSet[busStop]["stop name"] = targetRow["stop_name"].iloc[0]

    with open(OUTPUT_FILE_NAME, "w") as file:
        json.dump(finalDataSet, file, indent=4)



