//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/ScreenListenerAdapter.java
//

#ifndef _FFTScreenListenerAdapter_H_
#define _FFTScreenListenerAdapter_H_

#import "JreEmulation.h"
#include "com/sponberg/fluid/layout/ScreenListener.h"

@interface FFTScreenListenerAdapter : NSObject < FFTScreenListener > {
}

- (void)screenWillAppear;

- (void)screenDidAppear;

- (void)screenDidDisappear;

- (id)init;

@end

__attribute__((always_inline)) inline void FFTScreenListenerAdapter_init() {}

typedef FFTScreenListenerAdapter ComSponbergFluidLayoutScreenListenerAdapter;

#endif // _FFTScreenListenerAdapter_H_
