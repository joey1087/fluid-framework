//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/util/KVLReader.java
//

#ifndef _FFTKVLReader_H_
#define _FFTKVLReader_H_

@class JavaIoBufferedReader;
@class JavaUtilHashMap;
@class JavaUtilStack;
@protocol FFTKeyValueList;
@protocol JavaUtilList;
@protocol JavaUtilSet;

#import "JreEmulation.h"
#include "com/sponberg/fluid/util/KeyValueListModifyable.h"

@interface FFTKVLReader : NSObject < FFTKeyValueListModifyable > {
 @public
  id<FFTKeyValueListModifyable> root_;
}

- (id)initWithNSString:(NSString *)data;

- (id)initWithJavaIoBufferedReader:(JavaIoBufferedReader *)inArg;

- (void)init__WithJavaIoBufferedReader:(JavaIoBufferedReader *)inArg OBJC_METHOD_FAMILY_NONE;

- (id<JavaUtilList>)getWithNSString:(NSString *)key;

- (id<FFTKeyValueList>)getWithValueWithNSString:(NSString *)key
                                   withNSString:(NSString *)value;

- (BOOL)containsWithNSString:(NSString *)key;

- (id<JavaUtilSet>)keys;

- (NSString *)getValueWithNSString:(NSString *)key;

- (id<JavaUtilList>)getValuesWithNSString:(NSString *)key;

- (void)addWithNSString:(NSString *)key
    withFFTKeyValueList:(id<FFTKeyValueList>)newKvl;

- (void)removeWithNSString:(NSString *)key;

- (void)removeByValueWithNSString:(NSString *)key
                     withNSString:(NSString *)value;

- (void)setToValueWithNSString:(NSString *)key
           withFFTKeyValueList:(id<FFTKeyValueList>)newKvl;

- (NSString *)getValue;

- (void)overwriteSettingsFromWithFFTKeyValueListModifyable:(id<FFTKeyValueListModifyable>)fromReader;

- (void)overwriteSettingsWithJavaUtilStack:(JavaUtilStack *)keys
             withFFTKeyValueListModifyable:(id<FFTKeyValueListModifyable>)fromReader
             withFFTKeyValueListModifyable:(id<FFTKeyValueListModifyable>)toReaderRoot;

- (void)writeValueWithFFTKeyValueListModifyable:(id<FFTKeyValueListModifyable>)writeToKvl
                                        withInt:(int)index
                              withJavaUtilStack:(JavaUtilStack *)keys
                                   withNSString:(NSString *)value;

- (id<FFTKeyValueListModifyable>)findOnListByValueWithJavaUtilList:(id<JavaUtilList>)list
                                                      withNSString:(NSString *)value;

- (id<JavaUtilList>)getOrReturnEmptyWithFFTKeyValueList:(id<FFTKeyValueList>)list
                                           withNSString:(NSString *)key;

- (void)copyAllFieldsTo:(FFTKVLReader *)other;

@end

FOUNDATION_EXPORT BOOL FFTKVLReader_initialized;
J2OBJC_STATIC_INIT(FFTKVLReader)

J2OBJC_FIELD_SETTER(FFTKVLReader, root_, id<FFTKeyValueListModifyable>)

FOUNDATION_EXPORT id<JavaUtilList> FFTKVLReader_emptyList_;
J2OBJC_STATIC_FIELD_GETTER(FFTKVLReader, emptyList_, id<JavaUtilList>)

FOUNDATION_EXPORT id<JavaUtilList> FFTKVLReader_emptyValueList_;
J2OBJC_STATIC_FIELD_GETTER(FFTKVLReader, emptyValueList_, id<JavaUtilList>)

typedef FFTKVLReader ComSponbergFluidUtilKVLReader;

@interface FFTKVLReader_KeyValueListDefault : NSObject < FFTKeyValueListModifyable > {
 @public
  NSString *value_;
  JavaUtilHashMap *kvl_;
}

- (id)initWithNSString:(NSString *)value;

- (void)addWithNSString:(NSString *)key
    withFFTKeyValueList:(id<FFTKeyValueList>)newKvl;

- (void)setToValueWithNSString:(NSString *)key
           withFFTKeyValueList:(id<FFTKeyValueList>)newKvl;

- (void)removeByValueWithNSString:(NSString *)key
                     withNSString:(NSString *)value;

- (id<JavaUtilList>)getWithNSString:(NSString *)key;

- (id<FFTKeyValueList>)getWithValueWithNSString:(NSString *)key
                                   withNSString:(NSString *)value;

- (BOOL)containsWithNSString:(NSString *)key;

- (id<JavaUtilSet>)keys;

- (NSString *)getValueWithNSString:(NSString *)key;

- (id<JavaUtilList>)getValuesWithNSString:(NSString *)key;

- (NSString *)getValue;

- (NSString *)description;

- (void)removeWithNSString:(NSString *)key;

- (void)copyAllFieldsTo:(FFTKVLReader_KeyValueListDefault *)other;

@end

__attribute__((always_inline)) inline void FFTKVLReader_KeyValueListDefault_init() {}

J2OBJC_FIELD_SETTER(FFTKVLReader_KeyValueListDefault, value_, NSString *)
J2OBJC_FIELD_SETTER(FFTKVLReader_KeyValueListDefault, kvl_, JavaUtilHashMap *)

#endif // _FFTKVLReader_H_
