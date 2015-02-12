//
//  FFSystemService.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 27/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SystemService.h"
#import "CallbackFailable.h"

@interface FFSystemService : NSObject<FFTSystemService>

@property (nonatomic, assign) NSString *notificationId;

@property (nonatomic, assign) BOOL notificationIdSet;
@property (nonatomic, assign) BOOL notificationIdFailed;
@property (nonatomic, assign) BOOL notificationIdTimedOut;

@property (nonatomic, strong) id<FFTCallbackFailable> callback;

- (void)setDeviceNotificationIdFailed;
- (void)setDeviceNotificationIdTimedOut;

@end
