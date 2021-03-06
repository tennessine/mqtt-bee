# MQTT Bee

[![Build Status](https://travis-ci.org/mqtt-bee/mqtt-bee.svg?branch=develop)](https://travis-ci.org/mqtt-bee/mqtt-bee)

MQTT 5.0 and 3.1.1 compatible client library with a reactive API and back pressure support.

# Status
IMPORTANT: ALPHA STATUS, DO NOT USE IN PRODUCTION YET!

## DONE
 - Basic MQTT 3.1.1 and MQTT 5.0 functionality
 - Reactive API
 - Backpressure support for QoS 1 and 2
 - Additional MQTT 5 features: Server-Reauth

## TODO
 - APIs are not stable yet
 - Backpressure support for QoS 0
 - Reconnect Handling and Message redelivery
 - SSL integration (branch need to be integrated)
 - Websocket support
 - Simpler APIs
 - Disk Persistence

# Example
tbd

## How to build

JDK 8 is required.

## Branching model

* master: the release branch
* develop: where features are merged into
* every feature or bugfix will have its own branch, branched from develop, 
it will be merged after the code review of the pull request

### Branching guidelines

* branch types: feature, bugfix
* branch names: starting with feature/, bugfix/ + lower case name of the task, spaces replaced with -
* also branches from a feature branch are called feature/ or bugfix/ 
whether it contributes to the feature as a sub-feature or a bugfix

### Commit message guidelines

* Commits in feature/bugfix branches should be as atomic as possible.
* Commits in feature/bugfix branches do not have to follow strict guidelines, 
but they should describe the changes clearly.
* When feature/bugfix branches are merged into develop, the commit message should follow these guidelines: tbd

## Code style guidelines

* Please import the code style settings found in the codeStyle folder. 
* The project uses its own @NotNull and @Nullable annotations, 
every non-primitive parameter should be annotated with one of them.
* Optional will only be used in the public API.

