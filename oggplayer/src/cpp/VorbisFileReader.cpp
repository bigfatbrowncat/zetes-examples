/*
 * VorbisFileReader.cpp
 *
 *  Created on: Mar 20, 2013
 *      Author: imizus
 */

#include "VorbisFileReader.h"

namespace vam
{

	void VorbisFileReader::throwVorbisError(int code, wstring caller)
	{
		state = sError;
		ErrorType errorType;

		switch (code)
		{
		case OV_HOLE:
			errorType = etHole;
			break;
		case OV_ENOSEEK:
			errorType = etCantSeek;
			break;
		case OV_EREAD:
			errorType = etCantRead;
			break;
		case OV_EBADLINK:
			errorType = etBadLink;
			break;
		case OV_EINVAL:
			errorType = etInvalidArgument;
			break;
		case OV_ENOTVORBIS:
			errorType = etNotAnOgg;
			break;
		case OV_EVERSION:
			errorType = etVersionIncorrect;
			break;
		case OV_EBADHEADER:
			errorType = etBadHeader;
			break;
		case OV_EFAULT:
			errorType = etMemoryFault;
			break;

		default:
			errorType = etStrangeError;
			break;
		}

		throw Error(errorType, caller);
	}

	void VorbisFileReader::throwError(ErrorType type, wstring caller)
	{
		state = sError;
		throw Error(type, caller);
	}

	void VorbisFileReader::fillBuffer()
	{
		buffer_start_time = ov_time_tell(&vf);
		if (buffer_start_time < 0)
		{
			state = sError;
			throwVorbisError((int)buffer_start_time, L"fillBuffer (1)");
		}

		int ret = ov_read_float(&vf, &buffer, buffer_size_request, &current_section);
		if (ret < 0)
		{
			state = sError;
			throwVorbisError(ret, L"fillBuffer (2)");
		}
		else if (ret >= 0)
		{
			cursor_position_in_buffer = 0;
			buffer_actual_size = ret;
		}
	}

	void VorbisFileReader::updatePlayhead()
	{
		playhead = buffer_start_time + (double)cursor_position_in_buffer / rate;
	}


#ifdef __MINGW32__
	VorbisFileReader::VorbisFileReader(wstring file_name, int buffer_size_request) :
		buffer_size_request(buffer_size_request)
#else
	VorbisFileReader::VorbisFileReader(string file_name, int buffer_size_request) :
		buffer_size_request(buffer_size_request)
#endif
	{
#ifdef __MINGW32__
	 	file = _wfopen(file_name.c_str(), L"rb");
#else
	 	file = fopen(file_name.c_str(), "rb");
#endif

		if (file == NULL)
		{
			throwError(etCantOpen, L"constructor (1)");
		}

		// Opening vorbis file

		int ret = ov_open_callbacks(file, &vf, NULL, 0, OV_CALLBACKS_NOCLOSE);
		if (ret < 0)
		{
			throwVorbisError(ret, L"constructor (2)");
		}


		if (ret == 0)
		{

			// Reading the comments
			char **ptr = ov_comment(&vf, -1)->user_comments;
			vorbis_info *vi = ov_info(&vf, -1);
			while (*ptr)
			{
				comments.push_back(string(*ptr));
				++ptr;
			}

			bitsPerSecond = ov_bitrate(&vf, -1);
			channels = vi->channels;
			rate = vi->rate;
			read_buffer = new float[channels];
			vendor = string(ov_comment(&vf,-1)->vendor);
			length = ov_time_total(&vf, -1);
			playhead = 0;

			fillBuffer();

		}

	}

	VorbisFileReader::~VorbisFileReader()
	{
		delete [] read_buffer;
		ov_clear(&vf);
		if (file != NULL) fclose(file);
	}

	const float* VorbisFileReader::readSample()
	{
		bool playhead_was_negative = playhead < 0;

		cursor_position_in_buffer ++;
		updatePlayhead();

		if (playhead >= 0 && playhead_was_negative)													// if we have just stepped into the sound
		{
			fillBuffer();
		}

		if (buffer_actual_size > 0)																	// if the buffer isn't empty
		{
			if (cursor_position_in_buffer >= buffer_actual_size)									// if the buffer has run away
			{
				fillBuffer();
			}

			for (int i = 0; i < channels; i++)														// filling the read buffer with the sound buffer sample at position
			{
				read_buffer[i] = buffer[i][cursor_position_in_buffer];
			}
		}
		else
		{
			for (int i = 0; i < channels; i++)														// filling the read buffer with zeroes
			{
				read_buffer[i] = 0.f;
			}
		}

		return read_buffer;

	}

	void VorbisFileReader::rewind(double position)
	{
		if (position < 0 || position >= length)
		{
			buffer_start_time = position;
			buffer_actual_size = 0;
			cursor_position_in_buffer = 0;
		}
		else
		{
			int ret = ov_time_seek(&vf, position);
			if (ret < 0)
			{
				throwVorbisError(ret, L"rewind");
			}

			if (ret == 0)
			{
				fillBuffer();
			}
		}

		updatePlayhead();
	}


}
