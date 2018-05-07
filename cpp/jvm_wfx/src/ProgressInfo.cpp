/*
 *    ProgressInfo.cpp file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#include "ProgressInfo.h"
#include <string.h>
#include "Logger.h"

ProgressInfo::ProgressInfo(const char* source, const char* target, tProgressProc progressInfoExec, int pluginNo) :
        m_progressInfoExec(progressInfoExec), m_pluginNo(pluginNo)
{
    memset(m_source, 0, sizeof(m_source));
    memset(m_target, 0, sizeof(m_target));
    if (source != NULL)
    {
        strncpy(m_source, source, MAX_PATH);
    }
    if (target != NULL)
    {
        strncpy(m_target, target, MAX_PATH);
    }

}

ProgressInfo::~ProgressInfo()
{
}

bool ProgressInfo::call(int progressVal)
{
    if (m_progressInfoExec != NULL)
    {
        bool canceled =  m_progressInfoExec(m_pluginNo, m_source, m_target, progressVal) == 1;
        if (canceled){
            LOGGING("Operation on file %s canceled", m_source);
        }
        return canceled;
    }
    return false;
}
