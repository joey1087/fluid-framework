//
//  FFGoogleAnalyticsService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 6/07/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFGoogleAnalyticsService.h"

@implementation FFGoogleAnalyticsService

- (void)sendScreenViewWithNSString:(NSString *)trackerName
                      withNSString:(NSString *)screenName {
    
    NSLog(@"hstdbc sendScreen todo implement %@ %@", trackerName, screenName);
    
}

- (void)sendEventWithNSString:(NSString *)tracker
                 withNSString:(NSString *)category
                 withNSString:(NSString *)action
                 withNSString:(NSString *)label {

    NSLog(@"hstdbc sendEvent todo implement %@ %@ %@ %@", tracker, category, action, label);
    
}

@end
