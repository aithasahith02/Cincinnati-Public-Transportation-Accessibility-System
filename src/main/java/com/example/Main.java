package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.Dracula;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

//plan 
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    //TODO: all data related to the screen size and accesibility settings

    //TODO: this will customise the scene based on settings
    //AKA: this is function to update what everything looks like
    //functions to format text and layout sizes
    static void UpdateScreen(){

    }

    //This is the variable dump spot !!!
    public String currentScreen = "Map";
    public int width = 450;
    public int height = 800;

    public String rateToColor(int rate){
        switch (rate) {
            case 3:
                return "/Dark_Green.png";
            case 2:
                return "/Green.png";
            case 1:
                return "/Yellow.png";
            case 0:
                return "/Red.png";
            }
        return "/Dark_Green.png";
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        //All the UI contained in the top Box
        Label logo = new Label(); // this will be the logo for our application
        TextField searchBar = new TextField(); // this will be the text input
        searchBar.setPrefSize(width * 0.6 ,height *0.03);
        searchBar.setMaxSize(width * 0.6 ,height *0.03);
        searchBar.setPromptText("search");

        Image image = new Image("Search.png"); // Replace with your image path
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitHeight(height * 0.025);
        imageView.setFitWidth(height *0.025);

        Button searchButton = new Button(); // press this to search
        searchButton.setGraphic(imageView);
        searchButton.setPrefSize(height * 0.03 ,height * 0.03);

        //extract data from json
        class mapMarker {
            private String color;
            private double latitude;
            private double longitude;
            mapMarker(String color, double latitude, double longitude){
                this.color = color;
                this.latitude = latitude;
                this.longitude = longitude;
            }
            String getColor(){
                return this.color;
            }
            Double latitude(){
                return this.latitude;
            }
            Double longitude(){
                return this.longitude;
            }
        }
        

        //TODO: handle searches when search button is pressed
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String userInput = searchBar.getText();

            }
        });
        

        //create a marker
        Marker marker = new Marker(getClass().getResource("/Dark_Green.png"), -20, -5);
        marker.setPosition(new Coordinate(39.1031, -84.5120));
        marker.setVisible(true);

        //All the UI contained in the main Pane
        // Create a ComboBox for theme selection
        ComboBox<String> themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll("PrimerLight", "PrimerDark", "NordLight", "NordDark", "CupertinoLight", "CupertinoDark", "Dracula");
        themeSelector.setValue("PrimerLight");

        //change theme?
        themeSelector.setOnAction(event -> {
            String selectedTheme = themeSelector.getValue();
            // Apply the selected theme
            switch (selectedTheme) {
                case "PrimerLight":
                    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
                    break;
                case "PrimerDark":
                    Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
                    break;
                case "NordLight":
                    Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
                    break;
                case "NordDark":
                    Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
                    break;
                case "CupertinoLight":
                    Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                    break;
                case "CupertinoDark":
                    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
                    break;
                case "Dracula":
                    Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
                    break;
                }
                
    
        });

        MapView pleaseWORK = new MapView();
        pleaseWORK.setZoom(14);
        pleaseWORK.setCenter(new Coordinate(39.1031, -84.5120)); // Coordinates for New York City
        pleaseWORK.setPrefSize(width,height * 0.85);
        pleaseWORK.setMapType(MapType.OSM);
        pleaseWORK.initialize(Configuration.builder()
            .showZoomControls(false)
            .build());
    
        // Add event handler for MapView initialization
        pleaseWORK.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Add the label to the MapView
                //pleaseWORK.addMarker(marker);
                List<Marker> noGarbage = new ArrayList<>(); //fix for dissapearences
                try {
                    // Load the file as an InputStream
                    InputStream inputStream = test.class.getResourceAsStream("/data.json");
        
                    // Check if the file exists
                    if (inputStream == null) {
                        System.err.println("Error: data.json file not found.");
                        return;
                    }
        
                    // Read the file contents as a string
                    String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
                    // Parse the JSON content
                    JSONArray jsonArray = new JSONArray(jsonContent);
        
                    // Loop through the array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject outerObject = jsonArray.getJSONObject(i);
        
                        // Each outer object has one key (e.g., "1001", "1002", ...)
                        String key = outerObject.keys().next();
                        JSONObject innerObject = outerObject.getJSONObject(key);
                        Marker marker_repeat = new Marker(getClass().getResource(rateToColor(innerObject.getInt("rating"))), -20, -5);
                        marker_repeat.setPosition(new Coordinate(innerObject.getDouble("latitude"),innerObject.getDouble("longitude")));
                        marker_repeat.setVisible(true);
                        noGarbage.add(marker_repeat);
                        pleaseWORK.addMarker(marker_repeat);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace(); // Log the error
                    }

            }
        });

        // Detect clicks on the map
        pleaseWORK.addEventHandler(MapViewEvent.MAP_CLICKED, event ->{
            Coordinate clickLocation = event.getCoordinate();
            clickLocation.getLatitude();
            //39.1031, -84.5120

            if (clickLocation != null && -0.003 < (clickLocation.getLatitude() - 39.1031 ) && (clickLocation.getLatitude() - 39.1031  ) < 0.003 && -0.003 < (clickLocation.getLongitude() - -84.5120 ) && (clickLocation.getLongitude() - -84.5120  ) < 0.003) { // Adjust distance threshold as needed
                System.out.println("Map label clicked: Cincinnati!");
            }
            else{}
        
        });

        pleaseWORK.addEventHandler(MapViewEvent.ANY, event ->{
            try {
                // Load the file as an InputStream
                InputStream inputStream = test.class.getResourceAsStream("/data.json");
    
                // Check if the file exists
                if (inputStream == null) {
                    System.err.println("Error: data.json file not found.");
                    return;
                }
    
                // Read the file contents as a string
                String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    
                // Parse the JSON content
                JSONArray jsonArray = new JSONArray(jsonContent);
    
                // Loop through the array
                List<mapMarker> mapMarkersList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject outerObject = jsonArray.getJSONObject(i);
    
                    // Each outer object has one key (e.g., "1001", "1002", ...)
                    String key = outerObject.keys().next();
                    JSONObject innerObject = outerObject.getJSONObject(key);
                    Marker marker_repeat = new Marker(getClass().getResource(rateToColor(innerObject.getInt("rating"))), -20, -5);
                    marker_repeat.setPosition(new Coordinate(innerObject.getDouble("latitude"),innerObject.getDouble("longitude")));
                    marker_repeat.setVisible(true);
                    pleaseWORK.addMarker(marker_repeat);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace(); // Log the error
                }
        
        });

        //All the UI contained in the bottom Box
        primaryStage.setTitle("Home Screen");
        Button HomeBtn = new Button();
        HomeBtn.setText("Home");
        HomeBtn.setPrefHeight(height * 0.05);
        HomeBtn.setPrefWidth(width * 0.3);

        Button MapBtn = new Button();
        MapBtn.setText("Map");
        MapBtn.setPrefHeight(height * 0.05);
        MapBtn.setPrefWidth(width * 0.3);

        Button SettingsBtn = new Button();
        SettingsBtn.setText("Settings");
        SettingsBtn.setPrefHeight(height * 0.05);
        SettingsBtn.setPrefWidth(width * 0.3);

        //TODO: create custom methods for each button click :)
        HomeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: custom method here
            }
        });

        MapBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                //TODO: custom method here
            }
        });

        //this sets up what the homescreen will look like
        HBox topBox = new HBox(); // this will alternate between a LOGO display and a search bar
        topBox.setPrefHeight(height*0.05);
        topBox.setPadding(new Insets(0, (width * 0.05), 0, (width * 0.05)));
        topBox.setSpacing(width * 0.05);
        topBox.setAlignment(Pos.TOP_LEFT);
        topBox.getChildren().addAll(searchBar, searchButton);

        GridPane mainPane = new GridPane(); // this will be the Map, Mission statement, and Settings Screen
        mainPane.getRowConstraints().add(new RowConstraints(height * 0.55)); // create row (left right), with spcified height
        mainPane.getRowConstraints().add(new RowConstraints(height * 0.33));
        mainPane.add(pleaseWORK,0,0);

        HBox bottomBox = new HBox(); // this will only ever be the three bottom buttoms
        bottomBox.setPadding(new Insets(0, (width * 0.05), 0, (width * 0.05))); //sets distence from edge of UI
        bottomBox.setSpacing(width * 0.05);
        bottomBox.getChildren().addAll(HomeBtn, MapBtn, SettingsBtn);

        VBox root = new VBox(); //VBox that the whole project is in
        root.setPadding(new Insets((height * 0.01), 0, (height * 0.01), 0)); // set up whitespace at top and bottom
        root.getChildren().addAll(topBox, mainPane, bottomBox); //places other UI in VBox
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();

        MapBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                //TODO: custom method here
                mainPane.getChildren().clear();
                mainPane.setAlignment(Pos.CENTER);
                mainPane.add(pleaseWORK,0,0);
            }
        });

        SettingsBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                //TODO: custom method here removes the map from rootpane
                mainPane.getChildren().clear();
                mainPane.setAlignment(Pos.CENTER);
                mainPane.getChildren().addAll(themeSelector);
            }
        });
    }
}

//total verticle space available 100%, top and bottom insets 2%, 98%, buttons 5% leaves 93%, Logo/searchbar 5% leaves 88%, Main Pane 88%, 55% map, 33% markers
//for bottom box whitespace is 5% left right, and inbetween each button, this leaves 60% for buttons or 20% per button