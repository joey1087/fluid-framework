//
//  FFImageView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFImageView.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "EventsManager.h"
#import "ActionListener.h"

@interface FFImageView ()

@property (nonatomic, assign) BOOL moving;

@end

@implementation FFImageView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
    }
    return self;
}

- (void)setFrame:(CGRect)frame {
    frame = CGRectMake(frame.origin.x + [self.imageBounds getX], frame.origin.y + [self.imageBounds getY], [self.imageBounds getWidth], [self.imageBounds getHeight]);
    [super setFrame:frame];
}

- (BOOL)canBecomeFirstResponder {
    return YES;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {

    self.moving = NO;
    
    [super touchesBegan:touches withEvent:event];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.moving = YES;
    
    [super touchesMoved:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {

    if (!self.moving) {
        FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
        [eventInfo setDataModelKeyParentWithNSString:self.dataModelKeyParent];
        [eventInfo setDataModelKeyWithNSString:self.dataModelKey];
        [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:self.viewPath withFFTActionListener_EventInfo:eventInfo];
    }
    
    [super touchesEnded:touches withEvent:event];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.moving = YES;
    
    [super touchesCancelled:touches withEvent:event];
}

@end
