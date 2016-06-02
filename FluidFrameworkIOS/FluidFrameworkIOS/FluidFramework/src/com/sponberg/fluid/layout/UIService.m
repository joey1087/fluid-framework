//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/UIService.java
//

#include "IOSObjectArray.h"
#include "com/sponberg/fluid/Callback.h"
#include "com/sponberg/fluid/layout/ModalView.h"
#include "com/sponberg/fluid/layout/UIService.h"
#include "java/util/List.h"

@interface FFTUIService : NSObject
@end

@implementation FFTUIService

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "removeSplashScreenWithNSString:withBoolean:", "removeSplashScreen", "V", 0x401, NULL },
    { "pushLayoutWithNSString:", "pushLayout", "V", 0x401, NULL },
    { "pushLayoutWithNSString:withBoolean:", "pushLayout", "V", 0x401, NULL },
    { "popLayout", NULL, "V", 0x401, NULL },
    { "setLayoutWithNSString:withBoolean:", "setLayout", "V", 0x401, NULL },
    { "showModalViewWithFFTModalView:", "showModalView", "V", 0x401, NULL },
    { "dismissModalViewWithFFTModalView:", "dismissModalView", "V", 0x401, NULL },
    { "closeCurrentLayout", NULL, "V", 0x401, NULL },
    { "showAlertWithNSString:withNSString:", "showAlert", "V", 0x401, NULL },
    { "showAlertWithNSString:withNSString:withFFTCallback:", "showAlert", "V", 0x401, NULL },
    { "showAlertWithNSString:withNSString:withNSString:", "showAlert", "V", 0x401, NULL },
    { "showAlertWithNSString:withNSString:withNSString:withFFTCallback:", "showAlert", "V", 0x401, NULL },
    { "computeHeightOfTextWithNSString:withFloat:withNSString:withFloat:", "computeHeightOfText", "F", 0x401, NULL },
    { "getScreenWidthInPixels", NULL, "I", 0x401, NULL },
    { "getScreenHeightInPixels", NULL, "I", 0x401, NULL },
    { "refreshMenuButtons", NULL, "V", 0x401, NULL },
    { "grabFocusForViewWithNSString:", "grabFocusForView", "V", 0x401, NULL },
    { "hideKeyboard", NULL, "V", 0x401, NULL },
    { "setLayoutStackWithNSStringArray:", "setLayoutStack", "V", 0x481, NULL },
    { "scrollToBottomWithNSString:withNSString:", "scrollToBottom", "V", 0x401, NULL },
    { "isOrientationLandscape", NULL, "Z", 0x401, NULL },
    { "getCurrentScreenId", NULL, "Ljava.lang.String;", 0x401, NULL },
    { "showOverflowMenuWithFFTUIService_OverflowMenuDescriptor:withFFTUIService_IOverflowMenuHandler:", "showOverflowMenu", "Z", 0x401, NULL },
  };
  static J2ObjcClassInfo _FFTUIService = { "UIService", "com.sponberg.fluid.layout", NULL, 0x201, 23, methods, 0, NULL, 0, NULL};
  return &_FFTUIService;
}

@end

@implementation FFTUIService_OverflowMenuDescriptor

- (id<JavaUtilList>)getButtons {
  return buttons_;
}

- (id)getExtra {
  return extra_;
}

- (BOOL)isShowDismissButton {
  return showDismissButton_;
}

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons {
  return [self initFFTUIService_OverflowMenuDescriptorWithJavaUtilList:buttons withBoolean:YES withId:nil];
}

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
               withBoolean:(BOOL)showDismissButton {
  return [self initFFTUIService_OverflowMenuDescriptorWithJavaUtilList:buttons withBoolean:showDismissButton withId:nil];
}

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
                    withId:(id)extra {
  return [self initFFTUIService_OverflowMenuDescriptorWithJavaUtilList:buttons withBoolean:YES withId:extra];
}

- (id)initFFTUIService_OverflowMenuDescriptorWithJavaUtilList:(id<JavaUtilList>)buttons
                                                  withBoolean:(BOOL)showDissmissButton
                                                       withId:(id)extra {
  if (self = [super init]) {
    self->buttons_ = buttons;
    self->extra_ = extra;
    self->showDismissButton_ = showDissmissButton;
  }
  return self;
}

- (id)initWithJavaUtilList:(id<JavaUtilList>)buttons
               withBoolean:(BOOL)showDissmissButton
                    withId:(id)extra {
  return [self initFFTUIService_OverflowMenuDescriptorWithJavaUtilList:buttons withBoolean:showDissmissButton withId:extra];
}

- (void)copyAllFieldsTo:(FFTUIService_OverflowMenuDescriptor *)other {
  [super copyAllFieldsTo:other];
  other->buttons_ = buttons_;
  other->extra_ = extra_;
  other->showDismissButton_ = showDismissButton_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getButtons", NULL, "Ljava.util.List;", 0x1, NULL },
    { "getExtra", NULL, "Ljava.lang.Object;", 0x1, NULL },
    { "isShowDismissButton", NULL, "Z", 0x1, NULL },
    { "initWithJavaUtilList:", "OverflowMenuDescriptor", NULL, 0x1, NULL },
    { "initWithJavaUtilList:withBoolean:", "OverflowMenuDescriptor", NULL, 0x1, NULL },
    { "initWithJavaUtilList:withId:", "OverflowMenuDescriptor", NULL, 0x1, NULL },
    { "initWithJavaUtilList:withBoolean:withId:", "OverflowMenuDescriptor", NULL, 0x2, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "buttons_", NULL, 0x10, "Ljava.util.List;", NULL,  },
    { "extra_", NULL, 0x10, "Ljava.lang.Object;", NULL,  },
    { "showDismissButton_", NULL, 0x10, "Z", NULL,  },
  };
  static J2ObjcClassInfo _FFTUIService_OverflowMenuDescriptor = { "OverflowMenuDescriptor", "com.sponberg.fluid.layout", "UIService", 0x9, 7, methods, 3, fields, 0, NULL};
  return &_FFTUIService_OverflowMenuDescriptor;
}

@end

@implementation FFTUIService_OverflowMenuDescriptor_OverflowMenuButton

- (NSString *)getButtonTitle {
  return buttonTitle_;
}

- (id)initWithNSString:(NSString *)buttonTitle {
  if (self = [super init]) {
    self->buttonTitle_ = buttonTitle;
  }
  return self;
}

- (void)copyAllFieldsTo:(FFTUIService_OverflowMenuDescriptor_OverflowMenuButton *)other {
  [super copyAllFieldsTo:other];
  other->buttonTitle_ = buttonTitle_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getButtonTitle", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "initWithNSString:", "OverflowMenuButton", NULL, 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "buttonTitle_", NULL, 0x10, "Ljava.lang.String;", NULL,  },
  };
  static J2ObjcClassInfo _FFTUIService_OverflowMenuDescriptor_OverflowMenuButton = { "OverflowMenuButton", "com.sponberg.fluid.layout", "UIService$OverflowMenuDescriptor", 0x9, 2, methods, 1, fields, 0, NULL};
  return &_FFTUIService_OverflowMenuDescriptor_OverflowMenuButton;
}

@end

@interface FFTUIService_IOverflowMenuHandler : NSObject
@end

@implementation FFTUIService_IOverflowMenuHandler

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "handleUserSelectOverflowMenuButtonWithInt:", "handleUserSelectOverflowMenuButton", "V", 0x401, NULL },
    { "hanldeUserSelectDismissOverflowMenu", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _FFTUIService_IOverflowMenuHandler = { "IOverflowMenuHandler", "com.sponberg.fluid.layout", "UIService", 0x209, 2, methods, 0, NULL, 0, NULL};
  return &_FFTUIService_IOverflowMenuHandler;
}

@end
