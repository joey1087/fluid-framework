//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/tracking/ITrackingUtil.java
//

#ifndef _ComSponbergFluidTrackingITrackingUtil_H_
#define _ComSponbergFluidTrackingITrackingUtil_H_

#import "JreEmulation.h"

@protocol ComSponbergFluidTrackingITrackingUtil < NSObject, JavaObject >

- (void)sendPageViewWithNSString:(NSString *)page;

- (void)sendEventWithNSString:(NSString *)Category
                 withNSString:(NSString *)Action
                 withNSString:(NSString *)Label;

@end

__attribute__((always_inline)) inline void ComSponbergFluidTrackingITrackingUtil_init() {}

#endif // _ComSponbergFluidTrackingITrackingUtil_H_
