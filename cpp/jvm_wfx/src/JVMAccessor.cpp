/*
 *    JVMAccessor.cpp file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#include <JVMAccessor.h>
#include "gendef.h"
#include "JVMState.h"
#include "Utilities.h"
#include "Logger.h"
#include <assert.h>
#include <string>
#include <string.h>
#include <set>
#include "ProgressInfo.h"

JVMAccessor* JVMAccessor::s_instance = new JVMAccessor();
jmethodID JVMAccessor::s_WfxPairMetIdInitFS = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdGetFolderContent = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdGetFileInfo = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdMkDir = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdDelPath = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdRenPath = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdCopyPath = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdGetPath = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdPutPath = NULL;
jmethodID JVMAccessor::s_WfxPairMetIdfileExists = NULL;

jmethodID JVMAccessor::s_NativeProgressConstructor = NULL;

jmethodID JVMAccessor::s_FileInfoGetFileAttributes = NULL;
jmethodID JVMAccessor::s_FileInfoGetFileCreationTime = NULL;
jmethodID JVMAccessor::s_FileInfoGetFileLastAccessTime = NULL;
jmethodID JVMAccessor::s_FileInfoGetFileSize = NULL;
jmethodID JVMAccessor::s_FileInfoGetReserved0 = NULL;

JVMAccessor::JVMAccessor() :
        m_wfxPairObj(NULL), m_nativeProgressClass(NULL), m_initialized(false)
{

}

JVMAccessor::~JVMAccessor()
{
    release();
}

int JVMAccessor::initialize()
{
    if (!m_initialized)
    {
        char dependenciesPath[MAX_PATH];
        size_t pathSize = MAX_PATH;
        memset(dependenciesPath, 0, sizeof(dependenciesPath));
        bool isNewEnv = false;
        JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
        Utilities::getJavaDependenciesPath(dependenciesPath, &pathSize);

        jclass wfxLauncherClass = env->FindClass("org/cgc/wfx/FSClientLauncher");

        if (JVMState::instance()->exceptionExists(env) || wfxLauncherClass == NULL)
        {
            LOGGING("Unable to find Java launcher jar %s", JAVA_LAUNCHER_VAL)
            assert(false);
        }

        jstring depsPathStr = env->NewStringUTF(dependenciesPath);
        jmethodID getPairInstanceMethodId = env->GetStaticMethodID(wfxLauncherClass, "getPairInstance",
                                                                   "(Ljava/lang/String;)Lorg/cgc/wfx/WfxPair;");
        assert(getPairInstanceMethodId != NULL);

        jobject wfxPairObj = env->CallStaticObjectMethod(wfxLauncherClass, getPairInstanceMethodId, depsPathStr);

        if (!JVMState::instance()->exceptionExists(env) && wfxPairObj != NULL)
        {
            jclass wfxPairClass = env->GetObjectClass(wfxPairObj);

            s_WfxPairMetIdInitFS = env->GetMethodID(wfxPairClass, "initFS", "()V");
            assert(s_WfxPairMetIdInitFS != NULL);

            s_WfxPairMetIdGetFolderContent = env->GetMethodID(wfxPairClass, "getFolderContent", "(Ljava/lang/String;)[Ljava/lang/String;");
            assert(s_WfxPairMetIdGetFolderContent != NULL);

            s_WfxPairMetIdGetFileInfo = env->GetMethodID(wfxPairClass, "getFileInformation",
                                                         "(Ljava/lang/String;Ljava/lang/String;)Lorg/cgc/wfx/FileInformation;");
            assert(s_WfxPairMetIdGetFileInfo != NULL);

            s_WfxPairMetIdMkDir = env->GetMethodID(wfxPairClass, "mkDir", "(Ljava/lang/String;)Z");
            assert(s_WfxPairMetIdMkDir != NULL);

            s_WfxPairMetIdDelPath = env->GetMethodID(wfxPairClass, "deletePath", "(Ljava/lang/String;)Z");
            assert(s_WfxPairMetIdDelPath != NULL);

            s_WfxPairMetIdRenPath = env->GetMethodID(wfxPairClass, "renamePath", "(Ljava/lang/String;Ljava/lang/String;)Z");
            assert(s_WfxPairMetIdRenPath != NULL);

            s_WfxPairMetIdCopyPath = env->GetMethodID(wfxPairClass, "copyPath", "(Ljava/lang/String;Ljava/lang/String;)Z");
            assert(s_WfxPairMetIdCopyPath != NULL);

            s_WfxPairMetIdGetPath = env->GetMethodID(wfxPairClass, "getFile",
                                                     "(Ljava/lang/String;Ljava/lang/String;Lorg/cgc/wfx/Progress;)V");
            assert(s_WfxPairMetIdGetPath != NULL);

            s_WfxPairMetIdPutPath = env->GetMethodID(wfxPairClass, "putFile",
                                                     "(Ljava/lang/String;Ljava/lang/String;ZLorg/cgc/wfx/Progress;)V");

            assert(s_WfxPairMetIdPutPath != NULL);

            s_WfxPairMetIdfileExists = env->GetMethodID(wfxPairClass, "fileExists", "(Ljava/lang/String;)Z");
            assert(s_WfxPairMetIdfileExists != NULL);

            initFileEnumerator(env);

            initProgressInfo(env);

            if (!JVMState::instance()->exceptionExists(env))
            {
                m_wfxPairObj = env->NewGlobalRef(wfxPairObj);
                env->CallVoidMethod(m_wfxPairObj, s_WfxPairMetIdInitFS);
                if (JVMState::instance()->exceptionExists(env))
                {
                    assert(false);
                }
            } else
            {
                LOGGING("Fail on obtaining WfxPair instance. Please check the existence of jvm_wfx.jar inside of ~/.config/jvm_wfx/java/deps/ folder")
                assert(false);
            }
            env->DeleteLocalRef(depsPathStr);
            env->DeleteLocalRef(wfxPairObj);
            env->DeleteLocalRef(wfxPairClass);
            env->DeleteLocalRef(wfxLauncherClass);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            m_initialized = true;
            return 0;
        } else
        {
            LOGGING("Unable to find Java launcher jar %s and its WfxPair class", JAVA_LAUNCHER_VAL)
            assert(false);
        }
        if (isNewEnv)
        {
            JVMState::instance()->releaseEnv();
        }
        return -1;
    } else
    {
        return 0;
    }
}

void JVMAccessor::initFileEnumerator(JNIEnv* env)
{
    jclass wfxFileInformationClass = env->FindClass("org/cgc/wfx/FileInformation");
    if (!JVMState::instance()->exceptionExists(env))
    {
        s_FileInfoGetFileAttributes = env->GetMethodID(wfxFileInformationClass, "getFileAttributes", "()J");
        assert(s_FileInfoGetFileAttributes != NULL);
        s_FileInfoGetFileCreationTime = env->GetMethodID(wfxFileInformationClass, "getFileCreationTime", "()J");
        assert(s_FileInfoGetFileCreationTime != NULL);
        s_FileInfoGetFileLastAccessTime = env->GetMethodID(wfxFileInformationClass, "getFileLastAccessTime", "()J");
        assert(s_FileInfoGetFileLastAccessTime != NULL);
        s_FileInfoGetFileSize = env->GetMethodID(wfxFileInformationClass, "getFileSize", "()J");
        assert(s_FileInfoGetFileSize != NULL);
        s_FileInfoGetReserved0 = env->GetMethodID(wfxFileInformationClass, "getReserved0", "()J");
        assert(s_FileInfoGetReserved0 != NULL);
        env->DeleteLocalRef(wfxFileInformationClass);
    } else
    {
        assert(false);
    }
}

void JVMAccessor::initProgressInfo(JNIEnv* env)
{
    jclass wfxProgressInfoClass = env->FindClass("org/cgc/wfx/NativeProgress");
    if (!JVMState::instance()->exceptionExists(env))
    {
        m_nativeProgressClass = static_cast<jclass>(env->NewGlobalRef(wfxProgressInfoClass));
        s_NativeProgressConstructor = env->GetMethodID(wfxProgressInfoClass, "<init>", "(J)V");
        assert(s_NativeProgressConstructor != NULL);
    } else
    {
        assert(false);
    }

}

void JVMAccessor::release()
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        env->DeleteGlobalRef(m_wfxPairObj);
        m_wfxPairObj = NULL;
    }
    if (m_nativeProgressClass != NULL)
    {
        env->DeleteGlobalRef(m_nativeProgressClass);
        m_nativeProgressClass = NULL;
    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    m_initialized = false;
}

FileEnumerator* JVMAccessor::getFolderContent(char* path)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring pathStr = env->NewStringUTF(path);
        jobjectArray contentArray = static_cast<jobjectArray>(env->CallObjectMethod(m_wfxPairObj, s_WfxPairMetIdGetFolderContent, pathStr));
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(pathStr);
            if (contentArray != NULL)
            {
                set<string> contentItems;
                jsize len = env->GetArrayLength(contentArray);
                for (jsize i = 0; i < len; i++)
                {
                    jstring elem = static_cast<jstring>(env->GetObjectArrayElement(contentArray, i));
                    if (elem != NULL)
                    {
                        const char *str = env->GetStringUTFChars(elem, NULL);
                        string item(str);
                        contentItems.insert(item);
                        env->ReleaseStringUTFChars(elem, str);
                    }
                }
                string pathStr(path);
                jobject neObj = env->NewGlobalRef(m_wfxPairObj);
                if (isNewEnv)
                {
                    JVMState::instance()->releaseEnv();
                }
                return new FileEnumerator(neObj, pathStr, contentItems);
            }
        } else
        {
            env->DeleteLocalRef(pathStr);
        }
    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }

    return NULL;
}

bool JVMAccessor::mkdir(char* path)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring pathStr = env->NewStringUTF(path);
        jboolean retVal = env->CallBooleanMethod(m_wfxPairObj, s_WfxPairMetIdMkDir, pathStr);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(pathStr);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return retVal == JNI_TRUE;
        } else
        {
            env->DeleteLocalRef(pathStr);
        }
    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::deletePath(char* path)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring pathStr = env->NewStringUTF(path);
        jboolean retVal = env->CallBooleanMethod(m_wfxPairObj, s_WfxPairMetIdDelPath, pathStr);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(pathStr);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return retVal == JNI_TRUE;
        } else
        {
            env->DeleteLocalRef(pathStr);
        }
    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::rename(char* oldPath, char* newPath)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring oldPathStr = env->NewStringUTF(oldPath);
        jstring newPathStr = env->NewStringUTF(newPath);
        jboolean retVal = env->CallBooleanMethod(m_wfxPairObj, s_WfxPairMetIdRenPath, oldPathStr, newPathStr);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(oldPathStr);
            env->DeleteLocalRef(newPathStr);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return retVal == JNI_TRUE;
        } else
        {
            env->DeleteLocalRef(oldPathStr);
            env->DeleteLocalRef(newPathStr);
        }

    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::copy(char* srcPath, char* destPath)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring srcPathStr = env->NewStringUTF(srcPath);
        jstring destPathStr = env->NewStringUTF(destPath);
        jboolean retVal = env->CallBooleanMethod(m_wfxPairObj, s_WfxPairMetIdCopyPath, srcPathStr, destPathStr);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(srcPathStr);
            env->DeleteLocalRef(destPathStr);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return retVal == JNI_TRUE;
        } else
        {
            env->DeleteLocalRef(srcPathStr);
            env->DeleteLocalRef(destPathStr);
        }

    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::getFile(char* remotePath, char* localPath, ProgressInfo* progressInfo)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring remotePathStr = env->NewStringUTF(remotePath);
        jstring localPathStr = env->NewStringUTF(localPath);

        jobject progressObject = NULL;
        jlong progressInfoAdress = (jlong) progressInfo;
        progressObject = env->NewObject(m_nativeProgressClass, s_NativeProgressConstructor, progressInfoAdress);

        env->CallVoidMethod(m_wfxPairObj, s_WfxPairMetIdGetPath, remotePathStr, localPathStr, progressObject);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(localPathStr);
            env->DeleteLocalRef(remotePathStr);
            env->DeleteLocalRef(progressObject);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return true;
        } else
        {
            env->DeleteLocalRef(localPathStr);
            env->DeleteLocalRef(remotePathStr);
            env->DeleteLocalRef(progressObject);
        }

    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::putFile(char* localPath, char* remotePath, bool overwrite, ProgressInfo* progressInfo)
{
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring localPathStr = env->NewStringUTF(localPath);
        jstring remotePathStr = env->NewStringUTF(remotePath);
        jboolean joverwrite = overwrite;
        jobject progressObject = NULL;
        jlong progressInfoAdress = (jlong) progressInfo;
        progressObject = env->NewObject(m_nativeProgressClass, s_NativeProgressConstructor, progressInfoAdress);
        env->CallVoidMethod(m_wfxPairObj, s_WfxPairMetIdPutPath, localPathStr, remotePathStr, joverwrite, progressObject);
        if (!JVMState::instance()->exceptionExists(env))
        {
            env->DeleteLocalRef(localPathStr);
            env->DeleteLocalRef(remotePathStr);
            env->DeleteLocalRef(progressObject);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
            return true;
        } else
        {
            env->DeleteLocalRef(localPathStr);
            env->DeleteLocalRef(remotePathStr);
            env->DeleteLocalRef(progressObject);
        }

    }
    if (isNewEnv)
    {
        JVMState::instance()->releaseEnv();
    }
    return false;
}

bool JVMAccessor::jvmPathExist(char* remotePath)
{
    bool retVal = false;
    bool isNewEnv = false;
    JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
    if (m_wfxPairObj != NULL)
    {
        jstring remotePathStr = env->NewStringUTF(remotePath);
        jboolean fileExists = env->CallBooleanMethod(m_wfxPairObj, s_WfxPairMetIdfileExists, remotePathStr);
        if (!JVMState::instance()->exceptionExists(env))
        {
            retVal = fileExists;
            env->DeleteLocalRef(remotePathStr);
            if (isNewEnv)
            {
                JVMState::instance()->releaseEnv();
            }
        } else
        {
            env->DeleteLocalRef(remotePathStr);
        }
    }
    return retVal;

}

