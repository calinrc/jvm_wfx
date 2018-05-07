/*
 *    gendef.h file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#ifndef INCLUDE_GENDEF_H_
#define INCLUDE_GENDEF_H_

#include <JvmWfxVersion.h>
#include <stddef.h>

#define PLUGINS_LOCATION ".config"

#define PLUGIN_LOCATION PLUGINS_LOCATION "/jvm_wfx"
#define LOG_PATH PLUGIN_LOCATION "/logs"
#define JAVA_LAUNCHER_VAL PLUGIN_LOCATION "/java/wfx_launcher.jar"
#define LOGGER_LOCATION PLUGIN_LOCATION "/java/log4j.xml"
#define FULL_LOG_PATH PLUGIN_LOCATION "/logs/jvm_wfx.log"
#define DEPENDENCIES_PATH PLUGIN_LOCATION "/java/deps"

#define PATH_SEPARATOR ":"
#define FILE_SEPARATOR "/"

#ifdef LINUX

#include <dlfcn.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <dirent.h>
#define LIB_HANDLER void* 
#define LOAD_LIB(__PATH__, __PARAMS__) dlopen(__PATH__, __PARAMS__)
#define LOAD_PROC dlsym
#define FREE_LIB dlclose
#define MAIN_JVM_PATH "%s/jre/lib/amd64/server/libjvm.so"
#define ALTERNATIVE_JVM_PATH "%s/jre/lib/amd64/default/libjvm.so"

#else

#include <windows.h>
#define LIB_HANDLER HINSTANCE 
#define LOAD_LIB(__PATH__, __PARAMS__) LoadLibrary(__PATH__)
#define LOAD_PROC GetProcAddress
#define FREE_LIB FreeLibrary
#define MAIN_JVM_PATH "%s\\jre\\bin\\default\\jvm.dll"
#define ALTERNATIVE_JVM_PATH "%s\\jre\\bin\\server\\jvm.dll"



#endif

#define MAX_PATH 260

enum JVMStateEnum
{
    JVMLoaded, JVMLoadFail, JVMDetached, JVMDetachFail
};



#endif /* INCLUDE_GENDEF_H_ */
