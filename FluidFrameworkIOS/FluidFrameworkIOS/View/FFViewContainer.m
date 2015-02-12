//
//  FFViewContainer.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 8/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFViewContainer.h"
#import "FFFluidAppDelegate.h"
#import "FFDataNotificationService.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "EventsManager.h"
#import "ActionListener.h"
#import "ViewBehaviorLabel.h"

@interface FFViewContainer ()

@property (nonatomic, assign) BOOL eligableForTap;

@end

@implementation FFViewContainer

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

/*
- (void)willMoveToSuperview:(UIView *)newSuperview {
    if (newSuperview) {
        id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
        [[appDelegate dataNotificationService] enableDataChangeObserverForId:self.dataModelListenerId];
    } else {
        id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
        [[appDelegate dataNotificationService] disableDataChangeObserverForId:self.dataModelListenerId];
    }
}*/

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event {
    
    if (self.handleTap) {
        return [super pointInside:point withEvent:event];
    } else {
        return NO;
    }
}

- (void)userDidTap {
    
    FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
    [eventInfo setDataModelKeyParentWithNSString:self.dataModelKeyParent];
    [eventInfo setDataModelKeyWithNSString:self.dataModelKey];
    [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:self.viewPath withFFTActionListener_EventInfo:eventInfo];
}

- (void)resetBackgroundColor {
    
    if (!self.backgroundColorPressed) {
        return;
    }
    
    UIColor *backgroundColor = [FFView color:[self.viewBehavior getBackgroundColorWithNSString:self.dataModelKeyParent]];
    
    if (backgroundColor) {
        [self setBackgroundColor:backgroundColor];
    } else {
        [self setBackgroundColor:nil];
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.eligableForTap = YES;
    
    if (self.backgroundColorPressed) {
        [self setBackgroundColor:self.backgroundColorPressed];
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
 
    [self resetBackgroundColor];
    
    if (self.eligableForTap) {
        [self userDidTap];
    }
    
    self.eligableForTap = NO;
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event {
    
    [self resetBackgroundColor];
    
    self.eligableForTap = NO;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    
    [self resetBackgroundColor];
    
    self.eligableForTap = NO;
}

@end
