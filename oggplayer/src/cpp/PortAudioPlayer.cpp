/*
 * PortAudioWriter.cpp
 *
 *  Created on: Mar 20, 2013
 *      Author: imizus
 */

#include <stddef.h>
#include <unistd.h>

#include <stdio.h>

#include "PortAudioPlayer.h"

namespace vam
{
	void PortAudioPlayer::checkError(PaError code)
	{
		if (code != paNoError)
		{
			state = sError;
			throw Error(etPortAudioError, code);
		}
	}

	void PortAudioPlayer::throwError(ErrorType type)
	{
		throw Error(type, 0);
	}

	int PortAudioPlayer::portAudioCallback(const void*                     inputBuffer,
	                                      void*                           outputBuffer,
	                                      unsigned long                   framesPerBuffer,
	                                      const PaStreamCallbackTimeInfo* timeInfo,
	                                      PaStreamCallbackFlags           statusFlags,
	                                      void*                           userData)
	{
		PortAudioPlayer* sender = (PortAudioPlayer*)userData;
		sender->callbackInProgress = true;

		(void) inputBuffer; // Prevent unused argument warning.
		float *out = (float*)outputBuffer;

		for (int i = 0; i < framesPerBuffer; i++)
		{
			const float *buffer = sender->soundSource->readSample();
			for (int chan = 0; chan < sender->soundSource->getChannels(); chan++)
			{
				*out++ = buffer[chan] * 0.9f;
			}
		}
		sender->callbackInProgress = false;
		return 0;
	}

	PortAudioPlayer::PortAudioPlayer(int channels, int rate, int frames_per_buffer) :
			callbackInProgress(false)
	{
		outputParameters.device = Pa_GetDefaultOutputDevice(); // default output device
		if (outputParameters.device == paNoDevice)
		{
			throwError(etNoDevice);
		}

		outputParameters.channelCount = channels;
		outputParameters.sampleFormat = paFloat32;             // 32 bit floating point output
		outputParameters.suggestedLatency = Pa_GetDeviceInfo( outputParameters.device )->defaultLowOutputLatency;
		outputParameters.hostApiSpecificStreamInfo = NULL;

		checkError((PaError)Pa_OpenStream(&stream,
				 NULL,              // No input.
				 &outputParameters, // As above.
				 rate,
				 frames_per_buffer,               // Frames per buffer.
				 paClipOff,         // No out of range samples expected.
				 portAudioCallback,
				 this));

		state = sStopped;
	}

	void PortAudioPlayer::play()
	{
		if (state != sStopped)
		{
			throwError(etInvalidOperationInThisState);
		}

		checkError(Pa_StartStream(stream));

		state = sPlaying;
	}

	void PortAudioPlayer::stop()
	{
		if (state != sPlaying)
		{
			throwError(etInvalidOperationInThisState);
		}

		checkError(Pa_StopStream(stream));
		state = sStopped;
	}

	PortAudioPlayer::~PortAudioPlayer()
	{
		Pa_CloseStream(stream);
	}

	void PortAudioPlayer::setSoundSource(SoundSource& value)
	{
		while (callbackInProgress)
		{
			usleep(1000);	// sleep for 1 msec
		}
		soundSource = &value;
	}


} /* namespace va */
