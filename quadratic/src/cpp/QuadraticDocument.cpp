#include <math.h>

#include <jni.h>

extern "C"
{
	enum State { unsolved, validRoots, unsolvable, tooManyRoots };

	JNIEXPORT void JNICALL Java_quadratic_QuadraticDocument_solve(JNIEnv* env, jobject obj)
	{
		// Taking class, object and field descriptors
		jfieldID field_a, field_b, field_c, field_x1, field_x2, field_state;

		jclass class_QuadraticDocument = env->GetObjectClass(obj);
		field_a = env->GetFieldID(class_QuadraticDocument, "a", "D");
		field_b = env->GetFieldID(class_QuadraticDocument, "b", "D");
		field_c = env->GetFieldID(class_QuadraticDocument, "c", "D");
		field_x1 = env->GetFieldID(class_QuadraticDocument, "x1", "D");
		field_x2 = env->GetFieldID(class_QuadraticDocument, "x2", "D");
		field_state = env->GetFieldID(class_QuadraticDocument, "state", "Lquadratic/QuadraticDocument$State;");

		// Getting State enum values
		jclass class_State = env->FindClass("quadratic/QuadraticDocument$State");

		// unsolved
		jfieldID field_State_unsolved = env->GetStaticFieldID(class_State , "unsolved", "Lquadratic/QuadraticDocument$State;");
		jobject State_unsolved = env->GetStaticObjectField(class_State, field_State_unsolved);

		// validRoots
		jfieldID field_State_validRoots = env->GetStaticFieldID(class_State , "validRoots", "Lquadratic/QuadraticDocument$State;");
		jobject State_validRoots = env->GetStaticObjectField(class_State, field_State_validRoots);

		// unsolvable
		jfieldID field_State_unsolvable = env->GetStaticFieldID(class_State , "unsolvable", "Lquadratic/QuadraticDocument$State;");
		jobject State_unsolvable = env->GetStaticObjectField(class_State, field_State_unsolvable);

		// tooManyRoots
		jfieldID field_State_tooManyRoots = env->GetStaticFieldID(class_State , "tooManyRoots", "Lquadratic/QuadraticDocument$State;");
		jobject State_tooManyRoots = env->GetStaticObjectField(class_State, field_State_tooManyRoots);

		// Getting field values
		jdouble a = env->GetDoubleField(obj, field_a);
		jdouble b = env->GetDoubleField(obj, field_b);
		jdouble c = env->GetDoubleField(obj, field_c);

		jdouble x1, x2;
		State state;

		if (a == 0.0) {
			if (b != 0.0) {
				x1 = -c / b;
				x2 = -c / b;
				state = validRoots;
			} else {
				if (c == 0) {
					state = tooManyRoots;
				} else {
					state = unsolvable;
				}
			}
		} else if (c == 0.0) {
			x1 = 0.0;
			x2 = -b / a;
			state = validRoots;
		} else {
			double D = b * b - 4 * a * c;
			if (D >= 0.0) {
				x1 = (-b + sqrt(D)) / (4 * a * c);
				x2 = (-b - sqrt(D)) / (4 * a * c);
				state = validRoots;
			} else {
				state = unsolvable;
			}
		}

		// Setting field values
		env->SetDoubleField(obj, field_x1, x1);
		env->SetDoubleField(obj, field_x2, x2);
		switch (state) {
		case unsolved:
			env->SetObjectField(obj, field_state, State_unsolved);
			break;
		case validRoots:
			env->SetObjectField(obj, field_state, State_validRoots);
			break;
		case unsolvable:
			env->SetObjectField(obj, field_state, State_unsolvable);
			break;
		case tooManyRoots:
			env->SetObjectField(obj, field_state, State_tooManyRoots);
			break;
		}
	}
}
