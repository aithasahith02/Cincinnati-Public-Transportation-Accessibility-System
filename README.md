# CPASS - Cincinnati Public Transportation Accessibility System Survey

## Mission & Goal:
Our mission is to map accessibility features across 3000+ Cincinnatti bus stops. The goal of the this project is to remove the uncertainty surrounding the use of public transporaton for those living with disabilities.

## What is CPASS:
CPASS is an automated tool designed to assist people with disabilities in assessing the accessibility of bus stops. It leverages a special metric called the Accessibility Score, which is computed based on key factors such as:
- Availability of a shelter
- Presence of a bench
- Proper signage
- Pavement conditions
- surrounding infrastructure

## Key technologies employed:
- BusStopCV
- Roboflow
- AWS
- JavaFx

## Developer Setup:
In order to run this app please perform following steps:

### 1. Clone this repository
- Using the command, `gh repo clone aithasahith02/Cincinnati-Public-Transportation-Accessibility-System` clone this repository to your local machine.
- Navigate to the folder and run, `pip install -r requirements.txt`, to install necessary dependencies.

### 2. Security & Authorization
- As this project make use of various API's (Google Maps API & roboflow) make sure to replace the dummy credentials with your valid credentials wherever required.
- Make sure to populate the images in the `images` directory to avoid errors during the execution.

### 3. Additional requirements for the java package(UI/UX & front-end)
- Make sure that the latest version of JDK is installed and maven in properly configured in your system.

### 4. Execution
- Use "python" or "python3" commands to run python scripts (to fetch images, run the model, and format the data).
- In order to run the frontend code, navigate to `src/main/java/com/example/Main.java` and execute the following commands.
`mvn clean`
`mvn package` and
`java "--module-path" "C:\javafx-sdk-21.0.6\lib" "--add-modules" "javafx.controls,javafx.web" "--add-opens" "javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED" "--add-opens" "javafx.graphics/com.sun.javafx.sg.prism.web=ALL-UNNAMED" "-jar" "target\lab-1.0.0.jar"`

## Project Workflow
This project automates the process of fetching, 30,000 street view images from Google Maps API. These images are then used to detect key features of a bus station like  storing them in AWS S3, processing them using an ONNX-based ML model in AWS SageMaker, and passing the results to a Java-based UI for visualization.

Thanks for reading!
