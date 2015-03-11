//
//  FFSystemService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 27/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

#include "java/lang/Runnable.h"

#import "FFSystemService.h"
#import "CallbackFailable.h"

#if TARGET_IPHONE_SIMULATOR

#define SIMULATOR YES

#else // TARGET_IPHONE_SIMULATOR

// Device specific code
#define SIMULATOR NO

#endif // TARGET_IPHONE_SIMULATOR

@implementation FFSystemService

- (void)initiatePhoneCallWithNSString:(NSString *)phoneNumber {
    
    if (SIMULATOR) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Phone"
                                                        message:[NSString stringWithFormat:@"Phone disabled in Simulator: %@", phoneNumber]
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSString *phoneUrl = [[NSString alloc] initWithFormat:@"tel:%@",phoneNumber];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:phoneUrl]];
}

- (void)initiateEmailWithNSStringArray:(IOSObjectArray *)emails
                          withNSString:(NSString *)subject OBJC_METHOD_FAMILY_NONE {
    
    NSString *email = [emails objectAtIndex:0];
    
    if (SIMULATOR) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Email"
                                                        message:[NSString stringWithFormat:@"Email disabled in Simulator: %@", email]
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    NSString *url = [NSString stringWithFormat:@"mailto:%@?subject=%@", email, subject];
    
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

- (void)runOnUiThreadWithJavaLangRunnable:(id<JavaLangRunnable>)runnable {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
            [runnable run];
        });
    });
}

- (void)getDeviceNotificationIdWithFFTCallbackFailable:(id<FFTCallbackFailable>)callback {
    
    if (SIMULATOR) {
        [callback runWithNSString:@"emulator"];
        self.callback = nil;
        return;
    }
    
    if (self.notificationIdSet) {
        [callback runWithNSString:self.notificationId];
        self.callback = nil;
    } else if (self.notificationIdFailed) {
        [self.callback failWithNSString:@"Please turn on Push Notifications"];
        self.callback = nil;
    } else if (self.notificationIdTimedOut) {
        [self.callback failWithNSString:@"Push Notifications Callback timed out"];
    } else {
        self.callback = callback;
    }
}

- (void)setNotificationId:(NSString *)notificationId {

    _notificationId = notificationId;
    self.notificationIdSet = YES;
    
    if (self.callback) {
        
        if (SIMULATOR) {
            [self.callback runWithNSString:@"emulator"];
        } else {
            [self.callback runWithNSString:notificationId];
        }
        self.callback = nil;
    }
}

- (void)setDeviceNotificationIdFailed {
    
    self.notificationIdFailed = YES;
    
    [self.callback failWithNSString:@"Please turn on Push Notifications"];
    self.callback = nil;
}

- (void)setDeviceNotificationIdTimedOut {
    
    self.notificationIdTimedOut = YES;
    
    [self.callback failWithNSString:@"Push Notification Callback timed out"];
}

- (BOOL)isOnUiThread {
    return [NSThread isMainThread];
}

- (void)openBrowserWithWithNSString:(NSString *)url {

    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

- (NSString *)getDeviceModel {
    return [[UIDevice currentDevice] model];
}

- (NSString *)getDeviceName {
    return [[UIDevice currentDevice] name];
}

- (NSString *)getDeviceSystemName {
    return [[UIDevice currentDevice] systemName];
}

- (NSString *)getDeviceSystemVersion {
    return [[UIDevice currentDevice] systemVersion];
}

- (NSString *)getAppVersion {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}

- (void)openAppStorePageForRating {
    if ([[UIApplication sharedApplication].delegate respondsToSelector:@selector(openAppStorePageForRating)]) {
        [[UIApplication sharedApplication].delegate performSelector:@selector(openAppStorePageForRating) withObject:nil];
    }
}

@end
