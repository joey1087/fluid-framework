//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/View.java
//

#ifndef _FFTView_H_
#define _FFTView_H_

@class FFTConstraints;
@class FFTLayout;
@class FFTLayout_DirectionEnum;
@class FFTViewBehavior;
@class FFTView_OrientationProperties;
@class JavaLangDouble;
@class JavaUtilArrayList;
@protocol FFTLayoutAction;

#import "JreEmulation.h"

@interface FFTView : NSObject {
 @public
  NSString *id__;
  NSString *key_;
  NSString *visibleCondition_;
  FFTConstraints *givenConstraints_;
  FFTViewBehavior *viewBehavior_;
  JavaLangDouble *x_;
  JavaLangDouble *y_;
  JavaLangDouble *x2_;
  JavaLangDouble *y2_;
  JavaLangDouble *width_;
  JavaLangDouble *height_;
  JavaLangDouble *middleX_;
  FFTView_OrientationProperties *portrait_;
  FFTView_OrientationProperties *landscape_;
  FFTView_OrientationProperties *currentLayout_;
  FFTLayout *layout_;
  BOOL visible_;
}

- (id)initWithNSString:(NSString *)id_
          withNSString:(NSString *)key
          withNSString:(NSString *)visibleCondition
         withFFTLayout:(FFTLayout *)layout
    withFFTConstraints:(FFTConstraints *)givenConstraints
   withFFTViewBehavior:(FFTViewBehavior *)viewBehavior;

- (void)addActionXWithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)addActionX2WithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)addActionYWithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)addActionY2WithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)addActionWidthWithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)addActionHeightWithFFTLayoutAction:(id<FFTLayoutAction>)a;

- (void)runActionsWithJavaUtilArrayList:(JavaUtilArrayList *)actions;

- (void)setXWithJavaLangDouble:(JavaLangDouble *)v;

- (void)setYWithJavaLangDouble:(JavaLangDouble *)v;

- (void)setMiddleXWithJavaLangDouble:(JavaLangDouble *)v;

- (void)setX2WithJavaLangDouble:(JavaLangDouble *)v;

- (void)setY2WithJavaLangDouble:(JavaLangDouble *)y;

- (void)setWidthWithJavaLangDouble:(JavaLangDouble *)width;

- (void)setHeightWithJavaLangDouble:(JavaLangDouble *)height;

- (BOOL)isEqual:(id)o;

- (NSUInteger)hash;

- (NSString *)getValueWithNSString:(NSString *)prefix
                      withNSString:(NSString *)keys
                      withNSString:(NSString *)messageFormat;

- (void)setValueWithNSString:(NSString *)prefix
                withNSString:(NSString *)key
                      withId:(id)value;

- (void)viewDidLoad;

- (NSString *)description;

- (NSString *)getId;

- (NSString *)getKey;

- (NSString *)getVisibleCondition;

- (FFTConstraints *)getGivenConstraints;

- (FFTViewBehavior *)getViewBehavior;

- (JavaLangDouble *)getX;

- (JavaLangDouble *)getY;

- (JavaLangDouble *)getX2;

- (JavaLangDouble *)getY2;

- (JavaLangDouble *)getWidth;

- (JavaLangDouble *)getHeight;

- (JavaLangDouble *)getMiddleX;

- (FFTView_OrientationProperties *)getPortrait;

- (FFTView_OrientationProperties *)getLandscape;

- (FFTView_OrientationProperties *)getCurrentLayout;

- (FFTLayout *)getLayout;

- (BOOL)isVisible;

- (void)setPortraitWithFFTView_OrientationProperties:(FFTView_OrientationProperties *)portrait;

- (void)setLandscapeWithFFTView_OrientationProperties:(FFTView_OrientationProperties *)landscape;

- (void)setCurrentLayoutWithFFTView_OrientationProperties:(FFTView_OrientationProperties *)currentLayout;

- (void)setVisibleWithBoolean:(BOOL)visible;

- (void)copyAllFieldsTo:(FFTView *)other;

@end

__attribute__((always_inline)) inline void FFTView_init() {}

J2OBJC_FIELD_SETTER(FFTView, id__, NSString *)
J2OBJC_FIELD_SETTER(FFTView, key_, NSString *)
J2OBJC_FIELD_SETTER(FFTView, visibleCondition_, NSString *)
J2OBJC_FIELD_SETTER(FFTView, givenConstraints_, FFTConstraints *)
J2OBJC_FIELD_SETTER(FFTView, viewBehavior_, FFTViewBehavior *)
J2OBJC_FIELD_SETTER(FFTView, x_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, y_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, x2_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, y2_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, width_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, height_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, middleX_, JavaLangDouble *)
J2OBJC_FIELD_SETTER(FFTView, portrait_, FFTView_OrientationProperties *)
J2OBJC_FIELD_SETTER(FFTView, landscape_, FFTView_OrientationProperties *)
J2OBJC_FIELD_SETTER(FFTView, currentLayout_, FFTView_OrientationProperties *)
J2OBJC_FIELD_SETTER(FFTView, layout_, FFTLayout *)

typedef FFTView ComSponbergFluidLayoutView;

@interface FFTView_OrientationProperties : NSObject {
 @public
  FFTLayout_DirectionEnum *direction_;
  int horizontalChain_;
  JavaUtilArrayList *actionX_;
  JavaUtilArrayList *actionX2_;
  JavaUtilArrayList *actionY_;
  JavaUtilArrayList *actionY2_;
  JavaUtilArrayList *actionWidth_;
  JavaUtilArrayList *actionHeight_;
}

- (id)init;

- (void)copyAllFieldsTo:(FFTView_OrientationProperties *)other;

@end

__attribute__((always_inline)) inline void FFTView_OrientationProperties_init() {}

J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, direction_, FFTLayout_DirectionEnum *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionX_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionX2_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionY_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionY2_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionWidth_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(FFTView_OrientationProperties, actionHeight_, JavaUtilArrayList *)

#endif // _FFTView_H_
