//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/util/HtmlUtil.java
//

#include "com/sponberg/fluid/util/HtmlUtil.h"

@implementation FFTHtmlUtil

+ (NSString *)escapeSingleQuoteWithNSString:(NSString *)string {
  return [((NSString *) nil_chk(string)) replaceAll:@"'" withReplacement:@"\\\\\""];
}

+ (NSString *)escapeBackslashesWithNSString:(NSString *)string {
  return [((NSString *) nil_chk(string)) replaceAll:@"\\\\" withReplacement:@"\\\\\\\\"];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "escapeSingleQuoteWithNSString:", "escapeSingleQuote", "Ljava.lang.String;", 0x9, NULL },
    { "escapeBackslashesWithNSString:", "escapeBackslashes", "Ljava.lang.String;", 0x9, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcClassInfo _FFTHtmlUtil = { "HtmlUtil", "com.sponberg.fluid.util", NULL, 0x1, 3, methods, 0, NULL, 0, NULL};
  return &_FFTHtmlUtil;
}

@end
