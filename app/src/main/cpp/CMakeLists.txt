cmake_minimum_required(VERSION 3.22.1)

project("api_key")

add_library(api_key SHARED
            api_key.cpp)

find_library(log-lib log)

target_link_libraries(api_key
                      ${log-lib})

set_target_properties(api_key PROPERTIES
                      CXX_STANDARD 17
                      CXX_STANDARD_REQUIRED ON) 