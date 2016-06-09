//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/DatastoreTransaction.java
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/sponberg/fluid/FluidApp.h"
#include "com/sponberg/fluid/GlobalState.h"
#include "com/sponberg/fluid/datastore/DatastoreException.h"
#include "com/sponberg/fluid/datastore/DatastoreManager.h"
#include "com/sponberg/fluid/datastore/DatastoreService.h"
#include "com/sponberg/fluid/datastore/DatastoreTransaction.h"
#include "com/sponberg/fluid/datastore/SQLDataInput.h"
#include "com/sponberg/fluid/datastore/SQLInsert.h"
#include "com/sponberg/fluid/datastore/SQLQuery.h"
#include "com/sponberg/fluid/datastore/SQLQueryJoin.h"
#include "com/sponberg/fluid/datastore/SQLQueryJoin3.h"
#include "com/sponberg/fluid/datastore/SQLQueryJoinBase.h"
#include "com/sponberg/fluid/datastore/SQLQueryResultDefault.h"
#include "com/sponberg/fluid/datastore/SQLResultList.h"
#include "com/sponberg/fluid/datastore/SQLTable.h"
#include "com/sponberg/fluid/datastore/SQLUpdate.h"
#include "com/sponberg/fluid/datastore/SQLUtil.h"
#include "com/sponberg/fluid/datastore/SQLWhereClause.h"
#include "java/lang/Integer.h"
#include "java/lang/Long.h"
#include "java/lang/RuntimeException.h"
#include "java/util/ArrayList.h"
#include "java/util/LinkedHashMap.h"
#include "java/util/Map.h"
#include "java/util/concurrent/locks/ReentrantLock.h"

BOOL FFTDatastoreTransaction_initialized = NO;

@implementation FFTDatastoreTransaction

JavaUtilConcurrentLocksReentrantLock * FFTDatastoreTransaction_lock_;

- (id)init {
  return [self initFFTDatastoreTransactionWithNSString:[((FFTDatastoreManager_Database *) nil_chk([((FFTDatastoreManager *) nil_chk([((FFTFluidApp *) nil_chk(FFTGlobalState_get_fluidApp__())) getDatastoreManager])) getDefaultDatabase])) getSimpleName]];
}

- (id)initFFTDatastoreTransactionWithNSString:(NSString *)databaseName {
  if (self = [super init]) {
    started_ = NO;
    committed_ = NO;
    rolledBack_ = NO;
    ds_ = [((FFTFluidApp *) nil_chk(FFTGlobalState_get_fluidApp__())) getDatastoreService];
    self->database_ = [((FFTDatastoreManager *) nil_chk([FFTGlobalState_get_fluidApp__() getDatastoreManager])) getDatabaseWithNSString:databaseName];
  }
  return self;
}

- (id)initWithNSString:(NSString *)databaseName {
  return [self initFFTDatastoreTransactionWithNSString:databaseName];
}

- (void)start {
  if (started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state when start called"];
  }
  [((JavaUtilConcurrentLocksReentrantLock *) nil_chk(FFTDatastoreTransaction_lock_)) lock];
  [((id<FFTDatastoreService>) nil_chk(ds_)) openDatabaseWithNSString:[((FFTDatastoreManager_Database *) nil_chk(database_)) getDatabaseName]];
  [ds_ startTransaction];
  started_ = YES;
}

- (void)commit {
  if (!started_ || committed_ || rolledBack_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state when commit called"];
  }
  [((id<FFTDatastoreService>) nil_chk(ds_)) commitTransaction];
  [ds_ closeDatabase];
  committed_ = YES;
  [((JavaUtilConcurrentLocksReentrantLock *) nil_chk(FFTDatastoreTransaction_lock_)) unlock];
}

- (void)rollback {
  if (!started_ || committed_ || rolledBack_) {
    return;
  }
  [((id<FFTDatastoreService>) nil_chk(ds_)) rollbackTransaction];
  @try {
    [ds_ closeDatabase];
  }
  @catch (FFTDatastoreException *e) {
  }
  rolledBack_ = YES;
  [((JavaUtilConcurrentLocksReentrantLock *) nil_chk(FFTDatastoreTransaction_lock_)) unlock];
}

- (void)executeRawStatementWithNSString:(NSString *)statement {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  [((id<FFTDatastoreService>) nil_chk(ds_)) executeRawStatementWithNSString:statement];
}

- (FFTSQLResultList *)queryWithFFTSQLQuery:(FFTSQLQuery *)query {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [((id<FFTDatastoreService>) nil_chk(ds_)) queryWithFFTSQLQuery:query];
}

- (FFTDatastoreTransaction_QueryBuilder *)queryWithIOSClass:(IOSClass *)queryResultClass {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [[FFTDatastoreTransaction_QueryBuilder alloc] initWithFFTDatastoreTransaction:self withIOSClass:queryResultClass];
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)queryJoinWithIOSClass:(IOSClass *)queryResultClass
                                                       withIOSClass:(IOSClass *)queryResultClass2 {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [[FFTDatastoreTransaction_QueryJoinBuilder alloc] initWithFFTDatastoreTransaction:self withIOSClass:queryResultClass withIOSClass:queryResultClass2];
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)queryJoinWithIOSClass:(IOSClass *)queryResultClass
                                                        withIOSClass:(IOSClass *)queryResultClass2
                                                        withIOSClass:(IOSClass *)queryResultClass3 {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [[FFTDatastoreTransaction_QueryJoinBuilder3 alloc] initWithFFTDatastoreTransaction:self withIOSClass:queryResultClass withIOSClass:queryResultClass2 withIOSClass:queryResultClass3];
}

- (FFTDatastoreTransaction_QueryFunctionBuilder *)queryFunctionWithNSString:(NSString *)function
                                                               withIOSClass:(IOSClass *)queryResultClass {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [[FFTDatastoreTransaction_QueryFunctionBuilder alloc] initWithFFTDatastoreTransaction:self withNSString:function withNSString:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass]];
}

- (FFTDatastoreTransaction_UpdateBuilder *)updateWithId:(id<FFTSQLDataInput, FFTSQLTable>)object {
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [[FFTDatastoreTransaction_UpdateBuilder alloc] initWithFFTDatastoreTransaction:self withId:object];
}

- (JavaLangLong *)insertWithId:(id<FFTSQLDataInput, FFTSQLTable>)object {
  FFTSQLInsert *insert = [[FFTSQLInsert alloc] initWithId:object];
  if (!started_) {
    @throw [[FFTDatastoreException alloc] initWithNSString:@"Invalid state, transaction not started"];
  }
  return [((id<FFTDatastoreService>) nil_chk(ds_)) insertWithFFTSQLInsert:insert];
}

+ (void)initialize {
  if (self == [FFTDatastoreTransaction class]) {
    FFTDatastoreTransaction_lock_ = [[JavaUtilConcurrentLocksReentrantLock alloc] init];
    FFTDatastoreTransaction_initialized = YES;
  }
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction *)other {
  [super copyAllFieldsTo:other];
  other->committed_ = committed_;
  other->database_ = database_;
  other->ds_ = ds_;
  other->rolledBack_ = rolledBack_;
  other->started_ = started_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", "DatastoreTransaction", NULL, 0x1, NULL },
    { "initWithNSString:", "DatastoreTransaction", NULL, 0x1, NULL },
    { "start", NULL, "V", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "commit", NULL, "V", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "rollback", NULL, "V", 0x1, NULL },
    { "executeRawStatementWithNSString:", "executeRawStatement", "V", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "queryWithFFTSQLQuery:", "query", "Lcom.sponberg.fluid.datastore.SQLResultList;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "queryWithIOSClass:", "query", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "queryJoinWithIOSClass:withIOSClass:", "queryJoin", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "queryJoinWithIOSClass:withIOSClass:withIOSClass:", "queryJoin", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "queryFunctionWithNSString:withIOSClass:", "queryFunction", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryFunctionBuilder;", 0x1, NULL },
    { "updateWithId:", "update", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$UpdateBuilder;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "insertWithId:", "insert", "Ljava.lang.Long;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "ds_", NULL, 0x0, "Lcom.sponberg.fluid.datastore.DatastoreService;", NULL,  },
    { "started_", NULL, 0x0, "Z", NULL,  },
    { "committed_", NULL, 0x0, "Z", NULL,  },
    { "rolledBack_", NULL, 0x0, "Z", NULL,  },
    { "database_", NULL, 0x0, "Lcom.sponberg.fluid.datastore.DatastoreManager$Database;", NULL,  },
    { "lock_", NULL, 0x18, "Ljava.util.concurrent.locks.ReentrantLock;", &FFTDatastoreTransaction_lock_,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction = { "DatastoreTransaction", "com.sponberg.fluid.datastore", NULL, 0x1, 13, methods, 6, fields, 0, NULL};
  return &_FFTDatastoreTransaction;
}

@end

@implementation FFTDatastoreTransaction_UpdateBuilder

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$
                               withId:(id<FFTSQLDataInput, FFTSQLTable>)object {
  this$0_ = outer$;
  if (self = [super init]) {
    paramNames_ = [[JavaUtilArrayList alloc] init];
    paramValues_ = [[JavaUtilArrayList alloc] init];
    self->object_ = object;
  }
  return self;
}

- (FFTDatastoreTransaction_UpdateBuilder *)whereWithNSString:(NSString *)where {
  self->where__ = where;
  return self;
}

- (FFTDatastoreTransaction_UpdateBuilder *)paramWithNSString:(NSString *)name
                                                      withId:(id)value {
  if (where__ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  if (value == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Where parameter value can't be null. Instead of '= ?' use 'is null'"];
  }
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:name];
  [((JavaUtilArrayList *) nil_chk(paramValues_)) addWithId:value];
  return self;
}

- (void)execute {
  if ([((id<JavaUtilMap>) nil_chk([((id<FFTSQLDataInput, FFTSQLTable>) nil_chk(object_)) _getData])) size] == 0) {
    return;
  }
  FFTSQLUpdate *update = [[FFTSQLUpdate alloc] initWithId:object_];
  if (where__ != nil) {
    [update setWhereClauseWithFFTSQLWhereClause:[[FFTSQLWhereClause alloc] initWithNSString:where__ withJavaUtilArrayList:paramNames_ withJavaUtilArrayList:paramValues_]];
  }
  [((id<FFTDatastoreService>) nil_chk(this$0_->ds_)) updateWithFFTSQLUpdate:update];
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_UpdateBuilder *)other {
  [super copyAllFieldsTo:other];
  other->object_ = object_;
  other->paramNames_ = paramNames_;
  other->paramValues_ = paramValues_;
  other->where__ = where__;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:withId:", "UpdateBuilder", NULL, 0x1, NULL },
    { "whereWithNSString:", "where", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$UpdateBuilder;", 0x1, NULL },
    { "paramWithNSString:withId:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$UpdateBuilder;", 0x1, NULL },
    { "execute", NULL, "V", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "Lcom.sponberg.fluid.datastore.DatastoreTransaction;", NULL,  },
    { "object_", NULL, 0x0, "TT;", NULL,  },
    { "where__", "where", 0x0, "Ljava.lang.String;", NULL,  },
    { "paramNames_", NULL, 0x0, "Ljava.util.ArrayList;", NULL,  },
    { "paramValues_", NULL, 0x0, "Ljava.util.ArrayList;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_UpdateBuilder = { "UpdateBuilder", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 4, methods, 5, fields, 0, NULL};
  return &_FFTDatastoreTransaction_UpdateBuilder;
}

@end

@implementation FFTDatastoreTransaction_QueryBuilderBase

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$ {
  if (self = [super init]) {
    paramNames_ = [[JavaUtilArrayList alloc] init];
    paramValues_ = [[JavaUtilArrayList alloc] init];
    offset_ = 0;
    limit_ = nil;
    allowRefresh_ = YES;
    orderBy_ = nil;
    groupBy_ = nil;
  }
  return self;
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_QueryBuilderBase *)other {
  [super copyAllFieldsTo:other];
  other->allowRefresh_ = allowRefresh_;
  other->groupBy_ = groupBy_;
  other->limit_ = limit_;
  other->offset_ = offset_;
  other->orderBy_ = orderBy_;
  other->paramNames_ = paramNames_;
  other->paramValues_ = paramValues_;
  other->where_ = where_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:", "init", NULL, 0x0, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "where_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "paramNames_", NULL, 0x0, "Ljava.util.ArrayList;", NULL,  },
    { "paramValues_", NULL, 0x0, "Ljava.util.ArrayList;", NULL,  },
    { "offset_", NULL, 0x0, "I", NULL,  },
    { "limit_", NULL, 0x0, "Ljava.lang.Integer;", NULL,  },
    { "allowRefresh_", NULL, 0x0, "Z", NULL,  },
    { "orderBy_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "groupBy_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_QueryBuilderBase = { "QueryBuilderBase", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 1, methods, 8, fields, 0, NULL};
  return &_FFTDatastoreTransaction_QueryBuilderBase;
}

@end

@implementation FFTDatastoreTransaction_QueryBuilder

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$
                         withIOSClass:(IOSClass *)queryResultClass {
  this$1_ = outer$;
  if (self = [super initWithFFTDatastoreTransaction:outer$]) {
    self->queryResultClass_ = queryResultClass;
  }
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)selectWithNSStringArray:(IOSObjectArray *)columns {
  self->columns_ = columns;
  {
    IOSObjectArray *a__ = columns;
    NSString * const *b__ = ((IOSObjectArray *) nil_chk(a__))->buffer_;
    NSString * const *e__ = b__ + a__->size_;
    while (b__ < e__) {
      NSString *c = (*b__++);
      if ([((NSString *) nil_chk(c)) contains:@","]) {
        @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Column name (or function) must not contain ','"];
      }
    }
  }
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)selectColumnsWithNSStringArray:(IOSObjectArray *)columns {
  self->columns_ = columns;
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)whereWithNSString:(NSString *)where {
  self->where_ = where;
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)paramWithNSString:(NSString *)name
                                                     withId:(id)value {
  if (value == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:[NSString stringWithFormat:@"Parameter value can't be null. Instead of '{} = ?' use '{} is null'. For: %@", name]];
  }
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:name];
  [((JavaUtilArrayList *) nil_chk(paramValues_)) addWithId:value];
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)offsetWithInt:(int)offset {
  self->offset_ = offset;
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)limitWithInt:(int)limit {
  self->limit_ = [JavaLangInteger valueOfWithInt:limit];
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)allowRefreshWithBoolean:(BOOL)allowRefresh {
  self->allowRefresh_ = allowRefresh;
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)orderByWithNSString:(NSString *)orderBy {
  self->orderBy_ = orderBy;
  return self;
}

- (FFTDatastoreTransaction_QueryBuilder *)groupByWithNSString:(NSString *)groupBy {
  self->groupBy_ = groupBy;
  return self;
}

- (FFTSQLResultList *)execute {
  if (where_ == nil && [((JavaUtilArrayList *) nil_chk(paramNames_)) size] > 0) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set params without a where clause"];
  }
  FFTSQLQuery *query = [[FFTSQLQuery alloc] initWithIOSClass:queryResultClass_ withNSStringArray:columns_];
  if (where_ != nil) {
    [query setWhereClauseWithFFTSQLWhereClause:[[FFTSQLWhereClause alloc] initWithNSString:where_ withJavaUtilArrayList:paramNames_ withJavaUtilArrayList:paramValues_]];
  }
  [query setOffsetWithInt:offset_];
  [query setLimitWithJavaLangInteger:limit_];
  [query setAllowRefreshWithBoolean:allowRefresh_];
  [query setOrderByWithNSString:orderBy_];
  [query setGroupByWithNSString:groupBy_];
  return [((id<FFTDatastoreService>) nil_chk(this$1_->ds_)) queryWithFFTSQLQuery:query];
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_QueryBuilder *)other {
  [super copyAllFieldsTo:other];
  other->columns_ = columns_;
  other->queryResultClass_ = queryResultClass_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:withIOSClass:", "QueryBuilder", NULL, 0x1, NULL },
    { "selectWithNSStringArray:", "select", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x81, NULL },
    { "selectColumnsWithNSStringArray:", "selectColumns", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "whereWithNSString:", "where", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "paramWithNSString:withId:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "offsetWithInt:", "offset", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "limitWithInt:", "limit", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "allowRefreshWithBoolean:", "allowRefresh", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "orderByWithNSString:", "orderBy", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "groupByWithNSString:", "groupBy", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryBuilder;", 0x1, NULL },
    { "execute", NULL, "Lcom.sponberg.fluid.datastore.SQLResultList;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$1_", NULL, 0x1012, "Lcom.sponberg.fluid.datastore.DatastoreTransaction;", NULL,  },
    { "queryResultClass_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "columns_", NULL, 0x0, "[Ljava.lang.String;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_QueryBuilder = { "QueryBuilder", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 10, methods, 3, fields, 0, NULL};
  return &_FFTDatastoreTransaction_QueryBuilder;
}

@end

@implementation FFTDatastoreTransaction_QueryFunctionBuilder

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$
                         withNSString:(NSString *)aggregateFunction
                         withNSString:(NSString *)table {
  this$1_ = outer$;
  if (self = [super initWithFFTDatastoreTransaction:outer$]) {
    self->aggregateFunction_ = aggregateFunction;
    self->table_ = table;
  }
  return self;
}

- (FFTDatastoreTransaction_QueryFunctionBuilder *)whereWithNSString:(NSString *)where {
  self->where_ = where;
  return self;
}

- (FFTDatastoreTransaction_QueryFunctionBuilder *)paramWithNSString:(NSString *)name
                                                             withId:(id)value {
  if (where_ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  if (value == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Where parameter value can't be null. Instead of '= ?' use 'is null'"];
  }
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:name];
  [((JavaUtilArrayList *) nil_chk(paramValues_)) addWithId:value];
  return self;
}

- (FFTDatastoreTransaction_QueryFunctionBuilder *)offsetWithInt:(int)offset {
  self->offset_ = offset;
  return self;
}

- (FFTDatastoreTransaction_QueryFunctionBuilder *)limitWithInt:(int)limit {
  self->limit_ = [JavaLangInteger valueOfWithInt:limit];
  return self;
}

- (FFTSQLResultList *)execute {
  FFTSQLQuery *query = [[FFTSQLQuery alloc] initWithNSString:table_ withIOSClass:[IOSClass classWithClass:[FFTSQLQueryResultDefault class]] withNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ aggregateFunction_ } count:1 type:[IOSClass classWithClass:[NSString class]]]];
  if (where_ != nil) {
    [query setWhereClauseWithFFTSQLWhereClause:[[FFTSQLWhereClause alloc] initWithNSString:where_ withJavaUtilArrayList:paramNames_ withJavaUtilArrayList:paramValues_]];
  }
  [query setOffsetWithInt:offset_];
  [query setLimitWithJavaLangInteger:limit_];
  return [((id<FFTDatastoreService>) nil_chk(this$1_->ds_)) queryWithFFTSQLQuery:query];
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_QueryFunctionBuilder *)other {
  [super copyAllFieldsTo:other];
  other->aggregateFunction_ = aggregateFunction_;
  other->table_ = table_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:withNSString:withNSString:", "QueryFunctionBuilder", NULL, 0x1, NULL },
    { "whereWithNSString:", "where", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryFunctionBuilder;", 0x1, NULL },
    { "paramWithNSString:withId:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryFunctionBuilder;", 0x1, NULL },
    { "offsetWithInt:", "offset", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryFunctionBuilder;", 0x1, NULL },
    { "limitWithInt:", "limit", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryFunctionBuilder;", 0x1, NULL },
    { "execute", NULL, "Lcom.sponberg.fluid.datastore.SQLResultList;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$1_", NULL, 0x1012, "Lcom.sponberg.fluid.datastore.DatastoreTransaction;", NULL,  },
    { "aggregateFunction_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "table_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_QueryFunctionBuilder = { "QueryFunctionBuilder", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 6, methods, 3, fields, 0, NULL};
  return &_FFTDatastoreTransaction_QueryFunctionBuilder;
}

@end

@implementation FFTDatastoreTransaction_QueryJoinBuilder

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$
                         withIOSClass:(IOSClass *)queryResultClass
                         withIOSClass:(IOSClass *)queryResultClass2 {
  this$1_ = outer$;
  if (self = [super initWithFFTDatastoreTransaction:outer$]) {
    columnsByTablename_ = [[JavaUtilLinkedHashMap alloc] init];
    self->queryResultClass_ = queryResultClass;
    self->queryResultClass2_ = queryResultClass2;
    (void) [((JavaUtilLinkedHashMap *) nil_chk(columnsByTablename_)) putWithId:FFTSQLQueryJoinBase_get_kNoTableName_() withId:[[JavaUtilArrayList alloc] init]];
    (void) [columnsByTablename_ putWithId:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass] withId:[[JavaUtilArrayList alloc] init]];
    (void) [columnsByTablename_ putWithId:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass2] withId:[[JavaUtilArrayList alloc] init]];
  }
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)selectWithIOSClass:(IOSClass *)queryResultClass
                                               withNSStringArray:(IOSObjectArray *)columns {
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  {
    IOSObjectArray *a__ = columns;
    NSString * const *b__ = ((IOSObjectArray *) nil_chk(a__))->buffer_;
    NSString * const *e__ = b__ + a__->size_;
    while (b__ < e__) {
      NSString *c = (*b__++);
      if ([((NSString *) nil_chk(c)) contains:@","]) {
        @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Column name (or function) must not contain ','"];
      }
      [((JavaUtilArrayList *) nil_chk([((JavaUtilLinkedHashMap *) nil_chk(columnsByTablename_)) getWithId:tableName])) addWithId:c];
    }
  }
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)whereWithNSString:(NSString *)where {
  self->where_ = where;
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)paramWithIOSClass:(IOSClass *)queryResultClass
                                                   withNSString:(NSString *)name
                                                         withId:(id)value {
  if (where_ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  if (value == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Where parameter value can't be null. Instead of '= ?' use 'is null'"];
  }
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:[NSString stringWithFormat:@"%@.%@", tableName, name]];
  [((JavaUtilArrayList *) nil_chk(paramValues_)) addWithId:value];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)paramWithIOSClass:(IOSClass *)queryResultClass
                                                   withNSString:(NSString *)name
                                                   withIOSClass:(IOSClass *)queryResultClass2
                                                   withNSString:(NSString *)name2 {
  if (where_ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  NSString *tableName2 = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass2];
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:[NSString stringWithFormat:@"%@.%@", tableName, name]];
  [paramNames_ addWithId:[NSString stringWithFormat:@"%@.%@", tableName2, name2]];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)offsetWithInt:(int)offset {
  self->offset_ = offset;
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)limitWithInt:(int)limit {
  self->limit_ = [JavaLangInteger valueOfWithInt:limit];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder *)orderByWithNSString:(NSString *)orderBy {
  self->orderBy_ = orderBy;
  return self;
}

- (FFTSQLResultList *)execute {
  FFTSQLQueryJoin *query = [[FFTSQLQueryJoin alloc] initWithIOSClass:queryResultClass_ withIOSClass:queryResultClass2_ withJavaUtilLinkedHashMap:columnsByTablename_];
  if (where_ != nil) {
    [query setWhereClauseWithFFTSQLWhereClause:[[FFTSQLWhereClause alloc] initWithNSString:where_ withJavaUtilArrayList:paramNames_ withJavaUtilArrayList:paramValues_]];
  }
  [query setOffsetWithInt:offset_];
  [query setLimitWithJavaLangInteger:limit_];
  [query setOrderByWithNSString:orderBy_];
  return [((id<FFTDatastoreService>) nil_chk(this$1_->ds_)) queryWithFFTSQLQueryJoin:query];
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_QueryJoinBuilder *)other {
  [super copyAllFieldsTo:other];
  other->columnsByTablename_ = columnsByTablename_;
  other->queryResultClass_ = queryResultClass_;
  other->queryResultClass2_ = queryResultClass2_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:withIOSClass:withIOSClass:", "QueryJoinBuilder", NULL, 0x1, NULL },
    { "selectWithIOSClass:withNSStringArray:", "select", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x81, NULL },
    { "whereWithNSString:", "where", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "paramWithIOSClass:withNSString:withId:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "paramWithIOSClass:withNSString:withIOSClass:withNSString:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "offsetWithInt:", "offset", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "limitWithInt:", "limit", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "orderByWithNSString:", "orderBy", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder;", 0x1, NULL },
    { "execute", NULL, "Lcom.sponberg.fluid.datastore.SQLResultList;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$1_", NULL, 0x1012, "Lcom.sponberg.fluid.datastore.DatastoreTransaction;", NULL,  },
    { "queryResultClass_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "queryResultClass2_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "columnsByTablename_", NULL, 0x0, "Ljava.util.LinkedHashMap;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_QueryJoinBuilder = { "QueryJoinBuilder", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 9, methods, 4, fields, 0, NULL};
  return &_FFTDatastoreTransaction_QueryJoinBuilder;
}

@end

@implementation FFTDatastoreTransaction_QueryJoinBuilder3

- (id)initWithFFTDatastoreTransaction:(FFTDatastoreTransaction *)outer$
                         withIOSClass:(IOSClass *)queryResultClass
                         withIOSClass:(IOSClass *)queryResultClass2
                         withIOSClass:(IOSClass *)queryResultClass3 {
  this$1_ = outer$;
  if (self = [super initWithFFTDatastoreTransaction:outer$]) {
    columnsByTablename_ = [[JavaUtilLinkedHashMap alloc] init];
    self->queryResultClass_ = queryResultClass;
    self->queryResultClass2_ = queryResultClass2;
    self->queryResultClass3_ = queryResultClass3;
    (void) [((JavaUtilLinkedHashMap *) nil_chk(columnsByTablename_)) putWithId:FFTSQLQueryJoinBase_get_kNoTableName_() withId:[[JavaUtilArrayList alloc] init]];
    (void) [columnsByTablename_ putWithId:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass] withId:[[JavaUtilArrayList alloc] init]];
    (void) [columnsByTablename_ putWithId:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass2] withId:[[JavaUtilArrayList alloc] init]];
    (void) [columnsByTablename_ putWithId:[FFTSQLUtil getTableNameWithIOSClass:queryResultClass3] withId:[[JavaUtilArrayList alloc] init]];
  }
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)selectWithIOSClass:(IOSClass *)queryResultClass
                                                withNSStringArray:(IOSObjectArray *)columns {
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  {
    IOSObjectArray *a__ = columns;
    NSString * const *b__ = ((IOSObjectArray *) nil_chk(a__))->buffer_;
    NSString * const *e__ = b__ + a__->size_;
    while (b__ < e__) {
      NSString *c = (*b__++);
      if ([((NSString *) nil_chk(c)) contains:@","]) {
        @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Column name (or function) must not contain ','"];
      }
      [((JavaUtilArrayList *) nil_chk([((JavaUtilLinkedHashMap *) nil_chk(columnsByTablename_)) getWithId:tableName])) addWithId:c];
    }
  }
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)whereWithNSString:(NSString *)where {
  self->where_ = where;
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)paramWithIOSClass:(IOSClass *)queryResultClass
                                                    withNSString:(NSString *)name
                                                          withId:(id)value {
  if (where_ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  if (value == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Where parameter value can't be null. Instead of '= ?' use 'is null'"];
  }
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:[NSString stringWithFormat:@"%@.%@", tableName, name]];
  [((JavaUtilArrayList *) nil_chk(paramValues_)) addWithId:value];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)paramWithIOSClass:(IOSClass *)queryResultClass
                                                    withNSString:(NSString *)name
                                                    withIOSClass:(IOSClass *)queryResultClass2
                                                    withNSString:(NSString *)name2 {
  if (where_ == nil) {
    @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Can't set a where param until where has been defined"];
  }
  NSString *tableName = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass];
  NSString *tableName2 = [FFTSQLUtil getTableNameWithIOSClass:queryResultClass2];
  [((JavaUtilArrayList *) nil_chk(paramNames_)) addWithId:[NSString stringWithFormat:@"%@.%@", tableName, name]];
  [paramNames_ addWithId:[NSString stringWithFormat:@"%@.%@", tableName2, name2]];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)offsetWithInt:(int)offset {
  self->offset_ = offset;
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)limitWithInt:(int)limit {
  self->limit_ = [JavaLangInteger valueOfWithInt:limit];
  return self;
}

- (FFTDatastoreTransaction_QueryJoinBuilder3 *)orderByWithNSString:(NSString *)orderBy {
  self->orderBy_ = orderBy;
  return self;
}

- (FFTSQLResultList *)execute {
  FFTSQLQueryJoin3 *query = [[FFTSQLQueryJoin3 alloc] initWithIOSClass:queryResultClass_ withIOSClass:queryResultClass2_ withIOSClass:queryResultClass3_ withJavaUtilLinkedHashMap:columnsByTablename_];
  if (where_ != nil) {
    [query setWhereClauseWithFFTSQLWhereClause:[[FFTSQLWhereClause alloc] initWithNSString:where_ withJavaUtilArrayList:paramNames_ withJavaUtilArrayList:paramValues_]];
  }
  [query setOffsetWithInt:offset_];
  [query setLimitWithJavaLangInteger:limit_];
  [query setOrderByWithNSString:orderBy_];
  return [((id<FFTDatastoreService>) nil_chk(this$1_->ds_)) queryWithFFTSQLQueryJoin3:query];
}

- (void)copyAllFieldsTo:(FFTDatastoreTransaction_QueryJoinBuilder3 *)other {
  [super copyAllFieldsTo:other];
  other->columnsByTablename_ = columnsByTablename_;
  other->queryResultClass_ = queryResultClass_;
  other->queryResultClass2_ = queryResultClass2_;
  other->queryResultClass3_ = queryResultClass3_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTDatastoreTransaction:withIOSClass:withIOSClass:withIOSClass:", "QueryJoinBuilder3", NULL, 0x1, NULL },
    { "selectWithIOSClass:withNSStringArray:", "select", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x81, NULL },
    { "whereWithNSString:", "where", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "paramWithIOSClass:withNSString:withId:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "paramWithIOSClass:withNSString:withIOSClass:withNSString:", "param", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "offsetWithInt:", "offset", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "limitWithInt:", "limit", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "orderByWithNSString:", "orderBy", "Lcom.sponberg.fluid.datastore.DatastoreTransaction$QueryJoinBuilder3;", 0x1, NULL },
    { "execute", NULL, "Lcom.sponberg.fluid.datastore.SQLResultList;", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$1_", NULL, 0x1012, "Lcom.sponberg.fluid.datastore.DatastoreTransaction;", NULL,  },
    { "queryResultClass_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "queryResultClass2_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "queryResultClass3_", NULL, 0x0, "Ljava.lang.Class;", NULL,  },
    { "columnsByTablename_", NULL, 0x0, "Ljava.util.LinkedHashMap;", NULL,  },
  };
  static J2ObjcClassInfo _FFTDatastoreTransaction_QueryJoinBuilder3 = { "QueryJoinBuilder3", "com.sponberg.fluid.datastore", "DatastoreTransaction", 0x1, 9, methods, 5, fields, 0, NULL};
  return &_FFTDatastoreTransaction_QueryJoinBuilder3;
}

@end
