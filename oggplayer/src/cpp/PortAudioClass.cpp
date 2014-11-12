/*
 * PortAudioClass.cpp
 *
 *  Created on: Mar 20, 2013
 *      Author: imizus
 */

#include <portaudio.h>

#include "PortAudioClass.h"

namespace vam
{
	int PortAudioClass::portAudioObjectsCounter = 0;

	PortAudioClass::PortAudioClass()
	{
		if (portAudioObjectsCounter == 0)
		{
			checkError(Pa_Initialize());
		}
		portAudioObjectsCounter ++;
	}

	PortAudioClass::~PortAudioClass()
	{
		portAudioObjectsCounter --;
		if (portAudioObjectsCounter == 0)
		{
			checkError(Pa_Terminate());
		}
	}

	void PortAudioClass::checkError(PaError code)
	{
		if (code != paNoError) throw Error(code);
	}

} /* namespace va */
