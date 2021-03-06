//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLExecutableQuery.java
//

#include "IOSClass.h"
#include "IOSPrimitiveArray.h"
#include "com/sponberg/fluid/datastore/DatastoreException.h"
#include "com/sponberg/fluid/datastore/SQLExecutableQuery.h"
#include "com/sponberg/fluid/datastore/SQLParameterizedStatement.h"
#include "java/lang/Double.h"
#include "java/lang/IllegalAccessException.h"
#include "java/lang/InstantiationException.h"
#include "java/lang/Integer.h"

@interface FFTSQLExecutableQuery : NSObject
@end

@implementation FFTSQLExecutableQuery

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getParameterizedStatement", NULL, "Lcom.sponberg.fluid.datastore.SQLParameterizedStatement;", 0x401, NULL },
    { "isAllowRefresh", NULL, "Z", 0x401, NULL },
    { "getLimit", NULL, "Ljava.lang.Integer;", 0x401, NULL },
    { "getOffset", NULL, "I", 0x401, NULL },
    { "setOffsetWithInt:", "setOffset", "V", 0x401, NULL },
    { "stepQuery", NULL, "V", 0x401, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "addResult", NULL, "V", 0x401, "Ljava.lang.InstantiationException;Ljava.lang.IllegalAccessException;" },
    { "setNullWithInt:withNSString:", "setNull", "V", 0x401, NULL },
    { "setIntegerWithInt:withNSString:withJavaLangInteger:", "setInteger", "V", 0x401, NULL },
    { "setDoubleWithInt:withNSString:withJavaLangDouble:", "setDouble", "V", 0x401, NULL },
    { "setStringWithInt:withNSString:withNSString:", "setString", "V", 0x401, NULL },
    { "setBinaryWithInt:withNSString:withByteArray:", "setBinary", "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _FFTSQLExecutableQuery = { "SQLExecutableQuery", "com.sponberg.fluid.datastore", NULL, 0x201, 12, methods, 0, NULL, 0, NULL};
  return &_FFTSQLExecutableQuery;
}

@end
