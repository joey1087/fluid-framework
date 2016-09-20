//
//  FFTTextField.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 9/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFTextField.h"
#import "ViewPosition.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "EventsManager.h"
#import "ActionListener.h"

@implementation FFTextField

static CGFloat leftMarginInUnits = 1;

- (id)initWithFrame:(CGRect)frame view:(FFTViewPosition *)view {
    self = [super initWithFrame:frame];
    if (self) {
        self.view = view;
    }
    return self;
}

- (CGRect)textRectForBounds:(CGRect)bounds {
    
    float margin = [[FFTGlobalState fluidApp] unitsToPixelsWithDouble:leftMarginInUnits];
    
    bounds.origin.x += margin;
    
    return bounds;
}

- (CGRect)editingRectForBounds:(CGRect)bounds {

    float margin = [[FFTGlobalState fluidApp] unitsToPixelsWithDouble:leftMarginInUnits];
    
    bounds.origin.x += margin;
    
    return bounds;
}

- (void)editingDidBegin {
    
    [[[FFTGlobalState fluidApp] getEventsManager] userBeginEditingWithNSString:self.viewPath withFFTActionListener_EventInfo:nil];
}

@end
