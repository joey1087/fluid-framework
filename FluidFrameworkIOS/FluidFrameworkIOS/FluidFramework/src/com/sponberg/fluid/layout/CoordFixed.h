//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/CoordFixed.java
//

#ifndef _FFTCoordFixed_H_
#define _FFTCoordFixed_H_

@class JavaLangDouble;

#import "JreEmulation.h"
#include "com/sponberg/fluid/layout/Coord.h"

@interface FFTCoordFixed : FFTCoord {
 @public
  double l_;
}

- (id)initWithDouble:(double)l;

- (JavaLangDouble *)getFixed;

- (void)copyAllFieldsTo:(FFTCoordFixed *)other;

@end

__attribute__((always_inline)) inline void FFTCoordFixed_init() {}

typedef FFTCoordFixed ComSponbergFluidLayoutCoordFixed;

#endif // _FFTCoordFixed_H_
