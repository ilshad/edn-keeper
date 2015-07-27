# edn-keeper

Backing EDN data structures with Amazon S3 or local
filesystem. Uploads to S3 use [durable queue](https://github.com/factual/durable-queue),
so entries will survive process death.

## Installation

[![Clojars Project](http://clojars.org/ilshad/edn-keeper/latest-version.svg)](http://clojars.org/ilshad/edn-keeper)

Add following to project's dependencies:

```clojure
[byte-streams "0.2.0"]
[ilshad/edn-keeper "xxx"]
```

## Usage

First, define keeper object. Configure the storage with Amazon S3 or
local filesystem:

```clojure
(require '[edn-keeper.core :as k])

;; Option #1: Amazon S3
(def keeper (k/keeper {:storage :s3 :bucket "my-bucket-name"}))

;; Option #2: local filesystem
(def keeper (k/keeper {:storage :fs :path "/var/edn-keeper/my-application"}))
```

Keeper object implements `edn-keeper.core/IKeeper` protocol.

```clojure
;; Save the data in the storage. "my-key" is kind of database partition.
(k/put keeper "my-key" {:foo [bar 42]})

;; List partitions
(println (k/keys keeper))

;; Download partition form the storage and read EDN
(println (k/get keeper "my-key"))
```

## License

Copyright © 2015 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
