//
//  FFSearchBar.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 5/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFSearchBar.h"

@implementation FFSearchBar

- (id)initWithFrame:(CGRect)frame viewPath:(NSString *)viewPath fluidView:(FFTViewPosition *)fluidView {
    self = [super initWithFrame:frame];
    if (self) {
        self.fluidView = fluidView;
        self.viewPath = viewPath;
    }
    return self;
}

@end
