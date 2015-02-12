//
//  FFTextView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 12/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFTextView.h"
#import "View.h"
#import "GlobalState.h"
#import "FluidApp.h"

@implementation FFTextView

static CGFloat leftMarginInUnits = 1;

- (id)initWithFrame:(CGRect)frame view:(FFTViewPosition *)view {
    self = [super initWithFrame:frame];
    if (self) {
        self.view = view;
    }
    return self;
}

@end
