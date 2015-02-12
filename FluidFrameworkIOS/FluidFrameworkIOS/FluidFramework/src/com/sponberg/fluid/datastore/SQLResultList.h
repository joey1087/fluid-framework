//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLResultList.java
//

#ifndef _FFTSQLResultList_H_
#define _FFTSQLResultList_H_

@class JavaUtilLinkedList;
@protocol FFTSQLExecutableQuery;

#import "JreEmulation.h"
#include "java/lang/Iterable.h"
#include "java/util/Iterator.h"

@interface FFTSQLResultList : NSObject < JavaLangIterable, JavaUtilIterator > {
 @public
  id<FFTSQLExecutableQuery> query_;
  JavaUtilLinkedList *linkedList_;
  BOOL endReached_;
}

- (id)initWithFFTSQLExecutableQuery:(id<FFTSQLExecutableQuery>)query;

- (void)addWithId:(id)result;

- (id<JavaUtilIterator>)iterator;

- (BOOL)hasNext;

- (id)next;

- (void)remove;

- (void)refreshResults;

- (int)size;

- (id)getWithInt:(int)i;

- (void)copyAllFieldsTo:(FFTSQLResultList *)other;

@end

__attribute__((always_inline)) inline void FFTSQLResultList_init() {}

J2OBJC_FIELD_SETTER(FFTSQLResultList, query_, id<FFTSQLExecutableQuery>)
J2OBJC_FIELD_SETTER(FFTSQLResultList, linkedList_, JavaUtilLinkedList *)

typedef FFTSQLResultList ComSponbergFluidDatastoreSQLResultList;

#endif // _FFTSQLResultList_H_
