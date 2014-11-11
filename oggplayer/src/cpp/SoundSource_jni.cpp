/*
 * SoundSource_jni.cpp
 *
 *  Created on: Mar 26, 2013
 *      Author: imizus
 */

// System includes
#include <stddef.h>
#include <jni.h>

// Local includes
#include "SoundSource.h"
#include "JNITools.h"

using namespace vam;

extern "C"
{

	JNIEXPORT jobject JNICALL Java_vam_SoundSource_getState(JNIEnv * env, jobject soundSource_object)
	{
		SoundSource* nativeInstance = getAndCheckNativeInstance<SoundSource>(env, soundSource_object, "SoundSource");

		jclass state_class = env->FindClass("vam/SoundSource$State");
		jmethodID fromValue_method = env->GetMethodID(state_class, "fromValue", "(I)Lvam/SoundSource$State;");

		int state = (int)nativeInstance->getState();

		jobject state_object = env->CallStaticObjectMethod(state_class, fromValue_method, state);
		return state_object;
	}

}
