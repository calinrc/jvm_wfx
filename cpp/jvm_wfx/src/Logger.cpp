/*
 *    Logger.cpp file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#include "Logger.h"
#include <fcntl.h>
#include <stdarg.h>
#include <time.h>

#ifdef LINUX
#include <unistd.h>
#include <sys/time.h>
#include <pwd.h>
#else

#endif

#include <stdlib.h>
#include <string.h>
#include "Utilities.h"

Logger* Logger::s_instance = new Logger();

Logger::Logger() :
        m_isFileLoggingEnabled(false), m_isConsoleLoggingEnabled(false), m_file(NULL), m_externalLogger(NULL), m_pluginNo(-1)
{
}

Logger::~Logger()
{
    if (m_isFileLoggingEnabled && m_file != NULL)
    {
        fclose(m_file);
    }

}

void Logger::init(bool consoleEnable, bool fileEnable, tLogProc pLogProc, int pluginNo)
{
    this->m_externalLogger = pLogProc;
    this->m_pluginNo = pluginNo;
    this->init(consoleEnable, fileEnable);
}

void Logger::init(bool consoleEnable, bool fileEnable)
{
    this->m_isConsoleLoggingEnabled = consoleEnable;
    this->m_isFileLoggingEnabled = fileEnable;

    if (m_isFileLoggingEnabled && m_file == NULL)
    {

        char logPath[MAX_PATH];
        size_t pathSize = MAX_PATH;

        Utilities::mkDirectory(Utilities::getPluginsDir(logPath, &pathSize));
        pathSize = MAX_PATH;
        Utilities::mkDirectory(Utilities::getPluginDir(logPath, &pathSize));
        pathSize = MAX_PATH;
        Utilities::mkDirectory(Utilities::getLogDir(logPath, &pathSize));
        pathSize = MAX_PATH;
        Utilities::getLogFilePath(logPath, &pathSize);

        m_file = fopen(logPath, "a");
    }
    this->log("Logger initialized");

}

void Logger::end()
{
    if (m_isFileLoggingEnabled && m_file != NULL)
    {
        fclose(m_file);
        m_isFileLoggingEnabled = false;
        m_file = NULL;
    }

}

void Logger::log(const char* msg, ...)
{
    if (m_isFileLoggingEnabled == true || m_isConsoleLoggingEnabled == true)
    {
        va_list args;
        va_start(args, msg);
        char * displayMsg = buildMessage(msg, args);
        if (m_isFileLoggingEnabled)
        {
            fprintf(m_file, "%s\n", displayMsg);
            fflush(m_file);
        }
        if (m_isConsoleLoggingEnabled)
        {
            fprintf(stdout, "%s\n", displayMsg);

        }
        if (m_externalLogger != NULL)
        {
            m_externalLogger(m_pluginNo, 0, displayMsg);
        }
        va_end(args);
        delete[] displayMsg;
    }
}

char* Logger::buildMessage(const char* msg, va_list argList)
{
    int len = 0, count = 300000;
    char* buff = new char[count];
    char * cBuff = new char[count];


    time_t zaman;
    struct tm *ltime;

    time(&zaman);
    ltime = (struct tm *) localtime(&zaman);
    
    strftime(cBuff, count, "%d.%m.%y %H:%M:%S", ltime);
    sprintf(cBuff, "%s - %s", cBuff, msg);
    len = vsnprintf(buff, count - 1, cBuff, argList);
    if (len >= count)
    {
        delete[] buff;
        count = len + 1;
        buff = new char[count];
        len = vsnprintf(buff, count - 1, cBuff, argList);
    }
    if (len == -1)
    {
        delete[] buff;
        count = count * 10;
        buff = new char[count];
        len = vsnprintf(buff, count - 1, cBuff, argList);
    }
    delete[] cBuff;
    return buff;
}
