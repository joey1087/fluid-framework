//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/datastore/SQLResultList.java
//

#include "IOSClass.h"
#include "com/sponberg/fluid/datastore/DatastoreException.h"
#include "com/sponberg/fluid/datastore/SQLExecutableQuery.h"
#include "com/sponberg/fluid/datastore/SQLResultList.h"
#include "java/lang/Integer.h"
#include "java/lang/RuntimeException.h"
#include "java/util/Iterator.h"
#include "java/util/LinkedList.h"
#include "java/util/NoSuchElementException.h"

@implementation FFTSQLResultList

- (id)initWithFFTSQLExecutableQuery:(id<FFTSQLExecutableQuery>)query {
  if (self = [super init]) {
    linkedList_ = [[JavaUtilLinkedList alloc] init];
    endReached_ = NO;
    self->query_ = query;
  }
  return self;
}

- (void)addWithId:(id)result {
  [((JavaUtilLinkedList *) nil_chk(linkedList_)) addWithId:result];
}

- (id<JavaUtilIterator>)iterator {
  return self;
}

- (BOOL)hasNext {
  if (endReached_) {
    return NO;
  }
  else if ([((JavaUtilLinkedList *) nil_chk(linkedList_)) size] > 0) {
    return YES;
  }
  [self refreshResults];
  if ([((JavaUtilLinkedList *) nil_chk(linkedList_)) size] == 0) {
    endReached_ = YES;
  }
  return !endReached_;
}

- (id)next {
  if (endReached_) {
    @throw [[JavaUtilNoSuchElementException alloc] init];
  }
  else if ([((JavaUtilLinkedList *) nil_chk(linkedList_)) size] == 0) {
    [self refreshResults];
  }
  if ([((JavaUtilLinkedList *) nil_chk(linkedList_)) size] > 0) {
    return [linkedList_ poll];
  }
  else {
    endReached_ = YES;
    @throw [[JavaUtilNoSuchElementException alloc] init];
  }
}

- (void)remove {
  @throw [[JavaLangRuntimeException alloc] initWithNSString:@"Not supported"];
}

- (void)refreshResults {
  if ([((id<FFTSQLExecutableQuery>) nil_chk(query_)) getLimit] == nil) {
    return;
  }
  if (![query_ isAllowRefresh]) {
    return;
  }
  int offset = [query_ getOffset];
  offset += [((JavaLangInteger *) nil_chk([query_ getLimit])) intValue];
  [query_ setOffsetWithInt:offset];
  [query_ stepQuery];
}

- (int)size {
  return [((JavaUtilLinkedList *) nil_chk(linkedList_)) size];
}

- (id)getWithInt:(int)i {
  return [((JavaUtilLinkedList *) nil_chk(linkedList_)) getWithInt:i];
}

- (void)copyAllFieldsTo:(FFTSQLResultList *)other {
  [super copyAllFieldsTo:other];
  other->endReached_ = endReached_;
  other->linkedList_ = linkedList_;
  other->query_ = query_;
}
- (NSUInteger)countByEnumeratingWithState:(NSFastEnumerationState *)state objects:(__unsafe_unretained id *)stackbuf count:(NSUInteger)len {
  return JreDefaultFastEnumeration(self, state, stackbuf, len);
}


+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTSQLExecutableQuery:", "SQLResultList", NULL, 0x1, NULL },
    { "addWithId:", "add", "V", 0x1, NULL },
    { "iterator", NULL, "Ljava.util.Iterator;", 0x1, NULL },
    { "hasNext", NULL, "Z", 0x1, NULL },
    { "next", NULL, "TR;", 0x1, NULL },
    { "remove", NULL, "V", 0x1, NULL },
    { "refreshResults", NULL, "V", 0x1, "Lcom.sponberg.fluid.datastore.DatastoreException;" },
    { "size", NULL, "I", 0x1, NULL },
    { "getWithInt:", "get", "TR;", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "query_", NULL, 0x0, "Lcom.sponberg.fluid.datastore.SQLExecutableQuery;", NULL,  },
    { "linkedList_", NULL, 0x0, "Ljava.util.LinkedList;", NULL,  },
    { "endReached_", NULL, 0x0, "Z", NULL,  },
  };
  static J2ObjcClassInfo _FFTSQLResultList = { "SQLResultList", "com.sponberg.fluid.datastore", NULL, 0x1, 9, methods, 3, fields, 0, NULL};
  return &_FFTSQLResultList;
}

@end
