/*
 * VorbisFileReader.h
 *
 *  Created on: Mar 20, 2013
 *      Author: imizus
 */

#ifndef VORBISFILEREADER_H_
#define VORBISFILEREADER_H_

#include <string>
#include <vector>

#include <vorbis/vorbisfile.h>

#include "SoundSource.h"

using namespace std;

namespace vam
{

	class VorbisFileReader : public SoundSource
	{
	public:
		enum ErrorType
		{
			etCantOpen = 0,
			etNotAnOgg = 1,
			etCantRead = 2,
			etVersionIncorrect = 3,
			etBadHeader = 4,
			etMemoryFault = 5,
			etHole = 6,
			etBadLink = 7,
			etInvalidArgument = 8,
			etStrangeError = 9,
			etCantSeek = 10
		};

		class Error : public SoundSource::Error
		{
			friend class VorbisFileReader;
		private:
			ErrorType type;
			wstring caller;
		protected:
			Error(ErrorType type, wstring caller) :
				type(type),
				caller(caller),
				SoundSource::Error(L"VorbisFileReader::Error", (int)type, caller)
			{

			}
		public:
			ErrorType getType() const { return type; }
			const wstring& getCaller() const { return caller; }
		};

	private:
		OggVorbis_File vf;
		int current_section;

		int cursor_position_in_buffer;
		float** buffer;			// This buffer belongs to the Ogg Vorbis decoder
		int buffer_actual_size;

		FILE* file;
		float* read_buffer;		// This buffer is ours

		int buffer_size_request;

		State state;
		double buffer_start_time;
		double playhead;
		double length;
		int channels;
		int rate;
		int bitsPerSecond;
		string vendor;
		vector<string> comments;

	private:
		void throwVorbisError(int code, wstring caller);
		void throwError(ErrorType type, wstring caller);

		void fillBuffer();
		void updatePlayhead();

	public:
#ifdef __MINGW32__
		VorbisFileReader(wstring file_name, int buffer_size_request);
#else
		VorbisFileReader(string file_name, int buffer_size_request);
#endif
		virtual ~VorbisFileReader();

		virtual const float* readSample();
		int getChannels() const { return channels; }
		void rewind(double position);
		double getPlayhead() const { return playhead; }
		double getStartTime() const { return 0; }
		double getEndTime() const { return length; }
		int getRate() const { return rate; }

		State getState() const { return state; }
		int getBitsPerSecond() const { return bitsPerSecond; }
		const string& getVendor() const { return vendor; }
		const vector<string> getComments() const { return comments; }
	};


} /* namespace va */
#endif /* VORBISFILEREADER_H_ */
