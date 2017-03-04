/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.arc.atc.metrics.comparison;


import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.metrics.SimulationCalculations;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GraphComparisonDemoApp extends Application {

    private static final File GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());

    private static final String SIM_DUR = "3000";

    private static final File CONFIG_FILE = new File(MainResources.class.getResource("config.properties").getPath());
    private static final File SCENARIO_1_FILE = new File("/Desktop/Comparison/log_conf1_t" + SIM_DUR + ".log");
    private static final File SCENARIO_2_FILE = new File("/Desktop/Comparison/log_conf2_t" + SIM_DUR + ".log");
    private static final File SCENARIO_3_FILE = new File("/Desktop/Comparison/log_conf3-1200_t" + SIM_DUR + ".log");
    private static final File SCENARIO_4_FILE = new File("/Desktop/Comparison/log_conf3-10_t" + SIM_DUR + ".log");

    private static final int SIM_DURATION = 2999;

    private static final double IMAGE_RATIO = 1;

    public static final double IMAGE_WIDTH = 1200;
    public static final double IMAGE_HEIGHT = IMAGE_WIDTH * IMAGE_RATIO;


    public static final double MIN_PERCENTAGE = 0.20;

    private static final boolean WITH_ALL_UPDATES = false;


    private static final double AXIS_LENGTH = 250.0;

    private ThreadedAlternateLogParser threadedAlternateLogParser1;
    private ThreadedAlternateLogParser threadedAlternateLogParser2;
    private ThreadedAlternateLogParser threadedAlternateLogParser3;
    private ThreadedAlternateLogParser threadedAlternateLogParser4;

    private ATCGeography geography;
    private MercatorAttributes mercatorAttributes;

    private SimulationCalculations simCal1;
    private SimulationCalculations simCal2;
    private SimulationCalculations simCal3;
    private SimulationCalculations simCal4;

    private double max;
    private double max1;
    private double max2;
    private double max3;
    private double max4;

    private Color color1;
    private Color color2;
    private Color color3;
    private Color color4;

    private double valueRatio;

    private Scene scene;
    private TabPane tabPane;
    // 2D
    private AnchorPane pane2D;
    private Group group2d;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        tabPane = new TabPane();
        tabPane.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        createTab2D();
        scene = new Scene(tabPane, IMAGE_WIDTH, IMAGE_HEIGHT + 40, Color.BLACK);


        primaryStage.setScene(scene);
        primaryStage.show();
        //
        generate();
    }

    private void createTab2D() {
        pane2D = new AnchorPane();
        pane2D.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        Tab tab2d = new Tab("2D");
        tab2d.setContent(pane2D);
        tab2d.setClosable(false);
        tabPane.getTabs().add(tab2d);
        group2d = new Group();
        pane2D.getChildren().add(group2d);
    }


    ///////


    public void generate() {

        //for 3
//        color1 = Color.RED;
//        color2 = Color.GREEN;
//        color3 = Color.BLUE;

        // for 4
        color1 = Color.YELLOW;
        color2 = Color.DEEPSKYBLUE;
        color3 = Color.PINK;
        color4 = Color.CHARTREUSE;


        System.err.println(" Starting parsing AtcGeography");
        geography = (ATCGeography) XMLMaster.requestParsing(GEOGRAPHY_FILE, new ATCGeographyQueueParser(), this);
        SimulationManager.setATCGeography(geography);

        System.err.println("Starting scenario 1 parsing ... ");
        threadedAlternateLogParser1 = new ThreadedAlternateLogParser(SCENARIO_1_FILE, CONFIG_FILE, this::handleSimu1Parsed);
        threadedAlternateLogParser1.start();
    }

    private void handleSimu1Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 1 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 1 parsed!");
                //
                System.err.println("Starting scenario 2 parsing ... ");
                threadedAlternateLogParser2 = new ThreadedAlternateLogParser(SCENARIO_2_FILE, CONFIG_FILE, this::handleSimu2Parsed);
                threadedAlternateLogParser2.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu2Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 2 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 2 parsed!");
                System.err.println("Starting scenario 3 parsing ... ");
                threadedAlternateLogParser3 = new ThreadedAlternateLogParser(SCENARIO_3_FILE, CONFIG_FILE, this::handleSimu3Parsed);
                threadedAlternateLogParser3.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu3Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 3 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 3 parsed!");
                System.err.println("Starting scenario 4 parsing ... ");
                threadedAlternateLogParser4 = new ThreadedAlternateLogParser(SCENARIO_4_FILE, CONFIG_FILE, this::handleSimu4Parsed);
                threadedAlternateLogParser4.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu4Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 4 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 4 parsed!");
                System.err.println(" PARSING COMPLETED");
                calculatePicture();
                break;
            default:
                break;
        }
    }

    private void calculatePicture() {
        System.err.println("\nCalculating mercator attributes...");
        mercatorAttributes = new MercatorAttributes();
        mercatorAttributes.setMapWidth(IMAGE_WIDTH);
        mercatorAttributes.setMapHeight(IMAGE_HEIGHT);
        //mercatorAttributes.processCoordinates(geography.getWaypoints().stream().map(wpt -> new Coordinates(wpt.getLatitude(), wpt.getLongitude())).collect(Collectors.toList()));
        List<AfoUpdate> updates = new LinkedList<>();
        threadedAlternateLogParser1.getDataModel().getAllDataUpdates().values().forEach(map -> updates.addAll(map.values()));
        if (WITH_ALL_UPDATES) {
            mercatorAttributes.processCoordinates(updates.stream().map(wpt -> new Coordinates(wpt.getPosition().getLatitude(), wpt.getPosition().getLongitude())).collect(Collectors.toList()));
        } else {
            // bounds 1
            List<Coordinates> bounds1 = new LinkedList<>();
            bounds1.add(new Coordinates(38.266, -81.565));
            bounds1.add(new Coordinates(43.627, -82.035));
            bounds1.add(new Coordinates(43.58, -72.422));
            bounds1.add(new Coordinates(38.244, -72.403));
            // bounds 2

            List<Coordinates> bounds2 = new LinkedList<>();
            bounds2.add(new Coordinates(42.292, -78.827));
            bounds2.add(new Coordinates(42.211, -71.176));
            bounds2.add(new Coordinates(38.028, -71.192));
            bounds2.add(new Coordinates(37.997, -78.436));
            // bounds 3
            List<Coordinates> bounds3 = new LinkedList<>();
            bounds3.add(new Coordinates(41.82006394706533, -76.73005666017285));
            bounds3.add(new Coordinates(39.54200210251736, -76.7402068673739));
            bounds3.add(new Coordinates(39.54200210251736, -72.70549950495854));
            bounds3.add(new Coordinates(41.82006394706533, -72.71564971215959));

            mercatorAttributes.processCoordinates(bounds3);
        }

        System.err.println(" -> " + mercatorAttributes);
        System.err.println("... done");
        //

        System.err.println("Creating simulation calculations ...");
        System.err.println(" > 1");
        simCal1 = new SimulationCalculations(threadedAlternateLogParser1.getDataModel());
        System.err.println(" > 1 done");
        System.err.println(" > 2");
        simCal2 = new SimulationCalculations(threadedAlternateLogParser2.getDataModel());
        System.err.println(" > 2 done");
        System.err.println(" > 3");
        simCal3 = new SimulationCalculations(threadedAlternateLogParser3.getDataModel());
        System.err.println(" > 3 done");
        System.err.println(" > 4");
        simCal4 = new SimulationCalculations(threadedAlternateLogParser4.getDataModel());
        System.err.println(" > 4 done");
        //
        Platform.runLater(()->{
            representData2D();
        });


        //Platform.runLater(this::saveImage);
    }

    private void representData2D() {
        javafx.scene.shape.Rectangle background = new Rectangle(IMAGE_WIDTH, IMAGE_HEIGHT);
        group2d.getChildren().add(background);
        //
        //
        //simCal1.getSectorCalculations().values().forEach(sectorCalculation -> sectorCalculation.);


    }


    private void saveImage() {
        File f = new File("/Desktop/Comparison/output_" + System.currentTimeMillis() + ".png");
        WritableImage snapshot = pane2D.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage((int) IMAGE_WIDTH, (int) IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(pane2D.getWidth(), pane2D.getHeight());
            ImageIO.write(image, "png", f);
        } catch (IOException ex) {
            System.err.println("Exception while saving: {0}" + ex);
        }
    }

}
