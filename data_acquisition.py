import os
import urllib.request
import pandas as pd

API_KEY = "AIzaSyDB5a_xxxxxxxxxxxx"
SAVE_PATH = "C:\\Users\\sevmy\\OneDrive\\Documents\\RevolutionUC\\Vision AI Learning\\Archive"

if __name__ == "__main__":
    busStopsDF = pd.read_csv("data.txt") #get bus stop data

    #api call components
    base = "https://maps.googleapis.com/maps/api/streetview"
    size = "size=640x640"
    fov = "fov=120"
    returnErrorCode = "return_error_code=true"
    source = "source=outdoor"
    key = f"key={API_KEY}"

    nRows = busStopsDF.shape[0]
    for i in range(nRows):
        #location extraction
        lat = busStopsDF.iloc[i]["stop_lat"]
        long = busStopsDF.iloc[i]["stop_lon"]
        stopId = busStopsDF.iloc[i]["stop_code"]
        location = f"location={lat},{long}"

        for degree in {0, 120, 240}:
            heading = f"heading={degree}"
            request_url = f"{base}?{size}&{location}&{fov}&{heading}&{source}&{key}&returnErrorCode" #api call construction
            fileName = f"{stopId}-{degree}.jpeg"

            urllib.request.urlretrieve(request_url, os.path.join(SAVE_PATH, fileName))
            print("Stop:", i, "\t", fileName, "written!")



