
## 3.0.0 (Jan 12 2018)

#### Features

- Android 21 Compatibility

- BREAKING CHANGE: Use File rather than Path for Android compatibility.

- Dependency change - JTS to 1.15 under locationtech (Eclipse Licenses).

- Dependency change - protobuf-java to 3.5.1 from 3.0.0-beta-2.

- Dependency change - JUnit to 4.12 from 4.8.2.

- Dependency change - org.slf4j to 1.7.25 from 1.7.12.

#### Fixes

- Fixed guard cases in JTSAdapter to return an empty list when the geometry was not valid for encoding.

- Fixed calculation to float from int of MvtLayerParams#ratio. This value was not being read within the project but may affect other projects.

## 2.0.0 (Oct 2 2017)

#### Features

- BREAKING CHANGE: Rework MVTReader to return JtsMvt objects that retain MVT layer information. No longer returns flat collection of JTS Geometry.

- TagKeyValueMapConverter now uses a LinkedHashMap internally to preserve property order.

## 1.2.0 (Jul 6 2017)

#### Features

- Add support for clipping outside of MVT extent.

## 1.1.1 (Nov 1 2016)

#### Fixes

- Fix issue with JtsAdapter#flatFeatureList(Geometry) using wrong count for flattening GeometryCollection.

## 1.1.0 (Sep 2 2016)

#### Features

- Add support for other Polygon and Multipolygon ring classification when reading in a MVT.


## 1.0 (Aug 12 2016)

- Initial release.