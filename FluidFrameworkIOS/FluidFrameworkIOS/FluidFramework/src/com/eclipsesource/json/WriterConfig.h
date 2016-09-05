//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-external/com/eclipsesource/json/WriterConfig.java
//

#ifndef _FFTJSONWriterConfig_H_
#define _FFTJSONWriterConfig_H_

@class FFTJSONJsonWriter;
@class JavaIoWriter;

#import "JreEmulation.h"

@interface FFTJSONWriterConfig : NSObject {
}

- (FFTJSONJsonWriter *)createWriterWithJavaIoWriter:(JavaIoWriter *)writer;

- (id)init;

@end

FOUNDATION_EXPORT BOOL FFTJSONWriterConfig_initialized;
J2OBJC_STATIC_INIT(FFTJSONWriterConfig)

FOUNDATION_EXPORT FFTJSONWriterConfig *FFTJSONWriterConfig_MINIMAL_;
J2OBJC_STATIC_FIELD_GETTER(FFTJSONWriterConfig, MINIMAL_, FFTJSONWriterConfig *)
J2OBJC_STATIC_FIELD_SETTER(FFTJSONWriterConfig, MINIMAL_, FFTJSONWriterConfig *)

FOUNDATION_EXPORT FFTJSONWriterConfig *FFTJSONWriterConfig_PRETTY_PRINT_;
J2OBJC_STATIC_FIELD_GETTER(FFTJSONWriterConfig, PRETTY_PRINT_, FFTJSONWriterConfig *)
J2OBJC_STATIC_FIELD_SETTER(FFTJSONWriterConfig, PRETTY_PRINT_, FFTJSONWriterConfig *)

typedef FFTJSONWriterConfig ComEclipsesourceJsonWriterConfig;

@interface FFTJSONWriterConfig_$1 : FFTJSONWriterConfig {
}

- (FFTJSONJsonWriter *)createWriterWithJavaIoWriter:(JavaIoWriter *)writer;

- (id)init;

@end

__attribute__((always_inline)) inline void FFTJSONWriterConfig_$1_init() {}

#endif // _FFTJSONWriterConfig_H_