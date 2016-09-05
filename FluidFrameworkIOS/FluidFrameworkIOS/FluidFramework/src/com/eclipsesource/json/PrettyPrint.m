//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/PrettyPrint.java
//

#include "IOSClass.h"
#include "IOSPrimitiveArray.h"
#include "com/eclipsesource/json/JsonWriter.h"
#include "com/eclipsesource/json/PrettyPrint.h"
#include "java/io/IOException.h"
#include "java/io/Writer.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/Arrays.h"

@implementation FFTJSONPrettyPrint

- (id)initWithCharArray:(IOSCharArray *)indentChars {
  if (self = [super init]) {
    self->indentChars_ = indentChars;
  }
  return self;
}

+ (FFTJSONPrettyPrint *)singleLine {
  return [[FFTJSONPrettyPrint alloc] initWithCharArray:nil];
}

+ (FFTJSONPrettyPrint *)indentWithSpacesWithInt:(int)number {
  if (number < 0) {
    @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:@"number is negative"];
  }
  IOSCharArray *chars = [IOSCharArray arrayWithLength:number];
  [JavaUtilArrays fillWithCharArray:chars withChar:' '];
  return [[FFTJSONPrettyPrint alloc] initWithCharArray:chars];
}

+ (FFTJSONPrettyPrint *)indentWithTabs {
  return [[FFTJSONPrettyPrint alloc] initWithCharArray:[IOSCharArray arrayWithChars:(unichar[]){ 0x0009 } count:1]];
}

- (FFTJSONJsonWriter *)createWriterWithJavaIoWriter:(JavaIoWriter *)writer {
  return [[FFTJSONPrettyPrint_PrettyPrintWriter alloc] initWithJavaIoWriter:writer withCharArray:indentChars_];
}

- (void)copyAllFieldsTo:(FFTJSONPrettyPrint *)other {
  [super copyAllFieldsTo:other];
  other->indentChars_ = indentChars_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithCharArray:", "PrettyPrint", NULL, 0x4, NULL },
    { "singleLine", NULL, "Lcom.eclipsesource.json.PrettyPrint;", 0x9, NULL },
    { "indentWithSpacesWithInt:", "indentWithSpaces", "Lcom.eclipsesource.json.PrettyPrint;", 0x9, NULL },
    { "indentWithTabs", NULL, "Lcom.eclipsesource.json.PrettyPrint;", 0x9, NULL },
    { "createWriterWithJavaIoWriter:", "createWriter", "Lcom.eclipsesource.json.JsonWriter;", 0x4, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "indentChars_", NULL, 0x12, "[C", NULL,  },
  };
  static J2ObjcClassInfo _FFTJSONPrettyPrint = { "PrettyPrint", "com.eclipsesource.json", NULL, 0x1, 5, methods, 1, fields, 0, NULL};
  return &_FFTJSONPrettyPrint;
}

@end

@implementation FFTJSONPrettyPrint_PrettyPrintWriter

- (id)initWithJavaIoWriter:(JavaIoWriter *)writer
             withCharArray:(IOSCharArray *)indentChars {
  if (self = [super initWithJavaIoWriter:writer]) {
    self->indentChars_ = indentChars;
  }
  return self;
}

- (void)writeArrayOpen {
  indent_++;
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:'['];
  [self writeNewLine];
}

- (void)writeArrayClose {
  indent_--;
  [self writeNewLine];
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:']'];
}

- (void)writeArraySeparator {
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:','];
  if (![self writeNewLine]) {
    [writer_ writeWithInt:' '];
  }
}

- (void)writeObjectOpen {
  indent_++;
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:'{'];
  [self writeNewLine];
}

- (void)writeObjectClose {
  indent_--;
  [self writeNewLine];
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:'}'];
}

- (void)writeMemberSeparator {
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:':'];
  [writer_ writeWithInt:' '];
}

- (void)writeObjectSeparator {
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:','];
  if (![self writeNewLine]) {
    [writer_ writeWithInt:' '];
  }
}

- (BOOL)writeNewLine {
  if (indentChars_ == nil) {
    return NO;
  }
  [((JavaIoWriter *) nil_chk(writer_)) writeWithInt:0x000a];
  for (int i = 0; i < indent_; i++) {
    [writer_ writeWithCharArray:indentChars_];
  }
  return YES;
}

- (void)copyAllFieldsTo:(FFTJSONPrettyPrint_PrettyPrintWriter *)other {
  [super copyAllFieldsTo:other];
  other->indent_ = indent_;
  other->indentChars_ = indentChars_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithJavaIoWriter:withCharArray:", "PrettyPrintWriter", NULL, 0x2, NULL },
    { "writeArrayOpen", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeArrayClose", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeArraySeparator", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeObjectOpen", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeObjectClose", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeMemberSeparator", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeObjectSeparator", NULL, "V", 0x4, "Ljava.io.IOException;" },
    { "writeNewLine", NULL, "Z", 0x2, "Ljava.io.IOException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "indentChars_", NULL, 0x12, "[C", NULL,  },
    { "indent_", NULL, 0x2, "I", NULL,  },
  };
  static J2ObjcClassInfo _FFTJSONPrettyPrint_PrettyPrintWriter = { "PrettyPrintWriter", "com.eclipsesource.json", "PrettyPrint", 0xa, 9, methods, 2, fields, 0, NULL};
  return &_FFTJSONPrettyPrint_PrettyPrintWriter;
}

@end