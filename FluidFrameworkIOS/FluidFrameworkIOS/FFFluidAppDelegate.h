//
//  FFAppDelegate.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 27/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <Foundation/Foundation.h>

@class FFTFluidApp, FFDataNotificationService;

@protocol FFFluidAppDelegate <NSObject>

- (FFDataNotificationService *)dataNotificationService;

@end
