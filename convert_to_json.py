# This is the python script that converts raw output in the outputs.txt file to the formatted_detections.json file
import json
import re

with open("outputs.txt", "r") as file:
    lines = file.readlines()

detections = {}
detection_pattern = re.compile(r"\((\d+), ([\d\.]+), \[([\d\., ]+)\]\)")
current_id = None
current_category = None
category_index = 0
category_keys = [0, 120, 240]

for line in lines:
    line = line.strip()
    if not line:
        continue
    if line.startswith("Detections for ID"):
        current_id = line.split()[-1].strip(":")
        detections[current_id] = {0: [], 120: [], 240: []} 
        category_index = 0
        continue
    if "=" in line:
        category_name, status = line.split("=")
        category_name = category_name.strip()
        status = status.strip()
        roadway_status = {category_name.lower(): status == "True"}
        detections[current_id][category_keys[category_index]].append(roadway_status)
        continue

    if line == "-":
        category_index += 1
        continue

    matches = detection_pattern.findall(line)
    if matches:
        for class_id, score, box in matches:
            box_values = list(map(float, box.split(", ")))
            detections[current_id][category_keys[category_index]].append({
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