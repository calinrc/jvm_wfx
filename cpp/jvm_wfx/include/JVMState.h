/*
 *    JVMState.h file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#ifndef JVMSTATE_H_
#define JVMSTATE_H_

#include <jni.h>
#include "gendef.h"

typedef jint (*JNI_CreateJavaVM_func)(JavaVM **, void **, void *);

class JVMState
{
public:

    JVMStateEnum initialize(const char* javaLauncherJar);

    JNIEnv* getEnv(bool* isNewEnv = NULL);

    void releaseEnv();

    bool exceptionExists(JNIEnv* env);

    JVMStateEnum detach();
    static JVMState* instance();

private:
    JVMState();
    virtual ~JVMState();

    static JVMState* s_instance;
    bool m_initialized;
	LIB_HANDLER m_handle;
    JavaVM* m_jvm;
};

#endif /* JVMSTATE_H_ */
