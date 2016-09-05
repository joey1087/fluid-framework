//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/tracking/TrackingManager.java
//

#include "IOSClass.h"
#include "com/sponberg/fluid/tracking/ITrackingUtil.h"
#include "com/sponberg/fluid/tracking/TrackingManager.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/HashMap.h"
#include "java/util/List.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@implementation ComSponbergFluidTrackingTrackingManager

- (void)addTrackingUtilWithComSponbergFluidTrackingTrackingManager_UtilTypeEnum:(ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *)type
                                      withComSponbergFluidTrackingITrackingUtil:(id<ComSponbergFluidTrackingITrackingUtil>)util {
  (void) [((id<JavaUtilMap>) nil_chk(trackingUtilsMap_)) putWithId:type withId:util];
}

- (void)sendPageViewWithNSString:(NSString *)page {
  for (ComSponbergFluidTrackingTrackingManager_UtilTypeEnum * __strong type in nil_chk([((id<JavaUtilMap>) nil_chk(trackingUtilsMap_)) keySet])) {
    id<ComSponbergFluidTrackingITrackingUtil> util = [trackingUtilsMap_ getWithId:type];
    [((id<ComSponbergFluidTrackingITrackingUtil>) nil_chk(util)) sendPageViewWithNSString:page];
  }
}

- (void)sendEventWithNSString:(NSString *)Category
                 withNSString:(NSString *)Action
                 withNSString:(NSString *)Label {
  for (ComSponbergFluidTrackingTrackingManager_UtilTypeEnum * __strong type in nil_chk([((id<JavaUtilMap>) nil_chk(trackingUtilsMap_)) keySet])) {
    id<ComSponbergFluidTrackingITrackingUtil> util = [trackingUtilsMap_ getWithId:type];
    [((id<ComSponbergFluidTrackingITrackingUtil>) nil_chk(util)) sendEventWithNSString:Category withNSString:Action withNSString:Label];
  }
}

- (void)sendPageViewWithNSString:(NSString *)page
withComSponbergFluidTrackingTrackingManager_UtilTypeEnum:(ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *)type {
}

- (void)sendEventWithNSString:(NSString *)Category
                 withNSString:(NSString *)Action
                 withNSString:(NSString *)Label
withComSponbergFluidTrackingTrackingManager_UtilTypeEnum:(ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *)type {
}

- (void)sendPageViewWithNSString:(NSString *)page
                withJavaUtilList:(id<JavaUtilList>)types {
}

- (void)sendEventWithNSString:(NSString *)Category
                 withNSString:(NSString *)Action
                 withNSString:(NSString *)Label
             withJavaUtilList:(id<JavaUtilList>)types {
}

- (id)init {
  if (self = [super init]) {
    trackingUtilsMap_ = [[JavaUtilHashMap alloc] init];
  }
  return self;
}

- (void)copyAllFieldsTo:(ComSponbergFluidTrackingTrackingManager *)other {
  [super copyAllFieldsTo:other];
  other->trackingUtilsMap_ = trackingUtilsMap_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "addTrackingUtilWithComSponbergFluidTrackingTrackingManager_UtilTypeEnum:withComSponbergFluidTrackingITrackingUtil:", "addTrackingUtil", "V", 0x1, NULL },
    { "sendPageViewWithNSString:", "sendPageView", "V", 0x1, NULL },
    { "sendEventWithNSString:withNSString:withNSString:", "sendEvent", "V", 0x1, NULL },
    { "sendPageViewWithNSString:withComSponbergFluidTrackingTrackingManager_UtilTypeEnum:", "sendPageView", "V", 0x1, NULL },
    { "sendEventWithNSString:withNSString:withNSString:withComSponbergFluidTrackingTrackingManager_UtilTypeEnum:", "sendEvent", "V", 0x1, NULL },
    { "sendPageViewWithNSString:withJavaUtilList:", "sendPageView", "V", 0x1, NULL },
    { "sendEventWithNSString:withNSString:withNSString:withJavaUtilList:", "sendEvent", "V", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "trackingUtilsMap_", NULL, 0x0, "Ljava.util.Map;", NULL,  },
  };
  static J2ObjcClassInfo _ComSponbergFluidTrackingTrackingManager = { "TrackingManager", "com.sponberg.fluid.tracking", NULL, 0x1, 8, methods, 1, fields, 0, NULL};
  return &_ComSponbergFluidTrackingTrackingManager;
}

@end

BOOL ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_initialized = NO;

ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_values[2];

@implementation ComSponbergFluidTrackingTrackingManager_UtilTypeEnum

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [ComSponbergFluidTrackingTrackingManager_UtilTypeEnum class]) {
    ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_GoogleAnalytics = [[ComSponbergFluidTrackingTrackingManager_UtilTypeEnum alloc] initWithNSString:@"GoogleAnalytics" withInt:0];
    ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_Snowplow = [[ComSponbergFluidTrackingTrackingManager_UtilTypeEnum alloc] initWithNSString:@"Snowplow" withInt:1];
    ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_initialized = YES;
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithObjects:ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_values count:2 type:[IOSClass classWithClass:[ComSponbergFluidTrackingTrackingManager_UtilTypeEnum class]]];
}

+ (ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < 2; i++) {
    ComSponbergFluidTrackingTrackingManager_UtilTypeEnum *e = ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_values[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "GoogleAnalytics", "GoogleAnalytics", 0x4019, "Lcom.sponberg.fluid.tracking.TrackingManager$UtilType;", &ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_GoogleAnalytics,  },
    { "Snowplow", "Snowplow", 0x4019, "Lcom.sponberg.fluid.tracking.TrackingManager$UtilType;", &ComSponbergFluidTrackingTrackingManager_UtilTypeEnum_Snowplow,  },
  };
  static const char *superclass_type_args[] = {"Lcom.sponberg.fluid.tracking.TrackingManager$UtilType;"};
  static J2ObjcClassInfo _ComSponbergFluidTrackingTrackingManager_UtilTypeEnum = { "UtilType", "com.sponberg.fluid.tracking", "TrackingManager", 0x4019, 1, methods, 2, fields, 1, superclass_type_args};
  return &_ComSponbergFluidTrackingTrackingManager_UtilTypeEnum;
}

@end