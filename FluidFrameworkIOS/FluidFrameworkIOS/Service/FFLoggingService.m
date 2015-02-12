//
//  FFLoggingService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 11/09/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFLoggingService.h"

#import "LoggingService.h"

@implementation FFLoggingService

- (void)logMessageWithNSString:(NSString *)message {
    NSLog(@"%@", message);
}

- (void)logErrorWithNSString:(NSString *)message {
    NSLog(@"%@", message);
}

@end
