//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLInsert.java
//

#include "IOSClass.h"
#include "com/sponberg/fluid/datastore/SQLDataInput.h"
#include "com/sponberg/fluid/datastore/SQLInsert.h"
#include "com/sponberg/fluid/datastore/SQLParameterizedStatement.h"
#include "com/sponberg/fluid/datastore/SQLTable.h"
#include "java/lang/StringBuilder.h"
#include "java/util/ArrayList.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@implementation FFTSQLInsert

- (id)initWithId:(id<FFTSQLDataInput, FFTSQLTable>)object {
  if (self = [super init]) {
    self->object_ = object;
  }
  return self;
}

- (FFTSQLParameterizedStatement *)getParameterizedStatement {
  JavaUtilArrayList *params = [[JavaUtilArrayList alloc] init];
  JavaLangStringBuilder *builder = [[JavaLangStringBuilder alloc] init];
  (void) [builder appendWithNSString:@"insert into "];
  (void) [builder appendWithNSString:[((id<FFTSQLDataInput, FFTSQLTable>) nil_chk(object_)) _getTableName]];
  if ([((id<JavaUtilSet>) nil_chk([((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) object_) _getData])) entrySet])) size] > 0) {
    (void) [builder appendWithNSString:@" ("];
    BOOL first = YES;
    for (id<JavaUtilMap_Entry> __strong entry_ in nil_chk([((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) object_) _getData])) entrySet])) {
      if (!first) {
        (void) [builder appendWithNSString:@", "];
      }
      first = NO;
      (void) [builder appendWithNSString:[((id<JavaUtilMap_Entry>) nil_chk(entry_)) getKey]];
      [params addWithId:[[FFTSQLParameterizedStatement_Pair alloc] initWithNSString:[entry_ getKey] withId:[entry_ getValue]]];
    }
    (void) [builder appendWithNSString:@") values ("];
    first = YES;
    for (int i = 0; i < [((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) object_) _getData])) size]; i++) {
      if (!first) {
        (void) [builder appendWithNSString:@", ?"];
      }
      else {
        (void) [builder appendWithNSString:@"?"];
      }
      first = NO;
    }
    (void) [builder appendWithNSString:@")"];
  }
  else {
    (void) [builder appendWithNSString:@" default values"];
  }
  return [[FFTSQLParameterizedStatement alloc] initWithNSString:[builder description] withJavaUtilArrayList:params withJavaUtilArrayList:nil];
}

- (NSString *)getSqlStatementUnbound {
  JavaLangStringBuilder *builder = [[JavaLangStringBuilder alloc] init];
  (void) [builder appendWithNSString:@"insert into "];
  (void) [builder appendWithNSString:[((id<FFTSQLDataInput, FFTSQLTable>) nil_chk(object_)) _getTableName]];
  (void) [builder appendWithNSString:@" ("];
  BOOL first = YES;
  for (NSString * __strong key in nil_chk([((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) object_) _getData])) keySet])) {
    if (!first) {
      (void) [builder appendWithNSString:@", "];
    }
    first = NO;
    (void) [builder appendWithNSString:key];
  }
  (void) [builder appendWithNSString:@") values ("];
  first = YES;
  for (int i = 0; i < [((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) object_) _getData])) size]; i++) {
    if (!first) {
      (void) [builder appendWithNSString:@", ?"];
    }
    else {
      (void) [builder appendWithNSString:@"?"];
    }
    first = NO;
  }
  (void) [builder appendWithNSString:@")"];
  return [builder description];
}

- (NSString *)getTable {
  return [((id<FFTSQLDataInput, FFTSQLTable>) nil_chk(object_)) _getTableName];
}

- (id)getObject {
  return self->object_;
}

- (NSString *)description {
  return [NSString stringWithFormat:@"SQLInsert(object=%@)", [self getObject]];
}

- (void)copyAllFieldsTo:(FFTSQLInsert *)other {
  [super copyAllFieldsTo:other];
  other->object_ = object_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithId:", "SQLInsert", NULL, 0x1, NULL },
    { "getParameterizedStatement", NULL, "Lcom.sponberg.fluid.datastore.SQLParameterizedStatement;", 0x1, NULL },
    { "getSqlStatementUnbound", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getTable", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getObject", NULL, "TT;", 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "object_", NULL, 0x0, "TT;", NULL,  },
  };
  static J2ObjcClassInfo _FFTSQLInsert = { "SQLInsert", "com.sponberg.fluid.datastore", NULL, 0x1, 6, methods, 1, fields, 0, NULL};
  return &_FFTSQLInsert;
}

@end
