cmake_minimum_required(VERSION 3.13.0)

project("linknexakotlin")
include(ndk-stl-config.cmake)
set(ANDROID_STL "c++_shared")
#add_library(libnexalight SHARED IMPORTED)
#set_target_properties(libnexalight PROPERTIES IMPORTED_LOCATION
#    "${CMAKE_CURRENT_LIST_DIR}/../../../libnexa/build/android/${ANDROID_ABI}/libnexalight.so")

add_library(
    linknexakotlin
    SHARED
    nexakotlincpp.cpp
)

target_include_directories(
    linknexakotlin
    PUBLIC ${CMAKE_CURRENT_LIST_DIR}/../../../libnexa/src/include
    PUBLIC ${CMAKE_CURRENT_LIST_DIR}/../../../build/nativeHeaders
)

set(NEXA_LIGHT "-Wl,--whole-archive ${CMAKE_CURRENT_LIST_DIR}/../../../libnexa/build/android/${ANDROID_ABI}/libnexalight.a -Wl,--no-whole-archive")

target_link_libraries(linknexakotlin
        ${NEXA_LIGHT}
        android
        log
        c++_static
)


# ${CMAKE_CURRENT_LIST_DIR}/../../../libnexa/build/android/${ANDROID_ABI}/libnexa.a