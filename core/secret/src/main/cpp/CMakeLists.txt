cmake_minimum_required(VERSION 3.22.1)
project(Native)
add_library(
        # Sets the name of the library
        extractor
        # Sets the library as a shared library
        SHARED
        # Provides a relative path to your source file(s)
        extractor.cpp
)

set(CMAKE_CXX_STANDARD 23)

#add_subdirectory(http)
#target_link_libraries(Native httplib)
