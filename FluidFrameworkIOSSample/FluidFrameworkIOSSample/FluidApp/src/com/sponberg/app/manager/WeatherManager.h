//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/app/manager/WeatherManager.java
//

#ifndef _FAWeatherManager_H_
#define _FAWeatherManager_H_

@class FFTFluidApp;
@class FFTHttpServiceCallback_HttpResponse;
@class FFTTableList;
@class IOSObjectArray;

#import "JreEmulation.h"
#include "com/sponberg/fluid/ApplicationLoader.h"
#include "com/sponberg/fluid/HttpServiceCallback.h"
#include "java/lang/Runnable.h"

#define FAWeatherManager_kFetchDataTimeout 3000

@interface FAWeatherManager : NSObject < FFTApplicationLoader > {
 @public
  FFTTableList *dataPoints_;
  BOOL useFakeData_;
}

- (void)load__WithFFTFluidApp:(FFTFluidApp *)app;

- (void)downloadWeatherAsyncWithFFTFluidApp:(FFTFluidApp *)app;

- (void)getDataFromTestFile;

- (void)parseDataWithNSString:(NSString *)data;

- (IOSObjectArray *)getSupportedPlatforms;

- (FFTTableList *)getDataPoints;

- (BOOL)isUseFakeData;

- (void)setDataPointsWithFFTTableList:(FFTTableList *)dataPoints;

- (void)setUseFakeDataWithBoolean:(BOOL)useFakeData;

- (id)init;

- (void)copyAllFieldsTo:(FAWeatherManager *)other;

@end

__attribute__((always_inline)) inline void FAWeatherManager_init() {}

J2OBJC_FIELD_SETTER(FAWeatherManager, dataPoints_, FFTTableList *)

J2OBJC_STATIC_FIELD_GETTER(FAWeatherManager, kFetchDataTimeout, int)

typedef FAWeatherManager ComSponbergAppManagerWeatherManager;

@interface FAWeatherManager_$1 : NSObject < FFTHttpServiceCallback > {
 @public
  FAWeatherManager *this$0_;
  FFTFluidApp *val$app_;
}

- (void)successWithFFTHttpServiceCallback_HttpResponse:(FFTHttpServiceCallback_HttpResponse *)response;

- (void)failWithFFTHttpServiceCallback_HttpResponse:(FFTHttpServiceCallback_HttpResponse *)response;

- (id)initWithFAWeatherManager:(FAWeatherManager *)outer$
               withFFTFluidApp:(FFTFluidApp *)capture$0;

@end

__attribute__((always_inline)) inline void FAWeatherManager_$1_init() {}

J2OBJC_FIELD_SETTER(FAWeatherManager_$1, this$0_, FAWeatherManager *)
J2OBJC_FIELD_SETTER(FAWeatherManager_$1, val$app_, FFTFluidApp *)

@interface FAWeatherManager_$1_$1 : NSObject < JavaLangRunnable > {
 @public
  FAWeatherManager_$1 *this$0_;
  FFTHttpServiceCallback_HttpResponse *val$response_;
}

- (void)run;

- (id)initWithFAWeatherManager_$1:(FAWeatherManager_$1 *)outer$
withFFTHttpServiceCallback_HttpResponse:(FFTHttpServiceCallback_HttpResponse *)capture$0;

@end

__attribute__((always_inline)) inline void FAWeatherManager_$1_$1_init() {}

J2OBJC_FIELD_SETTER(FAWeatherManager_$1_$1, this$0_, FAWeatherManager_$1 *)
J2OBJC_FIELD_SETTER(FAWeatherManager_$1_$1, val$response_, FFTHttpServiceCallback_HttpResponse *)

@interface FAWeatherManager_$2 : NSObject < JavaLangRunnable > {
 @public
  FAWeatherManager *this$0_;
}

- (void)run;

- (id)initWithFAWeatherManager:(FAWeatherManager *)outer$;

@end

__attribute__((always_inline)) inline void FAWeatherManager_$2_init() {}

J2OBJC_FIELD_SETTER(FAWeatherManager_$2, this$0_, FAWeatherManager *)

#endif // _FAWeatherManager_H_
