//
//  FFSpace.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 6/07/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFSpace.h"

@implementation FFSpace

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event {
    return NO;
}

@end
