//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/DatastoreException.java
//

#include "com/sponberg/fluid/datastore/DatastoreException.h"
#include "java/lang/Exception.h"

@implementation FFTDatastoreException

- (id)initWithNSString:(NSString *)message {
  return [super initWithNSString:message];
}

- (id)initWithJavaLangException:(JavaLangException *)e {
  return [super initWithJavaLangThrowable:e];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:", "DatastoreException", NULL, 0x1, NULL },
    { "initWithJavaLangException:", "DatastoreException", NULL, 0x1, NULL },
  };
  static J2ObjcClassInfo _FFTDatastoreException = { "DatastoreException", "com.sponberg.fluid.datastore", NULL, 0x1, 2, methods, 0, NULL, 0, NULL};
  return &_FFTDatastoreException;
}

@end
