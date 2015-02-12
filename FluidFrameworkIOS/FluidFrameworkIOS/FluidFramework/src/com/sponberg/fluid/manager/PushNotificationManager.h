//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/manager/PushNotificationManager.java
//

#ifndef _FFTPushNotificationManager_H_
#define _FFTPushNotificationManager_H_

@class JavaUtilArrayList;
@class JavaUtilHashMap;
@protocol FFTPushNotificationManager_PushNotificationListener;

#import "JreEmulation.h"

@interface FFTPushNotificationManager : NSObject {
 @public
  JavaUtilArrayList *listeners_;
}

- (void)addPushNotificationListenerWithFFTPushNotificationManager_PushNotificationListener:(id<FFTPushNotificationManager_PushNotificationListener>)listener;

- (void)didReceivePushNotificationWithJavaUtilHashMap:(JavaUtilHashMap *)data;

- (id)init;

- (void)copyAllFieldsTo:(FFTPushNotificationManager *)other;

@end

__attribute__((always_inline)) inline void FFTPushNotificationManager_init() {}

J2OBJC_FIELD_SETTER(FFTPushNotificationManager, listeners_, JavaUtilArrayList *)

typedef FFTPushNotificationManager ComSponbergFluidManagerPushNotificationManager;

@protocol FFTPushNotificationManager_PushNotificationListener < NSObject, JavaObject >

- (void)pushNotificationReceivedWithJavaUtilHashMap:(JavaUtilHashMap *)data;

@end

__attribute__((always_inline)) inline void FFTPushNotificationManager_PushNotificationListener_init() {}

#endif // _FFTPushNotificationManager_H_
