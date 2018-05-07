/*
 *    Version.h file written and maintained by Calin Cocan
 *    Created on: May 07, 2018
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

#ifndef INCLUDE_JVMWFXVERSION_
#define INCLUDE_JVMWFXVERSION_

#ifdef PREDEFINED_BUILD_NO
#define BUILD_NO PREDEFINED_BUILD_NO
#else
#define BUILD_NO "DUMMY"
#endif

#ifdef PREDEFINED_VERSION_NO
#define VERSION PREDEFINED_VERSION_NO
#else
#define VERSION "0.1"
#endif


class JvmWfxVersion
{

    static const char* GetVersion()
    {
        return VERSION;
    }

    static const char* GetBuildNumber()
    {
        return BUILD_NO;
    }
};

#endif /* INCLUDE_JVMWFXVERSION_ */
