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

import org.aluminati3555.frc2019.systems.DriveSystem;

/**
 * This auto mode makes a 90 degree turn
 * 
 * @author Caleb Heydon
 */
public class ModeExampleTurn implements AluminatiAutoTask {
    private DriveSystem driveSystem;
    private AluminatiAutoTask task;

    public void start(double timestamp) {
        driveSystem.getGyro().zeroYaw();
        task.start(timestamp);
    }

    public void update(double timestamp) {
        task.update(timestamp);
    }

    public void stop() {
        if (task != null) {
            task.stop();
        }
    }

    public void advanceState() {

    }

    public boolean isComplete() {
        if (task == null) {
            return true;
        }

        return task.isComplete();
    }

    public ModeExampleTurn(DriveSystem driveSystem) {
        this.driveSystem = driveSystem;
        this.task = new ActionTurnToYaw(-90, 2, driveSystem);
    }
}
