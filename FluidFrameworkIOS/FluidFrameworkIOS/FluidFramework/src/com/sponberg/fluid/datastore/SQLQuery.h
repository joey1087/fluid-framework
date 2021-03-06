//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLQuery.java
//

#ifndef _FFTSQLQuery_H_
#define _FFTSQLQuery_H_

@class FFTSQLParameterizedStatement;
@class FFTSQLResultList;
@class FFTSQLWhereClause;
@class IOSByteArray;
@class IOSClass;
@class IOSObjectArray;
@class JavaLangDouble;
@class JavaLangInteger;
@protocol FFTSQLQueryResult;

#import "JreEmulation.h"
#include "com/sponberg/fluid/datastore/SQLExecutableQuery.h"
#include "com/sponberg/fluid/datastore/SQLStatement.h"

@interface FFTSQLQuery : NSObject < FFTSQLStatement, FFTSQLExecutableQuery > {
 @public
  NSString *tableName_;
  IOSObjectArray *selectColumns_;
  FFTSQLWhereClause *whereClause_;
  NSString *selectStatement_;
  int offset_;
  BOOL allowRefresh_;
  JavaLangInteger *limit_;
  IOSClass *queryResultClass_;
  FFTSQLResultList *results_;
  NSString *orderBy_;
  id<FFTSQLQueryResult> result_;
}

- (id)initWithIOSClass:(IOSClass *)queryResultClass
     withNSStringArray:(IOSObjectArray *)selectColumns;

- (id)initWithNSString:(NSString *)tableName
          withIOSClass:(IOSClass *)queryResultClass
     withNSStringArray:(IOSObjectArray *)selectColumns;

- (void)setWhereClauseWithFFTSQLWhereClause:(FFTSQLWhereClause *)whereClause;

- (void)setWhereWithNSString:(NSString *)where;

- (FFTSQLWhereClause *)getWhere;

- (void)setSelectStatementWithNSString:(NSString *)selectStatement;

- (FFTSQLParameterizedStatement *)getParameterizedStatement;

- (void)addResult;

- (void)setNullWithInt:(int)columnIndex
          withNSString:(NSString *)columnName;

- (void)setIntegerWithInt:(int)columnIndex
             withNSString:(NSString *)columnName
      withJavaLangInteger:(JavaLangInteger *)value;

- (void)setDoubleWithInt:(int)columnIndex
            withNSString:(NSString *)columnName
      withJavaLangDouble:(JavaLangDouble *)value;

- (void)setStringWithInt:(int)columnIndex
            withNSString:(NSString *)columnName
            withNSString:(NSString *)value;

- (void)setBinaryWithInt:(int)columnIndex
            withNSString:(NSString *)columnName
           withByteArray:(IOSByteArray *)value;

- (void)stepQuery;

- (NSString *)getTableName;

- (IOSObjectArray *)getSelectColumns;

- (FFTSQLWhereClause *)getWhereClause;

- (int)getOffset;

- (BOOL)isAllowRefresh;

- (JavaLangInteger *)getLimit;

- (IOSClass *)getQueryResultClass;

- (FFTSQLResultList *)getResults;

- (NSString *)getOrderBy;

- (id)getResult;

- (void)setTableNameWithNSString:(NSString *)tableName;

- (void)setOffsetWithInt:(int)offset;

- (void)setAllowRefreshWithBoolean:(BOOL)allowRefresh;

- (void)setLimitWithJavaLangInteger:(JavaLangInteger *)limit;

- (void)setQueryResultClassWithIOSClass:(IOSClass *)queryResultClass;

- (void)setResultsWithFFTSQLResultList:(FFTSQLResultList *)results;

- (void)setOrderByWithNSString:(NSString *)orderBy;

- (void)setResultWithId:(id<FFTSQLQueryResult>)result;

- (NSString *)description;

- (void)copyAllFieldsTo:(FFTSQLQuery *)other;

@end

__attribute__((always_inline)) inline void FFTSQLQuery_init() {}

J2OBJC_FIELD_SETTER(FFTSQLQuery, tableName_, NSString *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, selectColumns_, IOSObjectArray *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, whereClause_, FFTSQLWhereClause *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, selectStatement_, NSString *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, limit_, JavaLangInteger *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, queryResultClass_, IOSClass *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, results_, FFTSQLResultList *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, orderBy_, NSString *)
J2OBJC_FIELD_SETTER(FFTSQLQuery, result_, id)

typedef FFTSQLQuery ComSponbergFluidDatastoreSQLQuery;

#endif // _FFTSQLQuery_H_
