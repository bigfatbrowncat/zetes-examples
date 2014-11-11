/*
 * MixedSounds.cpp
 *
 *  Created on: Mar 25, 2013
 *      Author: imizus
 */

#include <math.h>

#include "MixedSounds.h"

namespace vam
{
	void MixedSounds::checkSoundPositions()
	{
		for (list<SoundSource*>::iterator iter = sounds.begin(); iter != sounds.end(); iter++)
		{
			if (fabs((*iter)->getPlayhead() - playhead) > 1.0 / (*iter)->getRate())
			{
				(*iter)->rewind(playhead);
			}
		}
	}

	void MixedSounds::fillBuffer()
	{
		buffer_start_time = playhead;
		if (sounds.size() > 0)
		{
			int channels = getChannels();

			// Checking for playheads equality
			checkSoundPositions();

			for (int i = 0; i < buffer_allocated_size; i++)
			{
				for (int ch = 0; ch < channels; ch++)
				{
					buffer[ch][i] = 0;
				}

				for (list<SoundSource*>::iterator iter = sounds.begin(); iter != sounds.end(); iter++)
				{
					const float* iterSample = (*iter)->readSample();
					switch ((*iter)->getChannels())
					{
					case 1:
						for (int ch = 0; ch < channels; ch++)
						{
							buffer[ch][i] += iterSample[0];
						}
						break;
					case 2:
						for (int ch = 0; ch < channels; ch++)
						{
							buffer[ch][i] += iterSample[ch];
						}
						break;
					default:
						throw Error(etUnsupportedChannelsNumber, L"readSample");
					}
				}
			}
			buffer_actual_size = buffer_allocated_size;
		}
		else
		{
			buffer_actual_size = 0;
		}

		cursor_position_in_buffer = 0;
	}

	void MixedSounds::updatePlayhead()
	{
		playhead = buffer_start_time + (double)cursor_position_in_buffer / rate;
	}

	void MixedSounds::updateChannelsAndRate()
	{
		// Updating channels

		int channels_max = 0;
		for (list<SoundSource*>::const_iterator iter = sounds.begin(); iter != sounds.end(); iter++)
		{
			if ((*iter)->getChannels() > channels_max)
			{
				channels_max = (*iter)->getChannels();
			}
		}
		channels = channels_max;

		// Updating rate

		if (sounds.size() == 0)
		{
			rate = 44100;	// By default
		}
		else
		{
			rate = sounds.front()->getRate();
		}
	}

	MixedSounds::MixedSounds(int buffer_size) :
			playhead(0),
			buffer(NULL),
			cursor_position_in_buffer(buffer_size),
			buffer_allocated_size(buffer_size),
			buffer_start_time(0),
			buffer_actual_size(0)
	{
		buffer = new float*[MAX_CHANNELS];
		for (int i = 0; i < MAX_CHANNELS; i++)
		{
			buffer[i] = new float[buffer_allocated_size];
		}

		fillBuffer();
	}



	const float* MixedSounds::readSample()
	{
		double startTime = getStartTime();

		bool playhead_was_negative = playhead < startTime;

		cursor_position_in_buffer ++;
		updatePlayhead();

		if (playhead >= startTime && playhead_was_negative)
		{
			fillBuffer();
		}

		if (buffer_actual_size > 0)
		{

			if (cursor_position_in_buffer >= buffer_allocated_size)
			{
				fillBuffer();
			}

			for (int i = 0; i < channels; i++)
			{
				read_buffer[i] = buffer[i][cursor_position_in_buffer];
			}
		}
		else
		{
			for (int i = 0; i < channels; i++)
			{
				read_buffer[i] = 0.f;
			}
		}

		return read_buffer;
	}

	int MixedSounds::getChannels() const
	{
		return channels;
	}

	void MixedSounds::rewind(double position)
	{
		playhead = position;
		fillBuffer();
	}

	double MixedSounds::getStartTime() const
	{
		double start_min = 0;
		for (list<SoundSource*>::const_iterator iter = sounds.begin(); iter != sounds.end(); iter++)
		{
			if ((*iter)->getStartTime() < start_min)
			{
				start_min = (*iter)->getStartTime();
			}
		}
		return start_min;
	}

	double MixedSounds::getEndTime() const
	{
		double end_max = 0;
		for (list<SoundSource*>::const_iterator iter = sounds.begin(); iter != sounds.end(); iter++)
		{
			if ((*iter)->getEndTime() > end_max)
			{
				end_max = (*iter)->getEndTime();
			}
		}
		return end_max;
	}

	int MixedSounds::getRate() const
	{
		return rate;
	}

	void MixedSounds::addSound(SoundSource& sound)
	{
		if (sound.getChannels() > MAX_CHANNELS)
		{
			throw Error(etUnsupportedChannelsNumber, L"addSound");
		}

		if (sounds.size() > 0 && sound.getRate() != rate)
		{
			throw Error(etInequalRate, L"addSound");
		}

		sounds.push_back(&sound);

		updateChannelsAndRate();
	}

	void MixedSounds::removeSound(SoundSource& sound)
	{
		sounds.remove(&sound);
		updateChannelsAndRate();
	}

	MixedSounds::~MixedSounds()
	{
		for (int i = 0; i < MAX_CHANNELS; i++)
		{
			delete [] buffer[i];
		}
		delete [] buffer;
	}

} /* namespace vam */
