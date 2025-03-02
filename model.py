# This is the python script that takes the onnx model. Postproccess the output and print the results to outputs.txt
# This script also downloads the roboflow model and detects the Sidewalk on the image and outputs to the file.

from ultralytics.utils.checks import check_requirements, check_yaml
from ultralytics.utils import yaml_load
from collections import defaultdict
from roboflow import Roboflow
import onnxruntime as ort
from typing import Tuple
import numpy as np
import torch
import cv2
import os

API_KEY = "lSimhYmnzM262Ys6NIzn"
MODEL_ID = "sidewalk_segment3"
VERSION = 4
IMAGE_PATH = "images/1001-0.jpeg" 

if not os.path.exists(IMAGE_PATH):
    raise FileNotFoundError(f"Error: Image file not found at '{IMAGE_PATH}'")

try:
    rf = Roboflow(api_key=API_KEY)
    project = rf.workspace().project(MODEL_ID)
    robo_model = project.version(VERSION).model
except Exception as e:
    raise RuntimeError(f"Error loading Roboflow model: {e}")

def execute_roboflow(image_path, robo_model, file):
    try:
        result = robo_model.predict(image_path, confidence=20, overlap=10).json()
        if "predictions" not in result or not result["predictions"]:
            raise ValueError("No predictions received from API.")
    except Exception as e:
        raise RuntimeError(f"API request failed: {e}")

    roadway_found = False
    for prediction in result["predictions"]:
        class_name = prediction["class"]
        confidence = prediction["confidence"]
        if class_name == "Sidewalk" and confidence >= 0.75:
            roadway_found = True
            break  
    
    if roadway_found:
        file.write("Sidewalk = True\n")
    else:
        file.write("Sidewalk = False\n")

class YOLOv8:
    def __init__(self, onnx_model, input_dir, confidence_thres, iou_thres):
        self.onnx_model = onnx_model
        self.input_dir = input_dir
        self.confidence_thres = confidence_thres
        self.iou_thres = iou_thres
        self.classes = yaml_load(check_yaml("coco8.yaml"))["names"]
        self.color_palette = np.random.uniform(0, 255, size=(len(self.classes), 3))
        self.session = ort.InferenceSession(self.onnx_model)

    def preprocess(self, image_path):
        self.img = cv2.imread(image_path)
        if self.img is None:
            raise ValueError(f"Image at path {image_path} could not be loaded.")
        self.img_height, self.img_width = self.img.shape[:2]
        img = cv2.cvtColor(self.img, cv2.COLOR_BGR2RGB)
        img = cv2.resize(img, (640, 640))
        image_data = np.array(img) / 255.0
        image_data = np.transpose(image_data, (2, 0, 1)) 
        image_data = np.expand_dims(image_data, axis=0).astype(np.float32)
        return image_data

    def postprocess(self, output):
        outputs = np.squeeze(output[0]).T
        boxes, scores, class_ids = [], [], []
        for i, row in enumerate(outputs):
            classes_scores = row[4:]
            max_score = np.max(classes_scores)
            if max_score >= self.confidence_thres:
                class_id = np.argmax(classes_scores)
                x, y, w, h = row[:4]
                boxes.append([x, y, w, h])
                scores.append(max_score)
                class_ids.append(class_id)
        return boxes, scores, class_ids

    def process_directory(self, file):
        detections_by_id = defaultdict(list)
        for filename in os.listdir(self.input_dir):
            if filename.endswith(".jpeg"):
                image_path = os.path.join(self.input_dir, filename)
                image_data = self.preprocess(image_path)
                outputs = self.session.run(None, {self.session.get_inputs()[0].name: image_data})
                boxes, scores, class_ids = self.postprocess(outputs)
                image_id = filename.split('-')[0]
                detections_by_id[image_id].append((class_ids, scores, boxes))
        for image_id, detections in detections_by_id.items():
            file.write(f"Detections for ID {image_id}:\n")
            for class_ids, scores, boxes in detections:
                execute_roboflow(image_path, robo_model, file)  # Process Roboflow
                file.write(f"{list(zip(class_ids, scores, boxes))}\n")
                file.write('-\n')

if __name__ == "__main__":
    model = "a2.onnx"  # Path to YOLOv8 ONNX model
    input_dir = "images"  # Directory containing images
    conf_thres = 0.5  # Confidence threshold for YOLOv8
    iou_thres = 0.5  # IOU threshold for YOLOv8
    check_requirements("onnxruntime-gpu" if torch.cuda.is_available() else "onnxruntime")

    # Open the output file in write mode
    with open("outputs.txt", "w") as file:
        detection = YOLOv8(model, input_dir, conf_thres, iou_thres)
        detection.process_directory(file)