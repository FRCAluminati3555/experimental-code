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

package org.aluminati3555.aluminativision.net;

/**
 * This class starts network tables
 * 
 * @author Caleb Heydon
 */
public class NTStarter extends Thread {
	@Override
	public void run() {
//		NetworkTableInstance nt = NetworkTableInstance.getDefault();
//
//		for (int i = 0; !nt.isConnected(); i++) {
//			if (i % 2 == 0) {
//				nt.startClientTeam(ServerConfig.getConfig().teamNumber);
//			} else {
//				nt.startClient(ServerConfig.getConfig().robotIP);
//			}
//
//			VisionUtil.sleep(5);
//		}
//
//		// If unable to connect in 5 seconds try a configured ip
//		double startTime = VisionUtil.getTime();
//		while (!nt.isConnected()) {
//			if ((VisionUtil.getTime() - startTime) >= 5) {
//				System.out.println("Unable to find the robot using the team number");
//				break;
//			}
//		}
	}
}
