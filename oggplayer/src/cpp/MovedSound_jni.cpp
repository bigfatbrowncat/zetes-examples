/*
 * MovedSound_jni.cpp
 *
 *  Created on: Mar 26, 2013
 *      Author: imizus
 */

#include <stddef.h>

// Local includes
#include "MovedSound.h"
#include "JNITools.h"

using namespace vam;

extern "C"
{
	JNIEXPORT jlong JNICALL Java_vam_MovedSound_createNativeInstance(JNIEnv * env, jclass movedSound_class, jint buffer_size)
	{
		MovedSound* nativeInstance = new MovedSound(buffer_size);
		return (jlong)nativeInstance;
	}

	JNIEXPORT void JNICALL Java_vam_MovedSound_destroyNativeInstance(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		if (nativeInstance != NULL)	delete nativeInstance;
	}

	JNIEXPORT jfloatArray JNICALL Java_vam_MovedSound_readSample(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

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

	JNIEXPORT void JNICALL Java_vam_MovedSound_rewind(JNIEnv * env, jobject movedSound_object, jdouble position)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			nativeInstance->rewind(position);
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
		}

	}

	JNIEXPORT jdouble JNICALL Java_vam_MovedSound_getPlayhead(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			return nativeInstance->getPlayhead();
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return 0;
		}
	}

	JNIEXPORT jdouble JNICALL Java_vam_MovedSound_getStartTime(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			return nativeInstance->getStartTime();
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return 0;
		}
	}

	JNIEXPORT jdouble JNICALL Java_vam_MovedSound_getEndTime(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			return nativeInstance->getEndTime();
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return 0;
		}
	}

	JNIEXPORT jint JNICALL Java_vam_MovedSound_getChannels(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			return nativeInstance->getChannels();
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return 0;
		}
	}

	JNIEXPORT jint JNICALL Java_vam_MovedSound_getRate(JNIEnv * env, jobject movedSound_object)
	{
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		try
		{
			return nativeInstance->getRate();
		}
		catch (const SoundSource::Error& err)
		{
			catchSoundSourceErrors(env, err);
			return 0;
		}
	}


	JNIEXPORT void JNICALL Java_vam_MovedSound_setSoundNative(JNIEnv * env, jobject movedSound_object, jobject soundSource_object)
	{
		// Getting the MixedSounds native object
		MovedSound* movedSound_nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		// Getting the SoundSource native object
		SoundSource* soundSource_nativeInstance = getAndCheckNativeInstance<SoundSource>(env, soundSource_object, "SoundSource");

		// Connecting
		movedSound_nativeInstance->setSound(*soundSource_nativeInstance);

	}

	JNIEXPORT void JNICALL Java_vam_MovedSound_setDelay(JNIEnv * env, jobject movedSound_object, jdouble value)
	{
		// Getting the MixedSounds native object
		MovedSound* nativeInstance = getAndCheckNativeInstance<MovedSound>(env, movedSound_object, "MovedSound");

		nativeInstance->setDelay(value);
	}

}
