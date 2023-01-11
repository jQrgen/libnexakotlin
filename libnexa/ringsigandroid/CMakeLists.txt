# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.13.0)
set(CMAKE_VERBOSE_MAKEFILE ON)
set(contrib_dir ${CMAKE_CURRENT_LIST_DIR}/../../../../../contrib)
set(ringsig_lib_dir ${contrib_dir}/build/fujisaki-ringsig/fujisaki-ringsig-master/)

find_package (Python3 COMPONENTS Interpreter)

message(STATUS "Building fujisaki-ringsig dependency")
execute_process(
    COMMAND ${PYTHON_EXECUTABLE} ${contrib_dir}/build-fujisaki-ringsig.py "--ndk_root=${NDK_ROOT}"
    RESULT_VARIABLE rv
    OUTPUT_VARIABLE out
    ERROR_VARIABLE err)
if (NOT rv EQUAL "0")
    message(FATAL_ERROR "${out} ${err}\n\nFailed to build fujisaki-ringsig dependency. Exit status ${rv}")
endif()

add_library(ringsigandroid SHARED ringsigandroid.cpp)
target_include_directories(ringsigandroid PUBLIC . ${ringsig_lib_dir})

if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    set(FUJISAKI_LIB_PATH "${ringsig_lib_dir}/target/armv7-linux-androideabi/release")
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    set(FUJISAKI_LIB_PATH "${ringsig_lib_dir}/target/aarch64-linux-android/release")
elseif (${ANDROID_ABI} STREQUAL "x86")
    set(FUJISAKI_LIB_PATH "${ringsig_lib_dir}/target/i686-linux-android/release")
elseif (${ANDROID_ABI} STREQUAL "x86_64")
    set(FUJISAKI_LIB_PATH "${ringsig_lib_dir}/target/x86_64-linux-android/release")
else()
    message(FATAL_ERROR "Unsupported ANDROID_ABI '${ANDROID_ABI}'")
endif()
target_link_directories(ringsigandroid PUBLIC ${FUJISAKI_LIB_PATH})
#file(COPY "${FUJISAKI_LIB_PATH}/libfujisaki_ringsig.so" DESTINATION ${CMAKE_CURRENT_BINARY_DIR})

add_library(fujisaki_ringsig STATIC IMPORTED)
set_target_properties(fujisaki_ringsig PROPERTIES IMPORTED_LOCATION
    "${FUJISAKI_LIB_PATH}/libfujisaki_ringsig.a")

target_link_libraries(ringsigandroid
    android  # base android library
    log      # android log library)
    fujisaki_ringsig)
