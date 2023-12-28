cmake_minimum_required(VERSION 3.22.1)
project(Native)
add_library(
        # Sets the name of the library
        secret
        # Sets the library as a shared library
        SHARED
        # Provides a relative path to your source file(s)
        Secret.cpp

)

