/*
 *    JVMState.cpp file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#include "JVMState.h"
#include <jni.h>
#include "Logger.h"
#include <stdlib.h>
#include <string.h>
#include "Utilities.h"
#include <assert.h>

JVMState* JVMState::s_instance = new JVMState();

JVMState::JVMState() :
        m_initialized(false), m_handle(NULL), m_jvm(NULL)
{

}

JVMState::~JVMState()
{
}

JVMStateEnum JVMState::initialize(const char* javaLauncherJar)
{

    JVMStateEnum retVal = JVMLoadFail;
    if (!m_initialized)
    {

        LOGGING("Start creating JVM");
        JavaVMInitArgs vm_args; /* JDK/JRE 6 VM initialization arguments */
        JavaVMOption* options = new JavaVMOption[4];

        char classpathParam[MAX_PATH + 100];
        char log4j[MAX_PATH + 100];
        char log4jParam[MAX_PATH + 100];
        char xms[10];
        char xmx[10];

        size_t pathSize = MAX_PATH;

        memset(classpathParam, 0, sizeof(classpathParam));
        memset(log4j, 0, sizeof(log4j));
        memset(log4jParam, 0, sizeof(log4jParam));
        memset(xms, 0, sizeof(xms));
        memset(xmx, 0, sizeof(xmx));

        sprintf(classpathParam, "-Djava.class.path=%s", javaLauncherJar);

        Utilities::getJavaLoggerFileLocation(log4j, &pathSize);
        sprintf(log4jParam, "-Dlog4j.configuration=file://%s", log4j);
        strcpy(xms, "-Xms32M");
        strcpy(xmx, "-Xmx128M");

        LOGGING("Using classpath param:  \"%s\" ", classpathParam);

        LOGGING("Using log4j.configuration param \"%s\"", log4jParam);

        options[0].optionString = classpathParam;
        options[0].extraInfo = 0;

        options[1].optionString = log4jParam;
        options[1].extraInfo = 0;

        options[2].optionString = xms;
        options[2].extraInfo = 0;

        options[3].optionString = xmx;
        options[3].extraInfo = 0;


        vm_args.version = JNI_VERSION_1_6;
        vm_args.nOptions = 4;
        vm_args.options = options;
        vm_args.ignoreUnrecognized = false;

        /* load and initialize a Java VM, return a JNI interface
         * pointer in env */

        char* javaHomeFolder = getenv("JAVA_HOME");
        if (javaHomeFolder != NULL)
        {
            char path[MAX_PATH] = { 0 };

            sprintf(path, MAIN_JVM_PATH, javaHomeFolder);

            bool foundJvm = false;

            struct stat st = { 0 };

            if (stat(path, &st) == -1)
            {
                LOGGING("Unable to find jvm dynamic library in %s", path);
                sprintf(path, ALTERNATIVE_JVM_PATH, javaHomeFolder);
                if (stat(path, &st) == -1)
                {
                    LOGGING("Unable to find jvm dynamic library in %s", path);
                } else
                {
                    foundJvm = true;
                }

            } else
            {
                //found in default Oracle, OpenJDK places
                foundJvm = true;
            }
            if (foundJvm)
            {
                LOGGING("Try loading JVM dynamic library...");
                m_handle = LOAD_LIB(path, RTLD_LAZY);

                if (m_handle != NULL)
                {

                    LOGGING("Try creating JVM");
                    JNI_CreateJavaVM_func JNI_CreateJavaVM_loc;
                    JNI_CreateJavaVM_loc = (JNI_CreateJavaVM_func)LOAD_PROC(m_handle, "JNI_CreateJavaVM");

                    JNIEnv* env;
                    jint jvmCreateState = JNI_CreateJavaVM_loc(&m_jvm, (void**) &env, &vm_args);

                    if (jvmCreateState == JNI_OK)
                    {
                        LOGGING("JVM create OK");
                        m_initialized = true;
                    }

                    LOGGING("End JVM creation");
                    retVal = JVMLoaded;
                } else
                {
#ifdef LINUX
                    LOGGING("JVM loading error %s", dlerror());
#else
					LOGGING("JVM loading error");
#endif
                    assert(false);
                }
            }
        } else
        {
            LOGGING("Unable to find JAVA_HOME variable");
            assert(false);
        }

        delete[] options;

    } else
    {
        retVal = JVMLoaded;
    }
    return retVal;

}

JNIEnv* JVMState::getEnv(bool* isNewEnv)
{

    JNIEnv* env = NULL;
    if (isNewEnv != NULL)
    {
        *isNewEnv = false;
    }

    if (m_jvm != NULL)
    {
        jint getEnvResult = m_jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
        switch (getEnvResult)
        {
            case JNI_OK:
                //do nothing but return env obj
                break;
            case JNI_EDETACHED:
                m_jvm->AttachCurrentThread((void **) &env, NULL);
                LOGGING("Create a new JNIEnv for a new thread. Make sure we are detaching it when not used anymore")
                if (isNewEnv != NULL)
                {
                    *isNewEnv = true;
                }
                break;
            case JNI_EVERSION:
                LOGGING("JNI version 1.6 not supported by current JVM")
                break;
            default:
                LOGGING("Fail on getting JNIEnv element for current thread. Fail with error %d", getEnvResult)
        }
    }

    return env;
}

void JVMState::releaseEnv()
{
    LOGGING("Detach current thread")
    m_jvm->DetachCurrentThread();
}

JVMStateEnum JVMState::detach()
{
    JVMStateEnum retVal = JVMDetached;
    if (m_jvm != NULL)
    {
        m_jvm->DestroyJavaVM();
        m_jvm = NULL;
    }
    if (m_handle != NULL)
    {
        FREE_LIB(m_handle);
        m_handle = NULL;
    }
    m_initialized = false;
    return retVal;
}

bool JVMState::exceptionExists(JNIEnv* env)
{
    jboolean exceptionExists = env->ExceptionCheck();
    if (exceptionExists)
    {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    return exceptionExists == JNI_TRUE;
}

JVMState* JVMState::instance()
{
    return s_instance;
}
