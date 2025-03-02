# This is the python script that converts raw output in the outputs.txt file to the formatted_detections.json file
import json
import re

with open("outputs.txt", "r") as file:
    lines = file.readlines()

detections = {"1001": {0: [], 120: [], 240: []}}
detection_pattern = re.compile(r"\((\d+), ([\d\.]+), \[([\d\., ]+)\]\)")
category_keys = [0, 120, 240]
current_category = 0

for line in lines:
    line = line.strip()
    if not line or line.startswith("Detections for ID"):
        continue
    if line == "-":
        current_category += 1
        continue
    if line.startswith("Roadway"):
        roadway_status = "roadway:true" if "True" in line else "roadway:false"
        detections["1001"][category_keys[current_category]].append(roadway_status)
    matches = detection_pattern.findall(line)
    if matches:
        for class_id, score, box in matches:
            box_values = list(map(float, box.split(", ")))
            detections["1001"][category_keys[current_category]].append({
                "class_id": int(class_id),
                "score": round(float(score), 6),
                "box": box_values
            })
json_output = json.dumps(detections, indent=4)
print(json_output)
try:
    with open("formatted_detections.json", "w") as json_file:
        json_file.write(json_output)
    print("Data has been successfully saved to formatted_detections.json")
except Exception as e:
    print(f"Error saving data to JSON file: {e}")