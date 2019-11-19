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

package org.aluminati3555.aluminativision.pipeline;

import org.aluminati3555.aluminativision.HardwareConfig;
import org.aluminati3555.aluminativision.pipeline.ConfigurablePipeline.PipelineConfig;

/**
 * This class manages the ten vision pipelines
 * 
 * @author Caleb Heydon
 */
public class PipelineManager {
	public static ConfigurablePipeline[] pipelines;

	static {
		pipelines = new ConfigurablePipeline[10];
	}

	/**
	 * Creates all new pipelines
	 */
	public static synchronized void createAllNew() {
		if (HardwareConfig.getConfig().numberOfCameras > 0) {
			for (int i = 0; i < pipelines.length; i++) {
				PipelineConfig config = new PipelineConfig();
				pipelines[i] = new ConfigurablePipeline(config);
			}
		}
	}
}
