//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/Color.java
//

#ifndef _FFTColor_H_
#define _FFTColor_H_

@class IOSObjectArray;

#import "JreEmulation.h"

@interface FFTColor : NSObject {
 @public
  double red_;
  double green_;
  double blue_;
  double alpha_;
}

- (id)initWithDouble:(double)red
          withDouble:(double)green
          withDouble:(double)blue
          withDouble:(double)alpha;

- (id)initWithInt:(int)red
          withInt:(int)green
          withInt:(int)blue
          withInt:(int)alpha;

- (id)initWithNSStringArray:(IOSObjectArray *)rgb;

+ (int)getAlphaWithNSStringArray:(IOSObjectArray *)rgb;

+ (FFTColor *)colorFromStringWithNSString:(NSString *)colorAsString;

+ (FFTColor *)getDefaultColor;

- (NSString *)getHtml;

- (NSString *)description;

- (double)getRed;

- (double)getGreen;

- (double)getBlue;

- (double)getAlpha;

- (BOOL)isEqual:(id)o;

- (BOOL)canEqualWithId:(id)other;

- (NSUInteger)hash;

- (void)copyAllFieldsTo:(FFTColor *)other;

@end

__attribute__((always_inline)) inline void FFTColor_init() {}

typedef FFTColor ComSponbergFluidLayoutColor;

#endif // _FFTColor_H_
