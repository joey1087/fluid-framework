//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/app/ui/PickWhen.java
//

#ifndef _FAPickWhen_H_
#define _FAPickWhen_H_

@class FFTActionListener_EventInfo;
@class FFTFluidApp;
@class IOSObjectArray;

#import "JreEmulation.h"
#include "com/sponberg/fluid/ApplicationLoader.h"
#include "com/sponberg/fluid/layout/ActionListenerAdapter.h"

@interface FAPickWhen : NSObject < FFTApplicationLoader > {
}

- (void)load__WithFFTFluidApp:(FFTFluidApp *)fApp;

- (void)userPickedWithLong:(long long int)whenId;

- (IOSObjectArray *)getSupportedPlatforms;

- (id)init;

@end

__attribute__((always_inline)) inline void FAPickWhen_init() {}

typedef FAPickWhen ComSponbergAppUiPickWhen;

@interface FAPickWhen_$1 : FFTActionListenerAdapter {
 @public
  FAPickWhen *this$0_;
}

- (void)userTappedWithFFTActionListener_EventInfo:(FFTActionListener_EventInfo *)info;

- (id)initWithFAPickWhen:(FAPickWhen *)outer$;

@end

__attribute__((always_inline)) inline void FAPickWhen_$1_init() {}

J2OBJC_FIELD_SETTER(FAPickWhen_$1, this$0_, FAPickWhen *)

#endif // _FAPickWhen_H_
