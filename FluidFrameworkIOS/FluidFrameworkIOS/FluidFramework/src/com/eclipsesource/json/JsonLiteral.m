//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/JsonLiteral.java
//

#include "IOSClass.h"
#include "com/eclipsesource/json/JsonLiteral.h"
#include "com/eclipsesource/json/JsonValue.h"
#include "com/eclipsesource/json/JsonWriter.h"
#include "java/io/IOException.h"

@implementation FFTJsonLiteral

- (id)initWithNSString:(NSString *)value {
  if (self = [super init]) {
    self->value_ = value;
  }
  return self;
}

- (void)writeWithFFTJsonWriter:(FFTJsonWriter *)writer {
  [((FFTJsonWriter *) nil_chk(writer)) writeWithNSString:value_];
}

- (NSString *)description {
  return value_;
}

- (BOOL)asBoolean {
  return [self isBoolean] ? [self isTrue] : [super asBoolean];
}

- (BOOL)isNull {
  return self == FFTJsonValue_get_NULL__();
}

- (BOOL)isBoolean {
  return self == FFTJsonValue_get_TRUE__() || self == FFTJsonValue_get_FALSE__();
}

- (BOOL)isTrue {
  return self == FFTJsonValue_get_TRUE__();
}

- (BOOL)isFalse {
  return self == FFTJsonValue_get_FALSE__();
}

- (NSUInteger)hash {
  return ((int) [((NSString *) nil_chk(value_)) hash]);
}

- (BOOL)isEqual:(id)object {
  if (self == object) {
    return YES;
  }
  if (object == nil) {
    return NO;
  }
  if ([self getClass] != [nil_chk(object) getClass]) {
    return NO;
  }
  FFTJsonLiteral *other = (FFTJsonLiteral *) check_class_cast(object, [FFTJsonLiteral class]);
  return [((NSString *) nil_chk(value_)) isEqual:other->value_];
}

- (void)copyAllFieldsTo:(FFTJsonLiteral *)other {
  [super copyAllFieldsTo:other];
  other->value_ = value_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithNSString:", "JsonLiteral", NULL, 0x0, NULL },
    { "writeWithFFTJsonWriter:", "write", "V", 0x4, "Ljava.io.IOException;" },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
    { "asBoolean", NULL, "Z", 0x1, NULL },
    { "isNull", NULL, "Z", 0x1, NULL },
    { "isBoolean", NULL, "Z", 0x1, NULL },
    { "isTrue", NULL, "Z", 0x1, NULL },
    { "isFalse", NULL, "Z", 0x1, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "value_", NULL, 0x12, "Ljava.lang.String;", NULL,  },
  };
  static J2ObjcClassInfo _FFTJsonLiteral = { "JsonLiteral", "com.eclipsesource.json", NULL, 0x0, 10, methods, 1, fields, 0, NULL};
  return &_FFTJsonLiteral;
}

@end
