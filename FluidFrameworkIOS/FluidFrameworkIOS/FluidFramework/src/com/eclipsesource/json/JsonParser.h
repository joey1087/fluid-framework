//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/JsonParser.java
//

#ifndef _FFTJSONJsonParser_H_
#define _FFTJSONJsonParser_H_

@class FFTJSONJsonArray;
@class FFTJSONJsonObject;
@class FFTJSONJsonValue;
@class FFTJSONParseException;
@class IOSCharArray;
@class JavaIoReader;
@class JavaLangStringBuilder;

#import "JreEmulation.h"

#define FFTJSONJsonParser_DEFAULT_BUFFER_SIZE 1024
#define FFTJSONJsonParser_MIN_BUFFER_SIZE 10

@interface FFTJSONJsonParser : NSObject {
 @public
  JavaIoReader *reader_;
  IOSCharArray *buffer_;
  int bufferOffset_;
  int index_;
  int fill_;
  int line_;
  int lineOffset_;
  int current_;
  JavaLangStringBuilder *captureBuffer_;
  int captureStart_;
}

- (id)initWithNSString:(NSString *)string;

- (id)initWithJavaIoReader:(JavaIoReader *)reader;

- (id)initWithJavaIoReader:(JavaIoReader *)reader
                   withInt:(int)buffersize;

- (FFTJSONJsonValue *)parse;

- (FFTJSONJsonValue *)readValue;

- (FFTJSONJsonArray *)readArray;

- (FFTJSONJsonObject *)readObject;

- (NSString *)readName;

- (FFTJSONJsonValue *)readNull;

- (FFTJSONJsonValue *)readTrue;

- (FFTJSONJsonValue *)readFalse;

- (void)readRequiredCharWithChar:(unichar)ch;

- (FFTJSONJsonValue *)readString;

- (NSString *)readStringInternal;

- (void)readEscape;

- (FFTJSONJsonValue *)readNumber;

- (BOOL)readFraction;

- (BOOL)readExponent;

- (BOOL)readCharWithChar:(unichar)ch;

- (BOOL)readDigit;

- (void)skipWhiteSpace;

- (void)read;

- (void)startCapture;

- (void)pauseCapture;

- (NSString *)endCapture;

- (FFTJSONParseException *)expectedWithNSString:(NSString *)expected;

- (FFTJSONParseException *)errorWithNSString:(NSString *)message;

- (BOOL)isWhiteSpace;

- (BOOL)isDigit;

- (BOOL)isHexDigit;

- (BOOL)isEndOfText;

- (void)copyAllFieldsTo:(FFTJSONJsonParser *)other;

@end

__attribute__((always_inline)) inline void FFTJSONJsonParser_init() {}

J2OBJC_FIELD_SETTER(FFTJSONJsonParser, reader_, JavaIoReader *)
J2OBJC_FIELD_SETTER(FFTJSONJsonParser, buffer_, IOSCharArray *)
J2OBJC_FIELD_SETTER(FFTJSONJsonParser, captureBuffer_, JavaLangStringBuilder *)

J2OBJC_STATIC_FIELD_GETTER(FFTJSONJsonParser, MIN_BUFFER_SIZE, int)

J2OBJC_STATIC_FIELD_GETTER(FFTJSONJsonParser, DEFAULT_BUFFER_SIZE, int)

typedef FFTJSONJsonParser ComEclipsesourceJsonJsonParser;

#endif // _FFTJSONJsonParser_H_
