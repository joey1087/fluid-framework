//
//  AppDelegate.m
//  FluidTest
//
//  Created by Hans Sponberg on 13/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "AppDelegate.h"
#import "SampleApp.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [[FASampleApp alloc] init]; // App gets placed in GlobalState

    [self setupApp:launchOptions];
    
    return YES;
}

@end
