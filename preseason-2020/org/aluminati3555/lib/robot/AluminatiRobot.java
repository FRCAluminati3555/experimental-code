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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.lib.robot;

import org.aluminati3555.lib.data.AluminatiData;

import edu.wpi.first.wpilibj.TimedRobot;

/**
 * This is team 3555's robot base class
 * 
 * @author Caleb Heydon
 */
public class AluminatiRobot extends TimedRobot {
    /**
     * Returns a useful string
     */
    @Override
    public String toString() {
        return "[AluminatiRobot] libraryVersion: " + AluminatiData.LIBRARY_VERSION + ", delay: " + this.getPeriod();
    }

    /**
     * Override this method to print
     */
    @Override
    public void startCompetition() {
        // Print when robot is initialized
        System.out.println(this.toString());

        super.startCompetition();
    }

    /**
     * This constructor allows the delay to be changed by modifying the
     * AluminatiData.robotDelay value before calling RobotBase.startRobot()
     */
    public AluminatiRobot() {
        super(AluminatiData.robotDelay);
    }
}
