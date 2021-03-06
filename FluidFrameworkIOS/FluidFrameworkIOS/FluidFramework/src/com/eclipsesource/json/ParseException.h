//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/ParseException.java
//

#ifndef _FFTParseException_H_
#define _FFTParseException_H_

#import "JreEmulation.h"
#include "java/lang/RuntimeException.h"

@interface FFTParseException : JavaLangRuntimeException {
 @public
  int offset_;
  int line_;
  int column_;
}

- (id)initWithNSString:(NSString *)message
               withInt:(int)offset
               withInt:(int)line
               withInt:(int)column;

- (int)getOffset;

- (int)getLine;

- (int)getColumn;

- (void)copyAllFieldsTo:(FFTParseException *)other;

@end

__attribute__((always_inline)) inline void FFTParseException_init() {}

typedef FFTParseException ComEclipsesourceJsonParseException;

#endif // _FFTParseException_H_
