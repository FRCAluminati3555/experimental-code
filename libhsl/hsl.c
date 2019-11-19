/*
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

#include <stdint.h>
#include <math.h>
#include <stdio.h>

#include "org_aluminati3555_aluminativision_hsl_HSL.h"

/*
 See this link for fast min and max https://www.geeksforgeeks.org/compute-the-minimum-or-maximum-max-of-two-integers-without-branching/
*/

int max(int a, int b)
{
	return a ^ ((a ^ b) & -(a < b));
}

int min(int a, int b)
{
	return b ^ ((a ^ b) & -(a < b));
}

void rgb_to_hsl(float r, float g, float b, float* h, float* s, float* l)
{
	float max_value = ((float) max(r, max(g, b))) / 255;
	float min_value = ((float) min(r, min(g, b))) / 255;

	r /= 255;
	g /= 255;
	b /= 255;

	*l = (max_value + min_value) / 2;

	if (max_value == min_value) {
		*h = 0;
		*s = 0;
	}
	else
	{
		float d = max_value - min_value;
		*s = *l > 0.5 ? d / (2 - d) : d / (max_value + min_value);

		if (r == max_value)
		{
			*h = (g - b) / d + (g < b ? 6: 0);
		}
		else if (g == max_value)
		{
			*h = (b - r) / d + 2;
		}
		else if (b == max_value)
		{
			*h = (r - g) / d + 4;
		}
		else
		{
			*h = 0;
		}

		*h /= 6;
	}

	*h *= 255;
	*s *= 255;
	*l *= 255;
}

JNIEXPORT void JNICALL Java_org_aluminati3555_aluminativision_hsl_HSL_nativeRGBToHLS(JNIEnv* env, jobject obj, jlong address, jint width, jint height)
{
	long data_address = (long) address;
	uint8_t* data = (uint8_t*) data_address;

	int frame_width = (int) width;
	int frame_height = (int) height;
	int pixels = width * height;
	int length = pixels * 3;

	int r;
	int g;
	int b;

	float h;
	float s;
	float l;

	for (int i = 0; i < length; i += 3)
	{
		r = data[i];
		g = data[i + 1];
		b = data[i + 2];
		rgb_to_hsl(r, g, b, &h, &s, &l);

		data[i] = (uint8_t) roundf(h);
		data[i + 1] = (uint8_t) roundf(l);
		data[i + 2] = (uint8_t) roundf(s);
	}
}
