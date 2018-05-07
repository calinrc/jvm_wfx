/*
 *    FileEnumerator.cpp file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#include <FileEnumerator.h>
#include <JVMAccessor.h>
#include <string.h>
#include "JVMState.h"
#include "common.h"
#include "gendef.h"
#include "Logger.h"

FileEnumerator::FileEnumerator(jobject wfxPairObj, string& parentPath, set<string>& content) :
        m_parent(parentPath), m_content(content), m_wfxPairObj(wfxPairObj)
{
    m_it = m_content.begin();
}

FileEnumerator::~FileEnumerator()
{
    this->close();
}

bool FileEnumerator::getNext(WIN32_FIND_DATAA *FindData)
{
    bool hasContent = m_it != m_content.end();
    bool isNewEnv = false;
    if (hasContent)
    {
        string item = *m_it;
        JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
        jobject fileInfoObj = getFileInfo(env, m_parent, item);
        if (fileInfoObj != NULL)
        {
            getFileInfoContent(env, fileInfoObj, item, FindData);
            env->DeleteLocalRef(fileInfoObj);
        }
        if (isNewEnv)
        {
            JVMState::instance()->releaseEnv();
        }
        ++m_it;
    }
    return hasContent;
}
void FileEnumerator::close()
{
    if (m_wfxPairObj != NULL)
    {
        bool isNewEnv = false;
        JNIEnv* env = JVMState::instance()->getEnv(&isNewEnv);
        env->DeleteGlobalRef(m_wfxPairObj);
        if (isNewEnv)
        {
            JVMState::instance()->releaseEnv();
        }
        m_wfxPairObj = NULL;
    }
}

jobject FileEnumerator::getFileInfo(JNIEnv* env, string& path, string& item)
{
    if (m_wfxPairObj != NULL)
    {
        jstring pathStr = env->NewStringUTF(path.c_str());
        jstring itemStr = env->NewStringUTF(item.c_str());
        jobject fileInfo = env->CallObjectMethod(m_wfxPairObj, JVMAccessor::s_WfxPairMetIdGetFileInfo, pathStr, itemStr);
        env->DeleteLocalRef(pathStr);
        env->DeleteLocalRef(itemStr);
        return fileInfo;
    }
    return NULL;
}

void FileEnumerator::getFileInfoContent(JNIEnv* env, jobject fileInfoItem, string& itemName, WIN32_FIND_DATAA *findData)
{
    memset(findData, 0, sizeof(WIN32_FIND_DATAA));

    strncpy(findData->cFileName, itemName.c_str(), MAX_PATH);
    jlong fileAttributes = env->CallLongMethod(fileInfoItem, JVMAccessor::s_FileInfoGetFileAttributes);
    findData->dwFileAttributes = (DWORD)fileAttributes;
    jlong fileCreationTime = env->CallLongMethod(fileInfoItem, JVMAccessor::s_FileInfoGetFileCreationTime);
    findData->ftCreationTime.dwLowDateTime = (DWORD) fileCreationTime;
    findData->ftCreationTime.dwHighDateTime = fileCreationTime >> 32;

    findData->ftLastWriteTime.dwLowDateTime = (DWORD) fileCreationTime;
    findData->ftLastWriteTime.dwHighDateTime = fileCreationTime >> 32;

    jlong fileAccessTime = env->CallLongMethod(fileInfoItem, JVMAccessor::s_FileInfoGetFileLastAccessTime);
    findData->ftLastAccessTime.dwLowDateTime = (DWORD) fileAccessTime;
    findData->ftLastAccessTime.dwHighDateTime = fileAccessTime >> 32;

    jlong fileSize = env->CallLongMethod(fileInfoItem, JVMAccessor::s_FileInfoGetFileSize);

    findData->nFileSizeLow = (DWORD) fileSize;
    findData->nFileSizeHigh = fileSize >> 32;

    jlong reserved0Val = env->CallLongMethod(fileInfoItem, JVMAccessor::s_FileInfoGetReserved0);

    findData->dwReserved0 = (DWORD)reserved0Val;

}

