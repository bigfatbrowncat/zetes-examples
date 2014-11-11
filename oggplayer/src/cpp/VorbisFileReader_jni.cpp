// System includes
#include <jni.h>
#include <string>

// Local includes
#include "VorbisFileReader.h"
#include "JNITools.h"

using namespace vam;
using namespace std;

extern "C"
{

	JNIEXPORT jlong JNICALL Java_vam_VorbisFileReader_createNativeInstance(
			JNIEnv * env, jclass vorbisFileReader_class, jstring file_name, jint buffer_size_request)
	{
#ifdef __MINGW32__
		const wchar_t* fileName = (const wchar_t*)env->GetStringChars(file_name, NULL);
#else
		const char* fileName = (char*)env->GetStringUTFChars(file_name, NULL);
#endif

		VorbisFileReader* nativeInstance = NULL;
		try
		{
#ifdef __MINGW32__
			nativeInstance = new VorbisFileReader(wstring(fileName), buffer_size_request);
#else
			nativeInstance = new VorbisFileReader(string(fileName), buffer_size_request);
#endif
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
		}

#ifdef __MINGW32__
		env->ReleaseStringChars(file_name, (const jchar*)fileName);
#else
		env->ReleaseStringUTFChars(file_name, fileName);
#endif
		return (jlong)nativeInstance;
	}

	JNIEXPORT void JNICALL Java_vam_VorbisFileReader_destroyNativeInstance(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		if (nativeInstance != NULL)	delete nativeInstance;
	}

	JNIEXPORT jfloatArray JNICALL Java_vam_VorbisFileReader_readSample(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		try
		{
			const float* sample = nativeInstance->readSample();
			int channels = nativeInstance->getChannels();

			jfloatArray sample_array = env->NewFloatArray(channels);
			env->SetFloatArrayRegion(sample_array, 0, channels, sample);
			return sample_array;
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return NULL;
		}
	}

	JNIEXPORT void JNICALL Java_vam_VorbisFileReader_rewind(JNIEnv * env, jobject vorbisFileReader_object, jdouble position)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		try
		{
			nativeInstance->rewind(position);
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
		}

	}

	JNIEXPORT jdouble JNICALL Java_vam_VorbisFileReader_getPlayhead(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getPlayhead();
	}

	JNIEXPORT jdouble JNICALL Java_vam_VorbisFileReader_getStartTime(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getStartTime();
	}

	JNIEXPORT jdouble JNICALL Java_vam_VorbisFileReader_getEndTime(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getEndTime();
	}

	JNIEXPORT jint JNICALL Java_vam_VorbisFileReader_getChannels(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getChannels();
	}

	JNIEXPORT jint JNICALL Java_vam_VorbisFileReader_getRate(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getRate();
	}

	JNIEXPORT jint JNICALL Java_vam_VorbisFileReader_getBitsPerSecond(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		return nativeInstance->getBitsPerSecond();
	}

	JNIEXPORT jstring JNICALL Java_vam_VorbisFileReader_getVendor(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		string s = nativeInstance->getVendor();
		jstring res = env->NewStringUTF(s.c_str());

		return res;
	}

	JNIEXPORT jobjectArray JNICALL Java_vam_VorbisFileReader_getComments(JNIEnv * env, jobject vorbisFileReader_object)
	{
		VorbisFileReader* nativeInstance = getAndCheckNativeInstance<VorbisFileReader>(env, vorbisFileReader_object, "VorbisFileReader");

		vector<string> ss = nativeInstance->getComments();

		jobjectArray res = env->NewObjectArray(ss.size(), env->FindClass("java/lang/String"), NULL);
		for (int i = 0; i < ss.size(); i++)
		{
			env->SetObjectArrayElement(res, i, env->NewStringUTF(ss[i].c_str()));
		}
		return res;
	}

}
