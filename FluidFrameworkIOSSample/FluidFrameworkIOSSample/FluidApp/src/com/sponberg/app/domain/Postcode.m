//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/app/domain/Postcode.java
//

#include "com/sponberg/app/datastore/postcodes/DSPostcode.h"
#include "com/sponberg/app/domain/Postcode.h"
#include "java/lang/Integer.h"
#include "java/lang/Long.h"

@implementation FAPostcode

- (JavaLangLong *)getFluidTableRowObjectId {
  return [[JavaLangLong alloc] initWithLong:[((JavaLangInteger *) nil_chk([self getId])) intValue]];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getFluidTableRowObjectId", NULL, "Ljava.lang.Long;", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcClassInfo _FAPostcode = { "Postcode", "com.sponberg.app.domain", NULL, 0x1, 2, methods, 0, NULL, 0, NULL};
  return &_FAPostcode;
}

@end
