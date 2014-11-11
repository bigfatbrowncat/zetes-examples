/*
 * PortAudioWriter.h
 *
 *  Created on: Mar 20, 2013
 *      Author: imizus
 */

#ifndef PORTAUDIOWRITER_H_
#define PORTAUDIOWRITER_H_

#include "SoundSource.h"

#include "PortAudioClass.h"

namespace vam
{
	class PortAudioPlayer : public PortAudioClass
	{
	public:
		enum State
		{
			sStopped	= 0,
			sPlaying	= 1,
			sError		= 2
		};

		enum ErrorType
		{
			etPortAudioError				= 0,
			etNoDevice						= 1,
			etInvalidOperationInThisState	= 2
		};

		class Error : public PortAudioClass::Error
		{
			friend class PortAudioPlayer;

		private:
			ErrorType type;

		protected:
			Error(ErrorType type, int code) : type(type), PortAudioClass::Error(code) {}

		public:
			ErrorType getType() const { return type; }
		};

	private:
		PaStreamParameters outputParameters;
		PaStream *stream;
		SoundSource* soundSource;
		State state;
		volatile bool callbackInProgress;

		virtual void checkError(PaError code);
		static int portAudioCallback(const void*                     inputBuffer,
		                                      void*                           outputBuffer,
		                                      unsigned long                   framesPerBuffer,
		                                      const PaStreamCallbackTimeInfo* timeInfo,
		                                      PaStreamCallbackFlags           statusFlags,
		                                      void*                           userData);

	protected:
		void throwError(ErrorType type);

	public:
		PortAudioPlayer(int channels, int rate, int frames_per_buffer);
		void play();
		void stop();
		virtual ~PortAudioPlayer();

		void setSoundSource(SoundSource& value);
		SoundSource& getSoundSource() const { return *soundSource; }
	};

} /* namespace va */
#endif /* PORTAUDIOWRITER_H_ */
