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

package gov.nasa.arc.atc.metrics.imagecreation;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author ahamon
 */
public class MeteringPixelCreator extends AbstractPixelCreator {

    // TODO: as for algo creator, use dedicated configurator for each pixel creator

    private static final Color DEFAULT_COLOR = Color.RED;
    private static final double DEFAULT_CIRCLE_RADIUS = 1.5;

    private MercatorAttributes mercatorAttributes;

    private int minimumTime = 0;
    private int maximumTime = 0;

    @Override
    public double[][] calculate(int minTime, int maxTime) {
        super.calculate(minTime, maxTime);
        minimumTime = minTime;
        maximumTime = maxTime;
        final int width = (int) mercatorAttributes.getMapWidth();
        final int height = (int) mercatorAttributes.getMapHeight();
        double[][] infoMatrix = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                infoMatrix[i][j] = 0.0;
            }
        }
        // hum change API
        // optimize
        synchronized (infoMatrix) {
            getDataModel().getAllPlanes().forEach(plane -> getDataModel().getAllDataUpdates().get(plane.getFullName()).entrySet().stream().filter(entry -> isCandidateUpdate(entry.getKey(), entry.getValue())).forEach(entry -> {
                Point2D screenPosition = mercatorAttributes.getXYPosition(entry.getValue().getPosition().getLatitude(), entry.getValue().getPosition().getLongitude());
                final int x = (int) screenPosition.getX() + getxOffSet();
                final int y = (int) screenPosition.getY() + getyOffSet();
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    infoMatrix[x][y] += 1;
                }
            }));
        }
        return infoMatrix;
    }

    @Override
    public void setProjection(MercatorAttributes attributes) {
        mercatorAttributes = attributes;
    }

    @Override
    public void display(int minTime, int maxTime, Group node) {
        double[][] values = calculate(minTime, maxTime);
        double max = Math.max(1.0, MatrixUtils.calculateMax(values));
        //
        getDataModel().getAllPlanes().forEach(plane -> getDataModel().getAllDataUpdates().get(plane.getFullName()).entrySet().stream().filter(entry -> isCandidateUpdate(entry.getKey(), entry.getValue())).forEach(entry -> {
            Point2D screenPosition = mercatorAttributes.getXYPosition(entry.getValue().getPosition().getLatitude(), entry.getValue().getPosition().getLongitude());
            final double x = screenPosition.getX() ;
            final double y = screenPosition.getY();
                final Circle c = new Circle(x , y , DEFAULT_CIRCLE_RADIUS, DEFAULT_COLOR);
                c.setOpacity(0.9 / max);
                node.getChildren().add(c);
        }));

    }

    private boolean isCandidateUpdate(int time, AfoUpdate update) {
        return time >= minimumTime && time <= maximumTime && update.isMetering() == 1;
    }

}
