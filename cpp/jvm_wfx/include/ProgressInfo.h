/*
 *    ProgressInfo.h file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#ifndef INCLUDE_PROGRESSINFO_H_
#define INCLUDE_PROGRESSINFO_H_

#include "gendef.h"
#include "wfxplugin.h"

class ProgressInfo
{
public:
    ProgressInfo(const char* source, const char* target, tProgressProc progressInfoExec, int pluginNo);
    virtual ~ProgressInfo();

    bool call(int progressVal);
private:
    char m_source[MAX_PATH];
    char m_target[MAX_PATH];
    tProgressProc m_progressInfoExec;
    int m_pluginNo;
};

#endif /* INCLUDE_PROGRESSINFO_H_ */
