# MapBox Vector Tile - Java

## Overview

Provides:

 * protoc generated model for Mapbox Vector Tiles v2.1.
 * Provides MVT encoding through use of the Java Topology Suite (JTS).

See https://github.com/mapbox/vector-tile-spec/tree/master/2.1 for details on the MVT spec.
See https://developers.google.com/protocol-buffers/ for details on protoc.
See http://www.vividsolutions.com/jts/ for details on JTS.

In the future, we would like to add decoding support and additional features.

## Generating VectorTile class using vector_tile.proto

Command `protoc` version should be the same version as the POM.xml dependency.

protoc --java_out=src/main/java src/main/resources/vector_tile.proto

## Extra .proto config

These options were added to the .proto file:

 * syntax = "proto2";
 * option java_package = "com.wdtinc.mapbox_vector_tile";
 * option java_outer_classname = "VectorTile";

## Reporting Issues

Use the Github issue tracker.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)
 
## License

http://www.apache.org/licenses/LICENSE-2.0.html