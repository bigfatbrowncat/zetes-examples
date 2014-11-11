#ifndef SOUNDSOURCE_H_
#define SOUNDSOURCE_H_

#include <string>
#include <sstream>

using namespace std;

#define MAX_CHANNELS			2

namespace vam
{
	class SoundSource
	{
	public:
		enum State
		{
			sReady = 0,
			sError = 1
		};

		class Error
		{
		private:
			wstring message;

		protected:
			void setMessage(const wstring& value) { message = value; }

		public:
			Error() {}
			Error(const wstring& message)
			{
				this->message = message;
			}
			Error(const wstring& errorClass, int type, const wstring& caller)
			{
				wostringstream oss;
				oss << errorClass << L" occured with type " << type << L" and caller \"" << caller << L"\"";
				setMessage(oss.str());
			}
			virtual ~Error() {}
		};

	protected:
		State state;

	public:
		SoundSource() : state(sReady) {}

		virtual const float* readSample() = 0;
		virtual int getChannels() const = 0;
		virtual void rewind(double position) = 0;
		virtual double getPlayhead() const = 0;
		virtual double getStartTime() const = 0;
		virtual double getEndTime() const = 0;
		virtual int getRate() const = 0;
		virtual State getState() const { return state; }

		virtual ~SoundSource() {}
	};
}

#endif
