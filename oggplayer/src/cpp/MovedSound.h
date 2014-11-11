/*
 * MovedSound.h
 *
 *  Created on: Mar 26, 2013
 *      Author: imizus
 */

#ifndef MOVEDSOUND_H_
#define MOVEDSOUND_H_

#include "SoundSource.h"

namespace vam
{

	class MovedSound: public SoundSource
	{
	public:
		enum ErrorType
		{
			etSoundNotSet			= 0
		};

		class Error
		{
			friend class MixedSounds;
		private:
			ErrorType type;
			wstring caller;
		protected:
			Error(ErrorType type, wstring caller) : type(type), caller(caller)
			{
			}
		public:
			ErrorType getType() const { return type; }
			wstring getCaller() const { return caller; }
		};

	private:
		float** buffer;						// This buffer is ours. It's a second buffer for fast process
		double buffer_start_time;
		int cursor_position_in_buffer;
		int buffer_allocated_size;
		int buffer_actual_size;

		float read_buffer[MAX_CHANNELS];

		SoundSource* sound;
		double delay;
		double playhead;

		void checkSoundPosition();

		void fillBuffer();
		void updatePlayhead();

	public:
		MovedSound(int buffer_size);
		virtual ~MovedSound();

		virtual const float* readSample();
		virtual int getChannels() const;
		virtual void rewind(double position);
		virtual double getPlayhead() const;
		virtual double getStartTime() const;
		virtual double getEndTime() const;
		virtual int getRate() const;

		void setSound(SoundSource& sound);
		void setDelay(double value);
	};

} /* namespace vam */
#endif /* MOVEDSOUND_H_ */
