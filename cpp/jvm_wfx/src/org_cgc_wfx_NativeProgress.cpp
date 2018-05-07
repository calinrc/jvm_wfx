#include "org_cgc_wfx_NativeProgress.h"
#include "ProgressInfo.h"
/*
 * Class:     org_cgc_wfx_NativeProgress
 * Method:    notifyProgress
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_cgc_wfx_NativeProgress_notifyProgress(JNIEnv *env, jobject, jlong ptr, jint progress)
{
    ProgressInfo* pregressStructure = (ProgressInfo*) ptr;
    if (pregressStructure != NULL)
    {
        return pregressStructure->call(progress) == true ? JNI_TRUE : JNI_FALSE;
    }
    return JNI_FALSE;

}

