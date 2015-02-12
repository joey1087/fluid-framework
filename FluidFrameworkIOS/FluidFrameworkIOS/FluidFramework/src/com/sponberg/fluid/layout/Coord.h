//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/Coord.java
//

#ifndef _FFTCoord_H_
#define _FFTCoord_H_

@class FFTSubtractor;
@class JavaLangDouble;
@class JavaUtilArrayList;

#import "JreEmulation.h"

@interface FFTCoord : NSObject {
 @public
  JavaUtilArrayList *subtractors_;
}

- (void)addSubtractorWithFFTSubtractor:(FFTSubtractor *)length;

- (BOOL)isRelativeToView;

- (BOOL)isRelativeToParent;

- (BOOL)isDynamic;

- (JavaLangDouble *)getFixed;

- (NSString *)getRelativeId;

- (NSString *)getRelativeEdge;

- (id)init;

- (void)copyAllFieldsTo:(FFTCoord *)other;

@end

__attribute__((always_inline)) inline void FFTCoord_init() {}

J2OBJC_FIELD_SETTER(FFTCoord, subtractors_, JavaUtilArrayList *)

typedef FFTCoord ComSponbergFluidLayoutCoord;

#endif // _FFTCoord_H_
