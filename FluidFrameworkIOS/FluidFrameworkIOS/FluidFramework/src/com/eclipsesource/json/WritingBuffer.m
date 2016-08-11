//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/WritingBuffer.java
//

#include "IOSClass.h"
#include "IOSPrimitiveArray.h"
#include "com/eclipsesource/json/WritingBuffer.h"
#include "java/io/IOException.h"
#include "java/io/Writer.h"
#include "java/lang/System.h"

@implementation FFTJSONWritingBuffer

- (id)initWithJavaIoWriter:(JavaIoWriter *)writer {
  return [self initFFTJSONWritingBufferWithJavaIoWriter:writer withInt:16];
}

- (id)initFFTJSONWritingBufferWithJavaIoWriter:(JavaIoWriter *)writer
                                       withInt:(int)bufferSize {
  if (self = [super init]) {
    fill_ = 0;
    self->writer_ = writer;
    buffer_ = [IOSCharArray arrayWithLength:bufferSize];
  }
  return self;
}

- (id)initWithJavaIoWriter:(JavaIoWriter *)writer
                   withInt:(int)bufferSize {
  return [self initFFTJSONWritingBufferWithJavaIoWriter:writer withInt:bufferSize];
}

- (void)writeWithInt:(int)c {
  if (fill_ > (int) [((IOSCharArray *) nil_chk(buffer_)) count] - 1) {
    [self flush];
  }
  (*IOSCharArray_GetRef(buffer_, fill_++)) = (unichar) c;
}

- (void)writeWithCharArray:(IOSCharArray *)cbuf
                   withInt:(int)off
                   withInt:(int)len {
  if (fill_ > (int) [((IOSCharArray *) nil_chk(buffer_)) count] - len) {
    [self flush];
    if (len > (int) [buffer_ count]) {
      [((JavaIoWriter *) nil_chk(writer_)) writeWithCharArray:cbuf withInt:off withInt:len];
      return;
    }
  }
  [JavaLangSystem arraycopyWithId:cbuf withInt:off withId:buffer_ withInt:fill_ withInt:len];
  fill_ += len;
}

- (void)writeWithNSString:(NSString *)str
                  withInt:(int)off
                  withInt:(int)len {
  if (fill_ > (int) [((IOSCharArray *) nil_chk(buffer_)) count] - len) {
    [self flush];
    if (len > (int) [buffer_ count]) {
      [((JavaIoWriter *) nil_chk(writer_)) writeWithNSString:str withInt:off withInt:len];
      return;
    }
  }
  [((NSString *) nil_chk(str)) getChars:off sourceEnd:off + len destination:buffer_ destinationBegin:fill_];
  fill_ += len;
}

- (void)flush {
  [((JavaIoWriter *) nil_chk(writer_)) writeWithCharArray:buffer_ withInt:0 withInt:fill_];
  fill_ = 0;
}

- (void)close {
}

- (void)copyAllFieldsTo:(FFTJSONWritingBuffer *)other {
  [super copyAllFieldsTo:other];
  other->buffer_ = buffer_;
  other->fill_ = fill_;
  other->writer_ = writer_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithJavaIoWriter:", "WritingBuffer", NULL, 0x0, NULL },
    { "initWithJavaIoWriter:withInt:", "WritingBuffer", NULL, 0x0, NULL },
    { "writeWithInt:", "write", "V", 0x1, "Ljava.io.IOException;" },
    { "writeWithCharArray:withInt:withInt:", "write", "V", 0x1, "Ljava.io.IOException;" },
    { "writeWithNSString:withInt:withInt:", "write", "V", 0x1, "Ljava.io.IOException;" },
    { "flush", NULL, "V", 0x1, "Ljava.io.IOException;" },
    { "close", NULL, "V", 0x1, "Ljava.io.IOException;" },
  };
  static J2ObjcFieldInfo fields[] = {
    { "writer_", NULL, 0x12, "Ljava.io.Writer;", NULL,  },
    { "buffer_", NULL, 0x12, "[C", NULL,  },
    { "fill_", NULL, 0x2, "I", NULL,  },
  };
  static J2ObjcClassInfo _FFTJSONWritingBuffer = { "WritingBuffer", "com.eclipsesource.json", NULL, 0x0, 7, methods, 3, fields, 0, NULL};
  return &_FFTJSONWritingBuffer;
}

@end
