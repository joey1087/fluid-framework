//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLUpdate.java
//

#ifndef _FFTSQLUpdate_H_
#define _FFTSQLUpdate_H_

@class FFTSQLParameterizedStatement;
@class FFTSQLWhereClause;
@protocol FFTSQLDataInput;
@protocol FFTSQLTable;

#import "JreEmulation.h"
#include "com/sponberg/fluid/datastore/SQLStatement.h"

@interface FFTSQLUpdate : NSObject < FFTSQLStatement > {
 @public
  id<FFTSQLDataInput, FFTSQLTable> object_;
  FFTSQLWhereClause *whereClause_;
}

- (id)initWithId:(id<FFTSQLDataInput, FFTSQLTable>)object;

- (void)setWhereClauseWithFFTSQLWhereClause:(FFTSQLWhereClause *)whereClause;

- (void)setWhereWithNSString:(NSString *)where;

- (FFTSQLWhereClause *)getWhere;

- (FFTSQLParameterizedStatement *)getParameterizedStatement;

- (NSString *)getTable;

- (NSString *)description;

- (void)copyAllFieldsTo:(FFTSQLUpdate *)other;

@end

__attribute__((always_inline)) inline void FFTSQLUpdate_init() {}

J2OBJC_FIELD_SETTER(FFTSQLUpdate, object_, id)
J2OBJC_FIELD_SETTER(FFTSQLUpdate, whereClause_, FFTSQLWhereClause *)

typedef FFTSQLUpdate ComSponbergFluidDatastoreSQLUpdate;

#endif // _FFTSQLUpdate_H_
