//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/ViewBehaviorBaseLabel.java
//

#include "IOSClass.h"
#include "com/sponberg/fluid/layout/Color.h"
#include "com/sponberg/fluid/layout/ViewBehavior.h"
#include "com/sponberg/fluid/layout/ViewBehaviorBaseLabel.h"
#include "com/sponberg/fluid/util/KeyValueList.h"
#include "java/lang/Boolean.h"
#include "java/lang/Double.h"
#include "java/lang/RuntimeException.h"

@implementation FFTViewBehaviorBaseLabel

NSString * FFTViewBehaviorBaseLabel_kAlignLeft_ = @"left";
NSString * FFTViewBehaviorBaseLabel_kAlignCenter_ = @"center";
NSString * FFTViewBehaviorBaseLabel_kAlignRight_ = @"right";
NSString * FFTViewBehaviorBaseLabel_kVerticalAlignTop_ = @"top";
NSString * FFTViewBehaviorBaseLabel_kVerticalAlignMiddle_ = @"middle";
NSString * FFTViewBehaviorBaseLabel_kVerticalAlignBottom_ = @"bottom";

- (id)initWithNSString:(NSString *)type
   withFFTKeyValueList:(id<FFTKeyValueList>)properties {
  if (self = [super initWithNSString:type withFFTKeyValueList:properties]) {
    self->text_ = [FFTViewBehavior getStringPropertyWithNSString:@"text" withNSString:nil withFFTKeyValueList:properties];
    self->align_ = [FFTViewBehavior getStringPropertyWithNSString:@"align" withNSString:nil withFFTKeyValueList:properties];
    self->verticalAlign_ = [FFTViewBehavior getStringPropertyWithNSString:@"vertical-align" withNSString:nil withFFTKeyValueList:properties];
    self->textColor_ = [self getColorPropertyWithNSString:@"text-color" withFFTColor:nil withFFTKeyValueList:properties];
    self->unknownTextColor_ = [self getColorPropertyWithNSString:@"unknown-text-color" withFFTColor:nil withFFTKeyValueList:properties];
    self->fontSize_ = [FFTViewBehavior getFontSizePropertyWithNSString:@"font-size" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->maxFontSize_ = [FFTViewBehavior getFontSizePropertyWithNSString:@"max-font-size" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->minFontSize_ = [FFTViewBehavior getFontSizePropertyWithNSString:@"min-font-size" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->backgroundColorPressed_ = [self getColorPropertyWithNSString:@"background-color-pressed" withFFTColor:nil withFFTKeyValueList:properties];
    self->ellipsize_ = [((JavaLangBoolean *) nil_chk([FFTViewBehavior getBooleanPropertyWithNSString:@"ellipsize" withBoolean:NO withFFTKeyValueList:properties])) booleanValue];
    if (minFontSize_ != nil && maxFontSize_ != nil && [minFontSize_ doubleValue] > [maxFontSize_ doubleValue]) {
      @throw [[JavaLangRuntimeException alloc] initWithNSString:@"min-font-size must be less than max-font-size"];
    }
    if (minFontSize_ != nil && fontSize_ != nil) {
      @throw [[JavaLangRuntimeException alloc] initWithNSString:@"min-font-size may not be used with font-size"];
    }
    if (maxFontSize_ != nil && fontSize_ != nil) {
      @throw [[JavaLangRuntimeException alloc] initWithNSString:@"max-font-size may not be used with font-size"];
    }
  }
  return self;
}

- (NSString *)description {
  return [NSString stringWithFormat:@"ViewBehaviorBaseLabel(text=%@, textColor=%@, unknownTextColor=%@, align=%@, verticalAlign=%@, fontSize=%@, maxFontSize=%@, minFontSize=%@, backgroundColorPressed=%@, ellipsize=%@)", [self getText], [self getTextColor], [self getUnknownTextColor], [self getAlign], [self getVerticalAlign], [self getFontSize], [self getMaxFontSize], [self getMinFontSize], [self getBackgroundColorPressed], [JavaLangBoolean toStringWithBoolean:[self isEllipsize]]];
}

- (NSString *)getText {
  return self->text_;
}

- (FFTColor *)getTextColor {
  return self->textColor_;
}

- (FFTColor *)getUnknownTextColor {
  return self->unknownTextColor_;
}

- (NSString *)getAlign {
  return self->align_;
}

- (NSString *)getVerticalAlign {
  return self->verticalAlign_;
}

- (JavaLangDouble *)getFontSize {
  return self->fontSize_;
}

- (JavaLangDouble *)getMaxFontSize {
  return self->maxFontSize_;
}

- (JavaLangDouble *)getMinFontSize {
  return self->minFontSize_;
}

- (FFTColor *)getBackgroundColorPressed {
  return self->backgroundColorPressed_;
}

- (BOOL)isEllipsize {
  return self->ellipsize_;
}

- (void)setTextWithNSString:(NSString *)text {
  self->text_ = text;
}

- (void)setTextColorWithFFTColor:(FFTColor *)textColor {
  self->textColor_ = textColor;
}

- (void)setUnknownTextColorWithFFTColor:(FFTColor *)unknownTextColor {
  self->unknownTextColor_ = unknownTextColor;
}

- (void)setAlignWithNSString:(NSString *)align {
  self->align_ = align;
}

- (void)setVerticalAlignWithNSString:(NSString *)verticalAlign {
  self->verticalAlign_ = verticalAlign;
}

- (void)setFontSizeWithJavaLangDouble:(JavaLangDouble *)fontSize {
  self->fontSize_ = fontSize;
}

- (void)setMaxFontSizeWithJavaLangDouble:(JavaLangDouble *)maxFontSize {
  self->maxFontSize_ = maxFontSize;
}

- (void)setMinFontSizeWithJavaLangDouble:(JavaLangDouble *)minFontSize {
  self->minFontSize_ = minFontSize;
}

- (void)setBackgroundColorPressedWithFFTColor:(FFTColor *)backgroundColorPressed {
  self->backgroundColorPressed_ = backgroundColorPressed;
}

- (void)setEllipsizeWithBoolean:(BOOL)ellipsize {
  self->ellipsize_ = ellipsize;
}

- (void)copyAllFieldsTo:(FFTViewBehaviorBaseLabel *)other {
  [super copyAllFieldsTo:other];
  other->align_ = align_;
  other->backgroundColorPressed_ = backgroundColorPressed_;
  other->ellipsize_ = ellipsize_;
  other->fontSize_ = fontSize_;
  other->maxFontSize_ = maxFontSize_;
  other->minFontSize_ = minFontSize_;
  other->text_ = text_;
  other->textColor_ = textColor_;
  other->unknownTextColor_ = unknownTextColor_;
  other->verticalAlign_ = verticalAlign_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:withFFTKeyValueList:", "ViewBehaviorBaseLabel", NULL, 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
    { "getText", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getTextColor", NULL, "Lcom.sponberg.fluid.layout.Color;", 0x1, NULL },
    { "getUnknownTextColor", NULL, "Lcom.sponberg.fluid.layout.Color;", 0x1, NULL },
    { "getAlign", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getVerticalAlign", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getFontSize", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getMaxFontSize", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getMinFontSize", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getBackgroundColorPressed", NULL, "Lcom.sponberg.fluid.layout.Color;", 0x1, NULL },
    { "isEllipsize", NULL, "Z", 0x1, NULL },
    { "setTextWithNSString:", "setText", "V", 0x1, NULL },
    { "setTextColorWithFFTColor:", "setTextColor", "V", 0x1, NULL },
    { "setUnknownTextColorWithFFTColor:", "setUnknownTextColor", "V", 0x1, NULL },
    { "setAlignWithNSString:", "setAlign", "V", 0x1, NULL },
    { "setVerticalAlignWithNSString:", "setVerticalAlign", "V", 0x1, NULL },
    { "setFontSizeWithJavaLangDouble:", "setFontSize", "V", 0x1, NULL },
    { "setMaxFontSizeWithJavaLangDouble:", "setMaxFontSize", "V", 0x1, NULL },
    { "setMinFontSizeWithJavaLangDouble:", "setMinFontSize", "V", 0x1, NULL },
    { "setBackgroundColorPressedWithFFTColor:", "setBackgroundColorPressed", "V", 0x1, NULL },
    { "setEllipsizeWithBoolean:", "setEllipsize", "V", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "kAlignLeft_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kAlignLeft_,  },
    { "kAlignCenter_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kAlignCenter_,  },
    { "kAlignRight_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kAlignRight_,  },
    { "kVerticalAlignTop_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kVerticalAlignTop_,  },
    { "kVerticalAlignMiddle_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kVerticalAlignMiddle_,  },
    { "kVerticalAlignBottom_", NULL, 0x9, "Ljava.lang.String;", &FFTViewBehaviorBaseLabel_kVerticalAlignBottom_,  },
    { "text_", NULL, 0x4, "Ljava.lang.String;", NULL,  },
    { "textColor_", NULL, 0x4, "Lcom.sponberg.fluid.layout.Color;", NULL,  },
    { "unknownTextColor_", NULL, 0x4, "Lcom.sponberg.fluid.layout.Color;", NULL,  },
    { "align_", NULL, 0x4, "Ljava.lang.String;", NULL,  },
    { "verticalAlign_", NULL, 0x4, "Ljava.lang.String;", NULL,  },
    { "fontSize_", NULL, 0x4, "Ljava.lang.Double;", NULL,  },
    { "maxFontSize_", NULL, 0x4, "Ljava.lang.Double;", NULL,  },
    { "minFontSize_", NULL, 0x4, "Ljava.lang.Double;", NULL,  },
    { "backgroundColorPressed_", NULL, 0x4, "Lcom.sponberg.fluid.layout.Color;", NULL,  },
    { "ellipsize_", NULL, 0x4, "Z", NULL,  },
  };
  static J2ObjcClassInfo _FFTViewBehaviorBaseLabel = { "ViewBehaviorBaseLabel", "com.sponberg.fluid.layout", NULL, 0x1, 22, methods, 16, fields, 0, NULL};
  return &_FFTViewBehaviorBaseLabel;
}

@end
