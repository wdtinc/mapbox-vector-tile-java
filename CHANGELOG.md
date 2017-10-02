
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