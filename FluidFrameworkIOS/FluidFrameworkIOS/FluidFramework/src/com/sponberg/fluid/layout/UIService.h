//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/UIService.java
//

#ifndef _FFTUIService_H_
#define _FFTUIService_H_

@class FFTModalView;
@class FFTUIService_OverflowMenuDescriptor;
@class IOSObjectArray;
@protocol FFTCallback;
@protocol FFTUIService_IOverflowMenuHandler;
@protocol JavaUtilList;

#import "JreEmulation.h"

@protocol FFTUIService < NSObject, JavaObject >

- (void)removeSplashScreenWithNSString:(NSString *)firstScreenId
                           withBoolean:(BOOL)insteadShowCurrentScreenIfAny;

- (void)pushLayoutWithNSString:(NSString *)screenId;

- (void)pushLayoutWithNSString:(NSString *)screenId
                   withBoolean:(BOOL)animated;

- (void)popLayout;

- (void)setLayoutWithNSString:(NSString *)screenId
                  withBoolean:(BOOL)stack;

- (void)showModalViewWithFFTModalView:(FFTModalView *)modalView;

- (void)dismissModalViewWithFFTModalView:(FFTModalView *)modalView;

- (void)closeCurrentLayout;

- (void)showAlertWithNSString:(NSString *)title
                 withNSString:(NSString *)message;

- (void)showAlertWithNSString:(NSString *)title
                 withNSString:(NSString *)message
              withFFTCallback:(id<FFTCallback>)callback;

- (void)showAlertWithNSString:(NSString *)title
                 withNSString:(NSString *)message
                 withNSString:(NSString *)buttonText;

- (void)showAlertWithNSString:(NSString *)title
                 withNSString:(NSString *)message
                 withNSString:(NSString *)buttonText
              withFFTCallback:(id<FFTCallback>)callback;

- (float)computeHeightOfTextWithNSString:(NSString *)text
                               withFloat:(float)width
                            withNSString:(NSString *)fontName
                               withFloat:(float)fontSizeInUnits;

- (int)getScreenWidthInPixels;

- (int)getScreenHeightInPixels;

- (void)refreshMenuButtons;

- (void)grabFocusForViewWithNSString:(NSString *)viewId;

- (void)hideKeyboard;

- (void)setLayoutStackWithNSStringArray:(IOSObjectArray *)screenIds;

- (void)scrollToBottomWithNSString:(NSString *)viewPath
                      withNSString:(NSString *)viewId;

- (BOOL)isOrientationLandscape;

- (NSString *)getCurrentScreenId;

- (BOOL)showOverflowMenuWithFFTUIService_OverflowMenuDescriptor:(FFTUIService_OverflowMenuDescriptor *)menuDescriptor
                          withFFTUIService_IOverflowMenuHandler:(id<FFTUIService_IOverflowMenuHandler>)handler;

@end

__attribute__((always_inline)) inline void FFTUIService_init() {}

#define ComSponbergFluidLayoutUIService FFTUIService

@interface FFTUIService_OverflowMenuDescriptor : NSObject {
 @public
  id<JavaUtilList> buttons_;
  id extra_;
  BOOL showDismissButton_;
}

- (id<JavaUtilList>)getButtons;

- (id)getExtra;

- (BOOL)isShowDismissButton;

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons;

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
               withBoolean:(BOOL)showDismissButton;

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
                    withId:(id)extra;

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
               withBoolean:(BOOL)showDissmissButton
                    withId:(id)extra;

- (void)copyAllFieldsTo:(FFTUIService_OverflowMenuDescriptor *)other;

@end

__attribute__((always_inline)) inline void FFTUIService_OverflowMenuDescriptor_init() {}

J2OBJC_FIELD_SETTER(FFTUIService_OverflowMenuDescriptor, buttons_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(FFTUIService_OverflowMenuDescriptor, extra_, id)

@interface FFTUIService_OverflowMenuDescriptor_OverflowMenuButton : NSObject {
 @public
  NSString *buttonTitle_;
}

- (NSString *)getButtonTitle;

- (id)initWithNSString:(NSString *)buttonTitle;

- (void)copyAllFieldsTo:(FFTUIService_OverflowMenuDescriptor_OverflowMenuButton *)other;

@end

__attribute__((always_inline)) inline void FFTUIService_OverflowMenuDescriptor_OverflowMenuButton_init() {}

J2OBJC_FIELD_SETTER(FFTUIService_OverflowMenuDescriptor_OverflowMenuButton, buttonTitle_, NSString *)

@protocol FFTUIService_IOverflowMenuHandler < NSObject, JavaObject >

- (void)handleUserSelectOverflowMenuButtonWithInt:(int)index;

- (void)hanldeUserSelectDismissOverflowMenu;

@end

__attribute__((always_inline)) inline void FFTUIService_IOverflowMenuHandler_init() {}

#endif // _FFTUIService_H_
