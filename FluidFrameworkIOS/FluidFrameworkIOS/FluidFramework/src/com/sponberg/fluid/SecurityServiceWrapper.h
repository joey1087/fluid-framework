//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/SecurityServiceWrapper.java
//

#ifndef _FFTSecurityServiceWrapper_H_
#define _FFTSecurityServiceWrapper_H_

@class FFTHttpServiceWrapper_MapModeEnum;
@class FFTHttpService_HttpAuthorization;
@class FFTHttpService_PostBodyTypeEnum;
@class FFTSecurityServiceWrapper_MapModeEnum;
@class JavaUtilHashMap;
@protocol FFTHttpServiceCallback;
@protocol JavaUtilMap;

#import "JreEmulation.h"
#include "com/sponberg/fluid/HttpService.h"
#include "java/lang/Enum.h"

@interface FFTSecurityServiceWrapper : NSObject < FFTHttpService > {
 @public
  id<FFTHttpService> httpService_;
  FFTSecurityServiceWrapper_MapModeEnum *mapMode_;
}

- (id)initWithFFTHttpService:(id<FFTHttpService>)httpService;

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
withFFTHttpServiceWrapper_MapModeEnum:(FFTHttpServiceWrapper_MapModeEnum *)mapMode
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)postRawWithNSString:(NSString *)URL
               withNSString:(NSString *)rawPost
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
 withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (void)putWithNSString:(NSString *)URL
    withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

+ (JavaUtilHashMap *)jsonifyMapsWithJavaUtilMap:(id<JavaUtilMap>)parameters;

+ (NSString *)jsonifyMapsHelperWithJavaUtilMap:(id<JavaUtilMap>)parameters;

+ (JavaUtilHashMap *)bracketifyMapsWithJavaUtilMap:(id<JavaUtilMap>)parameters;

+ (void)bracketifyMapsHelperWithJavaUtilHashMap:(JavaUtilHashMap *)map
                                   withNSString:(NSString *)prefix
                                withJavaUtilMap:(id<JavaUtilMap>)parameters;

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_PostBodyTypeEnum:(FFTHttpService_PostBodyTypeEnum *)postBodyType
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback;

- (id<FFTHttpService>)getHttpService;

- (FFTSecurityServiceWrapper_MapModeEnum *)getMapMode;

- (void)setMapModeWithFFTSecurityServiceWrapper_MapModeEnum:(FFTSecurityServiceWrapper_MapModeEnum *)mapMode;

- (BOOL)isEqual:(id)o;

- (BOOL)canEqualWithId:(id)other;

- (NSUInteger)hash;

- (NSString *)description;

- (void)copyAllFieldsTo:(FFTSecurityServiceWrapper *)other;

@end

__attribute__((always_inline)) inline void FFTSecurityServiceWrapper_init() {}

J2OBJC_FIELD_SETTER(FFTSecurityServiceWrapper, httpService_, id<FFTHttpService>)
J2OBJC_FIELD_SETTER(FFTSecurityServiceWrapper, mapMode_, FFTSecurityServiceWrapper_MapModeEnum *)

typedef FFTSecurityServiceWrapper ComSponbergFluidSecurityServiceWrapper;

typedef enum {
  FFTSecurityServiceWrapper_MapMode_Jsonify = 0,
  FFTSecurityServiceWrapper_MapMode_Bracketify = 1,
} FFTSecurityServiceWrapper_MapMode;

@interface FFTSecurityServiceWrapper_MapModeEnum : JavaLangEnum < NSCopying > {
}
+ (IOSObjectArray *)values;
+ (FFTSecurityServiceWrapper_MapModeEnum *)valueOfWithNSString:(NSString *)name;
- (id)copyWithZone:(NSZone *)zone;

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal;
@end

FOUNDATION_EXPORT BOOL FFTSecurityServiceWrapper_MapModeEnum_initialized;
J2OBJC_STATIC_INIT(FFTSecurityServiceWrapper_MapModeEnum)

FOUNDATION_EXPORT FFTSecurityServiceWrapper_MapModeEnum *FFTSecurityServiceWrapper_MapModeEnum_values[];

#define FFTSecurityServiceWrapper_MapModeEnum_Jsonify FFTSecurityServiceWrapper_MapModeEnum_values[FFTSecurityServiceWrapper_MapMode_Jsonify]
J2OBJC_STATIC_FIELD_GETTER(FFTSecurityServiceWrapper_MapModeEnum, Jsonify, FFTSecurityServiceWrapper_MapModeEnum *)

#define FFTSecurityServiceWrapper_MapModeEnum_Bracketify FFTSecurityServiceWrapper_MapModeEnum_values[FFTSecurityServiceWrapper_MapMode_Bracketify]
J2OBJC_STATIC_FIELD_GETTER(FFTSecurityServiceWrapper_MapModeEnum, Bracketify, FFTSecurityServiceWrapper_MapModeEnum *)

#endif // _FFTSecurityServiceWrapper_H_
