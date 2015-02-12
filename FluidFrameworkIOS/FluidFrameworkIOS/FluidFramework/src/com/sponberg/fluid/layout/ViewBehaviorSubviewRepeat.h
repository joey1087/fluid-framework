//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/ViewBehaviorSubviewRepeat.java
//

#ifndef _FFTViewBehaviorSubviewRepeat_H_
#define _FFTViewBehaviorSubviewRepeat_H_

@class FFTView;
@class FFTViewPosition;
@protocol FFTKeyValueList;
@protocol JavaUtilCollection;

#import "JreEmulation.h"
#include "com/sponberg/fluid/layout/ViewBehavior.h"

@interface FFTViewBehaviorSubviewRepeat : FFTViewBehavior {
 @public
  NSString *subview_ViewBehaviorSubviewRepeat_;
  NSString *key_;
}

- (id)initWithFFTKeyValueList:(id<FFTKeyValueList>)properties;

- (BOOL)supportsHeightCompute;

- (float)computeHeightWithBoolean:(BOOL)landscape
                     withNSString:(NSString *)dataModelPrefix
                      withFFTView:(FFTView *)view
                      withBoolean:(BOOL)useCache;

- (void)precomputeViewPositionsWithBoolean:(BOOL)landscape
                              withNSString:(NSString *)precomputePrefix
                       withFFTViewPosition:(FFTViewPosition *)view
                              withNSString:(NSString *)viewPathPrefixView
                    withJavaUtilCollection:(id<JavaUtilCollection>)newViewPositions;

- (NSString *)description;

- (NSString *)getSubview;

- (NSString *)getKey;

- (void)copyAllFieldsTo:(FFTViewBehaviorSubviewRepeat *)other;

@end

__attribute__((always_inline)) inline void FFTViewBehaviorSubviewRepeat_init() {}

J2OBJC_FIELD_SETTER(FFTViewBehaviorSubviewRepeat, subview_ViewBehaviorSubviewRepeat_, NSString *)
J2OBJC_FIELD_SETTER(FFTViewBehaviorSubviewRepeat, key_, NSString *)

typedef FFTViewBehaviorSubviewRepeat ComSponbergFluidLayoutViewBehaviorSubviewRepeat;

#endif // _FFTViewBehaviorSubviewRepeat_H_
