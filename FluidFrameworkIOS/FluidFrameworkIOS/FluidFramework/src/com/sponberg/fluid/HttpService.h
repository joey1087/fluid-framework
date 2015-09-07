//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/HttpService.java
//

#ifndef _FFTHttpService_H_
#define _FFTHttpService_H_

@class FFTHttpService_HttpAuthorization;
@class FFTHttpService_PostBodyTypeEnum;
@class JavaUtilHashMap;
@protocol FFTHttpServiceCallback;

#import "JreEmulation.h"
#include "java/lang/Enum.h"

@protocol FFTHttpService < NSObject, JavaObject >

- (void)getWithNSString:(NSString *)URL
    withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)getBinaryWithNSString:(NSString *)URL
          withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
   withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_PostBodyTypeEnum:(FFTHttpService_PostBodyTypeEnum *)postBodyType
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)putWithNSString:(NSString *)URL
    withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)postRawWithNSString:(NSString *)URL
               withNSString:(NSString *)rawMessage
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
 withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

@end

__attribute__((always_inline)) inline void FFTHttpService_init() {}

#define ComSponbergFluidHttpService FFTHttpService

typedef enum {
  FFTHttpService_PostBodyType_JsonString = 0,
  FFTHttpService_PostBodyType_FormData = 1,
} FFTHttpService_PostBodyType;

@interface FFTHttpService_PostBodyTypeEnum : JavaLangEnum < NSCopying > {
}
+ (IOSObjectArray *)values;
+ (FFTHttpService_PostBodyTypeEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

FOUNDATION_EXPORT BOOL FFTHttpService_PostBodyTypeEnum_initialized;
J2OBJC_STATIC_INIT(FFTHttpService_PostBodyTypeEnum)

FOUNDATION_EXPORT FFTHttpService_PostBodyTypeEnum *FFTHttpService_PostBodyTypeEnum_values[];

#define FFTHttpService_PostBodyTypeEnum_JsonString FFTHttpService_PostBodyTypeEnum_values[FFTHttpService_PostBodyType_JsonString]
J2OBJC_STATIC_FIELD_GETTER(FFTHttpService_PostBodyTypeEnum, JsonString, FFTHttpService_PostBodyTypeEnum *)

#define FFTHttpService_PostBodyTypeEnum_FormData FFTHttpService_PostBodyTypeEnum_values[FFTHttpService_PostBodyType_FormData]
J2OBJC_STATIC_FIELD_GETTER(FFTHttpService_PostBodyTypeEnum, FormData, FFTHttpService_PostBodyTypeEnum *)

@interface FFTHttpService_HttpAuthorization : NSObject {
 @public
  NSString *username_;
  NSString *password_;
}

- (id)initWithNSString:(NSString *)username
          withNSString:(NSString *)password;

- (NSString *)getUsername;

- (NSString *)getPassword;

- (void)copyAllFieldsTo:(FFTHttpService_HttpAuthorization *)other;

@end

__attribute__((always_inline)) inline void FFTHttpService_HttpAuthorization_init() {}

J2OBJC_FIELD_SETTER(FFTHttpService_HttpAuthorization, username_, NSString *)
J2OBJC_FIELD_SETTER(FFTHttpService_HttpAuthorization, password_, NSString *)

#endif // _FFTHttpService_H_
