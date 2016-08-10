//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/JsonValue.java
//

#ifndef _FFTJsonValue_H_
#define _FFTJsonValue_H_

@class FFTJsonArray;
@class FFTJsonObject;
@class FFTJsonWriter;
@class FFTWriterConfig;
@class JavaIoReader;
@class JavaIoWriter;

#import "JreEmulation.h"
#include "java/io/Serializable.h"

@interface FFTJsonValue : NSObject < JavaIoSerializable > {
}

- (id)init;

+ (FFTJsonValue *)readFromWithJavaIoReader:(JavaIoReader *)reader;

+ (FFTJsonValue *)readFromWithNSString:(NSString *)text;

+ (FFTJsonValue *)valueOfWithInt:(int)value;

+ (FFTJsonValue *)valueOfWithLong:(long long int)value;

+ (FFTJsonValue *)valueOfWithFloat:(float)value;

+ (FFTJsonValue *)valueOfWithDouble:(double)value;

+ (FFTJsonValue *)valueOfWithNSString:(NSString *)string;

+ (FFTJsonValue *)valueOfWithBoolean:(BOOL)value;

- (BOOL)isObject;

- (BOOL)isArray;

- (BOOL)isNumber;

- (BOOL)isString;

- (BOOL)isBoolean;

- (BOOL)isTrue;

- (BOOL)isFalse;

- (BOOL)isNull;

- (FFTJsonObject *)asObject;

- (FFTJsonArray *)asArray;

- (int)asInt;

- (long long int)asLong;

- (float)asFloat;

- (double)asDouble;

- (NSString *)asString;

- (BOOL)asBoolean;

- (void)writeToWithJavaIoWriter:(JavaIoWriter *)writer;

- (void)writeToWithJavaIoWriter:(JavaIoWriter *)writer
            withFFTWriterConfig:(FFTWriterConfig *)config;

- (NSString *)description;

- (NSString *)toStringWithFFTWriterConfig:(FFTWriterConfig *)config;

- (BOOL)isEqual:(id)object;

- (NSUInteger)hash;

- (void)writeWithFFTJsonWriter:(FFTJsonWriter *)writer;

@end

FOUNDATION_EXPORT BOOL FFTJsonValue_initialized;
J2OBJC_STATIC_INIT(FFTJsonValue)

FOUNDATION_EXPORT FFTJsonValue *FFTJsonValue_TRUE__;
J2OBJC_STATIC_FIELD_GETTER(FFTJsonValue, TRUE__, FFTJsonValue *)

FOUNDATION_EXPORT FFTJsonValue *FFTJsonValue_FALSE__;
J2OBJC_STATIC_FIELD_GETTER(FFTJsonValue, FALSE__, FFTJsonValue *)

FOUNDATION_EXPORT FFTJsonValue *FFTJsonValue_NULL__;
J2OBJC_STATIC_FIELD_GETTER(FFTJsonValue, NULL__, FFTJsonValue *)

typedef FFTJsonValue ComEclipsesourceJsonJsonValue;

#endif // _FFTJsonValue_H_
