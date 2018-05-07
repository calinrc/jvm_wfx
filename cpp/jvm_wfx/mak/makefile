################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

COMPILE_OPTIMIZE_PARAMS=-O0 -g3
DEBUG_FLAG=-DJVM_WFX_DEBUG

ifeq ($(CONFIGURATION),RELEASE)
	COMPILE_OPTIMIZE_PARAMS=-O3
        DEBUG_FLAG=
endif

ifdef BUILD_INFO
     META_INFO=-DPREDEFINED_BUILD_NO=\"$(BUILD_INFO)\"
endif

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: jvm_wfx.wfx

# Tool invocations
jvm_wfx.wfx: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C++ Linker'
	g++ -shared -o "jvm_wfx.wfx" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '
	$(MAKE) --no-print-directory post-build

# Other Targets
clean:
	-$(RM) $(OBJS)$(C++_DEPS)$(C_DEPS)$(CC_DEPS)$(LIBRARIES)$(CPP_DEPS)$(CXX_DEPS)$(C_UPPER_DEPS) jvm_wfx.wfx
	-@echo ' '

post-build:
	-cp -f jvm_wfx.wfx ${HOME}/.config/jvm_wfx/
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY: post-build

-include ../makefile.targets
