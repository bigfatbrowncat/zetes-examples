/*
 * PortAudioPlayer_jni.cpp
 *
 *  Created on: Mar 22, 2013
 *      Author: imizus
 */

#include <stddef.h>

// Local includes
#include "PortAudioPlayer.h"
#include "JNITools.h"

using namespace vam;

jobject errorType(JNIEnv * env, PortAudioPlayer::ErrorType errorType)
{
	jclass vorbisFileReaderError_class = env->FindClass("vam/PortAudioPlayer$ErrorType");
	jmethodID fromValue_method = env->GetStaticMethodID(vorbisFileReaderError_class, "fromValue", "(I)Lvam/PortAudioPlayer$ErrorType;");

	int value = (int)errorType;

	return env->CallStaticObjectMethod(vorbisFileReaderError_class, fromValue_method, value);
}

void throwJavaPortAudioPlayerError(JNIEnv * env, const PortAudioPlayer::Error& err)
{
	jclass vorbisFileReaderError_class = env->FindClass("vam/PortAudioPlayer$Error");
	jmethodID vorbisFileReaderError_constructor = env->GetMethodID(
			vorbisFileReaderError_class, "<init>", "(Lvam/PortAudioPlayer$ErrorType;I)V");

	jobject errorType_object = errorType(env, err.getType());

	jthrowable error_exception = (jthrowable)env->NewObject(vorbisFileReaderError_class, vorbisFileReaderError_constructor, errorType_object, err.getCode());
	env->Throw(error_exception);
}

extern "C"
{

	JNIEXPORT jlong JNICALL Java_vam_PortAudioPlayer_createNativeInstance(JNIEnv * env, jclass portAudioPlayer_class, jint channels, jint rate, jint frames_per_buffer)
	{
		PortAudioPlayer* nativeInstance = NULL;
		try
		{
			nativeInstance = new PortAudioPlayer(channels, rate, frames_per_buffer);
		}
		catch (const PortAudioPlayer::Error& err)
		{
			throwJavaPortAudioPlayerError(env, err);
		}

		return (jlong)nativeInstance;
	}

	JNIEXPORT void JNICALL Java_vam_PortAudioPlayer_destroyNativeInstance(JNIEnv * env, jobject portAudioPlayer_object)
	{
		jclass portAudioPlayer_class = env->GetObjectClass(portAudioPlayer_object);
		jfieldID nativeInstance_field = env->GetFieldID(portAudioPlayer_class, "nativeInstance", "J");
		PortAudioPlayer* nativeInstance = (PortAudioPlayer*)env->GetLongField(portAudioPlayer_object, nativeInstance_field);

		if (nativeInstance != NULL)	delete nativeInstance;

	}

	JNIEXPORT void JNICALL Java_vam_PortAudioPlayer_setNativeSoundSource(JNIEnv * env, jobject portAudioPlayer_object, jobject soundSource_object)
	{
		// Getting the PortAudio native object
		PortAudioPlayer* portAudioPlayer_nativeInstance = getAndCheckNativeInstance<PortAudioPlayer>(env, portAudioPlayer_object, "PortAudioPlayer");

		// Getting the SoundSource native object
		SoundSource* soundSource_nativeInstance = getAndCheckNativeInstance<SoundSource>(env, soundSource_object, "PortAudioPlayer");

		// Connecting
		portAudioPlayer_nativeInstance->setSoundSource(*soundSource_nativeInstance);
	}

	JNIEXPORT void JNICALL Java_vam_PortAudioPlayer_play(JNIEnv * env, jobject portAudioPlayer_object)
	{
		// Getting the PortAudio native object
		PortAudioPlayer* portAudioPlayer_nativeInstance = getAndCheckNativeInstance<PortAudioPlayer>(env, portAudioPlayer_object, "PortAudioPlayer");

		try
		{
			portAudioPlayer_nativeInstance->play();
		}
		catch (const PortAudioPlayer::Error& err)
		{
			throwJavaPortAudioPlayerError(env, err);
		}

	}

	JNIEXPORT void JNICALL Java_vam_PortAudioPlayer_stop(JNIEnv * env, jobject portAudioPlayer_object)
	{
		// Getting the PortAudio native object
		PortAudioPlayer* nativeInstance = getAndCheckNativeInstance<PortAudioPlayer>(env, portAudioPlayer_object, "PortAudioPlayer");

		try
		{
			nativeInstance->stop();
		}
		catch (const PortAudioPlayer::Error& err)
		{
			throwJavaPortAudioPlayerError(env, err);
		}
	}

}
