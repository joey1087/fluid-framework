//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/GlobalState.java
//

#ifndef _FFTGlobalState_H_
#define _FFTGlobalState_H_

@class FFTFluidApp;

#import "JreEmulation.h"

@interface FFTGlobalState : NSObject {
}

+ (FFTFluidApp *)fluidApp;

- (id)init;

@end

__attribute__((always_inline)) inline void FFTGlobalState_init() {}

FOUNDATION_EXPORT FFTFluidApp *FFTGlobalState_fluidApp__;
J2OBJC_STATIC_FIELD_GETTER(FFTGlobalState, fluidApp__, FFTFluidApp *)
J2OBJC_STATIC_FIELD_SETTER(FFTGlobalState, fluidApp__, FFTFluidApp *)

typedef FFTGlobalState ComSponbergFluidGlobalState;

#endif // _FFTGlobalState_H_
