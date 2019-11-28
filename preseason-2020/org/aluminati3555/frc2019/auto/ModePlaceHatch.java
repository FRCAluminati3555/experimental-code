/**
 * Copyright (c) 2019 Team 3555
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.frc2019.auto;

import org.aluminati3555.lib.auto.AluminatiAutoTask;
import org.aluminati3555.lib.vision.AluminatiLimelight;

import org.aluminati3555.frc2019.systems.DriveSystem;
import org.aluminati3555.frc2019.systems.HatchSystem;

/**
 * This auto mode places a hatch at the first visible target
 * 
 * @author Caleb Heydon
 */
public class ModePlaceHatch implements AluminatiAutoTask {
    private AluminatiAutoTask task;

    public void start(double timestamp) {
        task.start(timestamp);
    }

    public void update(double timestamp) {
        task.update(timestamp);
    }

    public void stop() {
        task.stop();
    }

    public void advanceState() {

    }

    public boolean isComplete() {
        return task.isComplete();
    }

    public ModePlaceHatch(DriveSystem driveSystem, HatchSystem hatchSystem, AluminatiLimelight limelight) {
        this.task = new ActionAutoPlaceHatch(driveSystem, hatchSystem, limelight);
    }
}
