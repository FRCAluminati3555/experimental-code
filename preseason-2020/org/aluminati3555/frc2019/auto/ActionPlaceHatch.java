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

import org.aluminati3555.frc2019.systems.HatchSystem;

/**
 * This autonomous action places a hatch
 * 
 * @author Caleb Heydon
 */
public class ActionPlaceHatch implements AluminatiAutoTask {
    private State state;
    private double time;

    private HatchSystem hatchSystem;

    public void start(double timestamp) {
        time = timestamp;

        // Start by closing the claw and extending
        hatchSystem.clamp();
        hatchSystem.extend();
    }

    public void update(double timestamp) {
        if (state == State.WAITING_FOR_OPERATOR) {
            time = timestamp;
        } else if (state == State.WAITING_FOR_CLAMP) {
            // Clamp hatch and wait half a second for claw to get the hatch
            hatchSystem.release();

            if (timestamp >= time + 0.5) {
                hatchSystem.retract();
                state = State.COMPLETE;
            }
        } else if (state != State.COMPLETE) {
            // If the action is in an undefined state, stop it immediately
            stop();
        }
    }

    public void stop() {
        // Immediately return hatch mechanism to default state
        hatchSystem.clamp();
        hatchSystem.retract();
        state = State.COMPLETE;
    }

    public boolean isComplete() {
        return (state == State.COMPLETE);
    }

    /**
     * Call this when the hatch mechanism should retract
     */
    public void advanceState() {
        state = State.WAITING_FOR_CLAMP;
    }

    public ActionPlaceHatch(HatchSystem hatchSystem) {
        this.hatchSystem = hatchSystem;
        state = State.WAITING_FOR_OPERATOR;
    }

    private enum State {
        WAITING_FOR_OPERATOR, WAITING_FOR_CLAMP, COMPLETE
    }
}
