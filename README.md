# MapBox Vector Tile - Java

Provides Java model for reading and writing Mapnik Vector Tiles v2.1. See the Mapbox Vector Tile spec for details.

## Generate VectorTile using vector_tile.proto

Command `protoc` version should be the same version as the POM.xml dependency.

protoc --java_out=src/main/java src/main/resources/vector_tile.proto

## Extra .proto config

These options were added to the .proto file:

 * syntax = "proto2";
 * option java_package = "com.wdtinc.mapbox_vector_tile";
 * option java_outer_classname = "VectorTile";