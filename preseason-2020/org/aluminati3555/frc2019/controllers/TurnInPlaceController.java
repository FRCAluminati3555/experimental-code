/**
 * Team 176
 */

package org.aluminati3555.frc2019.controllers;

/**
 * This class implements a simple turn-in-place controller that closes on a heading setpoint (in this case, the limelight)
 * There is nothing inherently specific that makes this solely for turning in place - I would use this same class for forward/back movement.
 *
 * This class does assume, however, that motion is symmetrical. That is, the force we need to move in one direction
 * is the same as it will be in the other. An example where this would not be the case is an elevator. Moving up
 * would require fighting gravity, whereas moving downward is not.
 *
 * This class implements a PI (PID loop without derivative component) loop with logic to prevent integral windup and
 * compensate for static friction.
 *
 */
public class TurnInPlaceController {

    //----------------
    // Parameters
    //----------------

    /**
     * The proportional gain
     */
    private double mKp;

    /**
     * The integral gain
     */
    private double mKi;

    /**
     * The coefficient of static friction
     */
    private double mKs;

    /**
     * The amount of error that is considered acceptable
     */
    private double mAllowableError;

    /**
     * The maximum output that should be applied.
     */
    private double mMaxOutput;

    //----------------
    // Variables
    //----------------

    /**
     * The integrator value
     */
    private double mTotalError;

    /**
     * The previous timestamp (for calculating dt)
     */
    private double mPrevTimestamp;


    public TurnInPlaceController(double kP, double kI, double kS, double allowableError, double maxOutput) {
        mKp = kP;
        mKi = kI;
        mKs = kS;
        mAllowableError = allowableError;
        mMaxOutput = maxOutput;
    }

    /**
     * Calculate the output that should be used for the next iteration.
     *
     * @param setpoint where we are trying to go
     * @param processValue the current value
     * @param timestamp the current timestamp
     * @return
     */
    public double update(double setpoint, double processValue, double timestamp) {

        // Compute our current error; the distance that we are off from our setpoint
        double error = (setpoint - processValue);
        // Compute dt
        double dt = timestamp - mPrevTimestamp;

        // We don't really know when we were called last, so we assume that if it was more than 300ms ago, that
        // it should be ignored. This is to prevent the integrator from becoming extremely large on the first iteration.
        // To be clear, this is a hack.
        if(dt > .3) {
            dt = 0;
        }
        mPrevTimestamp = timestamp;

        // Compute our proportional gain
        double p = error * mKp;

        // Prevent the integrator from going wild.
        if(p < mMaxOutput && Math.abs(error) > mAllowableError) {
            // Integrate
            mTotalError += error * dt;
        } else {
            // If we are far away, or too close, reset the integrator to prevent windup
            mTotalError = 0;
        }

        // Compute our integral gain
        double i = mTotalError * mKi;

        // Compute output to account for static friction.
        double s = 0;
        if(Math.abs(error) <= mAllowableError) {
            // We are very close, so we don't output anything
            s = 0;
        } else {
            // We are outside our allowable range. Output ks
            s = Math.signum(error) * mKs;
        }

        // Sum each of the terms together to get our max output
        double output = p + i + s;

        // We clamp output to a max output as it lets us make our parameters more aggressive - most of our time will
        // be spent when error is very small.
        // It's sort of like gain scheduling.. but easier.
        output = Math.min(output, mMaxOutput);

        // Return the final output to the caller
        return output;
    }

}
