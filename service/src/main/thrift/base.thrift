include "deps.thrift"

#@namespace scala base.thrift

struct Something {
  1: required deps.Dependency dep;
}

