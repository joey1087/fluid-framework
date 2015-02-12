//
//  FFButton.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 23/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFButton.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "EventsManager.h"
#import "ActionListener.h"
#import "ModalView.h"
#import "UIService.h"

@implementation FFButton

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
    }
    return self;
}

+ (id)buttonWithType:(UIButtonType)buttonType frame:(CGRect)frame imageInButton:(NSString *)imageName {

    FFButton *button = [[FFButton alloc] initWithFrame:frame];
    button.backgroundColor = [UIColor clearColor];
    button.imageName = imageName;
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
        button.button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.backgroundColor = [UIColor clearColor];
    } else {
        if (imageName) {
            button.button = [UIButton buttonWithType:UIButtonTypeCustom];
        } else {
            button.button = [UIButton buttonWithType:UIButtonTypeSystem];
        }
    }
    button.button.frame = frame;
    [button addSubview:button.button];
    
    if (imageName) {
        button.imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
        [button addSubview:button.imageView];
    }
    
    return button;
}

- (void)setFrame:(CGRect)frame {
    [super setFrame:frame];
    self.button.frame = CGRectMake(0, 0, frame.size.width, frame.size.height);
    self.imageView.frame = CGRectMake(self.imageX, self.imageY, self.imageWidth, self.imageHeight);
}

- (void)setBounds:(CGRect)bounds {
    [super setBounds:bounds];
    self.button.bounds = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    self.imageView.bounds = CGRectMake(self.imageX, self.imageY, self.imageWidth, self.imageHeight);
}

- (void)setBackgroundColor:(UIColor *)backgroundColor {
    self.button.layer.backgroundColor = backgroundColor.CGColor;
}

- (void)userTapped {
    
    if (self.modalView) {
        [self.modalView setUserSelectionWithId:self.viewId];
        [[[FFTGlobalState fluidApp] getUiService] dismissModalViewWithFFTModalView:self.modalView];
        [self.modalView modalCompleteWithId:self.viewId];
    } else {
        FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
        [eventInfo setDataModelKeyParentWithNSString:self.dataModelKeyParent];
        [eventInfo setDataModelKeyWithNSString:self.dataModelKey];
        [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:self.viewPath withFFTActionListener_EventInfo:eventInfo];
    }
}

@end
