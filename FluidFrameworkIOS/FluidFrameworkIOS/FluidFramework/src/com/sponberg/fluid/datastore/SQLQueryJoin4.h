//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLQueryJoin4.java
//

#ifndef _FFTSQLQueryJoin4_H_
#define _FFTSQLQueryJoin4_H_

@class FFTSQLQueryResultTuple4;
@class FFTSQLResultList;
@class IOSClass;
@class JavaUtilLinkedHashMap;
@protocol FFTSQLQueryResult;

#import "JreEmulation.h"
#include "com/sponberg/fluid/datastore/SQLQueryJoinBase.h"

@interface FFTSQLQueryJoin4 : FFTSQLQueryJoinBase {
 @public
  FFTSQLResultList *results_;
  FFTSQLQueryResultTuple4 *tuple_;
}

- (id)initWithIOSClass:(IOSClass *)queryResultClass
          withIOSClass:(IOSClass *)queryResultClass2
          withIOSClass:(IOSClass *)queryResultClass3
          withIOSClass:(IOSClass *)queryResultClass4
withJavaUtilLinkedHashMap:(JavaUtilLinkedHashMap *)columnsByTableName;

- (void)addResult;

- (id<FFTSQLQueryResult>)getCurrentTupleResultWithInt:(int)resultIndex;

- (void)stepQuery;

- (FFTSQLResultList *)getResults;

- (FFTSQLQueryResultTuple4 *)getTuple;

- (void)setResultsWithFFTSQLResultList:(FFTSQLResultList *)results;

- (void)setTupleWithFFTSQLQueryResultTuple4:(FFTSQLQueryResultTuple4 *)tuple;

- (NSString *)description;

- (void)copyAllFieldsTo:(FFTSQLQueryJoin4 *)other;

@end

__attribute__((always_inline)) inline void FFTSQLQueryJoin4_init() {}

J2OBJC_FIELD_SETTER(FFTSQLQueryJoin4, results_, FFTSQLResultList *)
J2OBJC_FIELD_SETTER(FFTSQLQueryJoin4, tuple_, FFTSQLQueryResultTuple4 *)

typedef FFTSQLQueryJoin4 ComSponbergFluidDatastoreSQLQueryJoin4;

#endif // _FFTSQLQueryJoin4_H_
